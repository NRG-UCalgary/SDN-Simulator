package system.utility.dataStructures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.commons.math3.util.Pair;

public class ScatterTableData {

	private int maxXValue;
	private int maxYValue;

	public int SeriesTitleRowIndex = 0;
	public int ColumnHeaderRowIndex = 1;
	public int FirstDataRowIndex = 2;

	public String yAxisColTitle;
	public String xAxisColTitle;

	public LinkedHashMap<String, ArrayList<Pair<Double, Integer>>> data; // <SeriesName, Series<time, seqNum>>

	public ScatterTableData(String xAxisTitle, String yAxisTitle) {
		this.xAxisColTitle = xAxisTitle;
		this.yAxisColTitle = yAxisTitle;
		data = new LinkedHashMap<String, ArrayList<Pair<Double, Integer>>>();

	}

	public int getLastColIndex() {
		return (data.size() * 2);
	}

	public int getLastRowIndexOfSeriesData(String seriesTitle) {
		return data.get(seriesTitle).size() + FirstDataRowIndex - 1;
	}

	public int getNumberOfSeries() {
		return data.size();
	}

	public int getMaxXValue() {
		maxXValue = Integer.MIN_VALUE;
		for (ArrayList<Pair<Double, Integer>> entryList : data.values()) {
			for (Pair<Double, Integer> entryPair : entryList) {
				if (entryPair.getFirst() >= maxXValue) {
					maxXValue = (int) Math.ceil(entryPair.getFirst());
				}
			}
		}
		return this.maxXValue;
	}

	public int getMaxYValue() {
		maxYValue = Integer.MIN_VALUE;
		for (ArrayList<Pair<Double, Integer>> entryList : data.values()) {
			for (Pair<Double, Integer> entryPair : entryList) {
				if (entryPair.getSecond() >= maxXValue) {
					maxYValue = entryPair.getSecond();
				}
			}
		}
		return this.maxYValue;
	}

}
