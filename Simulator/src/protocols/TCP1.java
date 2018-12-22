package protocols;

import entities.*;

public class TCP1 extends Agent {

	// Protocol settings (set by user)
	final int MSS;

	// State variables
	
	//Sender
	private int bytes_to_send_;
	private int seq_num_;
	private boolean SYN_sent_;
	
	//Receiver
	private boolean SYNACK_sent_;

	public TCP1(Flow flow, int seg_size) {
		super(flow);
		// TODO Auto-generated constructor stub
		// Protocol settings
		MSS = seg_size;

		// Sender states
		seq_num_ = 0;
		bytes_to_send_ = flow.getSize();

	}

	/* ======================================================================== */
	/* ========== General Methods ============================================= */
	/* ======================================================================== */
	public void receiveSegment(Segment recv_packet) {

	}

	/* ======================================================================== */
	/* ========== Sender methods ============================================== */
	/* ======================================================================== */
	public void sendSegment() {

	}

	public Segment generateSYNSeg() {
		Segment segment = new Segment();
		
		segment.SYN = true;
		
		// State variable - TCP is waiting for SYN-Back
		SYN_sent_ = true;

		return segment;
	}

	public Segment generateDataSeg() {
		Segment segment = new Segment();

		if (bytes_to_send_ >= MSS) {

			bytes_to_send_ -= MSS;
			seq_num_ += MSS;

			segment.size = MSS;
			segment.seq_number = seq_num_;

		} else { // This will be the last segment
			seq_num_ += bytes_to_send_;

			segment.size = bytes_to_send_;
			segment.seq_number = seq_num_;

			bytes_to_send_ -= bytes_to_send_;
			if (bytes_to_send_ != 0) {
				System.out.println("Invalid value for bytes_to_send_ = " + bytes_to_send_);
			}
		}
		return segment;
	}

	/* ======================================================================== */
	/* ========== Receiver methods ============================================ */
	/* ======================================================================== */
	public Segment generateSYNACK() {
		Segment segment = new Segment();
		
		segment.SYN_ACK = true;
		
		return segment;
	}
}
