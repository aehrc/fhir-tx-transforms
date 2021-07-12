/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.commons.codec.digest.DigestUtils;
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
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;
import ca.uhn.fhir.context.FhirContext;

public class ODSParser {

	public void processCodeSystemWithUpdate(String zipFile, String version, String outFolder, String txServerUrl, FeedClient feedClient)
			throws IOException, ParserConfigurationException, SAXException {

		System.out.println("Process ODS version - " + version);
		String primaryFileName = "CodeSystem-ODS-PrimaryRoleScope-" + version + ".json";
		String organisationRoleFileName = "CodeSystem-ODS-OrganisationRole-" + version + ".json";
		String organisationRecordClassFileName = "CodeSystem-ODS-OrganisationRecordClass-" + version + ".json";
		String organisationRelationshipFileName = "CodeSystem-ODS-Organisatio Relationship-" + version + ".json";
		File outFile_PrimaryRole = new File(outFolder,  primaryFileName);
		File outFile_OrganisationRole = new File(outFolder , organisationRoleFileName);
		File outFile_OrganisationRecordClass = new File(outFolder ,organisationRecordClassFileName);
		File outFile_OrganisationRelationship =  new File(outFolder ,  organisationRelationshipFileName);

		// Data

		List<Pair<String, String>> primaryRoleScope = new ArrayList<Pair<String, String>>();
		List<Triplet<String, String, String>> organisationRole = new ArrayList<Triplet<String, String, String>>();
		List<Triplet<String, String, String>> organisationRecordClass = new ArrayList<Triplet<String, String, String>>();
		List<Triplet<String, String, String>> organisationRelationship = new ArrayList<Triplet<String, String, String>>();
		
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

								case "OrganisationRecordClass":
									organisationRecordClass.add(new Triplet<String, String, String>(concept.getAttribute("id"),
											concept.getAttribute("code"), concept.getAttribute("displayName")));
									break;

								case "OrganisationRelationship":
									organisationRelationship.add(new Triplet<String, String, String>(concept.getAttribute("id"),
											concept.getAttribute("code"), concept.getAttribute("displayName")));
									break;
								}
							}
						}
						System.out.printf("Add %s Items in Orgnisation Role \n", organisationRole.size());
						System.out.printf("Add %s Items in Orgnisation Record Class \n", organisationRecordClass.size());
						System.out.printf("Add %s Items in Orgnisation Relationship \n", organisationRelationship.size());
					}
				}
			}
		}

		zip.close();

		// Create Code System

		CodeSystem codeSystem_PrimaryRoleScope = createTemplate("ODS Primary Role Scope", version);

		for (Pair<String, String> pair : primaryRoleScope) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(pair.getValue0());
			concept.setDisplay(pair.getValue1());
			codeSystem_PrimaryRoleScope.getConcept().add(concept);
		}

		CodeSystem codeSystem_OrganisationRole = createTemplate("ODS Organisation Role", version);

		for (Triplet<String, String, String> triplet : organisationRole) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(triplet.getValue0());
			concept.setDisplay(triplet.getValue2());
			codeSystem_OrganisationRole.getConcept().add(concept);
		}

		CodeSystem codeSystem_OrganisationRecordClass = createTemplate("ODS Organisation Record Class", version);

		for (Triplet<String, String, String> triplet : organisationRecordClass) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(triplet.getValue0());
			concept.setDisplay(triplet.getValue2());
			codeSystem_OrganisationRecordClass.getConcept().add(concept);
		}

		CodeSystem codeSystem_OrganisationRelationship = createTemplate("ODS Organisation Relationship", version);

		for (Triplet<String, String, String> triplet : organisationRelationship) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			concept.setCode(triplet.getValue0());
			concept.setDisplay(triplet.getValue2());
			codeSystem_OrganisationRelationship.getConcept().add(concept);
		}
		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem_PrimaryRoleScope),
				outFile_PrimaryRole);
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem_OrganisationRole),
				outFile_OrganisationRole);
		Utility.toTextFile(
				ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem_OrganisationRecordClass),
				outFile_OrganisationRecordClass);
		Utility.toTextFile(
				ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem_OrganisationRelationship),
				outFile_OrganisationRelationship);
		
		// out to Fhir r4
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(codeSystem_PrimaryRoleScope);
			fhirClientR4.createUpdateCodeSystem(codeSystem_OrganisationRole);
			fhirClientR4.createUpdateCodeSystem(codeSystem_OrganisationRecordClass);
			fhirClientR4.createUpdateCodeSystem(codeSystem_OrganisationRelationship);
		}
		if (feedClient != null) {
			createFeedEntry(outFolder, feedClient, primaryFileName, codeSystem_PrimaryRoleScope);
			createFeedEntry(outFolder, feedClient, organisationRoleFileName, codeSystem_OrganisationRole);
			createFeedEntry(outFolder, feedClient, organisationRecordClassFileName, codeSystem_OrganisationRecordClass);
			createFeedEntry(outFolder, feedClient, organisationRelationshipFileName, codeSystem_OrganisationRelationship);
		}
		

	}

	private void createFeedEntry(String outFolder, FeedClient feedClient, String primaryFileName,
			CodeSystem codeSystem_PrimaryRoleScope) throws IOException {
		Entry entry = FeedUtility.createFeedEntry_CodeSystem(codeSystem_PrimaryRoleScope, primaryFileName);
		String entryFileName = Utility.jsonFileNameToEntry(primaryFileName);
		Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder, entryFileName));
		feedClient.createEntryToNHSFeed(new File(outFolder,entryFileName), new File(outFolder,primaryFileName));
	}

	private CodeSystem createTemplate(String title, String version) {
		String name = title.replaceAll("\\s", "_");
		String url = title.replaceAll("\\s", "-");
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setUrl("http://fhir.nhs.uk/CodeSystem/" + url);
		codeSystem.setValueSet("http://fhir.nhs.uk/CodeSystem/" + url + "/vs");
		codeSystem.setId(url + "-" + version);
		codeSystem.setName(name);
		codeSystem.setVersion(version);
		codeSystem.setTitle(title);
		codeSystem.setDescription(title + " FHIR CodeSystem");
		codeSystem.setStatus(PublicationStatus.ACTIVE);
		codeSystem.setExperimental(false);
		codeSystem.setCopyright(
				"Copyright © 2020 Health and Social Care Information Centre. NHS Digital is the trading name of the Health and Social Care Information Centre.");
		codeSystem.setPublisher("NHS Digital");
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();
		codeSystem.setConcept(concepts);
		return codeSystem;
	}

}
