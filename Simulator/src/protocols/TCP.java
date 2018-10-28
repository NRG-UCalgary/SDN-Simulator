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
	private final int MAX_SEGMENT_SIZE = 400;
	private final double TIME_OUT = 5000000.0;

	/* TCP state variables */
	// Congestion Control Vairables
	private int cwnd_;
	private int ssthresh_;
	private int dupACKcount_;
	private boolean slow_start_;
	private boolean congestion_avoidance_;
	private boolean fast_recovery_;

	// General Variables
	private int send_window_;
	private int seq_num_;
	private int ack_seq_num_;
	private int packets_to_send_;
	private boolean connection_established_;
	// private Map<Integer, Boolean> deliveries_; // Map <Packet_Seq_Num,
	// hasDelivered>
	private Map<Integer, Boolean> send_timer_; // Map <Packet_Seq_Num, isTimerActive>

	public TCP(Flow flow) {
		super(flow);

		/* Initializing state variables */
		slow_start_ = true;
		congestion_avoidance_ = false;
		fast_recovery_ = false;
		cwnd_ = MAX_SEGMENT_SIZE;
		ssthresh_ = 64000;
		dupACKcount_ = 0;
		send_window_ = 1;
		seq_num_ = 0;
		ack_seq_num_ = 0;
		packets_to_send_ = flow.getSize();
		// deliveries_ = new HashMap<Integer, Boolean>();
		send_timer_ = new HashMap<Integer, Boolean>();
		packets_to_send_ = this.flow.getSize();
		connection_established_ = false;
	}

	/**************************************************************************/
	/************ Transport Protocol Methods **********************************/
	/**************************************************************************/

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
	public Network recv(Network net, Packet packet) {
		log.entranceToMethod("TCP", "recv");
		// TODO Auto-generated method stub
		switch (packet.getType()) {
		/*********** TCP Sender **********/
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
		/* ######################################### */
		/* ########## ACK arrival ################## */
		/* ######################################### */
		case "ACK":
			if (send_timer_.get(packet.getSeqNum()) == true) {
				// ACK has arrived before the time-out
				send_timer_.put(packet.getSeqNum(), false);

				boolean isDupACK = false;
				if (packet.getSeqNum() == ack_seq_num_) {
					isDupACK = true;
				} else {
					isDupACK = false;
				}
				/* Congestion Control Implementation */
				if (slow_start_) {
					/*-------------------------------------*/
					/*---------- Slow Start ---------------*/
					/*-------------------------------------*/
					// Checking the ACK Seq Number
					if (isDupACK) {
						dupACKcount_++;
						if (dupACKcount_ == 3) {
							// Go to fast recovery state
							fast_recovery_ = true;
							slow_start_ = false;
							congestion_avoidance_ = false;

							// Updating congestion control variables
							ssthresh_ = (int) Math.floor(cwnd_ / 2);
							System.out.println("The new sstresh while going to FF is : " + ssthresh_);
							cwnd_ = ssthresh_ + 3;

							// Retransmitting the missing segment
							net = retransmit(net, packet);
						}
					} else if (!isDupACK) { // Getting a new ACK
						cwnd_ = cwnd_ + MAX_SEGMENT_SIZE;
						dupACKcount_ = 0;
						net = transmit(net, packet);

						if (cwnd_ >= ssthresh_) {
							// Transition to Congestion-Avoidance (without any action)
							congestion_avoidance_ = true;
							slow_start_ = false;
							fast_recovery_ = false;
						}
					}
				} else if (congestion_avoidance_) {
					/*-----------------------------------------*/
					/*---------- Congestion Avoidance ---------*/
					/*-----------------------------------------*/
					// Checking the ACK Seq Number
					if (isDupACK) {
						dupACKcount_++;
						if (dupACKcount_ == 3) {
							// Go to fast recovery state
							fast_recovery_ = true;
							slow_start_ = false;
							congestion_avoidance_ = false;

							// Updating congestion control variables
							ssthresh_ = (int) Math.floor(cwnd_ / 2);
							System.out.println("The new sstresh while going to FF is : " + ssthresh_);
							cwnd_ = ssthresh_ + 3;

							// Retransmitting the missing segment
							net = retransmit(net, packet);
						}
					} else if (!isDupACK) { // Getting a new ACK
						cwnd_ = cwnd_ + (int) Math.floor(MAX_SEGMENT_SIZE * (MAX_SEGMENT_SIZE / cwnd_));
						System.out.println("This is cwnd_ in new ACK in CA: " + cwnd_);
						dupACKcount_ = 0;
						net = transmit(net, packet);
					}
				} else if (fast_recovery_) {
					/*--------------------------------------*/
					/*---------- Fast Recovery -------------*/
					/*--------------------------------------*/
					// Checking the ACK Seq Number
					if (isDupACK) {

						// Updating congestion control variables
						cwnd_ = cwnd_ + MAX_SEGMENT_SIZE;
						net = transmit(net, packet);

					} else if (!isDupACK) { // Getting a new ACK
						// Transition to Congestion-Avoidance state
						congestion_avoidance_ = true;
						slow_start_ = false;
						fast_recovery_ = false;

						// Actions
						cwnd_ = ssthresh_;
						dupACKcount_ = 0;

						// The question is what action should be done here?
					}
				} else {
					log.generalLog("Invalid state for TCP Congestion-Control FSM.");
				}
			} else {
				// Time-out has happened before arrival of ACK
				send_timer_.remove(packet.getSeqNum());
			}
			break;
		/********** TCP Receiver ************/
		case "SYN":
			Packet ack_packet = createSYNACK();
			double start_time = net.time;
			String type = "Arrival";
			Node src_node = this.flow.getDst();
			net.event_List.generateEvent(start_time, type, ack_packet, src_node);

			// Updating TCP States
			connection_established_ = true;

			break;
		/* ######################################### */
		/* ########## Data segment arrival ######### */
		/* ######################################### */
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
	public Network timeOut(Network net, Packet packet) {
		// Check if the ACK of the packet has already received
		if (send_timer_.get(packet.getSeqNum()) == true) {
			send_timer_.put(packet.getSeqNum(), false);

			/* TCP Congestion Control */
			if (slow_start_) {
				/*-------------------------------------*/
				/*---------- Slow Start ---------------*/
				/*-------------------------------------*/
				// Actions
				ssthresh_ = cwnd_ / 2;
				System.out.println("ssthresh after time-out in SS: " + ssthresh_);
				cwnd_ = MAX_SEGMENT_SIZE;
				dupACKcount_ = 0;
				net = retransmit(net, packet);

			} else if (congestion_avoidance_) {
				/*-----------------------------------------*/
				/*---------- Congestion Avoidance ---------*/
				/*-----------------------------------------*/
				// State transition to Slow-Start
				slow_start_ = true;
				congestion_avoidance_ = false;
				fast_recovery_ = false;
				// Actions
				ssthresh_ = cwnd_ / 2;
				System.out.println("ssthresh after time-out in CA: " + ssthresh_);
				cwnd_ = MAX_SEGMENT_SIZE;
				dupACKcount_ = 0;
				net = retransmit(net, packet);
			} else if (fast_recovery_) {
				/*--------------------------------------*/
				/*---------- Fast Recovery -------------*/
				/*--------------------------------------*/
				log.generalLog("Invalid state varible for TCP congestions control.");
				// State transition to Slow-Start
				slow_start_ = true;
				congestion_avoidance_ = false;
				fast_recovery_ = false;
				// Actions
				ssthresh_ = cwnd_ / 2;
				System.out.println("ssthresh after time-out in CA: " + ssthresh_);
				cwnd_ = MAX_SEGMENT_SIZE;
				dupACKcount_ = 0;
				net = retransmit(net, packet);
			}
		} else {
			// Packet has been delivered before the time-out
			send_timer_.remove(packet.getSeqNum());
		}

		return super.timeOut(net, packet);

	}

	/**************************************************************************/
	/************ Transport Protocol Methods **********************************/
	/**************************************************************************/
	private Network retransmit(Network net, Packet ack) {

		return net;
	}

	private Network transmit(Network net, Packet ack) {

		return net;
	}

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
		Packet packet = new Packet(flow.getLabel(), seq_num_, MAX_SEGMENT_SIZE, flow.getSrc(), flow.getDst());
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

		send_timer_.put(packet.getSeqNum(), true);

		// Creating the TCP-TimeOut event
		String next_type = "TCP-TimeOut";
		Double next_time = net.time + this.TIME_OUT;
		Node next_node = net.nodes.get(flow.getSrc().getLabel());

		// the time-out happened
		net.event_List.generateEvent(next_time, next_type, packet, next_node);

		return net;

	}
}
