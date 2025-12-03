package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum TipoLancamento {
	tpCredito("Credito",0),
	tpDebito("Debito",1),
	tpAll("Todos",2);
	
	private final String description;
	private int cod;


	private TipoLancamento(String description, int sigla){
		this.description = description;
		this.cod = sigla;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public int getSigla(){
		return this.cod;
	}
}
