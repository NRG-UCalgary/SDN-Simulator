package system;

import java.util.HashMap;

import entities.*;

public class Network {

	/* Statistical holder */
	public Statistics stats;

	/* Topology entities */
	public HashMap<Integer, Host> hosts;
	public HashMap<Integer, SDNSwitch> switches;
	public Controller controller;

	/* Controller databases */
	// Do we need a set of flows in the net object?
	// What do we use to map them?

	/* Simulation entities */

	private double time;
	public EventList eventList;

	public Network() {
		stats = new Statistics();
		hosts = new HashMap<Integer, Host>();
		switches = new HashMap<Integer, SDNSwitch>();
		time = 0;
		eventList = new EventList();
	}

	public void updateTime(double currentTime) {
		this.time = currentTime;
	}

	public double getCurrentTime() {
		return this.time;
	}

}
