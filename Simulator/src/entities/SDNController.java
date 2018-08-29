package entities;

import routings.Routing;
import system.NetList;

public class SDNController {
	public Routing router;

	public SDNController() {
		// TODO Auto-generated constructor stub
		router = new Routing();
	}

	public NetList<Flow> generatePaths(NetList<Flow> flows) {
		router.getPaths(null);
		return flows;
	}

}
