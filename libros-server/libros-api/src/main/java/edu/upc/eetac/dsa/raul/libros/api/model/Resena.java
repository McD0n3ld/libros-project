package edu.upc.eetac.dsa.raul.libros.api.model;

import java.util.ArrayList;

import edu.upc.eetac.dsa.raul.libros.api.links.Link;

public class Resena {
	private int resenaid;
	private int libroid;
	private String username;
	private java.util.Date lastUpdate;
	private String content;
	private String name;

	private ArrayList<Link> links;

	public Resena() {
		super();
		links = new ArrayList<Link>();
	}

	public int getResenaid() {
		return resenaid;
	}

	public void setResenaid(int resenaid) {
		this.resenaid = resenaid;
	}

	public int getLibroid() {
		return libroid;
	}

	public void setLibroid(int libroid) {
		this.libroid = libroid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public java.util.Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(java.util.Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
