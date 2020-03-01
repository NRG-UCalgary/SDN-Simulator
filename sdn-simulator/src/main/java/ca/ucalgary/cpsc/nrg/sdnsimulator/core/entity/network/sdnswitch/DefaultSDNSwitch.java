package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.sdnswitch;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.CtrlMessage;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.SDNSwitch;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Packet;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Debugger;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Keywords;

public class DefaultSDNSwitch extends SDNSwitch {

	public DefaultSDNSwitch(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from SDNSwitch) ----- */
	/* --------------------------------------------------- */
	protected void recvCtrlMessage(Network net, CtrlMessage message) {
		switch (message.getType()) {
		case Keywords.SDNMessages.Types.FlowSetUp:
			for (int flowID : message.flowTableEntries.keySet()) {
				addFlowTableEntry(flowID, message.flowTableEntries.get(flowID));
			}
			break;
		case Keywords.SDNMessages.Types.FlowRemoval:
			break;
		default:
			Debugger.debugToConsole("SNDSwitchv1.recvCtrlMessage()::Invalid message type.");
			break;
		}
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public void recvPacket(Network net, int srcNodeID, Packet packet) {
		switch (packet.type) {
		case Keywords.Packets.Types.SDNControl:
			/* ================================================ */
			/* ========== Control message stays in switch ===== */
			/* ================================================ */
			recvCtrlMessage(net, packet.controlMessage);
			break;
		case Keywords.Packets.Types.Segment:
			/* ================================================ */
			/* ========== Broadcast packet to hosts =========== */
			/* ================================================ */
			if (packet.segment.getDstHostID() == Keywords.BroadcastDestination) {
				broadcastToHosts(net, packet);
			}
			/* ================================================ */
			/* ========== Packet to end host ================== */
			/* ================================================ */
			else if (isConnectedToHost(packet.segment.getDstHostID())) {
				forwardToHost(net, packet.segment.getDstHostID(), packet);
			}
			/* ================================================ */
			/* ========== Packet to next switch =============== */
			/* ================================================ */
			else if (hasFlowEntry(packet.segment.getFlowID())) {
				if (packet.segment.getType() == Keywords.Segments.Types.UncontrolledFIN) {
					forwardToController(net, packet);
				} else {
					forwardToSwitch(net, packet.segment.getFlowID(), packet);
				}
			}
			/* ================================================ */
			/* ========== Packet to controller ================ */
			/* ================================================ */
			else {
				forwardToController(net, packet);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
		// The switch does not have timer for now
	}

}
