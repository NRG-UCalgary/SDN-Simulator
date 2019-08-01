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
	protected float alpha = 1;

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
			AccessLinkBandwidth = (float) Mathematics.megaToBase(500);
			NetworkLinkBandwidth = (float) Mathematics.gigaToBase(1);
			ReceiverAccessLinkPropagationDelay = 10;
			NetworkLinkPropagationDelay = 10000;
			//NetworkLinkPropagationDelay = 10;

			AccessLinkPropagationDelayDistribution = Keywords.RandomVariableGenerator.Distributions.Uniform;
			MinAccessLinkPropagationDelay = 1.0;
			MaxAccessLinkPropagationDelay = 10.0;
			AverageAccessLinkPropagationDelay = 5.0;
			StandardDeviationAccessLinkPropagationDelay = 4.0;

			break;
		case Keywords.Testbeds.Types.LAN:
			AccessLinkBandwidth = (float) Mathematics.megaToBase(500);
			NetworkLinkBandwidth = (float) Mathematics.gigaToBase(1);
			ReceiverAccessLinkPropagationDelay = 1;
			NetworkLinkPropagationDelay = 10;

			AccessLinkPropagationDelayDistribution = Keywords.RandomVariableGenerator.Distributions.Uniform;
			MinAccessLinkPropagationDelay = 1.0;
			MaxAccessLinkPropagationDelay = 5.0;
			AverageAccessLinkPropagationDelay = 2.0;
			StandardDeviationAccessLinkPropagationDelay = 1.0;

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
			accessLinkPropagationDelayPerFlowID.put(flowIndex,
					(float) rttRVG.getNextUniform(MinAccessLinkPropagationDelay, MaxAccessLinkPropagationDelay));
		}
		return accessLinkPropagationDelayPerFlowID;
	}

}
