package transport_protocols;

import entities.*;
import system.*;

public class SDTCPReceiverv1 extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ackNo_;

	private double rtt_;
	private int sWnd_;

	public SDTCPReceiverv1(Flow flow) {
		super(flow);
		this.srcHostID = flow.getDst().getID();
		this.dstHostID = flow.getSrc().getID();
		ackNo_ = 0;
		rtt_ = 0;
		sWnd_ = 0;
	}

	public Network recvSegment(Network net, Segment segment) {
		/* Updating the ackNo_ state variable */
		ackNo_ = segment.getSeqNum() + 1;

		switch (segment.getType()) {
		case Keywords.DATA:
			break;
		case Keywords.SYN:
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case Keywords.FINACK:
			break;
		case Keywords.FIN:
			break;
		/* ==================================== */
		default:
			Main.print("RBTCPReceiverv1.recv()::We should not get here.");
			break;
		}
		return net;
	}

	/** ###################################################### **/
	public Network start(Network net) {
		Main.print("Do we get hereR?!");
		return net;
	}
	/* Local methods */

}
