package br.com.nsym.domain.model.entity.tools;

public enum Operadora {
	VIVO("Vivo"),
	OI("Oi"),
	CLARO("Claro"),
	TIM("Tim"),
	PORTO("Porto Seguro");
	
	private final String description;
	
	private Operadora(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
}
