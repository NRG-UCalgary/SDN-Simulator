package entities;

import system.*;
import java.util.HashMap;
import java.util.Map;

public class Node {
	private Logger log = new Logger();

	public Map<Node, Link> neighbors;

	private Map<String, Link> flow_table;
	private String label;

	/* Constructor */
	public Node(String label) {
		this.label = label;
		neighbors = new HashMap<Node, Link>();
		flow_table = new HashMap<String, Link>();
	}

	/** Called in Class::Event.run() **/
	/* Objective::Checking to find the flow label in the node flow table */
	public boolean hasFlowEntry(String flow_label) {
		log.generalLog("Entered Node.hasFlowEntry().");

		if (flow_table.containsKey(flow_label)) {
			return true;
		} else {
			return false;
		}
	}

	/** Called in Class::Event.run() **/
	/* Objective::Showing the egress-link for the desired destination Node */
	public Link getEgressLink(String flow_label) {
		log.generalLog("Entered Node.getEgressLink().");
		return flow_table.get(flow_label);
	}

	/** Called in Class::Controller.newFlow() **/
	/* Objective::Updating the forwarding table */
	public void updateFlowTable(String flow_label, Link egress_link) {
		log.generalLog("Entered Node.uppdateFlowTable().");
		this.flow_table.put(flow_label, egress_link);
	}

	/*-------------------------- Getters and Setters -------------------------------------*/
	public void setTable(Map<String, Link> table) {
		this.flow_table = table;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<String, Link> getTable() {
		return this.flow_table;
	}

	public String getLabel() {
		return this.label;
	}
	/*------------------------------------------------------------------------------------*/
}
