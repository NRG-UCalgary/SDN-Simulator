package system.utility.dataStructures;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

public class ScatterTableData {

	public int SeriesTitleRowIndex = 0;
	public int ColumnHeaderRowIndex = 1;
	public int FirstDataRowIndex = 2;

	public String yAxisColTitle;
	public String xAxisColTitle;

	public int lastColIndex;

	public TreeMap<String, ArrayList<Pair<Double, Integer>>> data; // <SeriesName, Series<time, seqNum>>

	public ScatterTableData(String xAxisTitle, String yAxisTitle) {
		this.xAxisColTitle = xAxisTitle;
		this.yAxisColTitle = yAxisTitle;
		data = new TreeMap<String, ArrayList<Pair<Double, Integer>>>();
		// TODO Auto-generated constructor stub
	}

	public int getLastRowIndexOfSeriesData(String seriesTitle) {
		return data.get(seriesTitle).size() + FirstDataRowIndex;
	}

}
