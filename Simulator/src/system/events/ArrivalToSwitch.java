package system.events;

import system.*;
import entities.*;

public class ArrivalToSwitch extends Event {

	public ArrivalToSwitch(float eventTime, int switchID, Packet packet) {
		super(eventTime, switchID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.switches.get(this.nodeID).recvPacket(net, packet);
	}

}
