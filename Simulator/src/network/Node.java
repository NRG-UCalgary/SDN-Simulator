package network;

import java.util.ArrayList;

public class Node {

	ArrayList<Link> table = new ArrayList<Link>();
	public Buffer buffer;
	String label;

	public Node(String label, int buffer_size) {
		this.label = label;
		buffer = new Buffer(buffer_size, label);
	}

	public Link getNextLink(Node dst) {
		Link link;
		link = this.table.get(0);

		return link;
	}

}
