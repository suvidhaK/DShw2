package edu.purdue.cs505.RChannel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RChannelReceiver implements ReliableChannelReceiver {
  private BufferedReader input;
  private int i = 0;
  private PrintWriter outputStream;

  RChannelReceiver() {
    try {
      input = new BufferedReader(new FileReader("random.txt"));
      outputStream = new PrintWriter(new FileWriter("output.txt"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void rreceive(Message m) {
    String l;
    try {
      l = input.readLine();
      if (!l.equals(m.getMessageContents())) {
        Debugger.print(4, "XXXXXXXXXXXXX----NotMatched-----XXXXXXXXXXXX ");
        Debugger.print(4, "lenght: " + l.length() + " "
            + m.getMessageContents().length());
        outputStream.println(l + "\n != \n" + m.getMessageContents());
        outputStream.close();
        Thread.currentThread().stop();
      } else {
        Debugger.print(4, "Matched " + i);
        i++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
