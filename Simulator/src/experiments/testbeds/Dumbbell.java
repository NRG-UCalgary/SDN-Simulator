package experiments.testbeds;

import java.util.TreeMap;

import experiments.traffic.Traffic;
import simulator.Simulator;
import utility.*;

public class Dumbbell extends Testbed {

	public Dumbbell(short networkType) {
		super(networkType);
	}

	public Statistics executeSimulation(Traffic traffic) {
		int controlLinkIndex = 0;
		String controllerLabel = Keywords.Entities.Labels.Prefixes.ControllerPrefix + "0";
		NumberOfSenderAccessSwitches = 1;
		NumberOfNetworkSwitches = 1;
		NumberOfHostsPerAccessSwitch = traffic.flowSizePerFlowID.size();
		TreeMap<Integer, Float> accessLinkPropagationDelayPerFlowID = prepareAccessLinksPropagationDelay(
				AccessLinkPropagationDelayDistribution, NumberOfHostsPerAccessSwitch);
		Debugger.debugToConsole("    ^^^^^^^^^^^^^ New Simulation ^^^^^^^^^^^^^");
		Simulator sim = new Simulator();

		// Creating the controller
		sim.createController(controllerLabel, Keywords.Entities.Controllers.Types.Controller_1, alpha, 1);

		// Creating access switches
		for (int acessSwitchIndex = 0; acessSwitchIndex < NumberOfSenderAccessSwitches; acessSwitchIndex++) {
			String senderAccessSwitchLabel = Keywords.Entities.Labels.Prefixes.SenderAccessSwitchPrefix
					+ acessSwitchIndex;
			String receiverAccessSwitchLabel = Keywords.Entities.Labels.Prefixes.ReceiverAccessSwitchPrefix
					+ acessSwitchIndex;
			sim.createSwitch(senderAccessSwitchLabel, Keywords.Entities.Switches.Types.Switch_1);
			sim.createSwitch(receiverAccessSwitchLabel, Keywords.Entities.Switches.Types.Switch_1);

			// Creating control links for the access switches
			sim.createLink(Keywords.Entities.Labels.Prefixes.ControlLinkPrefix + controlLinkIndex,
					senderAccessSwitchLabel, controllerLabel, controlLinkPropagationDelay, controlLinkBandwidth,
					Keywords.Entities.Buffers.Size.Unlimited, false);
			controlLinkIndex++;
			sim.createLink(Keywords.Entities.Labels.Prefixes.ControlLinkPrefix + controlLinkIndex,
					receiverAccessSwitchLabel, controllerLabel, controlLinkPropagationDelay, controlLinkBandwidth,
					Keywords.Entities.Buffers.Size.Unlimited, false);
			controlLinkIndex++;

			// Creating hosts and connecting them to access switches
			for (int hostIndex = 0; hostIndex < NumberOfHostsPerAccessSwitch; hostIndex++) {
				String senderHostLabel = Keywords.Entities.Labels.Prefixes.SenderHostPrefix + hostIndex;
				String receiverHostLabel = Keywords.Entities.Labels.Prefixes.ReceiverHostPrefix + hostIndex;
				String senderAccessLinkLabel = Keywords.Entities.Labels.Prefixes.SenderAccessLinkPrefix + hostIndex;
				String receiverAccessLinkLabel = Keywords.Entities.Labels.Prefixes.ReceiverAccessLinkPrefix + hostIndex;

				sim.createHost(senderHostLabel);
				sim.createLink(senderAccessLinkLabel, senderHostLabel, senderAccessSwitchLabel,
						accessLinkPropagationDelayPerFlowID.get(hostIndex), AccessLinkBandwidth, Integer.MAX_VALUE,
						false);
				sim.createHost(receiverHostLabel);
				sim.createLink(receiverAccessLinkLabel, receiverHostLabel, receiverAccessSwitchLabel,
						ReceiverAccessLinkPropagationDelay, AccessLinkBandwidth, Integer.MAX_VALUE, false);
			}
		}

		// Creating network Switch
		String networkSwitchLabel;
		for (int networkSwitchIndex = 0; networkSwitchIndex < NumberOfNetworkSwitches; networkSwitchIndex++) {
			networkSwitchLabel = Keywords.Entities.Labels.Prefixes.NetworkSwitchPrefix + networkSwitchIndex;
			sim.createSwitch(networkSwitchLabel, Keywords.Entities.Switches.Types.Switch_1);

			sim.createLink(Keywords.Entities.Labels.Prefixes.ControlLinkPrefix + controlLinkIndex, networkSwitchLabel,
					controllerLabel, controlLinkPropagationDelay, controlLinkBandwidth,
					Keywords.Entities.Buffers.Size.Unlimited, false);
			controlLinkIndex++;
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
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Keywords.Entities.Buffers.Size.Unlimited, true);
			networkLinkLabel = Keywords.Entities.Labels.Prefixes.NetworkLinkPrefix + networkLinkIndex;
			networkLinkIndex++;
			String receiverAccessSwitchLabel = Keywords.Entities.Labels.Prefixes.ReceiverAccessSwitchPrefix
					+ accessSwitchIndex;
			sim.createLink(networkLinkLabel, lastNetworkSwitchLabel, receiverAccessSwitchLabel,
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Keywords.Entities.Buffers.Size.Unlimited, false);
		}

		// Creating the flows
		for (int flowIndex : traffic.arrivalTimePerFlowID.keySet()) {
			Debugger.debugToConsole("    Flow_" + flowIndex + " with size: " + traffic.flowSizePerFlowID.get(flowIndex)
					+ " arrives at: " + traffic.arrivalTimePerFlowID.get(flowIndex));
			String flowLabel = Keywords.Entities.Labels.Prefixes.FlowPrefix + flowIndex;
			String senderHostLabel = Keywords.Entities.Labels.Prefixes.SenderHostPrefix + flowIndex;
			String receiverHostLabel = Keywords.Entities.Labels.Prefixes.ReceiverHostPrefix + flowIndex;
			sim.generateFlow(flowLabel, senderHostLabel, receiverHostLabel, traffic.flowSizePerFlowID.get(flowIndex),
					traffic.arrivalTimePerFlowID.get(flowIndex), Keywords.Entities.Agents.Types.SDTCP);
		}
		// Running the simulation
		return sim.run(0, SimEndTime);
	}

}
