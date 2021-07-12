/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.StringType;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;
import au.csiro.fhir.transforms.xml.dmd.v2_3.amp.ACTUALMEDICINALPRODUCTS;
import au.csiro.fhir.transforms.xml.dmd.v2_3.amp.AmpType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.amp.ApiType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.amp.AppProdInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.amp.LicRouteType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.ACTUALMEDICINALPRODPACKS;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.AmppType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.PackInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.PrescInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.PriceInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.ReimbInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ingredient.INGREDIENTSUBSTANCES;
import au.csiro.fhir.transforms.xml.dmd.v2_3.ingredient.INGREDIENTSUBSTANCES.ING;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.HistoryInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.InfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.LOOKUP;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.SupplierInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.ControlInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.DrugFormType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.DrugRouteType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.OntDrugFormType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.VIRTUALMEDPRODUCTS;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.VmpType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.VpiType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmpp.DtInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmpp.VIRTUALMEDPRODUCTPACK;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vmpp.VmppType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vtm.VIRTUALTHERAPEUTICMOIETIES;
import au.csiro.fhir.transforms.xml.dmd.v2_3.vtm.VIRTUALTHERAPEUTICMOIETIES.VTM;
import ca.uhn.fhir.context.FhirContext;

enum ConceptType {
	AMP("AMP", "Actual medicinal product", "10363901000001102"),
	AMPP("AMPP", "Actual medicinal product pack", "10364001000001104"),
	VMP("VMP", "Virtual medicinal product", "10363801000001108"),
	VMPP("VMPP", "Virtual medicinal product pack", "8653601000001108"),
	VTM("VTM", "Virtual therapeutic moiety", "10363701000001104"), INGREDIENT("INGREDIENT", "Ingredient", "105590001");

	private String name;
	private String fullName;
	private String id;

	private ConceptType(String name, String fullName, String id) {
		this.name = name;
		this.fullName = fullName;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}

	public String getId() {
		return id;
	}
}

enum LookUpTag {
	AVAILABILITY_RESTRICTION, BASIS_OF_NAME, BASIS_OF_STRNTH, COLOUR, COMBINATION_PACK_IND, COMBINATION_PROD_IND,
	CONTROL_DRUG_CATEGORY, DF_INDICATOR, DISCONTINUED_IND, DND, DT_PAYMENT_CATEGORY, FLAVOUR, FORM, LEGAL_CATEGORY,
	LICENSING_AUTHORITY, LICENSING_AUTHORITY_CHANGE_REASON, NAMECHANGE_REASON, ONT_FORM_ROUTE, PRICE_BASIS,
	REIMBURSEMENT_STATUS, ROUTE, SPEC_CONT, SUPPLIER, UNIT_OF_MEASURE, VIRTUAL_PRODUCT_PRES_STATUS,
	VIRTUAL_PRODUCT_NON_AVAIL;

	public static LookUpTag findByName(String n) {
		for (LookUpTag v : values()) {
			if (v.toString().equals(n)) {
				return v;
			}
		}
		return null;
	}
}

class propertyType {
	String name;
	String type;
}

public class DMDParser {

	final Logger logger = Logger.getLogger(DMDParser.class.getName());

	final String baseURL_CodeSystem = "http://digital.nhs.uk/fhir/CodeSystem/dmd";
	final String baseURL_ValueSet = "http://digital.nhs.uk/fhir/ValueSet/dmd";
	final String csID = "CodeSystem-DMD";
	final String title = "Dictionary of medicines and devices (dm+d)";
	final String baseURL_CodeSystem_Lookup = "http://digital.nhs.uk/fhir/CodeSystem/dmd";
	final String baseURL_ValueSet_Lookup = "http://digital.nhs.uk/fhir/ValueSet/dmd";
	final String title_lookup = "DM+D Lookup Table ";
	final Pattern versionPattern = Pattern.compile("(.*)(nhsbsa)(_)(dmd)(_)([0-9]+\\.[0-9]\\.[0-9])(_)([0-9]+)");

	String ukSCTVersion;

	// Map by concept type to concept list.
	Map<ConceptType, List<ConceptDefinitionComponent>> allConcepts = new HashMap<ConceptType, List<ConceptDefinitionComponent>>();
	// All concept ID Set
	Set<String> allConceptsIdSet = new HashSet<String>();
	// All property in the CodeSystem
	Map<String, PropertyComponent> propertyRigister = new HashMap<String, PropertyComponent>();
	// All lookup property, map between data tag name and xml tag name
	Map<String, LookUpTag> lookUpNameMap = new HashMap<String, LookUpTag>();
	// All Lookup tables
	Map<LookUpTag, Map<String, String>> lookupTables = new HashMap<LookUpTag, Map<String, String>>();

	public DMDParser(String ukSCT) {
		ukSCTVersion = ukSCT;
	}

	public CodeSystem processCodeSystem(String dmdFolder, String releaseSerial, String supportFile, String version,
			String outFolder) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, JAXBException {

		System.out.println("Process DMD  " + version);

		
		File ingredientFile = new File(dmdFolder, "f_ingredient2_" + releaseSerial + ".xml");
		File ampFile = new File(dmdFolder, "f_amp2_" + releaseSerial + ".xml");
		File vtmFile = new File(dmdFolder, "f_vtm2_" + releaseSerial + ".xml");
		File amppFile = new File(dmdFolder, "f_ampp2_" + releaseSerial + ".xml");
		File vmpFile = new File(dmdFolder, "f_vmp2_" + releaseSerial + ".xml");
		File vmppFile = new File(dmdFolder, "f_vmpp2_" + releaseSerial + ".xml");
		File lookupFile = new File(dmdFolder, "f_lookup2_" + releaseSerial + ".xml");

		

		if(lookupTables.size()<1) {
			loadLookupTables(lookupFile);
		}
		
		// Initial CodeSystem
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId(csID);
		codeSystem.setUrl(baseURL_CodeSystem).setValueSet(baseURL_ValueSet).setDescription(
				"A FHIR CodeSystem rendering of the Dictionary of medicines and devices (dm+d) generated from the dm+d XML distribution")
				.setVersion(version).setTitle(title).setName(title).setStatus(PublicationStatus.ACTIVE)
				.setExperimental(true).setContent(CodeSystemContentMode.COMPLETE).setPublisher("NHS UK")
				.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem.setConcept(new ArrayList<CodeSystem.ConceptDefinitionComponent>());

		registerProperty("AMP", supportFile, propertyRigister);
		registerProperty("AMPP", supportFile, propertyRigister);
		registerProperty("VMP", supportFile, propertyRigister);
		registerProperty("VMPP", supportFile, propertyRigister);
		registerProperty("VTM", supportFile, propertyRigister);
		registerProperty("INGREDIENT", supportFile, propertyRigister);

		logger.info("Property Register " + propertyRigister.size());

		// Add all property into CodeSystem
		for (PropertyComponent pr : propertyRigister.values()) {
			codeSystem.addProperty(pr);
		}

		allConcepts.put(ConceptType.AMP, processAMP(propertyRigister, allConceptsIdSet, ampFile));
		allConcepts.put(ConceptType.AMPP, processAMPP(propertyRigister, allConceptsIdSet, amppFile));
		allConcepts.put(ConceptType.VMP, processVMP(propertyRigister, allConceptsIdSet, vmpFile));
		allConcepts.put(ConceptType.VMPP, processVMPP(propertyRigister, allConceptsIdSet, vmppFile));
		allConcepts.put(ConceptType.VTM, processVTM(propertyRigister, allConceptsIdSet, vtmFile));
		allConcepts.put(ConceptType.INGREDIENT, processINGREDIENT(propertyRigister, allConceptsIdSet, ingredientFile));
		
		addExtraConcepts(codeSystem);
		
		// NHS feedback 5 - do not display any strength information returned for products with multiple active ingredients
		mutipleStrengthProcessing();
		
		//Adding All Concepts
		for (Map.Entry<ConceptType, List<ConceptDefinitionComponent>> e : allConcepts.entrySet()) {
			for (ConceptDefinitionComponent cdc : e.getValue()) {
				codeSystem.addConcept(cdc);
			}
		}
		
		return codeSystem;
	}

	private void mutipleStrengthProcessing() {
		for (Map.Entry<ConceptType, List<ConceptDefinitionComponent>> e : allConcepts.entrySet()) {

			for (ConceptDefinitionComponent cdc : e.getValue()) {
				// Counting the IS 
				int isCount = 0;
				for (ConceptPropertyComponent property: cdc.getProperty()) {
					if(property.getCode().toString().equals("ISID")) {
						isCount ++;
					}
				}
				if (isCount > 1) {
					// Remove all Property with "STRNT"
					List<ConceptPropertyComponent> strengthProperties = new ArrayList<ConceptPropertyComponent>();
					for (ConceptPropertyComponent property: cdc.getProperty()) {
						if(property.getCode().toString().contains("STRNT")) {
							strengthProperties.add(property);
						}
					}
					cdc.getProperty().removeAll(strengthProperties);	
				
				}
			}
		}
	}

	private void addExtraConcepts(CodeSystem codeSystem) {
		/**
		 * Load all concepts in CodeSystem
		 */
		ConceptDefinitionComponent rootConcept = createConceptDefinition("DMD","DMD");
		codeSystem.addConcept(rootConcept);

		Arrays.asList(ConceptType.values()).forEach(type -> {
			ConceptDefinitionComponent typeConcept = createConceptDefinition(type.getId(),type.getFullName());
			addParentToConceptDefination(typeConcept, "DMD");
			codeSystem.addConcept(typeConcept);
		});
		// NHS Feedback 8 added missing SNOMED Code as children of DMD
		CreateExtraConceptsFromLookup(codeSystem,"767524001","Unit of measure","DMD",LookUpTag.UNIT_OF_MEASURE);
		CreateExtraConceptsFromLookup(codeSystem,"284009009","Route","DMD",LookUpTag.ROUTE);
		CreateExtraConceptsFromLookup(codeSystem,"2061601000001103","Supplier","DMD",LookUpTag.SUPPLIER);
		CreateExtraConceptsFromLookup(codeSystem,"105904009","Form","DMD",LookUpTag.FORM);
	}
	
	private ConceptDefinitionComponent createConceptDefinition(String code, String display) {
		ConceptDefinitionComponent newConcept = new ConceptDefinitionComponent();
		newConcept.setCode(code);
		newConcept.setDisplay(display);
		return newConcept;
	}
	
	private void addParentToConceptDefination(ConceptDefinitionComponent concept, String parentCode) {
		ConceptPropertyComponent addedParentProperty = new ConceptPropertyComponent();
		addedParentProperty.setCode("parent");
		addedParentProperty.setValue(new CodeType(parentCode));
		concept.addProperty(addedParentProperty);
	}
	
	private void CreateExtraConceptsFromLookup(CodeSystem codeSystem, String leadConceptID, String leadConceptDispaly, String rootID, LookUpTag lookupTag) {
		ConceptDefinitionComponent leadConcept = createConceptDefinition(leadConceptID, leadConceptDispaly);
		codeSystem.addConcept(leadConcept);
		addParentToConceptDefination(leadConcept, rootID);
		for(Map.Entry<String, String> entry: lookupTables.get(lookupTag).entrySet()) {
			ConceptDefinitionComponent childConcept = createConceptDefinition(entry.getKey(),entry.getValue());
			addParentToConceptDefination(childConcept, leadConceptID);
			codeSystem.addConcept(childConcept);
		}
	}


	public void processCodeSystemWithUpdate(String dmdFolder, String dmdSerial, String supportFile, String outFolder,
			String txServerUrl, FeedClient feedClient) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JAXBException {

		String version = processVersionNumber(dmdFolder);
		CodeSystem cs = processCodeSystem(dmdFolder, dmdSerial, supportFile, version, outFolder);
		String outFileName = "CodeSystem-DMD-" + version + ".json";
		File outFile = new File(outFolder, outFileName);

		FhirContext ctx = FhirContext.forR4();
		//Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(cs), outFile);
		Utility.toTextFile(ctx.newJsonParser().encodeResourceToString(cs), outFile);

		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateCodeSystemWithHeader(cs);

		}
		if (feedClient != null) {
			Entry entry = FeedUtility.createFeedEntry_CodeSystem(cs, outFileName);
			String entryFileName = Utility.jsonFileNameToEntry(outFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder, entryFileName));
			feedClient.updateEntryToNHSFeed(entry, new File(outFolder, entryFileName),
					new File(outFolder, outFileName));
		}

	}

	public void processSupportCodeSystemWithUpdate(String dmdFolder, String dmdSerial, String outFolder,
			String txServerUrl, FeedClient feedClient) throws IOException, JAXBException {

		String version = processVersionNumber(dmdFolder);
		File lookupFile = new File(dmdFolder, "f_lookup2_" + dmdSerial + ".xml");
		
		if(lookupTables.size()<1) {
			loadLookupTables(lookupFile);
		}
		

		Bundle bundle = new Bundle();
		bundle.setType(BundleType.COLLECTION);
		bundle.setId("DMD-CodeSystems-Bundle");
		FhirContext ctx = FhirContext.forR4();
		for (Map.Entry<LookUpTag, Map<String, String>> e : lookupTables.entrySet()) {
			CodeSystem codeSystem = new CodeSystem();
			String tag = e.getKey().toString();
			String title = title_lookup + tag;
			codeSystem.setId(csID + "-" + tag.replaceAll("_", "-"));
			String url = baseURL_CodeSystem_Lookup + "/" + tag;
			codeSystem.setUrl(url).setValueSet(baseURL_ValueSet_Lookup + "/" + tag).setDescription(
					"A FHIR CodeSystem rendering of the lookup table in Dictionary of medicines and devices (dm+d)")
					.setVersion(version).setTitle(title).setName(title).setStatus(PublicationStatus.ACTIVE)
					.setExperimental(true).setContent(CodeSystemContentMode.COMPLETE).setPublisher("NHS UK")
					.setContent(CodeSystemContentMode.COMPLETE);
			codeSystem.setConcept(new ArrayList<CodeSystem.ConceptDefinitionComponent>());
			for (Map.Entry<String, String> en : e.getValue().entrySet()) {
				ConceptDefinitionComponent concept = new ConceptDefinitionComponent();
				concept.setCode(en.getKey());
				concept.setDisplay(en.getValue());
				codeSystem.addConcept(concept);
			}

			BundleEntryComponent bundleEntry = new BundleEntryComponent();
			bundleEntry.setFullUrl(url);
			bundleEntry.setResource(codeSystem);
			bundle.addEntry(bundleEntry);

			String con = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(codeSystem);
			String fileName = "DMD-" + tag.toLowerCase() + ".json";
			Utility.toTextFile(con, outFolder + File.separator + fileName);

			if (txServerUrl != null) {
				FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
				//fhirClientR4.deleteCodeSystem(codeSystem.getIdBase());
				fhirClientR4.createUpdateCodeSystem(codeSystem);
			}

		}

		String con = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
		String fileName = "DMD_CodeSystems_Bundle.json";
		Utility.toTextFile(con, outFolder + File.separator + fileName);

		if (feedClient != null) {
			Entry entry = FeedUtility.createFeedEntry_Bundle(bundle, fileName, version);
			String entryFileName = Utility.jsonFileNameToEntry(fileName);
			Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder, entryFileName));
			feedClient.updateEntryToNHSFeed(entry, new File(outFolder, entryFileName), new File(outFolder, fileName));
		}
	}

	/**
	 * Load all properties with their type f
	 * 
	 * @param tabName
	 * @param dmdContentFile
	 */
	private void registerProperty(String tabName, String supportFile, Map<String, PropertyComponent> propertyRigister) {
		for (List<String> line : Utility.readXLSXFileSingleTab(supportFile, tabName, false)) {
			if (line.size() > 0) {
				String name = line.get(0);
				if (name.startsWith("<")) {
					name = name.replaceAll("[<>]", "");
					if (!propertyRigister.containsKey(name)) {
						String type = line.get(2);
						PropertyComponent propertyComponent = new PropertyComponent();
						propertyComponent.setCode(name).setDescription(name);
						if (type.equals("SCTID")) {
							propertyComponent.setType(PropertyType.CODING);
						} else if (type.equals("YYYY-MM-DD")) {
							propertyComponent.setType(PropertyType.DATETIME);
						} else {
							propertyComponent.setType(PropertyType.STRING);
							if (line.size() > 6 && line.get(6) != null) {
								String lookup = line.get(6);
								if (lookup.startsWith("<LOOKUP")) {
									propertyComponent.setType(PropertyType.CODING);
									String target = lookup.replaceAll("<LOOKUP>/<", "").replaceAll(">/<INFO>", "")
											.trim();
									LookUpTag tag = LookUpTag.findByName(target);
									if (tag != null) {
										logger.info("TABLE MAP - " + name + "\t" + target);
										lookUpNameMap.put(name, tag);
									}

								}
							}
						}
						propertyRigister.put(name, propertyComponent);
					}
				}
			}
		}

	}

	private String processVersionNumber(String dmdFolderName) {
		Matcher m = versionPattern.matcher(dmdFolderName);
		m.find();
		String year = m.group(8).substring(0, 4);
		String version = m.group(6);
		if (version.matches("^([0-9]\\.).*")) {
			version = "0" + version;
		}
		version = year + version;
		logger.info("Version number created " + version);
		return version;
	}

	private Map<String, String> extractTable(List<?> l) {
		Map<String, String> table = new HashMap<String, String>();
		for (Object o : l) {
			if (o instanceof InfoType) {
				table.put(String.valueOf(((InfoType) o).getCD()), ((InfoType) o).getDESC());
			} else if (o instanceof HistoryInfoType) {
				table.put(String.valueOf(((HistoryInfoType) o).getCD()), ((HistoryInfoType) o).getDESC());
			}

			else if (o instanceof SupplierInfoType) {
				table.put(String.valueOf(((SupplierInfoType) o).getCD()), ((SupplierInfoType) o).getDESC());
			}

		}

		return table;
	}

	private void loadLookupTables(File lookupFile)
			throws JAXBException, FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.ObjectFactory.class);
		LOOKUP products = (LOOKUP) context.createUnmarshaller().unmarshal(new FileReader(lookupFile));
		lookupTables.put(LookUpTag.AVAILABILITY_RESTRICTION,
				extractTable(products.getAVAILABILITYRESTRICTION().getINFO()));
		lookupTables.put(LookUpTag.BASIS_OF_NAME, extractTable(products.getBASISOFNAME().getINFO()));
		lookupTables.put(LookUpTag.BASIS_OF_STRNTH, extractTable(products.getBASISOFSTRNTH().getINFO()));
		lookupTables.put(LookUpTag.COLOUR, extractTable(products.getCOLOUR().getINFO()));
		lookupTables.put(LookUpTag.COMBINATION_PACK_IND, extractTable(products.getCOMBINATIONPACKIND().getINFO()));
		lookupTables.put(LookUpTag.COMBINATION_PROD_IND, extractTable(products.getCOMBINATIONPRODIND().getINFO()));
		lookupTables.put(LookUpTag.CONTROL_DRUG_CATEGORY, extractTable(products.getCONTROLDRUGCATEGORY().getINFO()));
		lookupTables.put(LookUpTag.DF_INDICATOR, extractTable(products.getDFINDICATOR().getINFO()));
		lookupTables.put(LookUpTag.DISCONTINUED_IND, extractTable(products.getDISCONTINUEDIND().getINFO()));
		lookupTables.put(LookUpTag.DND, extractTable(products.getDND().getINFO()));
		lookupTables.put(LookUpTag.DT_PAYMENT_CATEGORY, extractTable(products.getDTPAYMENTCATEGORY().getINFO()));
		lookupTables.put(LookUpTag.FLAVOUR, extractTable(products.getFLAVOUR().getINFO()));
		lookupTables.put(LookUpTag.FORM, extractTable(products.getFORM().getINFO()));
		lookupTables.put(LookUpTag.LEGAL_CATEGORY, extractTable(products.getLEGALCATEGORY().getINFO()));
		lookupTables.put(LookUpTag.LICENSING_AUTHORITY, extractTable(products.getLICENSINGAUTHORITY().getINFO()));
		lookupTables.put(LookUpTag.LICENSING_AUTHORITY_CHANGE_REASON,
				extractTable(products.getLICENSINGAUTHORITYCHANGEREASON().getINFO()));
		lookupTables.put(LookUpTag.NAMECHANGE_REASON, extractTable(products.getNAMECHANGEREASON().getINFO()));
		lookupTables.put(LookUpTag.ONT_FORM_ROUTE, extractTable(products.getONTFORMROUTE().getINFO()));
		lookupTables.put(LookUpTag.PRICE_BASIS, extractTable(products.getPRICEBASIS().getINFO()));
		lookupTables.put(LookUpTag.REIMBURSEMENT_STATUS, extractTable(products.getREIMBURSEMENTSTATUS().getINFO()));
		lookupTables.put(LookUpTag.ROUTE, extractTable(products.getROUTE().getINFO()));
		lookupTables.put(LookUpTag.SPEC_CONT, extractTable(products.getSPECCONT().getINFO()));
		lookupTables.put(LookUpTag.SUPPLIER, extractTable(products.getSUPPLIER().getINFO()));
		lookupTables.put(LookUpTag.UNIT_OF_MEASURE, extractTable(products.getUNITOFMEASURE().getINFO()));
		lookupTables.put(LookUpTag.VIRTUAL_PRODUCT_PRES_STATUS,
				extractTable(products.getVIRTUALPRODUCTPRESSTATUS().getINFO()));
		lookupTables.put(LookUpTag.VIRTUAL_PRODUCT_NON_AVAIL,
				extractTable(products.getVIRTUALPRODUCTNONAVAIL().getINFO()));
	}

	private List<ConceptDefinitionComponent> processAMP(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.amp.ObjectFactory.class);
		ACTUALMEDICINALPRODUCTS products = (ACTUALMEDICINALPRODUCTS) context.createUnmarshaller()
				.unmarshal(new FileReader(xmlFile));
		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "APID";
		Set<String> parents = new HashSet<>(Arrays.asList("VPID"));
		transferComplexType(conceptRegister, propertyRigister, keyID, "DESC", "NM", ConceptType.AMP, parents,
				AmpType.class, products.getAMPS().getAMP());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, ApiType.class,
				products.getAPINGREDIENT().getAPING());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, LicRouteType.class,
				products.getLICENSEDROUTE().getLICROUTE());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, AppProdInfoType.class,
				products.getAPINFORMATION().getAPINFO());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());

	}

	private List<ConceptDefinitionComponent> processAMPP(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.ObjectFactory.class);
		ACTUALMEDICINALPRODPACKS products = (ACTUALMEDICINALPRODPACKS) context.createUnmarshaller()
				.unmarshal(new FileReader(xmlFile));
		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "APPID";
		Set<String> parents = new HashSet<>(Arrays.asList("VPPID", "APID"));
		transferComplexType(conceptRegister, propertyRigister, keyID, "NM", "ABBREVNM", ConceptType.AMPP, parents,
				AmppType.class, products.getAMPPS().getAMPP());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, PackInfoType.class,
				products.getAPPLIANCEPACKINFO().getPACKINFO());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, PrescInfoType.class,
				products.getDRUGPRODUCTPRESCRIBINFO().getPRESCRIBINFO());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, PriceInfoType.class,
				products.getMEDICINALPRODUCTPRICE().getPRICEINFO());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, ReimbInfoType.class,
				products.getREIMBURSEMENTINFO().getREIMBINFO());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());

	}

	private List<ConceptDefinitionComponent> processVMP(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.vmp.ObjectFactory.class);
		VIRTUALMEDPRODUCTS vmp = (VIRTUALMEDPRODUCTS) context.createUnmarshaller().unmarshal(new FileReader(xmlFile));
		String keyID = "VPID";
		Set<String> parents = new HashSet<>(Arrays.asList("VTMID"));
		transferComplexType(conceptRegister, propertyRigister, keyID, "NM", "ABBREVNM", ConceptType.VMP, parents,
				VmpType.class, vmp.getVMPS().getVMP());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, VpiType.class,
				vmp.getVIRTUALPRODUCTINGREDIENT().getVPI());
		transferComplexTypeForONTFORMROUTE(conceptRegister, propertyRigister, keyID, null, null,
				null, null, OntDrugFormType.class,vmp.getONTDRUGFORM().getONT());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, DrugFormType.class,
				vmp.getDRUGFORM().getDFORM());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, DrugRouteType.class,
				vmp.getDRUGROUTE().getDROUTE());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, ControlInfoType.class,
				vmp.getCONTROLDRUGINFO().getCONTROLINFO());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());

	}

	private List<ConceptDefinitionComponent> processVMPP(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.vmpp.ObjectFactory.class);
		VIRTUALMEDPRODUCTPACK vmpp = (VIRTUALMEDPRODUCTPACK) context.createUnmarshaller()
				.unmarshal(new FileReader(xmlFile));

		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "VPPID";

		Set<String> parents = new HashSet<>(Arrays.asList("VPID"));
		transferComplexType(conceptRegister, propertyRigister, keyID, "NM", "ABBREVNM", ConceptType.VMPP, parents,
				VmppType.class, vmpp.getVMPPS().getVMPP());
		transferComplexType(conceptRegister, propertyRigister, keyID, null, null, null, null, DtInfoType.class,
				vmpp.getDRUGTARIFFINFO().getDTINFO());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());
	}

	private List<ConceptDefinitionComponent> processVTM(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.vtm.ObjectFactory.class);
		VIRTUALTHERAPEUTICMOIETIES vtm = (VIRTUALTHERAPEUTICMOIETIES) context.createUnmarshaller()
				.unmarshal(new FileReader(xmlFile));
		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "VTMID";
		transferComplexType(conceptRegister, propertyRigister, keyID, "NM", "ABBREVNM", ConceptType.VTM, null,
				VTM.class, vtm.getVTM());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());

	}

	private List<ConceptDefinitionComponent> processINGREDIENT(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		JAXBContext context = JAXBContext
				.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.ingredient.ObjectFactory.class);
		INGREDIENTSUBSTANCES ing = (INGREDIENTSUBSTANCES) context.createUnmarshaller()
				.unmarshal(new FileReader(xmlFile));

		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "ISID";

		transferComplexType(conceptRegister, propertyRigister, keyID, "NM", null, ConceptType.INGREDIENT, null,
				ING.class, ing.getING());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());

	}

	private void transferComplexType(Map<String, ConceptDefinitionComponent> conceptRegister,
			Map<String, PropertyComponent> propertyRigister, String idField, String displayField, String synonymField,
			ConceptType nativeParent, Set<String> parentField, Class<?> clazz, List<?> oList)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		Map<String, Class<?>> fields = new HashMap<String, Class<?>>();
		Map<String, String> fieldNameMap = new HashMap<String, String>();
		// Property Register
		for (Field field : clazz.getDeclaredFields()) {
			XmlElement element = (XmlElement) field.getAnnotations()[0];
			String elementName = element.name();
			System.out
					.println(clazz.getName() + "\t" + field.getName() + "\t" + element.name() + "\t" + field.getType());
			fields.put(field.getName().toUpperCase(), field.getType());
			fieldNameMap.put(field.getName().toUpperCase(), elementName);
		}
		// Concept Add
		for (Object o : oList) {
			Method m = o.getClass().getMethod("get" + idField);
			Object v = m.invoke(o);
			String id = v.toString();
			if (!conceptRegister.containsKey(id)) {
				ConceptDefinitionComponent cdc = new ConceptDefinitionComponent();
				cdc.setCode(id);
				conceptRegister.put(id, cdc);
			}
			// Add Native parent
			if (nativeParent != null) {
				ConceptPropertyComponent nativeParentProperty = new ConceptPropertyComponent();
				nativeParentProperty.setCode("parent");
				nativeParentProperty.setValue(new CodeType(nativeParent.getId()));
				conceptRegister.get(id).addProperty(nativeParentProperty);
			}
			for (Map.Entry<String, Class<?>> entry : fields.entrySet()) {
				String fieldName = entry.getKey();
				m = o.getClass().getMethod("get" + fieldName);
				v = m.invoke(o);

				String propertyName = fieldNameMap.get(fieldName);

				if (propertyName.equalsIgnoreCase(displayField) && v != null) {
					conceptRegister.get(id).setDisplay(v.toString());
				} else if (propertyName.equalsIgnoreCase(synonymField) && v != null) {
					ConceptDefinitionDesignationComponent designation = new ConceptDefinitionDesignationComponent();
					Coding coding = new Coding();
					coding.setSystem("http://snomed.info/sct");
					coding.setVersion("http://snomed.info/sct/83821000000107/version/" + ukSCTVersion);
					coding.setCode("900000000000013009");
					designation.setLanguage("en");
					designation.setValue(v.toString());
					designation.setUse(coding);
					conceptRegister.get(id).addDesignation(designation);
				} else if (parentField != null && parentField.contains(propertyName) && v != null) {
					// add parent
					ConceptPropertyComponent addedParentProperty = new ConceptPropertyComponent();
					addedParentProperty.setCode("parent");
					addedParentProperty.setValue(new CodeType(v.toString()));
					conceptRegister.get(id).addProperty(addedParentProperty);
					// System.out.println(nativeParent.getFullName() + "\t" + id + "\t" +
					// propertyName + "\t" + v.toString());
				} else if (!fieldName.equalsIgnoreCase(idField) && v != null && v.toString().length() > 0) {
					ConceptPropertyComponent conceptPropertyComponent = new ConceptPropertyComponent();
					PropertyComponent propertyComponent = propertyRigister.get(propertyName);
					conceptPropertyComponent.setCode(propertyName);
					if (propertyComponent.getType() == PropertyType.CODING) {
						if (lookUpNameMap.containsKey(propertyName)) {
							Map<String, String> valueMap = lookupTables.get(lookUpNameMap.get(propertyName));
							if (v.toString().length() > 0 && valueMap != null && valueMap.get(v.toString()) != null) {
								Coding coding = new Coding();
								coding.setSystem("http://digital.nhs.uk/fhir/CodeSystem/dmd/"
										+ lookUpNameMap.get(propertyName));
								coding.setCode(v.toString());
								coding.setDisplay(valueMap.get(v.toString()));
								conceptPropertyComponent.setValue(coding);
							} else {
								Coding coding = new Coding();
								coding.setSystem("http://digital.nhs.uk/fhir/CodeSystem/dmd/"
										+ lookUpNameMap.get(propertyName));
								coding.setCode(v.toString());
								coding.setDisplay("Check Data");
								conceptPropertyComponent.setValue(coding);
							}
						} else {
							Coding coding = new Coding();
							coding.setSystem(baseURL_CodeSystem); // NHS Feed back item 8
							coding.setCode(v.toString());
							conceptPropertyComponent.setValue(coding);
						}

					} else if (propertyComponent.getType() == PropertyType.DATETIME) {
						conceptPropertyComponent.setValue(new DateTimeType(v.toString()));
					}

					else if (propertyComponent.getType() == PropertyType.DECIMAL) {
						conceptPropertyComponent.setValue(new DecimalType(v.toString()));
						System.out.println("id" + "\t" + v.toString());
					}

					else {
						conceptPropertyComponent.setValue(new StringType(v.toString()));
					}
					conceptRegister.get(id).addProperty(conceptPropertyComponent);

				}
			}
		}

	}
	
	private void transferComplexTypeForONTFORMROUTE(Map<String, ConceptDefinitionComponent> conceptRegister,
			Map<String, PropertyComponent> propertyRigister, String idField, String displayField, String synobymField,
			ConceptType nativeParent, Set<String> parentField, Class<?> clazz, List<?> oList)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		Map<String, Class<?>> fields = new HashMap<String, Class<?>>();
		Map<String, String> fieldNameMap = new HashMap<String, String>();
		// Property Register
		for (Field field : clazz.getDeclaredFields()) {
			XmlElement element = (XmlElement) field.getAnnotations()[0];
			String elementName = element.name();
			System.out
					.println(clazz.getName() + "\t" + field.getName() + "\t" + element.name() + "\t" + field.getType());
				fields.put(field.getName().toUpperCase(), field.getType());
				fieldNameMap.put(field.getName().toUpperCase(), elementName);
		}
		// Concept Add
		for (Object o : oList) {
			Method m = o.getClass().getMethod("get" + idField);
			Object v = m.invoke(o);
			String id = v.toString();
			for (Map.Entry<String, Class<?>> entry : fields.entrySet()) {
				String fieldName = entry.getKey();
				if (!fieldName.equalsIgnoreCase(idField) && v != null) {
					// can only be form cd in this case
					String propertyName = "ONTFORMCD";
					m = o.getClass().getMethod("get" + fieldName);
					v = m.invoke(o);
					ConceptPropertyComponent conceptPropertyComponent = new ConceptPropertyComponent();
					conceptPropertyComponent.setCode(propertyName);
					if (lookUpNameMap.containsKey(propertyName)) {
						Map<String, String> valueMap = lookupTables.get(lookUpNameMap.get(propertyName));
						if (v.toString().length() > 0 && valueMap != null && valueMap.get(v.toString()) != null) {
							Coding coding = new Coding();
							coding.setSystem("http://digital.nhs.uk/fhir/CodeSystem/dmd/ONTFORMROUTE");
							coding.setCode(v.toString());
							coding.setDisplay(valueMap.get(v.toString()));
							conceptPropertyComponent.setValue(coding);
							System.out.println("TEMP Solution - " + fieldName + "\t" + propertyName + "\t" + conceptPropertyComponent.getValue().toString() );
						}
					}
					conceptRegister.get(id).addProperty(conceptPropertyComponent);

				}
			}
		}

	}

	private void conceptIDDuplicationCheck(Set<String> conceptIdSet, Map<String, ConceptDefinitionComponent> conceptRegister,
			String keyID) {
		Set<String> remove = new HashSet<String>();
		for (String cid : conceptRegister.keySet()) {
			if (conceptIdSet.contains(cid)) {
				logger.info("Check ID " + keyID + "\t" + cid);
				remove.add(cid);
			} else {
				conceptIdSet.add(cid);
			}
		}
		for (String s : remove) {
			conceptRegister.remove(s);
			logger.info("register remove " + s + "\t" + conceptRegister.size());
		}
	}

}
