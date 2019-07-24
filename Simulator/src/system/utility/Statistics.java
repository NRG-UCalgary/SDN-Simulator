package system.utility;

import java.util.ArrayList;
import java.util.HashMap;

import entities.*;
import entities.controllers.Controller;
import entities.switches.SDNSwitch;
import system.Main;
import system.Network;

public class Statistics {

	// Temporary
	public int bottleneckLinkID;

	public HashMap<Integer, Link> links; // <LinkID, Link>
	public HashMap<Integer, SDNSwitch> switches; // <SwitchID, SDNSwitch>
	public HashMap<Integer, Flow> flows; // <FlowID, Flow>
	public Controller controller;

	public Statistics(Network net, int btlLinkID) {
		this.bottleneckLinkID = btlLinkID;
		if (btlLinkID != net.controller.getBottleneckLinkID()) {
			Debugger.debug("Controller worked with wrong bottleneck.");
		}
		links = new HashMap<Integer, Link>();
		switches = new HashMap<Integer, SDNSwitch>();
		flows = new HashMap<Integer, Flow>();
		this.controller = net.controller;
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

	public float getAvgFlowThroughput() {
		float sum = 0;
		for (Flow flow : flows.values()) {
			sum += (flow.totalTransmissionTime / (float) (flow.FINSendingTime - flow.arrivalTime));
		}
		return sum / (float) flows.size();
	}

	public float getBottleneckUtilization() {
		Link bottleneck = links.get(bottleneckLinkID);
		float totalUpTime = bottleneck.lastSegmentTransmittedTime - bottleneck.firstSegmentArrivalTime;
		float util = 0;
		if (totalUpTime > 0) {
			util = bottleneck.totalTransmissionTime / totalUpTime;
		}
		return util;
	}

	public float getMaxBottleneckBufferOccupancy() {
		// TODO add the max occupancy of both data and ack stream
		return links.get(bottleneckLinkID).buffer.maxOccupancy;
	}

	public float getVarianceOfBottleneckUtilizationSharePerFlowSize() {
		float variance = 0;
		Link bottleneck = links.get(bottleneckLinkID);
		ArrayList<Float> values = new ArrayList<Float>();
		for (float flowID : bottleneck.utilizationTimePerFlowID.keySet()) {
			float utilizationShare = bottleneck.utilizationTimePerFlowID.get(flowID);
			float bottleNeckTotalTransmissionTime = bottleneck.totalTransmissionTime;
			float value = (100 * utilizationShare)
					/ ((float) (flows.get((int) flowID).getSize() * bottleNeckTotalTransmissionTime));
			values.add(value);
		}
		try {
			variance = (float) Mathematics.variance(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return variance;
	}

	public float getVarianceOfFlowCompletionTimePerFlowSize() {
		float variance = 0;
		ArrayList<Float> completionTimes = new ArrayList<Float>();
		for (Flow flow : flows.values()) {
			completionTimes.add(flow.completionTime / (float) flow.getSize());
		}
		try {
			variance = (float) Mathematics.variance(completionTimes);
		} catch (Exception e) {
			Main.error(this.getClass().getName(), "getVarianceOfFlowCompletionTimePerFlowSize",
					"null values in completionTimes array.");
		}
		return variance;
	}

	public float getFlowRejectionPercentage() {
		// TODO the counter update must be implemented
		return 0;
	}
}
