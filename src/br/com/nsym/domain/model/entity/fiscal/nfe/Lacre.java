package br.com.nsym.domain.model.entity.fiscal.nfe;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Lacre extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String lacre;
	
	@Getter
	@Setter
	@ManyToOne(cascade=CascadeType.REMOVE)
	@JoinColumn(name="nfe_id")
	private Nfe nfe;
	
	@Getter
	@Setter
	@ManyToOne(cascade=CascadeType.REMOVE)
	@JoinColumn(name="nfeRecebida_id")
	private NfeRecebida nfeRecebida;
}
