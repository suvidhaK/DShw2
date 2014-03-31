package edu.purdue.cs505;

import java.util.ArrayList;

import edu.purdue.cs505.RChannel.Debugger;
import edu.purdue.cs505.RChannel.ReliableChannelReceiver;

public class ReliableChannelReceiverCallbackClass implements
    ReliableChannelReceiver {

  private ReliableBroadcastClass broadcaster;

  public ReliableChannelReceiverCallbackClass(ReliableBroadcastClass breceiver) {
    this.broadcaster = breceiver;
  }

  /*
   * On receive of a message from a channel, see if its already delivered. If
   * not, broadcast and deliver it.
   */
  public void rreceive(edu.purdue.cs505.RChannel.Message channelMsg) {
    String contents = channelMsg.getMessageContents();
    Message broadcastMsg = Message.deserializeMessage(contents);
    ArrayList<Message> deliveredMsgs = broadcaster.currentProcess.delivered;
    // If received for the first time, broadcast again.
    synchronized (deliveredMsgs) {
      if (!deliveredMsgs.contains(broadcastMsg)) {
        broadcaster.reRbroadcast(broadcastMsg);
        Message reliableBMsg = Message.deserializeMessage(broadcastMsg
            .getContents());
        Debugger.print(3, "received Message Id: " + broadcastMsg.processID
            + " " + broadcastMsg.getMessageNumber());
        broadcaster.breceiver.rdeliver(reliableBMsg);
        deliveredMsgs.add(broadcastMsg);
      } else {
        Debugger.print(3, "Message Id: " + broadcastMsg.processID + " "
            + broadcastMsg.getMessageNumber() + " already delievered");
      }
    }
  }
}
