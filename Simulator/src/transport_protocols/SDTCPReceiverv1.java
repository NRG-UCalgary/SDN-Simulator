package transport_protocols;

import entities.*;
import system.*;

public class SDTCPReceiverv1 extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ackNo_;

	public SDTCPReceiverv1(Flow flow) {
		super(flow);
		this.srcHostID = flow.getSrcID();
		this.dstHostID = flow.getDstID();
		ackNo_ = Keywords.SYNSeqNum;
	}

	public Network recvSegment(Network net, Segment segment) {
		/* Updating the ackNo_ state variable */
		Segment ackSegment;
		updateACKNo(segment.getSeqNum());

		switch (segment.getType()) {
		case Keywords.CTRL:
			Main.print("SDTCPRecv.recvSegment().case\"CTRL\"::We should never get here.");
			break;
		case Keywords.DATA:
			// TODO late we might want to implement NACK
			ackSegment = new Segment(flow.getID(), Keywords.ACK, ackNo_, Keywords.ACKSegSize, this.srcHostID,
					this.dstHostID);
			net = sendSegment(net, ackSegment, 0);
			break;
		case Keywords.SYN:
			// SYNACK must be generated and sent to the sender
			ackSegment = new Segment(flow.getID(), Keywords.SYNACK, Keywords.SYNACKSeqNum, Keywords.SYNSegSize,
					this.srcHostID, this.dstHostID);
			net = sendSegment(net, ackSegment, 0);
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case Keywords.FINACK:
			break;
		case Keywords.FIN:
			/** ===== Statistical Counters ===== **/
			net.hosts.get(this.dstHostID).transportAgent.flow.completionTime = net.getCurrentTime();
			/** ================================ **/
			break;
		/* ==================================== */
		default:
			Main.print("SDTCPReceiverv1.recv().case\"default\"::We should not get here.");
			break;
		}
		return net;
	}

	/** ###################################################### **/
	public Network start(Network net) {
		Main.print("SDRCPReceiver.start()::We should never get here");
		return net;
	}

	/* Local methods */
	private void updateACKNo(int recvdSeqNo) {
		if (recvdSeqNo == ackNo_) {
			ackNo_++;
		}
	}

}
