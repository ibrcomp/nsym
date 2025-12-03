package br.com.ibrcomp.exception;

public class RegraNegocioException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9199322548296993628L;

	public RegraNegocioException(String msg) {
		super(msg);
	}
	
	public RegraNegocioException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
