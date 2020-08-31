/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.r4.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.Utility;

import org.hl7.fhir.r4.model.UrlType;

import ca.uhn.fhir.context.FhirContext;

public class NICIPParser {

	public void processResourceWithUpdate(String packageFolder, String version, String outFolder, String txServerUrl)
			throws IOException {

		String codeListFile = packageFolder + "\\NICIP_Code_Set_withSCT_" + version + ".txt";
		String opcsMapFile = packageFolder + "\\NICIP-OPCS-4_MappingTable_" + version + ".txt";
		String codeSystemFile = outFolder + "\\CodeSystem - NICIP " + version + ".json";
		String conceptMap_sct_File = outFolder + "\\ConceptMap - NICIP - SNOMED CT " + version + ".json";
		String conceptMap_opcs_File = outFolder + "\\ConceptMap - NICIP - OPCS " + version + ".json";

		/**
		 * Fix for naming error
		 */
		if (version.equals("20190601") || version.equals("20200401")) {
			opcsMapFile = packageFolder + "\\NICIP-OPCS_MappingTable_" + version + ".txt";
		}

		CodeSystem codeSystem = initCodeSystem(version);
		ConceptMap conceptMap_sct = initSctMap(version, "");
		ConceptMap conceptMap_opcs = initOPCSMap(version, "");

		List<SourceElementComponent> elements = conceptMap_sct.getGroup().get(0).getElement();

		for (String line : Utility.readTxtFile(codeListFile, true)) {
			String[] parts = line.split("\\t");
			if (parts[7].equals("A")) {
				boolean diag = parts[9].equals("Y") ? true : false;
				boolean intervention = parts[10].equals("Y") ? true : false;
				String sctID = parts[1];
				String sctName = parts[2];
				ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
				concept.setCode(parts[5]);
				concept.setDisplay(parts[12]);
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("diagnostic"), new BooleanType().setValue(diag)));
				concept.addProperty(new ConceptPropertyComponent(new CodeType("Interventional"),
						new BooleanType().setValue(intervention)));

				Coding coding = new Coding();
				coding.setSystem("http://snomed.info/sct");
				coding.setCode("900000000000013009");
				if (parts.length > 13) {
					concept.addDesignation(
							new ConceptDefinitionDesignationComponent().setUse(coding).setValue(parts[13]));
				}
				if (parts.length > 14) {
					concept.addDesignation(
							new ConceptDefinitionDesignationComponent().setUse(coding).setValue(parts[14]));
				}
				if (parts.length > 15) {
					concept.addDesignation(
							new ConceptDefinitionDesignationComponent().setUse(coding).setValue(parts[15]));
				}
				codeSystem.getConcept().add(concept);

				// sct Map
				SourceElementComponent source = new SourceElementComponent().setCode(parts[5]).setDisplay(parts[12]);
				source.addTarget(new TargetElementComponent().setCode(sctID).setDisplay(sctName)
						.setEquivalence(ConceptMapEquivalence.EQUAL));
				elements.add(source);
			}

		}

		List<SourceElementComponent> elements_opcs_map = conceptMap_opcs.getGroup().get(0).getElement();
		for (String line : Utility.readTxtFile(opcsMapFile, true)) {
			String[] parts = line.split("\\t");
			SourceElementComponent source = new SourceElementComponent().setCode(parts[0]).setDisplay(parts[1]);
			source.addTarget(
					new TargetElementComponent().setCode(parts[4]).setEquivalence(ConceptMapEquivalence.EQUAL));
			elements_opcs_map.add(source);
		}

		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem), codeSystemFile);
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(conceptMap_sct),
				conceptMap_sct_File);
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(conceptMap_opcs),
				conceptMap_opcs_File);

		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(codeSystem);
			fhirClientR4.createUpdateMap(conceptMap_sct);
			fhirClientR4.createUpdateMap(conceptMap_opcs);

		}
	}

	private CodeSystem initCodeSystem(String version) {
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId("NICIP-" + version);
		codeSystem.setUrl("http://fhir.hl7.org.uk/R4/CodeSystem/NICIP")
				.setValueSet("https://fhir.hl7.org.uk/R4/ValueSet/NICIP-" + version).setName("NHS NICIP")
				.setVersion(version).setTitle("NHS NICIP").setStatus(PublicationStatus.ACTIVE).setExperimental(false)
				.setPublisher("NHS UK").setContent(CodeSystemContentMode.COMPLETE);

		codeSystem.addProperty(new PropertyComponent().setCode("diagnostic").setDescription("diagnostic")
				.setType(PropertyType.BOOLEAN));
		codeSystem.addProperty(new PropertyComponent().setCode("Interventional").setDescription("Interventional")
				.setType(PropertyType.BOOLEAN));

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		codeSystem.setConcept(concepts);

		return codeSystem;

	}

	private ConceptMap initSctMap(String version, String sctVersion) {
		ConceptMap conceptMap = new ConceptMap();
		conceptMap.setId("NICIP-SNOMED-MAP-" + version);
		conceptMap.setUrl("http://fhir.hl7.org.uk/R4/ConceptMap/NICIP-SCT").setVersion(version)
				.setTitle("NHS NICIP to SNOMED CT Map").setStatus(PublicationStatus.ACTIVE).setExperimental(false)
				.setPublisher("NHS UK").setSource(new UrlType().setValue("https://fhir.hl7.org.uk/R4/ValueSet/NICIP"))
				.setTarget(new UrlType().setValue("http://snomed.info/sct?fhir_vs"));

		List<ConceptMapGroupComponent> groups = new ArrayList<ConceptMapGroupComponent>();
		ConceptMapGroupComponent group = new ConceptMapGroupComponent();
		group.setSource("https://fhir.hl7.org.uk/R4/CodeSystem/NICIP");
		group.setSourceVersion(version);
		group.setTarget("http://snomed.info/sct");
		group.setTargetVersion(sctVersion);

		List<SourceElementComponent> elements = new ArrayList<ConceptMap.SourceElementComponent>();
		group.setElement(elements);
		groups.add(group);
		conceptMap.setGroup(groups);

		return conceptMap;

	}

	private ConceptMap initOPCSMap(String version, String opcsVersion) {
		ConceptMap conceptMap = new ConceptMap();
		conceptMap.setId("NICIP-OPCS-MAP-" + version);
		conceptMap.setUrl("http://fhir.hl7.org.uk/R4/ConceptMap/NICIP-OPCS").setVersion(version)
				.setTitle("NHS NICIP to OPCS Map").setStatus(PublicationStatus.ACTIVE).setExperimental(false)
				.setPublisher("NHS UK").setSource(new UrlType().setValue("http://fhir.hl7.org.uk/R4/ValueSet/NICIP"))
				.setTarget(new UrlType().setValue("http://fhir.hl7.org.uk/R4/ValueSet/OPCS-4"));

		List<ConceptMapGroupComponent> groups = new ArrayList<ConceptMapGroupComponent>();
		ConceptMapGroupComponent group = new ConceptMapGroupComponent();

		group.setSource("https://fhir.hl7.org.uk/R4/CodeSystem/NICIP");
		group.setSourceVersion(version);
		group.setTarget("http://fhir.hl7.org.uk/R4/CodeSystem/OPCS-4");
		group.setTargetVersion(opcsVersion);
		List<SourceElementComponent> elements = new ArrayList<ConceptMap.SourceElementComponent>();
		group.setElement(elements);
		groups.add(group);
		conceptMap.setGroup(groups);

		return conceptMap;

	}

}
