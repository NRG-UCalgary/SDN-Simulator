package simulator.entities.switches;

import simulator.Network;
import simulator.Simulator;
import simulator.entities.CtrlMessage;
import simulator.entities.Link;
import simulator.entities.Packet;
import simulator.entities.SDNSwitch;
import utility.Debugger;
import utility.Keywords;
import utility.Mathematics;

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
	public void recvPacket(Network net, Packet packet) {
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
				forwardToHost(net, packet);
			}
			/* ================================================ */
			/* ========== Packet to next switch =============== */
			/* ================================================ */
			else if (hasFlowEntry(packet.segment.getFlowID())) {
				if (packet.segment.getType() == Keywords.Segments.Types.UncontrolledFIN) {
					/* Uncontrolled FIN goes to controller */
					forwardToController(net, packet);
				} else {
					forwardToSwitch(net, getNextSwitchID(packet.segment.getFlowID()), packet);
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

	public void releasePacket(Network net, int dstNodeID, Packet packet) {
		float nextTime, linkTotalDelay;
		/* ================================================ */
		/* ========== Next node is a Host ================= */
		/* ================================================ */
		if (isConnectedToHost(packet.segment.getDstHostID())) {
			linkTotalDelay = accessLinks.get(dstNodeID).transmitPacket(net.getCurrentTime(), packet);
			nextTime = Mathematics.addFloat(net.getCurrentTime(), linkTotalDelay);
			net.eventList.addArrivalToHost(nextTime, dstNodeID, packet);
		}
		/* ================================================ */
		/* ========== Next node is a Switch =============== */
		/* ================================================ */
		else if (hasFlowEntry(packet.segment.getFlowID())) {
			linkTotalDelay = networkLinks.get(dstNodeID).transmitPacket(net.getCurrentTime(), packet);
			nextTime = Mathematics.addFloat(net.getCurrentTime(), linkTotalDelay);
			net.eventList.addArrivalToSwitch(nextTime, dstNodeID, packet);
		}
		/* ================================================ */
		/* ========== Next node is a Controller =========== */
		/* ================================================ */
		else {
			nextTime = Mathematics.addFloat(net.getCurrentTime(),
					controlLink.transmitPacket(net.getCurrentTime(), packet));
			net.eventList.addArrivalToController(nextTime, this.ID, packet);
		}
	}

}
