package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum TipoDesconto {

	tpNaoConceder("Nao conceder desconto",0),
	tpValorFixo("Valor fixo ate data informada",1),
	tpPercentual("Percentual ate data informada",2),
	tpValorAntecipacaoDiaCorrido("Valor Antecipacao Dia Corrido",3),
	tpValorAntecipacaoDiaUtil("Valor antecipacao dia util",4),
	tpPercentualSobreValorNominalDiaCorrido("Percentual sobre valor nominal dia corrido",5),
	tpPercentualSobreValorNominalDiaUtil("Percentual sobre valor nominal dia util",6),
	tpCancelamentoDesconto("Cancelamento desconto",7);
	
	private final String description;
	private int sigla;


	private TipoDesconto(String description, int sigla){
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
