package system;

import java.util.HashMap;

import entities.*;

public class Network {

	/* Statistical holder */

	/* Topology entities */
	public HashMap<Integer, Host> hosts;
	public HashMap<Integer, SDNSwitch> switches;
	public Controller controller;

	/* Simulation entities */
	private double time; // in millisecond
	public EventList eventList;

	public Network() {
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
