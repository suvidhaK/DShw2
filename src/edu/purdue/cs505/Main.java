package edu.purdue.cs505;

import java.util.ArrayList;

public final class Main {

  public static void main(String[] args) {
    BroadcastReceiver receiver = new BReceiver();

    // create processes
    ArrayList<Process> processes = new ArrayList<Process>();
    processes.add(new Process("localhost", 3000));
    processes.add(new Process("localhost", 4000));
    processes.add(new Process("localhost", 6000));
    processes.add(new Process("localhost", 5000));
    processes.add(new Process("localhost", 7000));
    processes.add(new Process("localhost", 8000));
    processes.add(new Process("localhost", 9000));

    ArrayList<ReliableBroadcast> broadcasters = new ArrayList<ReliableBroadcast>();
    for (Process p : processes)
    // Process p = processes.get(0);
    {
      // create reliable broadcast for each process.
      ReliableBroadcast r = new RBroadcast();
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Message m = new Message();
    m.setContents("ok");
    broadcasters.get(0).rbroadcast(m);
    broadcasters.get(1).rbroadcast(m);
    broadcasters.get(2).rbroadcast(m);
  }

}
