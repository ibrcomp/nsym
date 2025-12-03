package br.com.nsym.domain.model.entity.fiscal.tools;

public enum TipoCalculo {

	TP("Percentual"),
	TV("Em Valor");
	
private final String description;
	
	private TipoCalculo(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
}
