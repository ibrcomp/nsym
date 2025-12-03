package br.com.nsym.domain.model.entity.venda;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.tools.AbstractEmitente;
import lombok.Getter;
import lombok.Setter;

@Entity
public class DestinatarioTransferencia extends AbstractEmitente {

	
	/**
	 *
	 */
	private static final long serialVersionUID = -4996293437643397206L;
	@Getter
	@Setter
	@OneToOne(mappedBy="destinoTransferencia",cascade=CascadeType.ALL)
	private Pedido pedido;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(pedido);
		return result;
	}

	
}
