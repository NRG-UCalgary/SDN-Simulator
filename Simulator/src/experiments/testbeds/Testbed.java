package experiments.testbeds;

import java.util.TreeMap;

import experiments.traffic.Traffic;
import utility.Keywords;
import utility.Mathematics;
import utility.RandomVariableGenerator;
import utility.Statistics;

public abstract class Testbed {
	// Link Bandwidth
	protected float AccessLinkBandwidth;
	/* Flow Properties */
	public short AccessLinkPropagationDelayDistribution;

	public float alpha = 1f;
	public float beta = 1f;
	public float gamma = 1f;

	protected double AverageAccessLinkPropagationDelay;
	protected float controlLinkBandwidth;
	protected float controlLinkPropagationDelay;

	protected double MaxAccessLinkPropagationDelay;
	protected double MinAccessLinkPropagationDelay;
	protected float NetworkLinkBandwidth;
	protected float NetworkLinkPropagationDelay;
	protected int NumberOfHostsPerAccessSwitch;

	protected int NumberOfNetworkSwitches;
	/* Switch Properties */
	protected int NumberOfSenderAccessSwitches;

	// Link Propagation Delay
	protected float ReceiverAccessLinkPropagationDelay;
	protected RandomVariableGenerator rttRVG;
	protected float SimEndTime;

	protected double StandardDeviationAccessLinkPropagationDelay;

	public Testbed(short networkType) {
		SimEndTime = Float.MAX_VALUE;
		rttRVG = new RandomVariableGenerator(
				Keywords.RandomVariableGenerator.StartingSeeds.AccessLinkPropagationDelayStartingSeed);
		switch (networkType) {
		case Keywords.Testbeds.Types.WAN:
			AccessLinkBandwidth = (float) Mathematics.gigaToBase(10);
			NetworkLinkBandwidth = (float) Mathematics.gigaToBase(10);
			ReceiverAccessLinkPropagationDelay = (float) Mathematics.milliToBase(5);
			NetworkLinkPropagationDelay = (float) Mathematics.milliToBase(5);

			AccessLinkPropagationDelayDistribution = Keywords.RandomVariableGenerator.Distributions.Constant;
			MinAccessLinkPropagationDelay = (float) Mathematics.milliToBase(5);
			MaxAccessLinkPropagationDelay = (float) Mathematics.milliToBase(5);
			AverageAccessLinkPropagationDelay = (float) Mathematics.milliToBase(5);
			StandardDeviationAccessLinkPropagationDelay = (float) Mathematics.milliToBase(5);

			break;
		case Keywords.Testbeds.Types.LAN:
			NetworkLinkBandwidth = (float) Mathematics.gigaToBase(10);
			NetworkLinkPropagationDelay = (float) Mathematics.microToBase(5);
			AccessLinkBandwidth = (float) Mathematics.gigaToBase(10);
			ReceiverAccessLinkPropagationDelay = (float) Mathematics.microToBase(5);

			AccessLinkPropagationDelayDistribution = Keywords.RandomVariableGenerator.Distributions.Constant;
			MinAccessLinkPropagationDelay = (float) Mathematics.microToBase(5);
			MaxAccessLinkPropagationDelay = (float) Mathematics.microToBase(5);
			AverageAccessLinkPropagationDelay = (float) Mathematics.microToBase(5);
			StandardDeviationAccessLinkPropagationDelay = 0;

			break;
		case Keywords.Testbeds.Types.Custom:
			AccessLinkBandwidth = (float) Mathematics.gigaToBase(1);
			NetworkLinkBandwidth = (float) Mathematics.gigaToBase(1);
			ReceiverAccessLinkPropagationDelay = (float) Mathematics.microToBase(1);
			NetworkLinkPropagationDelay = (float) Mathematics.microToBase(1);

			AccessLinkPropagationDelayDistribution = Keywords.RandomVariableGenerator.Distributions.Constant;
			MinAccessLinkPropagationDelay = (float) Mathematics.microToBase(1);
			MaxAccessLinkPropagationDelay = (float) Mathematics.microToBase(1);
			AverageAccessLinkPropagationDelay = (float) Mathematics.microToBase(1);
			StandardDeviationAccessLinkPropagationDelay = 0;

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
			accessLinkPropagationDelayPerFlowID.put(flowIndex, (float) rttRVG.getNextValue(distribution,
					MinAccessLinkPropagationDelay, StandardDeviationAccessLinkPropagationDelay));
		}
		return accessLinkPropagationDelayPerFlowID;
	}

}
