package br.com.nsym.domain.model.entity.fabrica.util;

public enum StatusProducao {
	
	COR("CORTE"),
	LAV("LAVANDERIA"),
	COS("COSTURA"),
	ACA("ACABAMENTO"),
	TIN("TINGIMENTO"),
	FAB("FABRICA"),
	DEP("DEPOSITO"),
	FIM("FINALIZADO"),
	EST("ESTAMPARIA");
	
	private final String description;
	
	private StatusProducao(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	

}
