package br.com.nsym.domain.model.entity.fiscal.tools;

import java.math.BigDecimal;

public enum TipoNFDebito {
	D01("01 - Transferência de créditos para Cooperativas",new BigDecimal("01")),
	D02("02 - Anulação de Crédito por Saídas Imunes/Isentas",new BigDecimal("02")),
	D03("03 - Débitos de notas fiscais não processadas na apuração",new BigDecimal("03")),
	D04("04 - Multa e juros",new BigDecimal("04")),
	D05("05 - Transferência de crédito na sucessão",new BigDecimal("05")),
	D06("06 - Pagamento antecipado",new BigDecimal("06")),
	D07("07 - Perda em estoque",new BigDecimal("07")),
	D08("08 - Desenquadramento do SN",new BigDecimal("08"));

	private final String description;
	private final BigDecimal cod;

	private TipoNFDebito(String description,BigDecimal cod){
		this.description = description;
		this.cod=cod;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public BigDecimal getCod() {
		return this.cod;
	}
}
