package protocols;

import entities.*;
import system.Network;

public class TCP1 extends Agent {

	/* Protocol settings */
	// TODO all these variables must be set by user or default
	final int MSS;
	final int ACK_SIZE = 40;
	final int LW = 0; // Loss Window: the size of cwnd_ after a TCP sender detects loss using its
						// retransmission timer
	final int IW = 0; // Initial Window: The size of the cwnd_ after three way handshake

	/* Performance Constants */
	private double ACK_GENERATION_TIME; // The processing time for generating an ACK (<500ms)

	/** State Variables **/

	/* Sender variables */
	private int bytes_to_send_;
	private int seq_num_;
	private boolean SYN_sent_;
	private int cwnd_; // Limnits the amount of data the sender can send. seq_num_ <= ACKed_seq +
						// min(cwnd_, rwnd_)

	private int greatest_recv_ACK_;

	private String congestion_control_; // Congestion Control variable (SS, CA or FR)

	/* Receiver variables */
	private boolean SYNACK_sent_;
	private int rwnd_; // The most recently advertised receiver wiwndow

	private int exp_seq_num_;

	public TCP1(Flow flow, int seg_size) {
		super(flow);

		// Protocol settings
		MSS = seg_size;

		// Sender states
		seq_num_ = 0;
		bytes_to_send_ = flow.getSize();

	}

	/* ======================================================================== */
	/* ========== General Methods ============================================= */
	/* ======================================================================== */
	/** Called in Class::Network.execute() **/
	/** Objective::Receives the segment from the event execution **/
	public Network recv(Network net, Segment recv_seg) {
		/* Checking the type of the received segment */
		switch (recv_seg.getType()) {
		case "DATA":
			/* Receiver checks the received seq_num with the expected seq_num */
			if (recv_seg.getSeqNum() == exp_seq_num_) {
				/* Received segment is correct */
				exp_seq_num_ = recv_seg.getSeqNum() + 1;
			} else if (recv_seg.getSeqNum() > exp_seq_num_) {
				// TODO TCP should send a duplicate ACK. It means the exp_seq_num_ should not be
				// changed
				// TODO possibly update the statistical variables for duplicate ACK
			}
			/* Receiver sends and ACK to the sender */
			net.event_List.generateEvent(net.time, "Arrival", generateACK(), flow.getDst());
			break;
		case "ACK":
			/* Sender sends the next DATA segments to the receiver */
			break;
		case "FIN":
			/* The connection is terminated */
			break;
		case "SYN":
			/* Receiver sends a SYNACK to the sender */
			break;
		case "SYNACK":
			/* Sender sends the first DATA segments */
			break;
		default:
			log.generalLog("Invalid segment type: " + recv_seg.getType());
			break;
		}
		return net;
	}

	public Network send(Network net) {
		return net;
	}

	/* ======================================================================== */
	/* ========== Sender methods ============================================== */
	/* ======================================================================== */

	/* ======================================================================== */
	/* ========== Receiver methods ============================================ */
	/* ======================================================================== */
	public Segment generateACK() {
		return null;
	}

	public Segment generateSYNACK() {

		return null;
	}
}
