package br.com.nsym.domain.model.entity.fiscal.tools;

public enum StatusNfe {
	
	EN("Transmitido, Autorizado","EN"),
	SA("Salvo, aguardando envio","SA"),
	EE("Não Transmitido, Erro","EE"),
	CA("NFE Cancelada","CA"),
	IN("Inutilizado","IN");
	
private final String description;
private final String sigla;
	
	private StatusNfe(String description,String sigla){
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
