package simulator;

import java.util.HashMap;
import simulator.entities.network.*;

public class Network {

	/* Statistical holder */

	public Controller controller;
	/* Simulation entities */
	private float currentTime; // in MicroSecond
	public EventList eventList;

	/* Topology entities */
	public HashMap<Integer, Controller> controllers;
	public HashMap<Integer, SDNSwitch> switches;
	public HashMap<Integer, Host> hosts;
	public HashMap<Integer, Link> links;

	public Network() {
		controllers = new HashMap<Integer, Controller>();
		hosts = new HashMap<Integer, Host>();
		switches = new HashMap<Integer, SDNSwitch>();
		links = new HashMap<Integer, Link>();
		currentTime = 0;
		eventList = new EventList();
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public void updateTime(float currentTime) {
		this.currentTime = currentTime;
	}

}
