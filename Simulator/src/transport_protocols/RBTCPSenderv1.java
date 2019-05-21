package transport_protocols;

import java.util.HashMap;

import entities.*;
import system.*;

public class RBTCPSenderv1 extends Agent {

	protected int bigrtt__;

	/* Sending Window */
	private int sWnd_;

	/* rtt_ introduced by the controller using SYNACK */
	private double rtt__;

	/* Number of flows introduced by controller using SYNACK */
	private double flowCount_;

	/* Bytes remained to send */
	private int segsToSend_;

	/* Inter-segment pacing delay */
	private double pacingDelay_;

	/* Sequence number indicator */
	// indicates the sequence number for latest unACKed segment
	private int seqNo_;
	// indicates the sequence number of the latest received ACK
	private int ackNo_;
	// indicates the in-flight (unACKed) segments
	private int inFlight_;

	/*  */

	/* Timer and TimeOut mechanism */
	// TODO the timer is not needed for now since we do not have timeouts (for now)
	// private double timeOutPeriod_;
	// private HashMap<Integer, Boolean> isACKArrived_;
	// private HashMap<Integer, Boolean> hasTimedOut_;

	public RBTCPSenderv1(Flow flow) {
		super(flow);
		this.srcHostID = flow.getSrc().getID();
		this.dstHostID = flow.getDst().getID();
		segsToSend_ = flow.getSize();

		/* initializing the state variables of the sender */
		sWnd_ = 1;
		rtt__ = 0;
		flowCount_ = 1;
		pacingDelay_ = 0;
		seqNo_ = 0;
		ackNo_ = 0;
		inFlight_ = 0;
	}

	/** Overriding basic methods of Agent class for RBTCPSender **/
	@Override
	public Network recv(Network net, Segment recvdSegment) {

		switch (recvdSegment.getType()) {
		case Keywords.CTRL:
			this.sWnd_ = recvdSegment.sWnd_;
			this.bigrtt__ = recvdSegment.bigRTT_;
			this.rtt__ = recvdSegment.rtt_;
			break;
		case Keywords.ACK:
			ackNo_ = recvdSegment.getSeqNum();
			// double value?
			inFlight_ = seqNo_ - ackNo_ + 1;

			// TODO call a method named send to handle the sending window calculations and
			// intra-flow pacing
			send(net);

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

		return super.recv(net, recvdSegment);
	}

	@Override
	public Network start(Network net) {
		net.eventList.generateEvent(this.flow.getStartTime(), "ARRIVAL", genSYN(), this.srcNode);
		return super.start(net);
	}

	@Override
	public Network timeOut(Network net, Segment segment) {
		return super.timeOut(net, segment);

	}

	/** ###################################################### **/

	/* Local methods */

	private Network send(Network net) {
		// TODO create departure events based on the sWnd_ and the number of unACKed
		// segments
		double send_time = net.getCurrentTime();
		Segment send_segment;
		if (segsToSend_ > 0) {
			send_segment = genSegment();
		} else {
			send_segment = genFIN();
		}
		for (int i = 0; i < sWnd_ - inFlight_; i++) {

			net.eventList.generateEvent(send_time, this.srcHostID, send_segment);
			send_time += pacingDelay_;
		}

		return net;
	}

	private Segment genSegment() {
		Segment seg = new Segment(this.flow.getID(), Keywords.DATA, seqNo_++, DataSegSize, this.srcHostID,
				this.dstHostID);
		segsToSend_--;
		return seg;
	}

	private Segment genSYN() {
		Segment seg = new Segment(this.flow.getID(), Keywords.SYN, SYNSeqNum, SYNSegSize, this.srcHostID,
				this.dstHostID);
		seg.rtt_ = 0;
		seg.sWnd_ = 1;
		return seg;
	}

	private Segment genFIN() {
		Segment seg = new Segment(this.flow.getID(), Keywords.FIN, SYNSeqNum, FINSegSize, this.srcHostID,
				this.dstHostID);
		return seg;
	}

}
