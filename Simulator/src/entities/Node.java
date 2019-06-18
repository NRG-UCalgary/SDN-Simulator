package entities;

import system.Network;

public abstract class Node extends Entity {

	public final String type;

	public Node(int ID, String type) {
		super(ID);
		this.type = type;
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract Network recvSegment(Network net, Segment segment);

	public abstract Network releaseSegment(Network net, Segment segment);

}
