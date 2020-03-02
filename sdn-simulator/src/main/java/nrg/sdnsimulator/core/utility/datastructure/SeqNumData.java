package nrg.sdnsimulator.core.utility.datastructure;

import java.util.TreeMap;

public class SeqNumData {

	private TreeMap<Integer, Double> ackNumbers;
	private String flowName;
	private TreeMap<Integer, Double> seqNumbers;

	public SeqNumData() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeqNumData other = (SeqNumData) obj;
		if (ackNumbers == null) {
			if (other.ackNumbers != null)
				return false;
		} else if (!ackNumbers.equals(other.ackNumbers))
			return false;
		if (flowName == null) {
			if (other.flowName != null)
				return false;
		} else if (!flowName.equals(other.flowName))
			return false;
		if (seqNumbers == null) {
			if (other.seqNumbers != null)
				return false;
		} else if (!seqNumbers.equals(other.seqNumbers))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ackNumbers == null) ? 0 : ackNumbers.hashCode());
		result = prime * result + ((flowName == null) ? 0 : flowName.hashCode());
		result = prime * result + ((seqNumbers == null) ? 0 : seqNumbers.hashCode());
		return result;
	}

	public TreeMap<Integer, Double> getAckNumbers() {
		return ackNumbers;
	}

	public void setAckNumbers(TreeMap<Integer, Double> ackNumbers) {
		this.ackNumbers = ackNumbers;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public TreeMap<Integer, Double> getSeqNumbers() {
		return seqNumbers;
	}

	public void setSeqNumbers(TreeMap<Integer, Double> seqNumbers) {
		this.seqNumbers = seqNumbers;
	}

}
