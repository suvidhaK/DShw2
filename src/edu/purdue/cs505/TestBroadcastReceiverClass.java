package edu.purdue.cs505;

import edu.purdue.cs505.RChannel.Debugger;

public class TestBroadcastReceiverClass implements BroadcastReceiver {
  int sum = 0;
  Integer flag = new Integer(0);
  static int aim = 2 * 50000;

  public void rdeliver(Message m) {
    int i = Integer.parseInt(m.contents);
    synchronized (flag) {
      sum = sum + i;
      if (sum == aim) {
        Debugger.print(4, "-------");
        Debugger.print(4, "Done " + sum);
        Debugger.print(4, "-------\n");
      } else if (sum > aim) {
        Debugger.print(4, "-------");
        Debugger.print(4, "----Problem " + sum);
        Debugger.print(4, "-------\n");
      } else {
        if (sum % 50 == 0) {
          Debugger.print(4, "-------");
          Debugger.print(4, "Continue: " + sum);
          Debugger.print(4, "-------\n");
        }
      }
    }
  }
}
