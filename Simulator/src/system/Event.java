package system;

import entities.*;
import utilities.Logger;

public class Event {
	private Logger log = new Logger();

	/** Special Strings **/
	private final String ArrivalEvent = "ARRIVAL";
	private final String DepartureEvent = "DEPAARTURE";
	private final String TCPTimeOutEvent = "TCPTIMEOUT";

	protected double time;
	protected String event_type;
	protected Segment segment;
	protected SDNSwitch node; // This Node does not possess any STATE Variable. Be careful to use it as only
							// it is an ID!

	// TODO Must be set through simulator by user
	/*------------  Constant Values for different types of delays -----------*/
	protected final double CONTROLLER_RTT_DELAY = 1.0;
	protected final double CONTROLLER_PROCESS_DELAY = 1.0;
	protected final double NODE_PROCESS_DELAY = 1.0;
	/*-----------------------------------------------------------------------*/

	private double next_time;
	private String next_type;
	private SDNSwitch next_node;

	public Event(double start_t, String event_type, Segment s, SDNSwitch node) {
		this.time = start_t;
		this.event_type = event_type;
		this.segment = s;
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
		log.eventInfo(this.segment.getFlowLabel(), this.segment.getType(), this.node.getLabel(),
				Double.toString(this.time));

		/* Updating the node for new state */
		SDNSwitch updated_node = net.switches.get(this.node.getLabel());

		switch (this.event_type) {
		/* ################## Arrival event ######################## */
		case ArrivalEvent:
			log.captureCase("Event", "execute", ArrivalEvent);

			if (segment.hasArrived(updated_node)) {
				log.arrivalOfPaket(this.segment.getSeqNum(), this.node.getLabel());

				/*
				 * Informing the flow agent that the segment has arrived - using recv() method
				 */
				updated_node.agents.get(segment.getFlowLabel()).recv(net, segment);
			} else {
				/* Check if segment type is SYN */
				// if (!node.hasFlowEntry(segment.getFlowLabel())) {
				if (this.segment.getType().equals("SYN")) {
					/******************************************************************/
					/********** Flow Entry dose not exists in the Flow Table **********/
					/******************************************************************/
					// This is the part that the flow has arrived to the access switch.
					
					
					/* The node informs the controller with the new flow */
					net = net.controller.newFlow(net, node, segment);

					/* Checking the occupancy of the buffer upon segment arrival */
					if (updated_node.getEgressLink(segment.getFlowLabel()).buffer.isFull()) {
						/** The buffer is full **/

						// The statistics should be updated for a segment drop

					} else {
						/** The buffer has available space **/

						/* Generating next Arrival event */

						// Transmission Delay
						Double trans_delay = updated_node.getEgressLink(segment.getFlowLabel())
								.getTransmissionDelay(segment.getSize());

						// Queuing Delay
						Double queue_delay = updated_node.getEgressLink(segment.getFlowLabel()).buffer
								.getWaitTime(trans_delay, this.time);

						// Processing Delay
						Double process_delay = NODE_PROCESS_DELAY + CONTROLLER_RTT_DELAY + CONTROLLER_PROCESS_DELAY;

						// Calculating next_time
						next_time = this.time + queue_delay + process_delay + trans_delay;

						/* Updating next_type */
						next_type = DepartureEvent;

						/* Updating next_node */
						next_node = node;

						// Generate next arrival event
						net.eventList.generateEvent(next_time, next_type, segment, next_node);
					}

				} else {
					/*********************************************************/
					/********** Flow Entry Exists in the Flow Table **********/
					/*********************************************************/

					/* Checking the occupancy of the buffer upon segment arrival */
					if (updated_node.getEgressLink(segment.getFlowLabel()).buffer.isFull()) {
						/** The buffer is full **/

						// The statistics should be updated for a segment drop

					} else {
						/** The buffer has available space **/

						/* Generating next Arrival event */

						// Transmission Delay
						Double trans_delay = updated_node.getEgressLink(segment.getFlowLabel())
								.getTransmissionDelay(segment.getSize());

						// Queuing Delay
						Double queue_delay = updated_node.getEgressLink(segment.getFlowLabel()).buffer
								.getWaitTime(trans_delay, this.time);

						// 2- Processing Delay
						Double process_delay = NODE_PROCESS_DELAY;

						// Calculating next_time
						next_time = this.time + queue_delay + process_delay + trans_delay;

						// Getting next_node
						next_node = node;

						// Updating next_type
						next_type = DepartureEvent;

						// Generate next arrival event
						net.eventList.generateEvent(next_time, next_type, segment, next_node);

					}
				}
			}
			/* Update the Log */

			break;
		/* ################## Departure event ######################## */
		case DepartureEvent:
			log.captureCase("Event", "execute", DepartureEvent);
			// The departure case is for updating the state of the buffers (occupancy). Now
			// that we have departures, creating of new arrivals can come to this part too.
			// This means the arrival event checks for delivery to destination node or
			// updating the buffer occupancy of buffer and newFlow entry.

			/* Updating the corresponding buffer occupancy state */
			updated_node.getEgressLink(segment.getFlowLabel()).buffer.deQueue();

			/* Creating the next Arrival event */
			/* Generating next Arrival event */

			// Propagation Delay
			Double prop_delay = updated_node.getEgressLink(segment.getFlowLabel()).getPropagationDelay();

			// Updating next_time
			next_time = this.time + prop_delay;

			// Getting next_node
			next_node = node.getEgressLink(segment.getFlowLabel()).getDst();

			// Updating next_type
			next_type = ArrivalEvent;

			// Generate next arrival event
			net.eventList.generateEvent(next_time, next_type, segment, next_node);

			// Right now we do not need Departure event
			break;
		/* ################## TimeOut event ######################## */
		case TCPTimeOutEvent:
			log.captureCase("Event", "run", TCPTimeOutEvent);

			/* Calling TimeOut method of the corresponding agent */

			break;
		default:
			System.out.println("Error: Event.run() - Invalid event type (" + this.event_type + ")");
			break;
		}

		/* Updating nodes and agents (and its flow) of Network object */
		net.switches.put(updated_node.getLabel(), updated_node);

		return net;
	}
}
