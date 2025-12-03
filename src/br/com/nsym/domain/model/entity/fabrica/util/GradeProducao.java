package br.com.nsym.domain.model.entity.fabrica.util;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.entity.fabrica.Producao;
import br.com.nsym.domain.model.entity.fabrica.Risco;
import lombok.Getter;
import lombok.Setter;

@Entity
public class GradeProducao extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -3046266759202065828L;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "Tamanho_ID")
	private Tamanho tamanho;
	
	@Getter
	@Setter
	private Long quantidade;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "OP_ID")
	private Producao producao;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "Risco_ID")
	private Risco risco;
	
	

}
