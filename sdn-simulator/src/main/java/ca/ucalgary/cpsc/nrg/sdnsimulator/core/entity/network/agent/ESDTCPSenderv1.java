package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.agent;

import java.util.ArrayList;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.Agent;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Flow;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Segment;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Keywords;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Mathematics;

public class ESDTCPSenderv1 extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ACKedSeqNum; // Do we need this?! yes!

	protected int bigrtt_;

	// indicates the in-flight (unACKed) segmentsToSend
	private int inFlight;

	/* Bytes remained to send */
	private int remainingSegments;
	private ArrayList<Segment> segmentsToSend;
	/* Sequence number indicator */
	// indicates the sequence number for latest sent segment
	private int seqNum;

	/* Sending Window */
	private int sWnd_;
	private int toSend;

	public ESDTCPSenderv1(Flow flow) {
		super(flow);
		srcHostID = flow.getSrcHostID();
		dstHostID = flow.getDstHostID();
		remainingSegments = flow.getSize();

		/* initializing the state variables of the sender */
		sWnd_ = 1;
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

	/* ########## Private ################################ */

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

	public void recvSegment(Network net, Segment segment) {
		segmentsToSend.clear();
		switch (segment.getType()) {
		case Keywords.Segments.Types.CTRL:
			/* Update the congestion control variables */
			sWnd_ = segment.sWnd_;
			bigrtt_ = segment.bigRTT_;
			interSegmentDelay_ = segment.interSegmentDelay_;
			break;
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

	/* ########## Public ################################# */
	public void sendFirst(Network net) {
		segmentsToSend.add(genSYN());
	}

	@Override
	public void timeout(Network net, int timerID) {

	}
}
