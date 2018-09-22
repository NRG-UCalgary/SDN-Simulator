package system;

import java.util.HashMap;
import java.util.Map;

import entities.*;
import protocols.*;

public class Network {
	private Logger log = new Logger();

	public Map<String, Node> nodes = new HashMap<String, Node>();
	public Map<String, Flow> flows = new HashMap<String, Flow>();
	public Controller controller = new Controller(this, "Dijkstra");
	public double time = 0.0;

	public EventList event_List = new EventList();

	// Agents represent Transport layer module.
	// They get information from the events (if the packet has arrived to its
	// destination) and
	// give back needed instructions to the network. This instruction can be the
	// creation of new
	// packets (ACKs or next Sequence number of the data packets.)
	// The question is, Do we need a Map of Flows when we have a map of Agents?
	// Isn't it possible to create a member of type Flow for each Agent?

	// Agents can be retrieved by the flow's label
	public Map<String, Agent> agents = new HashMap<String, Agent>();

	public Network() {
	}

}
