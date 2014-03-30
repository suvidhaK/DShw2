package edu.purdue.cs505;

import java.util.ArrayList;

import edu.purdue.cs505.RChannel.Debugger;
import edu.purdue.cs505.RChannel.ReliableChannelReceiver;

public class ReliableChannelCallback implements ReliableChannelReceiver {

  private RBroadcast broadcaster;

  public ReliableChannelCallback(RBroadcast breceiver) {
    this.broadcaster = breceiver;
  }

  /*
   * On receive of a message from a channel, see if its already delivered. If
   * not, broadcast and deliver it.
   */
  public void rreceive(edu.purdue.cs505.RChannel.Message channelMsg) {
    String contents = channelMsg.getMessageContents();
    Message broadcastMsg = Message.deserializeMessage(contents);
    Debugger.print(3, "RReceive.... Broadcasted by: " + broadcastMsg.processID
        + " received by:" + broadcaster.currentProcess.getProcessID());

    ArrayList<Message> deliveredMsgs = broadcaster.currentProcess.delivered;
    // If received for the first time, broadcast again.
    if (!deliveredMsgs.contains(broadcastMsg)) {
      Debugger.print(3, "Calling BReceiver callback from process:"
          + broadcaster.currentProcess);
      Message reliableBMsg = Message.deserializeMessage(broadcastMsg
          .getContents());
      broadcaster.breceiver.rdeliver(reliableBMsg);
      deliveredMsgs.add(broadcastMsg);
    }
  }
}
