/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;
import ca.uhn.fhir.context.FhirContext;


class OPCSCode {
	String codeID;
	String selectionIndicators;
	String codeIDNoDot;
	String display;
	String operationCode;
	String operationName3;
	String operationName4;
	String sexAbsolute;
	String sexScrutiny;
	String statusOfOperation;
	String methodOfDelivery;
}

public class OPCSParser {


	public CodeSystem processCodeSystem(String codeFile, String metaFile, String version, String outFolder)
			throws IOException {
		


		System.out.println("Process OPCS version " + version);
		Map<String, OPCSCode> allCodes = new HashMap<String, OPCSCode>();

		for (String line : Utility.readTxtFile(codeFile, false)) {
			String[] parts = line.split("\\t");
			if (parts.length > 0) {
				OPCSCode code = new OPCSCode();
				code.codeID = parts[0];
				code.codeIDNoDot = parts[0].replaceAll("\\.", "");
				code.display = parts[1];
				allCodes.put(code.codeIDNoDot, code);
			}
		}
		for (String line : Utility.readTxtFile(metaFile, false)) {

			if (line.length() > 0) {
				String selection = line.substring(0,11);
				String id = line.substring(12,16);
	
				String opreation3 = line.substring(24,79).trim();
				String opreation4 = line.substring(84,144).trim();
				String sexA = line.substring(187,188).trim();
				String sexS = line.substring(191,192).trim();
				String status = line.substring(193,195).trim();
				String method = line.substring(218,228).trim();
				OPCSCode code = allCodes.get(id);
				if(code==null) {
					System.out.println("Check code " + id);
				}
				
				if(selection.length() >0 ) {
					code.selectionIndicators = selection;
				}
				
				if(id.length() >0 ) {
					code.operationCode = id;
				}
				if(opreation3.length() >0 ) {
					code.operationName3 = opreation3;
				}
				if(opreation4.length() >0 ) {
					code.operationName4 = opreation4;
				}
				if(sexA.length() >0 ) {
					code.sexAbsolute = sexA;
				}
				
				if(sexS.length() >0 ) {
					code.sexScrutiny = sexS;
				}
				if(status.length() >0 ) {
					code.statusOfOperation = status;
				}
				if(method.length() >0 ) {
					code.methodOfDelivery = method;
				}
			}
		}

		System.out.printf("Total load code - %s\n", allCodes.size());


		// out to Fhir r4
	    
		String url = "http://fhir.hl7.org.uk/CodeSystem/OPCS-4";
		String title ="NHS OPCS-4";
		CodeSystem codeSystem = new CodeSystem();
		
		DateTimeType dt = new DateTimeType();
		dt.setValueAsString(version.equals("4.8")?"2017-04-01":(version.equals("4.9")?"2020-04-01":null));
		
		codeSystem.setId(title.replaceAll("\\s", "-") + "-" + version);
		codeSystem.setName(title.replaceAll("\\s", "_"))
		.setDateElement(dt)
		.setUrl(url)
		.setValueSet(url + "/vs")
		.setVersion(version)
		.setTitle(title)
		.setStatus(PublicationStatus.ACTIVE)
		.setExperimental(false)
		.setCopyright(
				"Copyright © 2020 Health and Social Care Information Centre. NHS Digital is the trading name of the Health and Social Care Information Centre.")
		.setPublisher("NHS Digital")
		.setDescription(title + " FHIR CodeSystem")
		.setContent(CodeSystemContentMode.COMPLETE);
		
		
		
		codeSystem.addProperty(
				new PropertyComponent().setCode("Selection indicators").setDescription("Selection indicators").setType(PropertyType.STRING));

		
		codeSystem.addProperty(
				new PropertyComponent().setCode("Operation code").setDescription("Operation code").setType(PropertyType.STRING));
			
		codeSystem.addProperty(
				new PropertyComponent().setCode("Operation name (3-character category)").setDescription("Operation name (3-character category)").setType(PropertyType.STRING));
		
		codeSystem.addProperty(
				new PropertyComponent().setCode("Operation name (4-character subcategory)").setDescription("Operation name (4-character subcategory)").setType(PropertyType.STRING));
		
		codeSystem.addProperty(
				new PropertyComponent().setCode("Sex (absolute)").setDescription("Sex (absolute)").setType(PropertyType.STRING));
		
		codeSystem.addProperty(
				new PropertyComponent().setCode("Sex (scrutiny)").setDescription("Sex (scrutiny)").setType(PropertyType.STRING));
		
		
		codeSystem.addProperty(
				new PropertyComponent().setCode("Status of operation").setDescription("Status of operation").setType(PropertyType.STRING));
		
		codeSystem.addProperty(
				new PropertyComponent().setCode("Method of delivery").setDescription("Method of delivery").setType(PropertyType.STRING));

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		for (Map.Entry<String, OPCSCode> entry : allCodes.entrySet()) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			OPCSCode code = entry.getValue();
			concept.setCode(code.codeID);
			concept.setDisplay(code.display);
			
			if(code.selectionIndicators!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Selection indicators"), new StringType().setValue(code.selectionIndicators)));	
			}
			
			if(code.operationCode!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Operation code"), new StringType().setValue(code.operationCode)));	
			}
			
			if(code.operationName3!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Operation name (3-character category)"), new StringType().setValue(code.operationName3)));	
			}
			if(code.operationName4!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Operation name (4-character subcategory)"), new StringType().setValue(code.operationName4)));	
			}
			
			if(code.sexAbsolute!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Sex (absolute)"), new StringType().setValue(code.sexAbsolute)));	
			}
			if(code.sexScrutiny!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Sex (scrutiny)"), new StringType().setValue(code.sexScrutiny)));	
			}
			if(code.methodOfDelivery!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Method of delivery"), new StringType().setValue(code.methodOfDelivery)));	
			}
			if(code.statusOfOperation!=null) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Status of operation"), new StringType().setValue(code.statusOfOperation)));	
			}
			
			if(code.codeIDNoDot.length() == 4) {
				// Add Parent hierarchy
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("parent"), new CodeType().setValue(code.codeIDNoDot.substring(0,3))));	
			}
			
			concepts.add(concept);
		}

		codeSystem.setConcept(concepts);
		return codeSystem;
	}

	public void processCodeSystemWithUpdate(String codeFile, String metaFile, String version, String outFolder,
			String txServerUrl, FeedClient feedClient) throws IOException {
		version = version.replaceAll("/", ".");
		CodeSystem cs = processCodeSystem(codeFile, metaFile, version, outFolder);
		String outFileName = "CodeSystem-OPCS-4-" + version + ".json";
		File outFile = new File( outFolder , outFileName );
		
		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(cs), outFile);
		
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);

		}
		if (feedClient != null) {
			Entry entry = FeedUtility.createFeedEntry_CodeSystem(cs, outFileName);
			String entryFileName = Utility.jsonFileNameToEntry(outFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder,  entryFileName));
			feedClient.updateEntryToNHSFeed(entry, new File(outFolder,entryFileName), new File(outFolder,outFileName));
		}

	}

}
