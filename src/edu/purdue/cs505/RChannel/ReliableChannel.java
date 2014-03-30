package edu.purdue.cs505.RChannel;

public interface ReliableChannel {
  void init(String destinationIP, int dPort, int lPort);

  void rsend(RMessage m);

  void rlisten(ReliableChannelReceiver rc);

  void halt();
}
