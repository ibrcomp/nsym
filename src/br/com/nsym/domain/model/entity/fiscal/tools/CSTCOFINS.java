package br.com.nsym.domain.model.entity.fiscal.tools;

public enum CSTCOFINS {
	CO01("01 - Operação Tributável - Base de Cálculo = Valor da Operação Alíquota Normal(Cumulativo/Não Cumulativo)",1),        
	CO02("02 - Operação Tributável - Base de Cálculo = Valor da Operação(Alíquota Diferenciada)",2),                             
	CO03("03 - Operação Tributável - Base de Cálculo = Quantidade Vendida X Alíquota por Unidade de COroduto",3),                      
	CO04("04 - Operação Tributável - Tributação Monofásica(Alíquota Zero)",4),                              
	CO05("05 - Operação Tributável (ST)",5), 
	CO06("06 - Operação Tributável - Alíquota Zero)",6),
	CO07("07 - Operação Isenta de Contribuição",7),
	CO08("08 - Operação sem IncidÃªncia da Contribuição",8),
	CO09("09 - Operação Tributável (ST)",9),
	CO49("49 - Outras operações de Saída",49),                            
	CO50("50 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Tributada no Mercado Interno",50),                           
	CO51("51 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Não Tributada no Mercado Internoo",51),          
	CO52("52 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receita de Exportação",52),                               
	CO53("53 - Operação com Direito a Crédito - Vinculada Exclusivamente a Receitas Tributadas e Não-Tributadas no Mercado Interno",53),                        
	CO54("54 - Operação com Direito a Crédito - Vinculada a Receitas Tributadas no Mercado Interno e de Exportação",54),                                
	CO55("55 - Operação com Direito a Crédito - Vinculada a Receitas Não-Tributada no Mercado Interno e de Exportação",55), 
	CO56("56 - Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributada no Mercado Interno e de Exportação",56), 
	CO60("60 - Crédito COresumido - Operação de Aquisição Vinculada Exclusivamente a Receita Tributada no Mercado Interno",60),
	CO61("61 - Crédito COresumido - Operação de Aquisição Vinculada Exclusivamente a Receita Não-Tributada no Mercado Interno",61),
	CO62("62 - Crédito COresumido - Operação de Aquisição Vinculada Exclusivamente a Receita de Exportação",62),
	CO63("63 - Crédito COresumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno",63),
	CO64("64 - Crédito COresumido - Operação de Aquisição Vinculada a Receitas Tributadas no Mercado Interno e de Exportação",64),
	CO65("65 - Crédito COresumido - Operação de Aquisição Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação",65),
	CO66("66 - Crédito COresumido - Operação de Aquisição Vinculada a Receitas Tributadae Não-Tributadas no Mercado Interno e de Exportação",66),
	CO67("67 - Crédito COresumido - Outras Operações",67),
	CO70("70 - Operação de Aquisição sem Direito a Crédito",70),
	CO71("71 - Operação de Aquisição com Isenção",71),
	CO72("72 - Operação de Aquisição com Suspensão",72),
	CO73("73 - Operação de Aquisição a Alíquota Zero",73),
	CO74("74 - Operação de Aquisição sem IncidÃªncia da Contribuição",74),
	CO75("75 - Operação de Aquisição por Substituição Tributária",75),
	CO98("98 - Outras Operações de Entrada",98),        
	CO99("99 - Outras Operações",99);   

	private final String description;
	private final int cofins;

	private CSTCOFINS(String description,int cofins){
		this.description = description;
		this.cofins=cofins;
	}

	@Override
	public String toString(){
		return this.description;
	}

	public int getCofins() {
		return this.cofins;
	}
}
