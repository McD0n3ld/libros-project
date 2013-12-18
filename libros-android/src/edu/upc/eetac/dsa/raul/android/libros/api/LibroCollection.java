package edu.upc.eetac.dsa.raul.android.libros.api;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.raul.android.libros.api.Link;

public class LibroCollection {

	private List<Libro> libros = new ArrayList<Libro>();
	private ArrayList<Link> links;

	public LibroCollection() {
		super();
		links = new ArrayList<Link>();
	}

	public void add(Libro libro) {
		libros.add(libro);
	}

	public List<Libro> getLibros() {
		return libros;
	}

	public void setLibros(List<Libro> libro) {
		this.libros = libro;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

	public void addLink(Link link) {
		links.add(link);
		return;
	}

}
