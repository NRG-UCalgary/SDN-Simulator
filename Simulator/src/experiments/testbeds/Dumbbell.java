package experiments.testbeds;

import java.util.TreeMap;

import experiments.traffic.Traffic;
import system.Simulator;
import system.utility.*;

public class Dumbbell extends Testbed {

	public Dumbbell(short networkType) {
		super(networkType);
	}

	public Statistics executeSimulation(Traffic traffic) {
		NumberOfSenderAccessSwitches = 1;
		NumberOfNetworkSwitches = 1;
		NumberOfHostsPerAccessSwitch = traffic.flowSizePerFlowID.size();
		TreeMap<Integer, Float> accessLinkPropagationDelayPerFlowID = prepareAccessLinksPropagationDelay(
				AccessLinkPropagationDelayDistribution, NumberOfHostsPerAccessSwitch);
		Debugger.debugToConsole("    ^^^^^^^^^^^^^ New Simulation ^^^^^^^^^^^^^");
		Simulator sim = new Simulator();
		// Creating access switches
		for (int acessSwitchIndex = 0; acessSwitchIndex < NumberOfSenderAccessSwitches; acessSwitchIndex++) {
			String senderAccessSwitchLabel = Keywords.Entities.Labels.Prefixes.SenderAccessSwitchPrefix
					+ acessSwitchIndex;
			String receiverAccessSwitchLabel = Keywords.Entities.Labels.Prefixes.ReceiverAccessSwitchPrefix
					+ acessSwitchIndex;
			sim.createSwitch(senderAccessSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);
			sim.createSwitch(receiverAccessSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);

			// Creating hosts and connecting them to access switches
			for (int hostIndex = 0; hostIndex < NumberOfHostsPerAccessSwitch; hostIndex++) {
				String senderHostLabel = Keywords.Entities.Labels.Prefixes.SenderHostPrefix + hostIndex;
				String receiverHostLabel = Keywords.Entities.Labels.Prefixes.ReceiverHostPrefix + hostIndex;
				String senderAccessLinkLabel = Keywords.Entities.Labels.Prefixes.SenderAccessLinkPrefix + hostIndex;
				String receiverAccessLinkLabel = Keywords.Entities.Labels.Prefixes.ReceiverAccessLinkPrefix + hostIndex;

				sim.createHost(senderHostLabel);
				sim.createAccessLink(senderAccessLinkLabel, senderHostLabel, senderAccessSwitchLabel,
						accessLinkPropagationDelayPerFlowID.get(hostIndex), AccessLinkBandwidth, Integer.MAX_VALUE,
						Keywords.Buffers.Policy.FIFO);
				sim.createHost(receiverHostLabel);
				sim.createAccessLink(receiverAccessLinkLabel, receiverHostLabel, receiverAccessSwitchLabel,
						ReceiverAccessLinkPropagationDelay, AccessLinkBandwidth, Integer.MAX_VALUE,
						Keywords.Buffers.Policy.FIFO);
			}
		}

		// Creating network Switch
		String networkSwitchLabel;
		for (int networkSwitchIndex = 0; networkSwitchIndex < NumberOfNetworkSwitches; networkSwitchIndex++) {
			networkSwitchLabel = Keywords.Entities.Labels.Prefixes.NetworkSwitchPrefix + networkSwitchIndex;
			sim.createSwitch(networkSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);
		}

		// Connecting AccessSwitches to the NetworkSwitch
		String firstNetworkSwitchLabel = Keywords.Entities.Labels.Prefixes.NetworkSwitchPrefix + 0;
		String lastNetworkSwitchLabel = Keywords.Entities.Labels.Prefixes.NetworkSwitchPrefix
				+ (NumberOfNetworkSwitches - 1);
		String networkLinkLabel;
		int networkLinkIndex = 0;
		for (int accessSwitchIndex = 0; accessSwitchIndex < NumberOfSenderAccessSwitches; accessSwitchIndex++) {
			networkLinkLabel = Keywords.Entities.Labels.Prefixes.NetworkLinkPrefix + networkLinkIndex;
			networkLinkIndex++;
			String senderAccessSwitchLabel = Keywords.Entities.Labels.Prefixes.SenderAccessSwitchPrefix
					+ accessSwitchIndex;
			sim.createLink(networkLinkLabel, senderAccessSwitchLabel, firstNetworkSwitchLabel,
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE, Keywords.Buffers.Policy.FIFO,
					true);
			networkLinkLabel = Keywords.Entities.Labels.Prefixes.NetworkLinkPrefix + networkLinkIndex;
			networkLinkIndex++;
			String receiverAccessSwitchLabel = Keywords.Entities.Labels.Prefixes.ReceiverAccessSwitchPrefix
					+ accessSwitchIndex;
			sim.createLink(networkLinkLabel, lastNetworkSwitchLabel, receiverAccessSwitchLabel,
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE, Keywords.Buffers.Policy.FIFO,
					false);
		}

		// Creating the controller
		String controllerLabel = Keywords.Entities.Labels.Prefixes.ControllerPrefix + 0;
		sim.createController(controllerLabel, alpha, Keywords.RoutingAlgorithms.Dijkstra);

		// Creating the flows
		for (int flowIndex : traffic.arrivalTimePerFlowID.keySet()) {
			Debugger.debugToConsole("    Flow_" + flowIndex + " with size: " + traffic.flowSizePerFlowID.get(flowIndex)
					+ " arrives at: " + traffic.arrivalTimePerFlowID.get(flowIndex));
			String flowLabel = Keywords.Entities.Labels.Prefixes.FlowPrefix + flowIndex;
			String senderHostLabel = Keywords.Entities.Labels.Prefixes.SenderHostPrefix + flowIndex;
			String receiverHostLabel = Keywords.Entities.Labels.Prefixes.ReceiverHostPrefix + flowIndex;
			sim.generateFlow(flowLabel, Keywords.Agents.Types.SDTCP, senderHostLabel, receiverHostLabel,
					traffic.flowSizePerFlowID.get(flowIndex), traffic.arrivalTimePerFlowID.get(flowIndex));
		}
		// Running the simulation
		return sim.run(0, SimEndTime);
	}

}
