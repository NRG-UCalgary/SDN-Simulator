package nrg.sdnsimulator.trafficgenerator;

import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Traffic {
	private TreeMap<Integer, Float> arrivalTimePerFlowID;
	private TreeMap<Integer, Integer> flowSizePerFlowID;

	public Traffic() {
		arrivalTimePerFlowID = new TreeMap<Integer, Float>();
		flowSizePerFlowID = new TreeMap<Integer, Integer>();
	}

}
