package entities;

import utilities.Logger;

public class Link {
	private Logger log = new Logger();

	public Buffer buffer;

	private int bandwidth;
	private double propDelay;
	private Node srcNode;
	private Node dstNode;
	private String label;

	public Link(String label, Node source, Node destination, double propagation_Delay, int band, int buffer_size,
			String buffer_policy) {
		this.label = label;
		this.bandwidth = band; // Mega_bits/second
		this.propDelay = propagation_Delay; // millisecond 
		this.srcNode = source;
		this.dstNode = destination;

		buffer = new Buffer(buffer_size, buffer_policy);
	}

	public double getTransmissionDelay(int packet_size) {
		log.entranceToMethod("Link", "getTransmissionDelay");
		Double trans_delay = packet_size / (double) this.bandwidth;
		log.generalLog("Transmision is: " + trans_delay);
		return trans_delay;
	}

	/*------------------- Getters and Setters  ------------------------*/
	public int getBandwidth() {
		return this.bandwidth;
	}

	public Double getPropagationDelay() {
		return this.propDelay;
	}

	public Node getSrc() {
		return this.srcNode;
	}

	public Node getDst() {
		return this.dstNode;
	}
	/*--------------------------------------------------------------*/
}
