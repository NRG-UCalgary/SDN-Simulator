package nrg.sdnsimulator.core.utility.excel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrossBetween;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.ScatterStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFScatterChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import nrg.sdnsimulator.core.utility.excel.datastructure.CategoryFactorBarTableData;
import nrg.sdnsimulator.core.utility.excel.datastructure.NumericFactorScatterTableData;

public class ChartPlotter {

	private static int chartHeight = 25;
	private static int chartWidth = 25;
	private static int FirstChartRowIndex = 1;
	private static int TableChartGap = 2;

	public ChartPlotter() {
	}

	public static XSSFSheet plotCategoryBarChart(XSSFSheet sheet, String chartTitle,
			CategoryFactorBarTableData table) {
		XSSFDrawing drawer = sheet.createDrawingPatriarch();
		// createAnchor(-,-,-,-,topLeftCol, topLeftRow, botRightCol, botRightRow)
		XSSFClientAnchor anchor = drawer.createAnchor(0, 0, 0, 0,
				(table.getLastColIndex() + TableChartGap), FirstChartRowIndex,
				(table.getLastColIndex() + chartWidth), (FirstChartRowIndex + chartHeight));
		XDDFChart chart = drawer.createChart(anchor);
		chart.setTitleText(chartTitle);
		chart.setTitleOverlay(false);
		XDDFChartLegend legend = chart.getOrAddLegend();
		legend.setPosition(LegendPosition.TOP_RIGHT);

		XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
		xAxis.setTitle(table.getxAxisColTitle());
		// xAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.LEFT);
		yAxis.setTitle(table.getyAxisColTitle());
		yAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		yAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
		XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, xAxis, yAxis);
		data.setBarDirection(BarDirection.COL);

		int xDataColIndex = 0;
		int yDataColIndex = xDataColIndex + 1;
		// CellRangeAddress(firstRow, lastRow, firstCol, LastCol)
		for (String seriesTitle : table.getData().keySet()) {
			XDDFCategoryDataSource xData = XDDFDataSourcesFactory.fromStringCellRange(sheet,
					new CellRangeAddress(table.getFirstDataRowIndex(),
							table.getLastRowIndexOfSeriesData(seriesTitle), xDataColIndex,
							xDataColIndex));
			XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory.fromNumericCellRange(
					sheet,
					new CellRangeAddress(table.getFirstDataRowIndex(),
							table.getLastRowIndexOfSeriesData(seriesTitle), yDataColIndex,
							yDataColIndex));
			XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(xData, yData);
			series.setTitle(seriesTitle, null);
			xDataColIndex += 2;
			yDataColIndex = xDataColIndex + 1;
		}
		chart.plot(data);
		return sheet;
	}

	public static XSSFSheet plotNumericalScatterChart(XSSFSheet sheet, String chartTitle,
			NumericFactorScatterTableData table) {
		XSSFDrawing drawer = sheet.createDrawingPatriarch();
		// createAnchor(-,-,-,-,topLeftCol, topLeftRow, botRightCol, botRightRow)
		XSSFClientAnchor anchor = drawer.createAnchor(0, 0, 0, 0,
				(table.getLastColIndex() + TableChartGap), FirstChartRowIndex,
				(table.getLastColIndex() + chartWidth), (FirstChartRowIndex + chartHeight));
		XDDFChart chart = drawer.createChart(anchor);
		// chart.setTitleText(chartTitle);
		chart.setTitleOverlay(false);
		XDDFChartLegend legend = chart.getOrAddLegend();
		legend.setPosition(LegendPosition.TOP_RIGHT);

		XDDFValueAxis xAxis = chart.createValueAxis(AxisPosition.BOTTOM);
		xAxis.setTitle(table.getxAxisColTitle());
		xAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.LEFT);
		yAxis.setTitle(table.getyAxisColTitle());
		yAxis.setCrosses(AxisCrosses.AUTO_ZERO);
		XDDFScatterChartData data = (XDDFScatterChartData) chart.createData(ChartTypes.SCATTER,
				xAxis, yAxis);
		data.setStyle(ScatterStyle.MARKER);

		int xDataColIndex = 0;
		int yDataColIndex = xDataColIndex + 1;
		// CellRangeAddress(firstRow, lastRow, firstCol, LastCol)
		for (String seriesTitle : table.getData().keySet()) {
			XDDFNumericalDataSource<Double> xData = XDDFDataSourcesFactory.fromNumericCellRange(
					sheet,
					new CellRangeAddress(table.getFirstDataRowIndex(),
							table.getLastRowIndexOfSeriesData(seriesTitle), xDataColIndex,
							xDataColIndex));
			XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory.fromNumericCellRange(
					sheet,
					new CellRangeAddress(table.getFirstDataRowIndex(),
							table.getLastRowIndexOfSeriesData(seriesTitle), yDataColIndex,
							yDataColIndex));
			XDDFScatterChartData.Series series = (XDDFScatterChartData.Series) data.addSeries(xData,
					yData);
			series.setTitle(seriesTitle, null);
			series.setSmooth(false);
			setMarkerStyle(series, seriesTitle);
			xDataColIndex += 2;
			yDataColIndex = xDataColIndex + 1;
		}
		chart.plot(data);
		return sheet;
	}

	private static void setMarkerStyle(XDDFScatterChartData.Series series, String seriesTitle) {

		switch (seriesTitle) {
		case "Data Segments":
			series.setMarkerSize((short) 4);
			series.setMarkerStyle(MarkerStyle.CIRCLE);
			break;
		case "ACKs":
			series.setMarkerSize((short) 5);
			series.setMarkerStyle(MarkerStyle.X);
			break;
		default:
			series.setMarkerSize((short) 7);
			series.setMarkerStyle(MarkerStyle.CIRCLE);
			break;
		}
	}

}
