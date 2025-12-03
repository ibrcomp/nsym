package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum Protesto {
	
	tpCorrido("Dias corridos",0),
	tpUtil("Dias uteis",1);
	
	private final String description;
	private int sigla;


	private Protesto(String description, int sigla){
		this.description = description;
		this.sigla = sigla;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public int getSigla(){
		return this.sigla;
	}

}
