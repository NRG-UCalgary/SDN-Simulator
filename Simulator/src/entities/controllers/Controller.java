package entities.controllers;

import java.util.HashMap;
import system.*;
import system.events.*;
import system.utility.*;
import entities.*;
import entities.controllers.routers.*;
import entities.switches.*;

public abstract class Controller extends Entity {

	// ControlLinks
	protected HashMap<Integer, Link> controlLinks; // <SwitchID, Link>

	// Modules
	protected Router router;
	protected ControlDatabase database;

	protected Network currentNetwork;
	protected Segment currentSegment;

	public Controller(int ID, Network net, int routingAlgorithm) {
		super(ID);
		controlLinks = new HashMap<Integer, Link>();
		database = new ControlDatabase(net);
		switch (routingAlgorithm) {
		case Keywords.Dijkstra:
			router = new Dijkstra(net.switches);
			break;
		case Keywords.Routing1:
			break;
		default:
			break;
		}
		currentNetwork = net;
		currentSegment = new Segment(-1, -1, -1, -1, -1, -1);

	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public abstract Network recvPacket(Network net, int switchID, Packet packet);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public Network releasePacket(Network net, int dstSwitchID, Packet packet) {
		this.controlLinks.get(dstSwitchID).buffer.deQueue();
		double nextTime = net.getCurrentTime() + controlLinks.get(dstSwitchID).buffer.getWaitTime(
				currentNetwork.getCurrentTime(), controlLinks.get(dstSwitchID).getTransmissionDelay(packet.getSize()));
		net.eventList.addEvent(new ArrivalToSwitch(nextTime, dstSwitchID, packet));
		return net;
	}

	protected void handleRouting(SDNSwitch srcAccessSwitch, SDNSwitch dstAccessSwitch) {
		/* Controller runs the router module to find the path for the flow */
		HashMap<Integer, Link> result = router.run(srcAccessSwitch.getID(), dstAccessSwitch.getID());

		/* Updating flow path database */
		/* Controller updates flow tables of all switches in the flow path */
		for (int switchID : result.keySet()) {
			sendFlowSetupMessage(switchID, result.get(switchID));
		}
		// TODO The ACK flow path must be set up too

		/* Finding the bottleneck link and RTT for the flow */
		int minBW = Integer.MAX_VALUE;
		double rtt = 0;
		for (Link link : result.values()) {
			rtt += link.getTotalDelay(Keywords.ACKSegSize) + link.getTotalDelay(Keywords.DataSegSize);
			if (link.getBandwidth() <= minBW) {
				minBW = link.getBandwidth();
			}
		}
		rtt += currentNetwork.hosts.get(currentSegment.getSrcHostID()).getAccessLinkRTT();
		rtt += currentNetwork.hosts.get(currentSegment.getDstHostID()).getAccessLinkRTT();
		database.btlBwOfFlowID.put(currentSegment.getFlowID(), minBW);
		database.rttOfFlowID.put(currentSegment.getFlowID(), rtt);

	}

	/* =========================================== */
	/* ========== Switch Communication =========== */
	/* =========================================== */
	protected void sendPacketToSwitch(int switchID, Packet packet) {
		double nextTime = currentNetwork.getCurrentTime() + controlLinks.get(switchID).buffer.getWaitTime(
				currentNetwork.getCurrentTime(), controlLinks.get(switchID).getTransmissionDelay(packet.getSize()));
		currentNetwork.eventList.addEvent(new DepartureFromController(nextTime, this.getID(), switchID, packet));
	}

	protected void sendFlowSetupMessage(int switchID, Link egressLink) {
		SDNSwitch networkSwitch = currentNetwork.switches.get(switchID);
		double nextTime = currentNetwork.getCurrentTime()
				+ this.getControlLinkDelay(networkSwitch.getID(), Keywords.CTRLSegSize);
		Event nextEvent = new FlowPathSetup(nextTime, networkSwitch.getID(), currentSegment.getFlowID(),
				egressLink.getDstID());
		currentNetwork.eventList.addEvent(nextEvent);
	}

	/* =========================================== */
	/* ========== Utility ======================== */
	/* =========================================== */

	protected SDNSwitch getAccessSwitch(int hostID) {
		return currentNetwork.switches.get(currentNetwork.hosts.get(hostID).accessSwitchID);
	}

	// TODO must be updated
	protected double getControlLinkDelay(int switchID, int segmentSize) {
		return currentNetwork.switches.get(switchID).controlLink.getTotalDelay(segmentSize);
	}

	protected void sendSegmentToAccessSwitches(Segment segment) {
		for (int switchID : database.getAccessSwitchIDsSet()) {
			sendPacketToSwitch(switchID, new Packet(segment, null));
		}
	}

	protected void sendControlMessageToAccessSwitches(HashMap<Integer, CtrlMessage> messages) {
		for (int switchID : database.getAccessSwitchIDsSet()) {
			sendPacketToSwitch(switchID, new Packet(null, messages.get(switchID)));
		}
	}

	public void connectSwitch(int switchID, Link controlLink) {
		this.controlLinks.put(switchID, controlLink);
	}

}
