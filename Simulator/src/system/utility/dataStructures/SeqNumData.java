package system.utility.dataStructures;

import java.util.TreeMap;

public class SeqNumData {

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ackNumbers == null) ? 0 : ackNumbers.hashCode());
		result = prime * result + ((flowName == null) ? 0 : flowName.hashCode());
		result = prime * result + ((seqNumbers == null) ? 0 : seqNumbers.hashCode());
		return result;
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

	public String flowName;
	public TreeMap<Integer, Double> seqNumbers;
	public TreeMap<Integer, Double> ackNumbers;

	public SeqNumData() {
	}

}
