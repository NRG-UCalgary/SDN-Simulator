package simulator.entities.network.controllers;

import simulator.Network;
import simulator.entities.network.Controller;
import simulator.entities.traffic.Packet;
import simulator.entities.traffic.Segment;
import utility.Keywords;

public class Controllerv2 extends Controller {

	public Controllerv2(int ID, short routingAlgorithm) {
		super(ID, routingAlgorithm);
	}

	@Override
	public void recvPacket(Network net, int switchID, Packet packet) {
		Segment segment = packet.segment;
		database.accessSwitchID = switchID;
		this.currentSegment = segment;
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			database.flowIDOfHostID.put(currentSegment.getSrcHostID(), currentSegment.getFlowID());
			handleRouting(net, switchID, getAccessSwitchID(net, currentSegment.getDstHostID()));

			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			currentSegment.changeType(Keywords.Segments.Types.FIN);
			break;
		default:
			break;
		}
		sendPacketToSwitch(net, switchID, new Packet(currentSegment, null));

	}

}
