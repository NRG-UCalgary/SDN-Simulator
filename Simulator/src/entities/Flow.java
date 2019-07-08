package entities;

import java.util.ArrayList;
import java.util.TreeMap;

import entities.switches.SDNSwitchv1;

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
	public TreeMap<Integer, Double> dataSeqNumSendingTimes; // <SeqNum, Time>
	public TreeMap<Integer, Double> ackSeqNumArrivalTimes; // <SeqNum, Time>

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

		dataSeqNumSendingTimes = new TreeMap<Integer, Double>();
		ackSeqNumArrivalTimes = new TreeMap<Integer, Double>();
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
