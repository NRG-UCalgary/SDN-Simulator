package protocols;

import entities.Flow;
import entities.Node;
import entities.Segment;
import system.Network;

public class RBTCP extends Agent {

	public RBTCP(Flow flow) {
		super(flow);
	}

	@Override
	public Network recv(Network net, Segment packet) {
		// TODO Auto-generated method stub
		return super.recv(net, packet);
	}
}
