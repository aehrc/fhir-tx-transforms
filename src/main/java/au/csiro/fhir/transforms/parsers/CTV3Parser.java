/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.IOException;
import java.util.ArrayList;
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
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.Utility;

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

		public CTVCode() {
			parents = new HashSet<CTV3Parser.CTVCode>();
			descriptions = new HashSet<CTV3Parser.CTVDesc>();
		}

		@Override
		public int hashCode() {

			return id.hashCode();
		}

	}

	public void processCodeSystemWithUpdate(String versioned_folder, String version, String outFolder,
			String txServerUrl) throws IOException {
		CodeSystem cs = processCodeSystem(versioned_folder, version, outFolder);
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);
		}
		;

	}

	public CodeSystem processCodeSystem(String versioned_folder, String version, String outFolder) throws IOException {

		final String outFile = outFolder + "\\Code System - CTV3 - " + version + ".json";

		System.out.println("Process CTV 3 version - " + version);
		String conFile = versioned_folder + "\\Concept.v3";
		String descFile = versioned_folder + "\\Terms.v3";
		String descripFile = versioned_folder + "\\descrip.v3";
		String relFile = versioned_folder + "\\V3hier.v3";

		Map<String, CTVCode> allCodes = new HashMap<String, CTV3Parser.CTVCode>();
		Map<String, String> domains = new HashMap<String, String>();
		Map<String, String> allTerms = new HashMap<String, String>();

		for (String line : Utility.readTxtFile(conFile, false)) {
			String[] parts = line.split("\\|");
			if (parts.length == 4) {
				CTVCode code = new CTVCode();
				code.id = parts[0];
				code.status = parts[1];
				domains.put(parts[0], parts[3]);
				allCodes.put(code.id, code);
			}
		}

		for (Map.Entry<String, String> e : domains.entrySet()) {
			allCodes.get(e.getKey()).domain = allCodes.get(e.getValue());
		}
		// System.out.printf("Total load concepts - %s\n", allCodes.size());

		// Load Desc

		for (String line : Utility.readTxtFile(descFile, false)) {
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
		// Load Real
		for (String line : Utility.readTxtFile(relFile, false)) {
			String[] parts = line.split("\\|");
			if (parts.length > 0) {
				CTVCode c = allCodes.get(parts[0]);
				CTVCode p = allCodes.get(parts[1]);
				c.parents.add(p);
			}
		}

		// out to Fhir r4
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setUrl("http://read.info/ctv3");
		codeSystem.setValueSet("http://read.info/ctv3/ValueSet-" + version);
		codeSystem.setId("Read-Code-Clinical-Term-Version-3-" + version);
		codeSystem.setName("NHS Read Code Clinical Term Version 3 Code System");
		codeSystem.setVersion(version);
		codeSystem.setTitle("NHS Read Code Clinical Term Version 3");
		codeSystem.setStatus(PublicationStatus.ACTIVE);
		codeSystem.setExperimental(false);
		codeSystem.setPublisher("NHS UK");
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		PropertyComponent propertyComponent_status = new PropertyComponent();
		propertyComponent_status.setCode("status").setDescription("Concept Status").setType(PropertyType.STRING);
		codeSystem.addProperty(propertyComponent_status);

		PropertyComponent propertyComponent_domain = new PropertyComponent();
		propertyComponent_domain.setCode("domain").setDescription("Concept Domain").setType(PropertyType.STRING);
		codeSystem.addProperty(propertyComponent_domain);

		PropertyComponent propertyComponent_parent = new PropertyComponent();
		propertyComponent_parent.setCode("parent").setDescription("Concept Parent").setType(PropertyType.CODE);
		codeSystem.addProperty(propertyComponent_parent);

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		for (String k : allCodes.keySet()) {
			CTVCode code = allCodes.get(k);
			concepts.add(buildConcept(code));
		}

		codeSystem.setConcept(concepts);

		FhirContext ctx = FhirContext.forR4();
		// Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem),
		// outFile);
		Utility.toTextFile(ctx.newJsonParser().encodeResourceToString(codeSystem), outFile);
		return codeSystem;

	}

	private ConceptDefinitionComponent buildConcept(CTVCode code) {

		ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
		concept.setCode(code.id);
		ConceptPropertyComponent property_status = new ConceptPropertyComponent(new CodeType("status"),
				new StringType(code.status));
		concept.addProperty(property_status);
		ConceptPropertyComponent property_domain = new ConceptPropertyComponent(new CodeType("domain"),
				new StringType(code.domain.id));
		concept.addProperty(property_domain);

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
}
