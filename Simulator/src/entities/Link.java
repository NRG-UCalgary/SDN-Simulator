package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import entities.buffers.Buffer;
import entities.buffers.Bufferv1;

/** Links are attributes of Switch and Host **/
/** There are two types of link: 1.accessLink 2.networkLink **/

public class Link extends Entity {

	public Buffer buffer;

	private int bandwidth;
	private double propDelay;
	private int srcNodeID;
	private int dstNodeID;

	/** ========== Statistical Counters ========== **/
	public HashMap<Integer, Double> utilizationTimePerFlow; // <FlowID, utilizationzTime>
	public Map<Double, Integer> arrivalTimePerFlowID;

	// in Departure event at releaseSegment in SDNSwitch
	/** ========================================== **/

	public Link(int ID, int sourceID, int destinationID, double propagationDelay, int band, int bufferSize,
			int bufferPolicy) {
		super(ID);
		this.bandwidth = band;// bits/millisecond
		this.propDelay = propagationDelay; // millisecond
		this.srcNodeID = sourceID;
		this.dstNodeID = destinationID;
		this.buffer = new Bufferv1(bufferSize, bufferPolicy);

		/** ========== Statistical Counters Initialization ========== **/
		utilizationTimePerFlow = new HashMap<Integer, Double>();
		arrivalTimePerFlowID = new TreeMap<Double, Integer>();
		/** ========================================================= **/
	}

	public double getTotalDelay(int segmentSize) {
		return this.getTransmissionDelay(segmentSize) + this.propDelay;
	}

	public double getTransmissionDelay(int segmentSize) {
		Double trans_delay = segmentSize / (double) this.bandwidth;
		return trans_delay;
	}

	/*------------------- Getters ------------------------*/
	public int getBandwidth() {
		return this.bandwidth;
	}

	public Double getPropagationDelay() {
		return this.propDelay;
	}

	public int getSrcID() {
		return this.srcNodeID;
	}

	public int getDstID() {
		return this.dstNodeID;
	}
}
