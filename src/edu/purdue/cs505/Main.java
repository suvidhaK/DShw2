package edu.purdue.cs505;

import java.util.ArrayList;

public final class Main {

  public static void main(String[] args) {
    test4();
    return;
  }

  static public void test1() {

    // create processes
    ArrayList<Process> processes = new ArrayList<Process>();
    processes.add(new Process("127.0.0.1", 3000));
    processes.add(new Process("127.0.0.1", 4000));
    processes.add(new Process("127.0.0.1", 5000));

    ArrayList<ReliableBroadcast> broadcasters = new ArrayList<ReliableBroadcast>();
    for (Process p : processes) {
      // create reliable broadcast for each process.
      ReliableBroadcast r = new ReliableBroadcastClass();
      BroadcastReceiver receiver = new TestBroadcastReceiverClass();
      r.init(p);
      for (Process other : processes)
        r.addProcess(other);

      // attach callback
      r.rblisten(receiver);
      broadcasters.add(r);
    }

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Message m = new Message();
    String pid = new Integer(1).toString();
    for (int i = 1; i <= 2000; i++) {
      m.setContents("1");
      m.setProcessID(pid);
      broadcasters.get(0).rbroadcast(m);
      broadcasters.get(1).rbroadcast(m);
      broadcasters.get(2).rbroadcast(m);
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
      Thread.sleep(1000);
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
  
  static public void test4() {
	    // create processes
	    ArrayList<Process> processes = new ArrayList<Process>();
	    processes.add(new Process("127.0.0.1", 3000));
	    /*	    processes.add(new Process("127.0.0.1", 4000));
processes.add(new Process("127.0.0.1", 5000));*/

	    ArrayList<ReliableBroadcast> broadcasters = new ArrayList<ReliableBroadcast>();
	    for (Process p : processes) {
	      // create reliable broadcast for each process.
	      FIFOSemanticReliableBroadcast r = new FIFOSemanticReliableBroadcastClass();
	      r.init(p);

	      BroadcastReceiver receiver = new TestFIFOSrbReceiverClass();

	      for (Process other : processes)
	        r.addProcess(other);

	      // attach callback
	      r.rblisten(receiver);
	      broadcasters.add(r);
	    }

	    try {
	      Thread.sleep(1000);
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    }
	    /*
	     * process 1 is broadcasting
	     */
	    SrbMessage m = new SrbMessage();
	    String pid = new Integer(1).toString();
	    for (int i = 0; i <= 10; i++) {
	      m.setMessageNumber(i);
	      m.setContents(new Integer(i).toString());
	      m.setProcessID(pid);
	      if (i == 5) {
	    	  m.makesObsolete(2);
	    	  m.makesObsolete(3);
	      }
	      broadcasters.get(0).rbroadcast(m);
	    }
	  }
}
