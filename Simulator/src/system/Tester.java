/** This class is for unit testing all other classes and their methods **/

package system;

import java.util.Map;

import entities.*;
import protocols.*;
import routings.*;

public class Tester {
	/* Package entities */
	private static Buffer buffer;
	private static Controller controller;
	private static Flow flow;
	private static Link link;
	private static Node node;
	private static Packet packet;

	/* Package protocols */
	private static Agent agent;
	private static RBTCP rbtcp;

	/* Package routings */
	private static Dijkstra dijk;
	private static Routing routing;

	/* Package system */
	private static Debugger debugger;
	private static Event event;
	private static EventList eventlist;
	private static Logger logger;
	private static Network network;
	private static Simulator simulator;

	// Number of nodes in the test topology
	private static int NODE_COUNT = 5;

	public static void main(String[] args) {
		NetList<Node> nodes = new NetList<Node>();
		for (int i = 1; i <= NODE_COUNT; i++) {
			Node n = new Node("n" + String.valueOf(i), 10);
			nodes.addElement(n.getLabel(), n);
		}

		NetList<Link> links = new NetList<Link>();

		Link l1 = new Link("l1", nodes.getByLabel("n1"), nodes.getByLabel("n2"), 1.0, 1.0);
		links.addElement(l1.getLabel(), l1);
		Link l2 = new Link("l2", nodes.getByLabel("n1"), nodes.getByLabel("n3"), 3.0, 1.0);
		links.addElement(l2.getLabel(), l2);
		Link l3 = new Link("l3", nodes.getByLabel("n2"), nodes.getByLabel("n3"), 1.0, 1.0);
		links.addElement(l3.getLabel(), l3);
		Link l4 = new Link("l4", nodes.getByLabel("n2"), nodes.getByLabel("n4"), 5.0, 1.0);
		links.addElement(l4.getLabel(), l4);
		Link l5 = new Link("l5", nodes.getByLabel("n3"), nodes.getByLabel("n5"), 4.0, 1.0);
		links.addElement(l5.getLabel(), l5);
		Link l6 = new Link("l6", nodes.getByLabel("n3"), nodes.getByLabel("n4"), 2.0, 1.0);
		links.addElement(l6.getLabel(), l6);

		/* Testing Dijkstra Algorithm */
		dijk = new Dijkstra(nodes, links);

		Map<Node, Double> dist = dijk.run(nodes.getByLabel("n1"));
		for (Map.Entry<Node, Double> entry : dist.entrySet()) {
			print(entry.getKey().getLabel() + " --- " + entry.getValue());
		}

	}

	public boolean test(Object o) {

		return true;
	}

	public static void print(Object o) {
		System.out.println(o);
	}

}
