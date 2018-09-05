package entities;

import system.*;
import routings.*;

public class Controller {
	public Routing router;

	public Controller(Network net, String routing_type) {
		switch (routing_type) {
		case "Dijkstra":
			router = new Dijkstra(net.nodes, net.links);
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
		
		
		return net;
	}
	
	/**  **/
}
