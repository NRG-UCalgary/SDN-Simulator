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
		return net.controller.recvSegment(net, currentNodeID, currentSegment);
	}

}
