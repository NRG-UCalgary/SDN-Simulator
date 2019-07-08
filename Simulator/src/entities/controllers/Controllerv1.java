package entities.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import entities.*;
import entities.buffers.*;
import system.*;
import system.utility.*;

public class Controllerv1 extends Controller {

	// Temorary
	public int currentSwitchID;

	/* Congestion Control Variables */
	public double alpha;
	public int previousBigRTT;
	public int bigRTT; // TODO With only one access Switch
	// TODO it should be changed to a map <AccessSwitchID, bigRTT> later
	public int previousSWnd;
	public int sWnd;
	public double interSegmentDelay;
	public int numberOfSendingCycles;
	public double interFlowDelayConstant; // The fixed delay between each flow swnd

	public Controllerv1(int ID, Network net, int routingAlgorithm, double alpha) {
		super(ID, net, routingAlgorithm);
		this.alpha = alpha;

	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Inherited methods (from Controller) --------------------------- */
	/* -------------------------------------------------------------------------- */
	public Network recvPacket(Network net, int switchID, Packet packet) {
		Segment segment = packet.segment;
		this.currentSwitchID = switchID;
		this.currentNetwork = net;
		this.currentSegment = segment;
		switch (segment.getType()) {
		case Keywords.SYN:
			Debugger.debug("SYN of flow: " + segment.getFlowID() + " arrived at: " + net.getCurrentTime());
			database.addFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			handleRouting(currentNetwork.switches.get(switchID), this.getAccessSwitch(currentSegment.getDstHostID()));
			break;
		case Keywords.FIN:
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			break;
		default:
			break;
		}
		handleCongestionControl();
		sendPacketToSwitch(switchID, new Packet(currentSegment, null));
		return currentNetwork;
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	/* =========================================== */
	/* ========== Congestion Control ============= */
	/* =========================================== */
	private void handleCongestionControl() {
		updateBigRTT();
		// bigRTT = 200;
		Debugger.debug("Controller BigRtt: " + bigRTT);
		updateInterSegmentDelay();
		Debugger.debug("Controller interSegmentDelay: " + interSegmentDelay);
		updateInterFlowDelayConstant();
		Debugger.debug("This is the interFlowDelay: " + interFlowDelayConstant);
		updateSWnd();
		Debugger.debug("Controller SWnd: " + sWnd);
		updateNumberOfSendingCycles();
		notifyHosts();
		// if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 1) {
		notifyAccessSwitches(prepareMessage());
		// }

	}

	private void updateBigRTT() {
		previousBigRTT = bigRTT;
		// TODO the bigRTT for the corresponding access Switch must be updated
		// NOTE: We start with only one accessSwitch so for now this implementation
		// works
		ArrayList<Integer> rtts = new ArrayList<Integer>();
		for (double rtt : database.rttOfFlowID.values()) {
			// TODO We consider RTT in millisecond and we use ceiling
			rtts.add((int) Math.ceil(rtt));
		}
		bigRTT = Mathematics.lcm(rtts);

	}

	private void updateInterSegmentDelay() {
		interSegmentDelay = Keywords.DataSegSize / (double) database.btlBwOfFlowID.get(currentSegment.getFlowID());

	}

	private void updateInterFlowDelayConstant() {
		// TODO this is for one access Switch only
		// TODO must be updated accordingly later
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 1) {
			interFlowDelayConstant = bigRTT / database.getNumberOfFlowsForAccessSwitch(currentSwitchID);
		} else {
			interFlowDelayConstant = 0;
		}
	}

	private void updateSWnd() {
		// note that this is only for the single bottleneck scenario
		previousSWnd = sWnd;
		this.sWnd = (int) Math.floor(alpha * (bigRTT * database.btlBwOfFlowID.get(currentSegment.getFlowID())
				/ (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) * Keywords.DataSegSize)));
		if (previousSWnd == 0) {
			previousSWnd = sWnd;
		}
	}

	private void updateNumberOfSendingCycles() {
		// TODO what should the number of sending cycles be?
		numberOfSendingCycles = 100;
	}

	private HashMap<Integer, CtrlMessage> prepareMessage() {
		HashMap<Integer, CtrlMessage> messages = new HashMap<Integer, CtrlMessage>();
		HashMap<Integer, BufferToken> preparedTokens = new HashMap<Integer, BufferToken>();

		// a CtrlMessage for each accessSwitches in the network
		for (int accessSwitchID : database.getAccessSwitchIDsSet()) {
			CtrlMessage singleMessage = new CtrlMessage(Keywords.BufferTokenUpdate);
			BufferToken ccTokenForEachBuffer = new BufferToken();
			int i = 0; // The flow ID index
			double accessLinkRttOfFlowZero = 0; // d_i
			double interFlowDelay = 0;
			double initialCycleDelay = 0;
			double steadyCycleDelay = 0;
			for (int hostID : database.getHostIDsSetForAccessSwitch(accessSwitchID)) {
				if (i == 0) { // flow_0
					interFlowDelay = 0;
					accessLinkRttOfFlowZero = currentNetwork.hosts.get(hostID).getAccessLinkRtt();
				} else { // flow_i and i>0
					interFlowDelay = i * interFlowDelayConstant
							+ (accessLinkRttOfFlowZero - currentNetwork.hosts.get(hostID).getAccessLinkRtt());
				}
				initialCycleDelay = previousBigRTT + interFlowDelay;
				Debugger.debugToConsole("This is initialDelay: " + initialCycleDelay);
				steadyCycleDelay = bigRTT - database.getRttForAccessSwitchIDAndHostID(accessSwitchID, hostID);
				Debugger.debugToConsole("SteadyCycleDelay: " + steadyCycleDelay);
				ccTokenForEachBuffer.activate(true, interSegmentDelay, initialCycleDelay, previousSWnd,
						steadyCycleDelay, sWnd);

				preparedTokens.put(hostID, ccTokenForEachBuffer);
				i++;
			}
			singleMessage.ccTokenOfHostID = preparedTokens;
			messages.put(accessSwitchID, singleMessage);
		}
		return messages;
	}

	private void notifyAccessSwitches(HashMap<Integer, CtrlMessage> messages) {
		// TODO this is for one accessSwitch assumption
		// TODO must be updated for more than access switches
		sendControlMessageToAccessSwitches(messages);
	}

	private void notifyHosts() {
		// TODO this is for one accessSwitch assumption
		// TODO must be updated for more than access switches
		Segment segmentToHosts = new Segment(Keywords.ControllerFLowID, Keywords.CTRL, Keywords.CTRLSeqNum,
				Keywords.CTRLSegSize, this.getID(), Keywords.BroadcastDestination);
		segmentToHosts.bigRTT_ = this.bigRTT;
		segmentToHosts.sWnd_ = this.sWnd;
		segmentToHosts.interSegmentDelay_ = this.interSegmentDelay;
		sendPacketToSwitch(this.currentSwitchID, new Packet(segmentToHosts, null));
	}

}
