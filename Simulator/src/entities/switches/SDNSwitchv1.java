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

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from SDNSwitch) ----- */
	/* --------------------------------------------------- */
	public Network recvCtrlMessage(Network net, CtrlMessage message) {
		switch (message.getType()) {
		case Keywords.BufferTokenUpdate:
			Debugger.debugToConsole("The Token arrived at: " + net.getCurrentTime());
			for (int hostID : accessLinks.keySet()) {
				accessLinks.get(hostID).buffer.updateTokenList(net.getCurrentTime(),
						message.ccTokenOfHostID.get(hostID));
			}
			break;
		case Keywords.FlowSetUp:
			addFlowTableEntry(message.flowID, message.neighborID);
			net.switches.get(message.neighborID).addFlowTableEntry(Simulator.ACKStreamID(message.flowID), this.getID());
			break;
		default:
			Debugger.debugToConsole("SNDSwitchv1.recvCtrlMessage()::Invalid message type.");
			break;
		}
		return net;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

}
