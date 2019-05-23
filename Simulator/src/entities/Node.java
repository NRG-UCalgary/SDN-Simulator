package entities;

import system.Network;

public abstract class Node extends Entity {

	public Node(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract Network recvSegment(Network net, Segment segment);

	public abstract Network releaseSegment(Network net, Segment segment);

}
