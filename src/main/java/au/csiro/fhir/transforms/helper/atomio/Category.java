package au.csiro.fhir.transforms.helper.atomio;

public class Category {

	private String name;

	private String scheme;

	private String label;

	public Category() {
	}

	public Category(String category, String categoryScheme, String categoryLabel) {
		super();
		this.name = category;
		this.scheme = categoryScheme;
		this.label = categoryLabel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}