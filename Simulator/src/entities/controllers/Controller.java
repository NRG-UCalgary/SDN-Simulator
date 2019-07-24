package entities.controllers;

import java.util.HashMap;

import system.*;
import system.utility.*;
import entities.*;
import entities.controllers.routers.*;
import entities.switches.*;

public abstract class Controller extends Entity {

	/** ========== Statistical Counters ========== **/
	public float numberOfDistinctSYNs;
	public float numberOfAdmittedFlows;
	public float numberOfRejectedFlows;
	/** ========================================== **/

	// ControlLinks
	protected HashMap<Integer, Link> controlLinks; // <SwitchID, Link>

	// Modules
	protected Router router;
	protected ControlDatabase database;

	protected Network currentNetwork;
	protected Segment currentSegment;

	public Controller(int ID, Network net, int routingAlgorithm) {
		super(ID);
		/** ========== Statistical Counters ========== **/
		numberOfDistinctSYNs = 0;
		numberOfAdmittedFlows = 0;
		numberOfRejectedFlows = 0;
		/** ========================================== **/

		controlLinks = new HashMap<Integer, Link>();
		database = new ControlDatabase(net);
		switch (routingAlgorithm) {
		case Keywords.RoutingAlgorithms.Dijkstra:
			router = new Dijkstra(net.switches);
			break;
		case Keywords.RoutingAlgorithms.Routing1:
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
	public abstract void recvPacket(Network net, int switchID, Packet packet);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public void releasePacket(Network net, int dstSwitchID, Packet packet) {
		float nextTime = net.getCurrentTime() + controlLinks.get(dstSwitchID).getTotalDelay(packet.getSize());
		net.eventList.addArrivalToSwitch(nextTime, dstSwitchID, packet);
		// net.eventList.addEvent(new ArrivalToSwitch(nextTime, dstSwitchID, packet));
		controlLinks.get(dstSwitchID).buffer.deQueue();
	}

	protected void handleRouting(Network net, SDNSwitch srcAccessSwitch, SDNSwitch dstAccessSwitch) {
		/* Controller runs the router module to find the path for the flow */
		HashMap<Integer, Link> result = router.run(srcAccessSwitch.getID(), dstAccessSwitch.getID());

		/* Updating flow path database */
		/* Controller updates flow tables of all switches in the flow path */
		for (int switchID : result.keySet()) {
			CtrlMessage flowSetupMessage = new CtrlMessage(Keywords.SDNMessages.Types.FlowSetUp);
			flowSetupMessage.prepareFlowSetUpMessage(currentSegment.getFlowID(), result.get(switchID));
			sendPacketToSwitch(net, switchID, new Packet(null, flowSetupMessage));
		}
		// TODO The ACK flow path must be set up too

		/* Finding the bottleneck link and RTT for the flow */
		float minBW = 0;
		float rtt = 0;
		for (Link link : result.values()) {
			rtt += link.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize)
					+ link.getTotalDelay(Keywords.Segments.Sizes.DataSegSize);
			if (link.getID() == database.bottleneckLinkID) {
				minBW = link.getBandwidth();
			}
		}

		rtt += currentNetwork.hosts.get(currentSegment.getSrcHostID()).getAccessLinkRtt();
		rtt += currentNetwork.hosts.get(currentSegment.getDstHostID()).getAccessLinkRtt();
		database.btlBwOfFlowID.put(currentSegment.getFlowID(), minBW);
		database.rttOfFlowID.put(currentSegment.getFlowID(), rtt);

	}

	/* =========================================== */
	/* ========== Switch Communication =========== */
	/* =========================================== */
	protected void sendPacketToSwitch(Network net, int switchID, Packet packet) {
		float nextTime = currentNetwork.getCurrentTime() + controlLinks.get(switchID).buffer.getBufferTime(
				currentNetwork.getCurrentTime(), controlLinks.get(switchID).getTransmissionDelay(packet.getSize()));
		net.eventList.addDepartureFromController(nextTime, this.ID, switchID, packet);
		// currentNetwork.eventList.addEvent(new DepartureFromController(nextTime,
		// this.ID, switchID, packet));
	}

	/* =========================================== */
	/* ========== Utility ======================== */
	/* =========================================== */

	protected SDNSwitch getAccessSwitch(int hostID) {
		return currentNetwork.switches.get(currentNetwork.hosts.get(hostID).accessSwitchID);
	}

	// TODO must be updated
	protected float getControlLinkTotalDelay(int switchID, int segmentSize) {
		return currentNetwork.switches.get(switchID).controlLink.getTotalDelay(segmentSize);
	}

	protected void sendSegmentToAccessSwitches(Network net, Segment segment) {
		for (int switchID : database.getAccessSwitchIDsSet()) {
			sendPacketToSwitch(net, switchID, new Packet(segment, null));
		}
	}

	protected void sendControlMessageToAccessSwitches(Network net, HashMap<Integer, CtrlMessage> messages) {
		for (int switchID : database.getAccessSwitchIDsSet()) {
			sendPacketToSwitch(net, switchID, new Packet(null, messages.get(switchID)));
		}
	}

	public void connectSwitch(int switchID, Link controlLink) {
		this.controlLinks.put(switchID, controlLink);
	}

	public int getBottleneckLinkID() {
		return database.bottleneckLinkID;
	}

	public void setBottleneckLinkID(int id) {
		database.bottleneckLinkID = id;
	}

}
