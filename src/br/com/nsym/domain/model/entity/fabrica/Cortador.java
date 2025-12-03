package br.com.nsym.domain.model.entity.fabrica;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Cortador extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 4670780228271766329L;
	
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	private String Obs;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorCorte = new BigDecimal("0");
	
	@Getter
	@Setter
	private boolean porPeca = false;
	
	

}
