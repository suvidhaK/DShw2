package edu.purdue.cs505;

import java.util.ArrayList;

public class Process {
  String IP; // IP of the process
  int Port; // Port of the process
  private int seqNum = 0;

  ArrayList<Message> delivered; // TODO : When to purge this?

  public Process(Process p) {
    IP = p.IP;
    Port = p.Port;
    this.delivered = new ArrayList<Message>();
  }

  public Process(String IP, int port) {
    if (IP.toLowerCase().equals("localhost")) {
      this.IP = "127.0.0.1";
    } else {
      this.IP = IP;
    }
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
    return ("IP " + IP + " Port " + Port);
  }

  public boolean equals(Object obj) {
    Process process = (Process) obj;
    if (this.Port == process.Port && this.IP.equals(process.IP))
      return true;

    return false;
  }

  public String getProcessID() {
    return new String(IP + ":" + Port);
  }

  public int getNextSeqNum() {
    return (seqNum++);
  }
}