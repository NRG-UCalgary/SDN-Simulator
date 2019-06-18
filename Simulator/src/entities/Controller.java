package entities;

import system.*;
import utilities.Debugger;
import utilities.Keywords;
import routings.*;
import events.*;
import controllers.ControlDatabase;
import java.util.HashMap;

public abstract class Controller extends Entity {

	// Modules
	protected Router router;
	protected ControlDatabase database;

	protected Network currentNetwork;
	protected Segment currentSegment;

	public Controller(int ID, Network net, int routingAlgorithm) {
		super(ID);
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
	public abstract Network recvSegment(Network net, int switchID, Segment segment);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */

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
		database.BtlBWs.put(currentSegment.getFlowID(), minBW);
		database.RTTs.put(currentSegment.getFlowID(), rtt);

	}

	/* =========================================== */
	/* ========== Switch Communication =========== */
	/* =========================================== */

	protected void sendSegmentToSwitch(int switchID, Segment segment) {
		double nextTime = currentNetwork.getCurrentTime() + getControlLinkDelay(switchID, segment.getSize());
		currentNetwork.eventList.addEvent(new ArrivalToSwitch(nextTime, switchID, segment, null));
	}

	protected void sendControlMessageToSwitch(int switchID, CtrlMessage controlMessage) {
		double nextTime = currentNetwork.getCurrentTime() + this.getControlLinkDelay(switchID, Keywords.CTRLSegSize);
		Event nextEvent = new ArrivalToSwitch(nextTime, switchID, null, controlMessage);
		currentNetwork.eventList.addEvent(nextEvent);
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

	protected double getControlLinkDelay(int switchID, int segmentSize) {
		return currentNetwork.switches.get(switchID).controlLink.getTotalDelay(segmentSize);
	}

	protected void sendSegmentToAccessSwitches(Segment segment) {
		for (int switchID : database.AccessSwitchIDs) {
			sendSegmentToSwitch(switchID, segment);
		}
	}

	protected void sendControlMessageToAccessSwitches(HashMap<Integer, CtrlMessage> messages) {
		for (int switchID : database.AccessSwitchIDs) {
			sendControlMessageToSwitch(switchID, messages.get(switchID));
		}
	}

}
