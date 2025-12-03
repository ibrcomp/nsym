package br.com.nsym.domain.model.entity.fiscal.tools;

import java.math.BigDecimal;

public enum CSTPIS {
	P01("01 - Operação Tributável - Base de Cálculo = Valor da Operação Alíquota Normal(Cumulativo/Não Cumulativo)",new BigDecimal("01")),        
	P02("02 - Operação Tributável - Base de Cálculo = Valor da Operação(Alíquota Diferenciada)",new BigDecimal("02")),                             
	P03("03 - Operação Tributável - Base de Cálculo = Quantidade Vendida X Alíquota por Unidade de Produto",new BigDecimal("03")),                      
	P04("04 - Operação Tributável - Tributação Monofásica(Alíquota Zero)",new BigDecimal("04")),                              
	P05("05 - Operação Tributável (ST)",new BigDecimal("05")), 
	P06("06 - Operação Tributável - Alíquota Zero)",new BigDecimal("06")),
	P07("07 - Operação Isenta de Contribuição",new BigDecimal("07")),
	P08("08 - Operação sem Incidência da Contribuição",new BigDecimal("08")),
	P09("09 - Operação Tributável (ST)",new BigDecimal("09")),
	P49("49 - Outras operações de Saída",new BigDecimal("49")),                            
	P50("50 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Tributada no Mercado Interno",new BigDecimal("50")),                           
	P51("51 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Não Tributada no Mercado Internoo",new BigDecimal("51")),          
	P52("52 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receita de Exportação",new BigDecimal("52")),                               
	P53("53 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receitas Tributadas e Não-Tributadas no Mercado Interno",new BigDecimal("53")),                        
	P54("54 - Operação com Direito a Crédito - Vinculada a Receitas Tributadas no Mercado Interno e de Exportação",new BigDecimal("54")),                                
	P55("55 - Operação com Direito a Crédito - Vinculada a Receitas Não-Tributada no Mercado Interno e de Exportação",new BigDecimal("55")), 
	P56("56 - Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributada no Mercado Interno e de Exportação",new BigDecimal("56")), 
	P60("60 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Tributada no Mercado Interno",new BigDecimal("60")),
	P61("61 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Não-Tributada no Mercado Interno",new BigDecimal("61")),
	P62("62 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita de Exportação",new BigDecimal("62")),
	P63("63 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno",new BigDecimal("63")),
	P64("64 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas no Mercado Interno e de Exportação",new BigDecimal("64")),
	P65("65 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação",new BigDecimal("65")),
	P66("66 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadae Não-Tributadas no Mercado Interno e de Exportação",new BigDecimal("66")),
	P67("67 - Crédito Presumido - Outras Operações",new BigDecimal("67")),
	P70("70 - Operação de Aquisição sem Direito a Crédito",new BigDecimal("70")),
	P71("71 - Operação de Aquisição com Isenção",new BigDecimal("71")),
	P72("72 - Operação de Aquisição com Suspensão",new BigDecimal("72")),
	P73("73 - Operação de Aquisição a Alíquota Zero",new BigDecimal("73")),
	P74("74 - Operação de Aquisição sem Incidência da Contribuição",new BigDecimal("74")),
	P75("75 - Operação de Aquisição por Substituição Tributária",new BigDecimal("75")),
	P98("98 - Outras Operações de Entrada",new BigDecimal("98")),        
	P99("99 - Outras Operações",new BigDecimal("99"));   

	private final String description;
	private final BigDecimal pis;

	private CSTPIS(String description,BigDecimal pis){
		this.description = description;
		this.pis=pis;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public BigDecimal getPis() {
		return this.pis;
	}
}
