package edu.purdue.cs505;

import edu.purdue.cs505.RChannel.Debugger;

public class BReceiver implements BroadcastReceiver {

  @Override
  public void rdeliver(Message m) {
    Debugger.print(4, "************************Delivered: Broadcasted from: "
        + m.processID + " Contents: " + m.getContents());

  }

}
