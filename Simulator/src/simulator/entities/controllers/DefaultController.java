package simulator.entities.controllers;

import simulator.Network;
import simulator.entities.Controller;
import simulator.entities.Packet;
import simulator.entities.Segment;
import utility.Keywords;

public class DefaultController extends Controller {
	public DefaultController(int ID, Network net, int routingAlgorithm) {
		super(ID, net, routingAlgorithm);
	}

	public void recvPacket(Network net, int switchID, Packet packet) {
		Segment segment = packet.segment;
		this.currentSegment = segment;
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			database.addFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			handleRouting(net, switchID, getAccessSwitchID(net, currentSegment.getDstHostID()));
			sendPacketToSwitch(net, switchID, new Packet(currentSegment, null));
			break;
		case Keywords.Segments.Types.FIN:
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			sendPacketToSwitch(net, switchID, new Packet(currentSegment, null));
			// Send flow_entry_removal later
			break;
		default:
			break;
		}
	}
}
