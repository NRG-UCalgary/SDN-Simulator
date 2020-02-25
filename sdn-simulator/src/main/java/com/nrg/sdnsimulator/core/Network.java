package com.nrg.sdnsimulator.core;

import java.util.HashMap;

import com.nrg.sdnsimulator.core.entity.network.Controller;
import com.nrg.sdnsimulator.core.entity.network.Host;
import com.nrg.sdnsimulator.core.entity.network.Link;
import com.nrg.sdnsimulator.core.entity.network.SDNSwitch;

public class Network {

	public Controller controller;
	private float currentTime;
	public EventList eventList;

	public HashMap<Integer, Controller> controllers;
	public HashMap<Integer, SDNSwitch> switches;
	public HashMap<Integer, Host> hosts;
	public HashMap<Integer, Link> links;

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

}
