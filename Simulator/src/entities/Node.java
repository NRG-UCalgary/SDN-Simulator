package entities;

import java.util.HashMap;
import java.util.Map;

public class Node {

	public Map<Node, Link> neighbors;

	private Map<Node, Link> forwarding_table;
	private Buffer buffer;
	private String label;

	public Node(String label, int buffer_size) {
		this.label = label;
		neighbors = new HashMap<Node, Link>();
		// The forwarding table is a Map<Flow,Link>
		forwarding_table = new HashMap<Node, Link>();
	}

	/** Called in Class::Event.run() **/
	/* Objective::Showing the egression Link for the desired destination Node */
	public Link getEgressLink(Node dst) {
		return forwarding_table.get(dst);
	}

	/*-------------------------- Getters and Setters -------------------------------------*/
	public void setTable(Map<Node, Link> table) {
		this.forwarding_table = table;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
	}

	public Map<Node, Link> getTable() {
		return this.forwarding_table;
	}

	public String getLabel() {
		return this.label;
	}

	public Buffer getBuffer() {
		return this.buffer;
	}

	/*------------------------------------------------------------------------------------*/
}
