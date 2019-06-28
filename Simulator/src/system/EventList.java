package system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventList {

	public ArrayList<Event> events = new ArrayList<Event>();

	public EventList() {
	}

	public void addEvent(Event newEvent) {
		events.add(newEvent);
		// Sorting the events based on their happening time
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
		this.events.remove(0);
		return event;
	}

	public int size() {
		return events.size();
	}
}
