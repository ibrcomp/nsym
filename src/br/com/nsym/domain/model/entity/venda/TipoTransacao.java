package br.com.nsym.domain.model.entity.venda;

public enum TipoTransacao {
	ven("Venda","ven"),
	dev("Devolução","dev"),
	tra("Transferência","tra");
	
	private String description;
	private String cod;
	
	private TipoTransacao(String description,String cod) {
		this.description=description;
		this.cod=cod;
	}

	@Override
	public String toString() {
		return this.description;
	}
	
	public String getCod(){
		return this.cod;
	}
}
