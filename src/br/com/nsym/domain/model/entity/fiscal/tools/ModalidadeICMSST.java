package br.com.nsym.domain.model.entity.fiscal.tools;

public enum ModalidadeICMSST {
	MVA("Margem de valor agregado(%)","4"),
	PT("Pauta(Valor)","5"),
	Tab("Preço Tabelado Máximo(Valor)","0"),
	LNEG("Lista Negativa(Valor)","1"),
	LPOS("Lista Positiva(Valor)","2"),
	LNEU("Lista Neutra(Valor)","3");
	
private final String description;
private final String cod;
	
	private ModalidadeICMSST(String description, String cod){
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
