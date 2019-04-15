package protocols;

import java.util.HashMap;

import entities.*;
import system.*;

public class RBTCPReceiverv1 extends Agent {

	public RBTCPReceiverv1(Flow flow) {
		super(flow);
		// TODO Auto-generated constructor stub
	}

	/* Sending Window */
	private int sWnd_;

	/* RTT introduced by the controller using SYNACK */
	private double rtt_;

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
	private double timeOutPeriod_;
	private HashMap<Integer, Boolean> isACKArrived_;
	private HashMap<Integer, Boolean> hasTimedOut_;

	public RBTCPSenderv1(Flow flow) {
		super(flow);
	}

	/** Overriding basic methods of Agent class for RBTCPSender **/
	@Override
	public Network recv(Network net, Segment recvdSegment) {

		switch (recvdSegment.getType()) {
		case ACK:
			// TODO check timer status
			/* sWnd_ is updated to the value that controller has announced */
			sWnd_ = recvdSegment.sWndSize;
			ackNo_ = recvdSegment.getSeqNum();
			rtt_ = recvdSegment.rtt;
			flowCount_ = recvdSegment.flowCount;
			pacingDelay_ = Math.floor(rtt_ / (double) flowCount_ * sWnd_); // TODO should it be floor or the actual
																			// double value?
			inFlight_ = seqNo_ - ackNo_ + 1;

			// TODO call a method named send to handle the sending window calculations and
			// intra-flow pacing
			send(net);

			break;
		case SYNACK:
			// For now with do not separate ACK from SYNACK
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case FINACK:
			break;
		case FIN:
			break;
		/* ==================================== */
		default:
			break;
		}

		return super.recv(net, recvdSegment);
	}

	@Override
	public Network start(Network net) {

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
		double send_time = net.time;
		for (int i = 0; i < sWnd_ - inFlight_; i++) {

			net.event_List.generateEvent(send_time, "ARRIVAL", genSegment(), this.srcNode);
			send_time += pacingDelay_;
		}

		return net;
	}

	private Segment genSegment() {
		Segment seg = new Segment(this.flow.getLabel(), DATA, seqNo_++, DataSegSize, this.srcNode, this.dstNode);
		return seg;
	}

}
