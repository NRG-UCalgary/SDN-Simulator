package entities;

import system.*;
import routings.*;

import java.util.Map;

public class Controller {
	private Logger log = new Logger();

	// TODO router type must be set by the client through the simulator
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
		log.generalLog("Entered Controller.newFlow().");

		/* Controller runs the router module to find the path for the flow */
		Map<Node, Link> result = router.run(curr_node, curr_flow.getDst());

		for (Node n : result.keySet()) {
			System.out.println(n.getLabel() + " -----> " + result.get(n).getLabel());
			n.updateFlowTable(curr_flow.getLabel(), result.get(n));
		}
		/* Controller updates flow tables of all switches in the flow path */

		return net;
	}
}
