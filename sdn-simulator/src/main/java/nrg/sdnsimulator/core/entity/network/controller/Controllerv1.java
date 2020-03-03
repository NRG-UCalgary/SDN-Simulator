package nrg.sdnsimulator.core.entity.network.controller;

import java.util.HashMap;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.Controller;
import nrg.sdnsimulator.core.entity.network.CtrlMessage;
import nrg.sdnsimulator.core.entity.network.buffer.BufferToken;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Keywords;

public class Controllerv1 extends Controller {

	/* Congestion Control Variables */
	private double alpha;
	private int bigRTT; // TODO With only one access Switch
	// Temporary
	private int currentSwitchID;
	private float interFlowDelayConstant; // The fixed delay between each flow swnd
	private float interSegmentDelay;
	private int previousBigRTT;
	// TODO it should be changed to a map <AccessSwitchID, bigRTT> later
	private int previousSWnd;
	private int sWnd;

	public Controllerv1(int ID, short routingAlgorithm, double alpha) {
		super(ID, routingAlgorithm);
		this.alpha = alpha;

	}

	/* =========================================== */
	/* ========== Congestion Control ============= */
	/* =========================================== */
	private void handleCongestionControl(Network net) {
		updateBigRTT();
		updateInterSegmentDelay();
		updateInterFlowDelayConstant();
		updateSWnd();
		notifyHosts(net);
		notifyAccessSwitches(net, prepareMessage(net));

	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	private void notifyAccessSwitches(Network net, HashMap<Integer, CtrlMessage> messages) {
		// TODO this is for one accessSwitch assumption
		// TODO must be updated for more than access switches
		sendControlMessageToAccessSwitches(net, messages);
	}

	private void notifyHosts(Network net) {
		// TODO this is for one accessSwitch assumption
		// TODO must be updated for more than access switches
		Segment segmentToHosts = new Segment(Keywords.ControllerFLowID,
				Keywords.Segments.Types.CTRL, Keywords.Segments.SpecialSequenceNumbers.CTRLSeqNum,
				Keywords.Segments.Sizes.CTRLSegSize, this.getID(), Keywords.BroadcastDestination);
		segmentToHosts.setBigRTT_(this.bigRTT);
		segmentToHosts.setsWnd_(this.sWnd);
		segmentToHosts.setInterSegmentDelay_(this.interSegmentDelay);
		sendPacketToSwitch(net, this.currentSwitchID, new Packet(segmentToHosts, null));
	}

	private HashMap<Integer, CtrlMessage> prepareMessage(Network net) {
		HashMap<Integer, CtrlMessage> messages = new HashMap<Integer, CtrlMessage>();
		// a CtrlMessage for each accessSwitches in the network
		for (int accessSwitchID : database.getAccessSwitchIDsSet()) {
			CtrlMessage singleMessage = new CtrlMessage(
					Keywords.SDNMessages.Types.BufferTokenUpdate);
			HashMap<Integer, BufferToken> preparedTokens = new HashMap<Integer, BufferToken>();
			int i = 0; // The flow ID index
			// float accessLinkRttOfFlowZero = 0; // d_i
			float interFlowDelay = 0;
			float initialCycleDelay = 0;
			float steadyCycleDelay = 0;
			for (int hostID : database.getHostIDsSetForAccessSwitchID(accessSwitchID)) {
				BufferToken ccTokenForEachBuffer = new BufferToken();
				if (i == 0) { // flow_0
					interFlowDelay = 0;
					// accessLinkRttOfFlowZero = net.hosts.get(hostID).getAccessLinkRtt();
				} else { // flow_i and i>0
					// interFlowDelay = i * interFlowDelayConstant
					// + (accessLinkRttOfFlowZero - net.hosts.get(hostID).getAccessLinkRtt());
				}
				initialCycleDelay = (previousBigRTT) + interFlowDelay;
				// initialCycleDelay = 0;//For validation
				steadyCycleDelay = bigRTT
						- database.getRttForAccessSwitchIDAndHostID(accessSwitchID, hostID);
				// steadyCycleDelay = 0;//For validation
				ccTokenForEachBuffer.activate(true, initialCycleDelay, previousSWnd,
						steadyCycleDelay, sWnd);

				preparedTokens.put(hostID, ccTokenForEachBuffer);
				i++;
			}

			singleMessage.setCcTokenOfHostID(preparedTokens);
			messages.put(accessSwitchID, singleMessage);
		}
		return messages;
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Inherited methods (from Controller) --------------------------- */
	/* -------------------------------------------------------------------------- */
	public void recvPacket(Network net, int switchID, Packet packet) {
		Segment segment = packet.getSegment();
		this.currentSwitchID = switchID;
		// this.currentNetwork = net;
		this.recvdSegment = segment;
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			database.addFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			handleRouting(net, switchID, getAccessSwitchID(net, recvdSegment.getDstHostID()));

			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			database.removeFlow(switchID, segment.getSrcHostID(), segment.getFlowID());
			recvdSegment.changeType(Keywords.Segments.Types.FIN);
			break;
		default:
			break;
		}
		handleCongestionControl(net);
		sendPacketToSwitch(net, switchID, new Packet(recvdSegment, null));
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

	private void updateInterFlowDelayConstant() {
		// TODO this is for one access Switch only
		// TODO must be updated accordingly later
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 1) {
			interFlowDelayConstant = bigRTT
					/ database.getNumberOfFlowsForAccessSwitch(currentSwitchID);
		} else {
			interFlowDelayConstant = 0;
		}
	}

	private void updateInterSegmentDelay() {
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 0) {
			interSegmentDelay = Keywords.Segments.Sizes.DataSegSize
					/ database.btlBwOfFlowID.get(recvdSegment.getFlowID());
		} else {
			interSegmentDelay = 0;
		}

	}

	private void updateSWnd() {
		// note that this is only for the single bottleneck scenario
		previousSWnd = sWnd;
		if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) > 0) {
			this.sWnd = (int) Math
					.floor(alpha * (bigRTT * database.btlBwOfFlowID.get(recvdSegment.getFlowID())
							/ (database.getNumberOfFlowsForAccessSwitch(currentSwitchID)
									* Keywords.Segments.Sizes.DataSegSize)));
			if (this.sWnd == 0) {
				this.sWnd = 1;
			}
			if (previousSWnd == 0) {
				previousSWnd = sWnd;
			}
		} else if (database.getNumberOfFlowsForAccessSwitch(currentSwitchID) == 0) {
		}
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
		// Controller does not need Timer for now

	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public int getBigRTT() {
		return bigRTT;
	}

	public void setBigRTT(int bigRTT) {
		this.bigRTT = bigRTT;
	}

	public int getCurrentSwitchID() {
		return currentSwitchID;
	}

	public void setCurrentSwitchID(int currentSwitchID) {
		this.currentSwitchID = currentSwitchID;
	}

	public float getInterFlowDelayConstant() {
		return interFlowDelayConstant;
	}

	public void setInterFlowDelayConstant(float interFlowDelayConstant) {
		this.interFlowDelayConstant = interFlowDelayConstant;
	}

	public float getInterSegmentDelay() {
		return interSegmentDelay;
	}

	public void setInterSegmentDelay(float interSegmentDelay) {
		this.interSegmentDelay = interSegmentDelay;
	}

	public int getPreviousBigRTT() {
		return previousBigRTT;
	}

	public void setPreviousBigRTT(int previousBigRTT) {
		this.previousBigRTT = previousBigRTT;
	}

	public int getPreviousSWnd() {
		return previousSWnd;
	}

	public void setPreviousSWnd(int previousSWnd) {
		this.previousSWnd = previousSWnd;
	}

	public int getsWnd() {
		return sWnd;
	}

	public void setsWnd(int sWnd) {
		this.sWnd = sWnd;
	}

}
