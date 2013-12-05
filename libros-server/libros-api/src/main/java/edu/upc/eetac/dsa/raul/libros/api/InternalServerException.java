package edu.upc.eetac.dsa.raul.libros.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.raul.libros.api.model.BeeterError;

public class InternalServerException extends WebApplicationException {
	private static final long serialVersionUID = -1284310111307676552L;

	public InternalServerException(String message) {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new BeeterError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message))
				.type(MediaType.BEETER_API_ERROR).build());
	}
}