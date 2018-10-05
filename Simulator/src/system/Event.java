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

		/* Update net time to the event time */
		net.time = this.time;
		log.eventInfo(this.packet.getFlowLabel(), this.node.getLabel(), Double.toString(this.time));
		/* Updating the node for new state */
		Node updated_node = net.nodes.get(this.node.getLabel());

		switch (this.event_type) {
		/* ################## Arrival event ######################## */
		case "Arrival":
			log.generalLog("Event.execute()::Captured Arrival case.");
			log.captureCase("Event", "execute", "Arrival");

			if (packet.hasArrived(updated_node)) {
				log.generalLog("Packet has arrived to node: " + this.node.getLabel());

				/*
				 * Informing the flow agent that the packet has arrived - using recv() method
				 */
				updated_node.agents.get(packet.getFlowLabel()).recv(net, packet.getType());
			} else {
				/* Check if the flow entry exists in node's flow table */
				if (!node.hasFlowEntry(packet.getFlowLabel())) {
					/******************************************************************/
					/********** Flow Entry dose not exists in the Flow Table **********/
					/******************************************************************/
					// newFlow() of controller should be called so that the flow table get updated
					/* The first packet of the flow arrives to the first switch node */
					// The Node Sends a Request packet to the controller to get the forwarding rule
					// for the flow
					// This should be implemented as a public method in controller
					net = net.controller.newFlow(net, node, packet);
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
					next_node = node.getEgressLink(packet.getFlowLabel()).getDst(); // There is a possibility that the
																					// Dst
																					// node
																					// of the
					// desired Link to be the same as the Current Node
					// that we have called its getEgressLink(). One way
					// to solve this issue is to give the Current Node
					// to the Link.getDst() method and complete the
					// checking in that method.

					// Generate next arrival event
					net.event_List.generateEvent(next_time, next_type, this.packet, next_node);

				} else {
					/*********************************************************/
					/********** Flow Entry Exists in the Flow Table **********/
					/*********************************************************/
					/* Checking the occupancy of the buffer upon packet arrival */
					if (updated_node.getEgressLink(packet.getFlowLabel()).buffer.isFull()) {

						/* Generating a Drop event */
						// There is no need for Drop event. We can update statistical counters
						// Also, there should be a mechanism to manage timers and time-outs of the
						// Transport layer protocols. But it's not here (I suppose).

					} else { // The buffer has available space

						/* Generating next Arrival event */

						// Getting next_node_id

						/* Calculating all types of delays for the packet */
						// 1- Queuing Delay
						// Getting wait time from the buffer
						Double queue_delay = updated_node.getEgressLink(packet.getFlowLabel()).buffer.getWaitTime();

						// 2- Processing Delay
						Double process_delay = 0.0; // Some constant that should be set by the simulator settings
						updated_node.getEgressLink(packet.getFlowLabel()).buffer
								.updateDepartureTime(this.time + process_delay + queue_delay);
						// 3-Propagation Delay 4- Transmission Delay
						Double prob_delay = updated_node.getEgressLink(packet.getFlowLabel()).getPropagationDelay();
						Double trans_delay = updated_node.getEgressLink(packet.getFlowLabel())
								.getTransmissionDelay(packet.getSize());

						// Calculating next_time
						next_time = this.time + queue_delay + process_delay + prob_delay + trans_delay;
						next_node = node.getEgressLink(packet.getFlowLabel()).getDst();
						next_type = "Arrival";

						// Generate next arrival event
						net.event_List.generateEvent(next_time, next_type, packet, next_node);

					}
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
			System.out.println("Error: Event.run() - Invalid event type (" + this.event_type + ")");
			break;
		}

		/* Updating nodes and agents (and its flow) of Network object */
		// updated_agent.setFlow(updated_flow);
		// updated_node.agents.put(this.packet.getFlowLabel(), updated_agent);
		net.nodes.put(updated_node.getLabel(), updated_node);

		return net;
	}
}
