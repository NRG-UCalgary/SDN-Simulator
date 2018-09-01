package system;

import entities.*;
import java.util.ArrayList;

public class EventList {

	public ArrayList<Event> events = new ArrayList<Event>();

	public EventList() {
		// TODO Auto-generated constructor stub
	}

	public void generateEvent(double start_t, String type, Flow f, int p_id, Node node) {

	}

	public void addEvent(Event e) {
		this.events.add(e);
	}

	public Event getEvent() {
		Event event = this.events.get(0);
		this.events.remove(0);
		return event;
	}
}
