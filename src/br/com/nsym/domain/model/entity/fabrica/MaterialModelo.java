package br.com.nsym.domain.model.entity.fabrica;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="MaterialModelo",uniqueConstraints = {@UniqueConstraint(columnNames={"Modelo_ID","Produto_ID","Producao_ID"})})
public class MaterialModelo extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 5735737703201930362L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Modelo_ID",referencedColumnName = "id")
	private Modelo modelo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Produto_ID",referencedColumnName = "id")
	private Produto produto;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Producao_ID",referencedColumnName = "id")
	private Producao producao;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quant = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private boolean possuiFichaTecnicas = false;

}
