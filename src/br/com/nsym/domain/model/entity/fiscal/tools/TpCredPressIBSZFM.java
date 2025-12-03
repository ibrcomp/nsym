package br.com.nsym.domain.model.entity.fiscal.tools;

import lombok.Getter;

public enum TpCredPressIBSZFM {
	
	ZF0("Sem Crédito Presumido",0),
	ZF1("Bens de consumo final (55%)",1),
	ZF2("Bens de capital (75%)",2),
	ZF3("Bens intermediários (90,25%)",3),
	ZF4("Bens de informática e outros definidos em legislação (100%)",4);

	private final String description;
	@Getter
	private int codigo;

	private TpCredPressIBSZFM(String description,int codigo){
		this.description = description;
		this.codigo = codigo;
	}

	@Override
	public String toString(){
		return this.description;
	}

}
