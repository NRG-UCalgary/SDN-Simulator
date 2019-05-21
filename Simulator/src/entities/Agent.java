package entities;

import system.*;

public abstract class Agent {

	protected final double ACKGenerationTime = 0.5; // usually is less than 500MS

	protected int srcHostID;
	protected int dstHostID;
	protected int size;

	protected Flow flow;

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
	}

	// This function may be overridden in transport protocol implementations
	public Network recv(Network net, Segment segment) {
		// if the received packet is a Data-Packet, an ACK packet should be created

		// if the received packet is an Ack-Packet, the next Data-Packet should be sent.

		return net;
	}

	public Network timeOut(Network net, Segment segment) {
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
