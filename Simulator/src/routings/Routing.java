package routings;

import network.*;
import system.*;

public class Routing {

	public Routing() {
		// TODO Auto-generated constructor stub
	}

	/* Called in Class::Network.initialized() */
	/* Initializes the path array of the flows */
	public NetList<Flow> generatePaths(NetList<Flow> flows) {
		/* a routing algorithm should be implemented here */
		for (Flow f : flows) {
			System.out.println("Hi");
			f.path.add(null);
			// for() {
			// f.path
			// }
		}
		return flows;
	}
}
