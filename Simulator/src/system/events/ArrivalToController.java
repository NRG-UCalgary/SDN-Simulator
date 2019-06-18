package system.events;

import system.*;
import system.utility.Keywords;
import entities.*;

public class ArrivalToController extends Event {

	public ArrivalToController(double startTime, int nodeID, Segment segment) {
		super(Keywords.ArrivalToController, startTime, nodeID, segment);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		//Debugger.event(this.type, this.time, this.nodeID, this.segment, null);
		net.updateTime(time);
		return net.controller.recvSegment(net, nodeID, segment);
	}

}
