package system;

import entities.*;

public class Event {
	private Logger log = new Logger();

	protected double time;
	protected String event_type;
	protected Packet packet;
	protected Node node; // This Node does not possess any STATE Variable. Be careful to use it as only
							// it is an ID!

	// TODO Must be set through simulator by user
	/*------------  Constant Values for different types of delays -----------*/
	protected final double CONTROLLER_RTT_DELAY = 1.0;
	protected final double CONTROLLER_PROCESS_DELAY = 1.0;
	protected final double NODE_PROCESS_DELAY = 1.0;
	/*-----------------------------------------------------------------------*/

	private double next_time;
	private String next_type;
	private Node next_node;

	public Event(double start_t, String event_type, Packet p, Node node) {
		this.time = start_t;
		this.event_type = event_type;
		this.packet = p;
		this.node = node;

		this.next_time = 0;
		this.next_type = null;
		this.next_node = null;
	}

	public Network execute(Network net) {
		log.entranceToMethod("Event", "execute");
		log.generalLog("Event type: " + this.event_type);

		/* Initializing next_variables for the new event */
		this.next_time = 0;
		this.next_type = null;
		this.next_node = null;

		/* Update net time to the event time */
		net.time = this.time;
		log.eventInfo(this.packet.getFlowLabel(), this.packet.getType(), this.node.getLabel(),
				Double.toString(this.time));

		/* Updating the node for new state */
		Node updated_node = net.nodes.get(this.node.getLabel());

		switch (this.event_type) {
		/* ################## Arrival event ######################## */
		case "Arrival":
			log.captureCase("Event", "execute", "Arrival");

			if (packet.hasArrived(updated_node)) {
				log.arrivalOfPaket(this.packet.getSeqNum(), this.node.getLabel());

				/*
				 * Informing the flow agent that the packet has arrived - using recv() method
				 */
				updated_node.agents.get(packet.getFlowLabel()).recv(net, packet);
			} else {
				/* Check if the flow entry exists in node's flow table */
				if (!node.hasFlowEntry(packet.getFlowLabel())) {
					/******************************************************************/
					/********** Flow Entry dose not exists in the Flow Table **********/
					/******************************************************************/

					/* The node informs the controller with the new flow */
					net = net.controller.newFlow(net, node, packet);

					/* Checking the occupancy of the buffer upon packet arrival */
					if (updated_node.getEgressLink(packet.getFlowLabel()).buffer.isFull()) {
						/** The buffer is full **/

						// The statistics should be updated for a packet drop

					} else {
						/** The buffer has available space **/

						/* Generating next Arrival event */

						// Transmission Delay
						Double trans_delay = updated_node.getEgressLink(packet.getFlowLabel())
								.getTransmissionDelay(packet.getSize());

						// Queuing Delay
						Double queue_delay = updated_node.getEgressLink(packet.getFlowLabel()).buffer
								.getWaitTime(trans_delay, this.time);

						// Processing Delay
						Double process_delay = NODE_PROCESS_DELAY + CONTROLLER_RTT_DELAY + CONTROLLER_PROCESS_DELAY;

						// Calculating next_time
						next_time = this.time + queue_delay + process_delay + trans_delay;

						/* Updating next_type */
						next_type = "Departure";

						/* Updating next_node */
						next_node = node;

						// Generate next arrival event
						net.event_List.generateEvent(next_time, next_type, packet, next_node);
					}

				} else {
					/*********************************************************/
					/********** Flow Entry Exists in the Flow Table **********/
					/*********************************************************/

					/* Checking the occupancy of the buffer upon packet arrival */
					if (updated_node.getEgressLink(packet.getFlowLabel()).buffer.isFull()) {
						/** The buffer is full **/

						// The statistics should be updated for a packet drop

					} else {
						/** The buffer has available space **/

						/* Generating next Arrival event */

						// Transmission Delay
						Double trans_delay = updated_node.getEgressLink(packet.getFlowLabel())
								.getTransmissionDelay(packet.getSize());

						// Queuing Delay
						Double queue_delay = updated_node.getEgressLink(packet.getFlowLabel()).buffer
								.getWaitTime(trans_delay, this.time);

						// 2- Processing Delay
						Double process_delay = NODE_PROCESS_DELAY;

						// Calculating next_time
						next_time = this.time + queue_delay + process_delay + trans_delay;

						// Getting next_node
						next_node = node;

						// Updating next_type
						next_type = "Departure";

						// Generate next arrival event
						net.event_List.generateEvent(next_time, next_type, packet, next_node);

					}
				}
			}
			/* Update the Log */

			break;
		case "Departure":
			log.captureCase("Event", "execute", "Departure");
			// The departure case is for updating the state of the buffers (occupancy). Now
			// that we have departures, creating of new arrivals can come to this part too.
			// This means the arrival event checks for delivery to destination node or
			// updating the buffer occupancy of buffer and newFlow entry.

			/* Updating the corresponding buffer occupancy state */
			updated_node.getEgressLink(packet.getFlowLabel()).buffer.deQueue();

			/* Creating the next Arrival event */
			/* Generating next Arrival event */

			// Propagation Delay
			Double prop_delay = updated_node.getEgressLink(packet.getFlowLabel()).getPropagationDelay();

			// Updating next_time
			next_time = this.time + prop_delay;

			// Getting next_node
			next_node = node.getEgressLink(packet.getFlowLabel()).getDst();

			// Updating next_type
			next_type = "Arrival";

			// Generate next arrival event
			net.event_List.generateEvent(next_time, next_type, packet, next_node);

			// Right now we do not need Departure event
			break;
		case "TCP-TimeOut":
			log.captureCase("Event", "run", "TCP-TimeOut");

			/* Calling TimeOut method of the corresponding agent */

			break;
		default:
			System.out.println("Error: Event.run() - Invalid event type (" + this.event_type + ")");
			break;
		}

		/* Updating nodes and agents (and its flow) of Network object */
		net.nodes.put(updated_node.getLabel(), updated_node);

		return net;
	}
}
