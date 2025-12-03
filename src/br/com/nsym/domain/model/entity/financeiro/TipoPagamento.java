package br.com.nsym.domain.model.entity.financeiro;

public enum TipoPagamento {
	Din("Dinheiro","01"),
	Che("Cheque","02"),
	Car("Cartão de Crédito","03"),
	Cde("Cartão de Débito","04"),
	Crl("Crédio Loja","05"),
	Val("Vale Alimentação","10"),
	Vre("Vale Refeição","11"),
	Vpr("Vale Presente","12"),
	Vco("Vale Combustível","13"),
	Dpm("Duplicata Mercantil","14"),
	Bol("Boleto Bancário","15"),
	Dbc("Depósito Bancário","16"),
	Pix("PIX - Pagamento Instantaneo","17"),
	Tbc("Transferencia Bancaria","18"),
	Pfd("Progama Fidelidade, CashBack","19"),
	Spg("Sem pagamento","90"),
	Fun("Fundo de Caixa","93"),
	Out("Outros","99");

	private final String description;
	private final String cod;

	
	private TipoPagamento(String description,String cod){
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
	
	public static String pegaPorIndice(int num) {
		TipoPagamento[] tipo =  TipoPagamento.values();
			return tipo[num].toString();
	}
	
}
