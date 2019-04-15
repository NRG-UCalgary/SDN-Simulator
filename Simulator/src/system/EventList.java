package system;

import entities.*;
import utilities.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventList {
	private Logger log = new Logger();

	public ArrayList<Event> events = new ArrayList<Event>();

	public EventList() {
	}

	public void generateEvent(double start_t, String type, Segment packet, SDNSwitch node) {
		log.entranceToMethod("EventList", "generateEvent");

		Event event = new Event(start_t, type, packet, node);
		// Some considerations may be added here about the place of the new event among
		// the events on the events
		events.add(event);

		// Sorting the events based on their happening time
		Collections.sort(events, timeComparator);
	}

	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	public static Comparator<Event> timeComparator = new Comparator<Event>() {
		@Override
		public int compare(Event e1, Event e2) {
			if (e1.time < e2.time) {
				return -1;
			} else if (e1.time > e2.time) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

	public Event getEvent() {
		log.entranceToMethod("EventList", "getEvent");
		Event event = this.events.get(0);
		this.events.remove(0);
		return event;
	}
}
