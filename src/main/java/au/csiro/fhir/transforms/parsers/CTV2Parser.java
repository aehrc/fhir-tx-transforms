/*
 * Copyright © 2020, Commonwealth Scientific and Industrial Research Organization (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemHierarchyMeaning;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.StringType;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.Utility;
import ca.uhn.fhir.context.FhirContext;

class Read2Term {
	String id;
	String firstDate;
	String lastDate;
	String text;
	String text60;
	String text198;

	public Read2Term(String id, String firstDate, String text) {
		super();
		this.id = id;
		this.firstDate = firstDate;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public String getText60() {
		return text60;
	}

	public void setText60(String text60) {
		this.text60 = text60;
	}

	public String getText198() {
		return text198;
	}

	public void setText198(String text198) {
		this.text198 = text198;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	
	public String getLongest(){
		if(text198 != null) return text198;
		else if(text60 != null) return text60;
		else return text;
	}
	
	public Set<String> getotherText(){
		Set<String> other = new HashSet<String>();
		if(text198 != null) {
			other.add(text);
			other.add(text60);
		}
		else if(text60 != null) {
			other.add(text);
		}
		return other;
	}

}

class Read2Concept {
	String codeID;
	List<Read2Term> terms;
	Read2Concept parent;
	Set<String> synnonyms;

	public List<Read2Term> getTerms() {
		return terms;
	}

	public void addTerm(Read2Term term) {
		terms.add(term);
		synnonyms.add(term.getText());
		if (term.getText60() != null && term.getText60().length() > 0)
			synnonyms.add(term.getText60());
		if (term.getText198() != null && term.getText198().length() > 0)
			synnonyms.add(term.getText198());
	}

	public Set<String> getSynnonyms() {
		return synnonyms;
	}

	public Read2Concept(String codeID) {
		super();
		this.codeID = codeID;
		this.terms = new ArrayList<Read2Term>();
		this.synnonyms = new HashSet<String>();
	}

	public String getCodeID() {
		return codeID;
	}

	public Read2Concept getParent() {
		return parent;
	}

	public void setParent(Read2Concept parent) {
		this.parent = parent;
	}

	public void setTerms(List<Read2Term> terms) {
		this.terms = terms;
	}

	public void setCodeID(String codeID) {
		this.codeID = codeID;
	}

	@Override
	public int hashCode() {
		return codeID.hashCode();
	}

}

public class CTV2Parser {

	private CodeSystem processCodeSystem( String termFile, String version, String outFolder)
			throws IOException, ParseException {
		Map<String, Read2Concept> concepts = new HashMap<String, Read2Concept>();
		String outFile = outFolder == null ? null : outFolder + "\\CodeSystem - READ V2 With Term " + version + ".json";

		for (String line : Utility.readTxtFile(termFile, true)) {
			String[] parts = line.split("\t");
			
				String id = parts[0];
				String termCode = parts[1].trim();
				String text = parts[2].trim();
				String text60 = parts[3].trim();
				String text198 = parts[4].trim();
				String firstDate = parts[5].trim();
				String lastDate = parts[6].trim();

				Read2Term term = new Read2Term(termCode, firstDate, text);
				
				if (text60 != null && text60.length() > 0)
					term.setText60(text60);
				if (text198 != null && text198.length() > 0)
					term.setText198(text198);
				if (!concepts.containsKey(id)) {
					Read2Concept read2Concept = new Read2Concept(id);
					concepts.put(id, read2Concept);
				}
				if(lastDate.length() >0) {
					term.setLastDate(lastDate);
				}
				Read2Concept c = concepts.get(id);
				c.addTerm(term);


		}
		System.out.println("Total Code from Term File " + concepts.size());
		// Add parent
		for (Read2Concept c : concepts.values()) {
			String codeID = c.getCodeID();
			if (!codeID.endsWith("....")) {
				if (codeID.endsWith("...")) {
					String pID = codeID.subSequence(0, 1) + "....";
					c.setParent(concepts.get(pID));
					System.out.println(codeID + "\t" + pID);
				} else if (codeID.endsWith("..")) {
					String pID = codeID.subSequence(0, 2) + "...";
					c.setParent(concepts.get(pID));
					System.out.println(codeID + "\t" + pID);
				} else if (codeID.endsWith(".")) {
					String pID = codeID.subSequence(0, 3) + "..";
					c.setParent(concepts.get(pID));
					System.out.println(codeID + "\t" + pID);
				} else {
					String pID = codeID.subSequence(0, 4) + ".";
					c.setParent(concepts.get(pID));
					System.out.println(codeID + "\t" + pID);
				}
			}
		}

		CodeSystem codeSystem = new CodeSystem();

		SimpleDateFormat f = new SimpleDateFormat("yyyymmdd");
	    Date d = f.parse(version);
	    Identifier ident = new Identifier();
	    ident.setSystem("urn:ietf:rfc:3986");
	    ident.setValue("urn:oid: 2.16.840.1.113883.6.29");

		codeSystem.setId("Read-Code-Clinical-Term-Version-2");
		codeSystem.addIdentifier(ident);
		codeSystem.setUrl("http://read.info/readv2").setValueSet("http://read.info/readv2/ValueSet")
				.setDate(d)
				.setName("NHS_Read_Code_Version_2_Code_System").setVersion(version).setTitle("NHS Read Code Version 2")
				.setStatus(PublicationStatus.ACTIVE).setExperimental(false).setPublisher("NHS UK")
				.setContent(CodeSystemContentMode.COMPLETE)
				.setHierarchyMeaning(CodeSystemHierarchyMeaning.CLASSIFIEDWITH);
		PropertyComponent propertyComponent_status = new PropertyComponent();
		propertyComponent_status.setCode("status").setDescription("Concept Status").setType(PropertyType.STRING);
		
		codeSystem.addProperty(new PropertyComponent().setType(PropertyType.STRING).setCode("termCode")
				.setDescription("The Read V2 term code"));
		codeSystem.addProperty(new PropertyComponent().setType(PropertyType.DATETIME).setCode("firstDate")
				.setDescription("The Read V2 term code first date"));
		codeSystem.addProperty(new PropertyComponent().setType(PropertyType.DATETIME).setCode("lastDate")
				.setDescription("The Read V2 term code last date"));

		List<ConceptDefinitionComponent> conceptsList = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		for (Map.Entry<String, Read2Concept> entry : concepts.entrySet()) {
			ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
			Read2Concept con = entry.getValue();
			boolean inactive = true;
			concept.setCode(entry.getKey());
			if (con.getParent() != null) {
				ConceptPropertyComponent property_parent = new ConceptPropertyComponent(new CodeType("parent"),
						new CodeType(con.getParent().getCodeID()));
				concept.addProperty(property_parent);
			}
			
			// Check if the concept is inactive 
			for(Read2Term term : con.getTerms()) {
				if(term.getId().equals("00") ) {
					if(term.getLastDate()==null) {
						inactive = false;
					}
				}
			}
			if(inactive) {
				for(Read2Term term : con.getTerms()) {
					if(term.getId().equals("00") ) {
						concept.setDisplay(term.getLongest());
						concept.addProperty(new ConceptPropertyComponent(new CodeType("inactive"),new BooleanType("true")));
						for(String s : term.getotherText()) {
							ConceptDefinitionDesignationComponent def = new ConceptDefinitionDesignationComponent();
							def.setLanguageElement(new CodeType("en"));
							def.setUse(new Coding("http://snomed.info/sct", "900000000000013009", "Synonym"));
							def.setValue(s);
							concept.addDesignation(def);
						}
						conceptsList.add(concept);
					}
				}
			}
			else {
				for(Read2Term term : con.getTerms()) {
					if(term.getId().equals("00") && term.getLastDate() ==null ) {
						concept.setDisplay(term.getLongest());
						for(String s : term.getotherText()) {
							ConceptDefinitionDesignationComponent def = new ConceptDefinitionDesignationComponent();
							def.setLanguageElement(new CodeType("en"));
							def.setUse(new Coding("http://snomed.info/sct", "900000000000013009", "Synonym"));
							def.setValue(s);
							concept.addDesignation(def);
						}
						conceptsList.add(concept);
					}
				}
				
				for(Read2Term term : con.getTerms()) {
					if(term.getLastDate() ==null) {
						ConceptDefinitionComponent term_concept = new ConceptDefinitionComponent();
						term_concept.setCode(con.getCodeID() + term.getId());
						ConceptPropertyComponent property_parent = new ConceptPropertyComponent(new CodeType("parent"),
								new CodeType(con.getCodeID()));
						term_concept.addProperty(property_parent);
						term_concept.addProperty(new ConceptPropertyComponent(new CodeType("termCode"),new StringType(term.getId())));
						term_concept.addProperty(new ConceptPropertyComponent(new CodeType("firstDate"),new DateTimeType(formatDateTime(term.getFirstDate()))));
						term_concept.setDisplay(term.getLongest());
						for(String s : term.getotherText()) {
							ConceptDefinitionDesignationComponent def = new ConceptDefinitionDesignationComponent();
							def.setLanguageElement(new CodeType("en"));
							def.setUse(new Coding("http://snomed.info/sct", "900000000000013009", "Synonym"));
							def.setValue(s);
							term_concept.addDesignation(def);
						}
						
						conceptsList.add(term_concept);
					}
					
				}
			}
			
		}
		codeSystem.setConcept(conceptsList);

		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem), outFile);

		return codeSystem;
	}

	public void processCodeSystemWithUpdate(String termFile, String version, String outFolder,
			String txServerUrl) throws IOException, ParseException {
		CodeSystem cs = processCodeSystem(termFile, version, outFolder);
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);
		}
	}
	
	private String formatDateTime(String input) {
		return input.substring(0,4) + "-" + input.substring(4);
	}

}
