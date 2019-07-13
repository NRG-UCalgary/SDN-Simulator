package entities.controllers;

import java.util.HashMap;
import entities.*;
import entities.buffers.*;
import system.*;
import system.utility.*;

public class Controllerv1 extends Controller {

	// Temporary
	public int currentSwitchID;

	/* Congestion Control Variables */
	public double alpha;
	public int previousBigRTT;
	public int bigRTT; // TODO With only one access Switch
	// TODO it should be changed to a map <AccessSwitchID, bigRTT> later
	public int previousSWnd;
	public int sWnd;
	public double interSegmentDelay;
	public double interFlowDelayConstant; // The fixed delay between each flow swnd

	public Controllerv1(int ID, Network net, int routingAlgorithm, double alpha) {
		super(ID, net, routingAlgorithm);
		this.alpha = alpha;

	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Inherited methods (from Controller) --------------------------- */
	/* -------------------------------------------------------------------------- */
	public Network recvPacket(Network net, int switchID, Packet packet) {
		Debugger.debug("========== Controler has received a Segment ==========");
		Segment segment = packet.segment;
		this.currentSwitchID = switchID;
		this.currentNetwork = net;
		this.currentSegment = segment;
		switch (segment.getType()) {
		case Keywords.SYN:
			Debugger.debug("SYN (flow_" + segment.getFlowID() + ")" + " arrived at: " + net.getCurrentTime());
			database.addFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			handleRouting(currentNetwork.switches.get(switchID), this.getAccessSwitch(currentSegment.getDstHostID()));

			break;
		case Keywords.UncontrolledFIN:
			Debugger.debug("FIN has arrived at:" + currentNetwork.getCurrentTime());
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			currentSegment.changeType(Keywords.FIN);
			break;
		default:
			break;
		}
		handleCongestionControl();
		sendPacketToSwitch(switchID, new Packet(currentSegment, null));
		Debugger.debug("======================================================");
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
		Debugger.debug("bigRtt = " + bigRTT);
		updateInterSegmentDelay();
		Debugger.debug("interSegmentDelay = " + interSegmentDelay);
		updateInterFlowDelayConstant();
		Debugger.debug("interFlowDelay Constant = " + interFlowDelayConstant);
		updateSWnd();
		Debugger.debug("sWnd = " + sWnd);
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
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) <= 1) {
			previousBigRTT = 0;
		}
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 0) {
			bigRTT = (int) Math.ceil(database.getMaxRTTForAccessSwitchID(currentSwitchID));
		} else {
			bigRTT = 0;
		}
	}

	private void updateInterSegmentDelay() {
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 0) {
			interSegmentDelay = Keywords.DataSegSize / (double) database.btlBwOfFlowID.get(currentSegment.getFlowID());
		} else {
			interSegmentDelay = 0;
		}

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
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 0) {
			this.sWnd = (int) Math.floor(alpha * (bigRTT * database.btlBwOfFlowID.get(currentSegment.getFlowID())
					/ (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) * Keywords.DataSegSize)));
			if (this.sWnd == 0) {
				this.sWnd = 1;
			}
			if (previousSWnd == 0) {
				previousSWnd = sWnd;
			}
		} else if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) == 0) {
		} else {
			Debugger.debugToConsole("WE should not get here");
		}
	}

	private HashMap<Integer, CtrlMessage> prepareMessage() {
		Debugger.debug("++++++++++ Preparing message ++++++++++");
		HashMap<Integer, CtrlMessage> messages = new HashMap<Integer, CtrlMessage>();
		// a CtrlMessage for each accessSwitches in the network
		for (int accessSwitchID : database.getAccessSwitchIDsSet()) {
			CtrlMessage singleMessage = new CtrlMessage(Keywords.BufferTokenUpdate);
			HashMap<Integer, BufferToken> preparedTokens = new HashMap<Integer, BufferToken>();
			int i = 0; // The flow ID index
			double accessLinkRttOfFlowZero = 0; // d_i
			double interFlowDelay = 0;
			double initialCycleDelay = 0;
			double steadyCycleDelay = 0;
			for (int hostID : database.getHostIDsSetForAccessSwitchID(accessSwitchID)) {
				BufferToken ccTokenForEachBuffer = new BufferToken();
				Debugger.debug(" The token for hostID: " + hostID);
				if (i == 0) { // flow_0
					interFlowDelay = 0;
					accessLinkRttOfFlowZero = currentNetwork.hosts.get(hostID).getAccessLinkRtt();
				} else { // flow_i and i>0
					interFlowDelay = i * interFlowDelayConstant
							+ (accessLinkRttOfFlowZero - currentNetwork.hosts.get(hostID).getAccessLinkRtt());
				}
				initialCycleDelay = (previousBigRTT) + interFlowDelay;
				Debugger.debug("  initial ccDelay = " + initialCycleDelay);
				steadyCycleDelay = bigRTT - database.getRttForAccessSwitchIDAndHostID(accessSwitchID, hostID);
				Debugger.debug("  steady ccDelay = " + steadyCycleDelay);
				ccTokenForEachBuffer.activate(true, interSegmentDelay, initialCycleDelay, previousSWnd,
						steadyCycleDelay, sWnd);

				preparedTokens.put(hostID, ccTokenForEachBuffer);
				i++;
				Debugger.debug("-------------------------------");
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
