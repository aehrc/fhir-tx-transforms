/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.Utility;
import ca.uhn.fhir.context.FhirContext;

public class DMDParser {
	
	final Logger logger = Logger.getLogger(DMDParser.class.getName());
	
	final String baseURL_CodeSystem = "http://digital.nhs.uk/fhir/CodeSystem/dmd";
	final String baseURL_ValueSet = "http://digital.nhs.uk/fhir/ValueSet/dmd";
	final String csID = "CodeSystem-DMD";
	final String title = "Dictionary of medicines and devices (dm+d)";
	final String baseURL_CodeSystem_Lookup = "http://digital.nhs.uk/fhir/CodeSystem/dmd";
	final String baseURL_ValueSet_Lookup = "http://digital.nhs.uk/fhir/ValueSet/dmd";
	final String title_lookup = "DM+D Lookup Table ";
	final Pattern versionPattern = 
			Pattern.compile("(.*)(nhsbsa)(_)(dmd)(_)([0-9]+\\.[0-9]\\.[0-9])(_)([0-9]+)");

	public CodeSystem processCodeSystem(String dmdFolder, String releaseSerial, String outFolder)
			throws IOException {

		String version = getVersionNumber(dmdFolder);
		System.out.println("Process DMD  " + dmdFolder);
		File outFile = outFolder == null ? null :new File( outFolder , "\\Code System - DMD - " + version + ".json");
		
		File ingredientFile = new File(dmdFolder, "f_ingredient2_" + releaseSerial + ".xml");
		File ampFile = new File(dmdFolder, "f_amp2_" + releaseSerial + ".xml");
		File vtmFile = new File(dmdFolder, "f_vtm2_" + releaseSerial + ".xml");
		File amppFile = new File(dmdFolder, "f_ampp2_" + releaseSerial + ".xml");
		File vmpFile = new File(dmdFolder, "f_vmp2_" + releaseSerial + ".xml");
		File vmppFile = new File(dmdFolder, "f_vmpp2_" +releaseSerial + ".xml");
		File lookupFile = new File(dmdFolder, "f_lookup2_" + releaseSerial + ".xml");

		// out to Fhir r4
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setUrl("http://fhir.hl7.org.uk/R4/CodeSystem/OPCS-4");
		codeSystem.setValueSet("http://fhir.hl7.org.uk/R4/ValueSet/OPCS-" + version);
		codeSystem.setId("OPCS-" + version);
		codeSystem.setName("NHS OPCS-4");
		codeSystem.setVersion(version);
		codeSystem.setTitle("NHS OPCS-4");
		codeSystem.setStatus(PublicationStatus.ACTIVE);
		codeSystem.setExperimental(false);
		codeSystem.setPublisher("NHS UK");
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		PropertyComponent propertyComponent_valid = new PropertyComponent();
		propertyComponent_valid.setCode("valid").setDescription("valid").setType(PropertyType.BOOLEAN);
		codeSystem.addProperty(propertyComponent_valid);

		return codeSystem;
	}

	public void processCodeSystemWithUpdate(String dmdFolder, String releaseSerial, String outFolder,String txServerUrl) throws IOException {
		CodeSystem cs = processCodeSystem(dmdFolder, releaseSerial, outFolder);
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);
		}

	}
	
	private String getVersionNumber(String dmdFolderName) {
		int startPos = dmdFolderName.indexOf("nhsbsa_dmd");
		String fullName = dmdFolderName.substring(startPos, startPos + 30);
		String[] parts = fullName.split("_");
		String version = parts[3].substring(0, 4) + parts[2];
		logger.info("Version number created " + version);
		return version;
	}

}
