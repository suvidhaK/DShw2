package edu.purdue.cs505;

import java.util.HashMap;
import java.util.PriorityQueue;

import edu.purdue.cs505.RChannel.Debugger;

public class FIFOReliableBroadcastReceiverClass implements BroadcastReceiver {

  private FIFOReliableBroadcastClass broadcaster;
  private HashMap<String, Integer> procSeq;
  private HashMap<String, PriorityQueue<Message>> procMsg;

  public FIFOReliableBroadcastReceiverClass(
      FIFOReliableBroadcastClass broadcaster) {
    this.broadcaster = broadcaster;
    procSeq = new HashMap<String, Integer>();
    procMsg = new HashMap<String, PriorityQueue<Message>>();

  }

  public void rdeliver(Message m) {
    Debugger.print(
        3,
        "Received message id: " + m.processID + " seq nu: "
            + m.getMessageNumber());
    String id = m.getProcessID();
    int seq = m.getMessageNumber();

    Integer seqNeeded = procSeq.get(id);
    if (seqNeeded == null) {
      seqNeeded = new Integer(0);
      procSeq.put(id, seqNeeded);
    }

    PriorityQueue<Message> msgBag = procMsg.get(id);
    if (msgBag == null) {
      msgBag = new PriorityQueue<Message>();
      procMsg.put(id, msgBag);
    }

    msgBag.add(m);

    Message headMsg = msgBag.peek();
    while (headMsg != null && headMsg.messageNumber == seqNeeded) {
      Message msg = Message.deserializeMessage(headMsg.getContents());
      broadcaster.breceiver.rdeliver(msg);
      msgBag.poll();
      headMsg = msgBag.peek();
      procSeq.put(id, ++seqNeeded);
    }
  }
}
