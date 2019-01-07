package entities;

import protocols.Agent;
import system.Network;

public class TCPReceiver extends Agent {

	private final String ACKFlow;

	/** Constants **/
	final int MSS = 1000;
	final int ACKSize = 40;
	final int SYNACKSeqNum = 0;

	/* Processing times */
	final double ACKGenProcTime = 0;

	/** State Variables **/
	/* Go-Back-N variables */
	private int expSeqNum_;

	public TCPReceiver(Flow flow) {
		super(flow);
		// TODO Auto-generated constructor stub
		/* Initializations */
		ACKFlow = this.flow.getLabel() + ACKFlowExtention;
		expSeqNum_ = MSS; // TODO what is the next sequence number for bytes?

	}

	/* ======================================================================== */
	/* ========== External Methods ============================================ */
	/* ======================================================================== */
	public Network recv(Network net, Segment recvSeg) {
		switch (recvSeg.getType()) {
		case DATA:
			if (recvSeg.getSeqNum() == expSeqNum_) {
				expSeqNum_ += MSS;
			}
			net.event_List.generateEvent(net.time + ACKGenProcTime, ACK, makeSegment(ACK, expSeqNum_), flow.getSrc());
			break;
		case SYN:
			// wnd_ = cwnd_; // TODO if rwnd_ is considered, this is the place
			// net = rdtSend(net);
			net.event_List.generateEvent(net.time, SYNACK, makeSegment(SYNACK, SYNACKSeqNum), flow.getSrc());
			break;
		case FIN:
			break;
		default:
			break;
		}
		return net;

	}

	public void udtSend(int seqNum) {

	}

	/* ======================================================================== */
	/* ========== Internal Methods ============================================ */
	/* ======================================================================== */

	private Segment makeSegment(String type, int ackSeqNum) {
		Segment seg = new Segment(ACKFlow, type, ackSeqNum, ACKSize, flow.getSrc(), flow.getDst());
		return seg;
	}
}
