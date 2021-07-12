/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;

import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.context.FhirContext;

public class CTV3Parser {

	class CTVDesc {
		String id;
		String type;
		String desc;

		@Override
		public int hashCode() {

			return id.hashCode();
		}
	}

	class CTVCode {
		String id;
		String status;
		String preferedName;
		CTVCode domain;
		Set<CTVCode> parents;
		Set<CTVDesc> descriptions;
		String firstDate;

		public CTVCode() {
			parents = new HashSet<CTV3Parser.CTVCode>();
			descriptions = new HashSet<CTV3Parser.CTVDesc>();
		}

		@Override
		public int hashCode() {

			return id.hashCode();
		}

	}

	public void processCodeSystemWithUpdate(String versioned_folder, String historyFile, String version,
			String outFolder, String txServerUrl, FeedClient feedClient) throws IOException, ParseException {
		CodeSystem cs = processCodeSystem(versioned_folder, historyFile, version, outFolder);
		String outFileName = "CodeSystem - CTV3 - " + version + ".json";
		File outFile = new File(outFolder, outFileName);

		FhirContext ctx = FhirContext.forR4();
		// Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(cs),
		// outFile);
		Utility.toTextFile(ctx.newJsonParser().encodeResourceToString(cs), outFile);

		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);
		}

		if (feedClient != null) {
			Entry entry = FeedUtility.createFeedEntry_CodeSystem(cs, outFileName);
			String entryFileName = Utility.jsonFileNameToEntry(outFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder, entryFileName));
			feedClient.updateEntryToNHSFeed(entry, new File(outFolder, entryFileName), new File(outFolder, outFileName));
		}

	}

	public CodeSystem processCodeSystem(String versioned_folder, String historyFile, String version, String outFolder)
			throws IOException, ParseException {

		System.out.println("Process CTV 3 version - " + version);
		File conFile = new File(versioned_folder, "Concept.v3");
		File termFile = new File(versioned_folder, "Terms.v3");
		File descripFile = new File(versioned_folder, "descrip.v3");
		File relFile = new File(versioned_folder, "V3hier.v3");
		File dcfFile = new File(versioned_folder, "Dcf.v3");

		Map<String, CTVCode> allCodes = new HashMap<String, CTV3Parser.CTVCode>();
		Map<String, String> domains = new HashMap<String, String>();
		Map<String, String> allTerms = new HashMap<String, String>();
		Map<String, String> history = new HashMap<String, String>();
		Map<String, String> dcf = new HashMap<String, String>();

		// Load History File
		for (String line : Utility.readTxtFile(historyFile, true)) {
			String[] parts = line.split("\t");
			String id = parts[0];
			if (id.length() > 0) {
				history.put(id, parts[1]);
			}
		}

		// Load dcf File
		for (String line : Utility.readTxtFile(dcfFile, true)) {
			String[] parts = line.split("\\|");
			String tid = parts[0];
			if (tid.length() > 0) {
				dcf.put(parts[1],tid);
			}
		}
		
		System.out.printf("Total dcf - %s\n", dcf.size());

		// Load concepts
		for (String line : Utility.readTxtFile(conFile, false)) {
			String[] parts = line.split("\\|");
			if (parts.length == 4) {
				CTVCode code = new CTVCode();
				code.id = parts[0];
				code.status = parts[1];
				domains.put(parts[0], parts[3]);
				allCodes.put(code.id, code);
				code.firstDate = history.get(code.id);
			}
		}

		for (Map.Entry<String, String> e : domains.entrySet()) {
			allCodes.get(e.getKey()).domain = allCodes.get(e.getValue());
		}
		System.out.printf("Total load concepts - %s\n", allCodes.size());

		// Load Desc

		for (String line : Utility.readTxtFile(termFile, false)) {
			String[] parts = line.split("\\|");
			if (parts.length > 0) {
				allTerms.put(parts[0], parts[2]);
			}
		}

		// load Description
		for (String line : Utility.readTxtFile(descripFile, false)) {
			String[] parts = line.split("\\|");
			if (parts.length > 0) {
				CTVCode code = allCodes.get(parts[0]);
				CTVDesc desc = new CTVDesc();
				desc.id = parts[1];
				desc.desc = allTerms.get(desc.id);
				desc.type = parts[2];
				code.descriptions.add(desc);
			}
		}

		// System.out.printf("Total load terms - %s\n", allTerms.size());

		// add desc from dcf for inactive code
		for (CTVCode c : allCodes.values()) {
			if (c.descriptions.size() == 0) {
				if(dcf.containsKey(c.id)) {
					CTVDesc desc = new CTVDesc();
					desc.id = dcf.get(c.id);
					desc.desc = allTerms.get(desc.id);
					desc.type = "P" ;
					c.descriptions.add(desc);
				}
			}
		}
		
		// Load Real
		for (String line : Utility.readTxtFile(relFile, false)) {
			String[] parts = line.split("\\|");
			if (parts.length > 0) {
				CTVCode c = allCodes.get(parts[0]);
				CTVCode p = allCodes.get(parts[1]);
				c.parents.add(p);
			}
		}
		
		for (String k : allCodes.keySet()) {
			CTVCode code = allCodes.get(k);
			if(code.parents.size()==0) {
				System.out.println(code.id);
			}
		}

		// out to Fhir r4
		SimpleDateFormat f = new SimpleDateFormat("yyyymmdd");
		Date d = f.parse(version);

		String url = "http://read.info/ctv3";
		String title = "Clinical Terms Version3";
		CodeSystem codeSystem = new CodeSystem();

		codeSystem.setId(title.replaceAll("\\s", "-"));
		codeSystem.setUrl(url).setValueSet(url + "/vs").setName(title.replaceAll("\\s", "_")).setVersion(version)
				.setDate(d).setTitle(title).setStatus(PublicationStatus.ACTIVE).setExperimental(false)
				.setCopyright(
						"Copyright © 2020 Health and Social Care Information Centre. NHS Digital is the trading name of the Health and Social Care Information Centre.")
				.setPublisher("NHS Digital").setDescription(title + " FHIR CodeSystem")
				.setContent(CodeSystemContentMode.COMPLETE);

		codeSystem.addProperty(new PropertyComponent().setCode("status").setDescription("Concept Status")
				.setType(PropertyType.STRING));

		codeSystem.addProperty(new PropertyComponent().setCode("domain").setDescription("Concept Domain")
				.setType(PropertyType.CODE));

		// codeSystem.addProperty(new
		// PropertyComponent().setCode("parent").setDescription("Concept Parent").
		// setType(PropertyType.CODE));

		codeSystem.addProperty(new PropertyComponent().setType(PropertyType.DATETIME).setCode("firstDate")
				.setDescription("The Read V3 term code first date"));

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		for (String k : allCodes.keySet()) {
			CTVCode code = allCodes.get(k);
			concepts.add(buildConcept(code));
		}

		codeSystem.setConcept(concepts);

		return codeSystem;

	}

	private ConceptDefinitionComponent buildConcept(CTVCode code) {

		ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
		concept.setCode(code.id);
		ConceptPropertyComponent property_status = new ConceptPropertyComponent(new CodeType("status"),
				new StringType(code.status));
		concept.addProperty(property_status);
		ConceptPropertyComponent property_domain = new ConceptPropertyComponent(new CodeType("domain"),
				new CodeType(code.domain.id));
		concept.addProperty(property_domain);
		if (code.firstDate != null) {
			concept.addProperty(new ConceptPropertyComponent(new CodeType("firstDate"),
					new DateTimeType(formatDateTime(code.firstDate))));
		}

		if (code.parents.size() > 0) {
			for (CTVCode p : code.parents) {
				ConceptPropertyComponent property_parent = new ConceptPropertyComponent(new CodeType("parent"),
						new CodeType(p.id));
				concept.addProperty(property_parent);
			}
		}

		for (CTVDesc d : code.descriptions) {
			if (d.type.equals("P")) {
				concept.setDisplay(d.desc);
			} else {
				ConceptDefinitionDesignationComponent designation = new ConceptDefinitionDesignationComponent();
				Coding coding = new Coding();
				coding.setSystem("http://snomed.info/sct");
				coding.setCode("900000000000013009");
				designation.setUse(coding);
				designation.setValue(d.desc);
				concept.addDesignation(designation);

			}
		}

		return concept;
	}

	private String formatDateTime(String input) {
		return input.substring(0, 4) + "-" + input.substring(4);
	}
}
