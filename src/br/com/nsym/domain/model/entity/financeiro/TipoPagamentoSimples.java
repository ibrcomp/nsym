package br.com.nsym.domain.model.entity.financeiro;

public enum TipoPagamentoSimples {
	
	Din("Dinheiro","01"),
	Che("Cheque","02"),
	Car("Cartão de Crédito","03"),
	Cde("Cartão de Débito","04"),
	Bol("Boleto Bancário","15"),
	Dbc("Depósito Bancário","16"),
	Pix("PIX - Pagamento Instantaneo","17"),
	Tbc("Transferencia Bancaria","18"),
	Fun("Fundo de Caixa","93"),
	Crl("Crédio Loja","05"),
	Out("Outros","99");

	private final String description;
	private final String cod;

	
	private TipoPagamentoSimples(String description,String cod){
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
	
	public TipoPagamentoSimples achaPagamentoPorCodigo(String cod){
		TipoPagamentoSimples tipoTemporario = TipoPagamentoSimples.Out;
		for (TipoPagamentoSimples tipo : TipoPagamentoSimples.values()) {
			if (cod.equals(tipo.getCod())){
				tipoTemporario = tipo;
			}
		}
		return tipoTemporario;
	}

}
