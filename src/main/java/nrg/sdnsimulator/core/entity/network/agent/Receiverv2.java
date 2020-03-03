package nrg.sdnsimulator.core.entity.network.agent;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.Agent;
import nrg.sdnsimulator.core.entity.traffic.Flow;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Keywords;

public class Receiverv2 extends Agent {

	private int ACKNum;

	public Receiverv2(Flow flow) {
		super(flow);
		ACKNum = -1;
	}

	@Override
	public void recvSegment(Network net, Segment segment) {
		segmentsToSend.clear();
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			ACKNum = 0;
			segmentsToSend.add(new Segment(flow.getID(), Keywords.Segments.Types.SYNACK,
					Keywords.Segments.SpecialSequenceNumbers.SYNSeqNum,
					Keywords.Segments.Sizes.SYNSegSize, this.srcHostID, this.dstHostID));
			break;
		case Keywords.Segments.Types.DATA:
			// Debugger.methodEntrance("Receiverv2", "recvPacket", "Segment Type: DATA");
			segmentsToSend
					.add(new Segment(flow.getID(), Keywords.Segments.Types.ACK, segment.getSeqNum(),
							Keywords.Segments.Sizes.ACKSegSize, this.srcHostID, this.dstHostID));
			break;
		case Keywords.Segments.Types.FIN:
			/*
			 * segmentsToSend.add(new Segment(flow.getID(), Keywords.Segments.Types.FINACK,
			 * segment.getSeqNum(), Keywords.Segments.Sizes.FINSegSize, this.srcHostID,
			 * this.dstHostID));
			 */
			break;
		case Keywords.Segments.Types.CTRL:
			break;
		default:
			break;
		}
	}

	private void updateACKNum(int recvdSeqNum) {
		if (recvdSeqNum == ACKNum + 1) {
			ACKNum++;
		}
	}

	@Override
	public void sendFirst(Network net) {
	}

	@Override
	public void timeout(Network net, int timerID) {

	}

	public int getACKNum() {
		return ACKNum;
	}

	public void setACKNum(int aCKNum) {
		ACKNum = aCKNum;
	}

}
