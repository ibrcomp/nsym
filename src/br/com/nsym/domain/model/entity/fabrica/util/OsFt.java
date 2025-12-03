package br.com.nsym.domain.model.entity.fabrica.util;

public enum OsFt {


	OS("Ordem Servico"),
	FT("Ficha Tecnica"),
	NA("Nenhum");
	
	private final String description;

	private OsFt(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	@Override
	public String toString(){
		return this.description;
	}
}
