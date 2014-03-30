package edu.purdue.cs505.RChannel;

public class Debugger {
  public static void print(int level, String msg) {
    if (level >= 4) {
      System.out.println(Thread.currentThread().getId() + " " + msg);
    }
    return;
  }
}
