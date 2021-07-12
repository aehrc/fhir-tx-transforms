package au.csiro.fhir.transforms.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ConceptMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.csiro.fhir.transforms.helper.atomio.Artefact;
import au.csiro.fhir.transforms.helper.atomio.Author;
import au.csiro.fhir.transforms.helper.atomio.Category;
import au.csiro.fhir.transforms.helper.atomio.Entry;


public class FeedUtility {
	
	static Set<String> defaultPermission = new HashSet<>(Arrays.asList("restricted.read"));
		
	
	public static Entry createFeedEntry_CodeSystem(CodeSystem codeSystem, String fileName) {
		Entry entry = new Entry();
		
		entry.getAuthors().add(new Author("AeHRC", "http://aehrc.com", "ontoserver-support@csiro.au"));
		entry.getCategories().add(new Category("FHIR_CodeSystem", "http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0", "FHIR CodeSystem"));
		entry.setContentItemIdentifier(codeSystem.getUrl());
		entry.setContentItemVersion(codeSystem.getVersion());
		entry.setFhirVersion("4.0.1");
		entry.getArtefacts().add(new Artefact("application/fhir+json"));
		entry.setRights(codeSystem.getCopyright());
	    entry.setSummary(codeSystem.getDescription());
	    entry.setTitle(codeSystem.getTitle());
	    entry.setPermissions(defaultPermission);
		return entry;
	}
	
	public static Entry createFeedEntry_Bundle(Bundle bundle, String fileName, String version) {
		Entry entry = new Entry();
		
		entry.getAuthors().add(new Author("AeHRC", "http://aehrc.com", "ontoserver-support@csiro.au"));
		entry.getCategories().add(new Category("FHIR_Bundle", "http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0", "FHIR Bundle"));
		entry.setContentItemIdentifier(bundle.getId());
		entry.setTitle(bundle.getId());
		entry.setFhirVersion("4.0.1");
		entry.getArtefacts().add(new Artefact("application/fhir+json"));
	    entry.setPermissions(defaultPermission);
	    entry.setContentItemVersion(version);
		return entry;
	}
	
	public static Entry createFeedEntry_ConceptMap(ConceptMap conceptMap, String fileName) {
		Entry entry = new Entry();
		entry.getAuthors().add(new Author("AeHRC", "http://aehrc.com", "ontoserver-support@csiro.au"));
		entry.getCategories().add(new Category("FHIR_ConceptMap", "http://ns.electronichealth.net.au/ncts/syndication/asf/scheme/1.0.0", "FHIR ConceptMap"));
		entry.setContentItemIdentifier(conceptMap.getUrl());
		entry.setContentItemVersion(conceptMap.getVersion());
		entry.setFhirVersion("4.0.1");
		entry.getArtefacts()
	        .add(new Artefact("application/fhir+json",  fileName));
		entry.setRights(conceptMap.getCopyright());
	    entry.setSummary(conceptMap.getDescription());
	    entry.setTitle(conceptMap.getTitle());
	    entry.setPermissions(defaultPermission);
		return entry;
	}
	
	public static String entryToJson(Entry entry) {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entry);
            System.out.println("ResultingJSONstring = " + json);     
        } catch (JsonProcessingException e) {
            e.printStackTrace();
           
        }
        return json;

	}
}
