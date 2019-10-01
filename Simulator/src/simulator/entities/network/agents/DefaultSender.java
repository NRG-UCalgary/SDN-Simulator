package simulator.entities.network.agents;

import java.util.ArrayList;

import simulator.Network;
import simulator.entities.network.Agent;
import simulator.entities.traffic.*;
import utility.*;

public class DefaultSender extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ACKedSeqNum; // Do we need this?! yes!

	// indicates the in-flight (unACKed) segmentsToSend
	private int inFlight;

	/* Bytes remained to send */
	private int remainingSegments;
	/* Sequence number indicator */
	// indicates the sequence number for latest sent segment
	private int seqNum;
	/* Sending Window */
	private int sWnd_;

	private int toSend;

	public DefaultSender(Flow flow, int sWnd) {
		super(flow);
		srcHostID = flow.getSrcHostID();
		dstHostID = flow.getDstHostID();
		remainingSegments = flow.getSize();

		/* initializing the state variables of the sender */
		sWnd_ = sWnd;
		seqNum = 0;
		ACKedSeqNum = 0;
		inFlight = 0;
		toSend = 0;
		segmentsToSend = new ArrayList<Segment>();
		interSegmentDelay_ = 0;
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Agent) --------- */
	/* --------------------------------------------------- */
	public void recvSegment(Network net, Segment segment) {
		segmentsToSend.clear();
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYNACK:
			/** ===== Statistical Counters ===== **/
			flow.dataSendingStartTime = net.getCurrentTime();
			flow.ackSeqNumArrivalTimes.put((float) segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
			ACKedSeqNum = segment.getSeqNum();
			prepareSegmentsToSend();
			break;
		case Keywords.Segments.Types.ACK:
			/** ===== Statistical Counters ===== **/
			flow.ackSeqNumArrivalTimes.put((float) segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
			if (isACKNumExpected(segment.getSeqNum())) {
				if (segment.getSeqNum() == flow.getSize()) {
					/** ===== Statistical Counters ===== **/
					flow.FINSendingTime = net.getCurrentTime();
					/** ================================ **/
					segmentsToSend.add(genFIN());
					break;
				}
				prepareSegmentsToSend();
			} else {
				// This is the case that the receiver is demanding something else
			}
			break;
		case Keywords.Segments.Types.FINACK:
			break;
		default:
			break;
		}
	}

	/* ########## Private ################################ */
	private Segment genDATASegment() {
		if (remainingSegments > 0) {
			Segment seg = new Segment(this.flow.getID(), Keywords.Segments.Types.DATA, seqNum,
					Keywords.Segments.Sizes.DataSegSize, srcHostID, dstHostID);
			remainingSegments--;
			return seg;
		} else if (remainingSegments == 0) {
			return null;
		} else {
			return null;
		}
	}

	private Segment genFIN() {
		seqNum++;
		Segment seg = new Segment(flow.getID(), Keywords.Segments.Types.UncontrolledFIN, seqNum,
				Keywords.Segments.Sizes.FINSegSize, srcHostID, dstHostID);
		return seg;
	}

	private Segment genSYN() {
		Segment seg = new Segment(flow.getID(), Keywords.Segments.Types.SYN,
				Keywords.Segments.SpecialSequenceNumbers.SYNSeqNum, Keywords.Segments.Sizes.SYNSegSize, srcHostID,
				dstHostID);
		return seg;
	}

	private boolean isACKNumExpected(int receivedACKNum) {
		if (receivedACKNum == ACKedSeqNum + 1) {
			ACKedSeqNum = receivedACKNum;
			inFlight--;
			return true;
		} else {
			return false;
		}
	}

	/* =========== Segment creation methods=============== */
	private void prepareSegmentsToSend() {
		segmentsToSend.clear();
		toSend = (int) Mathematics.minDouble(sWnd_ - inFlight, remainingSegments);
		for (int i = 0; i < toSend; i++) {
			seqNum++;
			segmentsToSend.add(genDATASegment());
			inFlight += 1;
		}
	}

	/* ########## Public ################################# */
	public void sendFirst(Network net) {
		segmentsToSend.clear();
		segmentsToSend.add(genSYN());
	}

	@Override
	public void timeout(Network net, int timerID) {

	}
}
