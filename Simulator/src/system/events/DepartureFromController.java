package system.events;

import entities.*;
import system.Event;
import system.Network;

public class DepartureFromController extends Event {
	int dstSwitchID;

	public DepartureFromController(float eventTime, int controllerID, int dstSwitchID, Packet packet) {
		super(eventTime, controllerID, packet);
		this.dstSwitchID = dstSwitchID;
	}

	public void execute(Network net) {
		net.updateTime(this.eventTime);
		net.controller.releasePacket(net, dstSwitchID, packet);
	}

}
