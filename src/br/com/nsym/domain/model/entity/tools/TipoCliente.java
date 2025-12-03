package br.com.nsym.domain.model.entity.tools;

public enum TipoCliente {
	
	Est("Estrangeiro"),
	Rev("Padrão"),
	Cfi("Consumidor Final / CNPJ"),
	CfC("Consumidor Final / CPF");
	
	
	private final String description;
	
	
	private TipoCliente(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}

}
