package ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility;

import java.util.HashMap;
import java.util.TreeMap;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.Controller;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.Host;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.Link;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.SDNSwitch;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Flow;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.system.Main;

public class Statistics {

	public Link bottleneckLink;
	public HashMap<Integer, Flow> flows;
	public HashMap<Integer, Link> links;
	public HashMap<Integer, Controller> controllers;
	public HashMap<Integer, SDNSwitch> switches;

	public Statistics(Network net, int btlLinkID) {
		if (btlLinkID != net.controllers.get(10000).getBottleneckLinkID()) {
			Debugger.debug("Controller worked with wrong bottleneck.");
		}
		links = new HashMap<Integer, Link>();
		switches = new HashMap<Integer, SDNSwitch>();
		flows = new HashMap<Integer, Flow>();
		controllers = new HashMap<Integer, Controller>();
		this.controllers = net.controllers;
		this.switches = net.switches;
		this.links = net.links;
		for (Host host : net.hosts.values()) {
			if (host.transportAgent.flow.getID() < 10000) {
				this.flows.put(host.transportAgent.flow.getID(), host.transportAgent.flow);
			}
		}
		for (Link link : links.values()) {
			if (link.isMonitored) {
				bottleneckLink = link;
			}
		}

	}

	public float getAvgFlowCompletionTime() {
		float sum = 0;
		for (Flow flow : flows.values()) {
			sum = Mathematics.addFloat(sum, Mathematics.subtractFloat(flow.FINSendingTime, flow.arrivalTime));
		}
		return sum / (float) flows.size();
	}

	public float getAvgFlowThroughput() {
		float sum = 0;
		for (Flow flow : flows.values()) {
			sum = Mathematics.addFloat(sum, calculateFlowThroughput(flow));
		}
		return 100 * (sum / (float) flows.size());
	}

	public float getAvgStartupDelay() {
		float sum = 0;
		for (Flow flow : flows.values()) {
			float startupDelay = (Mathematics.subtractFloat(flow.dataSendingStartTime, flow.arrivalTime));
			if (startupDelay < 0) {
				Main.error("Statistics", "getAvgStartupDelay",
						"Invalid startupDelay = " + startupDelay + ", flowID = " + flow.getID());
			}
			sum = Mathematics.addFloat(sum, startupDelay);
		}
		return Mathematics.divideFloat(sum, (float) flows.size());
	}

	public float getFairnessIndex() {
		float f = 0;
		float numinator = 0;
		float denuminator = 0;
		for (Flow flow : flows.values()) {
			float flowThroughput = calculateFlowThroughput(flow);
			numinator = Mathematics.addFloat(numinator, flowThroughput);
			denuminator = Mathematics.addFloat(denuminator, Mathematics.multiplyFloat(flowThroughput, flowThroughput));
		}
		numinator = Mathematics.multiplyFloat(numinator, numinator);
		denuminator = Mathematics.multiplyFloat(flows.size(), denuminator);
		f = Mathematics.divideFloat(numinator, denuminator);
		return f;
	}

	public float getBottleneckUtilization() {
		float utilization = 0;
		float totalUpTime = Mathematics.subtractFloat(bottleneckLink.lastSegmentTransmittedTime,
				bottleneckLink.firstSegmentArrivalTime);
		if (totalUpTime > 0) {
			utilization = Mathematics.divideFloat(bottleneckLink.totalTransmissionTime, totalUpTime);
		} else {
			Debugger.error("Statistics", "getBottleneckUtilization", "Invalid link totalUpTime = " + totalUpTime);
		}
		if (utilization < 0 || utilization > 1) {
			Debugger.error("Statistics", "getBottleneckUtilization", "Invalid link utilization = " + utilization);
		}
		return utilization;
	}

	public float getBtlAvgQueueLength() {
		TreeMap<Float, Float> queueTimeSeries = bottleneckLink.queueLength;
		float avgQueuelength = 0;
		int n = queueTimeSeries.size();
		float numinator = 0;
		float denuminator = Mathematics.subtractFloat(queueTimeSeries.lastKey(), queueTimeSeries.firstKey());
		for (int i = 0; i < n - 1; i++) {
			float lenght = queueTimeSeries.get(queueTimeSeries.firstKey());
			float tNow = queueTimeSeries.firstKey();
			float tNext = queueTimeSeries.higherKey(tNow);
			queueTimeSeries.remove(tNow);
			numinator = Mathematics.addFloat(numinator,
					Mathematics.multiplyFloat(lenght, Mathematics.subtractFloat(tNext, tNow)));
		}
		if (denuminator > 0) {
			avgQueuelength = Mathematics.divideFloat(numinator, denuminator);
		} else {
			Debugger.error("Statistics", "getBtlAvgQueueLength", "Invalid Q total time = " + denuminator);
		}
		return avgQueuelength;
	}

	public float getBtlMaxQueueLength() {
		return bottleneckLink.maxQeueLength;
	}

	/***********************************************************************/
	private float calculateFlowThroughput(Flow flow) {
		float throughput = Mathematics.divideFloat(flow.totalTransmissionTime,
				Mathematics.subtractFloat(flow.FINSendingTime, flow.arrivalTime));
		if (throughput < 0 || throughput > 1) {
			Main.error("Statistics", "calculateFlowThroughput",
					"Ivalid Throughput for flowID = " + flow.getID() + ", value = " + throughput);
		}
		return throughput;
	}
}
