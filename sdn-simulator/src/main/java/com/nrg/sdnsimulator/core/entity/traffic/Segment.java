package com.nrg.sdnsimulator.core.entity.traffic;

public class Segment {

	/*-------------------------------------*/
	/* Version 2 design */
	public int sWnd = 0;
	public float timeToNextCycle = 0;
	public float sInitialDelay = 0;
	public float sInterval = 0;
	public float sInterSegmentDelay = 0;
	/*-------------------------------------*/

	public int bigRTT_;
	private int dstHostID;
	/**************************/
	private int flowID;
	public float interSegmentDelay_;
	public float rtt_;
	private int seqNum;
	private int size; // in bits
	private int srcHostID;
	/** my protocol variables **/
	public int sWnd_;
	private int type;

	public Segment(int flowID, int type, int seqNum, int size, int srcHostID, int dstHostID) {
		this.flowID = flowID;
		sWnd_ = 0;
		rtt_ = 0;
		bigRTT_ = 0;
		this.type = type;
		this.seqNum = seqNum;
		this.size = size;
		this.srcHostID = srcHostID;
		this.dstHostID = dstHostID;

	}

	public void changeType(int newType) {
		type = newType;
	}

	public int getDstHostID() {
		return dstHostID;
	}

	public int getFlowID() {

		return flowID;
	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/

	public int getSeqNum() {
		return seqNum;
	}

	public int getSize() {
		return size;
	}

	public int getSrcHostID() {
		return srcHostID;
	}

	public int getType() {
		return type;
	}

	public void setDstHostID(int dstHostID) {
		this.dstHostID = dstHostID;
	}

	/***********************************************************************/
}
