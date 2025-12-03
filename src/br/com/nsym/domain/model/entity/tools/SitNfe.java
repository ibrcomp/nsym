package br.com.nsym.domain.model.entity.tools;

public enum SitNfe {
	
	Aut("Uso autorizado","1"),
	Den("Uso denegado","2"),
	Can("NF-e Cancelada","3");
	
	
	private final String description;
	private final String cod;
	
	private SitNfe(String description,String sigla) {
		this.description = description;
		this.cod = sigla;
	}
	
	@Override
	public String toString() {
		return this.description;
	}

	public String getCod() {
		return this.cod;
	}
}
