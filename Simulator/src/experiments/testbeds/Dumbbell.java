package experiments.testbeds;

import java.util.TreeMap;

import experiments.traffic.Traffic;
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
		Simulator sim = new Simulator();
		// Creating access switches
		for (int acessSwitchIndex = 0; acessSwitchIndex < NumberOfSenderAccessSwitches; acessSwitchIndex++) {
			String senderAccessSwitchLabel = Keywords.SenderAccessSwitchPrefix + acessSwitchIndex;
			String receiverAccessSwitchLabel = Keywords.ReceiverAccessSwitchPrefix + acessSwitchIndex;
			sim.createSwitch(senderAccessSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);
			sim.createSwitch(receiverAccessSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);

			// Creating hosts and connecting them to access switches
			for (int hostIndex = 0; hostIndex < NumberOfHostsPerAccessSwitch; hostIndex++) {
				String senderHostLabel = Keywords.SenderHostPrefix + hostIndex;
				String receiverHostLabel = Keywords.ReceiverHostPrefix + hostIndex;
				String senderAccessLinkLabel = Keywords.SenderAccessLinkPrefix + hostIndex;
				String receiverAccessLinkLabel = Keywords.ReceiverAccessLinkPrefix + hostIndex;

				sim.createHost(senderHostLabel);
				sim.createAccessLink(senderAccessLinkLabel, senderHostLabel, senderAccessSwitchLabel,
						accessLinkPropagationDelayPerFlowID.get(hostIndex), AccessLinkBandwidth, Integer.MAX_VALUE,
						Keywords.FIFO);
				sim.createHost(receiverHostLabel);
				sim.createAccessLink(receiverAccessLinkLabel, receiverHostLabel, receiverAccessSwitchLabel,
						ReceiverAccessLinkPropagationDelay, AccessLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);
			}
		}

		// Creating network Switch
		String networkSwitchLabel;
		for (int networkSwitchIndex = 0; networkSwitchIndex < NumberOfNetworkSwitches; networkSwitchIndex++) {
			networkSwitchLabel = Keywords.NetworkSwitchPrefix + networkSwitchIndex;
			sim.createSwitch(networkSwitchLabel, controlLinkPropagationDelay, controlLinkBandwidth);
		}

		// Connecting AccessSwitches to the NetworkSwitch
		String firstNetworkSwitchLabel = Keywords.NetworkSwitchPrefix + 0;
		String lastNetworkSwitchLabel = Keywords.NetworkSwitchPrefix + (NumberOfNetworkSwitches - 1);
		String networkLinkLabel;
		int networkLinkIndex = 0;
		for (int accessSwitchIndex = 0; accessSwitchIndex < NumberOfSenderAccessSwitches; accessSwitchIndex++) {
			networkLinkLabel = Keywords.NetworkLinkPrefix + networkLinkIndex;
			networkLinkIndex++;
			String senderAccessSwitchLabel = Keywords.SenderAccessSwitchPrefix + accessSwitchIndex;
			sim.createLink(networkLinkLabel, senderAccessSwitchLabel, firstNetworkSwitchLabel,
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);
			networkLinkLabel = Keywords.NetworkLinkPrefix + networkLinkIndex;
			networkLinkIndex++;
			String receiverAccessSwitchLabel = Keywords.ReceiverAccessSwitchPrefix + accessSwitchIndex;
			sim.createLink(networkLinkLabel, lastNetworkSwitchLabel, receiverAccessSwitchLabel,
					NetworkLinkPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);
		}

		// Creating the controller
		String controllerLabel = Keywords.ControllerPrefix + 0;
		sim.createController(controllerLabel, alpha, Keywords.Dijkstra);

		// Creating the flows
		for (int flowIndex : traffic.ArrivalTimePerFlowID.keySet()) {
			Debugger.debugToConsole("    Flow_" + flowIndex + " with size: " + traffic.FlowSizePerFlowID.get(flowIndex)
					+ " arrives at: " + traffic.ArrivalTimePerFlowID.get(flowIndex));
			String flowLabel = Keywords.FlowPrefix + flowIndex;
			String senderHostLabel = Keywords.SenderHostPrefix + flowIndex;
			String receiverHostLabel = Keywords.ReceiverHostPrefix + flowIndex;
			sim.generateFlow(flowLabel, Keywords.SDTCP, senderHostLabel, receiverHostLabel,
					traffic.FlowSizePerFlowID.get(flowIndex), traffic.ArrivalTimePerFlowID.get(flowIndex));
		}

		// Debugger output
		Debugger.debugOutPut();

		// Running the simulation
		return sim.run(0, SimEndTime);
	}

}
