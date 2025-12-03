package br.com.nsym.domain.model.entity.tools;

public enum Finalidade {
	Rev("Revenda"),
	Mp("Matéria Prima"),
	Cs("Consumo / Uso"),
	In("Insumo");
	
	
	private final String description;
	
	
	private Finalidade(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
}
