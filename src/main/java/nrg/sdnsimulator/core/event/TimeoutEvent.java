package nrg.sdnsimulator.core.event;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Event;
import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.Simulator;
import nrg.sdnsimulator.core.utility.Keywords;

@Getter
@Setter
public class TimeoutEvent extends Event {
	private int nodeID;
	private int timerID;

	public TimeoutEvent(float eventTime, int nodeID, int timerID) {
		super(eventTime);
		this.nodeID = nodeID;
		this.timerID = timerID;
	}

	@Override
	public void execute(Network net) {
		// Debugger.debugEvent("TimeOut", eventTime, null);
		net.updateTime(eventTime);
		short nodeType = Simulator.getNodeType(nodeID);
		switch (nodeType) {
		case Keywords.Entities.Nodes.Types.Controller:
			net.getControllers().get(nodeID).executeTimeOut(net, timerID);
			break;
		case Keywords.Entities.Nodes.Types.SDNSwitch:
			net.getSwitches().get(nodeID).executeTimeOut(net, timerID);
			break;
		case Keywords.Entities.Nodes.Types.Host:
			net.getHosts().get(nodeID).executeTimeOut(net, timerID);
			break;
		default:
			break;
		}
	}

}
