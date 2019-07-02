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
			this.size = segment.getSize();
			this.type = Keywords.Segment;
			this.segment = segment;
		} else if (controlMessage != null) {
			this.size = controlMessage.getSize();
			this.type = Keywords.SDNControl;
			this.controlMessage = controlMessage;
		}

	}

	public int getSize() {
		return this.size;
	}

}
