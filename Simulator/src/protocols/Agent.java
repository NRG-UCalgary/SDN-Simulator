package protocols;

import system.*;
import utilities.Logger;
import entities.*;

public abstract class Agent {
	protected Logger log = new Logger();

	/** Special Strings **/
	protected final String SYN = "SYN";
	protected final String SYNACK = "SYNACK";
	protected final String ACK = "ACK";
	protected final String DATA = "DATA";
	protected final String FIN = "FIN";
	protected final String FINACK = "FINACK";
	protected final String ACKFlowExtention = ".ACK";
	protected final String TCPTimeOutEvent = "TCPTIMEOUT";

	protected final String SlowStart = "SS";
	protected final String CongAvoidance = "CA";
	protected final String FastRecovery = "FR";

	protected final String ArrivalEvent = "ARRIVAL";

	protected final double ACKGenerationTime = 0.5; // usually is less than 500MS

	protected final int DataSegSize = 1000;
	protected final int ACKSegSize = 40;
	protected final int SYNSegSize = 40;
	protected final int FINSegSize = 40;
	protected final int SlowStartSSThreshFactor = 64;
	protected final int FastRecoveryCWNDDivindingFactore = 2;
	protected final int TimeOutSlowStartCWNDDivindingFactore = 2;
	protected final int SYNSeqNum = 0;

	protected SDNSwitch srcNode;
	protected SDNSwitch dstNode;
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
