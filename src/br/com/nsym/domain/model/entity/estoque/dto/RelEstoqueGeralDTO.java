package br.com.nsym.domain.model.entity.estoque.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class RelEstoqueGeralDTO {
	
	public RelEstoqueGeralDTO(String ref, String descicao, BigDecimal estoqueTotal, BigDecimal totalRecebido) {
		super();
		this.ref = ref;
		this.descicao = descicao;
		this.estoqueTotal = estoqueTotal;
		this.totalRecebido = totalRecebido;
	}

	@Getter
	@Setter
	private String ref; 
	
	@Getter
	@Setter
	private String descicao; 
	
	@Getter
	@Setter
	private BigDecimal estoqueTotal = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalRecebido = new BigDecimal("0");
	
	

}
