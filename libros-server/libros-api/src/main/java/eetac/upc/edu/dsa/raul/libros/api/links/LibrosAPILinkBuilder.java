package eetac.upc.edu.dsa.raul.libros.api.links;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import edu.upc.eetac.dsa.raul.libros.api.LibrosRootAPIResource;
import edu.upc.eetac.dsa.raul.libros.api.MediaType;
import edu.upc.eetac.dsa.raul.libros.api.LibroResource;
import eetac.upc.edu.dsa.raul.libros.api.model.Libro;


public class LibrosAPILinkBuilder {
	public final static Link buildURIRootAPI(UriInfo uriInfo) { //getBase -> http:blablabla/beeter-api/
		URI uriRoot = uriInfo.getBaseUriBuilder().path(LibrosRootAPIResource.class).build();
		Link link = new Link();
		link.setUri(uriRoot.toString());
		link.setRel("self bookmark"); //apunta a el mismo / pagina inicial
		link.setTitle("Libros API");
		link.setType(MediaType.LIBROS_API_LINK_COLLECTION); //devolver coleccion de enlaces

		return link;
	}

	public static final Link buildURIStings(UriInfo uriInfo, String rel) {
		return buildURIStings(uriInfo, null, null, null, rel);
	}

	public static final Link buildURIStings(UriInfo uriInfo, String offset, String length, String username, String rel) {
		URI uriStings;
		if (offset == null && length == null)
			uriStings = uriInfo.getBaseUriBuilder().path(LibroResource.class).build();	//devuelve http:blabla/stings
		else {
			if (username == null)
				uriStings = uriInfo.getBaseUriBuilder().path(LibroResource.class).queryParam("offset", offset).queryParam("length", length).build();
			else
				uriStings = uriInfo.getBaseUriBuilder().path(LibroResource.class).queryParam("offset", offset).queryParam("length", length)
						.queryParam("username", username).build();
		}

		Link self = new Link();
		self.setUri(uriStings.toString());
		self.setRel(rel);
		self.setTitle("Libro collection");
		self.setType(MediaType.LIBROS_API_LIBRO_COLLECTION);

		return self;
	}

	public static final Link buildTemplatedURIStings(UriInfo uriInfo, String rel) {

		return buildTemplatedURIStings(uriInfo, rel, false);
	}

	public static final Link buildTemplatedURIStings(UriInfo uriInfo, String rel, boolean username) {
		URI uriStings;
		if (username)
			uriStings = uriInfo.getBaseUriBuilder().path(LibroResource.class).queryParam("offset", "{offset}").queryParam("length", "{length}")
					.queryParam("username", "{username}").build();
		else
			uriStings = uriInfo.getBaseUriBuilder().path(LibroResource.class).queryParam("offset", "{offset}").queryParam("length", "{length}").build();

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriStings));
		link.setRel(rel);
		if (username)
			link.setTitle("Libro collection resource filtered by {username}");
		else
			link.setTitle("Libro collection resource");
		link.setType(MediaType.LIBROS_API_LIBRO_COLLECTION);

		return link;
	}

	public final static Link buildURISting(UriInfo uriInfo, Libro libro) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(LibroResource.class).build();
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel("self");
		link.setTitle("Libro " + libro.getLibroid());
		link.setType(MediaType.LIBROS_API_LIBRO);

		return link;
	}

	public final static Link buildURILibroId(UriInfo uriInfo, int libroid, String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(LibroResource.class).path(LibroResource.class, "getLibro").build(libroid);
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel(rel);
		link.setTitle("Libro " + libroid);
		link.setType(MediaType.LIBROS_API_LIBRO);

		return link;
	}

}
