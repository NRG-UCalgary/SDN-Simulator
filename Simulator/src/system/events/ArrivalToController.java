package system.events;

import system.*;
import system.utility.Keywords;
import entities.*;

public class ArrivalToController extends Event {

	public ArrivalToController(float eventTime, int nodeID, Packet packet) {
		super(Keywords.Operations.Events.Names.Arrivals.ArrivalToController, eventTime, nodeID, packet);
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
