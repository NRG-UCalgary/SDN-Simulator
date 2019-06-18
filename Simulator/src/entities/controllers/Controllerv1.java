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
	public int bigRTT; // TODO With only one access Switch
	// TODO it should be changed to a map <AccessSwitchID, bigRTT> later
	public int previousSWnd;
	public int sWnd;
	public int numberOfSendingCycles;
	public double interFlowDelay; // The fixed delay between each flow swnd

	public Controllerv1(int ID, Network net, int routingAlgorithm, double alpha) {
		super(ID, net, routingAlgorithm);
		this.alpha = alpha;

	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Inherited methods (from Controller) --------------------------- */
	/* -------------------------------------------------------------------------- */
	public Network recvSegment(Network net, int switchID, Segment segment) {
		this.currentSwitchID = switchID;
		this.currentNetwork = net;
		this.currentSegment = segment;
		switch (segment.getType()) {
		case Keywords.SYN:
			database.Flows.put(segment.getSrcHostID(), segment.getFlowID());
			handleRouting(currentNetwork.switches.get(switchID), this.getAccessSwitch(currentSegment.getDstHostID()));
			break;
		case Keywords.FIN:
			// TODO we might want to remove the flow entries for the completed flow
			database.Flows.remove(segment.getSrcHostID());
			break;
		default:
			break;
		}
		handleCongestionControl();
		sendSegmentToSwitch(switchID, currentSegment);
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
		Debugger.debug("Controller BigRtt: " + bigRTT);
		updateInterFlowDelay();
		updateSWnd();
		Debugger.debug("Controller SWnd: " + sWnd);
		updateNumberOfSendingCycles();
		notifyHosts();
		notifyAccessSwitch(prepareMessage());

	}

	private void updateBigRTT() {
		// TODO the bigRTT for the corresponding access Switch must be updated
		// NOTE: We start with only one accessSwitch so for now this implementation
		// works
		ArrayList<Integer> rtts = new ArrayList<Integer>();
		for (double rtt : database.RTTs.values()) {
			// TODO We consider RTT in millisecond and we use ceiling
			rtts.add((int) Math.ceil(rtt));
		}
		bigRTT = Mathematics.lcm(rtts);
	}

	private void updateInterFlowDelay() {
		// TODO this is for one access Switch only
		// TODO must be updated accordingly later
		interFlowDelay = bigRTT / database.Flows.size();
	}

	private void updateSWnd() {
		// note that this is only for the single bottleneck scenario
		previousSWnd = sWnd;
		this.sWnd = (int) Math.floor(alpha * (bigRTT * database.BtlBWs.get(currentSegment.getFlowID())
				/ (database.Flows.size() * Keywords.DataSegSize)));
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
		CtrlMessage singleMessage = new CtrlMessage();
		HashMap<Integer, ArrayList<BufferToken>> preparedTokens = new HashMap<Integer, ArrayList<BufferToken>>();
		ArrayList<BufferToken> eachAccessBufferTokens;
		BufferToken token;
		// a CtrlMessage for each accessSwitches in the network
		for (int accessSwitchID : database.AccessSwitchIDs) {
			// The flow ID index
			int i = 1;
			eachAccessBufferTokens = new ArrayList<BufferToken>();
			for (int hostID : currentNetwork.switches.get(accessSwitchID).accessLinks.keySet()) {
				for (int j = 0; j <= numberOfSendingCycles; j++) {
					double t = (i * interFlowDelay) - currentNetwork.hosts.get(hostID).getAccessLinkRTT() + j * bigRTT;
					token = new BufferToken(t, previousSWnd);
					eachAccessBufferTokens.add(token);
				}
				preparedTokens.put(hostID, eachAccessBufferTokens);
				i++;
			}
			singleMessage.tokens = preparedTokens;

			messages.put(accessSwitchID, singleMessage);
		}
		return messages;
	}

	private void notifyAccessSwitch(HashMap<Integer, CtrlMessage> messages) {
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
		// sendSegmentToAccessSwitches(segmentToHosts);
		sendSegmentToSwitch(this.currentSwitchID, segmentToHosts);
	}

}
