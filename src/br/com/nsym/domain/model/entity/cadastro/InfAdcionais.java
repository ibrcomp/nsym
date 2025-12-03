package br.com.nsym.domain.model.entity.cadastro;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class InfAdcionais extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String informacao;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Produto_id")
	private Produto produto;

}
