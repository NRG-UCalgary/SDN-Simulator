package protocols;

import entities.*;
import system.*;

public class TCP extends Agent {
	private Logger log = new Logger();

	private final int SYN_PACKET_NUMBER = 0;
	private final int SYN_PACKET_SIZE = 40;

	/* TCP state variables */
	private int seq_num_;
	private int ack_seq_num_;
	private int packets_to_send_;
	private boolean connection_established_;

	public TCP(Flow flow) {
		super(flow);

		/* Initializing state variables */
		seq_num_ = 0;
		packets_to_send_ = flow.getSize();
	}

	@Override
	public Network recv(Network net) {
		// TODO Auto-generated method stub
		return super.recv(net);
	}

	@Override
	public Network start(Network net) {
		log.generalLog("Entered TCP.start().");
		// In TCP, the start method, creates the SYN packet and its corresponding events
		// Create a SYN message
		Packet syn_packet = createSYN();
		// Create the First-Packet Event for the SYN message
		double start_time = this.flow.getStartTime();
		String type = "Arrival";
		Node src_node = this.flow.getSrc();
		net.event_List.generateEvent(start_time, type, syn_packet, src_node);

		return super.start(net);
	}

	private Packet createSYN() {
		log.generalLog("Entered TCP.createSYN().");
		Packet syn_packet = new Packet(flow.getLabel(), SYN_PACKET_NUMBER, SYN_PACKET_SIZE, flow.getSrc(),
				flow.getDst());
		return syn_packet;
	}

}
