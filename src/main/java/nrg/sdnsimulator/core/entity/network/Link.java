package nrg.sdnsimulator.core.entity.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.Entity;
import nrg.sdnsimulator.core.entity.network.buffer.Bufferv1;
import nrg.sdnsimulator.core.entity.network.buffer.DefaultBuffer;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.utility.Keywords;
import nrg.sdnsimulator.core.utility.Mathematics;

@Getter
@Setter
public abstract class Link extends Entity {

	protected boolean isMonitored;
	protected boolean isNetworkBottleneck;
	protected boolean isPathBottleneck;
	protected Buffer buffer;
	protected float bandwidth;
	protected float propagationDelay;
	protected int srcNodeID;
	protected int dstNodeID;

	/** ========== Statistical Counters ========== **/
	protected float totalTransmissionTime;
	protected float firstSegmentArrivalTime;
	protected float lastSegmentTransmittedTime;
	protected float maxQeueLength;
	protected TreeMap<Float, Float> queueLength;
	protected HashMap<Float, Float> utilizationTimePerFlowID; // <FlowID, utilizationzTime>
	protected ArrayList<Pair<Float, Float>> segmentArrivalTimeOfFlowID; // Array<<FlowID,

	/** ========================================== **/

	public Link(int ID, int sourceID, int destinationID, float propagationDelay, float band, short bufferType,
			int bufferSize, int bufferPolicy) {
		super(ID);
		isMonitored = false;
		isNetworkBottleneck = false;
		isPathBottleneck = false;
		this.bandwidth = band;// bits/microsecond
		this.propagationDelay = propagationDelay; // microsecond
		this.srcNodeID = sourceID;
		this.dstNodeID = destinationID;
		switch (bufferType) {
		case Keywords.Entities.Buffers.Types.Buffer_1:
			buffer = new Bufferv1(bufferSize, bufferPolicy);
			break;
		default:
			buffer = new DefaultBuffer(bufferSize, bufferPolicy);
			break;
		}
		isMonitored = false;

		/** ========== Statistical Counters Initialization ========== **/
		totalTransmissionTime = 0;
		firstSegmentArrivalTime = 0;
		lastSegmentTransmittedTime = 0;
		maxQeueLength = 0;
		queueLength = new TreeMap<Float, Float>();
		utilizationTimePerFlowID = new HashMap<Float, Float>();
		segmentArrivalTimeOfFlowID = new ArrayList<Pair<Float, Float>>();
		/** ==================================================== **/

	}

	public abstract void bufferPacket(Network net, Packet packet);

	public abstract void transmitPacket(Network net, Packet packet);

	public float getTotalDelay(int segmentSize) {
		return Mathematics.addFloat(this.getTransmissionDelay(segmentSize), propagationDelay);
	}

	public float getTransmissionDelay(int segmentSize) {
		return Mathematics.divideFloat(segmentSize, bandwidth);
	}

	public void updateUtilizationCounters(float currentTime, int flowID, float transmissionDelay) {
		if (isMonitored) {
			if (firstSegmentArrivalTime == 0) {
				firstSegmentArrivalTime = currentTime;
			}
			totalTransmissionTime = Mathematics.addFloat(totalTransmissionTime, transmissionDelay);
			lastSegmentTransmittedTime = Mathematics.addFloat(currentTime, transmissionDelay);
			if (utilizationTimePerFlowID.containsKey((float) flowID)) {
				utilizationTimePerFlowID.put((float) flowID,
						Mathematics.addFloat(utilizationTimePerFlowID.get((float) flowID), transmissionDelay));
			} else {
				utilizationTimePerFlowID.put((float) flowID, transmissionDelay);
			}
		}

	}

	public void updateMaxQueueLengthCounter(int bufferOccupancy) {
		if (bufferOccupancy > maxQeueLength) {
			maxQeueLength = bufferOccupancy;
		}
	}

	public void updateQueueLenghtCounter(float time, int bufferOccupancy) {
		queueLength.put(time, (float) (bufferOccupancy));
	}

	public void updateSegementArrivalToLinkCounters(float segmentArrivalTime, int flowID) {
		if (isMonitored) {
			segmentArrivalTimeOfFlowID.add(new Pair<Float, Float>((float) flowID, segmentArrivalTime));
		}
	}

}
