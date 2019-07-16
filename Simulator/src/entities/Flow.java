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
	public float completionTime; // in Receiver Agent
	public int totalDroppedSegments; // in Buffer
	public int totalSentSegments; // in Sender Agent
	public float totalBufferTime; // in Buffer (when getting bufferTime)

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

		dataSeqNumSendingTimes = new TreeMap<Float, Float>();
		ackSeqNumArrivalTimes = new TreeMap<Float, Float>();
		/** ========================================================= **/
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
}
