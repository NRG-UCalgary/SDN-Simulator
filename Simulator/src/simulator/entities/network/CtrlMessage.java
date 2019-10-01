package simulator.entities.network;

import java.util.HashMap;

import simulator.entities.network.buffers.BufferToken;
import utility.Keywords;

public class CtrlMessage {

	// BufferUpdate information
	public HashMap<Integer, BufferToken> ccTokenOfHostID; // <HostID, tokenForEachBuffer>

	// FlowSetup information
	public HashMap<Integer, Integer> flowTableEntries;// <FlowID, LinkID>
	private int size = Keywords.SDNMessages.Size;

	private int type;

	public CtrlMessage(int type) {
		flowTableEntries = new HashMap<Integer, Integer>();
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public int getType() {
		return type;
	}

	public void addFlowSetUpEntry(int FlowID, int egressLinkID) {
		flowTableEntries.put(FlowID, egressLinkID);
	}

}
