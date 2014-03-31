package edu.purdue.cs505;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable, Comparable {
  int messageNumber; // Message number for FIFO delivery
  String contents; // Message contents
  String processID; // ID of the process that originally sent this message . 

  // Use <IP> + Ò:Ó + <port> 
  public int getMessageNumber() {
    return messageNumber;
  }

  public void setMessageNumber(int messageNumber) {
    this.messageNumber = messageNumber;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  public String getProcessID() {
    return processID;
  }

  public void setProcessID(String processID) {
    this.processID = processID;
  }

  /*
   * Convert message obj to string.
   */
  public String serializeMessage() {
    String serializedObject = "";
    try {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(this);
      so.flush();
      serializedObject = bo.toString();
    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }
    return serializedObject;
  }

  /*
   * Convert string to message obj.
   */
  public static Message deserializeMessage(String serializedObj) {
    Message message = new Message();
    try {
      byte b[] = serializedObj.getBytes();
      ByteArrayInputStream bi = new ByteArrayInputStream(b);
      ObjectInputStream si = new ObjectInputStream(bi);
      message = (Message) si.readObject();
    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }
    return message;
  }

  public boolean equals(Object obj) {
    Message message = (Message) obj;
    if (this.messageNumber == message.messageNumber
        && this.processID.equals(message.processID))
      return true;

    return false;
  }

  public int compareTo(Object obj) {
    Message m = (Message) obj;
    if (this.messageNumber < m.messageNumber)
      return -1;
    else if (this.messageNumber == m.messageNumber)
      return 0;
    else
      return 1;
  }
}
