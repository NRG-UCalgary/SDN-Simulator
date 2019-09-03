package simulator.entities.network.hosts;

import java.util.ArrayList;

import simulator.Network;
import simulator.entities.network.Host;
import simulator.entities.traffic.Packet;
import simulator.entities.traffic.Segment;
import utility.Debugger;

public class DefaultHost extends Host {

	public DefaultHost(int ID) {
		super(ID);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Host) ---------- */
	/* --------------------------------------------------- */
	public void initialize(Network net) {
		sendSegments(net, transportAgent.sendFirst(net));

	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public void recvPacket(Network net, int srcNodeID, Packet packet) {
		Debugger.debugToConsole("========== Arrival To Host ===================");
		ArrayList<Segment> segmentsToSend = transportAgent.recvSegment(net, packet.segment);
		sendSegments(net, segmentsToSend);
		Debugger.debugToConsole("==============================================");
	}

}
