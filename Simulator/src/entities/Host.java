package entities;

import system.Main;
import system.Network;

public class Host extends Node {
	/* Each host should be connected to an access SDNSwitch */
	public Link accessLink;
	public int accessSwitchID;
	public Agent transportAgent;

	/* Each host has string label */
	public String label;

	public Host(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public Network recvSegment(Network net, Segment segment) {
		net = transportAgent.recvSegment(net, segment);
		return net;
	}

	public Network releaseSegment(Network net, Segment segment) {
		Main.print("We shall never get here for now");
		return net;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ########## Public ################################# */
	// TODO this method must be called for initialization
	public Network startSending(Network net) {
		return transportAgent.start(net);
	}

	public double getAccessLinkDelay(int segmentSize) {
		return accessLink.getTransmissionDelay(segmentSize) + accessLink.getPropagationDelay();
	}

	public void connectToNetwork(int accessSwitchID, Link accessLink) {
		this.accessLink = accessLink;
		this.accessSwitchID = accessSwitchID;
	}

	public void setAgent(Agent agent) {
		this.transportAgent = agent;
	}

	/** Called in Class::Event.run() **/
	/* Objective::Showing the egress-link for the desired destination Node */
	public Link getEgressLink() {
		return this.accessLink;
	}

}
