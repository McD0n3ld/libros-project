package edu.upc.eetac.dsa.raul.libros.api;

public interface MediaType {
	public final static String LIBROS_API_USER = "application/vnd.libros.api.user+json";
	public final static String LIBROS_API_USER_COLLECTION = "application/vnd.libros.api.user.collection+json";
	
	public final static String LIBROS_API_LIBRO = "application/vnd.libros.api.libro+json";
	public final static String LIBROS_API_LIBRO_COLLECTION = "application/vnd.libros.api.libro.collection+json";
	
	public final static String LIBROS_API_ERROR = "application/vnd.dsa.libros.error+json";
	
	public final static String LIBROS_API_LINK = "application/vnd.dsa.libros.link+json";
	public final static String LIBROS_API_LINK_COLLECTION = "application/vnd.dsa.libros.link.collection+json";
}