package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum TipoImpressao {
	
	tpCarne("Carne",0),
	tpPadrao("Padrao",1);
	
	private final String description;
	private int sigla;


	private TipoImpressao(String description, int sigla){
		this.description = description;
		this.sigla = sigla;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public int getSigla(){
		return this.sigla;
	}

}
