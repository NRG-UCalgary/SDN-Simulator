package system.events;

import entities.*;
import system.Event;
import system.Network;
import system.utility.Keywords;

public class DepartureFromController extends Event {
	int dstSwitchID;

	public DepartureFromController(float eventTime, int controllerID, int dstSwitchID, Packet packet) {
		super(Keywords.DepartureFromController, eventTime, controllerID, packet);
		this.dstSwitchID = dstSwitchID;
	}

	public Network execute(Network net) {
		net.updateTime(this.eventTime);
		return net.controller.releasePacket(net, dstSwitchID, packet);
	}

}
