package com.nrg.sdnsimulator.core.event;

import com.nrg.sdnsimulator.core.Network;
import com.nrg.sdnsimulator.core.entity.traffic.Packet;

public class DepartureFromNode extends PacketEvent {

	int linkID;

	public DepartureFromNode(float eventTime, int linkID, Packet packet) {
		super(eventTime, packet);
		this.linkID = linkID;
	}

	@Override
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.links.get(linkID).transmitPacket(net, packet);
	}

}
