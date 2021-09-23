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
import org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.r4.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.StringType;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;

import org.hl7.fhir.r4.model.UrlType;

import ca.uhn.fhir.context.FhirContext;

public class NICIPParser {

	Logger logger = Logger.getLogger(NICIPParser.class.toString());

	public void processResourceWithUpdate(String packageFolder, String version, String outFolder, String txServerUrl,
			FeedClient feedClient) throws IOException {

		File codeListFile = new File(packageFolder, "NICIP_Code_Set_withSCT_" + version + ".txt");
		File opcsMapFile = new File(packageFolder, "NICIP-OPCS-4_MappingTable_" + version + ".txt");

		/**
		 * Fix for naming error
		 */
		if (version.equals("20190601") || version.equals("20200401")) {
			opcsMapFile = new File(packageFolder, "NICIP-OPCS_MappingTable_" + version + ".txt");
		}

		CodeSystem codeSystem = initCodeSystem(version);
		ConceptMap conceptMap_sct = initSctMap(version, "");
		ConceptMap conceptMap_opcs = initOPCSMap(version, "48");

		List<SourceElementComponent> elements = conceptMap_sct.getGroup().get(0).getElement();

		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, CodeSystem.ConceptDefinitionComponent>();

		Set<String> conceptIDs = new HashSet<String>();

		for (String line : Utility.readTxtFile(codeListFile, true)) {
			String[] parts = line.split("\\t");
			String status = parts[7];
			if (status.equals("A") || status.equals("R") ) {
				boolean diag = parts[9].equals("Y") ? true : false;
				boolean intervention = parts[10].equals("Y") ? true : false;
				String nicipID = parts[0];
				conceptIDs.add(nicipID);
				String shortCode = parts[5];
				String preferredName = parts[12];

				if (!conceptRegister.containsKey(shortCode)) {
					conceptRegister.put(shortCode, new ConceptDefinitionComponent());
				} else {
					logger.info("Duplicate " + status + " concept " + shortCode);
				}

				ConceptDefinitionComponent concept = conceptRegister.get(shortCode);
				concept.setCode(shortCode);
				concept.setDisplay(preferredName);
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Status"), new CodeType().setValue(status)));
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Diagnostic"), new BooleanType().setValue(diag)));
				concept.addProperty(new ConceptPropertyComponent(new CodeType("Interventional"),
						new BooleanType().setValue(intervention)));
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Short Code"), new StringType().setValue(shortCode)));

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
				if (parts.length > 16) {
					concept.addDesignation(
							new ConceptDefinitionDesignationComponent().setUse(coding).setValue(parts[16]));
				}

			}

		}

		for (ConceptDefinitionComponent c : conceptRegister.values()) {
			codeSystem.getConcept().add(c);
		}

		System.out.println("Total NICIP concept imported " + conceptRegister.size());

		for (String line : Utility.readTxtFile(codeListFile, true)) {
			String[] parts = line.split("\\t");
			String status = parts[7];
			if (status.equals("A")) {
				String sctID = parts[1];
				String sctName = parts[2];
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
			TargetElementComponent target = new TargetElementComponent().setCode(parts[4]).setEquivalence(ConceptMapEquivalence.EQUAL);
			
			
			
			if(parts.length > 12 && parts[12]!=null && parts[12].length() >0) {
				OtherElementComponent otherMap = new OtherElementComponent();
				otherMap.setValue(parts[12]);
				otherMap.setProperty("http://fhir.hl7.org.uk/CodeSystem/OPCS-4");
				target.addProduct(otherMap);
			}
			if(parts.length > 20 && parts[20]!=null && parts[20].length() >0) {
				OtherElementComponent otherMap = new OtherElementComponent();
				otherMap.setValue(parts[20]);
				otherMap.setProperty("http://fhir.hl7.org.uk/CodeSystem/OPCS-4");
				target.addProduct(otherMap);
			}
			if(parts.length > 30 && parts[30]!=null && parts[30].length() >0) {
				OtherElementComponent otherMap = new OtherElementComponent();
				otherMap.setValue(parts[30]);
				otherMap.setProperty("http://fhir.hl7.org.uk/CodeSystem/OPCS-4");
				target.addProduct(otherMap);
			}
					
		
			source.addTarget(target);
			
			elements_opcs_map.add(source);
		}

		FhirContext ctx = FhirContext.forR4();

		String codeSystemFileName = "CodeSystem-NICIP-" + version + ".json";
		File codeSystemFile = new File(outFolder, codeSystemFileName);

		String concpetMapSctFileName = "ConceptMap-NICIP-SNOMEDCT-" + version + ".json";
		File concpetMapSctFile = new File(outFolder, concpetMapSctFileName);

		String concpetMapOpcsFileName = "ConceptMap-NICIP-OPCS-" + version + ".json";
		File concpetMapSOpcsFile = new File(outFolder, concpetMapOpcsFileName);

		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem), codeSystemFile);
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(conceptMap_sct),
				concpetMapSctFile);
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(conceptMap_opcs),
				concpetMapSOpcsFile);

		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(codeSystem);
			fhirClientR4.createUpdateConceptMap(conceptMap_sct);
			fhirClientR4.createUpdateConceptMap(conceptMap_opcs);

		}

		if (feedClient != null) {

			Entry entry = FeedUtility.createFeedEntry_CodeSystem(codeSystem, codeSystemFileName);
			String entryFileName = Utility.jsonFileNameToEntry(codeSystemFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder, entryFileName));
			feedClient.updateEntryToNHSFeed(entry, new File(outFolder, entryFileName),
					new File(outFolder, codeSystemFileName));


			Entry entryMapToSCT = FeedUtility.createFeedEntry_ConceptMap(conceptMap_sct, concpetMapSctFileName);
			String entryMapToSCTFileName = Utility.jsonFileNameToEntry(concpetMapSctFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entryMapToSCT), new File(outFolder, entryMapToSCTFileName));
			feedClient.updateEntryToNHSFeed(entryMapToSCT, new File(outFolder, entryMapToSCTFileName),
					new File(outFolder, concpetMapSctFileName));


			Entry entryMapToOpcs = FeedUtility.createFeedEntry_ConceptMap(conceptMap_opcs, concpetMapOpcsFileName);
			String entryMapToOpcsFileName = Utility.jsonFileNameToEntry(concpetMapOpcsFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entryMapToOpcs), new File(outFolder, entryMapToOpcsFileName));
			feedClient.updateEntryToNHSFeed(entryMapToOpcs, new File(outFolder, entryMapToOpcsFileName),
					new File(outFolder, concpetMapOpcsFileName));

		}

	}

	private CodeSystem initCodeSystem(String version) {
		String url = "http://digital.nhs.uk/fhir/CodeSystem/nicip";
		String title = "NICIP";
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId(title.replaceAll("\\s", "-") + version);
		codeSystem.setName(title.replaceAll("\\s", "_")).setUrl(url)
				.setValueSet("http://digital.nhs.uk/fhir/ValueSet/nicip").setVersion(version).setTitle(title)
				.setStatus(PublicationStatus.DRAFT).setExperimental(false)
				.setCopyright(
						"Copyright © 2020 Health and Social Care Information Centre. NHS Digital is the trading name of the Health and Social Care Information Centre.")
				.setPublisher("NHS Digital").setDescription(title + " FHIR CodeSystem")
				.setContent(CodeSystemContentMode.COMPLETE);

		codeSystem.addProperty(new PropertyComponent().setCode("Diagnostic").setDescription("Diagnostic")
				.setType(PropertyType.BOOLEAN));
		codeSystem.addProperty(new PropertyComponent().setCode("Interventional").setDescription("Interventional")
				.setType(PropertyType.BOOLEAN));
		codeSystem.addProperty(new PropertyComponent().setCode("Short Code").setDescription("Short Code")
				.setType(PropertyType.STRING));

		codeSystem.addProperty(new PropertyComponent().setCode("Status").setDescription("NICIP Status")
				.setType(PropertyType.CODE));
		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		codeSystem.setConcept(concepts);

		return codeSystem;

	}

	private ConceptMap initSctMap(String version, String sctVersion) {
		ConceptMap conceptMap = new ConceptMap();
		String title = "NHS NICIP to SNOMED CT Map";
		conceptMap.setId("NICIP-SNOMED-MAP-" + version);
		conceptMap.setUrl("http://digital.nhs.uk/fhir/ConceptMap/nicip-sct").setName(title.replaceAll("\\s", "_"))
				.setVersion(version).setTitle(title).setStatus(PublicationStatus.DRAFT).setExperimental(false)
				.setCopyright(
						"Copyright © 2020 Health and Social Care Information Centre. NHS Digital is the trading name of the Health and Social Care Information Centre.")
				.setPublisher("NHS Digital").setDescription(title + " FHIR ConceptMap")
				.setSource(new UrlType().setValue("http://digital.nhs.uk/fhir/ValueSet/nicip"))
				.setTarget(new UrlType().setValue("http://snomed.info/sct?fhir_vs"));

		List<ConceptMapGroupComponent> groups = new ArrayList<ConceptMapGroupComponent>();
		ConceptMapGroupComponent group = new ConceptMapGroupComponent();
		group.setSource("http://digital.nhs.uk/fhir/CodeSystem/nicip");
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
		String title = "NHS NICIP to OPCS Map";
		ConceptMap conceptMap = new ConceptMap();
		conceptMap.setId("NICIP-OPCS-MAP-" + version);
		conceptMap.setUrl("http://digital.nhs.uk/fhir/ConceptMap/nicip-opcs").setVersion(version).setTitle(title)
				.setStatus(PublicationStatus.DRAFT).setExperimental(false)
				.setCopyright(
						"Copyright © 2020 Health and Social Care Information Centre. NHS Digital is the trading name of the Health and Social Care Information Centre.")
				.setPublisher("NHS Digital").setDescription(title + " FHIR ConceptMap")
				.setSource(new UrlType().setValue("http://digital.nhs.uk/fhir/ValueSet/nicip"))
				.setTarget(new UrlType().setValue("http://fhir.hl7.org.uk/CodeSystem/OPCS-4/vs"));

		List<ConceptMapGroupComponent> groups = new ArrayList<ConceptMapGroupComponent>();
		ConceptMapGroupComponent group = new ConceptMapGroupComponent();

		group.setSource("http://digital.nhs.uk/fhir/CodeSystem/nicip");
		group.setSourceVersion(version);
		group.setTarget("http://fhir.hl7.org.uk/CodeSystem/OPCS-4");
		group.setTargetVersion(opcsVersion);
		List<SourceElementComponent> elements = new ArrayList<ConceptMap.SourceElementComponent>();
		group.setElement(elements);
		groups.add(group);
		conceptMap.setGroup(groups);

		return conceptMap;

	}

}
