package simulator.entities.network.controllers;

import simulator.*;
import simulator.entities.network.*;
import simulator.entities.traffic.*;
import system.Main;
import utility.*;

public class Controllerv2 extends Controller {

	private float alpha;
	private float beta;
	private int bufferedSegmentsInControlLinkCount = 0;
	private float maxAccessLinkTotalDelayDataSize = 0;

	public Controllerv2(int ID, short routingAlgorithm, float alpha, float beta) {
		super(ID, routingAlgorithm);
		this.alpha = alpha;
		this.beta = beta;
	}

	@Override
	public void recvPacket(Network net, int switchID, Packet packet) {

		bufferedSegmentsInControlLinkCount = 0;
		recvdSegment = packet.segment;
		database.accessSwitchID = switchID;
		switch (recvdSegment.getType()) {
		case Keywords.Segments.Types.SYN:
			handleRouting(net, switchID, getAccessSwitchID(net, recvdSegment.getDstHostID()));
			bufferedSegmentsInControlLinkCount++;
			database.arrivalOfSYNForFlowID(net, recvdSegment, switchID);
			handleCongestionControl(net, recvdSegment);
			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			// Update the database
			database.arrivalOfFINForFlowID(recvdSegment.getFlowID(), recvdSegment.getSrcHostID());
			if (database.flowIDOfHostID.keySet().size() > 0) {
				// TODO validate with at least two flows
				handleCongestionControl(net, recvdSegment);
			}
			recvdSegment.changeType(Keywords.Segments.Types.FIN);
			break;
		default:
			break;
		}
		sendPacketToSwitch(net, switchID, new Packet(recvdSegment, null));
	}

	private void handleCongestionControl(Network net, Segment recvdSegment) {
		Link controlLink = net.links.get(controlLinksIDs.get(database.accessSwitchID));
		Link accessLink = database.accessLinkOfFlowID.get(recvdSegment.getFlowID());
		if (recvdSegment.getType() != Keywords.Segments.Types.UncontrolledFIN) {
			if (accessLink.getTotalDelay(Keywords.Segments.Sizes.DataSegSize) > maxAccessLinkTotalDelayDataSize) {
				maxAccessLinkTotalDelayDataSize = accessLink.getTotalDelay(Keywords.Segments.Sizes.DataSegSize);
			}
		}
		float controllerDelayToNextCycle = calculateControllerDelayToNextCycle(net, controlLink);
		Debugger.interMethodDebug("controllerDelayToNextCycle = " + controllerDelayToNextCycle);
		float sInterval = calculateSInterval();
		Debugger.interMethodDebug("sInterval = " + sInterval);
		alpha = calculateAlpha();
		int interFlowIndex = 0;
		float accessLinkDelay_0 = 0;
		for (int hostID : database.flowIDOfHostID.keySet()) {
			int flowID = database.flowIDOfHostID.get(hostID);
			float accessLinkDelay_i = database.accessLinkOfFlowID.get(flowID)
					.getTotalDelay(Keywords.Segments.Sizes.DataSegSize);
			if (interFlowIndex == 0) {
				accessLinkDelay_0 = accessLinkDelay_i;
			}
			Segment segment = new Segment(Simulator.reverseFlowStreamID(flowID), Keywords.Segments.Types.CTRL,
					Keywords.Segments.SpecialSequenceNumbers.CTRLSeqNum, Keywords.Segments.Sizes.CTRLSegSize, this.ID,
					hostID);
			Debugger.printLine("-", 50);
			Debugger.interMethodDebug(" FlowID = " + flowID);
			// -------------------------------------
			// Calculate sWnd
			segment.sWnd = calculateFlowSWnd(sInterval,
					net.links.get(database.btlLinkIDOfFlowID.get(flowID)).getBandwidth());
			Debugger.interMethodDebug(" sWnd = " + segment.sWnd);
			// -------------------------------------
			// Calculate sInterSegmentDelay = transmissionDelay for flowBtlBw
			segment.sInterSegmentDelay = net.links.get(database.btlLinkIDOfFlowID.get(flowID))
					.getTransmissionDelay(Keywords.Segments.Sizes.DataSegSize);
			Debugger.interMethodDebug(" sInterSegmentDelay = " + segment.sInterSegmentDelay);
			// -------------------------------------
			// Calculate delayToNextCycle_i
			float delayToReceiveCTRL_i = calculateDelayToRecvCTRLByFlowID(net, controlLink, flowID);
			Debugger.interMethodDebug(
					" Delay to receive CTRL for flowID = " + flowID + " is = " + delayToReceiveCTRL_i);
			float delayToNextCycle_i = Mathematics.subtractFloat(controllerDelayToNextCycle, delayToReceiveCTRL_i);
			float c = maxAccessLinkTotalDelayDataSize;
			c = 5;
			delayToNextCycle_i = Mathematics.addFloat(delayToNextCycle_i, c);
			// not fixed
			segment.timeToNextCycle = delayToNextCycle_i;
			Debugger.interMethodDebug(" timeToNextCycle_" + interFlowIndex + " = " + segment.timeToNextCycle);
			// -------------------------------------
			// Calculate sInitialDelay
			float numi = Mathematics.multiplyFloat(interFlowIndex, sInterval);
			float initialDelay_i = Mathematics.divideFloat(numi, database.flowIDOfHostID.size());
			if (interFlowIndex > 0) {
				initialDelay_i = Mathematics.addFloat(initialDelay_i,
						Mathematics.subtractFloat(accessLinkDelay_0, accessLinkDelay_i));
			}
			segment.sInitialDelay = initialDelay_i;
			Debugger.interMethodDebug(" sInitialDelay_" + interFlowIndex + " = " + segment.sInitialDelay);
			// -------------------------------------
			segment.sInterval = sInterval;
			interFlowIndex++;
			sendPacketToSwitch(net, database.accessSwitchID, new Packet(segment, null));
			bufferedSegmentsInControlLinkCount++;

		}
	}

	private float calculateControllerDelayToNextCycle(Network net, Link controlLink) {
		float controllerDelayToNextCycle = 0;
		switch (recvdSegment.getType()) {
		case Keywords.Segments.Types.SYN:
			controllerDelayToNextCycle = calculateSYNACKDelay(net,
					controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
					controlLink.getPropagationDelay(), database.accessLinkOfFlowID.get(recvdSegment.getFlowID())
							.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize));
			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			controllerDelayToNextCycle = calclateMaxCTRLDelay(
					controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
					controlLink.getPropagationDelay());
			break;
		default:
			Main.error("Controllerv2", "handleCongestionControl", "Invalid recvdSegment type.");
			break;
		}
		return controllerDelayToNextCycle;
	}

	private float calculateDelayToRecvCTRLByFlowID(Network net, Link controlLink, int flowID) {
		float controlLinkTotalDelay_i = controlLink.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
		float controlLinkBufferTime = getControlLinkBufferTime(net);
		float accessLinkTotalDelay_i = database.accessLinkOfFlowID.get(flowID)
				.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
		float delayToReceiveCTRL_i = Mathematics.addFloat(accessLinkTotalDelay_i,
				Mathematics.addFloat(controlLinkTotalDelay_i, controlLinkBufferTime));
		return delayToReceiveCTRL_i;
	}

	private float getControlLinkBufferTime(Network net) {
		return Mathematics.multiplyFloat(bufferedSegmentsInControlLinkCount,
				net.links.get(controlLinksIDs.get(database.accessSwitchID))
						.getTransmissionDelay(Keywords.Segments.Sizes.SYNSegSize));
	}

	private float calculateAlpha() {
		alpha = 1;// FIXME For now
		return alpha;
	}

	private float calculateSInterval() {
		return database.maxRTT;// FIXME only for oneAccessSwitch
	}

	private float calculateSYNACKDelay(Network net, float controlLinkTransDelayCTRLSize, float controlLinkPropDelay,
			float accessLinkTotalDelaySYNSize) {
		Debugger.methodEntrance("Controllerv2", "calSYNACKDelay",
				"flowID = " + recvdSegment.getFlowID() + ", TransDelay = " + controlLinkTransDelayCTRLSize
						+ ", PropDelay = " + controlLinkPropDelay + ", ACLink totalDelay = "
						+ accessLinkTotalDelaySYNSize);

		float synRtt_i = database.SYNRTTOfFlowID.get(recvdSegment.getFlowID());
		Debugger.interMethodDebug("SYNRTT = " + synRtt_i);
		float controlLinkBufferDelay = Mathematics.multiplyFloat(database.flowIDOfHostID.keySet().size() + 1,
				controlLinkTransDelayCTRLSize);
		Debugger.interMethodDebug("ControlLinkBufferTime = " + controlLinkBufferDelay);
		float SYNACKDelay = Mathematics.addFloat(synRtt_i, controlLinkBufferDelay);
		SYNACKDelay = Mathematics.addFloat(SYNACKDelay,
				Mathematics.addFloat(controlLinkTransDelayCTRLSize, controlLinkPropDelay));
		SYNACKDelay = Mathematics.subtractFloat(SYNACKDelay, accessLinkTotalDelaySYNSize);
		Debugger.interMethodDebug("SYNACK Delay = " + SYNACKDelay);
		Debugger.methodExit("Controllerv2", "calSYNACKDelay", null);
		return SYNACKDelay;
	}

	private float calclateMaxCTRLDelay(float controlLinkTransDelayCTRLSize, float controlLinkPropagationDelay) {
		float maxAccessDelay = -1;
		float maxCTRLDelay = 0;
		float bufferDelay = 0;
		int flowIndex = 0;
		for (int flowID : database.accessLinkOfFlowID.keySet()) {
			Link accessLink = database.accessLinkOfFlowID.get(flowID);
			if (accessLink.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize) > maxAccessDelay) {
				maxAccessDelay = accessLink.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
				maxCTRLDelay = Mathematics.addFloat(maxCTRLDelay, maxAccessDelay);
				maxCTRLDelay = Mathematics.addFloat(maxCTRLDelay, controlLinkPropagationDelay);
				bufferDelay = Mathematics.multiplyFloat((flowIndex + 1), controlLinkTransDelayCTRLSize);
				maxCTRLDelay = Mathematics.addFloat(maxCTRLDelay, bufferDelay);
			}
			flowIndex++;
		}
		return maxCTRLDelay;
	}

	private int calculateFlowSWnd(float sInterval, float flowBtlBw) {
		float numinator = Mathematics.multiplyFloat(sInterval, flowBtlBw);
		float denuminator = Mathematics.multiplyFloat(Keywords.Segments.Sizes.DataSegSize,
				database.flowIDOfHostID.keySet().size());
		int flowSWnd = (int) Math
				.floor(Mathematics.multiplyFloat(alpha, Mathematics.divideFloat(numinator, denuminator)));
		if (flowSWnd < 1) {
			flowSWnd = 1;
		}
		return flowSWnd;
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
	}

	/**********************************************************************/
	/*********************** Deprecated Methods ***************************/
	/**********************************************************************/

	private void handleSYNCongestionControl(Network net) {
		Debugger.printLine("-", 80);
		Debugger.debugToConsole("SYNCC method:");
		float sInterval, SYNACKDelay;
		sInterval = calculateSInterval();
		Debugger.debugToConsole(" sInterval = " + sInterval);
		SYNACKDelay = calculateSYNACKDelay(net,
				net.links.get(controlLinksIDs.get(database.accessSwitchID))
						.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
				net.links.get(controlLinksIDs.get(database.accessSwitchID)).getPropagationDelay(),
				database.accessLinkOfFlowID.get(recvdSegment.getFlowID())
						.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize));
		alpha = calculateAlpha();
		Debugger.debugToConsole(" SYNACKDelay = " + SYNACKDelay);

		int interFlowIndex = 0;
		for (int hostID : database.flowIDOfHostID.keySet()) {
			int flowID = database.flowIDOfHostID.get(hostID);
			Segment segment = new Segment(Simulator.reverseFlowStreamID(flowID), Keywords.Segments.Types.CTRL,
					Keywords.Segments.SpecialSequenceNumbers.CTRLSeqNum, Keywords.Segments.Sizes.CTRLSegSize, this.ID,
					hostID);
			Debugger.printLine("^", 30);
			Debugger.debugToConsole(" FlowID = " + flowID);
			// -------------------------------------
			// Calculate sWnd
			segment.sWnd = calculateFlowSWnd(sInterval,
					net.links.get(database.btlLinkIDOfFlowID.get(flowID)).getBandwidth());
			Debugger.debugToConsole("  sWnd = " + segment.sWnd);
			// -------------------------------------
			// Calculate sInterSegmentDelay = transmissionDelay for flowBtlBw
			segment.sInterSegmentDelay = net.links.get(database.btlLinkIDOfFlowID.get(flowID))
					.getTransmissionDelay(Keywords.Segments.Sizes.DataSegSize);
			Debugger.debugToConsole("  sInterSegmentDelay = " + segment.sInterSegmentDelay);
			// -------------------------------------
			// Calculate timeToStart
			float controlProp_i = net.links.get(controlLinksIDs.get(database.accessSwitchID)).getPropagationDelay();
			float accessDelay_i = database.accessLinkOfFlowID.get(flowID)
					.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
			float bufferTime_i = Mathematics.multiplyFloat(interFlowIndex + 2,
					net.links.get(controlLinksIDs.get(database.accessSwitchID))
							.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize));
			segment.timeToNextCycle = Mathematics.subtractFloat(SYNACKDelay,
					Mathematics.addFloat(bufferTime_i, Mathematics.addFloat(controlProp_i, accessDelay_i)));
			Debugger.debugToConsole("  timeToNextCycle_" + interFlowIndex + " = " + segment.timeToNextCycle);

			// -------------------------------------
			// Calculate sInitialDelay
			float numi = Mathematics.multiplyFloat(interFlowIndex, sInterval);
			segment.sInitialDelay = Mathematics.divideFloat(numi, database.flowIDOfHostID.size());
			Debugger.debugToConsole("  sInitialDelay_" + interFlowIndex + " = " + segment.sInitialDelay);
			// -------------------------------------
			segment.sInterval = sInterval;
			interFlowIndex++;
			sendPacketToSwitch(net, database.accessSwitchID, new Packet(segment, null));
		}
		Debugger.printLine("-", 80);
	}

	private void handleFINCongestionControl(Network net) {
		float sInterval, CTRLDelay;
		sInterval = calculateSInterval();
		CTRLDelay = calclateMaxCTRLDelay(
				net.links.get(controlLinksIDs.get(database.accessSwitchID))
						.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
				net.links.get(controlLinksIDs.get(database.accessSwitchID)).getPropagationDelay());

		alpha = calculateAlpha();

		int interFlowIndex = 0;
		for (int hostID : database.flowIDOfHostID.keySet()) {
			int flowID = database.flowIDOfHostID.get(hostID);
			Segment segment = new Segment(Simulator.reverseFlowStreamID(flowID), Keywords.Segments.Types.CTRL,
					Keywords.Segments.SpecialSequenceNumbers.CTRLSeqNum, Keywords.Segments.Sizes.CTRLSegSize, this.ID,
					hostID);
			// -------------------------------------
			// Calculate sWnd
			segment.sWnd = calculateFlowSWnd(sInterval,
					net.links.get(database.btlLinkIDOfFlowID.get(flowID)).getBandwidth());
			// -------------------------------------
			// Calculate sInterSegmentDelay = transmissionDelay for flowBtlBw
			segment.sInterSegmentDelay = net.links.get(database.btlLinkIDOfFlowID.get(flowID))
					.getTransmissionDelay(Keywords.Segments.Sizes.DataSegSize);
			// -------------------------------------
			// Calculate timeToNextCycle
			float accessTotalDelay_i = database.accessLinkOfFlowID.get(flowID)
					.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
			float controlProp_i = net.links.get(controlLinksIDs.get(database.accessSwitchID)).getPropagationDelay();
			float controlTrans_i = net.links.get(controlLinksIDs.get(database.accessSwitchID))
					.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize);
			float bufferTime_i = Mathematics.multiplyFloat(interFlowIndex + 1, controlTrans_i);
			float subtractoinCompenet = Mathematics.addFloat(accessTotalDelay_i,
					Mathematics.addFloat(controlProp_i, bufferTime_i));
			float timeToNextCycle_i = Mathematics.subtractFloat(Mathematics.multiplyFloat(beta, CTRLDelay),
					subtractoinCompenet);
			segment.timeToNextCycle = timeToNextCycle_i;
			// -------------------------------------
			// Calculate sInitialDelay
			float numi = Mathematics.multiplyFloat(interFlowIndex, sInterval);
			segment.sInitialDelay = Mathematics.divideFloat(numi, database.flowIDOfHostID.size());
			interFlowIndex++;
			// -------------------------------------
			segment.sInterval = sInterval;
			sendPacketToSwitch(net, database.accessSwitchID, new Packet(segment, null));
		}
	}
}
