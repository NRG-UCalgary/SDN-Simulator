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

	public abstract void recvPacket(Network net, Packet packet);

	public abstract void releasePacket(Network net, int dstNodeID, Packet packet);

}
