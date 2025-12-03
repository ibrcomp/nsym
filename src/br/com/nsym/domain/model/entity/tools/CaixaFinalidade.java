package br.com.nsym.domain.model.entity.tools;

public enum CaixaFinalidade {
	aber("Abertura","aber"),
	fech("Fechamento","fech"),
	extr("Extrato","extr"),
	nao("nenhum","nao"),
	rece("Recebimento","rece");
	
	
	private final String description;
	private final String sigla;
	
	private CaixaFinalidade(String description,String sigla){
		this.description = description;
		this.sigla = sigla;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
	public String getSigla() {
		return this.sigla; 
	}
}
