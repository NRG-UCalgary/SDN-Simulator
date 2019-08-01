package simulator.entities.controllers.routers;

import java.util.HashMap;

import simulator.entities.Link;
import utility.Logger;

public abstract class Router {
	protected Logger log = new Logger();

	public Router() {
	}

	public abstract HashMap<Integer, Link> run(int srcID, int targetID);

	
}
