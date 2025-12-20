package br.com.ibrcomp.exception;

public class ParametroException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 647074637892187124L;
	
	public ParametroException(String msg) {
		super(msg);
	}
	
	public ParametroException(String msg,Throwable cause) {
		super(msg,cause);
	}
}
