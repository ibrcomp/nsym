package br.com.nsym.domain.model.entity.cadastro.tools;

public enum VersaoSat {

	A("0.07"),
	B("0.08"),
	C("0.09");
	
	private final String description;
	
	private VersaoSat(String description) {
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
	
}
