/*
 * Copyright © 2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;

public class Utility {
	
	protected static FhirContext ctx = FhirContext.forR4();

	public static List<String> readTxtFile(String fileName, boolean heading) throws IOException {
		File file = new File(fileName);
		return readTxtFile(file, heading);
	}

	public static List<String> readTxtFile(File file, boolean heading) throws IOException {
		List<String> con = FileUtils.readLines(file, Charset.defaultCharset());
		if (heading) {
			con.remove(0);
		}
		return con;

	}

	public static String readInputToString(InputStream is) {
		try {
			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			String line = buf.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;

	}

	public static String readTextToString(String fileName) {
		try {
			BufferedReader buf = new BufferedReader(new FileReader(fileName));
			String line = buf.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
			buf.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static void toTextFile(ArrayList<String> con, String fileName) {

		FileUtils.deleteQuietly(new File(fileName));

		try {
			FileWriter fileWriter = new FileWriter(fileName);
			for (String s : con) {
				fileWriter.write(s + "\r\n");
			}

			fileWriter.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		System.out.println("\nOutput to release file : " + fileName);
	}

	public static void toTextFile(String con, String fileName) {

		if (fileName != null) {
			FileUtils.deleteQuietly(new File(fileName));

			try {
				FileWriter fileWriter = new FileWriter(fileName);
				fileWriter.write(con);
				fileWriter.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
			System.out.println("\nOutput to release file : " + fileName);
		}

	}

	public static void toTextFile(String con, File file) {

		if (file != null) {
			FileUtils.deleteQuietly(file);

			try {
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(con);
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\nOutput to release file : " + file.getAbsolutePath());
		}

	}

	public static List<List<String>> readXLSXFileSingleTab(String fileName, String sheetName, boolean heading) throws FileNotFoundException {

		FileInputStream file = new FileInputStream(fileName);

		return readXLSXFileSingleTab(file, sheetName, heading);
	}

	public static List<List<String>> readXLSXFileSingleTab(InputStream file, String sheetName, boolean heading) {
		List<List<String>> lines = new ArrayList<>();
		try {
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet(sheetName);

			Iterator<Row> rowIterator = sheet.iterator();
			if (heading) {
				rowIterator.next(); // skip heading
			}
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				List<String> rowString = new LinkedList<>();

				for (int i = 0; i < row.getLastCellNum(); i++) {
					String cellCountent = "";
					if (row.getCell(i) != null && row.getCell(i).getCellType() == 0) {
						cellCountent = String.format("%.0f", row.getCell(i).getNumericCellValue());

					} else if (row.getCell(i) != null && row.getCell(i).getCellType() == 1) {
						cellCountent = row.getCell(i).getStringCellValue();
					}
					rowString.add(cellCountent);
				}
				lines.add(rowString);
			}

			workbook.close();
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static String jsonFileNameToEntry(String name) {

		return name.replaceAll(".json", "_Entry.json");

	}
	
	public static void printResource(Resource r) {

		System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(r));

	}

	

}
