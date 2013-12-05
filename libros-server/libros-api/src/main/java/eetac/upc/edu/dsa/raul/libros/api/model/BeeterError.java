package eetac.upc.edu.dsa.raul.libros.api.model;

public class BeeterError {
	private int status;
	private String message;
 
	public BeeterError() {
		super();
	}
 
	public BeeterError(int status, String message) {
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
