package io.sapl.test;

public class SaplTextException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2184089820092089345L;

	
	public SaplTextException() {
		
	}
	
	public SaplTextException(String message) {
		super(message);
	}
	
	public SaplTextException(String message, Exception e) {
		super(message, e);
	}
}
