package br.com.nsym.domain.model.entity.financeiro.tools;

public enum StatusConferencia {
	
	Ok("OK","OK"),
	warn("Divergente","Warn"),
	Error("Erro","Error");
	
	private final String description;
	private final String cod;
	
	private StatusConferencia(String description, String cod) {
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

}
