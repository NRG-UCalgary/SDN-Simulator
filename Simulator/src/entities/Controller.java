package entities;

import system.*;
import routings.*;

import java.util.ArrayList;
import java.util.HashMap;

import events.ArrivalToSwitch;
import events.FlowPathSetup;

public abstract class Controller extends Entity {

	// Modules
	protected Router router;
	// This Data Structure holds the links to accessSwitches - <SwitchID, Link>
	protected ArrayList<Integer> accessSwitches;
	// This data structure holds the BottleneckBW of each flow - <FlowID, LinkBW>
	protected HashMap<Integer, Integer> BtlBWs;
	// This data structure holds the BottleneckBW of each flow - <FlowID, rtt>
	protected HashMap<Integer, Double> RTTs;
	protected Network currentNetwork;
	protected Segment currentSegment;
	protected int flowCount;
	protected final int FirstFlowID;

	public Controller(int ID, Network net, int routingAlgorithm) {
		super(ID);
		FirstFlowID = 0;
		currentNetwork = new Network();
		currentSegment = new Segment(0, 0, 0, 0, 0, 0);
		flowCount = 0;
		BtlBWs = new HashMap<Integer, Integer>();
		RTTs = new HashMap<Integer, Double>();
		accessSwitches = new ArrayList<Integer>();
		for (SDNSwitch sdnSwitch : net.switches.values()) {
			if (sdnSwitch.isAccessSwitch()) {
				accessSwitches.add(sdnSwitch.getID());
			}
		}
		switch (routingAlgorithm) {
		case Keywords.Dijkstra:
			router = new Dijkstra(net.switches);
			break;
		case Keywords.Routing1:
			break;
		default:
			break;
		}
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract Network recvSegment(Network net, int switchID, Segment segment);

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	/* ########## Protected ############################## */
	protected SDNSwitch getAccessSwitch(int hostID) {
		return currentNetwork.switches.get(currentNetwork.hosts.get(hostID).accessSwitchID);
	}

	protected int getBtlBW(int flowID) {
		return this.getBtlBW(flowID);
	}

	protected void handleRouting(SDNSwitch srcAccessSwitch, SDNSwitch dstAccessSwitch) {
		/* Controller runs the router module to find the path for the flow */
		HashMap<SDNSwitch, Link> result = router.run(srcAccessSwitch, dstAccessSwitch);

		/* Controller updates flow tables of all switches in the flow path */
		for (SDNSwitch networkSwitch : result.keySet()) {
			sendFlowSetupMessage(networkSwitch.getID(), result.get(networkSwitch));
		}

		/* Finding the bottleneck link and RTT for the flow */
		int minBW = Integer.MAX_VALUE;
		double rtt = 0;
		for (Link link : result.values()) {
			rtt += 2 * link.getTotalDelay(currentSegment.getSize());
			if (link.getBandwidth() <= minBW) {
				minBW = link.getBandwidth();
			}
		}
		this.BtlBWs.put(currentSegment.getFlowID(), minBW);
		this.RTTs.put(currentSegment.getFlowID(), rtt);
	}

	/* ########## Public ################################# */
	public double getControlLinkDelay(int switchID, int segmentSize) {
		return currentNetwork.switches.get(switchID).controlLink.getTotalDelay(segmentSize);
	}

	/* ================================ */
	/* Methods for switch communication */
	/* ================================ */
	protected void sendSegmentToAccessSwitches(Segment segment) {
		for (int switchID : accessSwitches) {
			sendSegmentToSwitch(switchID, segment);
		}
	}

	protected void sendSegmentToSwitch(int switchID, Segment segment) {
		double nextTime = currentNetwork.getCurrentTime() + getControlLinkDelay(switchID, segment.getSize());
		currentNetwork.eventList.addEvent(new ArrivalToSwitch(nextTime, switchID, segment, null));
	}

	protected void sendFlowSetupMessage(int switchID, Link egressLink) {
		SDNSwitch networkSwitch = currentNetwork.switches.get(switchID);
		double nextTime = currentNetwork.getCurrentTime()
				+ this.getControlLinkDelay(networkSwitch.getID(), Keywords.CTRLSegSize);
		Event nextEvent = new FlowPathSetup(nextTime, networkSwitch.getID(), currentSegment.getFlowID(),
				egressLink.getDstID());
		currentNetwork.eventList.addEvent(nextEvent);
	}

}
