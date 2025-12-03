package br.com.nsym.domain.model.entity.fiscal.tools;

import lombok.Getter;

public enum FinalidadeNfe {

	NO("Normal",1),
	CO("Complementar",2),
	AJ("Ajuste",3),
	DV("Devolução de mercadoria",4),
	NC("Nota de crédito",5),
	ND("Nota de débito",6);

	private final String description;
	@Getter
	private int codigo;

	private FinalidadeNfe(String description,int codigo){
		this.description = description;
		this.codigo = codigo;
	}

	@Override
	public String toString(){
		return this.description;
	}
	
}


