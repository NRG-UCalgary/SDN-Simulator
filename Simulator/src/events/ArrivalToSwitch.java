package events;

import system.*;

import entities.*;

public class ArrivalToSwitch extends Event {

	private CtrlMessage currentCtrlMessage;

	public ArrivalToSwitch(double startTime, int switchID, Segment segment, CtrlMessage controlMessage) {
		super(startTime, switchID, segment);
		name = "Arrival to Switch";
		this.currentCtrlMessage = controlMessage;
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(currentTime);
		if (currentCtrlMessage != null) {
			return net.switches.get(this.currentNodeID).recvCtrlMessage(net, currentCtrlMessage);
		} else {
			Main.debug("ArrivalToSwitch.execute()::Switch ID = " + this.currentNodeID);
			return net.switches.get(this.currentNodeID).recvSegment(net, currentSegment);
		}
	}

}
