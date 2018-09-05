package system;

import entities.*;

public class Event {
	protected double time;
	protected String type;
	protected Flow flow;
	protected int packet_id;
	protected Node node;

	/*------------  Constant Values for different types of delays -----------*/
	protected final double CONTROLLER_RTT_DELAY = 0.0;
	protected final double CONTROLLER_PROCESS_DELAY = 0.0;
	/*-----------------------------------------------------------------------*/

	private double next_time;
	private String next_type;
	private Node next_node;

	public Event(double start_t, String type, Flow f, int p_id, Node node) {
		this.time = start_t;
		this.type = type;
		this.flow = f;
		this.packet_id = p_id;
		this.node = node;
	}

	public Network execute(Network net) {

		switch (this.type) {
		case "First-Packet":
			System.out.println("First-Packet: Got it!");

			/* The first packet of the flow arrives to the first switch node */
			// The Node Sends a Request packet to the controller to get the forwarding rule
			// for the flow
			// This should be implemented as a public method in controller
			net = net.controller.newFlow(net, node, flow);
			// This communication means the next arrival event should be created with at the
			// time after this one. this should be considered somehow.
			//
			/* An Arrival event should be created */
			// It can be assumed that the switch has contacted the controller and the
			// timings have been considered.

			/* Updating next_time */
			// 1- There should be control_delay that represents the Ropund_trip travel of
			// control packet to the Controller
			next_time = next_time + CONTROLLER_RTT_DELAY;

			// 2- There should be a process_delay that represents the process time need for
			// the controller to decide what order to give to the requesting Node
			next_time = next_time + CONTROLLER_PROCESS_DELAY;

			/* Updating next_type */
			// The type of the next event is generic Arrival
			next_type = "Arrival";

			/* Updating flow, packet_id and next_node(or Link?) */

			// Generate next arrival event
			net.event_List.generateEvent(next_time, next_type, flow, packet_id, next_node);
		case "Arrival":
			System.out.println("Arrival: Got it!");

			/* Checking the occupancy of the buffer upon packet arrival */
			if (net.nodes.get(node.getLabel()).getBuffer().isFull()) {

				/* Generating a Drop event */
				// There is no need for Drop event. We can update statistical counters

			} else { // The buffer has available space

				/* Check if the packet has arrived the destination */
				if (net.flows.get(flow.getLabel()).hasArrived(net.nodes.get(node.getLabel()))) {

					/*
					 * Informing the flow agent that the packet has arrived - using listener()
					 * method
					 */
					net = net.flows.get(flow.getLabel()).agent.listener(net, "recv");
				} else {// The packet is ready for the departure
					/* Generating next Arrival event */

					// Getting next_node_id

					/* Calculating all types of delays for the packet */
					// 1- Queuing Delay
					// Getting wait time from the buffer
					Double queue_delay = net.nodes.get(node.getLabel()).getBuffer().getWaitTime();

					// 2- Processing Delay
					Double process_delay = 0.0; // Some constant that should be set by the simulator settings
					net.nodes.get(node.getLabel()).getBuffer()
							.updateDepartureTime(this.time + process_delay + queue_delay);
					// 3-Propagation Delay 4- Transmission Delay
					Double prob_delay = net.getLink(net.nodes.get(node.getLabel()), net.nodes.get(next_node.getLabel()))
							.getPropagationDelay();
					Double trans_delay = net
							.getLink(net.nodes.get(node.getLabel()), net.nodes.get(next_node.getLabel()))
							.getTransmissionDelay(net.flows.get(flow.getLabel()).getPacketSize());

					// Calculating next_time
					next_time = this.time + queue_delay + process_delay + prob_delay + trans_delay;

					// Generate next arrival event
					net.event_List.generateEvent(next_time, next_type, flow, packet_id, next_node);
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
