package simulator.entities;

import java.util.HashMap;

import simulator.entities.buffers.BufferToken;
import utility.Keywords;

public class CtrlMessage {
	// BufferUpdate information
	public HashMap<Integer, BufferToken> ccTokenOfHostID; // <HostID, tokenForEachBuffer>
	public int flowID;

	// FlowSetup information
	public int neighborID;
	private int size = Keywords.SDNMessages.Size;

	private int type;

	public CtrlMessage(int type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public int getType() {
		return type;
	}

	public void prepareFlowSetUpMessage(int FlowID, Link egressLink) {
		this.flowID = FlowID;
		this.neighborID = egressLink.getDstNodeID();
	}

}
