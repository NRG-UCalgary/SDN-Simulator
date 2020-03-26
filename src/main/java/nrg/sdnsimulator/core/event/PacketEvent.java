package nrg.sdnsimulator.core.event;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Event;
import nrg.sdnsimulator.core.entity.traffic.Packet;

@Getter
@Setter
public abstract class PacketEvent extends Event {

	protected Packet packet;

	public PacketEvent(float eventTime, Packet packet) {
		super(eventTime);
		this.packet = packet;
	}

}
