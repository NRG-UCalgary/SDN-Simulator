package entities;

import system.*;
import utilities.Logger;

public class Segment {
	private Logger log;

	/** my protocol variables **/
	public int sWndSize;
	public int flowID;
	public double rtt;
	/**************************/

	private int seq_num;
	private String flow_label;
	private String type;
	private int size;
	private String state;
	private Host source;
	private Host destination;

	public Segment(String flow_label, String type, int seq_num, int size, Host source, Host destination) {
		this.flow_label = flow_label;
		this.type = type;
		this.seq_num = seq_num;
		this.size = size;
		this.source = source;
		this.destination = destination;

		log = new Logger();
	}

	/* Called in Class::Event */
	/* Gets current node and returns true if it is the flow destination */
	public boolean hasArrived(Host current_node) {
		log.entranceToMethod("Packet", "hasArrived");
		if (current_node == this.destination) {
		//	log.networkLog("Packet num:" + seq_num + " from flow " + flow_label + " arrived to Node "
		//			+ destination.getLabel());
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

	public int getSeqNum() {
		return seq_num;
	}

	public void setSeqNum(int seq_num) {
		this.seq_num = seq_num;
	}

	public String getFlowLabel() {
		return flow_label;
	}

	public void setFlow_label(String flow_label) {
		this.flow_label = flow_label;
	}
	
	public int getFlowID() {
		return flowID;
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

	public Host getSource() {
		return source;
	}

	public void setSource(Host source) {
		this.source = source;
	}

	public Host getDestination() {
		return destination;
	}

	public void setDestination(Host destination) {
		this.destination = destination;
	}
	/***********************************************************************/
}
