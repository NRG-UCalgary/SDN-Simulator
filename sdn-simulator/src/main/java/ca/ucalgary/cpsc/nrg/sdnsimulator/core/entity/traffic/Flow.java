package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic;

import java.util.TreeMap;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.Entity;

public class Flow extends Entity {

	/* Sequence Number */
	private int srcHostID;
	private int dstHostID;
	private int size;

	/** ========== Statistical Counters ========== **/
	// in Sender Agent
	public float FINSendingTime;
	public float dataSendingStartTime; // in Sender Agent
	public TreeMap<Float, Float> dataSeqNumSendingTimes; // <SeqNum, Time>
	public TreeMap<Float, Float> ackSeqNumArrivalTimes; // <SeqNum, Time>
	public float totalBufferTime; // in Buffer (when getting bufferTime)
	public int totalDroppedSegments; // in Buffer
	public int totalSentSegments; // in Sender Agent
	public float totalTransmissionTime;
	public float arrivalTime; // in Simulator
	public float completionTime; // in Receiver Agent

	/** ========================================== **/

	public Flow(int ID, int srcHostID, int dstHostID, int size, float arrivalTime) {
		super(ID);
		this.srcHostID = srcHostID;
		this.dstHostID = dstHostID;
		this.size = size;
		this.arrivalTime = arrivalTime;

		/** ========== Statistical Counters Initialization ========== **/
		dataSendingStartTime = 0;
		completionTime = 0;
		totalDroppedSegments = 0;
		totalSentSegments = 0;
		totalBufferTime = 0;
		FINSendingTime = 0;
		totalTransmissionTime = 0;

		dataSeqNumSendingTimes = new TreeMap<Float, Float>();
		ackSeqNumArrivalTimes = new TreeMap<Float, Float>();
		/** ========================================================= **/
	}

	public int getDstHostID() {
		return dstHostID;
	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/

	public int getSize() {
		return size;
	}

	public int getSrcHostID() {
		return srcHostID;
	}

	public void updateCompletionTime(float completionTime) {
		this.completionTime = completionTime;
	}

	/*---------- Statistical counters methods ----------*/
	public void updateDataSendingStartTime(float startTime) {
		dataSendingStartTime = startTime;
	}

}
