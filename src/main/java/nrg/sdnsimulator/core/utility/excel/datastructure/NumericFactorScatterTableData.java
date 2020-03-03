package nrg.sdnsimulator.core.utility.excel.datastructure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

public class NumericFactorScatterTableData {

	private int ColumnHeaderRowIndex = 1;
	private LinkedHashMap<String, ArrayList<Pair<Float, Float>>> data; // <SeriesName,
																		// Series<factor, metric>>
	private int FirstDataRowIndex = 2;
	private Float maxXValue;
	private Float maxYValue;
	private int SeriesTitleRowIndex = 0;
	private String xAxisColTitle;
	private String yAxisColTitle;

	public NumericFactorScatterTableData(String xAxisTitle, String yAxisTitle) {
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

	public int getFloatOfSeries() {
		return data.size();
	}

	public int getLastColIndex() {
		return (data.size() * 2);
	}

	public int getLastRowIndexOfSeriesData(String seriesTitle) {
		return data.get(seriesTitle).size() + FirstDataRowIndex - 1;
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
				if (entryPair.getSecond() >= maxYValue) {
					maxYValue = entryPair.getSecond();
				}
			}
		}
		return this.maxYValue;
	}

	public int getColumnHeaderRowIndex() {
		return ColumnHeaderRowIndex;
	}

	public void setColumnHeaderRowIndex(int columnHeaderRowIndex) {
		ColumnHeaderRowIndex = columnHeaderRowIndex;
	}

	public LinkedHashMap<String, ArrayList<Pair<Float, Float>>> getData() {
		return data;
	}

	public void setData(LinkedHashMap<String, ArrayList<Pair<Float, Float>>> data) {
		this.data = data;
	}

	public int getFirstDataRowIndex() {
		return FirstDataRowIndex;
	}

	public void setFirstDataRowIndex(int firstDataRowIndex) {
		FirstDataRowIndex = firstDataRowIndex;
	}

	public int getSeriesTitleRowIndex() {
		return SeriesTitleRowIndex;
	}

	public void setSeriesTitleRowIndex(int seriesTitleRowIndex) {
		SeriesTitleRowIndex = seriesTitleRowIndex;
	}

	public String getxAxisColTitle() {
		return xAxisColTitle;
	}

	public void setxAxisColTitle(String xAxisColTitle) {
		this.xAxisColTitle = xAxisColTitle;
	}

	public String getyAxisColTitle() {
		return yAxisColTitle;
	}

	public void setyAxisColTitle(String yAxisColTitle) {
		this.yAxisColTitle = yAxisColTitle;
	}

	public void setMaxXValue(Float maxXValue) {
		this.maxXValue = maxXValue;
	}

	public void setMaxYValue(Float maxYValue) {
		this.maxYValue = maxYValue;
	}

}
