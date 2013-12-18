package edu.upc.eetac.dsa.raul.android.libros.api;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.raul.android.libros.api.Link;

public class UserCollection {
	
	//private java.util.List<Sting> stings = new java.util.ArrayList<Sting>();
	private List<User> users = new ArrayList<User>();
	private ArrayList<Link> links;
	
	public UserCollection() {
		super();
		links = new ArrayList<Link>();
	}

	public void add(User user) {
		users.add(user);
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
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
