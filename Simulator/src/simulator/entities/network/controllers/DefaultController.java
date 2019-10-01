package simulator.entities.network.controllers;

import simulator.Network;
import simulator.entities.network.Controller;
import simulator.entities.traffic.Packet;
import simulator.entities.traffic.Segment;
import utility.Debugger;
import utility.Keywords;

public class DefaultController extends Controller {
	public DefaultController(int ID, short routingAlgorithm) {
		super(ID, routingAlgorithm);
	}

	public void recvPacket(Network net, int switchID, Packet packet) {
		Debugger.debugToConsole("========== Arrival To Controller =============");
		Segment segment = packet.segment;
		this.recvdSegment = segment;
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			Debugger.debugToConsole(
					"SYN Segment of flow: " + segment.getFlowID() + " arrived at time: " + net.getCurrentTime());
			database.addFlow(switchID, segment.getSrcHostID(), segment.getFlowID());

			handleRouting(net, switchID, getAccessSwitchID(net, recvdSegment.getDstHostID()));
			sendPacketToSwitch(net, switchID, new Packet(recvdSegment, null));
			Debugger.debugToConsole("Sending SYN back to switch: " + switchID);
			break;
		case Keywords.Segments.Types.FIN:
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			sendPacketToSwitch(net, switchID, new Packet(recvdSegment, null));
			break;
		default:
			break;
		}
		Debugger.debugToConsole("==============================================");
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
		// The controller does not need timer for now
		
	}




}
