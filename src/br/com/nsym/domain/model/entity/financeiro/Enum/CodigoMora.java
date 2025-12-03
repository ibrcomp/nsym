package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum CodigoMora {
	tpValorDiario("Valor Diario",1),
	tpTaxaPercentualMensal("Taxa percentual mensal",2);
	
	
	private final String description;
	private int sigla;


	private CodigoMora(String description, int sigla){
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
