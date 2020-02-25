package com.nrg.sdnsimulator.core.entity.network.controller;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import com.nrg.sdnsimulator.core.Network;
import com.nrg.sdnsimulator.core.entity.network.Link;
import com.nrg.sdnsimulator.core.entity.traffic.Segment;
import com.nrg.sdnsimulator.core.utility.Keywords;
import com.nrg.sdnsimulator.core.utility.Mathematics;

public class ControlDatabase {

	/*----------------------------------------------------------------------------------------*/
	/* Controller Database version 2 parameters */
	public int accessSwitchID = -1;
	public float maxRTT = 0;
	public float sharedEgressLinkBw = 0;
	public int totalNumberOfFlows = 0;
	public HashMap<Integer, Float> maxRTTOfAccessSwitchID = new HashMap<Integer, Float>(); // TODO give value somewhere
	public HashMap<Integer, Integer> flowIDOfHostID = new HashMap<Integer, Integer>();// <HostID, FlowID>
	public TreeMap<Integer, TreeMap<Integer, Integer>> flowIDOfHostIDOfAccessSwitchID; // <switchID,<hostID,flowID>>
	public HashMap<Integer, HashMap<Integer, Integer>> pathOfFlowID; // <FlowID, Map<switchID,nextSwitchID>>
	public HashMap<Integer, Float> RTTOfFlowID = new HashMap<Integer, Float>();
	public HashMap<Integer, Float> SYNRTTOfFlowID = new HashMap<Integer, Float>();
	public HashMap<Integer, Integer> btlLinkIDOfFlowID = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> networkBtlLinkIDOfFlowID = new HashMap<Integer, Integer>();
	public HashMap<Integer, Link> accessLinkOfFlowID = new HashMap<Integer, Link>();
	/*----------------------------------------------------------------------------------------*/

	// Temporary
	public int bottleneckLinkID;

	/* Flow related information */
	public HashMap<Integer, Float> btlBwOfFlowID; // <FlowID, BottleneckBW>
	// FlowID>>

	/* Topology related information */

	public HashMap<Integer, Float> rttOfFlowID; // <FlowID, RTT>

	public ControlDatabase(Network net) {
		/*----------------------------------------------------------------------------------------*/
		/* Controller Database version 2 parameters */

		/*----------------------------------------------------------------------------------------*/

		flowIDOfHostIDOfAccessSwitchID = new TreeMap<Integer, TreeMap<Integer, Integer>>();

		btlBwOfFlowID = new HashMap<Integer, Float>();
		rttOfFlowID = new HashMap<Integer, Float>();
		pathOfFlowID = new HashMap<Integer, HashMap<Integer, Integer>>();

	}

	public void addFlow(int accessSwitchID, int srcHostID, int flowID) {
		if (!flowIDOfHostIDOfAccessSwitchID.containsKey(accessSwitchID)) {
			flowIDOfHostIDOfAccessSwitchID.put(accessSwitchID, new TreeMap<Integer, Integer>());
		}
		flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).put(srcHostID, flowID);

	}

	public void removeFlow(int accessSwitchID, int srcHostID, int flowID) {
		flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).remove(srcHostID);

	}

	public Set<Integer> getAccessSwitchIDsSet() {
		return flowIDOfHostIDOfAccessSwitchID.keySet();
	}

	public int getFlowIDForHostID(int accessSwitchID, int hostID) {
		return flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID);
	}

	public Set<Integer> getHostIDsSetForAccessSwitchID(int accessSwitchID) {
		return flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).keySet();
	}

	public float getMaxRTTForAccessSwitchID(int accessSwitchID) {
		float maxRTT = Float.NEGATIVE_INFINITY;
		for (int hostID : flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).keySet()) {
			float rtt = getRttForFlowID(flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID));
			if (rtt >= maxRTT) {
				maxRTT = rtt;
			}
		}
		return maxRTT;
	}

	public float getRttForAccessSwitchIDAndHostID(int accessSwitchID, int hostID) {
		return rttOfFlowID.get(flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID));
	}

	public float getRttForFlowID(int flowID) {
		return rttOfFlowID.get(flowID);
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	public void arrivalOfFINForFlowID(int accessSwitchID, int flowID, int srcHostID) {
		flowIDOfHostID.remove(srcHostID);
		flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).remove(srcHostID);
		pathOfFlowID.remove(flowID);
		RTTOfFlowID.remove(flowID);
		btlLinkIDOfFlowID.remove(flowID);
		accessLinkOfFlowID.remove(flowID);
		updateMaxRTTOfAccessSwitch(accessSwitchID);
		totalNumberOfFlows++;
	}

	public void arrivalOfSYNForFlowID(Network net, Segment segment, int accessSwitchID) {
		// Update accessSwitchID in database
		this.accessSwitchID = accessSwitchID;
		// Add the flowID to database
		flowIDOfHostID.put(segment.getSrcHostID(), segment.getFlowID());
		if (flowIDOfHostIDOfAccessSwitchID.containsKey(accessSwitchID)) {
			flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).put(segment.getSrcHostID(), segment.getFlowID());
		} else {
			flowIDOfHostIDOfAccessSwitchID.put(accessSwitchID, new TreeMap<Integer, Integer>());
			flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).put(segment.getSrcHostID(), segment.getFlowID());
		}
		// Update RTT information of database
		updateRTTOfFlowID(net, segment);
		updateMaxRTTOfAccessSwitch(accessSwitchID);
		totalNumberOfFlows--;
	}

	public void updateRTTOfFlowID(Network net, Segment segment) {
		float rtt = 0;
		float synRtt = 0;
		Link senderAccessLink = net.links.get(net.hosts.get(segment.getSrcHostID()).accessLinkID);
		Link receiverAccessLink = net.links.get(net.hosts.get(segment.getDstHostID()).accessLinkID);
		// The rtt has a slight overestimation for using data segment size for both ways
		float minBand = Float.MAX_VALUE;
		rtt = Mathematics.addFloat(rtt, senderAccessLink.getTotalDelay(Keywords.Segments.Sizes.DataSegSize));
		if (senderAccessLink.getBandwidth() < minBand) {
			minBand = senderAccessLink.getBandwidth();
			btlLinkIDOfFlowID.put(segment.getFlowID(), senderAccessLink.getID());
		}
		if (receiverAccessLink.getBandwidth() < minBand) {
			minBand = receiverAccessLink.getBandwidth();
			btlLinkIDOfFlowID.put(segment.getFlowID(), receiverAccessLink.getID());
		}
		rtt = Mathematics.addFloat(rtt, senderAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));
		rtt = Mathematics.addFloat(rtt, receiverAccessLink.getTotalDelay(Keywords.Segments.Sizes.DataSegSize));
		rtt = Mathematics.addFloat(rtt, receiverAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));

		synRtt = Mathematics.multiplyFloat(2, senderAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));
		synRtt = Mathematics.addFloat(synRtt,
				Mathematics.multiplyFloat(2, receiverAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize)));
		float networkMinBand = Float.MAX_VALUE;
		for (int srcSwitchID : pathOfFlowID.get(segment.getFlowID()).keySet()) {
			int dstSwitchID = pathOfFlowID.get(segment.getFlowID()).get(srcSwitchID);
			int linkID = net.switches.get(srcSwitchID).getNetworkLinksIDs().get(dstSwitchID);
			Link link = net.links.get(linkID);
			rtt = Mathematics.addFloat(rtt, link.getTotalDelay(Keywords.Segments.Sizes.DataSegSize));
			rtt = Mathematics.addFloat(rtt, link.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));
			synRtt = Mathematics.addFloat(synRtt,
					Mathematics.multiplyFloat(2, link.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize)));
			if (link.getBandwidth() < minBand) {
				minBand = link.getBandwidth();
				btlLinkIDOfFlowID.put(segment.getFlowID(), link.getID());
			}
			// TODO check to see if works correctly
			if (link.getBandwidth() < networkMinBand) {
				networkMinBand = link.getBandwidth();
				networkBtlLinkIDOfFlowID.put(segment.getFlowID(), link.getID());
			}
		}
		SYNRTTOfFlowID.put(segment.getFlowID(), synRtt);
		RTTOfFlowID.put(segment.getFlowID(), rtt);
		if (rtt > maxRTT) {
			maxRTT = rtt;
			maxRTTOfAccessSwitchID.put(accessSwitchID, maxRTT);
		}

		// update accesslinkIDOfFlowID
		accessLinkOfFlowID.put(segment.getFlowID(), senderAccessLink);
	}

	public int getNumberOfFlowsForAccessSwitch(int accessSwitchID) {
		return flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).keySet().size();
	}

	public void updateMaxRTTOfAccessSwitch(int accessSwitchID) {
		float maxRTT = 0;
		for (int hostID : flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).keySet()) {
			int flowID = flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID);
			if (RTTOfFlowID.get(flowID) > maxRTT) {
				maxRTT = RTTOfFlowID.get(flowID);
			}
		}
		maxRTTOfAccessSwitchID.put(accessSwitchID, maxRTT);

	}

}
