/*
 * Copyright © 2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
 */

package au.csiro.fhir.transforms;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.parsers.CTV2Parser;
import au.csiro.fhir.transforms.parsers.CTV3Parser;
import au.csiro.fhir.transforms.parsers.ICDParser;
import au.csiro.fhir.transforms.parsers.NICIPParser;
import au.csiro.fhir.transforms.parsers.ODSParser;
import au.csiro.fhir.transforms.parsers.OPCSParser;

public class Parser {

	// private final String properties_file_name = "config.properties";

	Properties props = new Properties();
	String outFolder = null;
	String txServer = null;
	boolean updateServer = false;

	private void loadPropoerties(String configFileName) throws IOException {

		String propertyFileContents = Utility.readTextToString(configFileName);

		props.load(new StringReader(propertyFileContents.replace("\\", "\\\\")));

		outFolder = props.getProperty("output.dir");
		if (outFolder != null) {
			System.out.printf("Output folder is %s \n", outFolder);
		}
		txServer = props.getProperty("tx.server.url");
		if (txServer != null) {
			System.out.printf("FHIR terminology server URL is %s \n", txServer);
		}
		updateServer = Boolean.valueOf(props.getProperty("server.update"));
		System.out.printf("FHIR terminology server update is %s \n", updateServer);
	}

	private void parseCTV2() throws IOException {
		String codeFile = props.getProperty("ctv2.coreFile");
		String mapFile = props.getProperty("ctv2.mapFile");
		CTV2Parser parser = new CTV2Parser();
		parser.processCodeSystemWithUpdate(codeFile, "20160401", outFolder, updateServer ? txServer : null);
		parser.processConceptMapWithUpdate(mapFile, "20160401", outFolder, updateServer ? txServer : null);
	}

	private void parseCTV3() throws IOException {
		CTV3Parser parser = new CTV3Parser();
		for (String s : getPropertiesWithStartString("ctv3.version")) {
			String version = s.split("\\.")[2];
			parserSingleVersionCTV3(s, version, parser);
		}
	}

	private void parseNICIP() throws IOException {
		NICIPParser parser = new NICIPParser();
		for (String s : getPropertiesWithStartString("nicip.version")) {
			String version = s.split("\\.")[2];
			parserSingleVersionNICIP(s, version, parser);
		}
	}

	private void parseICD() throws IOException {
		ICDParser parser = new ICDParser();
		for (String s : getPropertiesWithStartString("icd10uk.version")) {
			String version = s.split("\\.")[2];
			String codeFile = props.getProperty(s);
			parser.processCodeSystemWithUpdate(codeFile, version, outFolder, txServer);
		}

	}

	private void parseOPCS() throws IOException {
		OPCSParser parser = new OPCSParser();
		Set<String> versions = new HashSet<String>();
		for (String s : getPropertiesWithStartString("opcs.version")) {
			String version = s.split("\\.")[2];
			versions.add(version);
		}
		for (String v : versions) {
			String codeFile = props.getProperty("opcs.version." + v + ".codeFile");
			String validFile = props.getProperty("opcs.version." + v + ".validFile");
			parseSingleOPCS(codeFile, validFile, v, outFolder, parser);
		}
	}

	private void parseODS() throws IOException, ParserConfigurationException, SAXException {
		ODSParser parser = new ODSParser();
		for (String s : getPropertiesWithStartString("ods.version")) {
			String version = s.split("\\.")[2];
			String zipFile = props.getProperty(s);
			parseSingleODS(zipFile, version, outFolder, parser);

		}
	}

	private void parseSingleOPCS(String codeFile, String validFile, String version, String outFolder, OPCSParser parser)
			throws IOException {
		parser.processCodeSystemWithUpdate(codeFile, validFile, version, outFolder, updateServer ? txServer : null);
	}

	private void parseSingleODS(String zipFile, String version, String outFolder, ODSParser parser)
			throws IOException, SAXException, ParserConfigurationException {
		if (zipFile != null) {
			parser.processCodeSystemWithUpdate(zipFile, version, outFolder, updateServer ? txServer : null);
		}

	}

	private void parserSingleVersionCTV3(String propName, String version, CTV3Parser parser) throws IOException {
		String packageFolder = props.getProperty(propName);
		if (packageFolder != null) {
			parser.processCodeSystemWithUpdate(packageFolder, version, outFolder, updateServer ? txServer : null);
		}
	}

	private void parserSingleVersionNICIP(String propName, String version, NICIPParser parser) throws IOException {
		String packageFolder = props.getProperty(propName);
		if (packageFolder != null) {
			parser.processResourceWithUpdate(packageFolder, version, outFolder, updateServer ? txServer : null);
		}
	}

	public void parseAll(String configFileName) throws IOException, ParserConfigurationException, SAXException {
		loadPropoerties(configFileName);
		if (Boolean.valueOf(props.getProperty("process.nicip"))) {
			parseNICIP();
		}
		if (Boolean.valueOf(props.getProperty("process.icd"))) {
			parseICD();
		}
		if (Boolean.valueOf(props.getProperty("process.opcs"))) {
			parseOPCS();
		}
		if (Boolean.valueOf(props.getProperty("process.ctv3"))) {
			parseCTV3();
		}
		if (Boolean.valueOf(props.getProperty("process.ctv2"))) {
			parseCTV2();
		}
		if (Boolean.valueOf(props.getProperty("process.ods"))) {
			parseODS();
		}
	}

	public static void main(String[] args) {

		if (args == null) {
			System.out.println("Usage is: Java -jar PATH_TO_JAR CONFIG_FILE");
			System.exit(0);
		}

		Parser parser = new Parser();
		try {
			parser.parseAll(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private List<String> getPropertiesWithStartString(String start) {
		List<String> ps = new ArrayList<String>();
		Enumeration<?> names = props.propertyNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement().toString();
			if (name.startsWith(start)) {
				System.out.println("Find version " + name);
				ps.add(name);
			}

		}

		return ps;
	}
}
