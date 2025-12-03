package br.com.nsym.domain.model.entity.venda;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.tools.AbstractEmitente;
import lombok.Getter;
import lombok.Setter;

@Entity
public class EmitenteVenda extends AbstractEmitente {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1236694970103823774L;

	
	@Getter
	@Setter
	@OneToOne(mappedBy="emitente",cascade = CascadeType.REMOVE)
	private Pedido pedido;


}
