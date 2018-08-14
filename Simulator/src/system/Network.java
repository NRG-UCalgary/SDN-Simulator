package system;

import network.*;
import routings.*;

public class Network {
	public NetList<Node> nodes = new NetList<Node>();
	public NetList<Link> links = new NetList<Link>();
	public NetList<Flow> flows = new NetList<Flow>();
	public Logger log = new Logger();
	public Routing routing_alg;

	public EventList event_List = new EventList();

	public Network() {
	}

	public void initialize() {

	}

	/* Called in Class::Event */
	/* Input: node1, node2 -- Output: corresponding Link */
	public Link getLink(int node_id_1, int node_id_2) {
		for (Link link : this.links) {
			if (link.getSrc() == this.nodes.get(node_id_1) & link.getDst() == this.nodes.get(node_id_2)) {
				return link;
			}
		}
		return null;
	}
}
