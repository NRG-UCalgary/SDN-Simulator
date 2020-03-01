package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.controller.router;

import java.util.HashMap;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.SDNSwitch;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Logger;

public abstract class Router {
	protected Logger log = new Logger();

	public Router() {
	}

	public abstract void setNodeInformation(HashMap<Integer, SDNSwitch> nodes);

	public abstract HashMap<Integer, Integer> run(Network net, int srcID, int targetID);

}
