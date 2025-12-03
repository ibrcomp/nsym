package br.com.nsym.domain.model.entity.financeiro.tools;

public enum MovimentoEnum {

	ABre("Abertura de Caixa","ABR"),
	Fech("Fechamento de Caixa","Fech"),
	Rec("Recebimento Caixa","Rec"),
	Ret("Retirada de Caixa","Ret"),
	Ent("Entrada de Caixa","Ent"),
	Tit("Titulo","Tit"),
	Reab("Reaberto","Reab");
	
	private final String description;
	private final String cod;
	
	private MovimentoEnum (String description, String cod ) {
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
