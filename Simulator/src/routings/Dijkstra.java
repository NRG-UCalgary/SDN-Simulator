/** Implements Dijkstra algorithm **/
/** Verification Test -- Done **/

package routings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entities.*;

public class Dijkstra extends Routing {

	private Map<String, Node> nodes;
	private List<Node> unvisited;
	private Map<Node, Node> previous;
	private Map<Node, Double> distance;

	/* Constructor */
	public Dijkstra(Map<String, Node> nodes) {
		this.nodes = nodes;
		distance = new HashMap<Node, Double>();
		previous = new HashMap<Node, Node>();
		unvisited = new ArrayList<Node>();
	}

	/** Called in Class::Controller.generatePaths() **/
	/* Objective::Finding the optimal paths for each Node */
	public Map<Node, Link> run(Node src) {
		distance = new HashMap<Node, Double>();
		previous = new HashMap<Node, Node>();
		unvisited = new ArrayList<Node>();

		log.generalLog("Entered Dijkstra.run().");
		/* Initialization */
		for (Node curr_node : nodes.values()) {
			distance.put(curr_node, Double.MAX_VALUE); // Unknown distance from source to v
			previous.put(curr_node, src); // Previous node in optimal path from source
			unvisited.add(curr_node); // All nodes initially in Q (unvisited nodes)
		}

		for (Node n : previous.keySet()) {
			log.generalLog(n.getLabel() + ", " + previous.get(n).getLabel());
		}
		log.generalLog("------------------------------------------------");

		distance.put(src, 0.0); // Distance from source to source

		while (!unvisited.isEmpty()) {
			Node minNode = null;
			Double minValue = Double.MAX_VALUE;
			for (Node node : unvisited) { // Node with the least distance
				if (distance.get(node) < minValue) { // will be selected first
					minValue = distance.get(node);
					minNode = node;
				}
			}
			unvisited.remove(minNode);

			for (Node n : minNode.neighbors.keySet()) { // For each neighbor n of minNode
				Double alt = distance.get(minNode) + minNode.neighbors.get(n).getPropagationDelay();
				if (alt < distance.get(n)) {
					distance.put(n, alt);
					previous.put(n, minNode);

					for (Node n1 : previous.keySet()) {
						log.generalLog(n1.getLabel() + ", " + previous.get(n1).getLabel());
					}
					log.generalLog("------------------------------------------------");

				}
			}
		}

		return getResult(src);
	}

	/** Called in Class::Dijkstra.run() **/
	/* Objective::Returning the routing table result<Node,Link> for Node src */
	private Map<Node, Link> getResult(Node src_node) {
		log.generalLog("Entered Dijkstra.getResult().");

		for (Node n : previous.keySet()) {
			log.generalLog(n.getLabel() + ", " + previous.get(n).getLabel());
		}

		Map<Node, Link> result = new HashMap<Node, Link>();
		Node temp = null;
		for (Node dst_node : this.nodes.values()) {
			if (dst_node.equals(src_node)) {
				result.put(dst_node, null);
				break;
			} else {
				while (!(previous.get(dst_node).equals(src_node))) {
					temp = previous.get(dst_node);
				}
				result.put(dst_node, src_node.neighbors.get(temp));
			}
		}
		return result;
	}

}
