package entities;

import system.Network;

public abstract class Node extends Entity {

	public Node(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	/** Implemented in Class::SDNSwitch, Host **/

	/**
	 * @param net
	 * @param segment
	 * @return
	 */
	public abstract Network recvSegment(Network net, Segment segment);

	/** Implemented in Class::Switch, Host **/
	/**
	 * @param nodeID
	 * @param segmentSize
	 * @return
	 */
	public abstract double getAccessLinkDelay(int nodeID, int segmentSize);
}
