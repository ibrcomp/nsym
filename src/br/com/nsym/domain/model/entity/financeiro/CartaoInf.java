package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import lombok.Getter;
import lombok.Setter;

@Entity
public class CartaoInf extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 2599221539442445998L;
	
	@Getter
	@Setter
	private String numAut;
	
	@Getter
	@Setter
	private String numControl;

	@Getter
	@Setter
	@OneToOne(mappedBy="cartao",cascade=CascadeType.ALL)
	private ParcelasNfe titulo;	
	
	@Getter
	@Setter
	private BigDecimal taxaUsada = new BigDecimal("0");
	
	
}
