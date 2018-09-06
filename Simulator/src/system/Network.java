package system;

import java.util.HashMap;
import java.util.Map;

import entities.*;

public class Network {
	public Map<String, Node> nodes = new HashMap<String, Node>();
	public Map<String, Flow> flows = new HashMap<String, Flow>();
	public Controller controller = new Controller(this, "Dijkstra");
	public Logger log = new Logger();
	public double time = 0.0;

	public EventList event_List = new EventList();

	public Network() {
	}

	public void initialize() {

		
	}
}
