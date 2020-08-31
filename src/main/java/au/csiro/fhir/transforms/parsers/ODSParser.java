/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.Utility;
import ca.uhn.fhir.context.FhirContext;

public class ODSParser {

	public void processCodeSystemWithUpdate(String zipFile, String version, String outFolder, String txServerUrl)
			throws IOException, ParserConfigurationException, SAXException {

		System.out.println("Process ODS version - " + version);
		final String outFile_PrimaryRole = outFolder == null ? null
				: outFolder + "\\Code System - ODS - Primary Role " + version + ".json";
		final String outFile_OrganisationRole = outFolder == null ? null
				: outFolder + "\\Code System - ODS - Organisation Role " + version + ".json";

		// Data

		List<Pair<String, String>> primaryRoleScope = new ArrayList<Pair<String, String>>();
		List<Triplet<String, String, String>> organisationRole = new ArrayList<Triplet<String, String, String>>();
		// Process
		ZipFile zip = new ZipFile(zipFile);

		Enumeration<? extends ZipEntry> entries = zip.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.endsWith("xml")) {
				System.out.println(name);
				InputStream is = zip.getInputStream(entry);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				DocumentBuilder builder = factory.newDocumentBuilder();

				Document doc = builder.parse(is);

				Element element = doc.getDocumentElement();

				NodeList nodes = element.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					Node n = nodes.item(i);
					if (n.getNodeName().equalsIgnoreCase("Manifest")) {
						Element e = (Element) n;
						NodeList ml = e.getElementsByTagName("PrimaryRole");
						for (int j = 0; j < ml.getLength(); j++) {
							Element pr = (Element) ml.item(j);

							primaryRoleScope.add(
									new Pair<String, String>(pr.getAttribute("id"), pr.getAttribute("displayName")));

						}
						System.out.printf("Add %s Items in Primary Role Scope\n", primaryRoleScope.size());
					} else if (n.getNodeName().equalsIgnoreCase("CodeSystems")) {
						Element e = (Element) n;
						NodeList ml = e.getElementsByTagName("CodeSystem");
						for (int j = 0; j < ml.getLength(); j++) {
							Element cs = (Element) ml.item(j);
							String codeSysteName = cs.getAttribute("name");
							NodeList cl = cs.getElementsByTagName("concept");
							for (int k = 0; k < cl.getLength(); k++) {
								Element concept = (Element) cl.item(k);
								switch (codeSysteName) {
								case "OrganisationRole":
									organisationRole.add(new Triplet<String, String, String>(concept.getAttribute("id"),
											concept.getAttribute("code"), concept.getAttribute("displayName")));
									break;
								}
							}
						}
						System.out.printf("Add %s Items in Orgnisation Role \n", organisationRole.size());
					}
				}
			}
		}

		zip.close();

		// Create Code System
		// out to Fhir r4
		CodeSystem codeSystem_Primary = new CodeSystem();
		codeSystem_Primary.setUrl("https://fhir.nhs.uk/Id/ods-site-code");
		codeSystem_Primary.setValueSet("https://fhir.nhs.uk/Id/ods-site-code/ValueSet-" + version);
		codeSystem_Primary.setId("ODS-PrimaryRole-" + version);
		codeSystem_Primary.setName("NHS ODS Primary Role");
		codeSystem_Primary.setVersion(version);
		codeSystem_Primary.setTitle("NHS ODS Primary Role");
		codeSystem_Primary.setStatus(PublicationStatus.ACTIVE);
		codeSystem_Primary.setExperimental(false);
		codeSystem_Primary.setPublisher("NHS UK");
		codeSystem_Primary.setContent(CodeSystemContentMode.COMPLETE);
		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();
		for (Pair<String, String> pair : primaryRoleScope) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(pair.getValue0());
			concept.setDisplay(pair.getValue1());
			concepts.add(concept);
		}

		codeSystem_Primary.setConcept(concepts);

		CodeSystem codeSystem_Orginzation = new CodeSystem();
		codeSystem_Orginzation.setId("ODS-OrganisationRole-" + version);
		codeSystem_Orginzation.setUrl("https://fhir.nhs.uk/Id/ods-organization-code")
				.setValueSet("https://fhir.nhs.uk/Id/ods-organization-code/ValueSet-" + version)
				.setName("NHS ODS Organisation Role").setVersion(version).setTitle("ODS Organisation Role")
				.setStatus(PublicationStatus.ACTIVE).setExperimental(false).setPublisher("NHS UK")
				.setContent(CodeSystemContentMode.COMPLETE);

		concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		for (Triplet<String, String, String> triplet : organisationRole) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(triplet.getValue0());
			concept.setDisplay(triplet.getValue2());
			concepts.add(concept);
		}

		codeSystem_Orginzation.setConcept(concepts);
		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem_Primary),
				outFile_PrimaryRole);
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem_Orginzation),
				outFile_OrganisationRole);

		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(codeSystem_Primary);
			fhirClientR4.createUpdateCodeSystem(codeSystem_Orginzation);
		}
		;

	}

}
