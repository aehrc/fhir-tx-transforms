package au.csiro.fhir.transforms.helper.atomio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Entry {

	private UUID id;

	private String title;

	private List<Author> authors = new ArrayList<>();

	private String rights;

	private Date published;

	private Date updated;

	private String summary;

	private String contentItemIdentifier;

	private String contentItemVersion;

	private Set<Category> categories = new HashSet<>();

	private String fhirVersion;

	private Set<String> fhirProfiles = new HashSet<>();

	private List<Artefact> artefacts = new ArrayList<>();

	private Set<String> permissions = new HashSet<>();

	/*
	 * public Entry() { this.id = UUID.randomUUID(); Date now = new Date();
	 * this.updated = now; this.published = now; }
	 */
	public Entry() {

	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContentItemIdentifier() {
		return contentItemIdentifier;
	}

	public void setContentItemIdentifier(String contentItemIdentifier) {
		this.contentItemIdentifier = contentItemIdentifier;
	}

	public String getContentItemVersion() {
		return contentItemVersion;
	}

	public void setContentItemVersion(String contentItemVersion) {
		this.contentItemVersion = contentItemVersion;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public String getFhirVersion() {
		return fhirVersion;
	}

	public void setFhirVersion(String fhirVersion) {
		this.fhirVersion = fhirVersion;
	}

	public Set<String> getFhirProfiles() {
		return fhirProfiles;
	}

	public void setFhirProfiles(Set<String> fhirProfiles) {
		this.fhirProfiles = fhirProfiles;
	}

	public List<Artefact> getArtefacts() {
		return artefacts;
	}

	public void setArtefacts(List<Artefact> artefacts) {
		this.artefacts = artefacts;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
}
