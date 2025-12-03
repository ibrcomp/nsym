package br.com.nsym.domain.model.entity.financeiro;

public enum CodTipoPagamento {
	tp01("Dinheiro","01"),
	tp02("Cheque","02"),
	tp03("Cartão de Crédito","03"),
	tp04("Cartão de Débito","04"),
	tp05("Crédio Loja","05"),
	tp10("Vale Alimentação","10"),
	tp11("Vale Refeição","11"),
	tp12("Vale Presente","12"),
	tp13("Vale Combustível","13"),
	tp14("Duplicata Mercantil","14"),
	tp15("Boleto Bancário","15"),
	tp90("Sem pagamento","90"),
	tp99("Outros","99");

	private final String description;
	private final String cod;

	
	private CodTipoPagamento(String description,String cod){
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
	
	public String valueOf(){
		return this.cod;
	}

}
