package nrg.sdnsimulator.core.entity.traffic;

import nrg.sdnsimulator.core.entity.network.CtrlMessage;
import nrg.sdnsimulator.core.utility.Keywords;

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

	public CtrlMessage getControlMessage() {
		return controlMessage;
	}

	public void setControlMessage(CtrlMessage controlMessage) {
		this.controlMessage = controlMessage;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
