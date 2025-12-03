package br.com.nsym.domain.model.entity.fiscal.tools;

import java.math.BigDecimal;

public enum TipoNFCredito {

	
	C01("01 - Multa e juros",new BigDecimal("01")),
	C02("02 - Apropriação de crédito presumido de IBS sobre o saldo devedor na ZFM (art. 450, § 1º, LC 214/25)",new BigDecimal("02")),
	C03("03 - Retorno por recusa na entrega ou por não localização do destinatário na tentativa de entrega",new BigDecimal("03")),
	C04("04 - Redução de valores",new BigDecimal("04")),
	C05("05 - Transferência de crédito na sucessão",new BigDecimal("05"));   

	private final String description;
	private final BigDecimal cod;

	private TipoNFCredito(String description,BigDecimal cod){
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
