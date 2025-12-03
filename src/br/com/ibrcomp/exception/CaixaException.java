package br.com.ibrcomp.exception;

public class CaixaException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9199322548296993628L;

	public CaixaException(String msg) {
		super(msg);
	}
	
	public CaixaException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
