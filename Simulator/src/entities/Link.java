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
		totalUtilizationTime = 0;
		totalUpTime = 0;
		utilizationTimePerFlowID = new HashMap<Float, Float>();
		segmentArrivalTimeOfFlowID = new ArrayList<Pair<Float, Float>>();/**
																			 * =========================================================
																			 **/
	}

	public float getTotalDelay(int segmentSize) {
		return this.getTransmissionDelay(segmentSize) + propagationDelay;
	}

	public float getTransmissionDelay(int segmentSize) {
		return segmentSize / (float) bandwidth;
	}

	/*---------- Statistical counter methods ---------- */
	public void updateUtilizationCounters(int flowID, float transmissionDelay) {
		if (utilizationTimePerFlowID.containsKey((float) flowID)) {
			utilizationTimePerFlowID.put((float) flowID,
					utilizationTimePerFlowID.get((float) flowID) + transmissionDelay);
		} else {
			utilizationTimePerFlowID.put((float) flowID, transmissionDelay);
		}
		totalUtilizationTime += transmissionDelay;

	}

	public void updateSegementArrivalToLinkCounters(int flowID, float segmentArrivalTime) {
		segmentArrivalTimeOfFlowID.add(new Pair<Float, Float>((float) flowID, segmentArrivalTime));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(bandwidth);
		result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
		result = prime * result + dstNodeID;
		result = prime * result + Float.floatToIntBits(propagationDelay);
		result = prime * result + ((segmentArrivalTimeOfFlowID == null) ? 0 : segmentArrivalTimeOfFlowID.hashCode());
		result = prime * result + srcNodeID;
		result = prime * result + Float.floatToIntBits(totalUpTime);
		result = prime * result + Float.floatToIntBits(totalUtilizationTime);
		result = prime * result + ((utilizationTimePerFlowID == null) ? 0 : utilizationTimePerFlowID.hashCode());
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
		Link other = (Link) obj;
		if (Float.floatToIntBits(bandwidth) != Float.floatToIntBits(other.bandwidth))
			return false;
		if (buffer == null) {
			if (other.buffer != null)
				return false;
		} else if (!buffer.equals(other.buffer))
			return false;
		if (dstNodeID != other.dstNodeID)
			return false;
		if (Float.floatToIntBits(propagationDelay) != Float.floatToIntBits(other.propagationDelay))
			return false;
		if (segmentArrivalTimeOfFlowID == null) {
			if (other.segmentArrivalTimeOfFlowID != null)
				return false;
		} else if (!segmentArrivalTimeOfFlowID.equals(other.segmentArrivalTimeOfFlowID))
			return false;
		if (srcNodeID != other.srcNodeID)
			return false;
		if (Float.floatToIntBits(totalUpTime) != Float.floatToIntBits(other.totalUpTime))
			return false;
		if (Float.floatToIntBits(totalUtilizationTime) != Float.floatToIntBits(other.totalUtilizationTime))
			return false;
		if (utilizationTimePerFlowID == null) {
			if (other.utilizationTimePerFlowID != null)
				return false;
		} else if (!utilizationTimePerFlowID.equals(other.utilizationTimePerFlowID))
			return false;
		return true;
	}
}
