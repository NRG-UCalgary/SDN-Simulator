package ca.ucalgary.cpsc.nrg.sdnsimulator.core.event;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Simulator;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Packet;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Keywords;

public class ArrivalToNode extends PacketEvent {

	int nodeID;
	int srcNodeID;

	public ArrivalToNode(float eventTime, int srcNodeID, int nodeID, Packet packet) {
		super(eventTime, packet);
		this.nodeID = nodeID;
		this.srcNodeID = srcNodeID;
	}

	@Override
	public void execute(Network net) {
		net.updateTime(eventTime);
		short nodeType = Simulator.getNodeType(nodeID);
		switch (nodeType) {
		case Keywords.Entities.Nodes.Types.Controller:
			net.controllers.get(nodeID).recvPacket(net, srcNodeID, packet);
			break;
		case Keywords.Entities.Nodes.Types.SDNSwitch:
			net.switches.get(nodeID).recvPacket(net, srcNodeID, packet);
			break;
		case Keywords.Entities.Nodes.Types.Host:
			net.hosts.get(nodeID).recvPacket(net, srcNodeID, packet);
			break;
		default:
			break;
		}
	}

}
