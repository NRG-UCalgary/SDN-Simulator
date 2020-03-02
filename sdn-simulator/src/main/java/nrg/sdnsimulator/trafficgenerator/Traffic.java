package nrg.sdnsimulator.trafficgenerator;

import java.util.TreeMap;

public class Traffic {
	private TreeMap<Integer, Float> arrivalTimePerFlowID;
	private TreeMap<Integer, Integer> flowSizePerFlowID;

	public Traffic() {
		arrivalTimePerFlowID = new TreeMap<Integer, Float>();
		flowSizePerFlowID = new TreeMap<Integer, Integer>();
	}

	public TreeMap<Integer, Float> getArrivalTimePerFlowID() {
		return arrivalTimePerFlowID;
	}

	public void setArrivalTimePerFlowID(TreeMap<Integer, Float> arrivalTimePerFlowID) {
		this.arrivalTimePerFlowID = arrivalTimePerFlowID;
	}

	public TreeMap<Integer, Integer> getFlowSizePerFlowID() {
		return flowSizePerFlowID;
	}

	public void setFlowSizePerFlowID(TreeMap<Integer, Integer> flowSizePerFlowID) {
		this.flowSizePerFlowID = flowSizePerFlowID;
	}
	
	

}
