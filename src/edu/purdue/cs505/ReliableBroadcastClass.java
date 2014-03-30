package edu.purdue.cs505;

import java.util.ArrayList;

import edu.purdue.cs505.RChannel.Debugger;
import edu.purdue.cs505.RChannel.RChannel;
import edu.purdue.cs505.RChannel.RMessage;

public class ReliableBroadcastClass implements ReliableBroadcast {

  public BroadcastReceiver breceiver;
  public ArrayList<Process> processes;
  public Process currentProcess;
  public ArrayList<RChannel> channels;
  public RChannel currentProcChannel;

  public void init(Process currentProcess) {
    processes = new ArrayList<Process>();
    this.currentProcess = new Process(currentProcess);
    this.currentProcChannel = new RChannel(currentProcess.Port);
    currentProcChannel.init("", 0);
    currentProcChannel.rlisten(new ReliableChannelReceiverCallbackClass(this));
  }

  /*
   * Adds a process to which broadcast messages are sent.
   */
  public void addProcess(Process p) {
    if (!currentProcess.getProcessID().equals(p.getProcessID())) {
      processes.add(p);
    }
  }

  /*
   * Broadcast message to all other processes and deliver to self.
   */
  public void rbroadcast(Message msg) {
    Message m = new Message();
    m.setContents(msg.serializeMessage());

    m.messageNumber = currentProcess.getNextSeqNum();
    m.processID = currentProcess.getProcessID();
    reRbroadcast(m);
    Debugger.print(3, "Calling BReceiver callback from process: "
        + currentProcess.getProcessID());
    synchronized (currentProcess.delivered) {
      if (!currentProcess.delivered.contains(m)) {
        breceiver.rdeliver(msg);
        currentProcess.delivered.add(m);
      }
    }
    Debugger.print(1, "Delivered by " + currentProcess);
  }

  public void reRbroadcast(Message msg) {
    for (Process p : processes) {
      // Testing code for robustness
      // if (new java.util.Random().nextInt(100) == 99) {
      // continue;
      // }

      RMessage channelMsg = new RMessage();
      channelMsg.setMessageContents(msg.serializeMessage());
      channelMsg.setDestinationIP(p.getIP());
      channelMsg.setDestinationPort(p.getPort());
      currentProcChannel.rsend(channelMsg);
      Debugger.print(2, "Send Message from " + currentProcess.getProcessID()
          + " to " + p.getProcessID());
    }
  }

  /*
   * Attach call back.
   */
  public void rblisten(BroadcastReceiver m) {
    this.breceiver = m;
  }
}