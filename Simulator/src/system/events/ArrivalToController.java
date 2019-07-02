package system.events;

import system.*;
import system.utility.Keywords;
import entities.*;

public class ArrivalToController extends Event {

	public ArrivalToController(double startTime, int nodeID, Packet packet) {
		super(Keywords.ArrivalToController, startTime, nodeID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		// Debugger.event(this.type, this.time, this.nodeID, this.segment, null);
		net.updateTime(eventTime);
		return net.controller.recvPacket(net, nodeID, packet);
	}

}
