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
	public float interSegmentDelay;
	public float interFlowDelayConstant; // The fixed delay between each flow swnd

	public Controllerv1(int ID, Network net, int routingAlgorithm, double alpha) {
		super(ID, net, routingAlgorithm);
		this.alpha = alpha;

	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Inherited methods (from Controller) --------------------------- */
	/* -------------------------------------------------------------------------- */
	public void recvPacket(Network net, int switchID, Packet packet) {
		Segment segment = packet.segment;
		this.currentSwitchID = switchID;
		// this.currentNetwork = net;
		this.currentSegment = segment;
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			database.addFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			handleRouting(net, net.switches.get(switchID), this.getAccessSwitch(currentSegment.getDstHostID()));

			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			currentSegment.changeType(Keywords.Segments.Types.FIN);
			break;
		default:
			break;
		}
		handleCongestionControl(net);
		sendPacketToSwitch(net, switchID, new Packet(currentSegment, null));
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	/* =========================================== */
	/* ========== Congestion Control ============= */
	/* =========================================== */
	private void handleCongestionControl(Network net) {
		updateBigRTT();
		updateInterSegmentDelay();
		updateInterFlowDelayConstant();
		updateSWnd();
		notifyHosts(net);
		// if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 1) {
		notifyAccessSwitches(net, prepareMessage(net));
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
			interSegmentDelay = Keywords.Segments.Sizes.DataSegSize
					/ database.btlBwOfFlowID.get(currentSegment.getFlowID());
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
					/ (database.getNumberOfFlowsForAccessSwitch(currentSwitchID)
							* Keywords.Segments.Sizes.DataSegSize)));
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

	private HashMap<Integer, CtrlMessage> prepareMessage(Network net) {
		HashMap<Integer, CtrlMessage> messages = new HashMap<Integer, CtrlMessage>();
		// a CtrlMessage for each accessSwitches in the network
		for (int accessSwitchID : database.getAccessSwitchIDsSet()) {
			CtrlMessage singleMessage = new CtrlMessage(Keywords.SDNMessages.Types.BufferTokenUpdate);
			HashMap<Integer, BufferToken> preparedTokens = new HashMap<Integer, BufferToken>();
			int i = 0; // The flow ID index
			float accessLinkRttOfFlowZero = 0; // d_i
			float interFlowDelay = 0;
			float initialCycleDelay = 0;
			float steadyCycleDelay = 0;
			for (int hostID : database.getHostIDsSetForAccessSwitchID(accessSwitchID)) {
				BufferToken ccTokenForEachBuffer = new BufferToken();
				if (i == 0) { // flow_0
					interFlowDelay = 0;
					accessLinkRttOfFlowZero = net.hosts.get(hostID).getAccessLinkRtt();
				} else { // flow_i and i>0
					interFlowDelay = i * interFlowDelayConstant
							+ (accessLinkRttOfFlowZero - net.hosts.get(hostID).getAccessLinkRtt());
				}
				initialCycleDelay = (previousBigRTT) + interFlowDelay;
				steadyCycleDelay = bigRTT - database.getRttForAccessSwitchIDAndHostID(accessSwitchID, hostID);
				ccTokenForEachBuffer.activate(true, initialCycleDelay, previousSWnd, steadyCycleDelay, sWnd);

				preparedTokens.put(hostID, ccTokenForEachBuffer);
				i++;
			}

			singleMessage.ccTokenOfHostID = preparedTokens;
			messages.put(accessSwitchID, singleMessage);
		}
		return messages;
	}

	private void notifyAccessSwitches(Network net, HashMap<Integer, CtrlMessage> messages) {
		// TODO this is for one accessSwitch assumption
		// TODO must be updated for more than access switches
		sendControlMessageToAccessSwitches(net, messages);
	}

	private void notifyHosts(Network net) {
		// TODO this is for one accessSwitch assumption
		// TODO must be updated for more than access switches
		Segment segmentToHosts = new Segment(Keywords.ControllerFLowID, Keywords.Segments.Types.CTRL,
				Keywords.Segments.SpecialSequenceNumbers.CTRLSeqNum, Keywords.Segments.Sizes.CTRLSegSize, this.getID(),
				Keywords.BroadcastDestination);
		segmentToHosts.bigRTT_ = this.bigRTT;
		segmentToHosts.sWnd_ = this.sWnd;
		segmentToHosts.interSegmentDelay_ = this.interSegmentDelay;
		sendPacketToSwitch(net, this.currentSwitchID, new Packet(segmentToHosts, null));
	}

}
