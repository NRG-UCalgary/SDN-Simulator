package nrg.sdnsimulator.core.entity.network;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.Entity;
import nrg.sdnsimulator.core.entity.traffic.Flow;
import nrg.sdnsimulator.core.entity.traffic.Segment;

@Getter
@Setter
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

}
