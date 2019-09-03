package simulator.entities.network;

import simulator.Network;
import simulator.entities.Entity;
import simulator.entities.traffic.Packet;

public abstract class Node extends Entity {

	public Node(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract void connectToNode(int linkID, int dstNodeID, short dstNodeType);

	public abstract void recvPacket(Network net, int srcNodeID, Packet packet);

	public void updateStatisticalCounters() {

	}
}
