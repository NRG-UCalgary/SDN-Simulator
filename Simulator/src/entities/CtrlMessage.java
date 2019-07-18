package entities;

import java.util.HashMap;

import entities.buffers.BufferToken;
import system.utility.*;

public class CtrlMessage {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ccTokenOfHostID == null) ? 0 : ccTokenOfHostID.hashCode());
		result = prime * result + flowID;
		result = prime * result + neighborID;
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
		CtrlMessage other = (CtrlMessage) obj;
		if (ccTokenOfHostID == null) {
			if (other.ccTokenOfHostID != null)
				return false;
		} else if (!ccTokenOfHostID.equals(other.ccTokenOfHostID))
			return false;
		if (flowID != other.flowID)
			return false;
		if (neighborID != other.neighborID)
			return false;
		if (size != other.size)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	private int type;
	private int size = Keywords.CtrlMessageSize;

	// FlowSetup information
	public int neighborID;
	public int flowID;

	// BufferUpdate information
	public HashMap<Integer, BufferToken> ccTokenOfHostID; // <HostID, tokenForEachBuffer>

	public CtrlMessage(int type) {
		this.type = type;
	}

	public void prepareFlowSetUpMessage(int FlowID, Link egressLink) {
		this.flowID = FlowID;
		this.neighborID = egressLink.getDstNodeID();
	}

	public int getSize() {
		return size;
	}

	public int getType() {
		return type;
	}

}
