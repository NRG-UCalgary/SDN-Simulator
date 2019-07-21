package system.events;

import entities.*;
import system.*;

public class DepartureFromSwitch extends Event {
	int dstNodeID;

	public DepartureFromSwitch(float eventTime, int switchID, int dstNodeID, Packet packet) {
		super(eventTime, switchID, packet);
		this.dstNodeID = dstNodeID;
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.switches.get(this.nodeID).releasePacket(net, dstNodeID, packet);
	}
}
