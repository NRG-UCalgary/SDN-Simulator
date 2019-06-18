package transport_protocols;

import entities.*;
import system.*;
import utilities.Debugger;
import utilities.Keywords;

public class SDTCPReceiverv1 extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ACKNum;

	public SDTCPReceiverv1(Flow flow) {
		super(flow);
		this.srcHostID = flow.getSrcID();
		this.dstHostID = flow.getDstID();
		ACKNum = 0;
	}

	public Network recvSegment(Network net, Segment segment) {
		Segment ackSegment;
		/* Updating the ACKNum_ state variable */

		switch (segment.getType()) {
		case Keywords.CTRL:
			Debugger.debug("Receiver.recvSegment::Receiver has received a CTRL segment.");
			break;
		case Keywords.DATA:
			updateACKNum(segment.getSeqNum());
			// TODO late we might want to implement NACK
			ackSegment = new Segment(flow.getID(), Keywords.ACK, ACKNum, Keywords.ACKSegSize, this.srcHostID,
					this.dstHostID);
			net = sendSegment(net, ackSegment, 0);
			break;
		case Keywords.SYN:
			// SYNACK must be generated and sent to the sender
			ackSegment = new Segment(flow.getID(), Keywords.SYNACK, ACKNum, Keywords.SYNSegSize, this.srcHostID,
					this.dstHostID);
			net = sendSegment(net, ackSegment, 0);
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case Keywords.FINACK:
			break;
		case Keywords.FIN:
			/** ===== Statistical Counters ===== **/
			net.hosts.get(this.dstHostID).transportAgent.flow.completionTime = net.getCurrentTime();
			/** ================================ **/
			break;
		/* ==================================== */
		default:
			Debugger.debug("SDTCPReceiverv1.recv().case\"default\"::We should not get here.");
			break;
		}
		return net;
	}

	/** ###################################################### **/

	/* Local methods */
	private void updateACKNum(int recvdSeqNum) {
		if (recvdSeqNum == ACKNum + 1) {
			ACKNum++;
		}
	}

}
