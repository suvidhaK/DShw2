package edu.purdue.cs505;

import edu.purdue.cs505.RChannel.Debugger;

public class TestBroadcastReceiverClass implements BroadcastReceiver {
  int sum = 0;
  static int aim = 3 * 2000;

  public void rdeliver(Message m) {
    int i = Integer.parseInt(m.contents);
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
      if (sum % 500 == 0) {
        Debugger.print(4, "-------");
        Debugger.print(4, "Continue: " + sum);
        Debugger.print(4, "-------\n");
      }
    }
  }
}
