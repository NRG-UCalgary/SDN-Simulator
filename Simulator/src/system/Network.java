package system;

import java.util.HashMap;
import java.util.Map;

import entities.*;
import protocols.*;

public class Network {
	private Logger log = new Logger();

	public Map<String, Node> nodes = new HashMap<String, Node>();
	public Controller controller = new Controller(this, "Dijkstra");
	public double time = 0.0;
	// Agents can be retrieved by the flow's label
	public Map<String, Agent> agents = new HashMap<String, Agent>();

	public EventList event_List = new EventList();

	public Network() {
	}

}
