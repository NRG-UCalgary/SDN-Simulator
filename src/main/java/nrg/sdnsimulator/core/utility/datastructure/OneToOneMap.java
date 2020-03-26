package nrg.sdnsimulator.core.utility.datastructure;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OneToOneMap {

	private Map<Integer, String> keyToValue;
	private Map<String, Integer> valueToKey;

	public OneToOneMap() {
		keyToValue = new HashMap<Integer, String>();
		valueToKey = new HashMap<String, Integer>();
	}

	public Integer getID(String value) {
		return this.valueToKey.get(value);
	}

	public boolean hasLabel(String label) {
		if (valueToKey.containsKey(label)) {
			return true;
		} else {
			return false;
		}
	}

	public String getLabel(int key) {
		return this.keyToValue.get(key);
	}

	public void put(int key, String value) {
		this.keyToValue.put(key, value);
		this.valueToKey.put(value, key);
	}

}
