package nrg.sdnsimulator.topology;

import nrg.sdnsimulator.core.utility.Keywords;

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
			return null;
		}
	}

}
