package br.com.ibrcomp.exception;

public class FabricaException extends Exception{

	/**
	 *
	 */
	private static final long serialVersionUID = 3732861902471996291L;
	
	public FabricaException(String msg) {
		super(msg);
	}
	
	public FabricaException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
