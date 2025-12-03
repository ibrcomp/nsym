package br.com.nsym.domain.model.entity.tools;

public enum PedidoStatus {
	
	AgR("Aguardando Recebimento","ARC"),
	CAN("Cancelado","CAN"),
	APC("Aguardando Aprovação Cliente","AGC"),
	Agp("Agrupado","AGP"),
	REC("Recebido","REC");
	

	private final String description;
	private final String sigla;
	
	private PedidoStatus(String description, String sigla) {
		this.description = description;
		this.sigla = sigla;
	}
	
	@Override
	public String toString() {
		return this.description;
	}
	
	public String getSigla() {
		return this.sigla;
	}
}
