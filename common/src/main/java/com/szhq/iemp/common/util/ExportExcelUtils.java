package com.szhq.iemp.common.util;

import com.szhq.iemp.common.vo.ExportExcelData;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

public class ExportExcelUtils {

	/**
	 * 使用浏览器选择路径下载
	 */
	public static void exportExcel(HttpServletResponse response, String fileName, ExportExcelData data) throws Exception {
		// 告诉浏览器用什么软件可以打开此文件
		response.setHeader("content-Type", "application/vnd.ms-excel");
		// 下载文件的默认名称
		response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(fileName, "utf-8"));
		response.setCharacterEncoding("utf-8");
		exportExcel(data, response.getOutputStream());
	}

	public static void exportExcel(ExportExcelData data, OutputStream out) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			String sheetName = data.getName();
			if (null == sheetName) {
				sheetName = "Sheet1";
			}
			HSSFSheet sheet = wb.createSheet(sheetName);
			writeExcel(wb, sheet, data);
			wb.write(out);
		} finally {
			wb.close();
		}
	}

	/**
	 * 表显示字段
	 */
	private static void writeExcel(HSSFWorkbook wb, Sheet sheet, ExportExcelData data) {
		int rowIndex = 0;
		rowIndex = writeTitlesToExcel(wb, sheet, data.getTitles());
		writeRowsToExcel(wb, sheet, data.getRows(), rowIndex);
		autoSizeColumns(sheet, data.getTitles().size() + 1);
	}
	
	/**
	 * 设置表头
	 */
	private static int writeTitlesToExcel(HSSFWorkbook wb, Sheet sheet, List<String> titles) {
		int rowIndex = 0;
		int colIndex = 0;

		Font titleFont = wb.createFont();
		titleFont.setFontName("simsun");
		titleFont.setBold(true);
		titleFont.setFontHeightInPoints((short) 14);
		titleFont.setColor(IndexedColors.BLACK.index);
		HSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setFont(titleFont);
		Row titleRow = sheet.createRow(rowIndex);
		colIndex = 0;

		for (String field : titles) {
			Cell cell = titleRow.createCell(colIndex);
			cell.setCellValue(field);
			cell.setCellStyle(titleStyle);
			colIndex++;
		}

		rowIndex++;
		return rowIndex;
	}

	/**
	 *  设置内容
	 */
	@SuppressWarnings("deprecation")
	private static int writeRowsToExcel(HSSFWorkbook wb, Sheet sheet, List<List<String>> rows, int rowIndex) {
		int colIndex = 0;
		Font dataFont = wb.createFont();
		dataFont.setFontName("simsun");
		dataFont.setColor(IndexedColors.BLACK.index);
		HSSFCellStyle dataStyle = wb.createCellStyle();
		dataStyle.setFont(dataFont);
		setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new Color(0, 0, 0)));
		if(rows != null && rows.size() > 0) {
			for (List<String> rowData : rows) {
				Row dataRow = sheet.createRow(rowIndex);
				colIndex = 0;
				for (Object cellData : rowData) {
					Cell cell = dataRow.createCell(colIndex);
					if (cellData != null) {
						cell.setCellValue(cellData.toString());
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(dataStyle);
					colIndex++;
				}
				rowIndex++;
			}
		}
		return rowIndex;
	}
	/**
	 * 自动调整列宽
	 */
	private static void autoSizeColumns(Sheet sheet, int columnNumber) {

		for (int i = 0; i < columnNumber; i++) {
			int orgWidth = sheet.getColumnWidth(i);
			sheet.autoSizeColumn(i, true);
			int newWidth = (int) (sheet.getColumnWidth(i) + 100);
			if (newWidth > orgWidth) {
				sheet.setColumnWidth(i, newWidth);
			} else {
				sheet.setColumnWidth(i, orgWidth);
			}
		}
	}
	
	/**
	 * 设置边框
	 */
	private static void setBorder(HSSFCellStyle style, BorderStyle border, XSSFColor color) {
		style.setBorderTop(border);
		style.setBorderLeft(border);
		style.setBorderRight(border);
		style.setBorderBottom(border);
		style.setTopBorderColor(Font.COLOR_NORMAL);
		style.setBottomBorderColor(Font.COLOR_NORMAL);
		style.setRightBorderColor(Font.COLOR_NORMAL);
		style.setLeftBorderColor(Font.COLOR_NORMAL);
	}
}
