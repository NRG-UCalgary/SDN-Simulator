package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.Entity;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Packet;

public abstract class Node extends Entity {

	public Node(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract void connectToNode(int linkID, int dstNodeID, short dstNodeType);

	public abstract void recvPacket(Network net, int srcNodeID, Packet packet);

	public abstract void executeTimeOut(Network net, int timerID);

	public void updateStatisticalCounters() {

	}
}
