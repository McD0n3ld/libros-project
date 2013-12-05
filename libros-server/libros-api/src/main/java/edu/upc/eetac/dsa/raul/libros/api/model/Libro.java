package edu.upc.eetac.dsa.raul.libros.api.model;

import edu.upc.eetac.dsa.raul.libros.api.links.Link;

import java.util.ArrayList;

public class Libro {
	private int libroid;
	private String titulo;
	private String autor;
	private String lengua;
	private String edicion;
	private java.util.Date fecha_edicion;
	private java.util.Date fecha_impresion;
	private String editorial;
	private java.util.Date lastUpdate;

	private ArrayList<Link> links;

	public Libro() {
		super();
		links = new ArrayList<Link>();
	}

	public int getLibroid() {
		return libroid;
	}

	public void setLibroid(int libroid) {
		this.libroid = libroid;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getLengua() {
		return lengua;
	}

	public void setLengua(String lengua) {
		this.lengua = lengua;
	}

	public String getEdicion() {
		return edicion;
	}

	public void setEdicion(String edicion) {
		this.edicion = edicion;
	}

	public java.util.Date getFecha_edicion() {
		return fecha_edicion;
	}

	public void setFecha_edicion(java.util.Date fecha_edicion) {
		this.fecha_edicion = fecha_edicion;
	}

	public java.util.Date getFecha_impresion() {
		return fecha_impresion;
	}

	public void setFecha_impresion(java.util.Date fecha_impresion) {
		this.fecha_impresion = fecha_impresion;
	}

	public String getEditorial() {
		return editorial;
	}

	public void setEditorial(String editorial) {
		this.editorial = editorial;
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

	public java.util.Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(java.util.Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
