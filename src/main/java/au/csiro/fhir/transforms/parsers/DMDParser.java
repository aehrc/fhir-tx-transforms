/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
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

import javax.sound.sampled.Line;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.FilterOperator;
import org.hl7.fhir.r4.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r4.model.CodeSystem.PropertyType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ConceptMap;
import org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.StringType;

import au.csiro.fhir.transforms.helper.FHIRClientR4;
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;
import au.csiro.fhir.transforms.xml.dmd.gtin.AMPPType;
import au.csiro.fhir.transforms.xml.dmd.gtin.GTINDETAILS;
import au.csiro.fhir.transforms.xml.dmd.gtin.GTINType;
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
	/*
	 * AMP("AMP", "Actual medicinal product", "10363901000001102"), AMPP("AMPP",
	 * "Actual medicinal product pack", "10364001000001104"), VMP("VMP",
	 * "Virtual medicinal product", "10363801000001108"), VMPP("VMPP",
	 * "Virtual medicinal product pack", "8653601000001108"), VTM("VTM",
	 * "Virtual therapeutic moiety", "10363701000001104"), INGREDIENT("INGREDIENT",
	 * "Ingredient", "105590001"), UNITOFMEASURE("Unit of measure",
	 * "Unit of measure", "767524001"), ROUTE("Route", "Route", "284009009"),
	 * SUPPLIER("Supplier", "Supplier", "2061601000001103"), FORM("Form", "Form",
	 * "105904009");
	 */
	
	AMP("Actual Medicinal Product", "Actual Medicinal Product", "AMP"),
	AMPP("Actual Medicinal Product Pack", "Actual Medicinal Product Pack", "AMPP"),
	VMP("Virtual Medicinal Product", "Virtual Medicinal Product", "VMP"),
	VMPP("Virtual Medicinal Product Pack", "Virtual Medicinal Product Pack", "VMPP"),
	VTM("Virtual Therapeutic Moiety", "Virtual Therapeutic Moiety", "VTM"), 
	INGREDIENT("Ingredient", "Ingredient", "INGREDIENT"),
	UNITOFMEASURE("	Unit of Measure", "Unit of Measure", "UOM"),
	ROUTE("Route", "Route", "ROUTE"),
	SUPPLIER("Supplier", "Supplier", "SUPPLIER"),
	FORM("Form", "Form", "FORM");
	
	
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

	final String baseURL_CodeSystem = "https://dmd.nhs.uk";
	final String baseURL_ValueSet = "https://dmd.nhs.uk/vs";
	final String csID = "CodeSystem-dmd";
	final String title = "Dictionary of medicines and devices (dm+d)";
	final String baseURL_CodeSystem_Lookup = "https://dmd.nhs.uk";
	final String title_lookup = "dm+d ";
	final Pattern versionPattern = Pattern.compile("(.*)(nhsbsa)(_)(dmd)(_)([0-9]+\\.[0-9]\\.[0-9])(_)([0-9]+)");
	
	final String gtinMapID = "ConceptMap-dmd-gtin";
	final String baseURL_ConceptMap = "https://dmd.nhs.uk/conceptmap/gtin";
	final String gtinMapTitle = "Dictionary of medicines and devices (dm+d) to GTIN Map";
	final String baseURL_CodeSystem_Gtin = "https://www.gs1.org/gtin";


	// Map by concept type to concept list.
	Map<ConceptType, List<ConceptDefinitionComponent>> allConcepts = new HashMap<ConceptType, List<ConceptDefinitionComponent>>();
	// All concept ID Set
	Set<String> allConceptsIdSet = new HashSet<String>();
	// All property in the CodeSystem
	Map<String, PropertyComponent> propertyRigister = new HashMap<String, PropertyComponent>();
	// All lookup property, map between data tag name and xml tag name
	Map<String, LookUpTag> lookUpNameMap = new HashMap<String, LookUpTag>();
	// look up table which are in a seperated code system 
	Map<LookUpTag, Map<String, String>> lookupTables_codeSystem = new HashMap<LookUpTag, Map<String, String>>();
	// look up table which are not in a seperated code system 
	Map<LookUpTag, Map<String, String>> lookupTables_concepts = new HashMap<LookUpTag, Map<String, String>>();

	public DMDParser() {
	}
	
	
	public ConceptMap processGtinMapping(String gtinFile, String version, String outFolder) throws JAXBException, FileNotFoundException{

		ConceptMap conceptMap = new ConceptMap();
		logger.info("Process Gtin Concept Map  " + version);
		Map<String,Set<String>> activeMap = new HashMap<String, Set<String>>();
		Map<String,Set<String>> retiredMap = new HashMap<String, Set<String>>();
		conceptMap.setId(gtinMapID + "-" + version.replaceAll("\\.", ""));
		conceptMap.setUrl(baseURL_ConceptMap).setDescription(
				"A FHIR ConceptMap between the Dictionary of medicines and devices (dm+d) and GTIN (Global Trade Item Number), generated from the dm+d XML distribution")
				.setVersion(version).setTitle(gtinMapTitle).setName(gtinMapTitle).setStatus(PublicationStatus.ACTIVE)
				.setExperimental(true).setPublisher("NHS UK");
		ConceptMapGroupComponent groupComponent = new ConceptMapGroupComponent();
		conceptMap.addGroup(groupComponent);
		
		groupComponent.setSource(baseURL_CodeSystem);
		groupComponent.setSourceVersion(version);
		groupComponent.setTarget(baseURL_CodeSystem_Gtin);
		
		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.gtin.ObjectFactory.class);
		
		GTINDETAILS details = (GTINDETAILS) context.createUnmarshaller()
				.unmarshal(new FileReader(gtinFile));
		for(AMPPType ampp : details.getAMPPS().getAMPP()) {
			String amppID = "";
			// Get ampp ID
			for(Object o : ampp.getAMPPIDAndGTINDATA()) {
				if(o instanceof BigInteger) {
					amppID = o.toString(); 
				}
			}
			if(!activeMap.containsKey(amppID)) {
				activeMap.put(amppID, new HashSet<String>());
				retiredMap.put(amppID, new HashSet<String>());
			}
			// Print gtin
			for(Object o : ampp.getAMPPIDAndGTINDATA()) {
				if(o instanceof GTINType) {
					GTINType gt = (GTINType) o; 
					String gtin= null;
					String startDt = null;
					String endDT = null;
					for(Object gto : gt.getGTINAndSTARTDTAndENDDT()) {
						JAXBElement<Object> jaxb = (JAXBElement<Object>) gto;
						
						if(jaxb.getName().toString().equals("GTIN")) { 
							gtin = jaxb.getValue().toString();
						}
						else if(jaxb.getName().toString().equals("STARTDT")) { 
							startDt = jaxb.getValue().toString();
						}
						else if(jaxb.getName().toString().equals("ENDDT")) { 
							endDT = jaxb.getValue().toString();
						}  
					}
					if(endDT == null) {
						activeMap.get(amppID).add(gtin);
					}
					else {
						retiredMap.get(amppID).add(gtin);
					}
				}
				
			}
		}
		int count = 0;
		for(String amppid : activeMap.keySet()) {
			SourceElementComponent sourceElementComponent = new SourceElementComponent();
			sourceElementComponent.setCode(amppid);
		
			for(String g : activeMap.get(amppid)) {
				TargetElementComponent target = new TargetElementComponent();
				//System.out.println(amppid + "\t" + g + "\t" + "Active");
				target.setCode(g);
				target.setEquivalence(ConceptMapEquivalence.RELATEDTO);
				OtherElementComponent product = new OtherElementComponent();
				product.setProperty("mapping_status");
				product.setValue("active");
				target.addProduct(product);
				sourceElementComponent.addTarget(target);
			}
			for(String g : retiredMap.get(amppid)) {
				TargetElementComponent target = new TargetElementComponent();		
				target.setCode(g);
				target.setEquivalence(ConceptMapEquivalence.RELATEDTO);
				OtherElementComponent product = new OtherElementComponent();
				product.setProperty("mapping_status");
				product.setValue("retired");
				target.addProduct(product);
				sourceElementComponent.addTarget(target);
			}
			groupComponent.addElement(sourceElementComponent);
			if(count++ > 10) break;
					
		}
		
		return conceptMap;
		
	}

	public CodeSystem processCodeSystem(String dmdFolder, String releaseSerial, String supportFile, String version,
			String outFolder) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, JAXBException {

		logger.info("Process dm+d  " + version);

		File ingredientFile = new File(dmdFolder, "f_ingredient2_" + releaseSerial + ".xml");
		File ampFile = new File(dmdFolder, "f_amp2_" + releaseSerial + ".xml");
		File vtmFile = new File(dmdFolder, "f_vtm2_" + releaseSerial + ".xml");
		File amppFile = new File(dmdFolder, "f_ampp2_" + releaseSerial + ".xml");
		File vmpFile = new File(dmdFolder, "f_vmp2_" + releaseSerial + ".xml");
		File vmppFile = new File(dmdFolder, "f_vmpp2_" + releaseSerial + ".xml");
		File lookupFile = new File(dmdFolder, "f_lookup2_" + releaseSerial + ".xml");

		if(lookupTables_codeSystem.size()<1) {
			loadLookupTables(lookupFile);
		}
		
		// Initial CodeSystem
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId(csID + "-" + version);
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
		registerProperty("LOOKUP", supportFile, propertyRigister);

		logger.info("Property Register " + propertyRigister.size());
		
		for(String name : propertyRigister.keySet()) {
			logger.info("Property " + name + " with type " + propertyRigister.get(name).getType().getDisplay());
		}

		// Add all property into CodeSystem
		for (PropertyComponent pr : propertyRigister.values()) {
			codeSystem.addProperty(pr);
			// Add filter 
			CodeSystemFilterComponent codeSystemFilterComponent = new CodeSystemFilterComponent();
			codeSystemFilterComponent.setCode(pr.getCode());
			codeSystemFilterComponent.addOperator(FilterOperator.IN);
			codeSystemFilterComponent.addOperator(FilterOperator.EQUAL);
			codeSystemFilterComponent.addOperator(FilterOperator.NOTIN);
			codeSystemFilterComponent.addOperator(FilterOperator.EXISTS);
			if(pr.getType().equals(PropertyType.STRING)) {
				codeSystemFilterComponent.addOperator(FilterOperator.REGEX);
			}
			codeSystemFilterComponent.setDescription("Preperty filter for " + pr.getCode());
			codeSystemFilterComponent.setValue("Preperty filter for " + pr.getCode());
			codeSystem.addFilter(codeSystemFilterComponent);
		}

		allConcepts.put(ConceptType.AMP, processAMP(propertyRigister, allConceptsIdSet, ampFile));
		allConcepts.put(ConceptType.AMPP, processAMPP(propertyRigister, allConceptsIdSet, amppFile));
		allConcepts.put(ConceptType.VMP, processVMP(propertyRigister, allConceptsIdSet, vmpFile));
		allConcepts.put(ConceptType.VMPP, processVMPP(propertyRigister, allConceptsIdSet, vmppFile));
		allConcepts.put(ConceptType.VTM, processVTM(propertyRigister, allConceptsIdSet, vtmFile));
		allConcepts.put(ConceptType.INGREDIENT, processINGREDIENT(propertyRigister, allConceptsIdSet, ingredientFile));
		allConcepts.put(ConceptType.UNITOFMEASURE, processUnitOfMeasure(propertyRigister, allConceptsIdSet, lookupFile));
		allConcepts.put(ConceptType.FORM, processForm(propertyRigister, allConceptsIdSet, lookupFile));
		allConcepts.put(ConceptType.SUPPLIER, processSupplier(propertyRigister, allConceptsIdSet, lookupFile));
		allConcepts.put(ConceptType.ROUTE, processRoute(propertyRigister, allConceptsIdSet, lookupFile));
		
		addExtraConcepts(codeSystem);
		
		// NHS feedback 5 - do not display any strength information returned for products with multiple active ingredients
		mutipleStrengthProcessing();
		
		//Adding All Concepts
		for (Map.Entry<ConceptType, List<ConceptDefinitionComponent>> e : allConcepts.entrySet()) {
			for (ConceptDefinitionComponent cdc : e.getValue()) {
				codeSystem.addConcept(cdc);
			}
		}
		
		validate(codeSystem);
		
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
		ConceptDefinitionComponent rootConcept = createConceptDefinition("dm+d","Dictionary of medicines and devices (dm+d)");
		codeSystem.addConcept(rootConcept);

		Arrays.asList(ConceptType.values()).forEach(type -> {
			ConceptDefinitionComponent typeConcept = createConceptDefinition(type.getId(),type.getFullName());
			addParentToConceptDefination(typeConcept, "dm+d");
			codeSystem.addConcept(typeConcept);
		});
		
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
	
	


	public void processCodeSystemWithUpdate(String dmdFolder, String dmdSerial, String supportFile, String outFolder,
			String txServerUrl, FeedClient feedClient) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JAXBException {

		String version = processVersionNumber(dmdFolder);
		CodeSystem cs = processCodeSystem(dmdFolder, dmdSerial, supportFile, version, outFolder);
		String outFileName = "CodeSystem-dmd-" + version + ".json";
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
	
	public void processGtinMappingmWithUpdate(String dmdFolder, String gtinFile, String outFolder,String txServerUrl, FeedClient feedClient) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JAXBException {

		String version = processVersionNumber(dmdFolder);
		ConceptMap cm = processGtinMapping(gtinFile, version, outFolder);
		
		String outFileName = "ConceptMap-dmd-gtin" + version + ".json";
		File outFile = new File(outFolder, outFileName);
		FhirContext ctx = FhirContext.forR4();
		//Utility.toTextFile(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(cm), outFile);
		Utility.toTextFile(ctx.newJsonParser().encodeResourceToString(cm), outFile);

		if (txServerUrl != null) {
			FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
			fhirClientR4.createUpdateMapWithHeader(cm);

		}
		/*
		if (feedClient != null) {
			Entry entry = FeedUtility.createFeedEntry_CodeSystem(c, outFileName);
			String entryFileName = Utility.jsonFileNameToEntry(outFileName);
			Utility.toTextFile(FeedUtility.entryToJson(entry), new File(outFolder, entryFileName));
			feedClient.updateEntryToNHSFeed(entry, new File(outFolder, entryFileName),
					new File(outFolder, outFileName));
		}
		*/
		

	}

	public void processSupportCodeSystemWithUpdate(String dmdFolder, String dmdSerial, String outFolder,
			String txServerUrl, FeedClient feedClient) throws IOException, JAXBException {

		String version = processVersionNumber(dmdFolder);
		File lookupFile = new File(dmdFolder, "f_lookup2_" + dmdSerial + ".xml");
		
		if(lookupTables_codeSystem.size()<1) {
			loadLookupTables(lookupFile);
		}
		

		Bundle bundle = new Bundle();
		bundle.setType(BundleType.COLLECTION);
		bundle.setId("Bundle-dmd-CodeSystems-"+version);
		FhirContext ctx = FhirContext.forR4();
		for (Map.Entry<LookUpTag, Map<String, String>> e : lookupTables_codeSystem.entrySet()) {
			CodeSystem codeSystem = new CodeSystem();
			String tag = e.getKey().toString();
			String title = title_lookup + tag;
			codeSystem.setId(csID + "-" + tag.replaceAll("_", "-") + "-" + version);
			String url = baseURL_CodeSystem_Lookup + "/" + tag;
			codeSystem.setUrl(url).setValueSet(url + "/vs").setDescription(
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
			String fileName = "dmd-" + tag.toLowerCase() + "-" + version + ".json";
			Utility.toTextFile(con, outFolder + File.separator + fileName);

			if (txServerUrl != null) {
				FHIRClientR4 fhirClientR4 = new FHIRClientR4(txServerUrl);
				fhirClientR4.createUpdateCodeSystem(codeSystem);
			}

		}

		String con = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
		String fileName = "Bundle_dmd_CodeSystems-" + version + ".json";
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
		InputStream supportFileInputStream = getFileFromResourceAsStream(supportFile);
		logger.info("Register property for " + tabName);
		for (List<String> line : Utility.readXLSXFileSingleTab(supportFileInputStream, tabName, false)) {
			if (line.size() > 0) {
				String name = line.get(0);
				if (name.startsWith("<")) {
					name = name.replaceAll("[<>]", "");
					if (!propertyRigister.containsKey(name)) {
						String type = line.get(2).trim();
						PropertyComponent propertyComponent = new PropertyComponent();
						propertyComponent.setCode(name).setDescription(name);
						if (type.equals("SCTID")) {
							propertyComponent.setType(PropertyType.CODING);
						} 
						
						else if (type.equals("YYYY-MM-DD")) {
							propertyComponent.setType(PropertyType.DATETIME);
						} 
						
						else if (type.equalsIgnoreCase("decimal")) {
							propertyComponent.setType(PropertyType.DECIMAL);
						}
						
						else if (type.equalsIgnoreCase("Integer")) {
							propertyComponent.setType(PropertyType.INTEGER);
						}
						else {
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
		lookupTables_codeSystem.put(LookUpTag.AVAILABILITY_RESTRICTION,
				extractTable(products.getAVAILABILITYRESTRICTION().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.BASIS_OF_NAME, extractTable(products.getBASISOFNAME().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.BASIS_OF_STRNTH, extractTable(products.getBASISOFSTRNTH().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.COLOUR, extractTable(products.getCOLOUR().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.COMBINATION_PACK_IND, extractTable(products.getCOMBINATIONPACKIND().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.COMBINATION_PROD_IND, extractTable(products.getCOMBINATIONPRODIND().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.CONTROL_DRUG_CATEGORY, extractTable(products.getCONTROLDRUGCATEGORY().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.DF_INDICATOR, extractTable(products.getDFINDICATOR().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.DISCONTINUED_IND, extractTable(products.getDISCONTINUEDIND().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.DND, extractTable(products.getDND().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.DT_PAYMENT_CATEGORY, extractTable(products.getDTPAYMENTCATEGORY().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.FLAVOUR, extractTable(products.getFLAVOUR().getINFO()));
		
		lookupTables_codeSystem.put(LookUpTag.LEGAL_CATEGORY, extractTable(products.getLEGALCATEGORY().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.LICENSING_AUTHORITY, extractTable(products.getLICENSINGAUTHORITY().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.LICENSING_AUTHORITY_CHANGE_REASON,
				extractTable(products.getLICENSINGAUTHORITYCHANGEREASON().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.NAMECHANGE_REASON, extractTable(products.getNAMECHANGEREASON().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.ONT_FORM_ROUTE, extractTable(products.getONTFORMROUTE().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.PRICE_BASIS, extractTable(products.getPRICEBASIS().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.REIMBURSEMENT_STATUS, extractTable(products.getREIMBURSEMENTSTATUS().getINFO()));
		
		lookupTables_codeSystem.put(LookUpTag.SPEC_CONT, extractTable(products.getSPECCONT().getINFO()));
		
		lookupTables_codeSystem.put(LookUpTag.VIRTUAL_PRODUCT_PRES_STATUS,
				extractTable(products.getVIRTUALPRODUCTPRESSTATUS().getINFO()));
		lookupTables_codeSystem.put(LookUpTag.VIRTUAL_PRODUCT_NON_AVAIL,
				extractTable(products.getVIRTUALPRODUCTNONAVAIL().getINFO()));
		lookupTables_concepts.put(LookUpTag.FORM, extractTable(products.getFORM().getINFO())); //onto-857
		lookupTables_concepts.put(LookUpTag.ROUTE, extractTable(products.getROUTE().getINFO()));//onto-857
		lookupTables_concepts.put(LookUpTag.SUPPLIER, extractTable(products.getSUPPLIER().getINFO()));//onto-857
		lookupTables_concepts.put(LookUpTag.UNIT_OF_MEASURE, extractTable(products.getUNITOFMEASURE().getINFO()));//onto-857
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
		transferComplexType(conceptRegister, propertyRigister, keyID, "DESC", "ABBREVNM", ConceptType.AMP, parents,
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
		transferComplexType(conceptRegister, propertyRigister,"PRNTAPPID" , null, null, null, null, au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.ContentType.class,
				products.getCOMBCONTENT().getCCONTENT());
		transferComplexType(conceptRegister, propertyRigister,"CHLDAPPID" , null, null, null, null, au.csiro.fhir.transforms.xml.dmd.v2_3.ampp.ContentType.class,
				products.getCOMBCONTENT().getCCONTENT());
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
		transferComplexType(conceptRegister, propertyRigister,"PRNTVPPID" , null, null, null, null, au.csiro.fhir.transforms.xml.dmd.v2_3.vmpp.ContentType.class,
				vmpp.getCOMBCONTENT().getCCONTENT());
		transferComplexType(conceptRegister, propertyRigister,"CHLDVPPID" , null, null, null, null, au.csiro.fhir.transforms.xml.dmd.v2_3.vmpp.ContentType.class,
				vmpp.getCOMBCONTENT().getCCONTENT());
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
	
	private List<ConceptDefinitionComponent> processUnitOfMeasure(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		JAXBContext context = JAXBContext
				.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.ObjectFactory.class);
		LOOKUP lookup = (LOOKUP) context.createUnmarshaller().unmarshal(new FileReader(xmlFile));
		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "CD";

		transferComplexType(conceptRegister, propertyRigister, keyID, "DESC", null, ConceptType.UNITOFMEASURE, null,
				HistoryInfoType.class, lookup.getUNITOFMEASURE().getINFO());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());

	}
	
	private List<ConceptDefinitionComponent> processForm(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		JAXBContext context = JAXBContext
				.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.ObjectFactory.class);
		LOOKUP lookup = (LOOKUP) context.createUnmarshaller().unmarshal(new FileReader(xmlFile));
		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "CD";
		transferComplexType(conceptRegister, propertyRigister, keyID, "DESC", null, ConceptType.FORM, null,
				HistoryInfoType.class, lookup.getFORM().getINFO());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());
	}
	
	private List<ConceptDefinitionComponent> processSupplier(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		JAXBContext context = JAXBContext
				.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.ObjectFactory.class);
		LOOKUP lookup = (LOOKUP) context.createUnmarshaller().unmarshal(new FileReader(xmlFile));
		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "CD";
		transferComplexType(conceptRegister, propertyRigister, keyID, "DESC", null, ConceptType.SUPPLIER, null,
				SupplierInfoType.class, lookup.getSUPPLIER().getINFO());
		conceptIDDuplicationCheck(conceptIdSet, conceptRegister, keyID);
		return new ArrayList<ConceptDefinitionComponent>(conceptRegister.values());
	}
	
	private List<ConceptDefinitionComponent> processRoute(Map<String, PropertyComponent> propertyRigister,
			Set<String> conceptIdSet, File xmlFile) throws JAXBException, FileNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		JAXBContext context = JAXBContext
				.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.ObjectFactory.class);
		LOOKUP lookup = (LOOKUP) context.createUnmarshaller().unmarshal(new FileReader(xmlFile));
		Map<String, ConceptDefinitionComponent> conceptRegister = new HashMap<String, ConceptDefinitionComponent>();
		String keyID = "CD";
		transferComplexType(conceptRegister, propertyRigister, keyID, "DESC", null, ConceptType.ROUTE, null,
				HistoryInfoType.class, lookup.getROUTE().getINFO());
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
				} else if (!fieldName.equalsIgnoreCase(idField) && v != null && v.toString().length() > 0) {
					ConceptPropertyComponent conceptPropertyComponent = new ConceptPropertyComponent();
					PropertyComponent propertyComponent = propertyRigister.get(propertyName);
					conceptPropertyComponent.setCode(propertyName);
					if (propertyComponent.getType() == PropertyType.CODING) {
						if (lookUpNameMap.containsKey(propertyName)) {
							Map<String, String> valueMap = lookupTables_codeSystem.get(lookUpNameMap.get(propertyName));
							if (v.toString().length() > 0 && valueMap != null && valueMap.get(v.toString()) != null) {
								Coding coding = new Coding();
								coding.setSystem(baseURL_CodeSystem + "/"
										+ lookUpNameMap.get(propertyName));
								coding.setCode(v.toString());
								coding.setDisplay(valueMap.get(v.toString()));
								conceptPropertyComponent.setValue(coding);
							} else {
								Coding coding = new Coding();
								coding.setSystem(baseURL_CodeSystem + "/"
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

					} 
					else if (propertyComponent.getType() == PropertyType.DATETIME) {
						conceptPropertyComponent.setValue(new DateTimeType(v.toString()));
					}

					else if (propertyComponent.getType() == PropertyType.DECIMAL) {
						// in the xsd   <xs:element name="STRNT_NMRTR_VAL"   type="xs:float"    minOccurs="0"  maxOccurs="1" />
						// So the v should always be float number
						if (!(v instanceof Float)) {
							System.out.println("check data for float value");
						}
						else {
							float vf = ((Float) v).floatValue();
							int vf_int = (int) vf;
							double diff = vf - vf_int;
							if(diff == 0) {
								//System.out.println("Change " + v.toString() + " to " + vf_int);
								conceptPropertyComponent.setValue(new DecimalType(vf_int));
							}
							else {
								//System.out.println("Keep " + v.toString());
								conceptPropertyComponent.setValue(new DecimalType(v.toString()));
							}
						}
						

					}
					
					else if (propertyComponent.getType() == PropertyType.INTEGER) {
						conceptPropertyComponent.setValue(new IntegerType(v.toString()));
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
						Map<String, String> valueMap = lookupTables_codeSystem.get(lookUpNameMap.get(propertyName));
						if (v.toString().length() > 0 && valueMap != null && valueMap.get(v.toString()) != null) {
							Coding coding = new Coding();
							coding.setSystem(baseURL_CodeSystem + "/ONT_FORM_ROUTE");
							coding.setCode(v.toString());
							coding.setDisplay(valueMap.get(v.toString()));
							conceptPropertyComponent.setValue(coding);
							//System.out.println("TEMP Solution - " + fieldName + "\t" + propertyName + "\t" + conceptPropertyComponent.getValue().toString() );
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
	
	private void validate(CodeSystem codeSystem) {
		logger.info("Validate Code System, size is " + codeSystem.getConcept().size());
		Set<String> propertyNames = new HashSet<String>();
		for(PropertyComponent p : codeSystem.getProperty()) {
			propertyNames.add(p.getCode());
		}
		for(ConceptDefinitionComponent c : codeSystem.getConcept()) {
			for(ConceptPropertyComponent cp : c.getProperty()) {
				String pName = cp.getCode();
				if(!pName.equals("parent")&&!propertyRigister.containsKey(pName)) {
					logger.severe("Validate Error : " + pName + "\t" + c.getCode());
				}
				if(cp.getValue().getClass() == Coding.class) {
					String lookupName = cp.getValueCoding().getSystem().replaceAll(baseURL_CodeSystem + "/", "");
					if(!lookupName.equals(baseURL_CodeSystem)&&LookUpTag.findByName(lookupName)==null) {
						logger.severe("Validate Error CodeSystem Reference: " + lookupName + "\t" + c.getCode());
					}
					
				}
			}
		}
	}
	
	private InputStream getFileFromResourceAsStream(String fileName) {

		// The class loader that loaded the class
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);

		// the stream holding the file content
		if (inputStream == null) {
			throw new IllegalArgumentException("file not found! " + fileName);
		} else {
			return inputStream;
		}

	}


}
