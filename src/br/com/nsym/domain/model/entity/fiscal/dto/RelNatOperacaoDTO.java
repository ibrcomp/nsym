package br.com.nsym.domain.model.entity.fiscal.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

public class RelNatOperacaoDTO {
	
	
	public RelNatOperacaoDTO(BigInteger numeroNFE, String razaoSocial, Date dataSaida, String natOpera,
			BigDecimal totalNota,String status, String matriz, String filial) {
		super();
		this.numeroNFE = numeroNFE;
		this.razaoSocial = razaoSocial;
		this.dataSaida = dataSaida;
		this.natOpera = natOpera;
		this.totalNota = totalNota;
		this.status = status;
		this.matriz = matriz;
		this.filial = filial;
	}

	@Getter
	@Setter
	private BigInteger numeroNFE; 
	
	@Getter
	@Setter
	private String razaoSocial;
	
	@Getter
	@Setter
	private Date dataSaida;
	
	@Getter
	@Setter
	private String natOpera; 
	
	@Getter
	@Setter
	private BigDecimal totalNota = new BigDecimal("0");
	
	@Getter
	@Setter
	private String status;
	
	@Getter
	@Setter
	private String matriz;
	
	@Getter
	@Setter
	private String filial;


}
