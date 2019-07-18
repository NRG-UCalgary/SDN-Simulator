package entities;

import system.utility.Keywords;

public class Packet extends Entity {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((controlMessage == null) ? 0 : controlMessage.hashCode());
		result = prime * result + ((segment == null) ? 0 : segment.hashCode());
		result = prime * result + size;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Packet other = (Packet) obj;
		if (controlMessage == null) {
			if (other.controlMessage != null)
				return false;
		} else if (!controlMessage.equals(other.controlMessage))
			return false;
		if (segment == null) {
			if (other.segment != null)
				return false;
		} else if (!segment.equals(other.segment))
			return false;
		if (size != other.size)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	private int size;
	public int type;
	public Segment segment;
	public CtrlMessage controlMessage;

	public Packet(Segment segment, CtrlMessage controlMessage) {
		super(-1);
		if (segment != null) {
			size = segment.getSize();
			type = Keywords.Segment;
			this.segment = segment;
		} else if (controlMessage != null) {
			size = controlMessage.getSize();
			type = Keywords.SDNControl;
			this.controlMessage = controlMessage;
		}
	}

	public int getSize() {
		return size;
	}

}
