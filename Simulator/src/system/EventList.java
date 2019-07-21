package system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import entities.*;
import system.events.*;

public class EventList {

	public ArrayList<Event> events = new ArrayList<Event>();

	public EventList() {
	}

	public void addEvent(Event newEvent) {
		events.add(newEvent);
		// Sorting the events based on their happening time
		Collections.sort(events, timeComparator);
	}

	public void addArrivalToController(float eventTime, int nodeID, Packet packet) {
		events.add(new ArrivalToController(eventTime, nodeID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addArrivalToHost(float eventTime, int hostID, Packet packet) {
		events.add(new ArrivalToHost(eventTime, hostID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addArrivalToSwitch(float eventTime, int switchID, Packet packet) {
		events.add(new ArrivalToSwitch(eventTime, switchID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addDepartureFromController(float eventTime, int controllerID, int dstSwitchID, Packet packet) {
		events.add(new DepartureFromController(eventTime, controllerID, dstSwitchID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addDepartureFromHost(float eventTime, int hostID, int dstSwitchID, Packet packet) {
		events.add(new DepartureFromHost(eventTime, hostID, dstSwitchID, packet));
		Collections.sort(events, timeComparator);
	}

	public void addDepartureFromSwitch(float eventTime, int switchID, int dstNodeID, Packet packet) {
		events.add(new DepartureFromSwitch(eventTime, switchID, dstNodeID, packet));
		Collections.sort(events, timeComparator);
	}

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
