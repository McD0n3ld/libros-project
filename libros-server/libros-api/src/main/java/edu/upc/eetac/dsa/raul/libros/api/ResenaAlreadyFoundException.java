package edu.upc.eetac.dsa.raul.libros.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import edu.upc.eetac.dsa.raul.libros.api.model.LibrosError;

public class ResenaAlreadyFoundException extends WebApplicationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4110163799971920585L;
	private final static String MESSAGE = "Resena already found";
	public ResenaAlreadyFoundException() {
		super(Response
				.status(Response.Status.FOUND)
				.entity(new LibrosError(Response.Status.FOUND.getStatusCode(), MESSAGE))
				.type(MediaType.LIBROS_API_ERROR).build());
	}
}
