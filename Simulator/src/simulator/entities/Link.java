package simulator.entities;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.util.Pair;

import simulator.entities.buffers.Bufferv1;
import utility.Mathematics;

public abstract class Link extends Entity {

	protected float bandwidth;
	public Buffer buffer;

	protected int dstNodeID;
	public float firstSegmentArrivalTime;
	public boolean isMonitored;
	public float lastSegmentTransmittedTime;

	protected float propagationDelay;
	public ArrayList<Pair<Float, Float>> segmentArrivalTimeOfFlowID; // Array<<FlowID, ArrivalTime>>
	protected int srcNodeID;
	/** ========== Statistical Counters ========== **/
	public float totalTransmissionTime;
	public HashMap<Float, Float> utilizationTimePerFlowID; // <FlowID, utilizationzTime>

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

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract float bufferPacket(float currentTime, Packet packet);

	/*------------------- Getters ------------------------*/
	public float getBandwidth() {
		return bandwidth;
	}

	public int getDstNodeID() {
		return dstNodeID;
	}

	public float getPropagationDelay() {
		return propagationDelay;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	public float getTotalDelay(int segmentSize) {
		return Mathematics.addFloat(this.getTransmissionDelay(segmentSize), propagationDelay);
	}

	public float getTransmissionDelay(int segmentSize) {
		return Mathematics.divideFloat(segmentSize, bandwidth);
	}

	public abstract float transmitPacket(float currentTime, Packet packet);

	public void updateSegementArrivalToLinkCounters(float segmentArrivalTime, int flowID) {
		if (isMonitored) {
			segmentArrivalTimeOfFlowID.add(new Pair<Float, Float>((float) flowID, segmentArrivalTime));
		}
	}

	/*---------- Statistical counter methods ---------- */
	public void updateUtilizationCounters(float currentTime, int flowID, float transmissionDelay) {
		if (isMonitored) {
			if (firstSegmentArrivalTime == 0) {
				firstSegmentArrivalTime = currentTime;
			}
			lastSegmentTransmittedTime = Mathematics.addFloat(currentTime, transmissionDelay);
			if (utilizationTimePerFlowID.containsKey((float) flowID)) {
				utilizationTimePerFlowID.put((float) flowID,
						Mathematics.addFloat(utilizationTimePerFlowID.get((float) flowID), transmissionDelay));
			} else {
				utilizationTimePerFlowID.put((float) flowID, transmissionDelay);
			}
			totalTransmissionTime = Mathematics.addFloat(totalTransmissionTime, transmissionDelay);
		}

	}

}
