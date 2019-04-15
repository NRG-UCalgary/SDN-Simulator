package system;

import java.util.HashMap;
import java.util.Map;

import entities.*;
import utilities.Logger;

public class Network {
	private Logger log = new Logger();

	/* Topology entities */
	public Map<String, Host> hosts;
	public Map<String, SDNSwitch> switches;
	public Controller controller;
	
	/* Controller databases */
	 
	
	/* Simulation entities */
	public double time;
	public EventList eventList;

	public Network() {
		hosts = new HashMap<String, Host>();
		switches = new HashMap<String, SDNSwitch>();
		
		time = 0;
		eventList = new EventList();
	}

}
