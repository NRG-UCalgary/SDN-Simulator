package entities;

public class Buffer {
	private int capacity;
	private String policy;

	private final double NO_WAIT_TIME = 0.0;

	// State variables of Buffer
	public int occupancy;
	private Double most_recent_packet_departure;

	// Constructor
	public Buffer(int cap, String policy) {
		this.capacity = cap;
		occupancy = 0;
		this.policy = policy;
		most_recent_packet_departure = 0.0;
	}

	/* Call in Class::Event */
	/* Returns queue time for the packet */
	public double getWaitTime(Double trans_delay, Double current_time) {
		if (most_recent_packet_departure <= current_time) {
			most_recent_packet_departure = trans_delay;
			return NO_WAIT_TIME;

		} else {
			Double wait_time = most_recent_packet_departure - current_time;
			most_recent_packet_departure = most_recent_packet_departure + trans_delay;
			return wait_time;
		}
	}

	/* Called in Class::Event */
	/* Return true if the buffer is full */
	public boolean isFull() {
		if (occupancy < capacity) {
			return false;
		} else if (occupancy == capacity) {
			return true;
		}
		System.out.println("Error Class::Buffer--Invalid buffer occupancy(" + occupancy + ").");
		return false;
	}
}
