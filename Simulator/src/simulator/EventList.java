package simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventList {
	private ArrayList<Event> events = new ArrayList<Event>();

	public EventList() {
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

	public void addEvent(Event newEvent) {
		events.add(newEvent);
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
