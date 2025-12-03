package br.com.nsym.domain.model.entity.cadastro;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.produto.BarrasCusto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="barras",uniqueConstraints= {@UniqueConstraint(columnNames = {"barras","id_empresa","id_filial"}),
											@UniqueConstraint(columnNames = {"barras","Tamanho_Id","Cor_Id","id_empresa","id_filial"})})
public class BarrasEstoque extends PersistentEntity{


	/**
	 *
	 */
	private static final long serialVersionUID = 3433977571198723135L;
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);

	@Getter
	@Setter
	private String barras;
	
	@Transient
	@Getter
	@Setter
	private String numReg;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Produto_Id")
	private Produto produtoBase;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Tamanho_Id")
	private Tamanho tamanho;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Cor_Id")
	private Cor cor;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal totalEstoque = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal totalComprado = new BigDecimal("0");
	
	@Getter
	@Setter
	@OneToOne(mappedBy="barra",fetch=FetchType.LAZY)
	private BarrasCusto precos;
	
	@Getter
	@Setter
	private LocalDate ultimaCompra ;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal totalUltimaCompra = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal estoqueAnterior= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantidadeAcrescentada= new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private LocalDate dataRecebimentoAnterior ;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantidadeInventarioAnterior= new BigDecimal("0",mc);	
	
	@Getter
	@Setter
	private LocalDate dataInventario ;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(barras, cor, numReg, precos, produtoBase, tamanho, totalComprado,
				totalEstoque, totalUltimaCompra, ultimaCompra);
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
		BarrasEstoque other = (BarrasEstoque) obj;
		return Objects.equals(barras, other.barras) && Objects.equals(cor, other.cor)
				&& Objects.equals(numReg, other.numReg) && Objects.equals(precos, other.precos)
				&& Objects.equals(produtoBase, other.produtoBase) && Objects.equals(tamanho, other.tamanho)
				&& Objects.equals(totalComprado, other.totalComprado)
				&& Objects.equals(totalEstoque, other.totalEstoque)
				&& Objects.equals(totalUltimaCompra, other.totalUltimaCompra)
				&& Objects.equals(ultimaCompra, other.ultimaCompra);
	}

	
}
