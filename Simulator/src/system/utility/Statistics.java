package system.utility;

import java.util.ArrayList;
import java.util.HashMap;

import entities.*;
import entities.switches.SDNSwitch;
import system.Main;
import system.Network;

public class Statistics {

	// Temporary
	int bottleneckLinkID;

	public HashMap<Integer, Link> links; // <LinkID, Link>
	public HashMap<Integer, SDNSwitch> switches; // <SwitchID, SDNSwitch>
	public HashMap<Integer, Flow> flows; // <FlowID, Flow>

	public Statistics(Network net) {
		links = new HashMap<Integer, Link>();
		switches = new HashMap<Integer, SDNSwitch>();
		flows = new HashMap<Integer, Flow>();

		this.switches = net.switches;
		for (SDNSwitch sdnSwitch : net.switches.values()) {
			for (Link link : sdnSwitch.networkLinks.values()) {
				this.links.put(link.getID(), link);
			}
		}
		for (Host host : net.hosts.values()) {
			if (host.transportAgent.flow.getID() < 10000) {
				this.flows.put(host.transportAgent.flow.getID(), host.transportAgent.flow);
			}
		}

		bottleneckLinkID = net.controller.getBottleneckLinkID();

	}

	public float getAvgFlowCompletionTime() {
		float sum = 0;
		for (Flow flow : flows.values()) {
			sum += flow.completionTime;
		}
		return sum / (float) flows.size();
	}

	public float getAvgStartupDelay() {
		float sum = 0;
		for (Flow flow : flows.values()) {
			sum += (flow.dataSendingStartTime - flow.arrivalTime);
		}
		return sum / (float) flows.size();
	}

	public float getBottleneckUtilization() {
		Link bottleneck = links.get(bottleneckLinkID);
		return bottleneck.totalUtilizationTime / bottleneck.totalUpTime;
	}

	public float getMaxBottleneckBufferOccupancy() {
		// TODO add the max occupancy of both data and ack stream
		return links.get(bottleneckLinkID).buffer.maxOccupancy;
	}

	public float getAvgBottleneckBufferOccupancy() {
		// TODO prepare the formula and counters
		return 0;
	}

	public float getVarianceOfBottleneckUtilizationShare() {
		float variance = 0;
		Link bottleneck = links.get(bottleneckLinkID);
		ArrayList<Float> values = new ArrayList<Float>();
		for (float flowID : bottleneck.utilizationTimePerFlowID.keySet()) {
			float utilizationShare = bottleneck.utilizationTimePerFlowID.get(flowID);
			values.add((100 * utilizationShare)
					/ ((float) (flows.get((int) flowID).getSize() * bottleneck.totalUtilizationTime)));
		}
		try {
			variance = (float) Mathematics.variance(values);
		} catch (Exception e) {
			Main.print("Error::Statistics.getVarianceOfBottleneckUtilizationShare()");
			e.printStackTrace();
		}
		return variance;
	}
}
