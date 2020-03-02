package nrg.sdnsimulator.core.entity.network.host;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.Host;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.event.ArrivalToNode;

public class DefaultHost extends Host {

	public DefaultHost(int ID) {
		super(ID);
	}

	public void initialize(Network net) {
		transportAgent.sendFirst(net);
		net.getEventList().addEvent(new ArrivalToNode(transportAgent.getFlow().getArrivalTime(), -1,
				this.ID, new Packet(transportAgent.getSegmentsToSend().get(0), null)));
		transportAgent.getSegmentsToSend().clear();
	}

	public void recvPacket(Network net, int srcNodeID, Packet packet) {
		transportAgent.recvSegment(net, packet.getSegment());
		sendSegments(net);
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
		// The host must pass the timeout notification to the agent
		transportAgent.timeout(net, timerID);
		sendSegments(net);

	}

}
