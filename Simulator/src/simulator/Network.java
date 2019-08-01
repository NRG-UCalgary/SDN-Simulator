package simulator;

import java.util.HashMap;

import simulator.entities.Controller;
import simulator.entities.Host;
import simulator.entities.SDNSwitch;

public class Network {

	/* Statistical holder */

	public Controller controller;
	/* Simulation entities */
	private float currentTime; // in MicroSecond
	public EventList eventList;

	/* Topology entities */
	public HashMap<Integer, Host> hosts;
	public HashMap<Integer, SDNSwitch> switches;

	public Network() {
		hosts = new HashMap<Integer, Host>();
		switches = new HashMap<Integer, SDNSwitch>();
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
