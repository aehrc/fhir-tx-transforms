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

public class OPCSParser {

	public CodeSystem processCodeSystem(String codeFile, String metaFile, String version, String outFolder)
			throws IOException {

		System.out.println("Process OPCS version " + version);
		final String outFile = outFolder == null ? null : outFolder + "\\Code System - OPCS-4 - " + version + ".json";

		Map<String, String> allCodes = new HashMap<String, String>();
		Set<String> validCode = new HashSet<String>();

		for (String line : Utility.readTxtFile(codeFile, false)) {
			String[] parts = line.split("\\t");
			if (parts.length > 0) {

				allCodes.put(parts[0].replaceAll("\\.", ""), parts[1]);
			}
		}

		for (String line : Utility.readTxtFile(metaFile, false)) {

			if (line.length() > 0) {
				validCode.add(line.substring(12, 16).trim());
			}
		}

		System.out.printf("Total load code - %s\n", allCodes.size());

		System.out.printf("Total valid code - %s\n", validCode.size());

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

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		for (Map.Entry<String, String> entry : allCodes.entrySet()) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(entry.getKey());
			concept.setDisplay(entry.getValue());
			if (validCode.contains(entry.getKey())) {
				ConceptPropertyComponent property = new ConceptPropertyComponent(new CodeType("valid"),
						new BooleanType().setValue(true));
				concept.addProperty(property);
			} else {
				ConceptPropertyComponent property = new ConceptPropertyComponent(new CodeType("valid"),
						new BooleanType().setValue(false));
				concept.addProperty(property);
			}

			concepts.add(concept);
		}

		codeSystem.setConcept(concepts);

		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem), outFile);

		return codeSystem;
	}

	public void processCodeSystemWithUpdate(String codeFile, String metaFile, String version, String outFolder,
			String txServerUrl) throws IOException {
		CodeSystem cs = processCodeSystem(codeFile, metaFile, version, outFolder);
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);

		}

	}

}
