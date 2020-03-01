package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.host;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.Host;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Packet;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.event.ArrivalToNode;

public class DefaultHost extends Host {

	public DefaultHost(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Host) ---------- */
	/* --------------------------------------------------- */
	public void initialize(Network net) {
		transportAgent.sendFirst(net);
		net.eventList.addEvent(new ArrivalToNode(transportAgent.flow.arrivalTime, -1, this.ID,
				new Packet(transportAgent.segmentsToSend.get(0), null)));
		transportAgent.segmentsToSend.clear();
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public void recvPacket(Network net, int srcNodeID, Packet packet) {
		transportAgent.recvSegment(net, packet.segment);
		sendSegments(net);
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
		// The host must pass the timeout notification to the agent
		transportAgent.timeout(net, timerID);
		sendSegments(net);

	}

}
