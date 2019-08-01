package simulator.events.departures;

import simulator.Network;
import simulator.entities.Packet;
import simulator.events.PacketDeparture;

public class FromController extends PacketDeparture {
	int dstSwitchID;

	public FromController(float eventTime, int controllerID, int dstSwitchID, Packet packet) {
		super(eventTime, controllerID, packet);
		this.dstSwitchID = dstSwitchID;
	}

	public void execute(Network net) {
		net.updateTime(this.eventTime);
		net.controller.releasePacket(net, dstSwitchID, packet);
	}

}
