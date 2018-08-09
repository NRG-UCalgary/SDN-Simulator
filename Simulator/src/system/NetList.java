package system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Integer> mapping = new HashMap<String, Integer>();

	public NetList() {
	}
	

	public E getByLabel(String Label) {
		E e = this.get(mapping.get(Label));
		return e;
	}

	public void addElement(String label, E e) {

		mapping.put(label, this.size());
		this.add(e);
	}

	public void setElement(String label, int index, E e) {
		mapping.put(label, index);
		this.set(index, e);
	}

	public void removeElement(String label, int index) {
		this.remove(index);
		mapping.remove(label, index);
	}

}
