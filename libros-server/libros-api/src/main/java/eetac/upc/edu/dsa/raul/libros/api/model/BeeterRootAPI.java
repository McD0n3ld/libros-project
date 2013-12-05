package eetac.upc.edu.dsa.raul.libros.api.model;

import java.util.ArrayList;

import eetac.upc.edu.dsa.raul.libros.api.links.Link;

public class BeeterRootAPI {

	private ArrayList<Link> links;

	public BeeterRootAPI() {
		super();
		links = new ArrayList<Link>();
	}

	public void addLink(Link link) {
		links.add(link);
		return;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

}
