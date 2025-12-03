package br.com.ibrcomp.exception;

public class EstoqueException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 647074637892187124L;
	
	public EstoqueException(String msg) {
		super(msg);
	}
	
	public EstoqueException(String msg,Throwable cause) {
		super(msg,cause);
	}
}
