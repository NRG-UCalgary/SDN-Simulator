package entities;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.util.Pair;

import entities.buffers.Buffer;
import entities.buffers.Bufferv1;

/** Links are attributes of Switch and Host **/
/** There are two types of link: 1.accessLink 2.networkLink **/

public class Link extends Entity {

	public Buffer buffer;

	private float bandwidth;
	private float propagationDelay;
	private int srcNodeID;
	private int dstNodeID;

	/** ========== Statistical Counters ========== **/
	public float totalUtilizationTime;
	public float totalUpTime;
	public HashMap<Integer, Float> utilizationTimePerFlowID; // <FlowID, utilizationzTime>
	public ArrayList<Pair<Float, Float>> arrivalTimeOfFlowID;

	/** ========================================== **/

	public Link(int ID, int sourceID, int destinationID, float propagationDelay, float band, int bufferSize,
			int bufferPolicy) {
		super(ID);
		this.bandwidth = band;// bits/microsecond
		this.propagationDelay = propagationDelay; // microsecond
		this.srcNodeID = sourceID;
		this.dstNodeID = destinationID;
		this.buffer = new Bufferv1(bufferSize, bufferPolicy);

		/** ========== Statistical Counters Initialization ========== **/
		totalUtilizationTime = 0;
		totalUpTime = 0;
		utilizationTimePerFlowID = new HashMap<Integer, Float>();
		arrivalTimeOfFlowID = new ArrayList<Pair<Float, Float>>();
		/** ========================================================= **/
	}

	public float getTotalDelay(int segmentSize) {
		return this.getTransmissionDelay(segmentSize) + propagationDelay;
	}

	public float getTransmissionDelay(int segmentSize) {
		return segmentSize / (float) bandwidth;
	}

	/*------------------- Getters ------------------------*/
	public float getBandwidth() {
		return bandwidth;
	}

	public float getPropagationDelay() {
		return propagationDelay;
	}

	public int getSrcNodeID() {
		return srcNodeID;
	}

	public int getDstNodeID() {
		return dstNodeID;
	}
}
