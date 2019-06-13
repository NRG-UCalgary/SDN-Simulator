package transport_protocols;

import java.util.ArrayList;

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
	private int ackSeqNo_;
	// indicates the in-flight (unACKed) segments
	private int inFlight_;

	public SDTCPSenderv1(Flow flow) {
		super(flow);
		this.srcHostID = flow.getSrcID();
		this.dstHostID = flow.getDstID();
		segsToSend_ = flow.getSize();

		/* initializing the state variables of the sender */
		sWnd_ = 1;
		rtt_ = 0;
		seqNo_ = Keywords.SYNSeqNum;
		ackSeqNo_ = -2;
		inFlight_ = 0;
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Agent) --------- */
	/* --------------------------------------------------- */

	/* ########## Public ################################# */
	public Network start(Network net) {
		Segment synSegment = genSYN();
		double nextTime = flow.arrivalTime + net.hosts.get(srcHostID).accessLink.getTotalDelay(synSegment.getSize());
		net.eventList
				.addEvent(new ArrivalToSwitch(nextTime, net.hosts.get(srcHostID).accessSwitchID, synSegment, null));
		return net;
	}

	public Network recvSegment(Network net, Segment recvdSegment) {
		ArrayList<Segment> segments;
		switch (recvdSegment.getType()) {
		case Keywords.CTRL:
			/* Update the congestion control variables */
			// TODO what is the difference between this.flow.ID()
			this.sWnd_ = recvdSegment.sWnd_;
			this.bigrtt_ = recvdSegment.bigRTT_;
			this.rtt_ = recvdSegment.rtt_;
			break;
		case Keywords.ACK:
			/** ===== Statistical Counters ===== **/
			this.flow.ackSeqNumArrivalTimes.put(recvdSegment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/

			if (recvdSegment.getSeqNum() == ackSeqNo_ + 1) {
				ackSeqNo_ = recvdSegment.getSeqNum();
				inFlight_--;
			}
			int toSend = sWnd_ - inFlight_;
			segments = new ArrayList<Segment>();
			for (int i = 0; i < toSend; i++) {
				segments.add(genDATASegment());
			}
			net = sendMultipleSegments(net, segments);

			ackSeqNo_ = recvdSegment.getSeqNum();
			inFlight_ = seqNo_ - ackSeqNo_ + 1;

			break;
		case Keywords.SYNACK:
			/** ===== Statistical Counters ===== **/
			this.flow.dataSendingStartTime = net.getCurrentTime();
			/** ================================ **/

			segments = new ArrayList<Segment>();
			for (int i = 0; i < sWnd_; i++) {
				seqNo_ += i;
				segments.add(genDATASegment());
				inFlight_ = i + 1;
			}
			net = sendMultipleSegments(net, segments);
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
		if (segsToSend_ > 0) {
			Segment seg = new Segment(this.flow.getID(), Keywords.DATA, seqNo_, Keywords.DataSegSize, this.srcHostID,
					this.dstHostID);
			segsToSend_--;

			return seg;
		} else {
			Main.print("The segsToSend is 0.");
			return genFIN();
		}
	}

	private Segment genFIN() {
		Segment seg = new Segment(this.flow.getID(), Keywords.FIN, Keywords.SYNSeqNum, Keywords.FINSegSize,
				this.srcHostID, this.dstHostID);
		return seg;
	}

}
