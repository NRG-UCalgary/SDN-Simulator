package system.events;

import system.Event;
import system.Network;
import system.Simulator;
import system.utility.Keywords;

public class FlowPathSetup extends Event {

	private int flowID;
	private int neighborID;

	public FlowPathSetup(double startTime, int switchID, int flowID, int neighborID) {
		super(Keywords.FlowPathSetup, startTime, switchID, null);
		this.flowID = flowID;
		this.neighborID = neighborID;
	}

	public Network execute(Network net) {
		// Debugger.event(this.type, this.time, this.nodeID, this.segment, null);
		net.updateTime(eventTime);
		net.switches.get(this.nodeID).addFlowTableEntry(flowID, neighborID);
		net.switches.get(neighborID).addFlowTableEntry(Simulator.ACKStreamID(flowID), nodeID);

		return net;
	}

}
