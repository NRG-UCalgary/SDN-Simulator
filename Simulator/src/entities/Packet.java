package entities;

import system.utility.Keywords;

public class Packet extends Entity {
	private int size;
	public int type;
	public Segment segment;
	public CtrlMessage controlMessage;

	public Packet(Segment segment, CtrlMessage controlMessage) {
		super(-1);
		if (segment != null) {
			size = segment.getSize();
			type = Keywords.Operations.Packets.Types.Segment;
			this.segment = segment;
		} else if (controlMessage != null) {
			size = controlMessage.getSize();
			type = Keywords.Operations.Packets.Types.SDNControl;
			this.controlMessage = controlMessage;
		}
	}

	public int getSize() {
		return size;
	}

}
