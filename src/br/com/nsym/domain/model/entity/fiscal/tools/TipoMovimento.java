package br.com.nsym.domain.model.entity.fiscal.tools;

import lombok.Getter;

public enum TipoMovimento {

	SA("Saída",1),
	EN("Entrada",0);
	
private final String description;
@Getter
private final int codigo;
	
	private TipoMovimento(String description,int codigo){
		this.description = description;
		this.codigo = codigo;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
}
