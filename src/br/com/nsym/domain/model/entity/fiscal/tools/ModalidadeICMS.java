package br.com.nsym.domain.model.entity.fiscal.tools;

public enum ModalidadeICMS {
	MVA("Margem de valor agregado","0"),
	PT("Pauta(Valor)","1"),
	Tab("Preço Tabelado Máximo(Valor)","2"),
	VP("Valor de operação","3");
	
private final String description;
private final String cod;
	
	private ModalidadeICMS(String description,String cod){
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
