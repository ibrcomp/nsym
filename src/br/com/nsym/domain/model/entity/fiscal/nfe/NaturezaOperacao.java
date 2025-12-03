package br.com.nsym.domain.model.entity.fiscal.nfe;

import javax.persistence.Entity;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class NaturezaOperacao extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private String descricao;
	

}
