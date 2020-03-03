package nrg.sdnsimulator.core.utility.excel.datastructure;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.math3.util.Pair;

public class CategoryFactorBarTableData {

	private int ColumnHeaderRowIndex = 1;
	private LinkedHashMap<String, ArrayList<Pair<String, Float>>> data; // <SeriesName,
	private int FirstDataRowIndex = 2;
	private Float maxYValue;
	private int SeriesTitleRowIndex = 0;
	private String xAxisColTitle;
	private String yAxisColTitle;

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

	public int getColumnHeaderRowIndex() {
		return ColumnHeaderRowIndex;
	}

	public void setColumnHeaderRowIndex(int columnHeaderRowIndex) {
		ColumnHeaderRowIndex = columnHeaderRowIndex;
	}

	public LinkedHashMap<String, ArrayList<Pair<String, Float>>> getData() {
		return data;
	}

	public void setData(LinkedHashMap<String, ArrayList<Pair<String, Float>>> data) {
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

	public void setMaxYValue(Float maxYValue) {
		this.maxYValue = maxYValue;
	}

}
