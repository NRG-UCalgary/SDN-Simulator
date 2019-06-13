package events;

import system.*;
import entities.*;

public class ArrivalToController extends Event {

	public ArrivalToController(double startTime, int nodeID, Segment segment) {
		super(startTime, nodeID, segment);
		name = "Arrival to Controller";
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(currentTime);
		Main.debug("ArrivalToController.execute()::Controller ID = " + this.currentNodeID);
		return net.controller.recvSegment(net, currentNodeID, currentSegment);
	}

}
