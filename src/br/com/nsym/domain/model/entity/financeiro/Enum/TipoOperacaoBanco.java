package br.com.nsym.domain.model.entity.financeiro.Enum;

public enum TipoOperacaoBanco {
	
	tpInclui("Inclui Boleto","tpInclui"),
	tpAltera("Alterar boleto","tpAltera"),
	tpBaixa("Baixa boleto","tpBaixa"),
	tpConsulta("Consulta boleto","tpConsulta"),
	tpConsultaDetalhe("Detalhes boleto","tpConsultaDetalhe"),
	tpPixCriar("Criar Pix","tpPixCriar"),
	tpPixCancelar("Cancelar Pix","tpPixCancelar"),
	tpPixConsultar("Consultar Pix","tpPixConsultar"),
	tpCancelar("Cancelar Boleto","tpCancelar"),
	tpTicket("Ticket","tpTicket");
	
	private final String description;
	private String sigla;


	private TipoOperacaoBanco(String description, String sigla){
		this.description = description;
		this.sigla = sigla;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public String getSigla(){
		return this.sigla;
	}

}
