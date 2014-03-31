package edu.purdue.cs505;

import java.util.ArrayList;

public final class Main {

  public static void main(String[] args) {
    test3();
    return;
  }

  static public void test1() {

    // create processes
    Process p1 = new Process("127.0.0.1", 3000);
    Process p2 = new Process("10.184.96.170", 4000);

    // create reliable broadcast for each process.
    ReliableBroadcast r = new ReliableBroadcastClass();
    BroadcastReceiver receiver = new TestBroadcastReceiverClass();
    r.init(p1);
    r.addProcess(p2);

    // attach callback
    r.rblisten(receiver);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Message m = new Message();
    String pid = new Integer(1).toString();
    for (int i = 1; i <= 50000; i++) {
      m.setContents("1");
      m.setProcessID(pid);
      r.rbroadcast(m);
    }
  }

  static public void test2() {
    // create processes
    ArrayList<Process> processes = new ArrayList<Process>();
    processes.add(new Process("127.0.0.1", 3000));
    processes.add(new Process("127.0.0.1", 4000));
    processes.add(new Process("127.0.0.1", 5000));

    ArrayList<ReliableBroadcast> broadcasters = new ArrayList<ReliableBroadcast>();
    for (Process p : processes) {
      // create reliable broadcast for each process.
      FIFOReliableBroadcast r = new FIFOReliableBroadcastClass();
      r.init(p);

      BroadcastReceiver receiver = new TestFIFOBroadcastReceiverClass();

      for (Process other : processes)
        r.addProcess(other);

      // attach callback
      r.rblisten(receiver);
      broadcasters.add(r);
    }

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Message m = new Message();
    String pid = new Integer(1).toString();
    for (int i = 0; i <= 10000; i++) {
      m.setContents(new Integer(i).toString());
      m.setProcessID(pid);
      broadcasters.get(0).rbroadcast(m);
    }
  }

  static public void test3() {
    // create processes
    Process p1 = new Process("127.0.0.1", 3000);
    Process p2 = new Process("10.184.96.170", 4000);

    // create reliable broadcast for each process.
    ReliableBroadcast r = new FIFOReliableBroadcastClass();
    BroadcastReceiver receiver = new TestFIFOBroadcastReceiverClass();
    r.init(p1);
    r.addProcess(p2);

    // attach callback
    r.rblisten(receiver);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Message m = new Message();
    String pid = new Integer(1).toString();
    for (int i = 0; i <= 50000; i++) {
      m.setContents(new Integer(i).toString());
      m.setProcessID(pid);
      r.rbroadcast(m);
    }
  }
}
