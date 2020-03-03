package nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import nrg.sdnsimulator.core.entity.network.buffer.BufferToken;
import nrg.sdnsimulator.core.utility.Keywords;

public class CtrlMessage {

	// BufferUpdate information
	private HashMap<Integer, BufferToken> ccTokenOfHostID; // <HostID, tokenForEachBuffer>
	// FlowSetup information
	private HashMap<Integer, Integer> flowTableEntries;// <FlowID, LinkID>
	private int size = Keywords.SDNMessages.Size;
	private int type;

	public CtrlMessage(int type) {
		flowTableEntries = new HashMap<Integer, Integer>();
		this.type = type;
	}

	public void addFlowSetUpEntry(int FlowID, int egressLinkID) {
		flowTableEntries.put(FlowID, egressLinkID);
	}

	public HashMap<Integer, BufferToken> getCcTokenOfHostID() {
		return ccTokenOfHostID;
	}

	public void setCcTokenOfHostID(HashMap<Integer, BufferToken> ccTokenOfHostID) {
		this.ccTokenOfHostID = ccTokenOfHostID;
	}

	public HashMap<Integer, Integer> getFlowTableEntries() {
		return flowTableEntries;
	}

	public void setFlowTableEntries(HashMap<Integer, Integer> flowTableEntries) {
		this.flowTableEntries = flowTableEntries;
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
