package eetac.upc.edu.dsa.raul.libros.api.links;

public class Link {
	private String uri;		//url absoluto (minimo de la clase)
	private String rel;
	private String type;	//media type que produce o consume
	private String title;	//descripcion

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
