package entities.Agents;

import java.util.ArrayList;

import entities.*;
import system.*;
import system.events.*;
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
		this.srcHostID = flow.getSrcID();
		this.dstHostID = flow.getDstID();
		remainingSegments = flow.getSize();
		Debugger.debug("This is flow size: " + remainingSegments);

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
	public Network sendSYN(Network net) {
		Segment synSegment = genSYN();
		double nextTime = flow.arrivalTime + net.hosts.get(srcHostID).accessLink.getTotalDelay(synSegment.getSize());
		net.eventList
				.addEvent(new ArrivalToSwitch(nextTime, net.hosts.get(srcHostID).accessSwitchID, synSegment, null));
		/** ===== Statistical Counters ===== **/
		this.flow.totalSentSegments++;
		this.flow.dataSeqNumSendingTimes.put(synSegment.getSeqNum(), net.getCurrentTime());
		/** ================================ **/
		return net;
	}

	public Network recvSegment(Network net, Segment segment) {
		segmentsToSend.clear();
		switch (segment.getType()) {
		case Keywords.CTRL:
			/* Update the congestion control variables */
			this.sWnd_ = segment.sWnd_;
			// this.sWnd_ = 10;
			this.bigrtt_ = segment.bigRTT_;
			break;
		case Keywords.ACK:
			/** ===== Statistical Counters ===== **/
			this.flow.ackSeqNumArrivalTimes.put(segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
			if (isACKNumExpected(segment.getSeqNum())) {
				prepareSegmentsToSend();
				net = sendMultipleSegments(net, segmentsToSend);
			} else {
				// This is the case that the receiver is demanding something else
			}
			break;
		case Keywords.SYNACK:
			/** ===== Statistical Counters ===== **/
			this.flow.dataSendingStartTime = net.getCurrentTime();
			this.flow.ackSeqNumArrivalTimes.put(segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
			ACKedSeqNum = segment.getSeqNum();
			prepareSegmentsToSend();
			net = sendMultipleSegments(net, segmentsToSend);
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
	private void prepareSegmentsToSend() {
		// TODO could be changed in future
		segmentsToSend.clear();
		toSend = Mathematics.minInteger(sWnd_ - inFlight, remainingSegments);
		for (int i = 0; i < toSend; i++) {
			seqNum++;
			segmentsToSend.add(genDATASegment());
			inFlight += 1;
		}
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
		Segment seg = new Segment(this.flow.getID(), Keywords.SYN, Keywords.SYNSeqNum, Keywords.SYNSegSize,
				this.srcHostID, this.dstHostID);
		return seg;
	}

	private Segment genDATASegment() {
		if (remainingSegments > 0) {
			Segment seg = new Segment(this.flow.getID(), Keywords.DATA, seqNum, Keywords.DataSegSize, this.srcHostID,
					this.dstHostID);
			remainingSegments--;
			return seg;
		} else if (remainingSegments == 0) {
			return null;
		} else {
			return null;
		}
	}

	// private Segment genFIN() {
	// Segment seg = new Segment(this.flow.getID(), Keywords.FIN, seqNum,
	// Keywords.FINSegSize, this.srcHostID,
	// this.dstHostID);
	// return seg;
	// }

}
