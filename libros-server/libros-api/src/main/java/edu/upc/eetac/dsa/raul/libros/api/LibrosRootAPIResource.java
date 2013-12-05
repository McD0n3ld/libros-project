package edu.upc.eetac.dsa.raul.libros.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.raul.libros.api.links.LibrosAPILinkBuilder;
import eetac.upc.edu.dsa.raul.libros.api.model.LibrosRootAPI;

@Path("/")
public class LibrosRootAPIResource {

	@Context
	private UriInfo uriInfo;

	@GET
	@Produces(MediaType.LIBROS_API_LINK_COLLECTION)
	public LibrosRootAPI getLinkResources() {
		LibrosRootAPI bra = new LibrosRootAPI();
		bra.addLink(LibrosAPILinkBuilder.buildURIRootAPI(uriInfo));
		bra.addLink(LibrosAPILinkBuilder.buildTemplatedURIStings(uriInfo,"libros",true)); //rel = que es este enlace
		bra.addLink(LibrosAPILinkBuilder.buildTemplatedURIStings(uriInfo,"libros",false)); //rel = que es este enlace
		bra.addLink(LibrosAPILinkBuilder.buildURIStings(uriInfo, "0", "5", "manolo", "sting"));
		return bra;		
	}
}
