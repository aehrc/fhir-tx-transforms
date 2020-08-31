/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.context.FhirContext;

public class ICDParser {
	class ICDCode implements Comparable<ICDCode> {
		int level;
		String codeID;
		String codeIDUK;
		String desc;
		String uk_usage;
		List<ICDCode> children;

		public ICDCode() {
			children = new ArrayList<ICDParser.ICDCode>();
		}

		@Override
		public int hashCode() {

			return codeID.hashCode();
		}

		@Override
		public int compareTo(ICDCode o) {

			return this.codeIDUK.compareTo(o.codeIDUK);
		}

	}

	Map<String, ICDCode> allIcd = new HashMap<>();

	List<ICDCode> roots = new ArrayList<ICDCode>();

	public void processCodeSystemWithUpdate(String conFile, String version, String outFolder, String txServerUrl)
			throws IOException {
		CodeSystem cs = processCodeSystem(conFile, version, outFolder);
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);
		}
	}

	private CodeSystem processCodeSystem(String codeFile, String version, String outFolder) throws IOException {

		final String outFile = outFolder == null ? null : outFolder + "\\CodeSystem - ICD UK " + version + ".json";

		System.out.println("Process ICD 10 UK version - " + version);

		CodeSystem codeSystem = initCodeSystem(version);

		for (String line : Utility.readTxtFile(codeFile, true)) {
			String[] parts = line.split("\\t");
			ICDCode icdCode = new ICDCode();
			icdCode.codeIDUK = parts[1];
			icdCode.codeID = parts[0];
			// icdCode.codeIDNoDot = parts[0].replaceAll("\\.", "");
			icdCode.desc = parts[4];
			icdCode.uk_usage = parts[3];
			allIcd.put(icdCode.codeIDUK, icdCode);
		}

		for (String id : allIcd.keySet()) {
			String pid = id.substring(0, id.length() - 1);
			if (allIcd.containsKey(pid)) {
				allIcd.get(pid).children.add(allIcd.get(id));
			} else {
				roots.add(allIcd.get(id));
			}

		}

		for (ICDCode root : getRoots()) {
			codeSystem.getConcept().add(buildConceptFromICD(root));
		}

		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem), outFile);

		return codeSystem;

	}

	private ConceptDefinitionComponent buildConceptFromICD(ICDCode icdCode) {
		ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
		concept.setCode(icdCode.codeID);
		concept.setDisplay(icdCode.desc);
		concept.addProperty(
				new ConceptPropertyComponent(new CodeType("UK usage"), new StringType().setValue(icdCode.uk_usage)));
		concept.addProperty(
				new ConceptPropertyComponent(new CodeType("UK id"), new StringType().setValue(icdCode.codeIDUK)));
		if (icdCode.children.size() > 0) {
			Collections.sort(icdCode.children);
			for (ICDCode c : icdCode.children) {
				concept.addConcept(buildConceptFromICD(c));
			}
		}
		return concept;
	}

	public List<ICDCode> getRoots() {

		Collections.sort(roots);
		return roots;
	}

	private CodeSystem initCodeSystem(String version) {
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId("ICD10-UK");
		codeSystem.setUrl("http://fhir.hl7.org.uk/R4/CodeSystem/ICD10UK")
				.setValueSet("https://fhir.hl7.org.uk/R4/ValueSet/ICD10UK").setName("NHS ICD 10 UK").setVersion(version)
				.setTitle("NHS ICD 10 UK").setStatus(PublicationStatus.ACTIVE).setExperimental(false)
				.setPublisher("NHS UK").setContent(CodeSystemContentMode.COMPLETE);

		codeSystem.addProperty(
				new PropertyComponent().setCode("UK usage").setDescription("UK usage").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("UK id").setDescription("UK id").setType(PropertyType.STRING));

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		codeSystem.setConcept(concepts);

		return codeSystem;

	}

}
