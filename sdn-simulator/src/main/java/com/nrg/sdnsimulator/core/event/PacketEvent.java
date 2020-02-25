package com.nrg.sdnsimulator.core.event;

import com.nrg.sdnsimulator.core.Event;
import com.nrg.sdnsimulator.core.entity.traffic.Packet;

public abstract class PacketEvent extends Event {

	Packet packet;

	public PacketEvent(float eventTime, Packet packet) {
		super(eventTime);
		this.packet = packet;
	}

}
