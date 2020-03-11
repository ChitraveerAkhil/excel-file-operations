package com.chitraveerakhil.fileOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class DuplicatesRemover {

	// get the file and coloumn-heading, identifier or coloumn-header as
	// parameters

	public String removeDuplicates(String columnHeading, InputStream excelFile, FormDataContentDisposition fileDetail,
			String sheetLocation) throws IOException, EncryptedDocumentException, InvalidFormatException {
		Workbook workbook;
		Sheet datatypeSheet = null;
		workbook = WorkbookFactory.create(excelFile);
		datatypeSheet = workbook.getSheetAt(Integer.parseInt(sheetLocation));
		// TODO Auto-generated catch block
		Iterator<Row> iterator = datatypeSheet.iterator();
		Set<String> values = getValuesInTheColumn(columnHeading, iterator);
		// Set<String> duplicatesRemoved = new LinkedHashSet<>(values);
		// removeDuplicate(values);
		String filePath = createOutputStream(values, fileDetail.getFileName());

		return filePath;
	}

	public Response retriveFile(String filePath) {
		String[] array = filePath.split("/");
		String fileName = array[array.length - 1];
		String[] exactName = fileName.split("_");

		File file = new File(filePath);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition",
				"attachment; filename=duplicatesRemoved_" + exactName[exactName.length - 1]);
		return response.build();
	}

	private String createOutputStream(Set<String> values, String fileName) throws IOException {
		HSSFWorkbook workbuk = new HSSFWorkbook();
		HSSFSheet dataSheet = workbuk.createSheet("duplicateRemoved");
		int rowCount = 0;
		Iterator<String> iterator = values.iterator();
		while (iterator.hasNext()) {
			int columnCount = 0;
			Row row = dataSheet.createRow(rowCount++);
			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(iterator.next());

		}
		String filePath = "./target/generated/excel/";
		Date date = new Date();
		filePath += date.getTime() + "_" + fileName;

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(new File(filePath));

		} catch (FileNotFoundException e) {
			Path path = Paths.get(filePath);
			Files.createDirectories(path.getParent());

			Path file = Files.createFile(path);
			outputStream = new FileOutputStream(file.toString());
		} finally {
			workbuk.write(outputStream);
			workbuk.close();
			outputStream.close();
		}
		return filePath;
	}

	private Set<String> getValuesInTheColumn(String columnHeading, Iterator<Row> iterator) {
		// List<String> values = new ArrayList<>();
		Set<String> values = new LinkedHashSet<>();
		int colunmnIdx = getColumnIndex(columnHeading, iterator);
		while (iterator.hasNext()) {

			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();
			while (cellIterator.hasNext()) {

				Cell currentCell = cellIterator.next();
				if (currentCell.getColumnIndex() == colunmnIdx) {
					values.add(currentCell.getStringCellValue());
				}
			}
		}
		return values;
	}

	private int getColumnIndex(String columnHeading, Iterator<Row> iterator) {
		int columnIdx = 0;
		while (iterator.hasNext()) {
			Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();
			while (cellIterator.hasNext()) {

				Cell currentCell = cellIterator.next();
				if (currentCell.getCellTypeEnum() == CellType.STRING) {
					if (columnHeading.equals(currentCell.getStringCellValue())) {
						columnIdx = currentCell.getColumnIndex();
						return columnIdx;
					}
				}
			}
		}
		return columnIdx;
	}

}