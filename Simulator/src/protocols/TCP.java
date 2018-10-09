package protocols;

import java.util.HashMap;
import java.util.Map;

import entities.*;
import system.*;

public class TCP extends Agent {
	private Logger log = new Logger();

	private final int SYN_PACKET_NUMBER = 0;
	private final int SYN_PACKET_SIZE = 40;
	private final int ACK_PACKET_SIZE = 40;
	private final int DATA_PACKET_SIZE = 400;
	private final double TIME_OUT = 5000000.0;

	/* TCP state variables */
	private int send_window_;
	private int seq_num_;
	private int packets_to_send_;
	private boolean connection_established_;
	private Map<Integer, Boolean> deliveries_; // Map <Packet_Seq_Num, hasDelivered>
	private Map<Integer, Boolean> send_timers_; // Map <Packet_Seq_Num, isTimerActive>

	public TCP(Flow flow) {
		super(flow);

		/* Initializing state variables */
		send_window_ = 1;
		seq_num_ = 0;
		packets_to_send_ = flow.getSize();
		deliveries_ = new HashMap<Integer, Boolean>();
		send_timers_ = new HashMap<Integer, Boolean>();
		packets_to_send_ = this.flow.getSize();
		connection_established_ = false;
	}

	/**************************************************************************/
	/************ Transport Protocol Methods **********************************/
	/**************************************************************************/
	@Override
	public Network recv(Network net, Packet packet) {
		log.entranceToMethod("TCP", "recv");
		// TODO Auto-generated method stub
		switch (packet.getType()) {
		case "SYN":
			Packet ack_packet = createSYNACK();
			double start_time = net.time;
			String type = "Arrival";
			Node src_node = this.flow.getDst();
			net.event_List.generateEvent(start_time, type, ack_packet, src_node);

			// Updating TCP States
			connection_established_ = true;

			break;
		case "SYNACK":
			// Connection is established
			connection_established_ = true;

			// First data packet should be sent

			// The seq_num_ should be set to 1
			seq_num_ = 1;
			packets_to_send_--;
			net = send(net);
			// TODO TCP slow start must be implemented here and in the send() function
			break;
		case "ACK":
			// It should be checked to see whether the ACK received before time-out or after
			// it
			if (send_timers_.get(packet.getSeqNum())) {
				// The timer is still active which means the ACK has arrived before time-out
				// The deliveries_ must be updated for the corresponding ACK
				deliveries_.put(packet.getSeqNum(), true);

				System.out.println("ACK seq: " + packet.getSeqNum() + "TCP seq: " + seq_num_);
				if (packet.getSeqNum() == seq_num_) {

					// TCP sates must be updated
					send_window_ = send_window_ * 2;
					packets_to_send_ = packets_to_send_ - send_window_;
					seq_num_ = packet.getSeqNum() + 1;

					// The next Data Packet must be sent now.
					net = send(net);
				}

			} else {
				// The timer is not active which means time-out already happened and ACK is
				// received late

				// TODO implement what happens if time-out happens
			}

			break;
		case "Data":
			// Updating TCP states
			seq_num_ = packet.getSeqNum();

			Packet ack_packet1 = createACK();
			double start_time1 = net.time;
			String type1 = "Arrival";
			Node src_node1 = this.flow.getDst();
			net.event_List.generateEvent(start_time1, type1, ack_packet1, src_node1);
			break;
		default:
			break;
		}
		return super.recv(net, packet);
	}

	@Override
	public Network start(Network net) {
		log.entranceToMethod("TCP", "start");
		// In TCP, the start method, creates the SYN packet and its corresponding events
		// Create a SYN message
		Packet syn_packet = createSYN();
		// Create the First-Packet Event for the SYN message
		double start_time = this.flow.getStartTime();
		String type = "Arrival";
		Node src_node = this.flow.getSrc();
		net.event_List.generateEvent(start_time, type, syn_packet, src_node);

		// Set the timer
		setTimer(net, syn_packet);

		return super.start(net);
	}

	@Override
	public Network timeOut(Network net, Packet packet) {
		// Checking send_timer_ to see if the packet has delivered
		if (deliveries_.get(packet.getSeqNum())) {
			// The ACK has been delivered before time-out
			// Essentially nothing should happen
			// The timer and the delivery entries should be removed
			send_timers_.remove(packet.getSeqNum());
			deliveries_.remove(packet.getSeqNum());

		} else {
			// The ACK has not been arrived and time-out is done
			// The Data Packet should be sent again
			send_timers_.put(packet.getSeqNum(), false);
			// TODO implement what happens after time-out

		}
		return super.timeOut(net, packet);

	}

	/** #################################################################### **/

	/**************************************************************************/
	/************ Transport Protocol Methods **********************************/
	/**************************************************************************/

	/* Creates arrival event for the new send packets */
	private Network send(Network net) {
		log.entranceToMethod("TCP", "send");
		// In TCP, the start method, creates the SYN packet and its corresponding events
		// Create a SYN message
		double next_time = net.time;

		for (int i = 0; i < send_window_; i++) {
			Packet next_packet = createDataPacket();
			// Create the First-Packet Event for the SYN message
			String next_type = "Arrival";
			Node next_node = this.flow.getSrc();
			net.event_List.generateEvent(next_time, next_type, next_packet, next_node);
			if (!(i == send_window_ - 1)) {
				seq_num_++;
				next_time = next_time + 3.33; // Hardcoded for now. Should change later
			}
			// Set the timer
			setTimer(net, next_packet);
		}

		return net;
	}

	/* Returns a SYN packet */
	private Packet createSYN() {
		log.entranceToMethod("TCP", "createSYN");
		Packet syn_packet = new Packet(flow.getLabel(), SYN_PACKET_NUMBER, SYN_PACKET_SIZE, flow.getSrc(),
				flow.getDst());
		syn_packet.setType("SYN");
		return syn_packet;
	}

	/* Returns a data packet */
	private Packet createDataPacket() {
		log.entranceToMethod("TCP", "createPacket");
		Packet packet = new Packet(flow.getLabel(), seq_num_, DATA_PACKET_SIZE, flow.getSrc(), flow.getDst());
		packet.setType("Data");
		return packet;
	}

	/* Returns an ACK packet */
	private Packet createACK() {
		log.entranceToMethod("TCP", "createACK");
		Packet packet = new Packet(flow.getLabel() + ".ACK", seq_num_, ACK_PACKET_SIZE, flow.getDst(), flow.getSrc());
		packet.setType("ACK");
		return packet;
	}

	/* Returns a SYNACK packet */
	private Packet createSYNACK() {
		log.entranceToMethod("TCP", "createSYNACK");
		Packet packet = new Packet(flow.getLabel() + ".ACK", seq_num_, ACK_PACKET_SIZE, flow.getDst(), flow.getSrc());
		packet.setType("SYNACK");
		return packet;
	}

	private Network setTimer(Network net, Packet packet) {
		log.entranceToMethod("TCP", "setTimer");
		// TODO set the timer flag to true and create a time_out event

		send_timers_.put(packet.getSeqNum(), true);
		deliveries_.put(packet.getSeqNum(), false);

		// Creating the TCP-TimeOut event
		String next_type = "TCP-TimeOut";
		Double next_time = net.time + this.TIME_OUT;
		Node next_node = net.nodes.get(flow.getSrc());

		// the time-out happened
		net.event_List.generateEvent(next_time, next_type, packet, next_node);

		return net;

	}
}
