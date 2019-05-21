package events;

import entities.Segment;
import entities.SDNSwitch;
import system.*;

public class Departure extends Event {

	public Departure(double startTime, int switchID, Segment segment) {
		super(startTime, switchID, segment);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		// TODO this event run must be updated
		net.updateTime(currentTime);

		net.switches.get(this.currentNodeID).getEgressLink(currentSegment.getFlowID()).buffer.deQueue();
		SDNSwitch currentSwitch = net.switches.get(this.currentNodeID); // It is assumed that we never have departure
																		// events for Hosts

		// The next node is a host
		if (currentSwitch.isConnectedToHost(currentSegment.getDstHostID())) {
			nextNodeID = currentSegment.getDstHostID();
			nextTime = currentTime
					+ currentSwitch.getAccessLinkDelay(currentSegment.getDstHostID(), currentSegment.getSize());
			nextEvent = new ArrivalToHost(nextTime, nextNodeID, currentSegment);
		}
		// The next node is a switch
		else {
			nextNodeID = net.switches.get(currentSwitch.getNextSwitch(currentSegment.getFlowID())).getID();
			nextTime = currentTime
					+ currentSwitch.getEgressLink(currentSegment.getFlowID())
							.getTransmissionDelay(currentSegment.getSize())
					+ currentSwitch.getEgressLink(currentSegment.getFlowID()).getPropagationDelay();
			nextEvent = new ArrivalToSwitch(nextTime, nextNodeID, currentSegment, null);

		}

		// Adding the next event to the EventList
		net.eventList.addEvent(nextEvent);
		return net;
	}
}
