package entities;

import system.*;

public class Bufferv1 {

	private final int CAPACITY;
	private final int POLICY;

	// TODO state variables
	/* Mode of the buffer */
	private int mode_;

	private double release_time_;
	private int tokens_;

	/* Occupancy */
	private int occupancy_;

	/* Token counter */
	private int tokensCount_;

	public Bufferv1(int capacity, int policy) {
		// TODO Auto-generated constructor stub
		this.CAPACITY = capacity;
		this.POLICY = policy;
		mode_ = Keywords.NORMALBUFFER;
		occupancy_ = 0;
		tokensCount_ = 0;
		tokens_ = 0;
	}

	/* ---------------------------------------------------- */
	/* Interface methods that could be called by controller */
	/* ---------------------------------------------------- */
	public void modeToTokenBased() {
		this.mode_ = Keywords.TOKENBASEDBUFFER;
	}

	public void modeToNormal() {
		this.mode_ = Keywords.NORMALBUFFER;
	}
	/* ---------------------------------------------------- */

	/*---------------------*/
	/* Normal mode methods */
	/*---------------------*/

	/*--------------------*/
	/* Token mode methods */
	/*--------------------*/
	// TODO Do we even need to model this inside the buffer? I mean the hold and
	// release process is based on the timings and events of the segments and
	// because for now we are not talking about the segment drops due to full
	// buffer, it does not make sense to model the buffer;
	private void genToken() {

	}

	private void release() {

	}

}
