package edu.purdue.cs505.RChannel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main3 {
  public static void main(String args[]) {
    System.out.println("Hello World2");

    RChannelReceiver rcr = new RChannelReceiver();

    RChannel senderTest = new RChannel(4000); // Sender
    senderTest.init("", 0);

    // RChannel receiverTest = new RChannel(5000); // Receiver
    // receiverTest.init("", 0);
    // receiverTest.rlisten(rcr);

    // try {
    // PrintWriter outputStream = new PrintWriter(new FileWriter("random.txt"));
    // for (int i = 0; i < 1024; i++) {
    // String uuid = UUID.randomUUID().toString();
    // while (uuid.length() < 10000) {
    // uuid += uuid;
    // }
    // outputStream.println(uuid);
    // }
    // outputStream.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return;
    RChannel receiverTest = null;
    test2(senderTest, receiverTest);
  }

  private static void test1(RChannel sender, RChannel receiver) {
    try {
      BufferedReader input = new BufferedReader(new FileReader("random.txt"));
      String l;
      while ((l = input.readLine()) != null) {
        RMessage m = new RMessage(l);
        m.setDestinationIP("10.184.96.170");
        m.setDestinationPort(5000);
        sender.rsend(m);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    Debugger.print(4, "All send done!");
  }

  private static void test2(RChannel sender, RChannel receiver) {
    int i = 0;
    while (i < 1000) {
      // sender.setDestinationIP("localhost");
      // sender.setDestinationPort(5000);
      RMessage msg = new RMessage(new String(Integer.toString(i)));
      msg.setDestinationIP("10.184.96.170");
      msg.setDestinationPort(5000);
      sender.rsend(msg);
      i++;
    }
    Debugger.print(4, "All send done!");
  }
}
