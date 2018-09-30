package entities;

public class Buffer {
	private int capacity;
	// private String policy;
	public int occupancy;

	/* Shows the waiting time for the upcoming packet to be sent */
	/* Must be updated each time a packet is Enqueued */
	private double wait_time;

	public Buffer(int cap, String policy) {
		this.capacity = cap;
		// this.policy = policy;
		occupancy = 0;
	}

	public String enQueue() {
		return null;
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

	/* Call in Class::Event */
	/* Returns queue time for the packet */
	public double getWaitTime() {
		// the wait time for new arrival packet to the queue is equal to
		// transmission_delay * number_of_packets in the queue

		wait_time = 0.0; ///// ###################### Must be completed

		return this.wait_time;
	}

	/* Call in Class::Event */
	public void updateDepartureTime(Double t) {
		this.wait_time = this.wait_time + t;
	}
}
