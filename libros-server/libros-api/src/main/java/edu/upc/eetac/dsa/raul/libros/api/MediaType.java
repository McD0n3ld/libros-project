package edu.upc.eetac.dsa.raul.libros.api;

public interface MediaType {
	public final static String BEETER_API_USER = "application/vnd.beeter.api.user+json";
	public final static String BEETER_API_USER_COLLECTION = "application/vnd.beeter.api.user.collection+json";
	public final static String BEETER_API_STING = "application/vnd.beeter.api.sting+json";
	public final static String BEETER_API_STING_COLLECTION = "application/vnd.beeter.api.sting.collection+json";
	
	public final static String BEETER_API_ERROR = "application/vnd.dsa.beeter.error+json";
	
	public final static String BEETER_API_LINK = "application/vnd.dsa.beeter.link+json";
	public final static String BEETER_API_LINK_COLLECTION = "application/vnd.dsa.beeter.link.collection+json";
}