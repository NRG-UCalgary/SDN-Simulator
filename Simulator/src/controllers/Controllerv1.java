package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import entities.*;
import events.*;
import system.*;
import utilities.*;

public class Controllerv1 extends Controller {

	/* Congestion Control variables */
	// The aggression control variable
	private double alpha;
	// The big rtt that accommodates all rtts of the flows
	private int bigRTT;
	// Optimum sending window
	private int sWnd;
	// The waitTime before releasing first ACK from accessSwitch
	private double waitTimeBeforeRelease;
	// inter-flow delay
	private double interFlowDelay;
	// Controller has a database of each flow (id) and its AccessLinkDelay
	HashMap<Integer, Double> AccessDelays;

	public Controllerv1(int ID, Network net, int routingAlgorithm, double alpha) {
		super(ID, net, routingAlgorithm);
		this.alpha = alpha;
		bigRTT = 0;
		sWnd = 0;
		interFlowDelay = 0;
		waitTimeBeforeRelease = 0;
		AccessDelays = new HashMap<Integer, Double>();
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from SDNSwitch) ----- */
	/* --------------------------------------------------- */
	public Network recvSegment(Network net, int switchID, Segment segment) {
		this.currentNetwork = net;
		this.currentSegment = segment;
		switch (segment.getType()) {
		case Keywords.SYN:
			this.flowCount++; // Increasing the flowCounter
			currentSegment.setFlowID(flowCount); // Setting the flowID field of SYN segment
			handleRouting(currentNetwork.switches.get(switchID), this.getAccessSwitch(currentSegment.getDstHostID()));
			break;
		case Keywords.FIN:
			this.flowCount--;
			// TODO we might want to remove the flow entries for the completed flow
			break;
		default:
			log.generalLog("Controllerv1.recvSegment()::Invalid segment type has been received.");
			break;
		}
		handleCongestionControl();
		sendSegmentToSwitch(switchID, currentSegment);
		return currentNetwork;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* =================== */
	/* First level methods */
	/* =================== */
	private void handleCongestionControl() {
		updateStateVariables();
		notifyHosts();
		sendBufferUpdateMessageToAccessSwitches(Keywords.TokenBasedBuffer, this.interFlowDelay,
				this.waitTimeBeforeRelease, this.sWnd);
	}

	/* ==================== */
	/* Second level methods */
	/* ==================== */
	private void updateStateVariables() {
		updateBigRTT();
		updateSWnd();
		updateInterFlowDelay();
		updateWaitTimeBeforeRelease();
	}

	private void notifyHosts() {
		Segment segmentToHosts = new Segment(Keywords.ControllerFLowID, Keywords.CTRL, -1, Keywords.CTRLSegSize,
				Keywords.ControllerID, Keywords.BroadcastDestination);
		segmentToHosts.bigRTT_ = this.bigRTT;
		segmentToHosts.sWnd_ = this.sWnd;
		sendSegmentToAccessSwitches(segmentToHosts);
	}

	private void updateBigRTT() {
		ArrayList<Integer> rtts = new ArrayList<Integer>();
		for (double rtt : RTTs.values()) {
			rtts.add((int) rtt);
		}
		bigRTT = Mathematics.lcm(rtts);
	}

	private void updateSWnd() {
		// note that this is only for the single bottleneck scenario
		this.sWnd = (int) Math.floor(alpha * (bigRTT * BtlBWs.get(currentSegment.getFlowID()) / flowCount));
	}

	private void updateInterFlowDelay() {
		this.interFlowDelay = (this.bigRTT / (double) flowCount);
	}

	private void updateWaitTimeBeforeRelease() {
		this.waitTimeBeforeRelease = this.bigRTT;
	}

	/* ================================ */
	/* Methods for switch communication */
	/* ================================ */

	private void sendBufferUpdateMessageToAccessSwitches(int bufferMode, double interFlowDelay,
			double waitBeforeRelease, int tokenNumber) {
		CtrlMessage message = new CtrlMessage();
		message.bufferMode = bufferMode;
		message.interFlowDelay = interFlowDelay;
		message.waitBeforeRelease = waitBeforeRelease;
		message.ackNumber = tokenNumber;
		for (int switchID : accessSwitches) {
			sendBufferUpdateMessage(switchID, message);
		}
	}

	private void sendBufferUpdateMessage(int switchID, CtrlMessage controlMessage) {
		double nextTime = currentNetwork.getCurrentTime() + this.getControlLinkDelay(switchID, Keywords.CTRLSegSize);
		Event nextEvent = new ArrivalToSwitch(nextTime, switchID, null, controlMessage);
		currentNetwork.eventList.addEvent(nextEvent);
	}
}
