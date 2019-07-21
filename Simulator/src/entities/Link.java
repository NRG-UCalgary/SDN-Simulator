package entities;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.util.Pair;

import entities.buffers.*;

/** Links are attributes of Switch and Host **/
/** There are two types of link: 1.accessLink 2.networkLink **/

public class Link extends Entity {

	public boolean isMonitored;
	public Buffer buffer;

	private float bandwidth;
	private float propagationDelay;
	private int srcNodeID;
	private int dstNodeID;

	/** ========== Statistical Counters ========== **/
	public float totalTransmissionTime;
	public float firstSegmentArrivalTime;
	public float lastSegmentTransmittedTime;
	public HashMap<Float, Float> utilizationTimePerFlowID; // <FlowID, utilizationzTime>
	public ArrayList<Pair<Float, Float>> segmentArrivalTimeOfFlowID; // Array<<FlowID, ArrivalTime>>

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
		totalTransmissionTime = 0;
		firstSegmentArrivalTime = 0;
		lastSegmentTransmittedTime = 0;
		utilizationTimePerFlowID = new HashMap<Float, Float>();
		segmentArrivalTimeOfFlowID = new ArrayList<Pair<Float, Float>>();
		/** ==================================================== **/
		isMonitored = false;
	}

	public float getTotalDelay(int segmentSize) {
		return this.getTransmissionDelay(segmentSize) + propagationDelay;
	}

	public float getTransmissionDelay(int segmentSize) {
		return segmentSize / (float) bandwidth;
	}

	/*---------- Statistical counter methods ---------- */
	public void updateUtilizationCounters(float currentTime, int flowID, float transmissionDelay) {
		if (isMonitored) {
			if (firstSegmentArrivalTime == 0) {
				firstSegmentArrivalTime = currentTime;
			}
			if (utilizationTimePerFlowID.containsKey((float) flowID)) {
				utilizationTimePerFlowID.put((float) flowID,
						utilizationTimePerFlowID.get((float) flowID) + transmissionDelay);
			} else {
				utilizationTimePerFlowID.put((float) flowID, transmissionDelay);
			}
			totalTransmissionTime += transmissionDelay;
		}

	}

	public void updateSegementArrivalToLinkCounters(int flowID, float segmentArrivalTime) {
		if (isMonitored) {
			segmentArrivalTimeOfFlowID.add(new Pair<Float, Float>((float) flowID, segmentArrivalTime));
		}
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
