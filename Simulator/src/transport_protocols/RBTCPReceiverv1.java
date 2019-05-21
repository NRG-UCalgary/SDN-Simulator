package transport_protocols;

import entities.*;
import system.*;

public class RBTCPReceiverv1 extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ackNo_;

	private double rtt_;
	private int sWnd_;

	public RBTCPReceiverv1(Flow flow) {
		super(flow);
		this.srcHostID = flow.getDst().getID();
		this.dstHostID = flow.getSrc().getID();
		ackNo_ = 0;
		rtt_ = 0;
		sWnd_ = 0;
	}

	/** Overriding basic methods of Agent class for RBTCPSender **/
	@Override
	public Network recv(Network net, Segment recvdSegment) {
		/* Updating the ackNo_ state variable */
		ackNo_ = recvdSegment.getSeqNum() + 1;

		switch (recvdSegment.getType()) {
		case Keywords.DATA:
			net = send(net, ACK);
			break;
		case Keywords.SYN:
			net = send(net, SYNACK);
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case Keywords.FINACK:
			break;
		case Keywords.FIN:
			net = send(net, FINACK);
			break;
		/* ==================================== */
		default:
			Main.print("RBTCPReceiverv1.recv()::We should not get here.");
			break;
		}
		return super.recv(net, recvdSegment);
	}

	/** ###################################################### **/

	/* Local methods */

	private Network send(Network net, String segmentType) {
		Main.print("RBTCPReceiverv1.send()::This is the value for this.srcHostID():: " + this.srcHostID);
		net.eventList.generateEvent(net.getCurrentTime(), "ARRIVAL", genSegment(segmentType), this.srcHostID);
		return net;
	}

	private Segment genSegment(int segmentType) {
		// TODO update the flow label based on the implementation of the ACK stream in
		// the network layer simulator
		Segment seg = new Segment(this.flow.getID() + ACKFlowExtention, segmentType, ackNo_, ACKSegSize, this.srcHostID,
				this.dstHostID);
		return seg;
	}

}
