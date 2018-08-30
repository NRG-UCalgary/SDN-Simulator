/** Implements Dijkstra algorithm **/
/** Verification Test -- Done **/

package routings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entities.*;
import system.*;

public class Dijkstra extends Routing {

	private NetList<Node> nodes;
	private NetList<Link> links;
	private List<Node> unvisited;
	private Map<Node, Node> previous;
	private Map<Node, Double> distance;

	/* Constructor */
	public Dijkstra(NetList<Node> nodes, NetList<Link> links) {
		this.nodes = nodes;
		this.links = links;
		distance = new HashMap<Node, Double>();
		previous = new HashMap<Node, Node>();
		unvisited = new ArrayList<Node>();

	}

	/** Called in Class::Controller.generatePaths() **/
	/* Objective::Finding the optimal paths for each Node */
	public Map<Node, Double> run(Node src) {

		/* Initialization */
		for (Node curr_node : nodes) {
			distance.put(curr_node, Double.MAX_VALUE); // Unknown distance from source to v
			previous.put(curr_node, src); // Previous node in optimal path from source
			unvisited.add(curr_node); // All nodes initially in Q (unvisited nodes)
		}

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

			for (Node n : getNeighbors(minNode)) { // For each neighbor n of minNode
				Double alt = distance.get(minNode) + getLength(minNode, n);
				if (alt < distance.get(n)) {
					distance.put(n, alt);
					previous.put(n, minNode);
				}
			}
		}

		/*
		 * distance and previous have the answers for all routing with the source of src
		 */

		return distance;
	}

	/** Called in Class::Dijkstra.run() **/
	/* Objective::Getting a Node and returns a list of its neighbors */
	private List<Node> getNeighbors(Node n) {
		List<Node> neighbors = new ArrayList<Node>();
		for (Link l : links) {
			if (l.getSrc().equals(n)) {
				neighbors.add(l.getDst());
			} else if (l.getDst().equals(n)) {
				neighbors.add(l.getSrc());
			}
		}
		return neighbors;
	}

	/** Called in Class::Dijkstra.run() **/
	/* Objective::Returning the Propagation Delay of the Link between two Nodes */
	private Double getLength(Node src, Node dst) {
		Double length = 0.0;
		for (Link l : this.links) {
			if (l.getSrc().equals(src) && l.getDst().equals(dst)) {
				length = l.getPropagationDelay();
				break;
			} else if (l.getDst().equals(src) && l.getSrc().equals(dst)) {
				length = l.getPropagationDelay();
				break;
			}
		}
		return length;
	}

}
