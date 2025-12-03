package br.com.nsym.domain.model.entity.tools;

public enum PedidoTipo {
	
	PVE("Pedido Venda","PVE"),
	ORC("Orçamento","ORC"),
	DEV("Devolução","DEV"),
	TRA("Transferencia","TRA");
	
	private final String description;
	private final String sigla;
	
	private PedidoTipo(String description,String sigla) {
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
