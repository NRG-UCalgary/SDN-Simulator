package entities;

import java.util.HashMap;
import java.util.Map;

public class Node {

	private Map<Node, Link> table;
	private Buffer buffer;
	private String label;

	public Node(String label, int buffer_size) {
		this.label = label;
		buffer = new Buffer(buffer_size, label);
		
		// The forwarding table is a Map<Flow,Link>
		table = new HashMap<Node, Link>();
	}

	/** Called in Class::Event.run() **/
	/* Objective::Showing the egression Link for the desired destination Node */
	public Link getEgressLink(Node dst) {
		return table.get(dst);
	}

	/*-------------------------- Getters and Setters -------------------------------------*/
	public void setTable(HashMap<Node, Link> table) {
		this.table = table;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
	}

	public Map<Node, Link> getTable() {
		return this.table;
	}

	public String getLabel() {
		return this.label;
	}

	public Buffer getBuffer() {
		return this.buffer;
	}

	/*------------------------------------------------------------------------------------*/
}
