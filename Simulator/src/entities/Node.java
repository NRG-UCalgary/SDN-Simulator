package entities;

import system.*;
import java.util.HashMap;
import java.util.Map;

public class Node {
	private Logger log = new Logger();

	public Map<Node, Link> neighbors;

	private Map<Node, Link> forwarding_table;
	private String label;

	/* Constructor */
	public Node(String label) {
		this.label = label;
		neighbors = new HashMap<Node, Link>();
		// The forwarding table is a Map<Flow,Link>
		forwarding_table = new HashMap<Node, Link>();
	}

	/** Called in Class::Event.run() **/
	/* Objective::Showing the egression Link for the desired destination Node */
	public Link getEgressLink(Node dst) {
		log.generalLog("Entered Node.getEgressLink().");

		return forwarding_table.get(dst);
	}

	/*-------------------------- Getters and Setters -------------------------------------*/
	public void setTable(Map<Node, Link> table) {
		this.forwarding_table = table;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<Node, Link> getTable() {
		return this.forwarding_table;
	}

	public String getLabel() {
		return this.label;
	}
	/*------------------------------------------------------------------------------------*/
}
