package simulator.entities;

import java.util.HashMap;

import simulator.Network;
import simulator.entities.controllers.ControlDatabase;
import simulator.entities.controllers.routers.Dijkstra;
import simulator.entities.controllers.routers.Router;
import utility.Keywords;
import utility.Mathematics;

public abstract class Controller extends Entity {

	/** ========================================== **/

	// ControlLinks
	protected HashMap<Integer, Link> controlLinks; // <SwitchID, Link>
	protected Segment currentSegment;
	protected ControlDatabase database;
	public float numberOfAdmittedFlows;

	/** ========== Statistical Counters ========== **/
	public float numberOfDistinctSYNs;
	public float numberOfRejectedFlows;

	// Modules
	protected Router router;

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
		currentSegment = new Segment(-1, -1, -1, -1, -1, -1);

	}

	public void connectSwitch(int switchID, Link controlLink) {
		this.controlLinks.put(switchID, controlLink);
	}

	
	/* =========================================== */
	/* ========== Utility ======================== */
	/* =========================================== */
	protected int getAccessSwitchID(Network net, int hostID) {
		return net.hosts.get(hostID).accessSwitchID;
	}

	public int getBottleneckLinkID() {
		return database.bottleneckLinkID;
	}

	// TODO must be updated
	protected float getControlLinkTotalDelay(Network net, int switchID, int segmentSize) {
		return net.switches.get(switchID).controlLink.getTotalDelay(segmentSize);
	}

	protected void handleRouting(Network net, int srcAccessSwitchID, int dstAccessSwitchID) {
		/* Controller runs the router module to find the path for the flow */
		HashMap<Integer, Link> result = router.run(srcAccessSwitchID, dstAccessSwitchID);

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

		rtt += net.hosts.get(currentSegment.getSrcHostID()).getAccessLinkRtt();
		rtt += net.hosts.get(currentSegment.getDstHostID()).getAccessLinkRtt();
		database.btlBwOfFlowID.put(currentSegment.getFlowID(), minBW);
		database.rttOfFlowID.put(currentSegment.getFlowID(), rtt);

	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public abstract void recvPacket(Network net, int switchID, Packet packet);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public void releasePacket(Network net, int dstSwitchID, Packet packet) {
		float nextTime = Mathematics.addFloat(net.getCurrentTime(),
				controlLinks.get(dstSwitchID).transmitPacket(net.getCurrentTime(), packet));
		net.eventList.addArrivalToSwitch(nextTime, dstSwitchID, packet);
	}

	protected void sendControlMessageToAccessSwitches(Network net, HashMap<Integer, CtrlMessage> messages) {
		for (int switchID : database.getAccessSwitchIDsSet()) {
			sendPacketToSwitch(net, switchID, new Packet(null, messages.get(switchID)));
		}
	}

	/* =========================================== */
	/* ========== Switch Communication =========== */
	/* =========================================== */
	protected void sendPacketToSwitch(Network net, int switchID, Packet packet) {
		float bufferTime = controlLinks.get(switchID).bufferPacket(net.getCurrentTime(), packet);
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
		net.eventList.addDepartureFromController(nextTime, this.ID, switchID, packet);
	}

	protected void sendSegmentToAccessSwitches(Network net, Segment segment) {
		for (int switchID : database.getAccessSwitchIDsSet()) {
			sendPacketToSwitch(net, switchID, new Packet(segment, null));
		}
	}

	public void setBottleneckLinkID(int id) {
		database.bottleneckLinkID = id;
	}

}
