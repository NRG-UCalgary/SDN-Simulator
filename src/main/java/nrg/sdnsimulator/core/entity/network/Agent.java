package nrg.sdnsimulator.core.entity.network;

import java.util.ArrayList;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.Entity;
import nrg.sdnsimulator.core.entity.traffic.Flow;
import nrg.sdnsimulator.core.entity.traffic.Segment;

public abstract class Agent extends Entity {

	protected int srcHostID;
	protected int dstHostID;
	protected Flow flow;
	protected float interSegmentDelay_;
	protected float mostRecentSegmentDepartureTime;
	protected ArrayList<Segment> segmentsToSend;

	public Agent(Flow flow) {
		super(-1);
		this.flow = flow;
		srcHostID = flow.getSrcHostID();
		dstHostID = flow.getDstHostID();
		segmentsToSend = new ArrayList<Segment>();
	}

	public abstract void recvSegment(Network net, Segment segment);

	public abstract void sendFirst(Network net);

	public abstract void timeout(Network net, int timerID);

	public int getSrcHostID() {
		return srcHostID;
	}

	public void setSrcHostID(int srcHostID) {
		this.srcHostID = srcHostID;
	}

	public int getDstHostID() {
		return dstHostID;
	}

	public void setDstHostID(int dstHostID) {
		this.dstHostID = dstHostID;
	}

	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	public float getInterSegmentDelay_() {
		return interSegmentDelay_;
	}

	public void setInterSegmentDelay_(float interSegmentDelay_) {
		this.interSegmentDelay_ = interSegmentDelay_;
	}

	public float getMostRecentSegmentDepartureTime() {
		return mostRecentSegmentDepartureTime;
	}

	public void setMostRecentSegmentDepartureTime(float mostRecentSegmentDepartureTime) {
		this.mostRecentSegmentDepartureTime = mostRecentSegmentDepartureTime;
	}

	public ArrayList<Segment> getSegmentsToSend() {
		return segmentsToSend;
	}

	public void setSegmentsToSend(ArrayList<Segment> segmentsToSend) {
		this.segmentsToSend = segmentsToSend;
	}

}
