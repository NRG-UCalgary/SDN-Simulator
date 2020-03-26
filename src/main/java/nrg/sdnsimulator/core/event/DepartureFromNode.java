package nrg.sdnsimulator.core.event;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.traffic.Packet;

@Getter
@Setter
public class DepartureFromNode extends PacketEvent {

	private int linkID;

	public DepartureFromNode(float eventTime, int linkID, Packet packet) {
		super(eventTime, packet);
		this.linkID = linkID;
	}

	@Override
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.getLinks().get(linkID).transmitPacket(net, packet);
	}

}
