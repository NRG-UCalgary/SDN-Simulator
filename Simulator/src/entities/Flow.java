package entities;

import protocols.*;

import java.util.ArrayList;

public class Flow {
	private String label;
	private String type;
	private Node source;
	private Node destination;
	private int size;
	private int packet_size;
	private double arrival_time;
	private double start_time;
	private double end_time;
	public Agent agent;

	/** Is it needed? **/
	public ArrayList<Packet> packets = new ArrayList<Packet>();

	/* Stores the Node IDs of the path */
	/* Initialized at the creation of the flow */
	public ArrayList<Node> path = new ArrayList<Node>();

	public Flow(String label, String type, Node src, Node dst, int size, Double arrv_time) {
		this.label = label;
		this.type = type;
		this.source = src;
		this.destination = dst;
		this.size = size;
		this.arrival_time = arrv_time;

		/* --------------- New Protocols Must Be Added Here ---------------- */
		switch (type) {
		case "TCP":
			agent = new TCP();
			break;
		case "RBTCP":
			agent = new RBTCP();
			break;
		case "Dummy":
			agent = new Agent();
			break;
		default:
			System.out.println("Error.Flow.Constructor().Invalid Flow Type - " + type);
			break;
		}
		/* ----------------------------------------------------------------- */
	}

	/***************
	 * Better to be implemented in Node as an entry of routing table
	 ********************/
	/* Called in Class::Event */
	/* Get current node and returns the next Node in the path */
	public Node nextNodeID(int current) {
		Node next;
		next = this.path.get(current + 1);
		return next;
	}

	/* Called in Class::Event */
	/* Gets current node and returns true if it is the flow destination */
	public boolean hasArrived(Node current_node) {
		if (current_node == this.destination) {
			System.out.println("The Packet has arrived.");
			return true;
		}
		return false;
	}

	/********************** Getters and Setters ***************************/
	public Node getSrc() {
		return source;
	}

	public Node getDst() {
		return destination;
	}

	public int getSize() {
		return size;
	}

	public double getArrivalTime() {
		return arrival_time;
	}

	public double getStartTime() {
		return start_time;
	}

	public void setStartTime(double start_time) {
		this.start_time = start_time;
	}

	public double getEndTime() {
		return end_time;
	}

	public void setEndTime(double end_time) {
		this.end_time = end_time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPacketSize() {
		return this.packet_size;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String l) {
		this.label = l;
	}
	/***********************************************************************/
}
