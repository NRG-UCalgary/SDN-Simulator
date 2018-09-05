package entities;

import system.*;
import routings.*;

public class Controller {
	public Dijkstra router;

	public Controller(Network net, String routing_type) {
		switch (routing_type) {
		case "Dijkstra":
			router = new Dijkstra(net.nodes);
			break;
		case "Something Else":
			break;
		default:
			break;
		}
	}

	/** Called in Class::Event.run() **/
	/* Objective::Updating flow_table of the Node */
	public Network newFlow(Network net, Node curr_node, Flow curr_flow) {
		net.nodes.get(curr_node.getLabel()).setTable(router.run(curr_flow.getDst()));
		return net;
	}
}
