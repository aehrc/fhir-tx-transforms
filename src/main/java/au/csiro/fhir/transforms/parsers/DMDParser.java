/*
 * Copyright © 2018-2020, Commonwealth Scientific and Industrial Research Organisation (CSIRO)
 * ABN 41 687 119 230. Licensed under the CSIRO Open Source Software Licence Agreement.
*/

package au.csiro.fhir.transforms.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

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
import au.csiro.fhir.transforms.helper.FeedClient;
import au.csiro.fhir.transforms.helper.FeedUtility;
import au.csiro.fhir.transforms.helper.Utility;
import au.csiro.fhir.transforms.helper.atomio.Entry;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.HistoryInfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.InfoType;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.LOOKUP;
import au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.SupplierInfoType;
import ca.uhn.fhir.context.FhirContext;


enum ConceptType {
	AMP("AMP", "Actual medicinal product", "10363901000001102"),
	AMPP("AMPP", "Actual medicinal product pack", "10364001000001104"),
	VMP("VMP", "Virtual medicinal product", "10363801000001108"),
	VMPP("VMPP", "Virtual medicinal product pack", "8653601000001108"),
	VTM("VTM", "Virtual therapeutic moiety", "10363701000001104"),
	INGREDIENT("INGREDIENT", "Ingredient", "105590001");

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
	AVAILABILITY_RESTRICTION,
	BASIS_OF_NAME,
	BASIS_OF_STRNTH,
	COLOUR,
	COMBINATION_PACK_IND,
	COMBINATION_PROD_IND,
	CONTROL_DRUG_CATEGORY,
	DF_INDICATOR,
	DISCONTINUED_IND,
	DND,
	DT_PAYMENT_CATEGORY,
	FLAVOUR,
	FORM,
	LEGAL_CATEGORY,
	LICENSING_AUTHORITY,
	LICENSING_AUTHORITY_CHANGE_REASON,
	NAMECHANGE_REASON,
	ONT_FORM_ROUTE,
	PRICE_BASIS,
	REIMBURSEMENT_STATUS,
	ROUTE,
	SPEC_CONT,
	SUPPLIER,
	UNIT_OF_MEASURE,
	VIRTUAL_PRODUCT_PRES_STATUS,
	VIRTUAL_PRODUCT_NON_AVAIL
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
	final Pattern versionPattern = 
			Pattern.compile("(.*)(nhsbsa)(_)(dmd)(_)([0-9]+\\.[0-9]\\.[0-9])(_)([0-9]+)");

	public CodeSystem processCodeSystem(String dmdFolder, String releaseSerial, String version,String outFolder)
			throws IOException {

		// Map by concept type to concept list.
		Map<ConceptType, List<ConceptDefinitionComponent>> allConcepts = new HashMap<ConceptType, List<ConceptDefinitionComponent>>();
		// All concept ID Set
		Set<String> allConceptsIdSet = new HashSet<String>();
		
		System.out.println("Process DMD  " + version);
	
		File ingredientFile = new File(dmdFolder, "f_ingredient2_" + releaseSerial + ".xml");
		File ampFile = new File(dmdFolder, "f_amp2_" + releaseSerial + ".xml");
		File vtmFile = new File(dmdFolder, "f_vtm2_" + releaseSerial + ".xml");
		File amppFile = new File(dmdFolder, "f_ampp2_" + releaseSerial + ".xml");
		File vmpFile = new File(dmdFolder, "f_vmp2_" + releaseSerial + ".xml");
		File vmppFile = new File(dmdFolder, "f_vmpp2_" +releaseSerial + ".xml");
		File lookupFile = new File(dmdFolder, "f_lookup2_" + releaseSerial + ".xml");

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

		return codeSystem;
	}


	public void processCodeSystemWithUpdate(String dmdFolder, String dmdSerial, String outFolder,String txServerUrl, FeedClient feedClient) throws IOException {
		
		String version = getVersionNumber(dmdFolder);
		CodeSystem cs = processCodeSystem(dmdFolder,dmdSerial, version, outFolder);
		String outFileName = "CodeSystem-DMD-" + version + ".json";
		File outFile = new File( outFolder , outFileName );
		
		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().encodeResourceToString(cs), outFile);
		
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
	
	public void processSupportCodeSystemWithUpdate(String dmdFolder, String dmdSerial, String outFolder,String txServerUrl, FeedClient feedClient) throws IOException {
		
		String version = getVersionNumber(dmdFolder);
		CodeSystem cs = processCodeSystem(dmdFolder,dmdSerial, version, outFolder);
		String outFileName = "CodeSystem-DMD-" + version + ".json";
		File outFile = new File( outFolder , outFileName );
		
		FhirContext ctx = FhirContext.forR4();
		Utility.toTextFile(ctx.newJsonParser().encodeResourceToString(cs), outFile);
		
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
	
	private String getVersionNumber(String dmdFolderName) {
		int startPos = dmdFolderName.indexOf("nhsbsa_dmd");
		String fullName = dmdFolderName.substring(startPos, startPos + 30);
		String[] parts = fullName.split("_");
		String version = parts[3].substring(0, 4) + parts[2];
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
	
	private Map<LookUpTag, Map<String, String>> loadLookupTables(File lookupFile) throws JAXBException, FileNotFoundException {

		Map<LookUpTag, Map<String, String>> lookupTables = new HashMap<LookUpTag, Map<String, String>>();
		JAXBContext context = JAXBContext.newInstance(au.csiro.fhir.transforms.xml.dmd.v2_3.lookup.ObjectFactory.class);
		LOOKUP products = (LOOKUP) context.createUnmarshaller().unmarshal(new FileReader(lookupFile));
		lookupTables.put(LookUpTag.AVAILABILITY_RESTRICTION, extractTable(products.getAVAILABILITYRESTRICTION().getINFO()));
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
		lookupTables.put(LookUpTag.LICENSING_AUTHORITY_CHANGE_REASON,extractTable(products.getLICENSINGAUTHORITYCHANGEREASON().getINFO()));
		lookupTables.put(LookUpTag.NAMECHANGE_REASON, extractTable(products.getNAMECHANGEREASON().getINFO()));
		lookupTables.put(LookUpTag.ONT_FORM_ROUTE, extractTable(products.getONTFORMROUTE().getINFO()));
		lookupTables.put(LookUpTag.PRICE_BASIS, extractTable(products.getPRICEBASIS().getINFO()));
		lookupTables.put(LookUpTag.REIMBURSEMENT_STATUS, extractTable(products.getREIMBURSEMENTSTATUS().getINFO()));
		lookupTables.put(LookUpTag.ROUTE, extractTable(products.getROUTE().getINFO()));
		lookupTables.put(LookUpTag.SPEC_CONT, extractTable(products.getSPECCONT().getINFO()));
		lookupTables.put(LookUpTag.SUPPLIER, extractTable(products.getSUPPLIER().getINFO()));
		lookupTables.put(LookUpTag.UNIT_OF_MEASURE, extractTable(products.getUNITOFMEASURE().getINFO()));
		lookupTables.put(LookUpTag.VIRTUAL_PRODUCT_PRES_STATUS, extractTable(products.getVIRTUALPRODUCTPRESSTATUS().getINFO()));
		lookupTables.put(LookUpTag.VIRTUAL_PRODUCT_NON_AVAIL, extractTable(products.getVIRTUALPRODUCTNONAVAIL().getINFO()));
		
		return lookupTables;
	}

}
