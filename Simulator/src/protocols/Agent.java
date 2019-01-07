package protocols;

import system.*;
import entities.*;

public abstract class Agent {
	protected Logger log = new Logger();

	/** Special Strings **/
	protected final String SYN = "SYN";
	protected final String SYNACK = "SYNACK";
	protected final String ACK = "ACK";
	protected final String DATA = "DATA";
	protected final String FIN = "FIN";
	protected final String ACKFlowExtention = ".ACK";

	protected final String SlowStart = "SS";
	protected final String CongAvoidance = "CA";
	protected final String FastRecovery = "FR";

	protected final String ArrivalEvent = "ARRIVAL";

	protected Node src;
	protected Node dst;
	protected int size;

	protected Flow flow;

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
	}

	// This function may be overridden in transport protocol implementations
	public Network recv(Network net, Segment packet) {
		// if the received packet is a Data-Packet, an ACK packet should be created

		// if the received packet is an Ack-Packet, the next Data-Packet should be sent.

		return net;
	}

	public Network timeOut(Network net, Segment packet) {
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
