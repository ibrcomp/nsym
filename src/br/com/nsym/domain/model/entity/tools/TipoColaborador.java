package br.com.nsym.domain.model.entity.tools;


/**
 * 
 * @author Ibrahim Yousef Quatani
 * @version 1.0.0
 * @since 1.0.0, 19/12/2016
 *
 */
public enum TipoColaborador {

	CI("Colaborador Interno"),
	CE("Colaborador Externo");


	private final String description;


	private TipoColaborador(String description){
		this.description = description;
	}

	@Override
	public String toString(){
		return this.description;
	}
	
}

