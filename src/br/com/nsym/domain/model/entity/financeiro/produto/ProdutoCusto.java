package br.com.nsym.domain.model.entity.financeiro.produto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.PersistentEntitySemEmpFilial;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(uniqueConstraints= {@UniqueConstraint(columnNames = {"Produto_ID", "id_empresa","id_filial"})})
public class ProdutoCusto extends PersistentEntitySemEmpFilial{
	
	/**
	 *
	 */
	private static final long serialVersionUID = -5507081411703687159L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal custo = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal custoLiquido = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal custoAnterior = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal custoMedio = new BigDecimal("0",mc); 
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal preco1= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal preco2 = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal preco3 = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal preco4 = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal preco5 = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Produto_ID")
	private Produto produto;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(custo, custoLiquido, preco1, preco2, preco3, preco4, preco5, produto);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProdutoCusto other = (ProdutoCusto) obj;
		return Objects.equals(custo, other.custo) && Objects.equals(custoLiquido, other.custoLiquido)
				&& Objects.equals(preco1, other.preco1) && Objects.equals(preco2, other.preco2)
				&& Objects.equals(preco3, other.preco3) && Objects.equals(preco4, other.preco4)
				&& Objects.equals(preco5, other.preco5) && Objects.equals(produto, other.produto);
	}

	@Override
	public String toString() {
		return "ProdutoCusto [custo=" + custo + ", custoLiquido=" + custoLiquido + ", custoAnterior=" + custoAnterior
				+ ", custoMedio=" + custoMedio + ", preco1=" + preco1 + ", preco2=" + preco2 + ", preco3=" + preco3
				+ ", preco4=" + preco4 + ", preco5=" + preco5 + "]";
	}


	
}
