package br.com.nsym.domain.model.entity.tools;

import lombok.Getter;

public enum FiscalStatus {
	NE("Não Emitida","NEM"),
	ES("Emitida Sat","EST"),
	ED("Emitida Danfe","EDF"),
	GA("Gerada,aguardando transmissão","GAT"),
	GT("Gerada,Transmitida","GTR");
	
	private final String description;
	@Getter
	private final String sigla;
	
	private FiscalStatus(String description, String sigla) {
		this.description = description;
		this.sigla = sigla;
	}
	
	@Override
	public String toString() {
		return this.description;
	}
	
	
}