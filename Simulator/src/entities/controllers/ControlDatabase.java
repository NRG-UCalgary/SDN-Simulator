package entities.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import entities.*;
import entities.switches.SDNSwitch;
import system.Network;

public class ControlDatabase {

	/* Topology related information */
	public TreeMap<Integer, TreeMap<Integer, Integer>> flowIDOfHostIDOfAccessSwitchID; // <AccessSwitchID, <HostID,
	// FlowID>>
	public HashMap<Integer, Double> controlDelayOfSwitchID; // <SwitchID, ControlLinkPropDelay>

	/* Flow related information */
	public HashMap<Integer, Integer> btlBwOfFlowID; // <FlowID, BottleneckBW>
	public HashMap<Integer, Double> rttOfFlowID; // <FlowID, RTT>
	public HashMap<Integer, ArrayList<Link>> pathOfFlowID; // <FlowID, ArrayList<Link>>

	public ControlDatabase(Network net) {
		flowIDOfHostIDOfAccessSwitchID = new TreeMap<Integer, TreeMap<Integer, Integer>>();
		controlDelayOfSwitchID = new HashMap<Integer, Double>();

		btlBwOfFlowID = new HashMap<Integer, Integer>();
		rttOfFlowID = new HashMap<Integer, Double>();
		pathOfFlowID = new HashMap<Integer, ArrayList<Link>>();

		for (SDNSwitch sdnSwitch : net.switches.values()) {
			if (sdnSwitch.isAccessSwitch()) {
				// flowIDOfHostIDOfAccessSwitchID.put(sdnSwitch.getID(), new HashMap<Integer,
				// Integer>());
			}
			controlDelayOfSwitchID.put(sdnSwitch.getID(), sdnSwitch.controlLink.getPropagationDelay());
		}

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

	public int getNumberOfFlowsForAccessSwitch(int accessSwitchID) {
		int sum = 0;
		for (int switchID : flowIDOfHostIDOfAccessSwitchID.keySet()) {
			sum += flowIDOfHostIDOfAccessSwitchID.get(switchID).size();
		}
		return sum;
		// return pairOfFlowHostForEachAccessSwitch.get(accessSwitchID).size();
		// TODO Must change later for multiple accessSwitch scenario
	}

	public int getFlowIDForHostID(int accessSwitchID, int hostID) {
		return flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID);
	}

	public double getRttForAccessSwitchIDAndHostID(int accessSwitchID, int hostID) {
		return rttOfFlowID.get(flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID));
	}

	public double getRttForFlowID(int flowID) {
		return rttOfFlowID.get(flowID);
	}

	public Set<Integer> getHostIDsSetForAccessSwitchID(int accessSwitchID) {
		return flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).keySet();
	}

	public Set<Integer> getAccessSwitchIDsSet() {
		return flowIDOfHostIDOfAccessSwitchID.keySet();
	}

	public double getMaxRTTForAccessSwitchID(int accessSwitchID) {
		double maxRTT = Double.NEGATIVE_INFINITY;
		for (int hostID : flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).keySet()) {
			double rtt = getRttForFlowID(flowIDOfHostIDOfAccessSwitchID.get(accessSwitchID).get(hostID));
			if (rtt >= maxRTT) {
				maxRTT = rtt;
			}
		}
		return maxRTT;

	}

}
