package nrg.sdnsimulator.core.entity.network.controller;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.Link;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Keywords;
import nrg.sdnsimulator.core.utility.Mathematics;

public class ControlDatabase {

	/*----------------------------------------------------------------------------------------*/
	/* Controller Database version 2 parameters */
	private int accessSwitchID = -1;
	private float maxRTT = 0;
	private float sharedEgressLinkBw = 0;
	private int totalNumberOfFlows = 0;
	private HashMap<Integer, Float> maxRTTOfAccessSwitchID = new HashMap<Integer, Float>(); // TODO
																							// give
																							// value
																							// somewhere
	private HashMap<Integer, Integer> flowIDOfHostID = new HashMap<Integer, Integer>();// <HostID,
																						// FlowID>
	private TreeMap<Integer, TreeMap<Integer, Integer>> flowIDOfHostIDOfAccessSwitchID; // <switchID,<hostID,flowID>>
	private HashMap<Integer, HashMap<Integer, Integer>> pathOfFlowID; // <FlowID,
																		// Map<switchID,nextSwitchID>>
	private HashMap<Integer, Float> RTTOfFlowID = new HashMap<Integer, Float>();
	private HashMap<Integer, Float> SYNRTTOfFlowID = new HashMap<Integer, Float>();
	private HashMap<Integer, Integer> btlLinkIDOfFlowID = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> networkBtlLinkIDOfFlowID = new HashMap<Integer, Integer>();
	private HashMap<Integer, Link> accessLinkOfFlowID = new HashMap<Integer, Link>();
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
			float rtt = getRttForFlowID(
					flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID));
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
			flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).put(segment.getSrcHostID(),
					segment.getFlowID());
		} else {
			flowIDOfHostIDOfAccessSwitchID.put(accessSwitchID, new TreeMap<Integer, Integer>());
			flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).put(segment.getSrcHostID(),
					segment.getFlowID());
		}
		// Update RTT information of database
		updateRTTOfFlowID(net, segment);
		updateMaxRTTOfAccessSwitch(accessSwitchID);
		totalNumberOfFlows--;
	}

	public void updateRTTOfFlowID(Network net, Segment segment) {
		float rtt = 0;
		float synRtt = 0;
		Link senderAccessLink = net.getLinks()
				.get(net.getHosts().get(segment.getSrcHostID()).getAccessLinkID());
		Link receiverAccessLink = net.getLinks()
				.get(net.getHosts().get(segment.getDstHostID()).getAccessLinkID());
		// The rtt has a slight overestimation for using data segment size for both ways
		float minBand = Float.MAX_VALUE;
		rtt = Mathematics.addFloat(rtt,
				senderAccessLink.getTotalDelay(Keywords.Segments.Sizes.DataSegSize));
		if (senderAccessLink.getBandwidth() < minBand) {
			minBand = senderAccessLink.getBandwidth();
			btlLinkIDOfFlowID.put(segment.getFlowID(), senderAccessLink.getID());
		}
		if (receiverAccessLink.getBandwidth() < minBand) {
			minBand = receiverAccessLink.getBandwidth();
			btlLinkIDOfFlowID.put(segment.getFlowID(), receiverAccessLink.getID());
		}
		rtt = Mathematics.addFloat(rtt,
				senderAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));
		rtt = Mathematics.addFloat(rtt,
				receiverAccessLink.getTotalDelay(Keywords.Segments.Sizes.DataSegSize));
		rtt = Mathematics.addFloat(rtt,
				receiverAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));

		synRtt = Mathematics.multiplyFloat(2,
				senderAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));
		synRtt = Mathematics.addFloat(synRtt, Mathematics.multiplyFloat(2,
				receiverAccessLink.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize)));
		float networkMinBand = Float.MAX_VALUE;
		for (int srcSwitchID : pathOfFlowID.get(segment.getFlowID()).keySet()) {
			int dstSwitchID = pathOfFlowID.get(segment.getFlowID()).get(srcSwitchID);
			int linkID = net.getSwitches().get(srcSwitchID).getNetworkLinksIDs().get(dstSwitchID);
			Link link = net.getLinks().get(linkID);
			rtt = Mathematics.addFloat(rtt,
					link.getTotalDelay(Keywords.Segments.Sizes.DataSegSize));
			rtt = Mathematics.addFloat(rtt, link.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize));
			synRtt = Mathematics.addFloat(synRtt, Mathematics.multiplyFloat(2,
					link.getTotalDelay(Keywords.Segments.Sizes.ACKSegSize)));
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

	public int getAccessSwitchID() {
		return accessSwitchID;
	}

	public void setAccessSwitchID(int accessSwitchID) {
		this.accessSwitchID = accessSwitchID;
	}

	public float getMaxRTT() {
		return maxRTT;
	}

	public void setMaxRTT(float maxRTT) {
		this.maxRTT = maxRTT;
	}

	public float getSharedEgressLinkBw() {
		return sharedEgressLinkBw;
	}

	public void setSharedEgressLinkBw(float sharedEgressLinkBw) {
		this.sharedEgressLinkBw = sharedEgressLinkBw;
	}

	public int getTotalNumberOfFlows() {
		return totalNumberOfFlows;
	}

	public void setTotalNumberOfFlows(int totalNumberOfFlows) {
		this.totalNumberOfFlows = totalNumberOfFlows;
	}

	public HashMap<Integer, Float> getMaxRTTOfAccessSwitchID() {
		return maxRTTOfAccessSwitchID;
	}

	public void setMaxRTTOfAccessSwitchID(HashMap<Integer, Float> maxRTTOfAccessSwitchID) {
		this.maxRTTOfAccessSwitchID = maxRTTOfAccessSwitchID;
	}

	public HashMap<Integer, Integer> getFlowIDOfHostID() {
		return flowIDOfHostID;
	}

	public void setFlowIDOfHostID(HashMap<Integer, Integer> flowIDOfHostID) {
		this.flowIDOfHostID = flowIDOfHostID;
	}

	public TreeMap<Integer, TreeMap<Integer, Integer>> getFlowIDOfHostIDOfAccessSwitchID() {
		return flowIDOfHostIDOfAccessSwitchID;
	}

	public void setFlowIDOfHostIDOfAccessSwitchID(
			TreeMap<Integer, TreeMap<Integer, Integer>> flowIDOfHostIDOfAccessSwitchID) {
		this.flowIDOfHostIDOfAccessSwitchID = flowIDOfHostIDOfAccessSwitchID;
	}

	public HashMap<Integer, HashMap<Integer, Integer>> getPathOfFlowID() {
		return pathOfFlowID;
	}

	public void setPathOfFlowID(HashMap<Integer, HashMap<Integer, Integer>> pathOfFlowID) {
		this.pathOfFlowID = pathOfFlowID;
	}

	public HashMap<Integer, Float> getRTTOfFlowID() {
		return RTTOfFlowID;
	}

	public void setRTTOfFlowID(HashMap<Integer, Float> rTTOfFlowID) {
		RTTOfFlowID = rTTOfFlowID;
	}

	public HashMap<Integer, Float> getSYNRTTOfFlowID() {
		return SYNRTTOfFlowID;
	}

	public void setSYNRTTOfFlowID(HashMap<Integer, Float> sYNRTTOfFlowID) {
		SYNRTTOfFlowID = sYNRTTOfFlowID;
	}

	public HashMap<Integer, Integer> getBtlLinkIDOfFlowID() {
		return btlLinkIDOfFlowID;
	}

	public void setBtlLinkIDOfFlowID(HashMap<Integer, Integer> btlLinkIDOfFlowID) {
		this.btlLinkIDOfFlowID = btlLinkIDOfFlowID;
	}

	public HashMap<Integer, Integer> getNetworkBtlLinkIDOfFlowID() {
		return networkBtlLinkIDOfFlowID;
	}

	public void setNetworkBtlLinkIDOfFlowID(HashMap<Integer, Integer> networkBtlLinkIDOfFlowID) {
		this.networkBtlLinkIDOfFlowID = networkBtlLinkIDOfFlowID;
	}

	public HashMap<Integer, Link> getAccessLinkOfFlowID() {
		return accessLinkOfFlowID;
	}

	public void setAccessLinkOfFlowID(HashMap<Integer, Link> accessLinkOfFlowID) {
		this.accessLinkOfFlowID = accessLinkOfFlowID;
	}

	public int getBottleneckLinkID() {
		return bottleneckLinkID;
	}

	public void setBottleneckLinkID(int bottleneckLinkID) {
		this.bottleneckLinkID = bottleneckLinkID;
	}

	public HashMap<Integer, Float> getBtlBwOfFlowID() {
		return btlBwOfFlowID;
	}

	public void setBtlBwOfFlowID(HashMap<Integer, Float> btlBwOfFlowID) {
		this.btlBwOfFlowID = btlBwOfFlowID;
	}

	public HashMap<Integer, Float> getRttOfFlowID() {
		return rttOfFlowID;
	}

	public void setRttOfFlowID(HashMap<Integer, Float> rttOfFlowID) {
		this.rttOfFlowID = rttOfFlowID;
	}

}
