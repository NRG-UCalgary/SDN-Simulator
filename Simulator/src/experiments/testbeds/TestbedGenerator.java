package experiments.testbeds;

import system.Main;
import system.utility.Keywords;

public class TestbedGenerator {

	public TestbedGenerator() {
	}

	public Testbed generate(int networkTopology, int networkType) {
		switch (networkTopology) {
		case Keywords.Inputs.Testbeds.Topologies.Dumbbell:
			return new Dumbbell(networkType);
		case Keywords.Inputs.Testbeds.Topologies.DataCenter:
			return new DataCenter(networkType);
		case Keywords.Inputs.Testbeds.Topologies.ParkingLot:
			return new ParkingLot(networkType);
		default:
			Main.error(this.getClass().getName(), "generate", "Invalid network type.");
			return null;
		}
	}

}
