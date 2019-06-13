package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import entities.*;
import system.Network;

public class ControlDatabase {

	/* Topology related information */
	public ArrayList<Integer> AccessSwitchIDs; // <SwitchID>
	public HashMap<Integer, Double> ControlDelays; // <SwitchID, ControlLinkPropDelay>

	/* Flow related information */
	public HashMap<Integer, Integer> BtlBWs; // <FlowID, BottleneckBW>
	public HashMap<Integer, Double> RTTs; // <FlowID, RTT>
	// TODO ultimately this should changed to a map of access Switch ID and RTTs
	public HashMap<Integer, ArrayList<Link>> Paths; // <FlowID, ArrayList<Link>>
	public HashMap<Integer, Integer> Flows; // <srcHostID, FlowID>

	public ControlDatabase(Network net) {
		AccessSwitchIDs = new ArrayList<Integer>();
		ControlDelays = new HashMap<Integer, Double>();
		for (SDNSwitch sdnSwitch : net.switches.values()) {
			if (sdnSwitch.isAccessSwitch) {
				AccessSwitchIDs.add(sdnSwitch.getID());
			}
			ControlDelays.put(sdnSwitch.getID(), sdnSwitch.controlLink.getPropagationDelay());
		}

		BtlBWs = new HashMap<Integer, Integer>();
		RTTs = new HashMap<Integer, Double>();
		Paths = new HashMap<Integer, ArrayList<Link>>();
		Flows = new HashMap<Integer, Integer>();
	}

}
