package br.com.nsym.domain.model.entity.fabrica.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.fabrica.FichaTecnica;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ItemProducao extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 6048445366768313697L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="FichaTecnica_ID")
	private FichaTecnica ficha;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Barras_ID")
	private BarrasEstoque barras;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal quantidade = new BigDecimal("0",mc);
	

}
