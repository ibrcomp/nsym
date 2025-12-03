package br.com.nsym.domain.model.entity.fiscal.tools;

public enum TipoPesquisa{
	CLI("Cliente"),
	FOR("Fornecedor"),
	COL("Colaborador"),
	FIL("Filial"),
	MAT("Matriz");
	
	private final String description;
	
	
	private TipoPesquisa(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
}
