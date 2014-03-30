package edu.purdue.cs505;

public interface BroadcastReceiver {
	void rdeliver(Message m); // Channel users should implement this for
	// handling delivered messages
}