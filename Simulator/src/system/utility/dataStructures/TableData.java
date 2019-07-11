package system.utility.dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

// Note that each two columns of data should be added to the table right after each other
public class TableData {

	public int lastColIndex;
	public int lastRowIndex;

	

	public TreeMap<Integer, String> headers; // <Index, header>
	public TreeMap<Integer, ArrayList<Double>> data; // <Index, List<data>>

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

}
