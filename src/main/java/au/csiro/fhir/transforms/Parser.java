/*
 * Copyright © 2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
 */

package au.csiro.fhir.transforms;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.parsers.CTV2Parser;
import au.csiro.fhir.transforms.parsers.CTV3Parser;
import au.csiro.fhir.transforms.parsers.DMDParser;
import au.csiro.fhir.transforms.parsers.ICDParser;
import au.csiro.fhir.transforms.parsers.NICIPParser;
import au.csiro.fhir.transforms.parsers.ODSParser;
import au.csiro.fhir.transforms.parsers.OPCSParser;

public class Parser {

	// private final String properties_file_name = "config.properties";

	Properties props = new Properties();
	String outFolder = null;
	String txServer = null;
	FeedClient feedClient= null;
	
	String feedServer = null;
	String feedClientId = null;
	String feedClientSecret = null;
	String authRealm = null;
	String authServer = null;

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
		feedServer = props.getProperty("feed.server.name");
		feedClientId = props.getProperty("feed.server.client.id");
		feedClientSecret= props.getProperty("feed.server.client.secret");
		authRealm = props.getProperty("auth.server.realm");
		authServer = props.getProperty("auth.server");

		if (feedServer != null && feedClientId != null && feedClientSecret != null && authRealm != null && authServer != null ) {
			feedClient = new FeedClient(feedServer, feedClientId, feedClientSecret, authRealm ,authServer);
			System.out.printf("Atomio feed is %s \n", feedServer);
		}
	}

	private void parseCTV2() throws IOException, ParseException {
		String termFile = props.getProperty("ctv2.termFile");	
		CTV2Parser parser = new CTV2Parser();
		String domain = "";
		if(termFile.contains("UNIFIED")) {
			domain = "UNIFIED";
		}
		else if(termFile.contains("UNISCOT")) {
			domain = "UNISCOT";
		}
		parser.processCodeSystemWithUpdate( termFile, "20160401", outFolder, txServer, feedClient,  domain);
	}
	
	private void parseCTV3() throws IOException, ParseException {
		CTV3Parser parser = new CTV3Parser();
		Set<String> versions = new HashSet<String>();
		for (String s : getPropertiesWithStartString("ctv3.version")) {
			String version = s.split("\\.")[2];
			versions.add(version);
		}
		for (String v : versions) {
			String historyFile = props.getProperty("ctv3.version." + v + ".historyFile");
			String folder = props.getProperty("ctv3.version." + v + ".folder");
			parserSingleVersionCTV3(folder,historyFile, v, parser);
		}
	
	}

	private void parseNICIP() throws IOException {
		NICIPParser parser = new NICIPParser();
		for (String s : getPropertiesWithStartString("nicip.version")) {
			String version = s.split("\\.")[2];
			parserSingleVersionNICIP(s, version, parser);
		}
	}

	private void parseICD() throws IOException, ParseException {
		ICDParser parser = new ICDParser();
		for (String s : getPropertiesWithStartString("icd10uk.version")) {
			String version = s.split("\\.")[2];
			version = version.replaceAll("/", ".");
			String codeFile = props.getProperty(s);
			parser.processCodeSystemWithUpdate(codeFile, version, outFolder,  txServer,feedClient);
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
		parser.processCodeSystemWithUpdate(codeFile, validFile, version, outFolder, txServer,feedClient);
	}

	private void parseSingleODS(String zipFile, String version, String outFolder, ODSParser parser)
			throws IOException, SAXException, ParserConfigurationException {
		if (zipFile != null) {
			parser.processCodeSystemWithUpdate(zipFile, version, outFolder, txServer, feedClient);
		}

	}

	private void parserSingleVersionCTV3(String folder , String historyFile, String version, CTV3Parser parser) throws IOException, ParseException {
		if (folder != null) {
			parser.processCodeSystemWithUpdate(folder, historyFile, version, outFolder, txServer,feedClient);
		}
	}

	private void parserSingleVersionNICIP(String propName, String version, NICIPParser parser) throws IOException {
		String packageFolder = props.getProperty(propName);
		if (packageFolder != null) {
			parser.processResourceWithUpdate(packageFolder, version, outFolder, txServer,feedClient);
		}
	}
	
	private void parseDMD() throws IOException, ParseException, JAXBException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String dmdFolder = props.getProperty("dmd.releaseFolder");	
		String dmdSerial = props.getProperty("dmd.releaseSerial");	
		String supportFile =  "dm+d Content For Terminology Server.xlsx";	
		String ukSCTVersion =  props.getProperty("dmd.ukSCTVersion");	
		DMDParser parser = new DMDParser(ukSCTVersion);
		parser.processSupportCodeSystemWithUpdate(dmdFolder, dmdSerial, outFolder, txServer, feedClient);
		parser.processCodeSystemWithUpdate( dmdFolder,dmdSerial ,supportFile,outFolder, txServer, feedClient);
	}

	public void parseAll(String configFileName) throws IOException, ParserConfigurationException, SAXException, ParseException, JAXBException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
		if (Boolean.valueOf(props.getProperty("process.dmd"))) {
			parseDMD();
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
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
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
