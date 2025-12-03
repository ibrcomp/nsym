package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum Sacado {
	tpFisica("Pessoa Fisica",0),
	tpJuridica("Pessoa Juridica",1),
	tpNenhum("Nenhum",3),
	tpOutros("Outros",2);
	
	private final String description;
	private int sigla;


	private Sacado(String description, int sigla){
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
