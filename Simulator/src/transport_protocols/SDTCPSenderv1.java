package transport_protocols;

import entities.*;
import events.ArrivalToSwitch;
import system.*;

public class SDTCPSenderv1 extends Agent {

	protected int bigrtt_;

	/* Sending Window */
	private int sWnd_;

	/* rtt_ introduced by the controller using SYNACK */
	private double rtt_;

	/* Bytes remained to send */
	private int segsToSend_;

	/* Sequence number indicator */
	// indicates the sequence number for latest unACKed segment
	private int seqNo_;
	// indicates the sequence number of the latest received ACK
	private int ackNo_;
	// indicates the in-flight (unACKed) segments
	private int inFlight_;

	public SDTCPSenderv1(Flow flow) {
		super(flow);
		this.srcHostID = flow.getSrc().getID();
		this.dstHostID = flow.getDst().getID();
		segsToSend_ = flow.getSize();

		/* initializing the state variables of the sender */
		sWnd_ = 1;
		rtt_ = 0;
		seqNo_ = 0;
		ackNo_ = 0;
		inFlight_ = 0;
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Agent) --------- */
	/* --------------------------------------------------- */

	/* ########## Public ################################# */
	public Network start(Network net) {
		Segment synSegment = genSYN();
		double nextTime = flow.getArrivalTime()
				+ net.hosts.get(srcHostID).accessLink.getTotalDelay(synSegment.getSize());
		net.eventList
				.addEvent(new ArrivalToSwitch(nextTime, net.hosts.get(srcHostID).accessSwitchID, synSegment, null));
		return sendSegment(net, genSYN());
	}

	public Network recvSegment(Network net, Segment recvdSegment) {

		switch (recvdSegment.getType()) {
		case Keywords.CTRL:
			/* Update the congestion control variables */
			// TODO what is the difference between this.flow.ID()
			this.sWnd_ = recvdSegment.sWnd_;
			this.bigrtt_ = recvdSegment.bigRTT_;
			this.rtt_ = recvdSegment.rtt_;
			break;
		case Keywords.ACK:
			ackNo_ = recvdSegment.getSeqNum();
			inFlight_ = seqNo_ - ackNo_ + 1;
			break;
		case Keywords.SYNACK:
			// For now with do not separate ACK from SYNACK
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
			break;
		}
		return net;
	}

	/* ########## Private ################################ */

	/* =========== Segment creation methods=============== */
	private Segment genSYN() {
		Segment seg = new Segment(this.flow.getID(), Keywords.SYN, Keywords.SYNSeqNum, Keywords.SYNSegSize,
				this.srcHostID, this.dstHostID);
		return seg;
	}

	private Segment genDATASegment() {
		Segment seg = new Segment(this.flow.getID(), Keywords.DATA, seqNo_++, Keywords.DataSegSize, this.srcHostID,
				this.dstHostID);
		segsToSend_--;
		return seg;
	}

	private Segment genFIN() {
		Segment seg = new Segment(this.flow.getID(), Keywords.FIN, Keywords.SYNSeqNum, Keywords.FINSegSize,
				this.srcHostID, this.dstHostID);
		return seg;
	}

}
