package experiments.traffic;

import java.util.TreeMap;

public class Traffic {
	public TreeMap<Integer, Float> arrivalTimePerFlowID;
	public TreeMap<Integer, Integer> flowSizePerFlowID;

	public Traffic() {
		arrivalTimePerFlowID = new TreeMap<Integer, Float>();
		flowSizePerFlowID = new TreeMap<Integer, Integer>();
	}

}
