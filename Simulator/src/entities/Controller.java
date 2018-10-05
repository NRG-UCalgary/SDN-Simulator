package entities;

import system.*;
import routings.*;

import java.util.Map;

public class Controller {
	private Logger log = new Logger();

	// TODO router type must be set by the client through the simulator
	public Dijkstra router;

	/*------------  Constant Values for different types of delays -----------*/
	public double CONTROLLER_RTT_DELAY = 1.0;
	public double CONTROLLER_PROCESS_DELAY = 1.0;
	/*-----------------------------------------------------------------------*/

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
	public Network newFlow(Network net, Node curr_node, Packet packet) {
		log.entranceToMethod("Controller", "newFlow");

		/* Controller runs the router module to find the path for the flow */
		Map<Node, Link> result = router.run(curr_node, packet.getDestination());

		/* Controller updates flow tables of all switches in the flow path */
		for (Node n : result.keySet()) {
			net.nodes.get(n.getLabel()).updateFlowTable(packet.getFlowLabel(), result.get(n));
		}
		/* Update the destination flow entry */
		// TODO in future hosts and switches can be separate (inherited from node) and
		// TODO this line must be updated accordingly
		// net.nodes.get(packet.getDestination().getLabel()).updateFlowTable(packet.getFlowLabel(),
		// null);

		return net;
	}

	public void test(String s) {

	}

	public void test() {
		String sis = "hey";
		test(sis);
	}

}
