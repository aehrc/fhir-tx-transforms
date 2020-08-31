/*
 * Copyright © 2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.UrlType;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.r4.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.Utility;
import ca.uhn.fhir.context.FhirContext;

public class CTV2Parser {

	private CodeSystem processCodeSystem(String conFile, String version, String outFolder) throws IOException {
		String outFile = outFolder == null ? null : outFolder + "\\CodeSystem - READ V2 " + version + ".json";
		Map<String, String> codes = new HashMap<String, String>();
		for (String line : Utility.readTxtFile(conFile, false)) {
			String[] parts = line.split(",");
			String id = parts[0].replaceAll("\"", "").trim();
			String term = parts[1].replaceAll("\"", "").trim();
			codes.put(id, term);
			// System.out.println(id + "\t" +term);
		}
		// out to Fhir r4
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId("Read-Code-Clinical-Term-Version-2");
		codeSystem.setUrl("http://read.info/readv2").setValueSet("http://read.info/readv2/ValueSet")
				.setName("NHS Read Code Version 2 Code System").setVersion(version).setTitle("NHS Read Code Version 2")
				.setStatus(PublicationStatus.ACTIVE).setExperimental(false).setPublisher("NHS UK")
				.setContent(CodeSystemContentMode.COMPLETE);

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		for (Map.Entry<String, String> entry : codes.entrySet()) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(entry.getKey());
			concept.setDisplay(entry.getValue());
			concepts.add(concept);
		}
		codeSystem.setConcept(concepts);

		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem), outFile);
		return codeSystem;

	}

	private ConceptMap processMapping(String mapFile, String version, String outFolder) throws IOException {

		String outFile = outFolder == null ? null : outFolder + "\\ConceptMap - Read2 -ICD10UK " + version + ".json";
		ConceptMap conceptMap = new ConceptMap();
		conceptMap.setId("READV2-ICD10UK-MAP");
		conceptMap.setUrl("http://fhir.hl7.org.uk/R4/ConceptMap/ReadV2-ICD10UK").setVersion(version)
				.setTitle("NHS Read V2 to ICD 10 UK Map").setStatus(PublicationStatus.ACTIVE).setExperimental(false)
				.setPublisher("NHS UK").setSource(new UrlType().setValue("http://read.info/readv2/ValueSet"))
				.setTarget(new UrlType().setValue("http://read.info/readv2/ValueSet"));

		List<ConceptMapGroupComponent> groups = new ArrayList<ConceptMapGroupComponent>();

		ConceptMapGroupComponent group = new ConceptMapGroupComponent();
		group.setSource("http://read.info/readv2");
		group.setSourceVersion(version);
		group.setTarget("http://fhir.hl7.org.uk/R4/CodeSystem/ICD10UK");
		group.setTargetVersion("5th"); // Fixed version, as there is no more update for read 2 code.

		List<SourceElementComponent> elements = new ArrayList<ConceptMap.SourceElementComponent>();
		// Add element
		for (String line : Utility.readTxtFile(mapFile, false)) {
			String[] parts = line.split("\\|");
			String id = parts[0];
			String map = parts[1];
			if (map.matches("^[A-Z](.*)")) {
				SourceElementComponent source = new SourceElementComponent().setCode(id);
				source.addTarget(new TargetElementComponent().setCode(map).setEquivalence(ConceptMapEquivalence.EQUAL));
				elements.add(source);
			}
		}

		group.setElement(elements);
		groups.add(group);
		conceptMap.setGroup(groups);

		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(conceptMap), outFile);

		return conceptMap;
	}

	public void processCodeSystemWithUpdate(String conFile, String version, String outFolder, String txServerUrl)
			throws IOException {
		CodeSystem cs = processCodeSystem(conFile, version, outFolder);
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);
		}
	}

	public void processConceptMapWithUpdate(String mapFile, String version, String outFolder, String txServerUrl)
			throws IOException {
		ConceptMap cm = processMapping(mapFile, version, outFolder);
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateMap(cm);
		}
	}
}
