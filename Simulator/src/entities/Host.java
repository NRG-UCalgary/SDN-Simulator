package entities;

import system.Keywords;

public class Host extends Node {
	/* Each host should be connected to an access SDNSwitch */
	// TODO determine encapsulation level of the attributes
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

	public double getAccessLinkDelay(int hostID, int segmentSize) {
		return accessLink.getTransmissionDelay(segmentSize) + accessLink.getPropagationDelay();
	}
	/* --------------------------------------------------- */

	public void connectToNetwork(int accessSwitchID, Link accessLink) {
		this.accessLink = accessLink;
		this.accessSwitchID = accessSwitchID;
	}

	public void setAgent(Agent agent) {
		this.transportAgent = agent;
	}

	public double getAccessLinkDelay(int segmentSize) {
		Link link = this.accessLink;
		return link.getPropagationDelay() + link.getTransmissionDelay(segmentSize);
	}

	/** Called in Class::Event.run() **/
	/* Objective::Showing the egress-link for the desired destination Node */
	public Link getEgressLink() {
		return this.accessLink;
	}

}
