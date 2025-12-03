package br.com.ibrcomp.exception;

public class TotaisCFeException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2670423981800367080L;

	public TotaisCFeException(String msg){
		super(msg);
	}
	
	public TotaisCFeException(String msg,Throwable cause){
		super(msg,cause);
	}
}
