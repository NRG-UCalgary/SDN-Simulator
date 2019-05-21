package events;

import system.*;
import entities.*;

public class ArrivalToController extends Event {

	public ArrivalToController(double startTime, int nodeID, Segment segment) {
		super(startTime, nodeID, segment);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(currentTime);
		// TODO the interface method of controller must be called which will make a
		// recvFromController event for the corresponding switch
		net = net.controller.recvSegment(net, currentNodeID, currentSegment);
		return net;
	}

}
