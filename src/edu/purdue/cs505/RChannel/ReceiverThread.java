package edu.purdue.cs505.RChannel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.LinkedList;

class ReceiverThread extends Thread {
  private RChannel rChannel;

  ReceiverThread(RChannel rChannel) {
    this.rChannel = rChannel;
  }

  /*
   * Implements the receiver thread
   */
  public void run() {
    int ackFailCount = 0;
    byte[] buf = new byte[2000];
    DatagramPacket dgp = new DatagramPacket(buf, buf.length);

    /*
     * Set timeout on receive to periodically flush the receiveBuffer & thereby
     * invoke the listener callback.
     */
    try {
      System.out.println(rChannel.getUdpChannel());
      rChannel.getUdpChannel().setSoTimeout(100);
    } catch (SocketException e1) {
      e1.printStackTrace();
    }

    while (true) {
      try {
        rChannel.getUdpChannel().receive(dgp);
        ByteArrayInputStream bais = new ByteArrayInputStream(dgp.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);
        RMessage msgReceived = (RMessage) ois.readObject();

        String id = dgp.getSocketAddress().toString().split("/")[1];
        msgReceived.setSenderIP(dgp.getAddress().toString().split("/")[1]);
        msgReceived.setSenderPort(dgp.getPort());

        if (msgReceived.isAck()) {
          Debugger.print(
              1,
              "Ack Recvd: " + msgReceived.toString() + ", from address: "
                  + dgp.getAddress() + ", port: " + dgp.getPort());

          synchronized (rChannel.sendBuffer) {
            if (!rChannel.sendBuffer.isEmpty()) {
              Iterator<RMessage> itr = rChannel.sendBuffer.iterator();
              Debugger.print(1, "Iterating for setting ackD");
              for (int sendCount = 0; sendCount < RChannel.bufferLength
                  && itr.hasNext(); sendCount++) {
                RMessage m = itr.next();
                Debugger.print(1, m.toString());
                if (m.getSeqNo() == msgReceived.getSeqNo()
                    && m.getDestinationIP().equals(
                        msgReceived.getDestinationIP())
                    && m.getDestinationPort() == msgReceived
                        .getDestinationPort()) {
                  m.setAckD(true);
                  Debugger.print(1, "Found and ackD saved");
                  break;
                }
              }
            }
          }
        }
        // Not Ack
        else {
          Debugger.print(1, "Id of the incomming msg: " + id);
          short msgSeqNum = msgReceived.getSeqNo();
          // Frame is as expected, within receiver window size limits.
          int start = rChannel.getRecvSeqNo(id);
          int end = start + RChannel.bufferLength;
          end %= Short.MAX_VALUE;
          if ((start <= msgSeqNum && msgSeqNum < end)
              || (start > end && msgSeqNum >= start && msgSeqNum > end)
              || (start > end && msgSeqNum < end)) {
            synchronized (rChannel.receiveBuffer) {
              rChannel.receiveBuffer.add(msgReceived);
            }
            sendACK(msgReceived, dgp);
            invokeCallBack();
          }

          // Missing ACK - Frame is already received, so just send ack.
          else if (msgSeqNum < start
              || ((msgSeqNum + rChannel.bufferLength) % Short.MAX_VALUE) >= start) {
            sendACK(msgReceived, dgp);
            ackFailCount++;
          } else {
            Debugger.print(
                2,
                "Bad msgSeqNum: " + msgSeqNum + " startSeq: "
                    + rChannel.getRecvSeqNo(id) + " MaxSeqNum: "
                    + ((rChannel.getRecvSeqNo(id) + RChannel.bufferLength)));
            Debugger.print(2, " " + ackFailCount);
          }
        }
      } catch (SocketTimeoutException e) {
        invokeCallBack();
      } catch (SocketException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * Send back ACK
   */
  private void sendACK(RMessage msgReceived, DatagramPacket dgp) {
    String content;
    byte[] buf; // = new byte[66000];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos;
    try {
      oos = new ObjectOutputStream(baos);
      content = msgReceived.getMessageContents();
      msgReceived.setMessageContents("");
      msgReceived.setAck(true);
      oos.writeObject(msgReceived);
      msgReceived.setMessageContents(content);
      buf = baos.toByteArray();
      DatagramPacket out = new DatagramPacket(buf, buf.length,
          dgp.getAddress(), dgp.getPort());
      Debugger.print(1, "Sending Ack for: " + msgReceived.toString());
      rChannel.getUdpChannel().send(out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * Sorts the received packets in FIFO order. Reconstructs message from
   * contents of datagrams, and invokes call back when a message is completely
   * constructed.
   */
  private void invokeCallBack() {
    if (!rChannel.receiveBuffer.isEmpty()) {
      Debugger.print(1, "invoke callback");
      // Invoke callback till successive messages are sequential.

      for (String id : rChannel.recvSeqNo.keySet()) {
        short start = rChannel.getRecvSeqNo(id);
        short end = (short) ((rChannel.getRecvSeqNo(id) + RChannel.bufferLength) % Short.MAX_VALUE);
        short expected = rChannel.getRecvSeqNo(id);

        Iterator<RMessage> itr = rChannel.receiveBuffer.iterator();
        LinkedList<RMessage> userBufferList = rChannel.userBuffer.get(id);

        if (userBufferList == null) {
          userBufferList = new LinkedList<RMessage>();
          rChannel.userBuffer.put(id, userBufferList);
        }

        // Maintains FIFO order for splited messages
        while (itr.hasNext()) {
          RMessage msg = itr.next();
          String msgID = msg.getSenderIP() + ":" + msg.getSenderPort();
          if (msgID.equals(id) && expected == msg.getSeqNo()) {
            expected = (short) ((expected + 1) % Short.MAX_VALUE);
            rChannel.incRecvSeq(id);
            itr.remove();
            userBufferList.add(msg);
          } else if (msgID.equals(id) && msg.getSeqNo() > expected
              && start < end) {
            break;
          }
        }

        if (!userBufferList.isEmpty()
            && rChannel.reliableChannelReceiver != null) {
          while (!userBufferList.isEmpty()) {
            RMessage m = userBufferList.remove();
            if (m.isEnd()) {
              Message msg = new RMessage();
              msg.setMessageContents(m.getMessageContents());
              rChannel.reliableChannelReceiver.rreceive(msg);
            } else if (!userBufferList.isEmpty()) {
              RMessage next = userBufferList.peek();
              String s = next.getMessageContents();
              next.setMessageContents(m.getMessageContents() + s);
            } else {
              userBufferList.offerFirst(m);
              break;
            }
          }
        }
      }
    }
  }
}