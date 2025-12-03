package br.com.nsym.domain.model.entity.financeiro.tools;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class TotalizadorFinanceiro {


	@Getter
	@Setter
	private BigDecimal totalCredito = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal totalCreditoRec = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal totalDebito = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal totalDebitoPag = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal totalCreditoLiq = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal totalDebitoLiq = new BigDecimal("0");
	
}
