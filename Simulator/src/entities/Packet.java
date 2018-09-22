package entities;

import system.*;

public class Packet {
	private Logger log;

	private int packet_id;
	private String flow_label;
	private String type;
	private int size;
	private String state;
	private Node source;
	private Node destination;

	public Packet(String flow_label, int packet_id, int size, Node source, Node destination) {
		this.flow_label = flow_label;
		this.packet_id = packet_id;
		this.size = size;
		this.source = source;
		this.destination = destination;

		log = new Logger();
	}

	/* Called in Class::Event */
	/* Gets current node and returns true if it is the flow destination */
	public boolean hasArrived(Node current_node) {
		log.generalLog("Entered Packet.hasArrived().");
		if (current_node == this.destination) {
			log.networkLog("Packet num:" + packet_id + " from flow" + flow_label + " arrived to Node "
					+ destination.getLabel());
			return true;
		}
		return false;
	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/
	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public int getPacket_id() {
		return packet_id;
	}

	public void setPacket_id(int packet_id) {
		this.packet_id = packet_id;
	}

	public String getFlow_label() {
		return flow_label;
	}

	public void setFlow_label(String flow_label) {
		this.flow_label = flow_label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getDestination() {
		return destination;
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}
	/***********************************************************************/
}
