package simulator.entities.network.switches;

import simulator.Network;
import simulator.entities.network.CtrlMessage;
import simulator.entities.network.SDNSwitch;
import simulator.entities.traffic.Packet;
import utility.Debugger;
import utility.Keywords;

public class SDNSwitchv1 extends SDNSwitch {

	/* Constructor */
	public SDNSwitchv1(int ID) {
		super(ID);
	}

	/* -------------------------------------------------------------- */
	/* ---------- Implemented abstract methods (from SDNSwitch) ----- */
	/* -------------------------------------------------------------- */
	protected void recvCtrlMessage(Network net, CtrlMessage message) {
		switch (message.getType()) {
		case Keywords.SDNMessages.Types.BufferTokenUpdate:
			for (int hostID : message.ccTokenOfHostID.keySet()) {
				net.links.get(accessLinksIDs.get(hostID)).buffer.updateCCToken(net.getCurrentTime(),
						message.ccTokenOfHostID.get(hostID));
			}
			break;
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
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	public void recvPacket(Network net, int srcNodeID, Packet packet) {
		switch (packet.type) {
		case Keywords.Packets.Types.SDNControl:
			/* ================================================ */
			/* ========== Packet stays in switch ============== */
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
					/* Uncontrolled FIN goes to controller */
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
