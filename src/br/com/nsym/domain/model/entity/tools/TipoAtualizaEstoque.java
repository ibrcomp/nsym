package br.com.nsym.domain.model.entity.tools;

public enum TipoAtualizaEstoque {
	
	Total("Total - Seta o estoque, zerando o que não foi digitado"),
	ParcTotal("Parcial - A quantidade informada é a quantidade que existe em estoque");
	
	private final String description;
	
	private TipoAtualizaEstoque(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}

}
