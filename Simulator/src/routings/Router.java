package routings;

import java.util.HashMap;
import entities.Link;
import utilities.Logger;

public abstract class Router {
	protected Logger log = new Logger();

	public Router() {
	}

	public abstract HashMap<Integer, Link> run(int srcID, int targetID);

}
