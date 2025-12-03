package br.com.nsym.domain.model.entity.fiscal;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"Ncm_ID","id_empresa","id_filial"})})
public class NcmEstoque extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal estoque = new BigDecimal("0");
	
	@Getter
	@Setter
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="Ncm_ID")
	private Ncm ncm;
}
