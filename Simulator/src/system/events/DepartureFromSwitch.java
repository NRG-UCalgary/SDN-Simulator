package system.events;

import entities.Segment;
import system.*;
import system.utility.Keywords;

public class DepartureFromSwitch extends Event {

	public DepartureFromSwitch(double startTime, int switchID, Segment segment) {
		super(Keywords.DepartureFromSwitch, startTime, switchID, segment);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		//Debugger.event(this.type, this.time, this.nodeID, this.segment, null);
		net.updateTime(eventTime);
		net.switches.get(this.nodeID).releaseSegment(net, segment);
		return net;
	}
}
