package br.com.ibrcomp.exception;

public class NfeException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -6443475770727999252L;

	public NfeException(String msg) {
		super(msg);
	}
	
	public NfeException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
