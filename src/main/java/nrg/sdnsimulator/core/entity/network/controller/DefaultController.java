package nrg.sdnsimulator.core.entity.network.controller;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.Controller;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Keywords;

public class DefaultController extends Controller {
	public DefaultController(int ID, short routingAlgorithm) {
		super(ID, routingAlgorithm);
	}

	public void recvPacket(Network net, int switchID, Packet packet) {
		Segment segment = packet.getSegment();
		this.recvdSegment = segment;
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			database.addFlow(switchID, segment.getSrcHostID(), segment.getFlowID());

			handleRouting(net, switchID, getAccessSwitchID(net, recvdSegment.getDstHostID()));
			sendPacketToSwitch(net, switchID, new Packet(recvdSegment, null));
			break;
		case Keywords.Segments.Types.FIN:
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			sendPacketToSwitch(net, switchID, new Packet(recvdSegment, null));
			break;
		default:
			break;
		}
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
		// The controller does not need timer for now

	}

}
