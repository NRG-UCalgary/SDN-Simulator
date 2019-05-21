package system;

import utilities.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventList {
	private Logger log = new Logger();

	public ArrayList<Event> events = new ArrayList<Event>();

	public EventList() {
	}

	public void addEvent(Event newEvent) {
		log.entranceToMethod("EventList", "generateEvent");
		events.add(newEvent);
		// Sorting the events based on their happening time
		Collections.sort(events, timeComparator);
	}

	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	public static Comparator<Event> timeComparator = new Comparator<Event>() {
		@Override
		public int compare(Event e1, Event e2) {
			if (e1.currentTime < e2.currentTime) {
				return -1;
			} else if (e1.currentTime > e2.currentTime) {
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
