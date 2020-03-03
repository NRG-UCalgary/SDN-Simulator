package nrg.sdnsimulator.core.entity.network.controller.router;

import java.util.HashMap;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.network.SDNSwitch;

public abstract class Router {

	public Router() {
	}

	public abstract void setNodeInformation(HashMap<Integer, SDNSwitch> nodes);

	public abstract HashMap<Integer, Integer> run(Network net, int srcID, int targetID);

}
