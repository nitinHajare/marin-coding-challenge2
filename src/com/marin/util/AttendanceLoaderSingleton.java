package com.marin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.marin.common.GlobalConstants;
import com.marin.model.Sansad;

public class AttendanceLoaderSingleton {

	// Logger to log messages
	private Logger logger = Logger.getLogger("AttendanceLoaderSingleton");

	// collection will hold all members where key would be division / seat no.
	private Map<Integer, Sansad> sansads = new HashMap<>();

	public Map<Integer, Sansad> getAttendance() {
		return sansads;
	}

	private volatile static AttendanceLoaderSingleton instance;

	private AttendanceLoaderSingleton() {
		// Suppressing creating a new instances
		loadAttendance();
		logger.info("Inside private contructor of AttendanceLoaderSingleton");
	}

	public static AttendanceLoaderSingleton getInstance() {
		if (instance == null) {
			synchronized (AttendanceLoaderSingleton.class) {
				if (instance == null) {
					instance = new AttendanceLoaderSingleton();
				}
			}
		}
		return instance;
	}

	private void loadAttendance() {
		try {
			Sansad sansad;
			String folderPath = GlobalConstants.SPREADSHEET_DATA_FOLDER_PATH;
			logger.info("Spreadsheet Data Folder Path: " + folderPath);
			File folder = new File(folderPath);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					logger.info("Filename: " + listOfFiles[i].getName());
					FileInputStream file = new FileInputStream(new File(
							folderPath + "/" + listOfFiles[i].getName()));

					// Get the workbook instance for XLS file
					HSSFWorkbook workbook = new HSSFWorkbook(file);

					// Get first sheet from the workbook
					HSSFSheet sheet = workbook.getSheetAt(0);

					// Get iterator to all the rows in current sheet
					Iterator<Row> rowIterator = sheet.iterator();

					// Iterate through each rows from first sheet
					while (rowIterator.hasNext()) {
						int division = 0;
						Sansad member = null;
						sansad = new Sansad();
						Row row = rowIterator.next();
						if (row.getRowNum() == 0) {
						}
						// skip first row as it is a header information
						else {
							// Get iterator to all cells of current row
							Iterator<Cell> cellIterator = row.cellIterator();
							while (cellIterator.hasNext()) {

								Cell cell = cellIterator.next();
								int columnIndex = cell.getColumnIndex();
								if (GlobalConstants.DIVISION_COLUMN_INDEX == columnIndex) {
									division = (int) cell.getNumericCellValue();
									member = sansads.get(division);
								}
								if (member == null) {
									switch (columnIndex) {
									case GlobalConstants.SNO_COLUMN_INDEX:
										sansad.setSno((int) cell
												.getNumericCellValue());
										break;
									case GlobalConstants.DIVISION_COLUMN_INDEX:
										sansad.setDivision((int) cell
												.getNumericCellValue());
										break;
									case GlobalConstants.MEMBER_NAME_COLUMN_INDEX:
										sansad.setMemberName(cell
												.getStringCellValue().trim());
										break;
									case GlobalConstants.LOKSABHA_COLUMN_INDEX:
										sansad.setLoksabha((int) cell
												.getNumericCellValue());
										break;
									case GlobalConstants.SESSION_COLUMN_INDEX:
										sansad.setSession((int) cell
												.getNumericCellValue());
										break;
									case GlobalConstants.STATE_COLUMN_INDEX:
										sansad.setState(cell
												.getStringCellValue().trim());
										break;
									case GlobalConstants.CONSTITUENCY_COLUMN_INDEX:
										sansad.setConstituency(cell
												.getStringCellValue().trim());
										break;
									case GlobalConstants.TOTAL_SITTINGS_COLUMN_INDEX:
										sansad.setTotalSittings((int) cell
												.getNumericCellValue());
										break;
									case GlobalConstants.ATTENDANCE_COLUMN_INDEX:
										int attendance;
										cell.setCellType(Cell.CELL_TYPE_STRING);
										String value = cell
												.getStringCellValue();
										if (value
												.equals(GlobalConstants.ATTENDANCE_MISSING)) {
											attendance = 0;
										} else {
											attendance = Integer
													.parseInt(value);
										}

										sansad.setAttendance(attendance);
										break;
									}
								} else {
									if (GlobalConstants.ATTENDANCE_COLUMN_INDEX == columnIndex) {
										int attendace;
										cell.setCellType(Cell.CELL_TYPE_STRING);
										String value = cell
												.getStringCellValue();
										if (value
												.equals(GlobalConstants.ATTENDANCE_MISSING)) {
											attendace = 0;
										} else {
											attendace = Integer.parseInt(value);
										}

										int lastSessionAttendance = member
												.getAttendance();

										member.setAttendance(lastSessionAttendance
												+ attendace);
									}
								}
							}
						}
						if (member != null && division != 0) {
							sansads.put(division, member);
						} else if (sansad.getMemberName() != null) {
							sansads.put(division, sansad);
						}
					}

				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}

				logger.info(String.format(
						"Total %d Sansad inforamtion is loaded / updated",
						sansads.size()));
			}
		} catch (FileNotFoundException e) {
			logger.info("File not found " + e);
		} catch (IOException e) {
			logger.info("Error while performing IO opearation " + e);
		}
	}
}
