package nrg.sdnsimulator.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nrg.sdnsimulator.core.Simulator;
import nrg.sdnsimulator.topology.SampleTopology;

@RestController
@RequestMapping("/api")
public class MainController {

	@GetMapping("/simulate")
	public void homePage() {
		SampleTopology topo = new SampleTopology();
		topo.run();

	}

}
