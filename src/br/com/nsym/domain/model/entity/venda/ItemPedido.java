package br.com.nsym.domain.model.entity.venda;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.fiscal.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ItemPedido extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5575005153589993879L;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Pedido_ID")
	private Pedido pedido;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Colaborador")
	private Colaborador vendedor;
	
}
