package br.com.nsym.domain.model.entity.financeiro;

public enum ParcelaStatus {
	
	REC("Recebido","REC"),
	PAR("Parcial Recebido","PAR"),
	PAB("Parcial Aberto","PAB"),
	NAO("Não Considerar","NAO"),
	AGR("AGRUPADO","AGR"),
	ALL("TODOS","ALL"),
	ABE("Pendente","ABE");
	
	
	
	private final String description;
	private final String cod;
	
	private ParcelaStatus(String description, String cod) {
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
	
	public ParcelaStatus achaParcelaPorCodigo(String cod){
		ParcelaStatus tipoTemporario = ParcelaStatus.REC;
		for (ParcelaStatus tipo : ParcelaStatus.values()) {
			if (cod.equals(tipo.getCod())){
				tipoTemporario = tipo;
			}
		}
		return tipoTemporario;
	}

}
