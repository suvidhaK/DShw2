package edu.purdue.cs505;

import java.io.Serializable;
import java.util.ArrayList;

public class SrbMessage extends Message implements Serializable, Comparable {
	static int MAX_OBSOLETES=100;
	protected ArrayList<Integer> obsoletes;
	
	public SrbMessage() {
	  this.obsoletes = new ArrayList<Integer>();
	}

	public void makesObsolete (int msgNumber) {
	  this.obsoletes.add(msgNumber);
	}
 
	public int[] getObsoletedMessages() {
	  Integer msgNumList[] = new Integer[this.obsoletes.size()];
	  int ret[] = new int[this.obsoletes.size()];
	  msgNumList = this.obsoletes.toArray(msgNumList);
	  for (int i=0; i<ret.length; i++) {
		  ret[i] = Integer.valueOf(msgNumList[i]);
	  }
	  return ret;
	}
		 
}
