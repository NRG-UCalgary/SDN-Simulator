package system.events;

import system.*;
import entities.*;

public class ArrivalToController extends Event {

	public ArrivalToController(float eventTime, int nodeID, Packet packet) {
		super(eventTime, nodeID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.controller.recvPacket(net, nodeID, packet);
	}

}
