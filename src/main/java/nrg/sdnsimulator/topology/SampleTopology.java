package nrg.sdnsimulator.topology;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Simulator;

@Getter
@Setter
public class SampleTopology {

	public SampleTopology() {
	}

	public void run() {
		
		String controllerLabel = "ctrl";
		String acSwitchOne = "acs1";
		String acSwitchTwo = "acs2";
		String netSwitch = "nets";
		String ctrLinkOne = "cl1";
		String ctrLinkTwo = "cl2";
		String ctrLinkThreeNet = "cl3";

		boolean isMonitored = false;

		Simulator sim = new Simulator();

		// create controller
		sim.createController(controllerLabel);

		// create access switches
		sim.createSwitch(acSwitchOne);
		sim.createSwitch(acSwitchTwo);
		// create control links for access switch
		sim.createLink(ctrLinkOne, acSwitchOne, controllerLabel, 100, 100, 10, isMonitored);
		sim.createLink(ctrLinkTwo, acSwitchTwo, controllerLabel, 100, 100, 10, isMonitored);

		// create network switch
		sim.createSwitch(netSwitch);
		// create control link for network switch
		sim.createLink(ctrLinkThreeNet, netSwitch, controllerLabel, 100, 100, 10, isMonitored);
	}

}
