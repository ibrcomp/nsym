package br.com.nsym.domain.model.entity.estoque;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.fiscal.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="ItemIventario",uniqueConstraints = {@UniqueConstraint(columnNames={"id","id_empresa","id_filial"})})
public class ItemEstoqueEntrada extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6309460294331076132L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@ManyToOne
	private EntradaEstoque entrada;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal custoRecebimento= new BigDecimal("0",mc);
	
}
