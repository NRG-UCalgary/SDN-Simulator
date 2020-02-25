package com.nrg.sdnsimulator.core.entity.network.controller.router;

import java.util.HashMap;

import com.nrg.sdnsimulator.core.Network;
import com.nrg.sdnsimulator.core.entity.network.SDNSwitch;
import com.nrg.sdnsimulator.core.utility.Logger;

public abstract class Router {
	protected Logger log = new Logger();

	public Router() {
	}

	public abstract void setNodeInformation(HashMap<Integer, SDNSwitch> nodes);

	public abstract HashMap<Integer, Integer> run(Network net, int srcID, int targetID);

}
