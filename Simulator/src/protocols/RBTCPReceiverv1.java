package protocols;

import entities.*;
import system.*;

public class RBTCPReceiverv1 extends Agent {

	// indicates the sequence number of the latest received ACK
	private int ackNo_;

	private double rtt_;
	private int flowCount_;
	private int sWnd_;

	public RBTCPReceiverv1(Flow flow) {
		super(flow);
		this.srcNode = flow.getDst();
		this.dstNode = flow.getSrc();
		ackNo_ = 0;
		rtt_ = 0;
		flowCount_ = 0;
		sWnd_ = 0;
	}

	/** Overriding basic methods of Agent class for RBTCPSender **/
	@Override
	public Network recv(Network net, Segment recvdSegment) {
		/*---------------------------------------------------*/
		/* Updating centralized congestion control variables */
		/*---------------------------------------------------*/
		rtt_ = recvdSegment.rtt;
		flowCount_ = recvdSegment.flowCount;
		sWnd_ = recvdSegment.sWndSize;
		/*---------------------------------------------------*/

		/* Updating the ackNo_ state variable */
		ackNo_ = recvdSegment.getSeqNum() + 1;

		switch (recvdSegment.getType()) {
		case DATA:
			net = send(net, ACK);
			break;
		case SYN:
			net = send(net, SYNACK);
			break;
		/* ==================================== */
		/* Not applicable for now */
		/* Maybe in future we can separate different types of ACKs */
		case FINACK:
			break;
		case FIN:
			net = send(net, FINACK);
			break;
		/* ==================================== */
		default:
			Main.print("RBTCPReceiverv1.recv()::We should not get here.");
			break;
		}
		return super.recv(net, recvdSegment);
	}

	/** ###################################################### **/

	/* Local methods */

	private Network send(Network net, String segmentType) {
		Main.print("RBTCPReceiverv1.send()::This is the value for this.srcNode():: " + this.srcNode.getLabel());
		net.event_List.generateEvent(net.time, "ARRIVAL", genSegment(segmentType), this.srcNode);
		return net;
	}

	private Segment genSegment(String segmentType) {
		// TODO update the flow label based on the implementation of the ACK stream in
		// the network layer simulator
		Segment seg = new Segment(this.flow.getLabel() + ACKFlowExtention, segmentType, ackNo_, ACKSegSize,
				this.srcNode, this.dstNode);
		/*--------------------------------------------------*/
		/* Setting centralized congestion control variables */
		/*--------------------------------------------------*/
		seg.rtt = rtt_;
		seg.sWndSize = sWnd_;
		seg.flowCount = flowCount_;
		/*--------------------------------------------------*/

		return seg;
	}

}
