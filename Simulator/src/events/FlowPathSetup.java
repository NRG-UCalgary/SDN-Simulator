package events;

import system.Event;
import system.Network;
import system.Simulator;

public class FlowPathSetup extends Event {

	private int flowID;
	private int neighborID;

	public FlowPathSetup(double startTime, int switchID, int flowID, int neighborID) {
		super(startTime, switchID, null);
		name = "Flow Path Setup";
		this.flowID = flowID;
		this.neighborID = neighborID;
	}

	public Network execute(Network net) {
		net.updateTime(currentTime);
		net.switches.get(this.currentNodeID).addFlowTableEntry(flowID, neighborID);
		net.switches.get(neighborID).addFlowTableEntry(Simulator.ACKStreamID(flowID), currentNodeID);
		return net;
	}

}
