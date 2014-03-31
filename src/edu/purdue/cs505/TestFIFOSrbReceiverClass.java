package edu.purdue.cs505;

import edu.purdue.cs505.RChannel.Debugger;

/*
 * This class is a burkha for client call-back that 
 * we use in in test.  
 */
public class TestFIFOSrbReceiverClass implements BroadcastReceiver {

	int exp = 0;
	@Override
	public void rdeliver(Message m) {
		// TODO Auto-generated method stub
		int i = Integer.parseInt(m.contents);
		int msgNum = m.getMessageNumber();
		String srcId = m.getProcessID();
	    if (exp < i) {
	      Debugger.print(4, "SRB msgNum: " + msgNum);
	      Debugger.print(4, "-- obsolation --");
	      exp = i+1;

	    } else {
	      if (i == 10) {
	    	Debugger.print(4, "SRB msgNum: " + msgNum + " from: "+srcId);
	        Debugger.print(4, "Done");
	      }
	      else {
	      	Debugger.print(4, "SRB msgNum: " + msgNum + "from: "+srcId);
	      	/*
	        Debugger.print(4, "Done till " + last);
	        */
	      }
	      exp++;
	    }
		return;
	}

}
