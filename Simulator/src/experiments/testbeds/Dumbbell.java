package experiments.testbeds;

import java.util.TreeMap;

import experiments.traffic.Traffic;
import system.Main;
import system.Simulator;
import system.utility.*;

public class Dumbbell extends Testbed {

	public Dumbbell(int networkType) {
		super(networkType);
	}

	public Statistics executeSimulation(Traffic traffic) {
		NumberOfSenderAccessSwitches = 1;
		NumberOfNetworkSwitches = 1;
		NumberOfHostsPerAccessSwitch = traffic.FlowSizePerFlowID.size();
		TreeMap<Integer, Float> accessLinkPropagationDelayPerFlowID = prepareAccessLinksPropagationDelay(
				AccessLinkPropagationDelayDistribution, NumberOfHostsPerAccessSwitch);
		Main.print("======================== New Simulation ==========================");
		Simulator sim = new Simulator();
		// Creating access switches
		for (int acessSwitchIndex = 0; acessSwitchIndex < NumberOfSenderAccessSwitches; acessSwitchIndex++) {
			String senderAccessSwitchLabel = Keywords.Inputs.Entities.Labels.Prefixes.SenderAccessSwitchPrefix
					+ acessSwitchIndex;
			String receiverAccessSwitchLabel = Keywords.Inputs.Entities.Labels.Prefixes.ReceiverAccessSwitchPrefix
					+ acessSwitchIndex;
			sim.createSwitch(senderAccessSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);
			sim.createSwitch(receiverAccessSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);

			// Creating hosts and connecting them to access switches
			for (int hostIndex = 0; hostIndex < NumberOfHostsPerAccessSwitch; hostIndex++) {
				String senderHostLabel = Keywords.Inputs.Entities.Labels.Prefixes.SenderHostPrefix + hostIndex;
				String receiverHostLabel = Keywords.Inputs.Entities.Labels.Prefixes.ReceiverHostPrefix + hostIndex;
				String senderAccessLinkLabel = Keywords.Inputs.Entities.Labels.Prefixes.SenderAccessLinkPrefix
						+ hostIndex;
				String receiverAccessLinkLabel = Keywords.Inputs.Entities.Labels.Prefixes.ReceiverAccessLinkPrefix
						+ hostIndex;

				sim.createHost(senderHostLabel);
				sim.createAccessLink(senderAccessLinkLabel, senderHostLabel, senderAccessSwitchLabel,
						accessLinkPropagationDelayPerFlowID.get(hostIndex), AccessLinkBandwidth, Integer.MAX_VALUE,
						Keywords.Operations.Buffers.Policy.FIFO);
				sim.createHost(receiverHostLabel);
				sim.createAccessLink(receiverAccessLinkLabel, receiverHostLabel, receiverAccessSwitchLabel,
						ReceiverAccessLinkPropagationDelay, AccessLinkBandwidth, Integer.MAX_VALUE,
						Keywords.Operations.Buffers.Policy.FIFO);
			}
		}

		// Creating network Switch
		String networkSwitchLabel;
		for (int networkSwitchIndex = 0; networkSwitchIndex < NumberOfNetworkSwitches; networkSwitchIndex++) {
			networkSwitchLabel = Keywords.Inputs.Entities.Labels.Prefixes.NetworkSwitchPrefix + networkSwitchIndex;
			sim.createSwitch(networkSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);
		}

		// Connecting AccessSwitches to the NetworkSwitch
		String firstNetworkSwitchLabel = Keywords.Inputs.Entities.Labels.Prefixes.NetworkSwitchPrefix + 0;
		String lastNetworkSwitchLabel = Keywords.Inputs.Entities.Labels.Prefixes.NetworkSwitchPrefix
				+ (NumberOfNetworkSwitches - 1);
		String networkLinkLabel;
		int networkLinkIndex = 0;
		for (int accessSwitchIndex = 0; accessSwitchIndex < NumberOfSenderAccessSwitches; accessSwitchIndex++) {
			networkLinkLabel = Keywords.Inputs.Entities.Labels.Prefixes.NetworkLinkPrefix + networkLinkIndex;
			networkLinkIndex++;
			String senderAccessSwitchLabel = Keywords.Inputs.Entities.Labels.Prefixes.SenderAccessSwitchPrefix
					+ accessSwitchIndex;
			sim.createLink(networkLinkLabel, senderAccessSwitchLabel, firstNetworkSwitchLabel,
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE,
					Keywords.Operations.Buffers.Policy.FIFO);
			networkLinkLabel = Keywords.Inputs.Entities.Labels.Prefixes.NetworkLinkPrefix + networkLinkIndex;
			networkLinkIndex++;
			String receiverAccessSwitchLabel = Keywords.Inputs.Entities.Labels.Prefixes.ReceiverAccessSwitchPrefix
					+ accessSwitchIndex;
			sim.createLink(networkLinkLabel, lastNetworkSwitchLabel, receiverAccessSwitchLabel,
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE,
					Keywords.Operations.Buffers.Policy.FIFO);
		}

		// Creating the controller
		String controllerLabel = Keywords.Inputs.Entities.Labels.Prefixes.ControllerPrefix + 0;
		sim.createController(controllerLabel, alpha, Keywords.Operations.RoutingAlgorithms.Dijkstra);

		// Creating the flows
		for (int flowIndex : traffic.ArrivalTimePerFlowID.keySet()) {
			Debugger.debugToConsole("    Flow_" + flowIndex + " with size: " + traffic.FlowSizePerFlowID.get(flowIndex)
					+ " arrives at: " + traffic.ArrivalTimePerFlowID.get(flowIndex));
			String flowLabel = Keywords.Inputs.Entities.Labels.Prefixes.FlowPrefix + flowIndex;
			String senderHostLabel = Keywords.Inputs.Entities.Labels.Prefixes.SenderHostPrefix + flowIndex;
			String receiverHostLabel = Keywords.Inputs.Entities.Labels.Prefixes.ReceiverHostPrefix + flowIndex;
			sim.generateFlow(flowLabel, Keywords.Operations.Agents.Types.SDTCP, senderHostLabel, receiverHostLabel,
					traffic.FlowSizePerFlowID.get(flowIndex), traffic.ArrivalTimePerFlowID.get(flowIndex));
		}

		// Debugger output
		Debugger.debugOutPut();

		// Running the simulation
		return sim.run(0, SimEndTime);
	}

}
