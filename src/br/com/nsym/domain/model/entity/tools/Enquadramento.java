package br.com.nsym.domain.model.entity.tools;

public enum Enquadramento {
	SimplesNacional("Simples Nacional","1"),
	SimplesNacionalExcecao("Simples Nacional com Exceção","2"),
	SimplesNacionalMei("Simples Nacional - MEI","4"),
	Normal("Regime Normal","3");
	
private final String description;
private final String cod;
	
	private Enquadramento(String description, String cod){
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
