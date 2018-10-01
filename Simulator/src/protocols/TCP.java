package protocols;

import entities.*;
import system.*;

public class TCP extends Agent {
	private Logger log = new Logger();

	private final int SYN_PACKET_NUMBER = 0;
	private final int SYN_PACKET_SIZE = 40;
	private final int ACK_PACKET_SIZE = 40;
	private final int DATA_PACKET_SIZE = 1000;

	/* TCP state variables */
	private int seq_num_;
	private int ack_seq_num_;
	private int packets_to_send_;
	private boolean connection_established_;
	private boolean ack_arrived_notif_;

	public TCP(Flow flow) {
		super(flow);

		/* Initializing state variables */
		seq_num_ = 0;
		packets_to_send_ = flow.getSize();
	}

	@Override
	public Network recv(Network net) {
		// TODO Auto-generated method stub
		Packet ack_packet = createACK();
		double start_time = net.time;
		String type = "Arrival";
		Node src_node = this.flow.getDst();
		net.event_List.generateEvent(start_time, type, ack_packet, src_node);
		// Set the ack timer
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

		/* The send timer must be set for the SYN packet */
		// The timer can be implemented as an event with a control variable in the TCP
		// instance
		return super.start(net);
	}

	private Packet createSYN() {
		log.generalLog("Entered TCP.createSYN().");
		Packet syn_packet = new Packet(flow.getLabel(), SYN_PACKET_NUMBER, SYN_PACKET_SIZE, flow.getSrc(),
				flow.getDst());
		return syn_packet;
	}

	private Packet createPacket() {
		log.generalLog("Entered TCP.createPacket().");
		Packet packet = new Packet(flow.getLabel(), seq_num_, DATA_PACKET_SIZE, flow.getSrc(), flow.getDst());
		return packet;
	}

	private Packet createACK() {
		log.generalLog("Entered TCP.createACK().");
		Packet packet = new Packet(flow.getLabel(), seq_num_, ACK_PACKET_SIZE, flow.getDst(), flow.getSrc());
		return packet;
	}

	private void setTimer() {

	}
}
