package nrg.sdnsimulator.core.utility;

import java.util.HashMap;
import java.util.TreeMap;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.Controller;
import nrg.sdnsimulator.core.entity.network.Host;
import nrg.sdnsimulator.core.entity.network.Link;
import nrg.sdnsimulator.core.entity.network.SDNSwitch;
import nrg.sdnsimulator.core.entity.traffic.Flow;
import nrg.sdnsimulator.core.system.SimApp;

public class Statistics {

	private Link bottleneckLink;
	private HashMap<Integer, Flow> flows;
	private HashMap<Integer, Link> links;
	private HashMap<Integer, Controller> controllers;
	private HashMap<Integer, SDNSwitch> switches;

	public Statistics(Network net, int btlLinkID) {
		if (btlLinkID != net.getControllers().get(10000).getBottleneckLinkID()) {
			Debugger.debug("Controller worked with wrong bottleneck.");
		}
		links = new HashMap<Integer, Link>();
		switches = new HashMap<Integer, SDNSwitch>();
		flows = new HashMap<Integer, Flow>();
		controllers = new HashMap<Integer, Controller>();
		this.controllers = net.getControllers();
		this.switches = net.getSwitches();
		this.links = net.getLinks();
		for (Host host : net.getHosts().values()) {
			if (host.getTransportAgent().getFlow().getID() < 10000) {
				this.flows.put(host.getTransportAgent().getFlow().getID(),
						host.getTransportAgent().getFlow());
			}
		}
		for (Link link : links.values()) {
			if (link.isMonitored()) {
				bottleneckLink = link;
			}
		}

	}

	public float getAvgFlowCompletionTime() {
		float sum = 0;
		for (Flow flow : flows.values()) {
			sum = Mathematics.addFloat(sum,
					Mathematics.subtractFloat(flow.getFINSendingTime(), flow.getArrivalTime()));
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
			float startupDelay = (Mathematics.subtractFloat(flow.getDataSendingStartTime(),
					flow.getArrivalTime()));
			if (startupDelay < 0) {
				SimApp.error("Statistics", "getAvgStartupDelay",
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
			denuminator = Mathematics.addFloat(denuminator,
					Mathematics.multiplyFloat(flowThroughput, flowThroughput));
		}
		numinator = Mathematics.multiplyFloat(numinator, numinator);
		denuminator = Mathematics.multiplyFloat(flows.size(), denuminator);
		f = Mathematics.divideFloat(numinator, denuminator);
		return f;
	}

	public float getBottleneckUtilization() {
		float utilization = 0;
		float totalUpTime = Mathematics.subtractFloat(
				bottleneckLink.getLastSegmentTransmittedTime(),
				bottleneckLink.getFirstSegmentArrivalTime());
		if (totalUpTime > 0) {
			utilization = Mathematics.divideFloat(bottleneckLink.getTotalTransmissionTime(),
					totalUpTime);
		} else {
			Debugger.error("Statistics", "getBottleneckUtilization",
					"Invalid link totalUpTime = " + totalUpTime);
		}
		if (utilization < 0 || utilization > 1) {
			Debugger.error("Statistics", "getBottleneckUtilization",
					"Invalid link utilization = " + utilization);
		}
		return utilization;
	}

	public float getBtlAvgQueueLength() {
		TreeMap<Float, Float> queueTimeSeries = bottleneckLink.getQueueLength();
		float avgQueuelength = 0;
		int n = queueTimeSeries.size();
		float numinator = 0;
		float denuminator = Mathematics.subtractFloat(queueTimeSeries.lastKey(),
				queueTimeSeries.firstKey());
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
			Debugger.error("Statistics", "getBtlAvgQueueLength",
					"Invalid Q total time = " + denuminator);
		}
		return avgQueuelength;
	}

	public float getBtlMaxQueueLength() {
		return bottleneckLink.getMaxQeueLength();
	}

	private float calculateFlowThroughput(Flow flow) {
		float throughput = Mathematics.divideFloat(flow.getTotalTransmissionTime(),
				Mathematics.subtractFloat(flow.getFINSendingTime(), flow.getArrivalTime()));
		if (throughput < 0 || throughput > 1) {
			SimApp.error("Statistics", "calculateFlowThroughput",
					"Ivalid Throughput for flowID = " + flow.getID() + ", value = " + throughput);
		}
		return throughput;
	}

	public Link getBottleneckLink() {
		return bottleneckLink;
	}

	public void setBottleneckLink(Link bottleneckLink) {
		this.bottleneckLink = bottleneckLink;
	}

	public HashMap<Integer, Flow> getFlows() {
		return flows;
	}

	public void setFlows(HashMap<Integer, Flow> flows) {
		this.flows = flows;
	}

	public HashMap<Integer, Link> getLinks() {
		return links;
	}

	public void setLinks(HashMap<Integer, Link> links) {
		this.links = links;
	}

	public HashMap<Integer, Controller> getControllers() {
		return controllers;
	}

	public void setControllers(HashMap<Integer, Controller> controllers) {
		this.controllers = controllers;
	}

	public HashMap<Integer, SDNSwitch> getSwitches() {
		return switches;
	}

	public void setSwitches(HashMap<Integer, SDNSwitch> switches) {
		this.switches = switches;
	}

}
