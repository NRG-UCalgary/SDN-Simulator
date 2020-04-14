package nrg.sdnsimulator.core.entity.network.sdnswitch;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.CtrlMessage;
import nrg.sdnsimulator.core.entity.network.SDNSwitch;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.utility.Keywords;

public class DefaultSDNSwitch extends SDNSwitch {

	public DefaultSDNSwitch(int ID) {
		super(ID);
	}

	protected void recvCtrlMessage(Network net, CtrlMessage message) {
		switch (message.getType()) {
		case Keywords.SDNMessages.Types.FlowSetUp:
			for (int flowID : message.getFlowTableEntries().keySet()) {
				addFlowTableEntry(flowID, message.getFlowTableEntries().get(flowID));
			}
			break;
		case Keywords.SDNMessages.Types.FlowRemoval:
			break;
		default:
			break;
		}
	}

	public void recvPacket(Network net, int srcNodeID, Packet packet) {
		switch (packet.getType()) {
		case Keywords.Packets.Types.SDNControl:
			/* ================================================ */
			/* ========== Control message stays in switch ===== */
			/* ================================================ */
			recvCtrlMessage(net, packet.getControlMessage());
			break;
		case Keywords.Packets.Types.Segment:
			/* ================================================ */
			/* ========== Broadcast packet to hosts =========== */
			/* ================================================ */
			if (packet.getSegment().getDstHostID() == Keywords.BroadcastDestination) {
				broadcastToHosts(net, packet);
			}
			/* ================================================ */
			/* ========== Packet to end host ================== */
			/* ================================================ */
			else if (isConnectedToHost(packet.getSegment().getDstHostID())) {
				forwardToHost(net, packet.getSegment().getDstHostID(), packet);
			}
			/* ================================================ */
			/* ========== Packet to next switch =============== */
			/* ================================================ */
			else if (hasFlowEntry(packet.getSegment().getFlowID())) {
				if (packet.getSegment().getType() == Keywords.Segments.Types.UncontrolledFIN) {
					forwardToController(net, packet);
				} else {
					forwardToSwitch(net, packet.getSegment().getFlowID(), packet);
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
