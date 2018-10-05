package protocols;

import entities.Flow;
import entities.Node;
import system.Network;

public class RBTCP extends Agent {

	public RBTCP(Flow flow) {
		super(flow);
	}

	@Override
	public Network recv(Network net, String packet_type) {
		// TODO Auto-generated method stub
		return super.recv(net, packet_type);
	}
}
