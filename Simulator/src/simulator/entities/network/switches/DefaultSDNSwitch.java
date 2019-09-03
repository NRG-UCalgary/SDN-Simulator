package simulator.entities.network.switches;

import simulator.Network;
import simulator.entities.network.CtrlMessage;
import simulator.entities.network.SDNSwitch;
import simulator.entities.traffic.Packet;
import utility.Debugger;
import utility.Keywords;

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
		Debugger.debugToConsole("========== Arrival To Switch =================");
		switch (packet.type) {
		case Keywords.Packets.Types.SDNControl:
			/* ================================================ */
			/* ========== Control message stays in switch ===== */
			/* ================================================ */
			Debugger.debugToConsole("Control Message received by switch: " + ID);
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
				Debugger.debugToConsole(" Packet forwarded to host: " + packet.segment.getDstHostID());
			}
			/* ================================================ */
			/* ========== Packet to next switch =============== */
			/* ================================================ */
			else if (hasFlowEntry(packet.segment.getFlowID())) {
				forwardToSwitch(net, packet.segment.getFlowID(), packet);
				Debugger.debugToConsole(" Packet forwarded to switch: "
						+ net.links.get(flowTable.get(packet.segment.getFlowID())).getDstNodeID());

			}
			/* ================================================ */
			/* ========== Packet to controller ================ */
			/* ================================================ */
			else {
				forwardToController(net, packet);
				Debugger.debugToConsole(" Packet forwarded to controller.");
			}
			break;
		default:
			break;
		}
		Debugger.debugToConsole("==============================================");
	}

}
