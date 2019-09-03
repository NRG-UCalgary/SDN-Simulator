package simulator.events;

import simulator.Network;
import simulator.entities.traffic.Packet;
import utility.Debugger;

public class DepartureFromNode extends PacketEvent {

	int linkID;

	public DepartureFromNode(float eventTime, int linkID, Packet packet) {
		super(eventTime, packet);
		this.linkID = linkID;
	}

	@Override
	public void execute(Network net) {
		Debugger.debugToConsole("-------------------- Departure Event ------------------------");
		net.updateTime(eventTime);
		net.links.get(linkID).transmitPacket(net, packet);
	}

}
