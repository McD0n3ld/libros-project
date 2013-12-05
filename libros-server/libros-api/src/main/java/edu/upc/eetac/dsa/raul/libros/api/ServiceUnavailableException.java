package edu.upc.eetac.dsa.raul.libros.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import eetac.upc.edu.dsa.raul.libros.api.model.BeeterError;

public class ServiceUnavailableException extends WebApplicationException {
	private static final long serialVersionUID = -1284310111307676552L;

	public ServiceUnavailableException(String message) {
		super(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(new BeeterError(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), message))
				.type(MediaType.BEETER_API_ERROR).build());
	}
}
