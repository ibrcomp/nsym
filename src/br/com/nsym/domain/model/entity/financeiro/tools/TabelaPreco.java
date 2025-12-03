package br.com.nsym.domain.model.entity.financeiro.tools;

public enum TabelaPreco {
	TZ("Tabela Custo","99"),
	TA("Tabela A","01"),
	TB("Tabela B","02"),
	TC("Tabela C","03"),
	TD("Tabela D","04"),
	TE("Tabela E","05");
	
	
	private final String description;
	private final String cod;

	
	private TabelaPreco(String description,String cod){
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
