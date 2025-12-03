package br.com.nsym.domain.model.entity.fiscal.tools;

public enum MotivoDesonera {
	TX("Táxi"),
	PA("Produtor Agropecuário"),
	LOC("Frotista/Locadora"),
	DIP("Diplomático/Consular"),
	UTL("Utilit. e Motoc. da Amazônia Ocid"),
	SUF("SUFRAMA"),
	VPB("Venda a Órgãos Públicos"),
	OUT("Outros"),
	DEF("Deficiente Condutor"),
	DNC("Deficiente Não Condutor"),
	OFD("Orgão de fomento e desenvolvimento");
	
private final String description;
	
	private MotivoDesonera(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
}
