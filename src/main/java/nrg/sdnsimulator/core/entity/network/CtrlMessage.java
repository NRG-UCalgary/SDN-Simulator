package nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.entity.network.buffer.BufferToken;
import nrg.sdnsimulator.core.utility.Keywords;

@Getter
@Setter
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


}
