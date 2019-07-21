package entities;

import java.util.HashMap;

import entities.buffers.BufferToken;
import system.utility.*;

public class CtrlMessage {
	private int type;
	private int size = Keywords.SDNMessages.Size;

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
