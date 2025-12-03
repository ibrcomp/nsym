package br.com.ibrcomp.exception;

public class EstoqueRuntimeException extends RuntimeException {
	 /**
	 *
	 */
	private static final long serialVersionUID = 1705821388076207620L;

	public EstoqueRuntimeException(String message) {
	        super(message);
	    }

	    public EstoqueRuntimeException(String message, Throwable cause) {
	        super(message, cause);
	    }


}
