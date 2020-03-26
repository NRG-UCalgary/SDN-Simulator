package nrg.sdnsimulator.core.entity.network.controller;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.Simulator;
import nrg.sdnsimulator.core.entity.network.Controller;
import nrg.sdnsimulator.core.entity.network.Link;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Keywords;
import nrg.sdnsimulator.core.utility.Mathematics;

@Getter
@Setter
public class Controllerv2 extends Controller {

	private float alpha; // sWnd
	private float beta; // SYNACKDelay
	private float gamma; // sInterval
	private float mostRecentCycleStartTime = 0;
	private boolean validationReport = false;
	private int sCycleIndex = -1;

	public Controllerv2(int ID, short routingAlgorithm, float alpha, float beta, float gamma) {
		super(ID, routingAlgorithm);
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
	}

	@Override
	public void recvPacket(Network net, int switchID, Packet packet) {
		currentNetwork = net;
		recvdSegment = packet.getSegment();
		database.setAccessSwitchID(switchID);
		switch (recvdSegment.getType()) {
		case Keywords.Segments.Types.SYN:
			handleRouting(net, switchID, getAccessSwitchID(net, recvdSegment.getDstHostID()));
			database.arrivalOfSYNForFlowID(net, recvdSegment, switchID);
			handleCongestionControl(net, recvdSegment);
			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			// Update the database
			database.arrivalOfFINForFlowID(switchID, recvdSegment.getFlowID(), recvdSegment.getSrcHostID());
			if (database.getFlowIDOfHostID().keySet().size() > 0) {
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
		sCycleIndex++;
		Link controlLink = net.getLinks().get(controlLinksIDs.get(database.getAccessSwitchID()));
		float sCycleStartDelay = calculateSCycleStartDelay(net, controlLink);
		float sInterval = calculateSInterval();
		int interFlowIndex = 0;
		float accessLinkDelay_0 = 0;
		for (int hostID : database.getFlowIDOfHostIDOfAccessSwitchID().get(database.getAccessSwitchID()).keySet()) {
			int flowID = database.getFlowIDOfHostIDOfAccessSwitchID().get(database.getAccessSwitchID()).get(hostID);
			float accessLinkDelay_i = database.getAccessLinkOfFlowID().get(flowID)
					.getTotalDelay(Keywords.Segments.Sizes.DataSegSize);
			if (interFlowIndex == 0) {
				accessLinkDelay_0 = accessLinkDelay_i;
			}
			Segment segment = new Segment(Simulator.reverseFlowStreamID(flowID), Keywords.Segments.Types.CTRL,
					Keywords.Segments.SpecialSequenceNumbers.CTRLSeqNum, Keywords.Segments.Sizes.CTRLSegSize, this.ID,
					hostID);
			// -------------------------------------
			// Calculate sWnd
			segment.setsWnd(calculateFlowSWnd(sInterval,
					net.getLinks().get(database.getBtlLinkIDOfFlowID().get(flowID)).getBandwidth()));

			// -------------------------------------
			// Calculate sInterSegmentDelay = transmissionDelay for flowBtlBw
			segment.setsInterSegmentDelay(net.getLinks().get(database.getBtlLinkIDOfFlowID().get(flowID))
					.getTransmissionDelay(Keywords.Segments.Sizes.DataSegSize));
			// -------------------------------------
			// Calculate delayToNextCycle_i
			float CTRLDelay_i = calculateCTRLDelayOfFlowID(net, controlLink, flowID, interFlowIndex);
			float delayToNextCycle_i = Mathematics.subtractFloat(sCycleStartDelay, CTRLDelay_i);
			segment.setTimeToNextCycle(delayToNextCycle_i);
			// -------------------------------------
			// Calculate sInitialDelay
			float numi = Mathematics.multiplyFloat(interFlowIndex, sInterval);
			float initialDelay_i = Mathematics.divideFloat(numi, database.getFlowIDOfHostID().size());
			if (interFlowIndex > 0) {
				initialDelay_i = Mathematics.addFloat(initialDelay_i,
						Mathematics.subtractFloat(accessLinkDelay_0, accessLinkDelay_i));
			}
			segment.setsInitialDelay(initialDelay_i);
			// -------------------------------------
			segment.setsInterval(sInterval);
			interFlowIndex++;
			sendPacketToSwitch(net, database.getAccessSwitchID(), new Packet(segment, null));

		}
	}

	private float calculateSCycleStartDelay(Network net, Link controlLink) {
		float sCycleStartDelay = 0;
		switch (recvdSegment.getType()) {
		case Keywords.Segments.Types.SYN:
			sCycleStartDelay = calculateSYNACKDelay(net,
					controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
					controlLink.getPropagationDelay(), database.getAccessLinkOfFlowID().get(recvdSegment.getFlowID())
							.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize));
			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			sCycleStartDelay = calclateMaxCTRLDelay(
					controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
					controlLink.getPropagationDelay());
			break;
		default:
			break;
		}
		sCycleStartDelay = Mathematics.addFloat(sCycleStartDelay,
				calclateMaxCTRLDelay(controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
						controlLink.getPropagationDelay()));
		float sCycleStartTime = Mathematics.addFloat(net.getCurrentTime(), sCycleStartDelay);
		if (mostRecentCycleStartTime > sCycleStartTime) {
			sCycleStartTime = mostRecentCycleStartTime;
			sCycleStartDelay = Mathematics.subtractFloat(mostRecentCycleStartTime, net.getCurrentTime());
		} else {
			mostRecentCycleStartTime = sCycleStartTime;
		}
		return sCycleStartDelay;
	}

	private float calclateMaxCTRLDelay(float controlLinkTransDelayCTRLSize, float controlLinkPropagationDelay) {
		float maxCTRLDelay = -1;
		int flowIndex = 0;
		for (int hostID : database.getFlowIDOfHostIDOfAccessSwitchID().get(database.getAccessSwitchID()).keySet()) {
			int flowID = database.getFlowIDOfHostID().get(hostID);
			float CTRLDelay = 0;
			Link accessLink = database.getAccessLinkOfFlowID().get(flowID);
			float accessTotalDelay = accessLink.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
			accessTotalDelay = Mathematics.addFloat(accessTotalDelay,
					accessLink.getTransmissionDelay(Keywords.Segments.Sizes.ACKSegSize));
			float CLQDelay = Mathematics.multiplyFloat(flowIndex, controlLinkTransDelayCTRLSize);
			float CLTotalDelay = Mathematics.addFloat(CLQDelay,
					Mathematics.addFloat(controlLinkTransDelayCTRLSize, controlLinkPropagationDelay));
			CTRLDelay = Mathematics.addFloat(accessTotalDelay, CLTotalDelay);
			if (CTRLDelay > maxCTRLDelay) {
				maxCTRLDelay = CTRLDelay;
			}
			flowIndex++;
		}

		maxCTRLDelay = -1;
		float ctrlDelay = 0;
		flowIndex = 0;
		for (int hostID : database.getFlowIDOfHostIDOfAccessSwitchID().get(database.getAccessSwitchID()).keySet()) {
			int flowID = database.getFlowIDOfHostID().get(hostID);
			ctrlDelay = calculateCTRLDelayOfFlowID(currentNetwork,
					currentNetwork.getLinks().get(controlLinksIDs.get(database.getAccessSwitchID())), flowID,
					flowIndex);
			if (ctrlDelay > maxCTRLDelay) {
				maxCTRLDelay = ctrlDelay;
			}
			flowIndex++;
		}
		return maxCTRLDelay;
	}

	private float calculateCTRLDelayOfFlowID(Network net, Link controlLink, int flowID, int flowIndex) {
		float CLTotalDelay = controlLink.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
		float CLQDelay = 0;
		if (recvdSegment.getType() == Keywords.Segments.Types.SYN) {
			CLQDelay = Mathematics.addFloat(controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CtrlMessageSize),
					Mathematics.multiplyFloat(flowIndex,
							controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize)));
		} else {
			CLQDelay = Mathematics.multiplyFloat(flowIndex,
					controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize));
		}
		CLTotalDelay = Mathematics.addFloat(CLTotalDelay, CLQDelay);
		float ALTotalDelay = database.getAccessLinkOfFlowID().get(flowID)
				.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
		float ALQDelay = database.getAccessLinkOfFlowID().get(flowID)
				.getTransmissionDelay(Keywords.Segments.Sizes.ACKSegSize);
		ALTotalDelay = Mathematics.addFloat(ALTotalDelay, ALQDelay);
		float CTRLDelay = Mathematics.addFloat(CLTotalDelay, ALTotalDelay);
		return CTRLDelay;
	}

	private float calculateSInterval() {
		float maxRTT = Mathematics.multiplyFloat(gamma,
				database.getMaxRTTOfAccessSwitchID().get(database.getAccessSwitchID()));
		return maxRTT;
	}

	private float calculateSYNACKDelay(Network net, float CLTransDelayCTRLSize, float CLPropDelay,
			float accessLinkTotalDelaySYNSize) {
		Link networkBtlLink = net.getLinks().get(database.getNetworkBtlLinkIDOfFlowID().get(recvdSegment.getFlowID()));
		float networkBtlTransDelay_Data = networkBtlLink.getTransmissionDelay(Keywords.Segments.Sizes.DataSegSize);
		float networkBtlTransDelay_ACK = networkBtlLink.getTransmissionDelay(Keywords.Segments.Sizes.ACKSegSize);
		int numberOfFlwos = database.getNumberOfFlowsForAccessSwitch(database.getAccessSwitchID());

		float CLQDelay_i = Mathematics.multiplyFloat(numberOfFlwos + 1, CLTransDelayCTRLSize);
		CLQDelay_i = Mathematics.multiplyFloat(numberOfFlwos, CLQDelay_i);
		float CLTotalDelay = Mathematics.addFloat(CLQDelay_i, Mathematics.addFloat(CLTransDelayCTRLSize, CLPropDelay));
		float synRtt_i = database.getSYNRTTOfFlowID().get(recvdSegment.getFlowID());
		float synRtt_Queue = Mathematics.addFloat(networkBtlTransDelay_Data, networkBtlTransDelay_ACK);
		synRtt_i = Mathematics.addFloat(synRtt_i, synRtt_Queue);
		float SYNACKDelay = Mathematics.addFloat(synRtt_i, CLTotalDelay);
		SYNACKDelay = Mathematics.multiplyFloat(beta, SYNACKDelay);
		SYNACKDelay = Mathematics.subtractFloat(SYNACKDelay, accessLinkTotalDelaySYNSize);
		return SYNACKDelay;
	}

	private int calculateFlowSWnd(float sInterval, float flowBtlBw) {
		float numinator = Mathematics.multiplyFloat(sInterval, flowBtlBw);
		float denuminator = Mathematics.multiplyFloat(Keywords.Segments.Sizes.DataSegSize,
				database.getNumberOfFlowsForAccessSwitch(database.getAccessSwitchID()));
		int flowSWnd = (int) Math
				.floor((Mathematics.multiplyFloat(alpha, Mathematics.divideFloat(numinator, denuminator)))) - 1;
		if (flowSWnd < 1) {
			flowSWnd = 1;
		}
		return flowSWnd;
	}

	@Override
	public void executeTimeOut(Network net, int timerID) {
	}

}
