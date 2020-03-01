package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.agent;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.Agent;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Flow;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Segment;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Keywords;

public class DefaultReceiver extends Agent {

	private int ACKNum;

	public DefaultReceiver(Flow flow) {
		super(flow);
		srcHostID = flow.getSrcHostID();
		dstHostID = flow.getDstHostID();
		ACKNum = 0;
	}

	public void recvSegment(Network net, Segment segment) {
		segmentsToSend.clear();
		switch (segment.getType()) {
		case Keywords.Segments.Types.SYN:
			segmentsToSend.add(new Segment(flow.getID(), Keywords.Segments.Types.SYNACK, ACKNum,
					Keywords.Segments.Sizes.SYNSegSize, srcHostID, dstHostID));
			break;
		case Keywords.Segments.Types.DATA:
			updateACKNum(segment.getSeqNum());
			segmentsToSend.add(new Segment(flow.getID(), Keywords.Segments.Types.ACK, ACKNum,
					Keywords.Segments.Sizes.ACKSegSize, srcHostID, dstHostID));
			break;
		case Keywords.Segments.Types.FIN:
			segmentsToSend.add(new Segment(flow.getID(), Keywords.Segments.Types.FINACK, segment.getSeqNum(),
					Keywords.Segments.Sizes.FINSegSize, srcHostID, dstHostID));
			/** ===== Statistical Counters ===== **/
			net.hosts.get(this.dstHostID).transportAgent.flow.completionTime = net.getCurrentTime();
			/** ================================ **/
			break;
		default:
			break;
		}
	}

	// TODO design bug and must be resolved later
	@Override
	public void sendFirst(Network net) {
	}

	/** ###################################################### **/

	/* Local methods */
	private void updateACKNum(int recvdSeqNum) {
		if (recvdSeqNum == ACKNum + 1) {
			ACKNum++;
		}
	}

	@Override
	public void timeout(Network net, int timerID) {
		// TODO Auto-generated method stub

	}

}
