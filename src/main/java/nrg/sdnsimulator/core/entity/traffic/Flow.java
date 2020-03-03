package nrg.sdnsimulator.core.entity.traffic;

import java.util.TreeMap;

import nrg.sdnsimulator.core.entity.Entity;

public class Flow extends Entity {

	private int srcHostID;
	private int dstHostID;
	private int size;

	/** ========== Statistical Counters ========== **/
	private float FINSendingTime;
	private float dataSendingStartTime; // in Sender Agent
	private TreeMap<Float, Float> dataSeqNumSendingTimes; // <SeqNum, Time>
	private TreeMap<Float, Float> ackSeqNumArrivalTimes; // <SeqNum, Time>
	private float totalBufferTime; // in Buffer (when getting bufferTime)
	private int totalDroppedSegments; // in Buffer
	private int totalSentSegments; // in Sender Agent
	private float totalTransmissionTime;
	private float arrivalTime; // in Simulator
	private float completionTime; // in Receiver Agent

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

	public float getFINSendingTime() {
		return FINSendingTime;
	}

	public void setFINSendingTime(float fINSendingTime) {
		FINSendingTime = fINSendingTime;
	}

	public float getDataSendingStartTime() {
		return dataSendingStartTime;
	}

	public void setDataSendingStartTime(float dataSendingStartTime) {
		this.dataSendingStartTime = dataSendingStartTime;
	}

	public TreeMap<Float, Float> getDataSeqNumSendingTimes() {
		return dataSeqNumSendingTimes;
	}

	public void setDataSeqNumSendingTimes(TreeMap<Float, Float> dataSeqNumSendingTimes) {
		this.dataSeqNumSendingTimes = dataSeqNumSendingTimes;
	}

	public TreeMap<Float, Float> getAckSeqNumArrivalTimes() {
		return ackSeqNumArrivalTimes;
	}

	public void setAckSeqNumArrivalTimes(TreeMap<Float, Float> ackSeqNumArrivalTimes) {
		this.ackSeqNumArrivalTimes = ackSeqNumArrivalTimes;
	}

	public float getTotalBufferTime() {
		return totalBufferTime;
	}

	public void setTotalBufferTime(float totalBufferTime) {
		this.totalBufferTime = totalBufferTime;
	}

	public int getTotalDroppedSegments() {
		return totalDroppedSegments;
	}

	public void setTotalDroppedSegments(int totalDroppedSegments) {
		this.totalDroppedSegments = totalDroppedSegments;
	}

	public int getTotalSentSegments() {
		return totalSentSegments;
	}

	public void setTotalSentSegments(int totalSentSegments) {
		this.totalSentSegments = totalSentSegments;
	}

	public float getTotalTransmissionTime() {
		return totalTransmissionTime;
	}

	public void setTotalTransmissionTime(float totalTransmissionTime) {
		this.totalTransmissionTime = totalTransmissionTime;
	}

	public float getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(float arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public float getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(float completionTime) {
		this.completionTime = completionTime;
	}

	public void setSrcHostID(int srcHostID) {
		this.srcHostID = srcHostID;
	}

	public void setDstHostID(int dstHostID) {
		this.dstHostID = dstHostID;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
