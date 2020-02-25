package com.nrg.sdnsimulator.core.utility.excel.datastructure;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.math3.util.Pair;

public class CategoryFactorBarTableData {
	public int ColumnHeaderRowIndex = 1;

	public LinkedHashMap<String, ArrayList<Pair<String, Float>>> data; // <SeriesName, Series<factor, metric>>
	public int FirstDataRowIndex = 2;
	private Float maxYValue;

	public int SeriesTitleRowIndex = 0;
	public String xAxisColTitle;

	public String yAxisColTitle;

	public CategoryFactorBarTableData(String xAxisTitle, String yAxisTitle) {
		this.xAxisColTitle = xAxisTitle;
		this.yAxisColTitle = yAxisTitle;
		data = new LinkedHashMap<String, ArrayList<Pair<String, Float>>>();

	}

	public void addSeriesToTable(String seriesName, LinkedHashMap<String, Float> values) {
		ArrayList<Pair<String, Float>> seriesValues = new ArrayList<Pair<String, Float>>();
		for (String factor : values.keySet()) {
			seriesValues.add(new Pair<String, Float>(factor, values.get(factor)));
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

	public Float getMaxYValue() {
		maxYValue = Float.MIN_VALUE;
		for (ArrayList<Pair<String, Float>> entryList : data.values()) {
			for (Pair<String, Float> entryPair : entryList) {
				if (entryPair.getSecond() >= maxYValue) {
					maxYValue = entryPair.getSecond();
				}
			}
		}
		return this.maxYValue;
	}

}
