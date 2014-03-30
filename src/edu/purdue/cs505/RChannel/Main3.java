package edu.purdue.cs505.RChannel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class Main3 {
  public static void main(String args[]) {
    System.out.println("Hello World2");

    RChannelReceiver rcr = new RChannelReceiver();

    // RChannel senderTest = new RChannel(); // Sender
    // senderTest.init("localhost", 5000, 4000);

    // RChannel receiverTest = new RChannel(); // Receiver
    // receiverTest.init("localhost", 4000, 5000);

    RChannel senderTest = new RChannel(4000); // Sender
    // senderTest.init("localhost", 5000);
    senderTest.init("", 0);

    RChannel receiverTest = new RChannel(5000); // Receiver
    // receiverTest.init("localhost", 4000);
    receiverTest.init("", 0);
    receiverTest.rlisten(rcr);

    try {
      PrintWriter outputStream = new PrintWriter(new FileWriter("random.txt"));
      for (int i = 0; i < 1024; i++) {
        String uuid = UUID.randomUUID().toString();
        while (uuid.length() < 10000) {
          uuid += uuid;
        }
        outputStream.println(uuid);
      }
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // return;
    test1(senderTest, receiverTest);
  }

  private static void test1(RChannel sender, RChannel receiver) {
    try {
      BufferedReader input = new BufferedReader(new FileReader("random1.txt"));
      String l;
      while ((l = input.readLine()) != null) {
        RMessage m = new RMessage(l);
        m.setDestinationIP("127.0.0.1");
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
    while (i < 100001) {
      // sender.setDestinationIP("localhost");
      // sender.setDestinationPort(5000);
      RMessage msg = new RMessage(new String(Integer.toString(i)));
      msg.setDestinationIP("127.0.0.1");
      msg.setDestinationPort(5000);
      sender.rsend(msg);
      i++;
    }
    Debugger.print(4, "All send done!");
  }
}
