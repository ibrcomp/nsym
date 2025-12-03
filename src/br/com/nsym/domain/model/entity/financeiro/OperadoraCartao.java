package br.com.nsym.domain.model.entity.financeiro;

public enum OperadoraCartao {
	
	ACS("1","Administradora de Cartões Sicredi Ltda."),
	ACSR("2","Administradora de Cartões Sicredi Ltda.(filial RS)"),
	AEX("3","Banco American Express S/A - AMEX"),
	BGE("4","BANCO GE - CAPITAL"),
	SAF("5","BANCO SAFRA S/A"),
	TOP("6","BANCO TOPÁZIO S/A"),
	TRI("7","BANCO TRIANGULO S/A"), 
	BIG("8","BIGCARD Adm. de Convenios e Serv."),
	BOU("9","BOURBON Adm. de Cartões de Crédito"),
	CAB("10","CABAL Brasil Ltda."),
	CET("11","CETELEM Brasil S/A - CFI"),
	CIE("12","CIELO S/A"),
	CRE("13","CREDI 21 Participações Ltda."),
	ECX("14","ECX CARD Adm. e Processadora de Cartões S/A"),
	BRA("15","Empresa Bras. Tec. Adm. Conv. Hom. Ltda. - EMBRATEC"),
	EMP("16","EMPÓRIO CARD LTDA"),
	FRE("17","FREEDDOM e Tecnologia e Serviços S/A"),
	FUN("18","FUNCIONAL CARD LTDA."),
	HIP("19","HIPERCARD Banco Multiplo S/A"),
	MAP("20","MAPA Admin. Conv. e Cartões Ltda."),
	NPA("21","Novo Pag Adm. e Proc. de Meios Eletrônicos de Pagto. Ltda."),
	PER("22","PERNAMBUCANAS Financiadora S/A Crédito, Fin. e Invest."),
	POL("23","POLICARD Systems e Serviços Ltda."), 
	PRO("24","PROVAR Negócios de Varejo Ltda."),
	RED("25","REDECARD S/A"),
	REN("26","RENNER Adm. Cartões de Crédito Ltda."),
	RPA("27","RP Administração de Convênios Ltda."),
	SAN("28","SANTINVEST S/A Crédito, Financiamento e Investimentos"),
	SOD("29","SODEXHO Pass do Brasil Serviços e Comércio S/A"),
	SOR("30","SOROCRED Meios de Pagamentos Ltda."),
	TEC("31","Tecnologia Bancária S/A - TECBAN"),
	TIC("32","TICKET Serviços S/A"),
	TRV("33","TRIVALE Administração Ltda."),
	UNI("34","Unicard Banco Múltiplo S/A - TRICARD"),
	OUT("999","Outros");
	
	

	private final String description;
	private final String cod;
	
	private OperadoraCartao(String cod,String description){
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
