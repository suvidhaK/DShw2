package edu.purdue.cs505;

public class FIFOSemanticReliableBroadcastClass implements
		FIFOSemanticReliableBroadcast {
	public static boolean srbOn=true;
	public static long deliveryDelay=10;
	private FIFOReliableBroadcast frb;
	protected BroadcastReceiver breceiver;
	private Process currentProcess;
	
	public FIFOSemanticReliableBroadcastClass () {
		this.frb = new FIFOReliableBroadcastClass();
	}
	@Override
	public void init(Process currentProcess) {
		this.currentProcess = currentProcess;
		this.frb.init(currentProcess);
	}

	@Override
	public void addProcess(Process p) {
		this.frb.addProcess(p);
	}

	@Override
	public void rbroadcast(Message m) {
		/*
		 * There is no separate wrapper for SRB.
		 */
		m.processID = this.currentProcess.getProcessID();
		this.frb.rbroadcast(m);
	}

	@Override
	public void rblisten(BroadcastReceiver m) {
		/*
		 * We create a wrapper over user-provided receiver
		 * call-back, which is then registered as listener
		 * with FRB. When FRB delivers a message, it our wrapper
		 * call-back is called.
		 */
		this.breceiver = m;
		BroadcastReceiver srbReceiver = new FIFOSrbReceiverClass(breceiver);
	    frb.rblisten(srbReceiver);
	}

}
