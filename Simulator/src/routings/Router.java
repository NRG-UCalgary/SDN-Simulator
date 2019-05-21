package routings;

import java.util.HashMap;
import entities.Link;
import entities.SDNSwitch;
import utilities.Logger;

public abstract class Router {
	protected Logger log = new Logger();

	public Router() {
	}

	public abstract HashMap<SDNSwitch, Link> run(SDNSwitch src, SDNSwitch target);

}
