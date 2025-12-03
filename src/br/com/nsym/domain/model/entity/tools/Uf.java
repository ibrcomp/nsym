package br.com.nsym.domain.model.entity.tools;

import java.math.BigDecimal;

/**
 * 
 * @author Ibrahim Yousef Quatani
 * @version 1.0.0
 * @since 1.0.0, 19/12/2016
 *
 */
public enum Uf {

	AC("Acre",new BigDecimal("0"),new BigDecimal("12")),
	AL("Alagoas",new BigDecimal("1"),new BigDecimal("27")),
	AP("Amapá",new BigDecimal("3"),new BigDecimal("16")),
	AM("Amazonas",new BigDecimal("2"),new BigDecimal("13")),
	BA("Bahia",new BigDecimal("4"),new BigDecimal("29")),
	CE("Ceará",new BigDecimal("5"),new BigDecimal("23")),
	DF("Distrito Federal",new BigDecimal("6"),new BigDecimal("53")),
	ES("Espirito Santo",new BigDecimal("7"),new BigDecimal("32")),
	GO("Goiás",new BigDecimal("8"),new BigDecimal("52")),
	MA("Maranhão",new BigDecimal("9"),new BigDecimal("21")),
	MT("Mato Grosso",new BigDecimal("10"),new BigDecimal("51")),
	MS("Mato Grosso do Sul",new BigDecimal("11"),new BigDecimal("50")),
	MG("Minas Gerais",new BigDecimal("12"),new BigDecimal("31")),
	PA("Pará",new BigDecimal("13"),new BigDecimal("15")),
	PB("Paraíba",new BigDecimal("14"),new BigDecimal("25")),
	PR("Paraná",new BigDecimal("15"),new BigDecimal("41")),
	PE("Pernambuco",new BigDecimal("16"),new BigDecimal("26")),
	PI("Piauí",new BigDecimal("17"),new BigDecimal("22")),
	RJ("Rio de Janeiro",new BigDecimal("20"),new BigDecimal("33")),
	RN("Rio Grande do Norte",new BigDecimal("18"),new BigDecimal("24")),
	RS("Rio Grande do Sul",new BigDecimal("19"),new BigDecimal("43")),
	RO("Rondônia",new BigDecimal("21"),new BigDecimal("11")),
	RR("Roraima",new BigDecimal("22"),new BigDecimal("14")),
	SC("Santa Catarina",new BigDecimal("23"),new BigDecimal("42")),
	SP("São Paulo",new BigDecimal("24"),new BigDecimal("35")),
	SE("Sergipe",new BigDecimal("25"),new BigDecimal("28")),
	TO("Tocantins",new BigDecimal("26"),new BigDecimal("17")),
	EX("Exterior",new BigDecimal("27"),new BigDecimal("0")),
	AN("Ambiente Nacional",new BigDecimal("91"),new BigDecimal("91"));
	
	
	private final String description;
	
	private final BigDecimal cod;
	
	private final BigDecimal ibge;
	
	
	private Uf(String description, BigDecimal cod, BigDecimal ibge){
		this.description = description;
		this.cod = cod;
		this.ibge = ibge;
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
	public int getCod(){
		return this.cod.intValue();
	}
		
	public BigDecimal getIbgeUf(){
		return this.ibge;
	}
	
}
