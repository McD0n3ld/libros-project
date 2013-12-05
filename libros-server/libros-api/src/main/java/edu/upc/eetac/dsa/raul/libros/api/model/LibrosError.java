package edu.upc.eetac.dsa.raul.libros.api.model;

public class LibrosError {
	private int status;
	private String message;
 
	public LibrosError() {
		super();
	}
 
	public LibrosError(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
 
	public int getStatus() {
		return status;
	}
 
	public void setStatus(int status) {
		this.status = status;
	}
 
	public String getMessage() {
		return message;
	}
 
	public void setMessage(String message) {
		this.message = message;
	}
}
