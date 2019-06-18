package entities.controllers.routers;

import java.util.HashMap;
import entities.Link;
import system.utility.Logger;

public abstract class Router {
	protected Logger log = new Logger();

	public Router() {
	}

	public abstract HashMap<Integer, Link> run(int srcID, int targetID);

}
