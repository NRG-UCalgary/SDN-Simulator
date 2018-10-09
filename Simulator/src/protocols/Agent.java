package protocols;

import system.*;
import entities.*;

public abstract class Agent {
	protected Logger log = new Logger();

	protected Node src;
	protected Node dst;
	protected int size;

	protected Flow flow;

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
	}

	// This function may be overridden in transport protocol implementations
	public Network recv(Network net, Packet packet) {
		// if the received packet is a Data-Packet, an ACK packet should be created

		// if the received packet is an Ack-Packet, the next Data-Packet should be sent.

		return net;
	}
	
	public Network timeOut(Network net, Packet packet) {
		return net;
	}

	// This method shall be overridden in any implementation of the class Agent
	public Network start(Network net) {

		return net;
	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/
	public Flow getFlow() {
		return this.flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	/***********************************************************************/

}
