/** This class is the TCP GBN Sender module **/

package entities;

import protocols.Agent;
import system.Network;

public class TCPSender extends Agent {

	/** Constants **/
	final int MSS = 1000;
	final int HeaderSize = 40;
	final int LW = 0; // Loss Window: cwnd_ after loss detection (retransmission timer)
	final int IW = 0; // Initial Window: cwnd_ after 3-way handshake
	final int SstreshInit = 64 * 100000;

	final int SYNSeqNum = 0;
	/* Processing times */
	final double SegGenProcTime = 0;
	final double SegRetransProcTime = 0;

	/** State Variables **/
	private int bytesToSend_;

	/* Congestion Control variables */
	private String congestionControl_; // can be SlowStart, CongAvoidance or FastRecovery
	private int cwnd_;
	private int rwnd_;
	private int sstresh_;
	private int dupACKCount_;

	/* Go-Back-N variables */
	private int wnd_;
	private int base_;
	private int nextSeqNum_;

	public TCPSender(Flow flow) {
		super(flow);
		// TODO Auto-generated constructor stub
		/* Initializations */
		bytesToSend_ = flow.getSize();
		base_ = MSS;
		nextSeqNum_ = MSS; // TODO what is the next sequence number for bytes?

		congestionControl_ = SlowStart;
		cwnd_ = MSS;
		sstresh_ = SstreshInit;
		dupACKCount_ = 0;

	}

	/* ======================================================================== */
	/* ========== External Methods ============================================ */
	/* ======================================================================== */
	public Network recv(Network net, Segment recvSeg) {
		switch (recvSeg.getType()) {
		case ACK:

			/* New ACK arrival */
			if (recvSeg.getSeqNum() == base_ + MSS) {
				/* GBN process */
				base_ += MSS;
				stopTimer(recvSeg.getSeqNum());
				/* Congestion Control process */
				switch (congestionControl_) {
				case SlowStart:
					/* remains at slow start */
					cwnd_ = cwnd_ + MSS;
					break;
				case CongAvoidance:
					/* remains at congestion avoidance */
					cwnd_ = (int) (cwnd_ + MSS * (MSS / (double) cwnd_)); // TODO double check if ceiling or floor
					break;
				case FastRecovery:
					/* transition to congestion avoidance */
					cwnd_ = sstresh_;
					// TODO do we send something here or not?
					break;
				default:
					break;
				}
				dupACKCount_ = 0;
				wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
				rdtSend(net);
			}
			/* Duplicate ACK arrival */
			else if (recvSeg.getSeqNum() == base_) {
				switch (congestionControl_) {
				case SlowStart:
					dupACKCount_++;
					if (dupACKCount_ == 3) {
						/* transmission to fast recovery */
						congestionControl_ = FastRecovery;
						sstresh_ = cwnd_ / 2; // TODO check with TCP New Reno RFC
						cwnd_ = sstresh_ + 3 * MSS;
						wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
						// net = retransmit(net, base_);
						nextSeqNum_ = base_;
						net = rdtSend(net);
					}
					break;
				case CongAvoidance:
					dupACKCount_++;
					if (dupACKCount_ == 3) {
						/* transmission to fast recovery */
						congestionControl_ = FastRecovery;
						sstresh_ = cwnd_ / 2; // TODO check with TCP New Reno RFC
						cwnd_ = sstresh_ + 3 * MSS;
						wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
						// net = retransmit(net, base_);
						nextSeqNum_ = base_;
						net = rdtSend(net);
					}
					break;
				case FastRecovery:
					/* remains at fast recovery */
					cwnd_ = cwnd_ + MSS; // TODO double check with TCP New Reno RFC
					wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
					net = rdtSend(net);
					break;
				default:
					break;
				}
			}
			break;
		case SYNACK:
			/* Congestion Control process */
			congestionControl_ = SlowStart;
			sstresh_ = SstreshInit;
			dupACKCount_ = 0;
			wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
			net = rdtSend(net);
			break;
		case FIN:

			break;
		default:
			break;
		}
		return net;

	}

	public Network sendSYN(Network net) {
		net.event_List.generateEvent(net.time, ArrivalEvent, makeSegment(SYN, SYNSeqNum), flow.getSrc());
		return net;
	}

	/* ======================================================================== */
	/* ========== Internal Methods ============================================ */
	/* ======================================================================== */
	private Network rdtSend(Network net) {
		/* GBN Process */
		while (nextSeqNum_ < base_ + wnd_ && bytesToSend_ > 0) {
			/* Creating the arrival event of the segment to the source node */
			net.event_List.generateEvent(net.time + SegGenProcTime, ArrivalEvent, makeSegment(DATA, nextSeqNum_),
					flow.getSrc());
			if (base_ == nextSeqNum_) { // TODO should the timer start only for the base segment?
				startTimer(base_);
			}
			nextSeqNum_ += MSS;
		}
		return null;
	}

	private Network retransmit(Network net, int seqNum) { // TODO does sender retransmit the whole window? probably yes.
		Segment seg = new Segment(flow.getLabel(), DATA, seqNum, MSS, flow.getSrc(), flow.getDst());
		net.event_List.generateEvent(net.time + SegRetransProcTime, ArrivalEvent, seg, flow.getSrc());
		return net;
	}

	private Segment makeSegment(String type, int seqNum) {
		int segSize = 0;
		if (bytesToSend_ < MSS) {
			segSize = bytesToSend_;
			bytesToSend_ = 0;
		} else {
			segSize = MSS;
			bytesToSend_ -= MSS;
		}

		Segment seg = new Segment(flow.getLabel(), type, seqNum, segSize + HeaderSize, flow.getSrc(), flow.getDst());
		return seg;
	}

	private void startTimer(int seqNum) {
	}

	private void stopTimer(int seqNum) {
	}

	private void timeOut(Network net, int seqNum) {
		switch (congestionControl_) {
		case SlowStart:
			/* remains in slow start */
			sstresh_ = cwnd_ / 2; // TODO check with TCP New Reno RFC
			cwnd_ = MSS;
			dupACKCount_ = 0;
			wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
			nextSeqNum_ = base_;
			net = rdtSend(net);
			break;
		case CongAvoidance:
			/* transition to slow start */
			sstresh_ = cwnd_ / 2; // TODO check with TCP New Reno RFC
			cwnd_ = MSS;
			dupACKCount_ = 0;
			wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
			nextSeqNum_ = base_;
			net = rdtSend(net);
			break;
		case FastRecovery:
			/* transition to slow start */
			sstresh_ = cwnd_ / 2; // TODO check with TCP New Reno RFC
			cwnd_ = MSS;
			dupACKCount_ = 0;
			wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
			nextSeqNum_ = base_;
			net = rdtSend(net);
			break;
		default:
			break;
		}
	}
}
