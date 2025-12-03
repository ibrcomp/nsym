package br.com.ibrcomp.exception;

public class DevolucaoException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6040537824963201114L;

	public DevolucaoException(String msg) {
		super(msg);
	}
	
	public DevolucaoException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
