package br.com.ibrcomp.exception;

public class RecebimentoNFeException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9199322548296993628L;

	public RecebimentoNFeException(String msg) {
		super(msg);
	}
	
	public RecebimentoNFeException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
