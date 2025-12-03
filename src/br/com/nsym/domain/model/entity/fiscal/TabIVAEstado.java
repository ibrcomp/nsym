package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ivaNcmPorEstado",uniqueConstraints = {@UniqueConstraint(columnNames={"uf","NCM_ID","id_empresa"})})
										
public class TabIVAEstado extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Transient
	private MathContext mc = new MathContext(19, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 2)
	private BigDecimal pIVA =  new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf uf;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="NCM_ID")
	private Ncm ncm;
}
