package br.com.nsym.domain.model.entity.tools;

public enum TipoPagamentoComissao {

	PF("Pagamento no Faturamento"),
	PL("Pagamento na Liquidação");
	
	
	private final String description;
	
	private TipoPagamentoComissao(String description){
		this.description = description;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
}
