package system.utility.dataStructures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

public class ScatterTableData {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ColumnHeaderRowIndex;
		result = prime * result + FirstDataRowIndex;
		result = prime * result + SeriesTitleRowIndex;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((maxXValue == null) ? 0 : maxXValue.hashCode());
		result = prime * result + ((maxYValue == null) ? 0 : maxYValue.hashCode());
		result = prime * result + ((xAxisColTitle == null) ? 0 : xAxisColTitle.hashCode());
		result = prime * result + ((yAxisColTitle == null) ? 0 : yAxisColTitle.hashCode());
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
		ScatterTableData other = (ScatterTableData) obj;
		if (ColumnHeaderRowIndex != other.ColumnHeaderRowIndex)
			return false;
		if (FirstDataRowIndex != other.FirstDataRowIndex)
			return false;
		if (SeriesTitleRowIndex != other.SeriesTitleRowIndex)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (maxXValue == null) {
			if (other.maxXValue != null)
				return false;
		} else if (!maxXValue.equals(other.maxXValue))
			return false;
		if (maxYValue == null) {
			if (other.maxYValue != null)
				return false;
		} else if (!maxYValue.equals(other.maxYValue))
			return false;
		if (xAxisColTitle == null) {
			if (other.xAxisColTitle != null)
				return false;
		} else if (!xAxisColTitle.equals(other.xAxisColTitle))
			return false;
		if (yAxisColTitle == null) {
			if (other.yAxisColTitle != null)
				return false;
		} else if (!yAxisColTitle.equals(other.yAxisColTitle))
			return false;
		return true;
	}

	private Float maxXValue;
	private Float maxYValue;

	public int SeriesTitleRowIndex = 0;
	public int ColumnHeaderRowIndex = 1;
	public int FirstDataRowIndex = 2;

	public String yAxisColTitle;
	public String xAxisColTitle;

	public LinkedHashMap<String, ArrayList<Pair<Float, Float>>> data; // <SeriesName, Series<factor, metric>>

	public ScatterTableData(String xAxisTitle, String yAxisTitle) {
		this.xAxisColTitle = xAxisTitle;
		this.yAxisColTitle = yAxisTitle;
		data = new LinkedHashMap<String, ArrayList<Pair<Float, Float>>>();

	}

	public void addSeriesToTable(String seriesName, TreeMap<Float, Float> values) {
		ArrayList<Pair<Float, Float>> seriesValues = new ArrayList<Pair<Float, Float>>();
		for (Float factor : values.keySet()) {
			seriesValues.add(new Pair<Float, Float>(factor, values.get(factor)));
		}
		data.put(seriesName, seriesValues);
	}

	public int getLastColIndex() {
		return (data.size() * 2);
	}

	public int getLastRowIndexOfSeriesData(String seriesTitle) {
		return data.get(seriesTitle).size() + FirstDataRowIndex - 1;
	}

	public int getFloatOfSeries() {
		return data.size();
	}

	public Float getMaxXValue() {
		maxXValue = Float.MIN_VALUE;
		for (ArrayList<Pair<Float, Float>> entryList : data.values()) {
			for (Pair<Float, Float> entryPair : entryList) {
				if (entryPair.getFirst() >= maxXValue) {
					maxXValue = (float) Math.ceil(entryPair.getFirst());
				}
			}
		}
		return this.maxXValue;
	}

	public Float getMaxYValue() {
		maxYValue = Float.MIN_VALUE;
		for (ArrayList<Pair<Float, Float>> entryList : data.values()) {
			for (Pair<Float, Float> entryPair : entryList) {
				if (entryPair.getSecond() >= maxXValue) {
					maxYValue = entryPair.getSecond();
				}
			}
		}
		return this.maxYValue;
	}

}
