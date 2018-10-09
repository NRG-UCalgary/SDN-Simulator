package protocols;

import entities.Flow;
import entities.Node;
import entities.Packet;
import system.Network;

public class RBTCP extends Agent {

	public RBTCP(Flow flow) {
		super(flow);
	}

	@Override
	public Network recv(Network net, Packet packet) {
		// TODO Auto-generated method stub
		return super.recv(net, packet);
	}
}
