package experiments.testbeds;

import java.util.TreeMap;

import experiments.traffic.Traffic;
import system.utility.Keywords;
import system.utility.Mathematics;
import system.utility.RandomVariableGenerator;
import system.utility.Statistics;

public abstract class Testbed {
	protected float alpha = 1;
	protected float SimEndTime;
	protected RandomVariableGenerator rttRVG;

	// Link Bandwidth
	protected float AccessLinkBandwidth;
	protected float NetworkLinkBandwidth;
	protected float controlLinkBandwidth;

	// Link Propagation Delay
	protected float ReceiverAccessLinkPropagationDelay;
	protected double AverageAccessLinkPropagationDelay;
	protected double MinAccessLinkPropagationDelay;
	protected double MaxAccessLinkPropagationDelay;

	protected float NetworkLinkPropagationDelay;
	protected float controlLinkPropagationDelay;

	/* Switch Properties */
	protected int NumberOfSenderAccessSwitches;
	protected int NumberOfNetworkSwitches;
	protected int NumberOfHostsPerAccessSwitch;

	/* Flow Properties */
	public int AccessLinkPropagationDelayDistribution;

	public Testbed(int networkType) {
		SimEndTime = Float.MAX_VALUE;
		rttRVG = new RandomVariableGenerator(Keywords.AccessLinkPropagationDelayStartingSeed);
		switch (networkType) {
		case Keywords.WAN:
			AccessLinkBandwidth = (float) Mathematics.megaToBase(500);
			NetworkLinkBandwidth = (float) Mathematics.gigaToBase(1);
			ReceiverAccessLinkPropagationDelay = 10;
			NetworkLinkPropagationDelay = (float) Mathematics.milliToMicro(10);

			AccessLinkPropagationDelayDistribution = Keywords.Uniform;
			MinAccessLinkPropagationDelay = 1.0;
			MaxAccessLinkPropagationDelay = 10.0;
			AverageAccessLinkPropagationDelay = 5.0;

			break;
		case Keywords.LAN:
			AccessLinkBandwidth = (float) Mathematics.megaToBase(500);
			NetworkLinkBandwidth = (float) Mathematics.gigaToBase(1);
			ReceiverAccessLinkPropagationDelay = 1;
			NetworkLinkPropagationDelay = 10;

			AccessLinkPropagationDelayDistribution = Keywords.Uniform;
			MinAccessLinkPropagationDelay = 1.0;
			MaxAccessLinkPropagationDelay = 5.0;
			AverageAccessLinkPropagationDelay = 2.0;

			break;
		default:
			break;
		}
		this.controlLinkBandwidth = this.AccessLinkBandwidth;
		this.controlLinkPropagationDelay = this.NetworkLinkPropagationDelay;
	}

	public abstract Statistics executeSimulation(Traffic traffic);

	protected TreeMap<Integer, Float> prepareAccessLinksPropagationDelay(int distribution, int totalNumberOfFlows) {
		TreeMap<Integer, Float> accessLinkPropagationDelayPerFlowID = new TreeMap<Integer, Float>();
		rttRVG.resetRng();
		for (int flowIndex = 0; flowIndex < totalNumberOfFlows; flowIndex++) {
			float propagationDelay = 0;
			switch (distribution) {
			case Keywords.Exponential:
				propagationDelay = (float) rttRVG.getNextExponential(AverageAccessLinkPropagationDelay);
				break;
			case Keywords.Uniform:
				propagationDelay = (float) rttRVG.getNextUniform(MinAccessLinkPropagationDelay,
						MaxAccessLinkPropagationDelay);
				break;
			default:
				break;
			}
			accessLinkPropagationDelayPerFlowID.put(flowIndex, propagationDelay);
		}
		return accessLinkPropagationDelayPerFlowID;
	}

}
