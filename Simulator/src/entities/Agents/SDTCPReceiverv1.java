package entities.Agents;

import entities.*;
import system.*;
import system.utility.Debugger;
import system.utility.Keywords;

public class SDTCPReceiverv1 extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ACKNum;

	public SDTCPReceiverv1(Flow flow) {
		super(flow);
		srcHostID = flow.getSrcHostID();
		dstHostID = flow.getDstHostID();
		ACKNum = 0;
	}

	public Network recvSegment(Network net, Segment segment) {
		Segment ackSegment;
		/* Updating the ACKNum_ state variable */

		switch (segment.getType()) {
		case Keywords.Operations.Segments.Types.CTRL:
			Main.error("SDTCPREceiver", "recvSegment", "Receiver got CTRL segment.");
			break;
		case Keywords.Operations.Segments.Types.DATA:
			updateACKNum(segment.getSeqNum());
			// TODO late we might want to implement NACK
			ackSegment = new Segment(flow.getID(), Keywords.Operations.Segments.Types.ACK, ACKNum,
					Keywords.Operations.Segments.Sizes.ACKSegSize, this.srcHostID, this.dstHostID);
			net = sendSegment(net, ackSegment);
			break;
		case Keywords.Operations.Segments.Types.SYN:
			// SYNACK must be generated and sent to the sender
			ackSegment = new Segment(flow.getID(), Keywords.Operations.Segments.Types.SYNACK, ACKNum,
					Keywords.Operations.Segments.Sizes.SYNSegSize, this.srcHostID, this.dstHostID);
			net = sendSegment(net, ackSegment);
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case Keywords.Operations.Segments.Types.FINACK:
			break;
		case Keywords.Operations.Segments.Types.FIN:
			Segment FINACKSegment = new Segment(flow.getID(), Keywords.Operations.Segments.Types.FINACK,
					segment.getSeqNum(), Keywords.Operations.Segments.Sizes.FINSegSize, this.srcHostID, this.dstHostID);
			net = sendSegment(net, FINACKSegment);
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
