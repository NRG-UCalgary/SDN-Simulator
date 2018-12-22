package entities;

public class Segment {
	public int size;
	public int seq_number;
	public int ack_number;
	public int win_size;
	public boolean ACK;
	public boolean SYN;
	public boolean SYN_ACK;
	public boolean FIN;

	public Segment() {
		// TODO Auto-generated constructor stub

		ACK = false;
		SYN = false;
		SYN_ACK = false;
		FIN = false;
	}

}
