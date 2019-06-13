package entities;

import switches.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Flow extends Entity {

	private Host source;
	private Host destination;
	private int size;

	/** ========== Statistical Counters ========== **/
	public double arrivalTime; // in Simulator
	public double dataSendingStartTime; // in Sender Agent
	public double completionTime; // in Receiver Agent
	public int totalDroppedSegments; // in Buffer
	public int totalSentSegments; // in Sender Agent
	public double totalBufferTime; // in Buffer (when getting bufferTime)

	/* Sequence Number */
	// in Sender Agent
	public HashMap<Integer, Double> dataSeqNumSendingTimes; // <SeqNum, Time>
	public HashMap<Integer, Double> ackSeqNumArrivalTimes; // <SeqNum, Time>

	/** ========================================== **/

	/* Stores the Node IDs of the path */
	/* Initialized at the creation of the flow */
	public ArrayList<SDNSwitchv1> path = new ArrayList<SDNSwitchv1>();

	public Flow(int ID, Host src, Host dst, int size, Double arrvTime) {
		super(ID);
		this.source = src;
		this.destination = dst;
		this.size = size;

		/** ========== Statistical Counters Initialization ========== **/
		arrivalTime = arrvTime;
		dataSendingStartTime = 0;
		completionTime = 0;
		totalDroppedSegments = 0;
		totalSentSegments = 0;
		totalBufferTime = 0;

		dataSeqNumSendingTimes = new HashMap<Integer, Double>();
		ackSeqNumArrivalTimes = new HashMap<Integer, Double>();
		/** ========================================================= **/
	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/

	public int getSize() {
		return size;
	}

	public int getDstID() {
		int id = destination.getID();
		return id;
	}

	public int getSrcID() {
		return source.getID();
	}
}
