package simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import simulator.entities.Packet;
import simulator.events.arrivals.ToController;
import simulator.events.arrivals.ToHost;
import simulator.events.arrivals.ToSwitch;
import simulator.events.departures.FromController;
import simulator.events.departures.FromHost;
import simulator.events.departures.FromSwitch;

public class EventList {

	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	public static Comparator<Event> timeComparator = new Comparator<Event>() {
		@Override
		public int compare(Event e1, Event e2) {
			if (e1.eventTime < e2.eventTime) {
				return -1;
			} else if (e1.eventTime > e2.eventTime) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

	public ArrayList<Event> events = new ArrayList<Event>();

	public EventList() {
	}

	public void addArrivalToController(float eventTime, int nodeID, Packet packet) {
		events.add(new ToController(eventTime, nodeID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addArrivalToHost(float eventTime, int hostID, Packet packet) {
		events.add(new ToHost(eventTime, hostID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addArrivalToSwitch(float eventTime, int switchID, Packet packet) {
		events.add(new ToSwitch(eventTime, switchID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addDepartureFromController(float eventTime, int controllerID, int dstSwitchID, Packet packet) {
		events.add(new FromController(eventTime, controllerID, dstSwitchID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addDepartureFromHost(float eventTime, int hostID, int dstSwitchID, Packet packet) {
		events.add(new FromHost(eventTime, hostID, dstSwitchID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addDepartureFromSwitch(float eventTime, int switchID, int dstNodeID, Packet packet) {
		events.add(new FromSwitch(eventTime, switchID, dstNodeID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addEvent(Event newEvent) {
		events.add(newEvent);
		// Sorting the events based on their happening time
		Collections.sort(events, timeComparator);
	}

	public Event getEvent() {
		Event event = this.events.get(0);
		return event;
	}
	public void removeEvent() {
		this.events.remove(0);
	}

	public int size() {
		return events.size();
	}
}
