package edu.purdue.cs505;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import edu.purdue.cs505.RChannel.Debugger;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;

class MessageWrap {
	private long timestamp;
	private SrbMessage msg;
	public MessageWrap(SrbMessage m) {
		this.timestamp = System.currentTimeMillis();
		this.msg = m;
	}
	public long getTimeStamp () {
		return this.timestamp;
	}
	public SrbMessage getMessage () {
		return this.msg;
	}
	@Override
	public boolean equals (Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof MessageWrap) {
			MessageWrap othermw = (MessageWrap) other;
			int myId = this.msg.getMessageNumber();
			int otherId = othermw.getMessage().getMessageNumber();
			return (myId == otherId);
		}
		else {
			Debugger.print(4, "Object of invalid type encountered");
			return false;
		}
	}
}

public class FIFOSrbReceiverClass implements BroadcastReceiver {

	/*
	 * clientReceiver is the call-back registered by the client.
	 * rdeliver is the call-back that we registered with FRB. 
	 * When FRB delivers a message, rdeliver is called. We then need
	 * to decide on what to deliver to clientReceiver.
	 */
	private BroadcastReceiver clientReceiver;
	public static long timerFreq = 100;
	private HashMap <String, LinkedBlockingQueue<MessageWrap>> procMsg; 
	
	public FIFOSrbReceiverClass(BroadcastReceiver clientReceiver) {
		this.clientReceiver = clientReceiver;
		this.procMsg = new HashMap <String, LinkedBlockingQueue<MessageWrap>>();
		Timer timer = new Timer();
		final FIFOSrbReceiverClass srb = this;
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run () {
				srb.deliverEligible();
			}
		}, timerFreq, timerFreq);
		
	}
	public void removeObsoletes (SrbMessage m) {
		String srcId = m.getProcessID();
		LinkedBlockingQueue<MessageWrap> msgQ = procMsg.get(srcId);
		if (msgQ == null) {
			/* nothing to do */
			return;
		}
		final int[] obsList = m.getObsoletedMessages();
		class Obs {
			public boolean isObsolete (SrbMessage msg) {
				int msgNumber = msg.getMessageNumber();
				for (int i=0;i<obsList.length;i++) {
					if (obsList[i] == msgNumber) {
						return true;
					}
				}
				return false;
			}
		}
		Obs obs = new Obs ();
		Iterator<MessageWrap> itr = msgQ.iterator();
		while (itr.hasNext()) {
			MessageWrap oldmw = itr.next();
			SrbMessage oldm = oldmw.getMessage();
			if (obs.isObsolete(oldm)) {
				msgQ.remove(oldmw);
			}
 		}
		
	}
	public void wrapAndStoreMsg (SrbMessage m) {
		MessageWrap msgWrap = new MessageWrap(m);
		String srcId = m.getProcessID();
		LinkedBlockingQueue<MessageWrap> msgQ = procMsg.get(srcId);
		if (msgQ == null) {
			msgQ = new LinkedBlockingQueue<MessageWrap>();
			this.procMsg.put(srcId, msgQ);
		}
		/*
		 * We keep trying to insert until space is available in
		 * the queue. An alternative is to block on insert using:
		 * 			msgQ.put(msgWrap);
		 * which throws InterruptedException. Since we have a 
		 * timer, which can cause interrups, we resort to this 
		 * simple thing instead.
		 */
		synchronized (msgQ) {
			while (!msgQ.offer(msgWrap));
		}
		return;
	}
	public void stripAndDeliver (SrbMessage m) {
		/*
		 * Nothing to strip. Only deliver.
		Message cm = Message.deserializeMessage(m.getContents());
		*/
		this.clientReceiver.rdeliver(m);
	}
	public void deliverEligible () {
		for (LinkedBlockingQueue<MessageWrap> q : this.procMsg.values()) {
			synchronized (q) {
				MessageWrap head = q.peek();
				while (head != null) {
					long currTime = System.currentTimeMillis();
					long timestamp = head.getTimeStamp();
					long diff = currTime - timestamp;
					if (diff >= FIFOSemanticReliableBroadcastClass.deliveryDelay) {
						this.stripAndDeliver(head.getMessage());
						q.poll();
					}
					else {
						/* 
						 * Invariant: Messages behind head of the queue have
						 * timestamps greater than that of head.
						 */
						break;
					}
					head = q.peek();
				}
			}
		}
	}
	public void rdeliver(Message m) {
		SrbMessage srbm;
		if (m instanceof SrbMessage) {
			srbm = (SrbMessage) m;
		} else {
			throw new ClassCastException();
		}
		Debugger.print(3, "FRB delivered msgNum: " + srbm.getMessageNumber());
		if (FIFOSemanticReliableBroadcastClass.deliveryDelay == 0) {
			this.stripAndDeliver(srbm);
		}
		else {
			this.removeObsoletes(srbm);
			this.wrapAndStoreMsg (srbm);
			this.deliverEligible ();
		}
	}

}
