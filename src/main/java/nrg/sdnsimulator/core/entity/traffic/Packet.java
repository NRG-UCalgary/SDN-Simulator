package nrg.sdnsimulator.core.entity.traffic;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.entity.network.CtrlMessage;
import nrg.sdnsimulator.core.utility.Keywords;

@Getter
@Setter
public class Packet {

	private CtrlMessage controlMessage;
	private Segment segment;
	private int size;
	private int type;

	public Packet(Segment segment, CtrlMessage controlMessage) {
		if (segment != null) {
			size = segment.getSize();
			type = Keywords.Packets.Types.Segment;
			this.segment = segment;
		} else if (controlMessage != null) {
			size = controlMessage.getSize();
			type = Keywords.Packets.Types.SDNControl;
			this.controlMessage = controlMessage;
		} else {
		}
	}

}
