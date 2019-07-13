package tests;

import system.Simulator;
import system.utility.Debugger;
import system.utility.Keywords;
import system.utility.Mathematics;
import system.utility.Statistics;

public class Scenario {

	private int NetworkType;

	/* Link Characteristics */
	// Link Bandwidth
	private double AccessLinkBandwidth;
	private double NetworkLinkBandwidth;
	private double controlLinkBandwidth;

	// Link Propagation Delay
	private double AccessLinkMinPropagationDelay;
	private double AccessLinkMaxPropagationDelay;

	private double NetworkLinkMinPropagationDelay;
	private double NetworkLinkMaxPropagationDelay;

	private double controlLinkMinPropagationDelay;
	private double controlLinkMaxPropagationDelay;

	/* Switch Properties */
	private int NumberOfAccessSwitches;
	private int NumberOfNetworkSwitches;

	/* Host Properties */
	private int MaxNumberOfHostsPerAccessSwitch;

	/* Flow Properties */
	private int MinFlowSize;
	private int MaxFlowSize;
	private int AverageFlowSize;

	/* Flow InterArrival Time Properties */

	public Scenario(int networkType) {
		this.NetworkType = networkType;
		switch (networkType) {
		case Keywords.WAN:
			this.AccessLinkBandwidth = Mathematics.gigaToBase(10);
			this.AccessLinkMinPropagationDelay = Mathematics.microToMilli(1);
			this.AccessLinkMaxPropagationDelay = Mathematics.microToMilli(10);
			this.NetworkLinkBandwidth = Mathematics.gigaToBase(100);
			this.NetworkLinkMinPropagationDelay = 10;
			this.NetworkLinkMaxPropagationDelay = 100;
			break;
		case Keywords.LAN:
			this.AccessLinkBandwidth = Mathematics.gigaToBase(1);
			this.AccessLinkMinPropagationDelay = Mathematics.microToMilli(1);
			this.AccessLinkMaxPropagationDelay = Mathematics.microToMilli(10);
			this.NetworkLinkBandwidth = Mathematics.gigaToBase(10);
			this.NetworkLinkMinPropagationDelay = Mathematics.microToMilli(1);
			this.NetworkLinkMaxPropagationDelay = Mathematics.microToMilli(10);
			break;
		default:
			break;
		}
		// ControlLink has the bandwidth of AccessLink
		// ControlLink has the propagation delay of NetworkLink
		this.controlLinkBandwidth = this.AccessLinkBandwidth;
		this.controlLinkMinPropagationDelay = this.NetworkLinkMinPropagationDelay;
		this.controlLinkMaxPropagationDelay = this.NetworkLinkMaxPropagationDelay;

	}

	public void dumbell() {
		Statistics stats;
		double simEndTime = Double.MAX_VALUE;
		int flowSize = 100;
		int numberOfFlows = 1;
		double flowArrivalTime = 0.0;
		MaxNumberOfHostsPerAccessSwitch = 1;
		NumberOfAccessSwitches = 1;
		NumberOfNetworkSwitches = 1;

		Simulator sim = new Simulator();
		for (int switchIndex = 0; switchIndex < NumberOfAccessSwitches; switchIndex++) {
			sim.createSwitch("sendAccessSwitch_" + switchIndex, controlLinkMinPropagationDelay, controlLinkBandwidth);
			sim.createSwitch("recvAccessSwitch_" + switchIndex, controlLinkMinPropagationDelay, controlLinkBandwidth);
			for (int hostIndex = 0; hostIndex < MaxNumberOfHostsPerAccessSwitch; hostIndex++) {
				sim.createHost("sender_" + hostIndex);
				sim.createAccessLink("senderAccessLink_" + hostIndex, "sender_" + hostIndex,
						"sendAccessSwitch_" + switchIndex, AccessLinkMinPropagationDelay, AccessLinkBandwidth,
						Integer.MAX_VALUE, Keywords.FIFO);
				sim.createHost("receiver_" + hostIndex);
				sim.createAccessLink("receiverAccessLink_" + hostIndex, "receiver_" + hostIndex,
						"recvAccessSwitch_" + switchIndex, AccessLinkMinPropagationDelay, AccessLinkBandwidth,
						Integer.MAX_VALUE, Keywords.FIFO);
			}
		}
		sim.createSwitch("networkSwitch", controlLinkMinPropagationDelay, controlLinkBandwidth);

		for (int accessSwitchIndex = 0; accessSwitchIndex < NumberOfAccessSwitches; accessSwitchIndex++) {
			sim.createLink("networkLink_" + accessSwitchIndex, "sendAccessSwitch_" + accessSwitchIndex, "networkSwitch",
					NetworkLinkMinPropagationDelay, NetworkLinkBandwidth, Integer.MAX_VALUE, Keywords.FIFO);
		}
		sim.createController("c_0", Keywords.Dijkstra);
		for (int flowIndex = 0; flowIndex < numberOfFlows; numberOfFlows++) {
			sim.generateFlow("flow_" + flowIndex, Keywords.SDTCP, "sender_" + flowIndex, "receiver_" + flowIndex,
					flowSize, flowArrivalTime);
		}
		stats = sim.run(0.0, simEndTime);

		// Debugger Output
		Debugger.debugOutPut();
	}

	public void parkingLot() {

	}
}
