package br.com.nsym.domain.model.entity.financeiro.tools;

public enum StatusCaixa {

	
	Abe("Aberto","Abe"),
	Fec("Fechado OK","Fec");
	
	private final String description;
	private final String cod;
	
	private StatusCaixa(String description, String cod) {
		this.description = description;
		this.cod = cod;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
	public String getCod(){
		return this.cod;
	}
}
