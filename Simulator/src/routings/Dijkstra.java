/** Implements Dijkstra algorithm **/
/** Verification Test -- Done **/

package routings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entities.*;

public class Dijkstra extends Routing {

	private Map<String, SDNSwitch> nodes;
	private List<SDNSwitch> unvisited;
	private Map<SDNSwitch, SDNSwitch> previous;
	private Map<SDNSwitch, Double> distance;
	private Map<SDNSwitch, Link> result;

	/* Constructor */
	public Dijkstra(Map<String, SDNSwitch> nodes) {
		this.nodes = nodes;
		distance = new HashMap<SDNSwitch, Double>();
		previous = new HashMap<SDNSwitch, SDNSwitch>();
		unvisited = new ArrayList<SDNSwitch>();
		result = new HashMap<SDNSwitch, Link>();
	}

	/** Called in Class::Controller.generatePaths() **/

	/* Objective::Finding the optimal paths for each Node */
	public Map<SDNSwitch, Link> run(SDNSwitch src, SDNSwitch target) {
		log.entranceToMethod("Dijkstra", "run");

		distance = new HashMap<SDNSwitch, Double>();
		previous = new HashMap<SDNSwitch, SDNSwitch>();
		unvisited = new ArrayList<SDNSwitch>();

		/* Initialization */
		for (SDNSwitch curr_node : nodes.values()) {
			distance.put(curr_node, Double.MAX_VALUE); // Unknown distance from source to v
			previous.put(curr_node, src); // Previous node in optimal path from source
			unvisited.add(curr_node); // All nodes initially in Q (unvisited nodes)
		}

		distance.put(src, 0.0); // Distance from source to source

		while (!unvisited.isEmpty()) {
			SDNSwitch minNode = null;
			Double minValue = Double.MAX_VALUE;
			for (SDNSwitch node : unvisited) { // Node with the least distance
				if (distance.get(node) < minValue) { // will be selected first
					minValue = distance.get(node);
					minNode = node;
				}
			}
			unvisited.remove(minNode);

			for (SDNSwitch n : minNode.neighbors.keySet()) { // For each neighbor n of minNode
				Double alt = distance.get(minNode) + minNode.neighbors.get(n).getPropagationDelay();
				if (alt < distance.get(n)) {
					distance.put(n, alt);
					previous.put(n, minNode);

				}
			}
		}

		this.result = new HashMap<SDNSwitch, Link>();
		generateResult(src, target);
		return this.result;
	}

	/** Called in Class::Dijkstra.run() **/
	/* Objective::Returning the routing table result<Node,Link> for Node src */
	private SDNSwitch generateResult(SDNSwitch src, SDNSwitch dst) {
		log.entranceToMethod("Dijkstra", "generateResult");

		SDNSwitch neighbor = previous.get(dst);
		this.result.put(neighbor, neighbor.neighbors.get(dst));
		// Check if the neighbor is connected to the src
		if (previous.get(neighbor).equals(src)) {
			this.result.put(src, src.neighbors.get(neighbor));
			return null;
		} else {
			return generateResult(src, neighbor);
		}
	}
}
