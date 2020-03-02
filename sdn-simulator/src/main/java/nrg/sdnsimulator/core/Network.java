package nrg.sdnsimulator.core;

import java.util.HashMap;

import nrg.sdnsimulator.core.entity.network.Controller;
import nrg.sdnsimulator.core.entity.network.Host;
import nrg.sdnsimulator.core.entity.network.Link;
import nrg.sdnsimulator.core.entity.network.SDNSwitch;

public class Network {

	private float currentTime;
	private EventList eventList;
	private HashMap<Integer, Controller> controllers;
	private HashMap<Integer, SDNSwitch> switches;
	private HashMap<Integer, Host> hosts;
	private HashMap<Integer, Link> links;

	public Network() {
		controllers = new HashMap<Integer, Controller>();
		hosts = new HashMap<Integer, Host>();
		switches = new HashMap<Integer, SDNSwitch>();
		links = new HashMap<Integer, Link>();
		eventList = new EventList();
		currentTime = 0;
	}

	public Controller getController(int id) {
		return controllers.get(id);
	}

	public SDNSwitch getSwitch(int id) {
		return switches.get(id);
	}

	public Host getHost(int id) {
		return hosts.get(id);
	}

	public Link getLink(int id) {
		return links.get(id);
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public void updateTime(float currentTime) {
		this.currentTime = currentTime;
	}

	public EventList getEventList() {
		return eventList;
	}

	public HashMap<Integer, Controller> getControllers() {
		return controllers;
	}

	public void setControllers(HashMap<Integer, Controller> controllers) {
		this.controllers = controllers;
	}

	public HashMap<Integer, SDNSwitch> getSwitches() {
		return switches;
	}

	public void setSwitches(HashMap<Integer, SDNSwitch> switches) {
		this.switches = switches;
	}

	public HashMap<Integer, Host> getHosts() {
		return hosts;
	}

	public void setHosts(HashMap<Integer, Host> hosts) {
		this.hosts = hosts;
	}

	public HashMap<Integer, Link> getLinks() {
		return links;
	}

	public void setLinks(HashMap<Integer, Link> links) {
		this.links = links;
	}

}
