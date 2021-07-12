/*
 * Copyright © 2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.helper;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utility {

	public static List<String> readTxtFile(String fileName, boolean heading) throws IOException {
		File file = new File(fileName);
		return readTxtFile(file,heading);
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
	
	public static String jsonFileNameToEntry(String name) {

		return name.replaceAll(".json", "_Entry.json");

	}

}
