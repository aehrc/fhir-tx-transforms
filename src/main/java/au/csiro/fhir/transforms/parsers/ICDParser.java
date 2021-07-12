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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;

import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.utilities.DateTimeUtil;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import edu.emory.mathcs.backport.java.util.Arrays;

public class ICDParser {
	class ICDCode implements Comparable<ICDCode> {
		int level;
		String codeID;
		String codeIDNoDot;
		String altCode;
		String desc;
		String usage;
		String uk_usage;
		String modifier4;
		String modifier5;
		Set<String> qualifier;
		String gendermask;
		String minAge;
		String maxAge;
		String treeDescription;
		
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
			return this.codeIDNoDot.compareTo(o.codeIDNoDot);
		}

	}

	Map<String, ICDCode> allIcd = new HashMap<>();

	List<ICDCode> roots = new ArrayList<ICDCode>();

	public void processCodeSystemWithUpdate(String conFile, String version, String outFolder, String txServerUrl, FeedClient feedClient)
			throws IOException, ParseException {
		CodeSystem cs = processCodeSystem(conFile, version, outFolder);
		
		String outFileName =  "CodeSystem-ICDUK-" + version + ".json";
		File outFile = new File(outFolder ,outFileName);
		
		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(cs), outFile);
		
		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystem(cs);
		}
		
		if (feedClient != null) {
			Entry entry = FeedUtility.createFeedEntry_CodeSystem(cs, outFileName);
			String entryFileName = Utility.jsonFileNameToEntry(outFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder,  entryFileName));
			feedClient.updateEntryToNHSFeed(entry, new File(outFolder,entryFileName), new File(outFolder,outFileName));
		}
	}

	private CodeSystem processCodeSystem(String codeFile, String version, String outFolder) throws IOException, ParseException {
		
		System.out.println("Process ICD 10 UK version - " + version);

		CodeSystem codeSystem = initCodeSystem(version);

		for (String line : Utility.readTxtFile(codeFile, true)) {
			String[] parts = line.split("\\t");
			ICDCode icdCode = new ICDCode();
			icdCode.altCode = parts[1];
			icdCode.codeIDNoDot = parts[1];
			icdCode.codeID = parts[0];
			icdCode.desc = parts[4];
			icdCode.usage = parts[2];
			icdCode.uk_usage = parts[3];
			if(parts.length >5  && parts[5].length() > 0) {icdCode.modifier4 = parts[5];}
			
			if(parts.length >6 && parts[6].length() > 0) {icdCode.modifier5 = parts[6];}
			if(parts.length >7  && parts[7].length() > 0) {
				icdCode.qualifier = new HashSet<String>(Arrays.asList(parts[7].split("\\^")));
			}
			if(parts.length >8 && parts[8].length() > 0)icdCode.gendermask = parts[8];
			if(parts.length >9  && parts[9].length() > 0)icdCode.minAge = parts[9];
			if(parts.length >10 && parts[10].length() > 0)icdCode.maxAge = parts[10];
			if(parts.length >11 && parts[11].length() > 0)icdCode.treeDescription = parts[11];
			
			if(icdCode.modifier4!=null) {
				icdCode.desc+= " " + icdCode.modifier4;
			}
			if(icdCode.modifier5!=null ) {
				icdCode.desc+= " " + icdCode.modifier5;
			}
			allIcd.put(icdCode.codeIDNoDot, icdCode);
		}
		
		System.out.println("Total ICD Code count " + allIcd.size());

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
		return codeSystem;

	}

	private ConceptDefinitionComponent buildConceptFromICD(ICDCode icdCode) {
		ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
		concept.setCode(icdCode.codeID);
		concept.setDisplay(icdCode.desc);
		concept.addProperty(
				new ConceptPropertyComponent(new CodeType("Usage UK"), new StringType().setValue(icdCode.uk_usage)));
		concept.addProperty(
				new ConceptPropertyComponent(new CodeType("Alt Code"), new StringType().setValue(icdCode.altCode)));
		
		concept.addProperty(
				new ConceptPropertyComponent(new CodeType("Usage"), new StringType().setValue(icdCode.usage)));
		
		if(icdCode.modifier4!=null) {
			concept.addProperty(
					new ConceptPropertyComponent(new CodeType("Modifier 4"), new StringType().setValue(icdCode.modifier4)));
				
		}
		if(icdCode.modifier5!=null) {
			concept.addProperty(
					new ConceptPropertyComponent(new CodeType("Modifier 5"), new StringType().setValue(icdCode.modifier5)));
			
		}
		
		if(icdCode.qualifier!=null) {
			for(String s : icdCode.qualifier) {
				concept.addProperty(
						new ConceptPropertyComponent(new CodeType("Qualifiers"), new StringType().setValue(s)));
			}
			
			
		}
		
		if(icdCode.gendermask!=null) {
			concept.addProperty(
					new ConceptPropertyComponent(new CodeType("Gender Mask"), new StringType().setValue(icdCode.gendermask)));
			
		}
		
		if(icdCode.minAge!=null) {
			concept.addProperty(
					new ConceptPropertyComponent(new CodeType("Min Age"), new StringType().setValue(icdCode.minAge)));
			
		}
		if(icdCode.maxAge!=null) {
			concept.addProperty(
					new ConceptPropertyComponent(new CodeType("Max Age"), new StringType().setValue(icdCode.maxAge)));
			
		}
		
		if(icdCode.treeDescription!=null) {
			concept.addProperty(
					new ConceptPropertyComponent(new CodeType("Tree Description"), new StringType().setValue(icdCode.treeDescription)));
			
		}
		
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

	private CodeSystem initCodeSystem(String version) throws ParseException {
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId("ICD-10-UK-"+version);
		DateTimeType dt = new DateTimeType();
		dt.setValueAsString(version.equals("5.0")?"2016-04-01":(version.equals("4.0")?"2012-04-01":null));

		codeSystem.setUrl("http://hl7.org/fhir/sid/icd-10-uk")
				.setValueSet("http://hl7.org/fhir/sid/icd-10-uk/vs" + "|" + version)
				.setDateElement(dt)
				.setName("ICD_10_UK")
				.setVersion(version)
				.setTitle("ICD 10 UK")
				.setStatus(PublicationStatus.ACTIVE)
				.setExperimental(false)
				.setContent(CodeSystemContentMode.COMPLETE)
				.setCopyright(
						"Copyright © 2020 Health and Social Care Information Centre. NHS Digital is the trading name of the Health and Social Care Information Centre.")
				.setPublisher("NHS Digital")
				.setDescription("ICD 10 UK FHIR CodeSystem");

		codeSystem.addProperty(
				new PropertyComponent().setCode("Usage UK").setDescription("Usage UK").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Alt Code").setDescription("UK id").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Usage").setDescription("Usage").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Modifier 4").setDescription("Modifier 4").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Modifier 5").setDescription("Modifier 5").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Qualifiers").setDescription("Qualifiers").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Gender Mask").setDescription("Gender Mask").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Min Age").setDescription("Min Age").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Max Age").setDescription("Max Age").setType(PropertyType.STRING));
		codeSystem.addProperty(
				new PropertyComponent().setCode("Tree Description").setDescription("Tree Description").setType(PropertyType.STRING));

		List<ConceptDefinitionComponent> concepts = new ArrayList<CodeSystem.ConceptDefinitionComponent>();

		codeSystem.setConcept(concepts);

		return codeSystem;

	}

}
