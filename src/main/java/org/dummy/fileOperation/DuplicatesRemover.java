package org.dummy.fileOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	// get the file and coloumn-heading, identifier or coloumn-header as parameters

	public String removeDuplicates(String columnHeading, FileInputStream excelFile,
			FormDataContentDisposition fileDetail)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(excelFile);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();
		Set<String> values = getValuesInTheColumn(columnHeading, iterator);
		// Set<String> duplicatesRemoved = new LinkedHashSet<>(values);
		// removeDuplicate(values);
		String filePath = createOutputStream(values, fileDetail.getFileName());

		return filePath;
	}

	public Response retriveFile(String filePath) {
		String[] array = filePath.split("/");
		String fileName = array[array.length];
		String[] exactName = fileName.split("_");
		File file = new File(filePath);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=" + exactName[0] + "_duplicatesRemoved");
		return response.build();
	}

	// save uploaded file to new location
	void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private String createOutputStream(Set<String> values, String fileName) throws FileNotFoundException, IOException {
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
		String filePath = "/home/akhil/";
		Date date = new Date();
		filePath += fileName;
		filePath += "_" + date;
		File duplicateRemoved = new File(filePath);
		FileOutputStream outputStream = new FileOutputStream(duplicateRemoved);
		// FileOutputStream outputStream = new FileOutputStream(new
		// File("/home/e0000081/sample1.xls"));
		workbuk.write(outputStream);
		workbuk.close();
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
	/*
	 * public static void main(String[] args) throws FileNotFoundException { //
	 * String FILE_NAME = "/home/e0000081/Downloads/firefox/SampleXLSFile_38kb.xls";
	 * String FILE_NAME = "/home/akhil/Downloads/Sample-Spreadsheet-100-rows.xls";
	 * FileInputStream excelFile; DuplicatesRemover remover = new
	 * DuplicatesRemover(); try { excelFile = new FileInputStream(new
	 * File(FILE_NAME)); remover.removeDuplicates("Grant Carroll", excelFile);
	 * 
	 * } catch (EncryptedDocumentException | InvalidFormatException | IOException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 */

}

// String FILE_NAME = "/home/akhil/Downloads/Sample-Spreadsheet-100-rows.xls";
// FileOutputStream outputStream = new FileOutputStream(new
// File("/home/akhil/sample1.xls"));