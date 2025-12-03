package br.com.ibrcomp.exception;

import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.fiscal.Ncm;

public class TributosException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8486220311581006943L;
	
	public TributosException(String msg){
		super(msg);
	}
	
	public TributosException(String msg,Throwable cause){
		super(msg,cause);
	}
	
	public TributosException(Produto produto){
		super("tributosException.NCM" + produto.getReferencia());
	}
	
	public TributosException(Ncm ncm){
		super("tributosException.tributo" + ncm.getNcm());
	}
	
	public TributosException(String msg,Ncm ncm){
		super(msg + ncm.getNcm());
	}
}
