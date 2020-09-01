package util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tableDefinition.dao.TableDefinition;

public class ExcelWriter {
	private Workbook workbook = new XSSFWorkbook();
	private String path = "C://Users//SAMSUNG//Desktop";
	private String filename = "테이블정의서" + new Date().getTime() + ".xlsx";
	private Cell cell;
	private Row row;

	/**
	 * 엑셀 작성
	 * @param workbook
	 * @return
	 */
	public boolean excelGenerator() {
		try(FileOutputStream fileOutputStream = new FileOutputStream(path + File.separator + filename);) { // AutoCloseable
			workbook.write(fileOutputStream);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 테이블 목록 시트 작성
	 * @param result
	 * @return
	 */
	public Workbook makeTableListSheet(List<TableDefinition> tableListResult) {
		List<TableDefinition> tableList = tableListResult;
		int rowCount = 0;

		Cell cell = null;
		Sheet sheet = workbook.createSheet("테이블 정의서 목록");
		sheet.setColumnWidth(0, 10000); // 너비 지정
		sheet.setColumnWidth(1, 10000);

		CellStyle firstRowCellStyle = workbook.createCellStyle();
		Row row = sheet.createRow(rowCount++);
		firstRowCellStyle = makeDefaultCenterCellStyle(firstRowCellStyle);
		firstRowCellStyle = setCellBackground(firstRowCellStyle, IndexedColors.GREY_25_PERCENT.getIndex()); // 배경 색 칠하기
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

		cell = writeStringCell(row, cell, 0, "테이블 정의서", firstRowCellStyle);
		
		CellStyle secRowCellStyle = workbook.createCellStyle();
		row = sheet.createRow(rowCount++);
		secRowCellStyle = makeDefaultCenterCellStyle(secRowCellStyle);
		secRowCellStyle = setCellBackground(secRowCellStyle, IndexedColors.GREY_25_PERCENT.getIndex()); // 배경 색 칠하기
		
		cell = writeStringCell(row, cell, 0, "테이블 ID", secRowCellStyle);

		cell = writeStringCell(row, cell, 1, "비고", secRowCellStyle);
		
		int tableListIndex = 0;
		
		CellStyle rowCellStyle = workbook.createCellStyle();
		rowCellStyle = makeDefaultCellStyle(rowCellStyle);

		while (tableListIndex < tableList.size()) {
			row = sheet.createRow(rowCount++);

			cell = writeStringCell(row, cell, 0, tableList.get(tableListIndex).getTABLE_NAME(), rowCellStyle);
			cell = setDocumentHyperlink(cell, tableList.get(tableListIndex).getTABLE_NAME()); // 링크 설정하기
			
			cell = writeStringCell(row, cell, 1, tableList.get(tableListIndex).getTABLE_NAME(), rowCellStyle);

			workbook.createSheet(tableList.get(tableListIndex).getTABLE_NAME());
			tableListIndex++;
		}

		return workbook;
	}
	
	/**
	 * 테이블 상세정보 작성
	 * @param result
	 * @return
	 */
	public Workbook makeTableInfoTab (List<TableDefinition> tableInfo) {
		Sheet sheet = workbook.getSheet(tableInfo.get(0).getTABLE_NAME());
		
		workbook = makeListInfoHeaderCellStyle(workbook, sheet, tableInfo.get(0).getTABLE_NAME());

		
		CellStyle numCellStyle = makeDefaultCellStyle(workbook.createCellStyle());
		numCellStyle = makeDefaultCenterCellStyle(numCellStyle);
		
		CellStyle defaultCellStyle = makeDefaultCellStyle(workbook.createCellStyle());
		
		CellStyle lengthCellStyle = makeDefaultCellStyle(workbook.createCellStyle());
		
		int rowCount = 3;
		int columnCount = 0;
		
		while (rowCount < tableInfo.size() + 3) {
			String[] tableInfoList = {tableInfo.get(rowCount - 3).getCOLUMN_ID(), tableInfo.get(rowCount - 3).getCOMMENTS(),
					tableInfo.get(rowCount - 3).getCOLUMN_NAME(), tableInfo.get(rowCount - 3).getDATA_TYPE(),
					tableInfo.get(rowCount - 3).getDATA_LENGTH(), tableInfo.get(rowCount - 3).getNULLABLE(),
					tableInfo.get(rowCount - 3).getCONSTRAINT_TYPE()};
			
			row = sheet.createRow(rowCount);
			
			for (int i = 0; i < tableInfoList.length; i++) {
				if (i == 0) {
					writeStringCell(row, cell, columnCount, tableInfoList[i], numCellStyle);
				} else {
					writeStringCell(row, cell, columnCount, tableInfoList[i], defaultCellStyle);
				}
				
			}
			
			writeStringCell(row, cell, columnCount, tableInfo.get(rowCount - 3).getCOLUMN_ID(), numCellStyle);
			columnCount++;

			writeStringCell(row, cell, columnCount, tableInfo.get(rowCount - 3).getCOMMENTS(), defaultCellStyle);
			columnCount++;
			
			writeStringCell(row, cell, columnCount, tableInfo.get(rowCount - 3).getCOLUMN_NAME(), defaultCellStyle);
			columnCount++;
			
			writeStringCell(row, cell, columnCount, tableInfo.get(rowCount - 3).getDATA_TYPE(), defaultCellStyle);
			columnCount++;
			
			writeNumericCell(row, cell, columnCount, Double.parseDouble(tableInfo.get(rowCount - 3).getDATA_LENGTH()), lengthCellStyle);
			columnCount++;
			
			writeStringCell(row, cell, columnCount, tableInfo.get(rowCount - 3).getNULLABLE(), defaultCellStyle);
			columnCount++;
			
			writeStringCell(row, cell, columnCount, tableInfo.get(rowCount - 3).getCONSTRAINT_TYPE(), defaultCellStyle);
			
			rowCount++;
			columnCount = 0;
		}
		
		return workbook;
	}
	
	/**
	 * 상세정보 헤더 서식
	 * @param cellStyle
	 * @return
	 */
	public Workbook makeListInfoHeaderCellStyle(Workbook workbook, Sheet sheet, String tableName) {
		CellStyle firstRowCellStyle = workbook.createCellStyle();
		row = sheet.createRow(0);
		firstRowCellStyle = makeDefaultCenterCellStyle(firstRowCellStyle);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		writeStringCell(row, cell, 0, "테의블 정의서", firstRowCellStyle);
		
		CellStyle secRowCellStyle1 = workbook.createCellStyle(); 
		row = sheet.createRow(1); 

		secRowCellStyle1 = makeDefaultCenterCellStyle(secRowCellStyle1);
		secRowCellStyle1 = setCellBackground(secRowCellStyle1, IndexedColors.GREY_25_PERCENT.getIndex()); // 배경색 설정
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));

		writeStringCell(row, cell, 0, "테이블 ID", secRowCellStyle1);
		
		CellStyle secRowCellStyle2 = workbook.createCellStyle();
		secRowCellStyle2 = makeDefaultCenterCellStyle(secRowCellStyle2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 6));

		writeStringCell(row, cell, 2, tableName, secRowCellStyle2);
		
		CellStyle thirdRowCellStyle = workbook.createCellStyle();
		row = sheet.createRow(2);
		
		thirdRowCellStyle = setCellBackground(thirdRowCellStyle, IndexedColors.LIGHT_YELLOW.getIndex()); // 배경색 설정
		thirdRowCellStyle = makeDefaultCenterCellStyle(thirdRowCellStyle);

		writeStringCell(row, cell, 0, "번호", thirdRowCellStyle);
		writeStringCell(row, cell, 1, "컬럼명(한글)", thirdRowCellStyle);
		writeStringCell(row, cell, 2, "컬럼명(영문)", thirdRowCellStyle);
		writeStringCell(row, cell, 3, "DataType", thirdRowCellStyle);
		writeStringCell(row, cell, 4, "Length", thirdRowCellStyle);
		writeStringCell(row, cell, 5, "Null", thirdRowCellStyle);
		writeStringCell(row, cell, 6, "PK", thirdRowCellStyle);
		
		return workbook;
	}
	
	/**
	 * 공통 스타일 : 얇은 검정색 테두리 그려주기 + 세로 중앙 정렬
	 * @param cellStyle
	 * @return
	 */
	public CellStyle makeDefaultCellStyle(CellStyle cellStyle) {
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		return cellStyle;
	}
	
	/**
	 * 공통 스타일 : 얇은 검정색 테두리 그려주기 + 세로 중앙 정렬 + 가로 중앙 정렬
	 * @param cellStyle
	 * @return
	 */
	public CellStyle makeDefaultCenterCellStyle(CellStyle cellStyle) {
		/*
		 * cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		 * cellStyle.setBorderBottom(BorderStyle.THIN);
		 * cellStyle.setBorderTop(BorderStyle.THIN);
		 * cellStyle.setBorderLeft(BorderStyle.THIN);
		 * cellStyle.setBorderRight(BorderStyle.THIN);
		 */
		cellStyle = makeDefaultCellStyle(cellStyle);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		return cellStyle;
	}
	
	/**
	 * 셀 배경색 넣기
	 * @param cellStyle
	 * @param color
	 * @return
	 */
	public CellStyle setCellBackground(CellStyle cellStyle, short color) {
		cellStyle.setFillForegroundColor(color);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}
	
	/**
	 * 스타일이 있는 문자 셀 작성하기
	 * @return
	 */
	public Cell writeStringCell(Row row, Cell cell, int columnCount, String cellValue, CellStyle cellStyle) {
		cell = row.createCell(columnCount);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(cellValue);
		
		return cell;
	}
	
	/**
	 * 스타일이 없는 문자 셀 작성하기
	 * @return
	 */
	public Cell writeStringCell(Row row, Cell cell, int columnCount, String cellValue) {
		cell = row.createCell(columnCount);
		cell.setCellValue(cellValue);
		
		return cell;
	}
	
	/**
	 * 스타일이 있는 숫자 셀 작성하기
	 * @return
	 */
	public Cell writeNumericCell(Row row, Cell cell, int columnCount, double cellValue, CellStyle cellStyle) {
		cell = row.createCell(columnCount);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(cellValue);
		
		return cell;
	}
	
	/**
	 * 스타일이 없는 숫자 셀 작성하기
	 * @return
	 */
	public Cell writeNumericCell(Row row, Cell cell, int columnCount, double cellValue) {
		cell = row.createCell(columnCount);
		cell.setCellValue(cellValue);
		
		return cell;
	}
	
	/**
	 * 엑셀 문서내에서 hyperlink 만들기
	 * @param cell
	 * @param link
	 * @return
	 */
	public Cell setDocumentHyperlink(Cell cell, String link) {
		XSSFHyperlink hyperlink = (XSSFHyperlink) workbook.getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
		
		hyperlink.setAddress(link + "!A1");
		hyperlink.setLabel(link + "!A1");
		
		cell.setHyperlink(hyperlink);

		return cell;
	}
}
