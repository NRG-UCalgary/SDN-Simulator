package system;

import network.*;

public class Event {
	protected double time;
	protected String type;
	protected int flow_id;
	protected int packet_id;
	protected int node_id;

	private double next_time;
	private String next_type;
	private int next_node_id;

	public Event(double start_t, String type, int f_id, int p_id, int node_id) {
		this.time = start_t;
		this.type = type;
		this.flow_id = f_id;
		this.packet_id = p_id;
		this.node_id = node_id;
	}

	public Network execute(Network net) {

		switch (this.type) {
		case "Arrival":
			System.out.println("Arrival: Got it!");

			/* Handling the node Queue State */
			if (net.nodes.get(node_id).buffer.enQueue().equals("ENQ")) {

			}

			/* Checking the occupancy of the buffer upon packet arrival */
			if (net.nodes.get(node_id).buffer.isFull()) {
				/* Generating a Drop event */
			} else {
				
				
				
				/* Check if the packet has arrived the destination */
				if (net.flows.get(flow_id).hasArrived(net.nodes.get(node_id))) {
					net = net.flows.get(flow_id).agent.listener(net, "recv");
				} else {
					// Updating next_node_id
					next_node_id = net.flows.get(flow_id).nextNodeID(this.node_id);

					// Calculate next_time
					Link link = net.getLink(node_id, next_node_id);
					// Generate next arrival event
					net.event_List.generateEvent(next_time, next_type, flow_id, packet_id, next_node_id);
				}

			}
			/* Update the Log */

			break;
		case "Departure":
			System.out.println("Departure: Got it!");

			break;
		case "Drop":
			System.out.println("Drop: Got it!");
			break;
		default:
			System.out.println("Error: Event.run() - Invalid event type (" + this.type + ")");
			break;
		}
		return net;

	}
}
