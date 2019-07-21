package experiments.testbeds;

import system.Main;
import system.utility.Keywords;

public class TestbedGenerator {

	public TestbedGenerator() {
	}

	public Testbed generate(short networkTopology, short networkType) {
		switch (networkTopology) {
		case Keywords.Testbeds.Topologies.Dumbbell:
			return new Dumbbell(networkType);
		case Keywords.Testbeds.Topologies.DataCenter:
			return new DataCenter(networkType);
		case Keywords.Testbeds.Topologies.ParkingLot:
			return new ParkingLot(networkType);
		default:
			Main.error(this.getClass().getName(), "generate", "Invalid network type.");
			return null;
		}
	}

}
