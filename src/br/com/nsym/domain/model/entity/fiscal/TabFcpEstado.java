package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="fcpNcmPorEstado",uniqueConstraints = {@UniqueConstraint(columnNames={"uf","NCM_ID","id_empresa"})})

public class TabFcpEstado extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Column(precision = 19 , scale = 2)
	private BigDecimal pFcp = new BigDecimal("0");

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
