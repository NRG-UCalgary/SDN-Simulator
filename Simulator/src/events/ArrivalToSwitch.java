package events;

import system.*;
import utilities.*;

import entities.*;

public class ArrivalToSwitch extends Event {

	public ArrivalToSwitch(double startTime, int switchID, Segment segment, CtrlMessage controlMessage) {
		super(Keywords.ArrivalToSwitch, startTime, switchID, segment);
		this.ctrlMessage = controlMessage;
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(time);
		if (ctrlMessage != null) {
			//Debugger.event(this.type, this.time, this.nodeID, null, ctrlMessage);
			return net.switches.get(this.nodeID).recvCtrlMessage(net, ctrlMessage);
		} else {
			//Debugger.event(this.type, this.time, this.nodeID, this.segment, null);
			return net.switches.get(this.nodeID).recvSegment(net, segment);
		}
	}

}
