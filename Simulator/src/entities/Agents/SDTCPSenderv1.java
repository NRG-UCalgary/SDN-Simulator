package entities.Agents;

import java.util.ArrayList;

import entities.*;
import system.*;
import system.utility.*;

public class SDTCPSenderv1 extends Agent {

	protected int bigrtt_;

	/* Sending Window */
	private int sWnd_;

	/* Bytes remained to send */
	private int remainingSegments;

	/* Sequence number indicator */
	// indicates the sequence number for latest sent segment
	private int seqNum;
	// indicates the sequence number of the latest received ACK
	private int ACKedSeqNum; // Do we need this?! yes!
	// indicates the in-flight (unACKed) segmentsToSend
	private int inFlight;

	private int toSend;
	private ArrayList<Segment> segmentsToSend;

	public SDTCPSenderv1(Flow flow) {
		super(flow);
		srcHostID = flow.getSrcHostID();
		dstHostID = flow.getDstHostID();
		remainingSegments = flow.getSize();

		/* initializing the state variables of the sender */
		sWnd_ = 0;
		seqNum = 0;
		ACKedSeqNum = 0;
		inFlight = 0;
		toSend = 0;
		segmentsToSend = new ArrayList<Segment>();
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Agent) --------- */
	/* --------------------------------------------------- */

	/* ########## Public ################################# */
	public void sendSYN(Network net) {
		Segment synSegment = genSYN();
		float nextTime = flow.arrivalTime;
		int nextNodeID = net.hosts.get(srcHostID).accessSwitchID;
		net.eventList.addDepartureFromHost(nextTime, srcHostID, nextNodeID, new Packet(synSegment, null));
		// net.eventList.addEvent(new DepartureFromHost(nextTime, srcHostID, nextNodeID,
		// new Packet(synSegment, null)));
		mostRecentSegmentDepartureTime = nextTime;
		/** ===== Statistical Counters ===== **/
		this.flow.totalSentSegments++;
		this.flow.dataSeqNumSendingTimes.put((float) synSegment.getSeqNum(), nextTime);
		/** ================================ **/
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
		case Keywords.Segments.Types.ACK:
			/** ===== Statistical Counters ===== **/
			flow.ackSeqNumArrivalTimes.put((float) segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
			if (isACKNumExpected(segment.getSeqNum())) {
				if (segment.getSeqNum() == flow.getSize()) {
					/** ===== Statistical Counters ===== **/
					flow.FINSendingTime = net.getCurrentTime();
					/** ================================ **/
					sendFIN(net);
					break;
				}
				prepareSegmentsToSend();
				sendMultipleSegments(net, segmentsToSend);

			} else {
				// This is the case that the receiver is demanding something else
			}
			break;
		case Keywords.Segments.Types.SYNACK:
			/** ===== Statistical Counters ===== **/
			flow.dataSendingStartTime = net.getCurrentTime();
			flow.ackSeqNumArrivalTimes.put((float) segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
			ACKedSeqNum = segment.getSeqNum();
			prepareSegmentsToSend();
			sendMultipleSegments(net, segmentsToSend);
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case Keywords.Segments.Types.FINACK:
			/** ===== Statistical Counters ===== **/
			flow.ackSeqNumArrivalTimes.put((float) segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
			break;
		case Keywords.Segments.Types.FIN:
			break;
		/* ==================================== */
		default:
			break;
		}
	}

	/* ########## Private ################################ */

	/* =========== Segment creation methods=============== */
	private void prepareSegmentsToSend() {
		// TODO could be changed in future
		segmentsToSend.clear();
		toSend = (int) Mathematics.minInteger(sWnd_ - inFlight, remainingSegments);
		for (int i = 0; i < toSend; i++) {
			seqNum++;
			segmentsToSend.add(genDATASegment());
			inFlight += 1;
		}
	}

	private void sendFIN(Network net) {
		sendSegment(net, genFIN());
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

	private Segment genSYN() {
		Segment seg = new Segment(flow.getID(), Keywords.Segments.Types.SYN,
				Keywords.Segments.SpecialSequenceNumbers.SYNSeqNum, Keywords.Segments.Sizes.SYNSegSize, srcHostID,
				dstHostID);
		return seg;
	}

	private Segment genFIN() {
		seqNum++;
		Segment seg = new Segment(flow.getID(), Keywords.Segments.Types.UncontrolledFIN, seqNum,
				Keywords.Segments.Sizes.FINSegSize, srcHostID, dstHostID);
		return seg;
	}

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
}
