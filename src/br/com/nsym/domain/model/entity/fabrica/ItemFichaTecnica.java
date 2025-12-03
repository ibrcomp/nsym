package br.com.nsym.domain.model.entity.fabrica;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.fiscal.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ItemFichaTecnica extends Item  {

	/**
	 *
	 */
	private static final long serialVersionUID = 84636243249352403L;
	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Ficha_ID")
	private FichaTecnica ficha;
	

}
