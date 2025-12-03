package br.com.nsym.domain.model.entity.cadastro.produto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(uniqueConstraints= {@UniqueConstraint(columnNames = {"produto_ID", "id_empresa","id_filial"})})
public class ProdutoEstoque extends PersistentEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="produto_ID")
	private Produto produto;
	
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
	private LocalDate dataUltimoRecebimento = LocalDate.now();
	
	

}
