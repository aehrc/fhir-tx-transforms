package au.csiro.fhir.transforms.helper.atomio;

public class Author {
	  private String name;

	  private String url;

	  private String email;

	  public Author() {}

	  public Author(String name, String url, String email) {
	    super();
	    this.name = name;
	    this.url = url;
	    this.email = email;
	  }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	  

}
