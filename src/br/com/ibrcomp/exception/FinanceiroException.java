package br.com.ibrcomp.exception;

public class FinanceiroException extends Exception {/**
	 * 
	 */
	private static final long serialVersionUID = -9199322548296993628L;

	public FinanceiroException(String msg) {
		super(msg);
	}
	
	public FinanceiroException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
