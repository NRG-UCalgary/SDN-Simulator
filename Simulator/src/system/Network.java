package system;

import java.util.HashMap;
import java.util.Map;

import entities.*;

public class Network {
	private Logger log = new Logger();

	public Map<String, Node> nodes;
	public Controller controller;
	public double time = 0.0;
	public EventList event_List;

	public Network() {
		nodes = new HashMap<String, Node>();
		event_List = new EventList();
	}

}
