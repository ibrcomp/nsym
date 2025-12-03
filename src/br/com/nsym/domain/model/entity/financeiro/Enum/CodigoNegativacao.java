package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum CodigoNegativacao {
	tpNenhum("Nenhum",0),
	tpProtestarCorrido("Protestar Corrido",1),
	tpProtestarUteis("Protestar Uteis",2),
	tpNaoProtestar("Nao Protestar",3),
	tpNegativar("Negativar",4),
	tpNaoNegativar("Nao negativar",5),
	tpCancelamento("Cancelamento",6);
	
	
	private final String description;
	private int sigla;


	private CodigoNegativacao(String description, int sigla){
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
