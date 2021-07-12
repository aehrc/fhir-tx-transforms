package au.csiro.fhir.transforms.helper.atomio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Feed {
	  private String name;
	  private UUID uuid;
	  private String title;

	  private String subtitle;
	  private Date published;
	  private Date updated;

	  private List<Entry> entries = new ArrayList<>();

	  private Set<String> permissions = new HashSet<>();

	  public Feed() {
	    super();
	    Date now = new Date();
	    this.updated = now;
	    this.published = now;
	  }

	  public Feed(String name, String title, String subTitle) {
	    this();
	    this.uuid = UUID.randomUUID();
	    this.name = name;
	    this.title = title;
	    this.subtitle = subTitle;
	  }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
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

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
	  
}