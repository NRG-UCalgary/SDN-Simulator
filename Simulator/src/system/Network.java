package system;

import java.util.HashMap;
import java.util.Map;

import entities.*;

public class Network {
	public Map<String, Node> nodes = new HashMap<String, Node>();
	public Map<String, Flow> flows = new HashMap<String, Flow>();
	public Controller controller = new Controller(this, "Dijkstra");
	public Logger log = new Logger();

	public EventList event_List = new EventList();

	public Network() {
	}

	public void initialize() {

		/* Generating traffic paths of flows */
		// this.flows = routing_alg.getPaths(flows);'
	}

	/* Called in Class::Event */
	/* Input: node1, node2 -- Output: corresponding Link */
	
	/*
	public Link getLink(Node src, Node dst) {
		for (Map.Entry<String, Link> entry : links.entrySet()) {
			if (entry.getValue().getSrc().equals(src) && entry.getValue().getDst().equals(dst)) {
				return entry.getValue();
			} else if (entry.getValue().getSrc().equals(dst) && entry.getValue().getDst().equals(src)) {
				return entry.getValue();
			}
		}
		return null;
	}  */
}
