package au.csiro.fhir.transforms.helper.atomio;

public class Artefact {

	  private String type;


	  private String sha256;


	  private Long length;


	  private String filename;

	
	  private String path;

	  public Artefact() {}

	  public Artefact(String type, String sha256, Long length, String filename, String path) {
	    super();
	    this.type = type;
	    this.sha256 = sha256;
	    this.length = length;
	    this.filename = filename;
	    this.path = path;
	  }
	  
	  public Artefact(String type, String filename) {
		    super();
		    this.type = type;
		    this.filename = filename;

	  }
	  
	  public Artefact(String type) {
		    super();
		    this.type = type;

	  }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSha256() {
		return sha256;
	}

	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}