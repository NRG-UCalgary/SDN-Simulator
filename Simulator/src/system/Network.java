package system;

import java.util.HashMap;

import entities.*;
import entities.controllers.Controller;
import entities.switches.SDNSwitch;

public class Network {

	/* Statistical holder */

	/* Topology entities */
	public HashMap<Integer, Host> hosts;
	public HashMap<Integer, SDNSwitch> switches;
	public Controller controller;

	/* Simulation entities */
	private float currentTime; // in MicroSecond
	public EventList eventList;

	public Network() {
		hosts = new HashMap<Integer, Host>();
		switches = new HashMap<Integer, SDNSwitch>();
		currentTime = 0;
		eventList = new EventList();
	}

	public void updateTime(float currentTime) {
		this.currentTime = currentTime;
	}

	public float getCurrentTime() {
		return currentTime;
	}

}
