package entities;

import protocols.*;

public class Host extends Node {
	/* Each host should be connected to an access SDNSwitch */
	// TODO determine encapsulation level of the attributes
	public Link accessLink;
	public String accessSwitchLabel;
	public Agent transportAgent;

	/* Each host has string label */
	public String label;

	public Host(String label) {
		super(label);
	}

	public void connectToNetwork(Link accessLink, String accessSwitchLabel) {
		this.accessLink = accessLink;
		this.accessSwitchLabel = accessSwitchLabel;
	}

	public void setAgent(Agent agent) {
		this.transportAgent = agent;
	}

	public double getAccessLinkDelay(int segmentSize) {
		Link link = this.accessLink;
		return link.getPropagationDelay() + link.getTransmissionDelay(segmentSize);
	}

}
