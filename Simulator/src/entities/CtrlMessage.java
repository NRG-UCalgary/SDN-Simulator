package entities;

import java.util.HashMap;

import entities.buffers.BufferToken;
import system.utility.*;

public class CtrlMessage {

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
		this.neighborID = egressLink.getDstID();
	}

	public int getSize() {
		return this.size;
	}

	public int getType() {
		return this.type;
	}

}
