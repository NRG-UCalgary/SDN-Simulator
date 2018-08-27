package system;

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

			/* Checking the occupancy of the buffer upon packet arrival */
			if (net.nodes.get(node_id).buffer.isFull()) {
				
				/* Generating a Drop event */
				// There is no need for Drop event. We can update statistical counters

			} else { // The buffer has available space
				

				/* Check if the packet has arrived the destination */
				if (net.flows.get(flow_id).hasArrived(net.nodes.get(node_id))) {
					
					/*
					 * Informing the flow agent that the packet has arrived - using listener()
					 * method
					 */
					net = net.flows.get(flow_id).agent.listener(net, "recv");
				} else {// The packet is ready for the departure
					/* Generating next Arrival event */

					// Getting next_node_id
				//	next_node_id = net.flows.get(flow_id).nextNodeID(this.node_id);

					/* Calculating all types of delays for the packet */
					// 1- Queuing Delay
					// Getting wait time from the buffer
					Double queue_delay = net.nodes.get(node_id).buffer.getWaitTime();

					// 2- Processing Delay
					Double process_delay = 0.0; // Some constant that should be set by the simulator settings
					net.nodes.get(node_id).buffer.updateDepartureTime(this.time + process_delay + queue_delay);
					// 3-Propagation Delay 4- Transmission Delay
					Double prob_delay = net.getLink(node_id, next_node_id).getPropagationDelay();
					Double trans_delay = net.getLink(node_id, next_node_id)
							.getTransmissionDelay(net.flows.get(flow_id).getPacketSize());

					// Calculating next_time
					next_time = this.time + queue_delay + process_delay + prob_delay + trans_delay;

					// Generate next arrival event
					net.event_List.generateEvent(next_time, next_type, flow_id, packet_id, next_node_id);
				}

			}
			/* Update the Log */

			break;
		case "Departure":
			System.out.println("Departure: Got it!");
			// Right now we do not need Departure event
			break;
		case "Drop":
			System.out.println("Drop: Got it!");
			// Right now we do not need Drop event
			break;
		default:
			System.out.println("Error: Event.run() - Invalid event type (" + this.type + ")");
			break;
		}
		return net;

	}
}
