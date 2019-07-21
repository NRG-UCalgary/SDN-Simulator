package entities;

import java.util.ArrayList;
import java.util.TreeMap;

import entities.switches.SDNSwitchv1;

public class Flow extends Entity {

	private int srcHostID;
	private int dstHostID;
	private int size;
	public float arrivalTime; // in Simulator

	/** ========== Statistical Counters ========== **/
	public float dataSendingStartTime; // in Sender Agent
	public float FINSendingTime;
	public float completionTime; // in Receiver Agent
	public int totalDroppedSegments; // in Buffer
	public int totalSentSegments; // in Sender Agent
	public float totalBufferTime; // in Buffer (when getting bufferTime)
	public float totalTransmissionTime;

	/* Sequence Number */
	// in Sender Agent
	public TreeMap<Float, Float> dataSeqNumSendingTimes; // <SeqNum, Time>
	public TreeMap<Float, Float> ackSeqNumArrivalTimes; // <SeqNum, Time>

	/** ========================================== **/

	/* Stores the Node IDs of the path */
	/* Initialized at the creation of the flow */
	public ArrayList<SDNSwitchv1> path = new ArrayList<SDNSwitchv1>();

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

	/*---------- Statistical counters methods ----------*/
	public void updateDataSendingStartTime(float startTime) {
		dataSendingStartTime = startTime;
	}

	public void updateCompletionTime(float completionTime) {
		this.completionTime = completionTime;
	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/

	public int getSize() {
		return size;
	}

	public int getDstHostID() {
		return dstHostID;
	}

	public int getSrcHostID() {
		return srcHostID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ackSeqNumArrivalTimes == null) ? 0 : ackSeqNumArrivalTimes.hashCode());
		result = prime * result + Float.floatToIntBits(arrivalTime);
		result = prime * result + Float.floatToIntBits(completionTime);
		result = prime * result + Float.floatToIntBits(dataSendingStartTime);
		result = prime * result + ((dataSeqNumSendingTimes == null) ? 0 : dataSeqNumSendingTimes.hashCode());
		result = prime * result + dstHostID;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + size;
		result = prime * result + srcHostID;
		result = prime * result + Float.floatToIntBits(totalBufferTime);
		result = prime * result + totalDroppedSegments;
		result = prime * result + totalSentSegments;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Flow other = (Flow) obj;
		if (ackSeqNumArrivalTimes == null) {
			if (other.ackSeqNumArrivalTimes != null)
				return false;
		} else if (!ackSeqNumArrivalTimes.equals(other.ackSeqNumArrivalTimes))
			return false;
		if (Float.floatToIntBits(arrivalTime) != Float.floatToIntBits(other.arrivalTime))
			return false;
		if (Float.floatToIntBits(completionTime) != Float.floatToIntBits(other.completionTime))
			return false;
		if (Float.floatToIntBits(dataSendingStartTime) != Float.floatToIntBits(other.dataSendingStartTime))
			return false;
		if (dataSeqNumSendingTimes == null) {
			if (other.dataSeqNumSendingTimes != null)
				return false;
		} else if (!dataSeqNumSendingTimes.equals(other.dataSeqNumSendingTimes))
			return false;
		if (dstHostID != other.dstHostID)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (size != other.size)
			return false;
		if (srcHostID != other.srcHostID)
			return false;
		if (Float.floatToIntBits(totalBufferTime) != Float.floatToIntBits(other.totalBufferTime))
			return false;
		if (totalDroppedSegments != other.totalDroppedSegments)
			return false;
		if (totalSentSegments != other.totalSentSegments)
			return false;
		return true;
	}
}
