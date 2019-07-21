package entities.switches;

import entities.*;
import system.Network;
import system.Simulator;
import system.utility.Debugger;
import system.utility.Keywords;

public class SDNSwitchv1 extends SDNSwitch {

	/* Constructor */
	public SDNSwitchv1(int ID, Link controlLink) {
		super(ID, controlLink);
	}

	/* -------------------------------------------------------------- */
	/* ---------- Implemented abstract methods (from SDNSwitch) ----- */
	/* -------------------------------------------------------------- */
	public void recvCtrlMessage(Network net, CtrlMessage message) {
		switch (message.getType()) {
		case Keywords.SDNMessages.Types.BufferTokenUpdate:
			for (int hostID : message.ccTokenOfHostID.keySet()) {
				accessLinks.get(hostID).buffer.updateCCToken(net.getCurrentTime(), message.ccTokenOfHostID.get(hostID));
			}
			break;
		case Keywords.SDNMessages.Types.FlowSetUp:
			addFlowTableEntry(message.flowID, message.neighborID);
			net.switches.get(message.neighborID).addFlowTableEntry(Simulator.reverseFlowStreamID(message.flowID),
					this.getID());
			break;
		case Keywords.SDNMessages.Types.FlowRemoval:
			// TODO the flow entry can be removed here
			break;
		default:
			Debugger.debugToConsole("SNDSwitchv1.recvCtrlMessage()::Invalid message type.");
			break;
		}
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

}
