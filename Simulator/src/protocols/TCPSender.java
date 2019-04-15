package protocols;

import java.util.HashMap;

import entities.*;
import system.*;

public class TCPSender extends Agent {

	final int MSS;
	final int LW = 0; // Loss Window: the size of cwnd_ after a TCP sender detects loss using its
						// retransmission timer
	final int IW = 0; // Initial Window: The size of the cwnd_ after three way handshake

	/* Performance Constants */

	/** State Variables **/

	/* General variables */
	private int bytes_to_send_;
	private int seq_num_;
	private int last_ACK_num_;
	private boolean SYN_sent_;
	private int cwnd_; // Limnits the amount of data the sender can send. seq_num_ <= ACKed_seq +
						// min(cwnd_, rwnd_)
	private int rwnd_; // Should it be always 1?
	private int ssthresh_;

	/* Timer and TimeOut mechanism */
	private double time_out_period_;
	private HashMap<Integer, Boolean> isACKArrived_;
	private HashMap<Integer, Boolean> hasTimedOut_;

	/* Congestion Control State */
	private String congestion_control_; // Congestion Control variable (SS, CA or FR)
	private int dup_ACK_count_;

	public TCPSender(Flow flow) {
		super(flow);

		/** Initialization **/
		/* General TCP states */
		isACKArrived_ = new HashMap<Integer, Boolean>();
		hasTimedOut_ = new HashMap<Integer, Boolean>();
		MSS = DataSegSize;
		SYN_sent_ = false;
		bytes_to_send_ = flow.getSize();

		/* GBN states */
		seq_num_ = 0;
		last_ACK_num_ = -1;
		rwnd_ = 1;

		/* Congestion control states */
		congestion_control_ = SlowStart;
		cwnd_ = MSS;
		ssthresh_ = SlowStartSSThreshFactor * MSS;
		dup_ACK_count_ = 0;

	}

	// Another interface between TCP agent and the simulator.
	// This function is only called at the start of the flow lifetime and is
	// in charge of sending the SYN segment
	public Network start(Network net) {
		return net;
	}

	// This is the interface of the TCP agents to the Network layer of the simulator
	// The Network layer calls this method to give the TCP the segment received to
	// the destination host
	public Network recv(Network net, Segment recvdACK) {

		/* Switching over received segment type */
		switch (recvdACK.getType()) {
		case ACK:
			/* Checking the timer status for the recvdSeg seq_num_ */
			if (!hasTimedOut_.get(recvdACK.getSeqNum() - 1)) {
				/* The ACK has arrived before the timer time-out */
				// TODO regular TCP behavior for ACK goes here

				/* Congestion Control */
				switch (congestion_control_) {
				case SlowStart:
					if (recvdACK.getSeqNum() == seq_num_) { // New ACK
						dup_ACK_count_ = 0;
						cwnd_ = cwnd_ + MSS;
						if (cwnd_ >= ssthresh_) {
							congestion_control_ = CongAvoidance;
							cwnd_ = (int) (cwnd_ + MSS * (MSS / (double) cwnd_));
						}
						// TODO new segment must be transmitted
					} else if (recvdACK.getSeqNum() == last_ACK_num_) { // Duplicate ACK
						dup_ACK_count_++;
						if (dup_ACK_count_ == 3) {
							ssthresh_ = (int) Math.floor(cwnd_ / (double) FastRecoveryCWNDDivindingFactore);
							cwnd_ = ssthresh_ + 3;
							// missing segment (or the whole window) should be retransmitted
							congestion_control_ = FastRecovery;
						}
					} else {
						// We should not get here practically
						Main.print("TCPSender.recv().SlowStart Whe should not get here!");
					}
					break;
				case CongAvoidance:
					if (recvdACK.getSeqNum() == seq_num_) {
						dup_ACK_count_ = 0;
						cwnd_ = (int) (cwnd_ + MSS * (MSS / (double) cwnd_));
					} else if (recvdACK.getSeqNum() == last_ACK_num_) {
						dup_ACK_count_++;
						if (dup_ACK_count_ == 3) {
							ssthresh_ = (int) Math.floor(cwnd_ / (double) FastRecoveryCWNDDivindingFactore);
							cwnd_ = ssthresh_ + 3;
							// missing segment (or the whole window) should be retransmitted
							congestion_control_ = FastRecovery;
						}
					} else {
						// We should not get here practically
						Main.print("TCPSender.recv().CongAvoidance Whe should not get here!");
					}
					break;
				case FastRecovery:
					if (recvdACK.getSeqNum() == seq_num_) {
						dup_ACK_count_ = 0;
						cwnd_ = ssthresh_;
						congestion_control_ = CongAvoidance;
					} else if (recvdACK.getSeqNum() == last_ACK_num_) {
						cwnd_ = cwnd_ + MSS;
						// TODO Transmit new segments as GBN allows
					} else {
						// We should not get here practically
						Main.print("TCPSender.recv().FastRecovery Whe should not get here!");
					}
					break;
				}

				/* Updating isACKArrived_ flag for the segment */
				isACKArrived_.put(recvdACK.getSeqNum(), true);
			} else {
				/* The Segment has timed-out before the arrival of the ACK */

				/*
				 * What should TCP do in such case? does it consider this ACK as a duplicate
				 * ACK?
				 */
			}
			// The isACKArrived Flag must be set to TRUE
			isACKArrived_.put(recvdACK.getSeqNum(), true);
			// TODO the received segment is an Acknowledgement and the proper decision must
			// be made for next sending data
			// TODO TCP states must be updated
			// TODO Next segment must be prepared and sent by calling sendSeg()
			break;
		case SYNACK:
			// TODO can SYNACK be treated as ACK?
			break;
		case FIN:
			// TODO Do we need FIN realizer? - I don't think so
			break;
		default:
			break;
		}
		return net;

	}

	// This function is called in recv() method to send the next data segment. Note
	// that this method is only in charge of sending a single segment
	// By sending we mean adding the needed events to the event-list of the Network
	// entity
	// after sending the segment, setTimer method should be called
	private Network sendSeg(Network net) {
		return net;
	}

	// This method is in charge creating a new segment and update the Agent states
	// accordingly.
	// This method gets the segment type as an input parameter and returns the
	// segment entity
	private Segment createDataSeg() {
		Segment segment = new Segment(flow.getLabel(), DATA, seq_num_, DataSegSize, srcNode, dstNode);
		return segment;
	}

	private Segment createSYNSeg() {
		Segment segment = new Segment(flow.getLabel(), SYN, seq_num_, SYNSegSize, srcNode, dstNode);
		return segment;
	}

	// This method is in charge of setting a timer after sending a segment to the
	// network.
	// The timer is actually a timeOut event in the event-list that should
	// eventually be deleted if the ACK is received before the timer is ran out
	// Hence there should be some flag mechanism that will be checked to see which
	// happened first; The ACK reception or the TimeOut
	private Network setTimer(Network net, Segment sentSegment) {

		// TODO a timeOut event must be created here and added to the event-list
		net.event_List.generateEvent(net.time + time_out_period_, TCPTimeOutEvent, sentSegment, srcNode);
		// TODO The corresponding flags must be set
		isACKArrived_.put(sentSegment.getSeqNum(), false);
		return net;
	}

	// This method is in charge of updating Agent's state for a timeOut happening
	public Network timeOut(Network net, Segment sentSegment) {

		/* Checking if the ACK has not already arrived */
		if (!isACKArrived_.get(sentSegment.getSeqNum())) {
			/* Setting the value of isTimeOuted_ */
			hasTimedOut_.put(sentSegment.getSeqNum(), true);

			/* Congestion Control */
			congestion_control_ = SlowStart;
			ssthresh_ = (int) Math.floor(cwnd_ / (double) TimeOutSlowStartCWNDDivindingFactore);
			cwnd_ = MSS;
			dup_ACK_count_ = 0;

			// TODO retransmit by GBN (whole sending window)

		} else {
			/* Practically nothing should happen here */
			/* Logging to make sure everything works fine */
			// TODO log the event of timeOut after the ACK has already arrived
		}

		/** Implementation of timeOut based on GBN (according to Carey and book?) **/

		return net;
	}
}
