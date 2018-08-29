package entities;

import system.*;
import routings.*;

public class Controller {
	public Routing router;

	public Controller(Network net, String routing_type) {
		switch (routing_type) {
		case "Dijkstra":
			router = new Dijkstra(net.nodes, net.links);
			break;
		case "Something Else":
			break;
		default:
			break;
		}
	}

}
