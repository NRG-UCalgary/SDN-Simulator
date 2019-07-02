package system.events;

import system.*;
import system.utility.*;

import entities.*;

public class ArrivalToSwitch extends Event {

	public ArrivalToSwitch(double startTime, int switchID, Packet packet) {
		super(Keywords.ArrivalToSwitch, startTime, switchID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(eventTime);
		return net.switches.get(this.nodeID).recvPacket(net, packet);
	}

}
