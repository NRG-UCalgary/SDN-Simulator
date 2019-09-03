package simulator.entities.network.controllers.routers;

import java.util.HashMap;

import simulator.Network;
import simulator.entities.network.SDNSwitch;
import utility.Logger;

public abstract class Router {
	protected Logger log = new Logger();

	public Router() {
	}

	public abstract void setNodeInformation(HashMap<Integer, SDNSwitch> nodes);

	public abstract HashMap<Integer, Integer> run(Network net, int srcID, int targetID);

}
