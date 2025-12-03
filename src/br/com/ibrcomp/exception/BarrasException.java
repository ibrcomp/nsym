package br.com.ibrcomp.exception;

public class BarrasException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -6443475770727999252L;

	public BarrasException(String msg) {
		super(msg);
	}
	
	public BarrasException(String msg,Throwable cause) {
		super(msg,cause);
	}
}
