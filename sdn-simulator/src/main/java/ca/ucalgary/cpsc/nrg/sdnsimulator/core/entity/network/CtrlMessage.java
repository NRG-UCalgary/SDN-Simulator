package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.buffer.BufferToken;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Keywords;


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
