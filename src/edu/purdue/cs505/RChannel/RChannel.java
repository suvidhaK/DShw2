package edu.purdue.cs505.RChannel;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public class RChannel implements ReliableChannel {
  protected static int bufferLength = 32;
  protected static int stringLength = 1000;
  protected static int timeout = 10;

  protected LinkedList<RMessage> sendBuffer;
  protected TreeSet<RMessage> receiveBuffer;
  protected HashMap<String, LinkedList<RMessage>> userBuffer;

  private String destinationIP;

  private int destinationPort;
  private int localPort;
  // private short sendSeqNo;
  // private short recvSeqNo;
  protected HashMap<String, Short> sendSeqNo;
  protected HashMap<String, Short> recvSeqNo;

  private ReceiverThread rThread;
  private SenderThread sThread;
  private DatagramSocket udpChannel;
  // private MulticastSocket udpChannel;
  public ReliableChannelReceiver reliableChannelReceiver;

  public RChannel(int lport) {
    this.localPort = lport;
    if (udpChannel == null)
      try {
        udpChannel = new DatagramSocket(lport);
      } catch (SocketException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }

  public void init(String destinationIP, int dPort) {
    init(destinationIP, dPort, localPort);
    return;
  }

  /*
   * Initialize the channel. Creates a socket and starts the sender and receiver
   * thread.
   */
  public void init(String destinationIP, int dPort, int lPort) {
    try {
      sendBuffer = new LinkedList<RMessage>();
      userBuffer = new HashMap<String, LinkedList<RMessage>>();
      sendSeqNo = new HashMap<String, Short>();
      recvSeqNo = new HashMap<String, Short>();
      this.destinationIP = destinationIP;
      receiveBuffer = new TreeSet<RMessage>();
      destinationPort = dPort;
      localPort = lPort;

      if (udpChannel == null)
        udpChannel = new DatagramSocket(lPort);
      // udpChannel = new MulticastSocket(lPort);
      // udpChannel.setReuseAddress(true);
      // InetAddress addr = InetAddress.getByName("127.0.0.1");
      // udpChannel.bind(addr);
      Debugger.print(1, "RChannel started at port: " + lPort);

      rThread = new ReceiverThread(this);
      sThread = new SenderThread(this);

      rThread.start();
      sThread.start();
    } catch (SocketException e) {
      System.out.println("Cannot init Reliable channel because: "
          + e.getMessage());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /*
   * The method breaks the incoming message such that message Content is not
   * more than 65,507 bytes. To be on the safe side, underlying string cannot
   * contain more than stringLength bytes
   */
  public void rsend(RMessage m) {
    String stringToSend = m.getMessageContents();
    while (stringToSend.length() > stringLength) {
      RMessage msgToSend = new RMessage(stringToSend.substring(0, stringLength));
      msgToSend.setDestinationIP(m.getDestinationIP());
      msgToSend.setDestinationPort(m.getDestinationPort());
      msgToSend.setEnd(false);
      if (!send(msgToSend)) {
        continue;
      }
      stringToSend = stringToSend.substring(stringLength);
    }
    RMessage msgToSend = new RMessage(stringToSend);
    msgToSend.setDestinationIP(m.getDestinationIP());
    msgToSend.setDestinationPort(m.getDestinationPort());
    msgToSend.setEnd(true);
    send(msgToSend);
  }

  /*
   * Implement it
   */
  public void rlisten(ReliableChannelReceiver rc) {
    reliableChannelReceiver = rc;
    // if (!receiveBuffer.isEmpty())
    // rc.rreceive(receiveBuffer.remove(0));
  }

  /*
   * Implement it
   */
  public void halt() {
    rThread.stop();
    sThread.stop();
    this.udpChannel.close();
    this.udpChannel.disconnect();
  }

  public String getDestinationIP() {
    return destinationIP;
  }

  public int getDestinationPort() {
    return destinationPort;
  }

  public int getLocalPort() {
    return localPort;
  }

  public DatagramSocket getUdpChannel() {
    return udpChannel;
  }

  public short getRecvSeqNo(String id) {
    Short seq = recvSeqNo.get(id);
    if (seq == null) {
      recvSeqNo.put(id, new Short((short) 0));
      return 0;
    }
    return seq.shortValue();
  }

  public void setRecvSeqNo(String id, short seqNo) {
    recvSeqNo.put(id, new Short(seqNo));
  }

  public void incRecvSeq(String id) {
    // this.recvSeqNo = (short) ((this.recvSeqNo + 1) %
    // Short.MAX_VALUE);
    Short seq = recvSeqNo.get(id);
    if (seq != null) {
      seq = (short) ((seq + 1) % Short.MAX_VALUE);
      recvSeqNo.put(id, seq);
    }
  }

  public short getSendSeqNo(String id) {
    Short seq = sendSeqNo.get(id);
    if (seq == null) {
      sendSeqNo.put(id, new Short((short) 0));
      return 0;
    }
    return seq.shortValue();
  }

  public void setSendSeqNo(String id, short seqNo) {
    sendSeqNo.put(id, new Short(seqNo));
  }

  private void incSendSeq(String id) {
    Short seq = sendSeqNo.get(id);
    if (seq != null) {
      seq = (short) ((seq + 1) % Short.MAX_VALUE);
      sendSeqNo.put(id, seq);
    }

  }

  public void setDestinationIP(String destinationIP) {
    this.destinationIP = destinationIP;
  }

  public void setDestinationPort(int destinationPort) {
    this.destinationPort = destinationPort;
  }

  /*
   * Puts the message in the buffer so that sender thread can send the message
   * If message is successfully put, returns true else false
   */
  private boolean send(RMessage msgToSend) {
    msgToSend.setAck(false);
    String id = msgToSend.getDestinationIP();
    id += ":" + msgToSend.getDestinationPort();
    msgToSend.setSeqNo(this.getSendSeqNo(id));
    incSendSeq(id);
    synchronized (sendBuffer) {
      sendBuffer.add(msgToSend);
    }
    Debugger.print(1, "Adding to Send Buffer " + msgToSend.toString());
    return true;
  }

}
