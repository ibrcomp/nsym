package br.com.nsym.domain.model.entity.tools;

public enum TipoMensagem {
	SimplesNacional("Doc. emitido por EPP...."),
	Diferimento("Redução na base de cálculo .... "),
	St("St de acordo com ....."),
	VAproTrib("Valor Total aproximado de tributos é de ...."),
	Aprov("Permite aproveitamento de ICMS .......");
	
	
	
private final String description;

	
	private TipoMensagem(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}

}
