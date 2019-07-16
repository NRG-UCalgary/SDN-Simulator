package system.events;

import system.*;
import system.utility.*;

import entities.*;

public class ArrivalToSwitch extends Event {

	public ArrivalToSwitch(float eventTime, int switchID, Packet packet) {
		super(Keywords.ArrivalToSwitch, eventTime, switchID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(eventTime);
		return net.switches.get(this.nodeID).recvPacket(net, packet);
	}

}
