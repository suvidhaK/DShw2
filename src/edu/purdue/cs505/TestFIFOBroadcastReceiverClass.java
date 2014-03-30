package edu.purdue.cs505;

import edu.purdue.cs505.RChannel.Debugger;

public class TestFIFOBroadcastReceiverClass implements BroadcastReceiver {

  // HashMap<String, HashMap<String, Integer>> dataReceived;
  int last = 0;

  public void rdeliver(Message m) {
    int i = Integer.parseInt(m.contents);
    if (last != i) {
      Debugger.print(4, "Problem");

    } else {
      if (last == 10000) {
        Debugger.print(4, "Done");
      }
      if (last % 2000 == 0)
        Debugger.print(4, "Done till " + last);
      last++;
    }
  }
}
