package edu.purdue.cs505;

import java.util.ArrayList;


public class Process {
	String IP; // IP of the process
	int Port; // Port of the process
	private static int seqNum = 0;
	
	ArrayList<Message> delivered; //TODO : When to purge this?

	public Process(String IP, int port) {
		this.IP = IP;
		this.Port = port;
		this.delivered = new ArrayList<Message>(); 
	}
	
	public String getIP() {
		return IP;
	}
	
	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		Port = port;
	}
	
	public String toString() {
		    return ("IP" + IP + "Port" + Port);
	}
	
	public boolean equals(Object obj) {
	    Process process = (Process)obj;
	    if(this.Port == process.Port && this.IP.equals(process.IP))
	    	return true;
	    
	    return false;
	}
	
	public String getProcessID()
	{
		return new String(IP + ":" + Port);
	}
	
	public int getNextSeqNum()
	{
		seqNum = seqNum + 1;
		return(seqNum);
	}
}