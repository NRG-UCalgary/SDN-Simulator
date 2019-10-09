package simulator.entities.network.controllers;

import simulator.*;
import simulator.entities.network.*;
import simulator.entities.traffic.*;
import system.Main;
import utility.*;

public class Controllerv2 extends Controller {

	private float alpha; // sWnd
	private float beta; // SYNACKDelay
	private float gamma; // sInterval

	// Remedy
	private float mostRecentCycleStartTime = 0;

	private boolean validationReport = false;
	private int sCycleIndex = -1;

	public Controllerv2(int ID, short routingAlgorithm, float alpha, float beta, float gamma) {
		super(ID, routingAlgorithm);
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		Debugger.debugToConsole("Alpha = " + alpha + ", beta = " + beta + ", gamma = " + gamma);
	}

	@Override
	public void recvPacket(Network net, int switchID, Packet packet) {
		currentNetwork = net;
		recvdSegment = packet.segment;
		database.accessSwitchID = switchID;
		switch (recvdSegment.getType()) {
		case Keywords.Segments.Types.SYN:
			if (validationReport) {
				Debugger.debugToConsole("+++++++++++++++++++++++++++++++++++++++++++++++");
				Debugger.debugToConsole("SYN of flowID = " + recvdSegment.getFlowID());
			}
			handleRouting(net, switchID, getAccessSwitchID(net, recvdSegment.getDstHostID()));
			database.arrivalOfSYNForFlowID(net, recvdSegment, switchID);
			handleCongestionControl(net, recvdSegment);
			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			if (validationReport) {
				Debugger.debugToConsole("+++++++++++++++++++++++++++++++++++++++++++++++");
				Debugger.debugToConsole("FIN of flowID = " + recvdSegment.getFlowID());
			}
			// Update the database
			database.arrivalOfFINForFlowID(switchID, recvdSegment.getFlowID(), recvdSegment.getSrcHostID());
			if (database.flowIDOfHostID.keySet().size() > 0) {
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
		if (validationReport) {
			Debugger.debugToConsole("----------------------------------------");
			Debugger.debugToConsole("---------- sCycle_" + sCycleIndex + " ---------------------");
			Debugger.debugToConsole("---------- Time: " + net.getCurrentTime() + " -------------------");
			Debugger.debugToConsole("----------------------------------------");
		}
		Link controlLink = net.links.get(controlLinksIDs.get(database.accessSwitchID));
		float sCycleStartDelay = calculateSCycleStartDelay(net, controlLink);
		float sInterval = calculateSInterval();
		if (validationReport) {
			Debugger.debugToConsole("sCycleStartDelay = " + sCycleStartDelay);
			Debugger.debugToConsole("sInterval = " + sInterval);
		}
		int interFlowIndex = 0;
		float accessLinkDelay_0 = 0;
		for (int hostID : database.flowIDOfHostIDOfAccessSwitchID.get(database.accessSwitchID).keySet()) {
			int flowID = database.flowIDOfHostIDOfAccessSwitchID.get(database.accessSwitchID).get(hostID);
			float accessLinkDelay_i = database.accessLinkOfFlowID.get(flowID)
					.getTotalDelay(Keywords.Segments.Sizes.DataSegSize);
			if (interFlowIndex == 0) {
				accessLinkDelay_0 = accessLinkDelay_i;
			}
			Segment segment = new Segment(Simulator.reverseFlowStreamID(flowID), Keywords.Segments.Types.CTRL,
					Keywords.Segments.SpecialSequenceNumbers.CTRLSeqNum, Keywords.Segments.Sizes.CTRLSegSize, this.ID,
					hostID);
			if (validationReport) {
				Debugger.debugToConsole("  +++++++++++++++++++");
				Debugger.debugToConsole("  FlowID = " + flowID);
			}
			// -------------------------------------
			// Calculate sWnd
			segment.sWnd = calculateFlowSWnd(sInterval,
					net.links.get(database.btlLinkIDOfFlowID.get(flowID)).getBandwidth());
			if (validationReport) {
				Debugger.debugToConsole("  sWnd = " + segment.sWnd);
			}
			 //Debugger.debugToConsole(" sWnd = " + segment.sWnd);
			 //Debugger.stopFlag();
			if (segment.sWnd < 1) {
				Debugger.stopFlag();
			}

			if (segment.sWnd == 1) {
				// Debugger.debugToConsole(" sWnd = " + segment.sWnd);
				// Debugger.stopFlag();
			}
			// -------------------------------------
			// Calculate sInterSegmentDelay = transmissionDelay for flowBtlBw
			segment.sInterSegmentDelay = net.links.get(database.btlLinkIDOfFlowID.get(flowID))
					.getTransmissionDelay(Keywords.Segments.Sizes.DataSegSize);
			if (validationReport) {
				Debugger.debugToConsole("  sInterSegmentDelay = " + segment.sInterSegmentDelay);
			}
			if (segment.sInterSegmentDelay < 0) {
				Main.error("Controllerv2", "handleCongestionControl",
						"Invalid sInterSegmentDelay = " + segment.sInterSegmentDelay);
				Debugger.stopFlag();
			}
			// -------------------------------------
			// Calculate delayToNextCycle_i
			float CTRLDelay_i = calculateCTRLDelayOfFlowID(net, controlLink, flowID, interFlowIndex);
			float delayToNextCycle_i = Mathematics.subtractFloat(sCycleStartDelay, CTRLDelay_i);
			segment.timeToNextCycle = delayToNextCycle_i;
			if (validationReport) {
				Debugger.debugToConsole("  CTRLDelay_i = " + CTRLDelay_i);
				Debugger.debugToConsole("  delayToNextCycle_i = " + delayToNextCycle_i);
			}
			if (delayToNextCycle_i <= 0) {
				Main.error("Controllerv2", "handleCongestionControl",
						"Invalid delayToNextCycle = " + delayToNextCycle_i);
				Debugger.stopFlag();
			}
			// -------------------------------------
			// Calculate sInitialDelay
			float numi = Mathematics.multiplyFloat(interFlowIndex, sInterval);
			float initialDelay_i = Mathematics.divideFloat(numi, database.flowIDOfHostID.size());
			if (interFlowIndex > 0) {
				initialDelay_i = Mathematics.addFloat(initialDelay_i,
						Mathematics.subtractFloat(accessLinkDelay_0, accessLinkDelay_i));
			}
			segment.sInitialDelay = initialDelay_i;
			if (validationReport) {
				Debugger.debugToConsole("  initialDelay_i = " + initialDelay_i);
			}
			if (initialDelay_i < 0) {
				Main.error("Controllerv2", "handleCongestionControl", "Invalid initialDelay = " + initialDelay_i);
				Debugger.stopFlag();
			}
			// -------------------------------------
			segment.sInterval = sInterval;
			if (sInterval <= 0) {
				Main.error("Controllerv2", "handleCongestionControl", "Invalid sInterval = " + sInterval);
				Debugger.stopFlag();
			}
			interFlowIndex++;
			sendPacketToSwitch(net, database.accessSwitchID, new Packet(segment, null));

		}
	}

	private float calculateSCycleStartDelay(Network net, Link controlLink) {
		float sCycleStartDelay = 0;
		switch (recvdSegment.getType()) {
		case Keywords.Segments.Types.SYN:
			sCycleStartDelay = calculateSYNACKDelay(net,
					controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
					controlLink.getPropagationDelay(), database.accessLinkOfFlowID.get(recvdSegment.getFlowID())
							.getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize));
			break;
		case Keywords.Segments.Types.UncontrolledFIN:
			sCycleStartDelay = calclateMaxCTRLDelay(
					controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
					controlLink.getPropagationDelay());
			break;
		default:
			Main.error("Controllerv2", "handleCongestionControl", "Invalid recvdSegment type.");
			Debugger.stopFlag();
			break;
		}
		sCycleStartDelay = Mathematics.addFloat(sCycleStartDelay,
				calclateMaxCTRLDelay(controlLink.getTransmissionDelay(Keywords.Segments.Sizes.CTRLSegSize),
						controlLink.getPropagationDelay()));
		float sCycleStartTime = Mathematics.addFloat(net.getCurrentTime(), sCycleStartDelay);
		if (validationReport) {
			Debugger.debugToConsole("      sCycleStartDelay = " + sCycleStartDelay);
			Debugger.debugToConsole("      sCycleStartTime = " + sCycleStartTime);
			Debugger.debugToConsole("      mostRecentCycleStartTime = " + mostRecentCycleStartTime);
		}
		if (mostRecentCycleStartTime > sCycleStartTime) {
			sCycleStartTime = mostRecentCycleStartTime;
			sCycleStartDelay = Mathematics.subtractFloat(mostRecentCycleStartTime, net.getCurrentTime());
		} else {
			mostRecentCycleStartTime = sCycleStartTime;
		}
		if (validationReport) {
			Debugger.debugToConsole("      AFTER  sCycleStartTime = " + sCycleStartTime);
			Debugger.debugToConsole("      AFTER  sCycleStartDelay = " + sCycleStartDelay);
		}
		return sCycleStartDelay;
	}

	private float calclateMaxCTRLDelay(float controlLinkTransDelayCTRLSize, float controlLinkPropagationDelay) {
		float maxCTRLDelay = -1;
		int flowIndex = 0;
		for (int hostID : database.flowIDOfHostIDOfAccessSwitchID.get(database.accessSwitchID).keySet()) {
			int flowID = database.flowIDOfHostID.get(hostID);
			float CTRLDelay = 0;
			Link accessLink = database.accessLinkOfFlowID.get(flowID);
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
		for (int hostID : database.flowIDOfHostIDOfAccessSwitchID.get(database.accessSwitchID).keySet()) {
			int flowID = database.flowIDOfHostID.get(hostID);
			ctrlDelay = calculateCTRLDelayOfFlowID(currentNetwork,
					currentNetwork.links.get(controlLinksIDs.get(database.accessSwitchID)), flowID, flowIndex);
			if (ctrlDelay > maxCTRLDelay) {
				maxCTRLDelay = ctrlDelay;
			}
			flowIndex++;
		}
		if (validationReport) {
			Debugger.debugToConsole("maxCTRLDelay = " + maxCTRLDelay);
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
		float ALTotalDelay = database.accessLinkOfFlowID.get(flowID).getTotalDelay(Keywords.Segments.Sizes.CTRLSegSize);
		float ALQDelay = database.accessLinkOfFlowID.get(flowID)
				.getTransmissionDelay(Keywords.Segments.Sizes.ACKSegSize);
		ALTotalDelay = Mathematics.addFloat(ALTotalDelay, ALQDelay);
		float CTRLDelay = Mathematics.addFloat(CLTotalDelay, ALTotalDelay);
		return CTRLDelay;
	}

	private float calculateSInterval() {
		float maxRTT = Mathematics.multiplyFloat(gamma, database.maxRTTOfAccessSwitchID.get(database.accessSwitchID));
		return maxRTT;
	}

	private float calculateSYNACKDelay(Network net, float CLTransDelayCTRLSize, float CLPropDelay,
			float accessLinkTotalDelaySYNSize) {
		Link networkBtlLink = net.links.get(database.networkBtlLinkIDOfFlowID.get(recvdSegment.getFlowID()));
		float networkBtlTransDelay_Data = networkBtlLink.getTransmissionDelay(Keywords.Segments.Sizes.DataSegSize);
		float networkBtlTransDelay_ACK = networkBtlLink.getTransmissionDelay(Keywords.Segments.Sizes.ACKSegSize);
		int numberOfFlwos = database.getNumberOfFlowsForAccessSwitch(database.accessSwitchID);

		float CLQDelay_i = Mathematics.multiplyFloat(numberOfFlwos + 1, CLTransDelayCTRLSize);
		CLQDelay_i = Mathematics.multiplyFloat(numberOfFlwos, CLQDelay_i);
		float CLTotalDelay = Mathematics.addFloat(CLQDelay_i, Mathematics.addFloat(CLTransDelayCTRLSize, CLPropDelay));
		float synRtt_i = database.SYNRTTOfFlowID.get(recvdSegment.getFlowID());
		float synRtt_Queue = Mathematics.addFloat(networkBtlTransDelay_Data, networkBtlTransDelay_ACK);
		synRtt_i = Mathematics.addFloat(synRtt_i, synRtt_Queue);
		float SYNACKDelay = Mathematics.addFloat(synRtt_i, CLTotalDelay);
		SYNACKDelay = Mathematics.multiplyFloat(beta, SYNACKDelay);
		SYNACKDelay = Mathematics.subtractFloat(SYNACKDelay, accessLinkTotalDelaySYNSize);
		if (validationReport) {
			Debugger.debugToConsole("SYNACLDelay = " + SYNACKDelay);
		}
		return SYNACKDelay;
	}

	private int calculateFlowSWnd(float sInterval, float flowBtlBw) {
		float numinator = Mathematics.multiplyFloat(sInterval, flowBtlBw);
		float denuminator = Mathematics.multiplyFloat(Keywords.Segments.Sizes.DataSegSize,
				database.getNumberOfFlowsForAccessSwitch(database.accessSwitchID));
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

	/**********************************************************************/
	/*********************** Deprecated Methods ***************************/
	/**********************************************************************/

}
