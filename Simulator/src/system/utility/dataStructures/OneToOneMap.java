package system.utility.dataStructures;

import java.util.HashMap;
import java.util.Map;

public class OneToOneMap {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyToValue == null) ? 0 : keyToValue.hashCode());
		result = prime * result + ((valueToKey == null) ? 0 : valueToKey.hashCode());
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
		OneToOneMap other = (OneToOneMap) obj;
		if (keyToValue == null) {
			if (other.keyToValue != null)
				return false;
		} else if (!keyToValue.equals(other.keyToValue))
			return false;
		if (valueToKey == null) {
			if (other.valueToKey != null)
				return false;
		} else if (!valueToKey.equals(other.valueToKey))
			return false;
		return true;
	}

	private Map<Integer, String> keyToValue;
	private Map<String, Integer> valueToKey;

	public OneToOneMap() {
		keyToValue = new HashMap<Integer, String>();
		valueToKey = new HashMap<String, Integer>();
	}

	public String getValue(int key) {
		return this.keyToValue.get(key);
	}

	public Integer getKey(String value) {
		return this.valueToKey.get(value);
	}

	public void put(int key, String value) {
		this.keyToValue.put(key, value);
		this.valueToKey.put(value, key);
	}

	// Other method may be needed to be implemented

}
