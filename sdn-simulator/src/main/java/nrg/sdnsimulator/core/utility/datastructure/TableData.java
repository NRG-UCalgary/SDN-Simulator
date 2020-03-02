package nrg.sdnsimulator.core.utility.datastructure;

import java.util.ArrayList;
import java.util.TreeMap;

public class TableData {

	private TreeMap<Integer, ArrayList<Double>> data; // <Index, List<data>>
	private TreeMap<Integer, String> headers; // <Index, header>
	private int lastColIndex;
	private int lastRowIndex;

	public TableData(TreeMap<Integer, String> headers, TreeMap<Integer, ArrayList<Double>> data) {
		this.headers = headers;
		this.data = data;
		lastColIndex = headers.size() - 1;
		lastRowIndex = Integer.MIN_VALUE;
		for (ArrayList<Double> dataList : data.values()) {
			if (dataList.size() > lastRowIndex) {
				lastRowIndex = dataList.size();
			}
		}

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableData other = (TableData) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (lastColIndex != other.lastColIndex)
			return false;
		if (lastRowIndex != other.lastRowIndex)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + lastColIndex;
		result = prime * result + lastRowIndex;
		return result;
	}

	public TreeMap<Integer, ArrayList<Double>> getData() {
		return data;
	}

	public void setData(TreeMap<Integer, ArrayList<Double>> data) {
		this.data = data;
	}

	public TreeMap<Integer, String> getHeaders() {
		return headers;
	}

	public void setHeaders(TreeMap<Integer, String> headers) {
		this.headers = headers;
	}

	public int getLastColIndex() {
		return lastColIndex;
	}

	public void setLastColIndex(int lastColIndex) {
		this.lastColIndex = lastColIndex;
	}

	public int getLastRowIndex() {
		return lastRowIndex;
	}

	public void setLastRowIndex(int lastRowIndex) {
		this.lastRowIndex = lastRowIndex;
	}
	
	

}
