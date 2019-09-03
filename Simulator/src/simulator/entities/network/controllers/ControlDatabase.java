package simulator.entities.network.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import simulator.Network;

public class ControlDatabase {

	/*----------------------------------------------------------------------------------------*/
	/* Controller Database version 2 parameters */
	public float sTimer = 0;
	public int sharedLinkID = -1;
	public int accessSwitchID = -1;
	public HashMap<Integer, Integer> flowIDOfHostID = new HashMap<Integer, Integer>();
	public HashMap<Integer, Float> sWaitOfFlowID = new HashMap<Integer, Float>();
	public HashMap<Integer, Integer> sWndOfFlwoID = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bottleneckLinkIDOfFlowID = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> accessLinkIDOfFlowID = new HashMap<Integer, Integer>();
	/*----------------------------------------------------------------------------------------*/

	// Temporary
	public int bottleneckLinkID;

	/* Flow related information */
	public HashMap<Integer, Float> btlBwOfFlowID; // <FlowID, BottleneckBW>
	// FlowID>>

	/* Topology related information */
	public TreeMap<Integer, TreeMap<Integer, Integer>> flowIDOfHostIDOfAccessSwitchID; // <AccessSwitchID, <HostID,
	public HashMap<Integer, ArrayList<Integer>> pathOfFlowID; // <FlowID, ArrayList<LinkID>>
	public HashMap<Integer, Float> rttOfFlowID; // <FlowID, RTT>

	public ControlDatabase(Network net) {
		flowIDOfHostIDOfAccessSwitchID = new TreeMap<Integer, TreeMap<Integer, Integer>>();

		btlBwOfFlowID = new HashMap<Integer, Float>();
		rttOfFlowID = new HashMap<Integer, Float>();
		pathOfFlowID = new HashMap<Integer, ArrayList<Integer>>();

	}

	public void addFlow(int accessSwitchID, int srcHostID, int flowID) {
		if (!flowIDOfHostIDOfAccessSwitchID.containsKey(accessSwitchID)) {
			flowIDOfHostIDOfAccessSwitchID.put(accessSwitchID, new TreeMap<Integer, Integer>());
		}
		flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).put(srcHostID, flowID);

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

	public int getNumberOfFlowsForAccessSwitch(int accessSwitchID) {
		int sum = 0;
		for (int switchID : flowIDOfHostIDOfAccessSwitchID.keySet()) {
			sum += flowIDOfHostIDOfAccessSwitchID.get(switchID).size();
		}
		return sum;
		// TODO Must change later for multiple accessSwitch scenario
	}

	public float getRttForAccessSwitchIDAndHostID(int accessSwitchID, int hostID) {
		return rttOfFlowID.get(flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID));
	}

	public float getRttForFlowID(int flowID) {
		return rttOfFlowID.get(flowID);
	}

	public void removeFlow(int accessSwitchID, int srcHostID, int flowID) {
		flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).remove(srcHostID);

	}

}
