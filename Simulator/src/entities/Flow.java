package entities;

import switches.*;

import java.util.ArrayList;


public class Flow extends Entity {

	private String label;
	private String type;
	private Host source;
	private Host destination;
	private int size;
	private int packet_size;
	private double arrivalTime;
	private double completionTime;

	/** Is it needed? **/
	public ArrayList<Segment> packets = new ArrayList<Segment>();

	/* Stores the Node IDs of the path */
	/* Initialized at the creation of the flow */
	public ArrayList<SDNSwitchv1> path = new ArrayList<SDNSwitchv1>();

	public Flow(int ID, String type, Host src, Host dst, int size, Double arrv_time) {
		super(ID);
		this.type = type;
		this.source = src;
		this.destination = dst;
		this.size = size;
		this.arrivalTime = arrv_time;

	}

	/**********************************************************************/
	/********************** Getters and Setters ***************************/
	/**********************************************************************/
	public Host getSrc() {
		return source;
	}

	public Host getDst() {
		return destination;
	}

	public int getSize() {
		return size;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public double getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(double end_time) {
		this.completionTime = end_time;
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

	public int getFlowID() {
		return this.getFlowID();
	}
	/***********************************************************************/
}
