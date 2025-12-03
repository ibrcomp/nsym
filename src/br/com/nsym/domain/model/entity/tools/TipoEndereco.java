package br.com.nsym.domain.model.entity.tools;
/**
 *  Classe responsável por definir o tipo de Endereço fornecido
 * 
 * @author Ibrahim Yousef Quatani
 * 
 * @version 1.0.0
 * @since 20/12/2016
 *
 */
public enum TipoEndereco {
	COM("Comercial"),
	RES("Residencial"),
	ENT("Entrega");
	
	private final String description;
	
	private TipoEndereco(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}

}
