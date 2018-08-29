package routings;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.*;
import system.*;

public class Dijkstra {

	private List<Node> visited;
	private List<Node> unvisited;
	private Map<Node, Node> previous;
	private Map<Node, Double> distance;

	public Dijkstra() {
		// TODO Auto-generated constructor stub
	}

	public void run(NetList<Node> nodes, NetList<Link> links, Node src) {
		distance = new HashMap<Node, Double>();
		previous = new HashMap<Node, Node>();

		/* Initialization */
		for (Node curr_node : nodes) {
			distance.put(curr_node, Double.MAX_VALUE);		// Unknown distance from source to v
			previous.put(curr_node, null);					// Previous node in optimal path from source
			unvisited.add(curr_node);						// All nodes initially in Q (unvisited nodes)
		}
		
		distance.put(src, 0.0);								// Distance from source to source
		
		while(!unvisited.isEmpty()) {
			 Node minNode = null;
			 Double minValue = Double.MAX_VALUE;
			 for(Node node : unvisited) {
			        if(distance.get(node) < minValue) {
			            minValue = distance.get(node);
			            minNode = node;
			        }
			    }
			 
			 unvisited.remove(minNode);
			 
			 for() {
				 
			 }
			
		}
		
	}
	
	public Node getMinDistance(Map<Node, Double> distances) {
		Double min = Collections.min(distances.values());
			
		return null;
		
	}

}
/*

function Dijkstra(Graph, source):
	 2
	 3      create vertex set Q
	 4
	 5      for each vertex v in Graph:             // Initialization
	 6          dist[v] ← INFINITY                  // Unknown distance from source to v
	 7          prev[v] ← UNDEFINED                 // Previous node in optimal path from source
	 8          add v to Q                          // All nodes initially in Q (unvisited nodes)
	 9
	10      dist[source] ← 0                        // Distance from source to source
	11      
	12      while Q is not empty:
	13          u ← vertex in Q with min dist[u]    // Node with the least distance
	14                                              // will be selected first
	15          remove u from Q 
	16          
	17          for each neighbor v of u:           // where v is still in Q.
	18              alt ← dist[u] + length(u, v)
	19              if alt < dist[v]:               // A shorter path to v has been found
	20                  dist[v] ← alt 
	21                  prev[v] ← u 
	22
	23      return dist[], prev[]
	
	*/
