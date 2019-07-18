package experiments.testbeds;

import system.Main;
import system.utility.Keywords;

public class TestbedGenerator {

	public TestbedGenerator() {
	}

	public Testbed generate(int networkTopology, int networkType) {
		switch (networkTopology) {
		case Keywords.Dumbbell:
			return new Dumbbell(networkType);
		case Keywords.DataCenter:
			return new DataCenter(networkType);
		case Keywords.ParkingLot:
			return new ParkingLot(networkType);
		default:
			Main.error(this.getClass().getName(), "generate", "Invalid network type.");
			return null;
		}
	}

}
