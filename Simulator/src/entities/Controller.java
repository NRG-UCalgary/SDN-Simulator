package entities;

import system.*;
import utilities.Logger;
import routings.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
	private Logger log = new Logger();

	private final int FirstFlowID = 0;

	// The global view of the network is stored in a Network Object in controller
	Network currNetwork;

	// The received segment is defined as a global variable to be used internal
	// methods
	Segment currSegment;

	// The aggression control variable
	private double alpha;

	// The bottleneck bandwidth for the current flow
	private int btlBw;
	// This is the RTTi based on the ACK segment size
	private double currSYNRtt;
	// The big rtt that accommodates all rtts of the flows
	private int bigRTT;
	// The remainder of the bigRTT based on the current time
	private double bigRTTRemainder;

	// The variable that keeps the start time of the sending cycle
	private double sendingCycleStartTime;

	// This shows the wait_time until the next cycle (the time that first flow's ACK
	// will be released
	private double waitTime;
	// Optimum sending window
	private int sWnd;

	// The number of flows
	private int flowCount;
	// Controller has a database of each flow (id) and its rtt (data segment size)
	HashMap<Integer, Double> RTTs;
	// Controller has a database of each flow (id) and its bottleneck bandwidth
	HashMap<Integer, Integer> BtlBws;

	// Controller has a database of each flow (id) and its AccessLinkDelay
	HashMap<Integer, Double> AccessDelays;

	// Controller has a database of flows and their paths inside network data
	// structure
	Map<Integer, Map<SDNSwitch, Link>> Paths;

	// TODO router type must be set by the client through the simulator
	public Dijkstra router;

	/*------------  Constant Values for different types of delays -----------*/
	public double CONTROLLER_RTT_DELAY = 1.0;
	public double CONTROLLER_PROCESS_DELAY = 1.0;
	/*-----------------------------------------------------------------------*/

	public Controller(Network net, String routing_type) {

		/* Initialization */
		currNetwork = new Network();
		currSegment = new Segment(null, null, 0, 0, null, null);
		bigRTT = 0;
		bigRTTRemainder = 0;
		sendingCycleStartTime = 0;
		waitTime = 0;
		sWnd = 0;
		flowCount = 0;
		btlBw = 0;
		alpha = 1; // TODO this value must have a default mechanism or user defined value
		RTTs = new HashMap<Integer, Double>();
		BtlBws = new HashMap<Integer, Integer>();
		AccessDelays = new HashMap<Integer, Double>();
		Paths = new HashMap<Integer, Map<SDNSwitch, Link>>();

		switch (routing_type) {
		case "Dijkstra":
			router = new Dijkstra(net.switches);
			break;
		case "Something Else":
			break;
		default:
			break;
		}
	}

	/** Called in Class::Event.run() **/
	/*
	 * RULE: net is only used at the start of the method to update the currNetwork
	 */
	/*
	 * RULE: The internal methods work with currNetwork and newFlow method will
	 * return currNetwork in the end
	 */
	/* Objective::Updating flow_table of the Node */
	/*
	 * This method is called by Event object execution method, upon arrival of SYN
	 * segment to an access switch
	 */
	/*
	 * One similar method to this one must be developed that handles FIN segments
	 */
	public Network newFlowArrival(Network net, SDNSwitch accessSwitch, Segment segment) {
		log.entranceToMethod("Controller", "newFlow");
		this.currNetwork = net;
		this.currSegment = segment;

		/** Handle Routing **/
		handleRouting(accessSwitch);

		/** Update databases and states based on the found path **/
		updateDatabases();

		/** Handle Congestion Control **/
		handleCongControl();

		return currNetwork;
	}

	public Network flowDeparture(Network net, SDNSwitch accessSwitch, Segment segment) {
		this.currNetwork = net;
		this.currSegment = segment;

		// TODO Do we need to remove flow path from the switches flow tables?

		// When a FIN packet is arrived to an access switch, the controller must wait
		// for the end of the bigRTT to start the new sending cycle
		// This means that the controller must calculate the new cycle start time (or
		// wait time relative to the current time) only based on the bigRTT remainder

		// So the controller should:
		// calculate the wait time
		// calculate the new sWnd size
		// send the control segment with the sWnd size to the access switches (so the
		// switches will send it to the hosts)
		// calculate pacing delays and update the buffers of the access switch
		// accordingly

		/** Handle Congestion Control **/

		return currNetwork;
	}

	/** ++++++++++++++++++++++++++++++++++++++++++++++++++++ **/
	/** The application of controller represented as methods **/
	/** ++++++++++++++++++++++++++++++++++++++++++++++++++++ **/
	private void handleRouting(SDNSwitch accessSwitch) {

		/* Controller runs the router module to find the path for the flow */
		Map<SDNSwitch, Link> result = router.run(accessSwitch, currSegment.getDestination().accessSwitch);

		/* Controller updates flow tables of all switches in the flow path */
		for (SDNSwitch n : result.keySet()) {
			currNetwork.switches.get(n.getLabel()).updateFlowTable(currSegment.getFlowLabel(), result.get(n));
		}
		Paths.put(currSegment.getFlowID(), result);

		// TODO the flow tables must be updated for the ACK stream too

	}

	private void updateDatabases() {

		/* Calculate the remainder of bigRTT */
		this.bigRTTRemainder = currNetwork.time - sendingCycleStartTime;

		/* Calculate the databases of the controller based on the found path */
		// The access delay of the new flow
		double accessDelay = currSegment.getSource().getAccessLinkDelay(Keywords.DataSegSize);
		// The Data segment round-trip time based on source and destination access links
		double rttSum = 2 * (accessDelay + currSegment.getDestination().getAccessLinkDelay(Keywords.DataSegSize));
		// Used to find the bottleneck bandwidth along the flow path
		int minBw = Integer.MAX_VALUE;
		// The SYN segment round-trip time based on destination access link
		this.currSYNRtt = 2 * (currSegment.getDestination().getAccessLinkDelay(Keywords.SYNSegSize));
		// Loop over the flow path for calculate full round-trip times
		for (Link l : Paths.get(currSegment.getFlowID()).values()) {
			rttSum += 2 * (l.getTransmissionDelay(Keywords.DataSegSize) + l.getPropagationDelay());
			this.currSYNRtt += 2 * (l.getTransmissionDelay(Keywords.SYNSegSize) + l.getPropagationDelay());
			if (l.getBandwidth() <= minBw) {
				minBw = l.getBandwidth();
			}
		}
		// Update databases with calculated values
		this.RTTs.put(currSegment.getFlowID(), rttSum);
		this.BtlBws.put(currSegment.getFlowID(), minBw);
		this.AccessDelays.put(currSegment.getFlowID(), accessDelay);
	}

	private void handleCongControl() {

		/** Handling the central settings **/
		// waitTime must be calculated
		this.waitTime = Max(currSYNRtt, bigRTTRemainder);
		// TODO sending delays must be calculated and buffers must be updated
		// accordingly
		updateBuffers();
		// TODO Control message(s) should be sent to the access switch(s) so the new
		// sWnd size be advertised to the senders
		sendControlSegment();

		// TODO we do not need to keep track of the bigRTT (no timer is needed).
		// Instead, the controller should set the start token times to current_time +
		// max(previous bigRTT, SYNACK_rtt_to_switch)
		// the timer for next sending cycle must be updated (the start time of the
		// next cycle must be preserved)
		this.sendingCycleStartTime = currNetwork.time + waitTime + AccessDelays.get(FirstFlowID);

		/** Handling the end host settings **/
		/* Updating congestion control variables */
		this.flowCount++;
		this.sWnd = findSWndSize();

		/* Updating SYN segment congestion control headers */
		this.currSegment.flowID = this.flowCount;

	}

	/* ==================== */
	/* Second level methods */
	/* ==================== */
	private void updateBuffers() {
		// TODO at some point, findSendingDelay() must be called here
		// There is an important question that how does the token know when it is
		// generated?
		// One answer might be that the controller generates the token at the needed
		// time but this would lead to very granular time management which would be
		// infeasible
		// Another answer might be that the tokens have time attribute which can be read
		// by the switch (feasible?) - It should be mentioned here that the system time
		// of the switch and the controller must be synchronized

		// TODO

	}

	private double findSendingDelay(int flowID) {

		return (flowID * this.bigRTT / (double) flowCount)
				- currSegment.getSource().getAccessLinkDelay(Keywords.DataSegSize);
	}

	private void sendControlSegment() {
		// TODO some arrival events (for the control segment(s)) must be added to
		// currNetwork event list
		// This should happen before the controller changes the buffer mode in the
		// access switch
		//
	}

	private int findSWndSize() {

		// Update the bigRTT value
		findbigRTT();
		return (int) Math.floor(alpha * bigRTT * btlBw / flowCount);
	}

	private void findbigRTT() {
		ArrayList<Integer> rtts = new ArrayList<Integer>();
		for (double rtt : RTTs.values()) {
			rtts.add((int) rtt);
		}
		bigRTT = lcm(rtts);
	}
	/* ============================== */

	/* ============================== */
	/* Mathematical utility functions */
	/* ============================== */
	private double Max(double a, double b) {
		if (a >= b) {
			return a;
		} else {
			return b;
		}
	}

	private int lcm(ArrayList<Integer> vals) {

		return (int) ((int) multiply(vals) / (double) findGCD(vals));
	}

	private int gcd(int a, int b) {
		if (a == 0) {
			return b;
		} else {
			return gcd(b % a, a);
		}
	}

	private int findGCD(ArrayList<Integer> vals) {

		int result = vals.get(0);
		for (int val : vals) {
			result = gcd(val, result);
		}
		return 0;

	}

	private int multiply(ArrayList<Integer> vals) {
		int sum = 0;
		for (int value : vals) {
			sum += value;
		}
		return sum;
	}
	/* ============================== */
}
