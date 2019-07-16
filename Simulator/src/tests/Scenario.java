package tests;

import java.util.TreeMap;

import system.Simulator;
import system.utility.Debugger;
import system.utility.Keywords;
import system.utility.Mathematics;
import system.utility.OutputHandler;
import system.utility.Statistics;

public class Scenario {

	private int NetworkType;

	private float alpha = 1;

	/* Link Characteristics */
	// Link Bandwidth
	private float AccessLinkBandwidth;
	private float NetworkLinkBandwidth;
	private float controlLinkBandwidth;

	// Link Propagation Delay
	private float AccessLinkMinPropagationDelay;
	private float AccessLinkMaxPropagationDelay;

	private float NetworkLinkMinPropagationDelay;
	private float NetworkLinkMaxPropagationDelay;

	private float controlLinkMinPropagationDelay;
	private float controlLinkMaxPropagationDelay;

	/* Switch Properties */
	private int NumberOfAccessSwitches;
	private int NumberOfNetworkSwitches;

	/* Host Properties */
	private int MaxNumberOfHostsPerAccessSwitch;

	/* Flow Properties */
	private int MinFlowSize;
	private int MaxFlowSize;
	private int AverageFlowSize;
	private float minFlowInterArrivalTime;
	private float maxFlowInterArrivalTime;
	private float averageFlowInterArrivalTime;
	private float varianceFlowInterArrivalTime;

	/* Flow InterArrival Time Properties */

	public Scenario(int networkType, int topologyType) {
		this.NetworkType = networkType;
		switch (networkType) {
		case Keywords.WAN:
			this.AccessLinkBandwidth = (float) Mathematics.megaToBase(500);
			this.NetworkLinkBandwidth = (float) Mathematics.gigaToBase(1);
			this.AccessLinkMinPropagationDelay = 10;
			this.AccessLinkMaxPropagationDelay = 100;
			this.NetworkLinkMinPropagationDelay = (float) Mathematics.milliToMicro(10);
			this.NetworkLinkMaxPropagationDelay = (float) Mathematics.milliToMicro(100);
			this.MinFlowSize = (int) Mathematics.kiloToBase(1);
			this.MaxFlowSize = (int) Mathematics.kiloToBase(100);
			break;
		case Keywords.LAN:
			this.AccessLinkBandwidth = (float) Mathematics.megaToBase(500);
			this.NetworkLinkBandwidth = (float) Mathematics.gigaToBase(1);
			this.AccessLinkMinPropagationDelay = 1;
			this.AccessLinkMaxPropagationDelay = 10;
			this.NetworkLinkMinPropagationDelay = 10;
			this.NetworkLinkMaxPropagationDelay = 100;
			this.MinFlowSize = (int) Mathematics.kiloToBase(1);
			this.MaxFlowSize = (int) Mathematics.kiloToBase(10);
			break;
		default:
			break;
		}
		this.controlLinkBandwidth = this.AccessLinkBandwidth;
		this.controlLinkMinPropagationDelay = this.NetworkLinkMinPropagationDelay;
		this.controlLinkMaxPropagationDelay = this.NetworkLinkMaxPropagationDelay;

	}

	public void numberOfFlowsStudy(int minNumberOfFlows, int maxNumberOfFlows, int hops) {
		OutputHandler outputHandler = new OutputHandler();
		TreeMap<Integer, Statistics> StudyStats = new TreeMap<Integer, Statistics>();
		float simEndTime = Float.MAX_VALUE;
		int flowSize = this.MinFlowSize;
		float flowInterArrivalTime = 1; // TODO determine this by running single flow
		float flowArrivalTime;

		MaxNumberOfHostsPerAccessSwitch = 1;
		NumberOfAccessSwitches = 1;
		NumberOfNetworkSwitches = 1;

		for (int numberOfFlows = minNumberOfFlows; numberOfFlows <= maxNumberOfFlows; numberOfFlows += hops) {
			Simulator sim = new Simulator();
			for (int switchIndex = 0; switchIndex < NumberOfAccessSwitches; switchIndex++) {
				String senderAccessSwitchLabel = Keywords.SenderAccessSwitchPrefix + switchIndex;
				String receiverAccessSwitchLabel = Keywords.ReceiverAccessSwitchPrefix + switchIndex;
				sim.createSwitch(senderAccessSwitchLabel, controlLinkMinPropagationDelay, controlLinkBandwidth);
				sim.createSwitch(receiverAccessSwitchLabel, controlLinkMinPropagationDelay, controlLinkBandwidth);
				for (int hostIndex = 0; hostIndex < numberOfFlows; hostIndex++) {
					String senderHostLabel = Keywords.SenderHostPrefix + hostIndex;
					String receiverHostLabel = Keywords.ReceiverHostPrefix + hostIndex;
					String senderAccessLinkLabel = Keywords.SenderAccessLinkPrefix + hostIndex;
					String receiverAccessLinkLabel = Keywords.ReceiverAccessLinkPrefix + hostIndex;

					sim.createHost(senderHostLabel);
					sim.createAccessLink(senderAccessLinkLabel, senderHostLabel, senderAccessSwitchLabel,
							AccessLinkMinPropagationDelay, AccessLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);
					sim.createHost(receiverHostLabel);
					sim.createAccessLink(receiverAccessLinkLabel, receiverHostLabel, receiverAccessSwitchLabel,
							AccessLinkMinPropagationDelay, AccessLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);
				}
			}

			// Creating network Switch
			String networkSwitchLabel = Keywords.NetworkSwitchPrefix + 0;
			sim.createSwitch(networkSwitchLabel, controlLinkMinPropagationDelay, controlLinkBandwidth);

			// Connecting AccessSwitches to the NetworkSwitch
			String networkLinkLabel;
			int networkLinkIndex = 0;
			for (int accessSwitchIndex = 0; accessSwitchIndex < NumberOfAccessSwitches; accessSwitchIndex++) {
				networkLinkLabel = Keywords.NetworkLinkPrefix + networkLinkIndex;
				networkLinkIndex++;
				String senderAccessSwitchLabel = Keywords.SenderAccessSwitchPrefix + accessSwitchIndex;
				sim.createLink(networkLinkLabel, senderAccessSwitchLabel, networkSwitchLabel,
						NetworkLinkMinPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);
				networkLinkLabel = Keywords.NetworkLinkPrefix + networkLinkIndex;
				networkLinkIndex++;
				String receiverAccessSwitchLabel = Keywords.ReceiverAccessSwitchPrefix + accessSwitchIndex;
				sim.createLink(networkLinkLabel, networkSwitchLabel, receiverAccessSwitchLabel,
						NetworkLinkMinPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);

			}
			String controllerLabel = Keywords.ControllerPrefix + 0;
			sim.createController(controllerLabel, alpha, Keywords.Dijkstra);
			flowArrivalTime = 0;
			for (int flowIndex = 0; flowIndex < numberOfFlows; flowIndex++) {
				String flowLabel = Keywords.FlowPrefix + flowIndex;
				String senderHostLabel = Keywords.SenderHostPrefix + flowIndex;
				String receiverHostLabel = Keywords.ReceiverHostPrefix + flowIndex;
				sim.generateFlow(flowLabel, Keywords.SDTCP, senderHostLabel, receiverHostLabel, flowSize,
						flowArrivalTime);
				flowArrivalTime += flowInterArrivalTime;
			}
			StudyStats.put(numberOfFlows, sim.run(0, simEndTime));
			// Debugger Output
			Debugger.debugOutPut();
		}
		outputHandler.outStatsforNumberOfFlows(StudyStats);

	}

}
