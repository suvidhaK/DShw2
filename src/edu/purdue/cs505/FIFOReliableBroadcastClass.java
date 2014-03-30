package edu.purdue.cs505;

public class FIFOReliableBroadcastClass implements FIFOReliableBroadcast {

  private ReliableBroadcast broadcaster;
  private FIFOReliableBroadcastReceiverClass fifoReceiver;
  protected BroadcastReceiver breceiver;
  private Process currentProcess;

  public void init(Process currentProcess) {
    broadcaster = new ReliableBroadcastClass();
    broadcaster.init(currentProcess);

    // Register call back for FIFO receiver
    fifoReceiver = new FIFOReliableBroadcastReceiverClass(this);
    broadcaster.rblisten(fifoReceiver);

    // Current Process
    this.currentProcess = new Process(currentProcess);
  }

  public void addProcess(Process p) {
    broadcaster.addProcess(p);
  }

  public void rbroadcast(Message m) {

    Message msg = new Message();
    msg.setContents(m.serializeMessage());
    msg.processID = currentProcess.getProcessID();
    msg.setMessageNumber(currentProcess.getNextSeqNum());

    broadcaster.rbroadcast(msg);
  }

  public void rblisten(BroadcastReceiver m) {
    breceiver = m;
  }

}
