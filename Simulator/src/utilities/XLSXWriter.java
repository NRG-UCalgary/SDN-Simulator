package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXWriter {

	private XSSFWorkbook workbook;

	/** Constructor **/
	public XLSXWriter() {
		// TODO Auto-generated constructor stub
	}

	/** Main function for testing **/
	public static void main(String... strings) throws IOException {

		// Create an array with the data in the same order in which you expect to be
		// filled in excel file
		String[] data = { "Mr. E", "Noida" };

		// Create an object of current class
		XLSXWriter writer = new XLSXWriter();

		writer.writeExcel(null, null, "sheet-1", data);
		// Write the file using file name, sheet name and the data to be filled
		// System.getProperty("user.dir") +

		Workbook w = new XSSFWorkbook();
		Sheet s = w.createSheet("test");
		Font headerFont = w.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 17);
		headerFont.setColor(IndexedColors.RED.getIndex());

		CellStyle headerCellStyle = w.createCellStyle();
		headerCellStyle.setFont(headerFont);

		Row hRow = s.createRow(0);

		Cell c = hRow.createCell(0);
		c.setCellValue("Salam");
		c.setCellStyle(headerCellStyle);

		FileOutputStream out = new FileOutputStream("test.xlsx");
		w.write(out);
		out.close();
		w.close();

	}

	public void writeExcel(String filePath, String fileName, String sheetName, String[] dataToWrite)
			throws IOException {

		// Create an object of File class to open xlsx file

		/** For reading and writing in a existing file **/
		/* File file = new File(filePath + "/" + fileName); */

		// Create an object of FileInputStream class to read excel file

		/** For reading and writing in a existing file **/
		/* FileInputStream inputStream = new FileInputStream(file); */

		// Create object of XSSFWorkbook class
		workbook = new XSSFWorkbook();

		/* Creating new sheet */
		Sheet s1 = workbook.createSheet(sheetName);

		/* Creating new row */
		Row headers = s1.createRow(0);
		Cell h1 = headers.createCell(0);
		h1.setCellValue("Numbers");

		for (int i = 1; i <= 10; i++) {
			Row r1 = s1.createRow(i);
			Cell c1 = r1.createCell(0);
			c1.setCellValue(i);
		}

		// Create an object of FileOutputStream class to create write data in excel file
		FileOutputStream outputStream = new FileOutputStream("test/t.xlsx");

		// write data in the excel file
		workbook.write(outputStream);

		// close output stream
		outputStream.close();
		workbook.close();

	}

}
