package system.utility.dataStructures;

import java.util.HashMap;
import java.util.Map;

public class OneToOneMap {

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
