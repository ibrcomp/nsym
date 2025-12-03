package br.com.nsym.domain.model.entity.estoque;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.tools.TipoAtualizaEstoque;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="EntradaEstoque",uniqueConstraints = {@UniqueConstraint(columnNames={"id","id_empresa","id_filial"})})
@SqlResultSetMapping(
		name= "RelEstoqueGeral",
		classes = {
			@ConstructorResult(targetClass = br.com.nsym.domain.model.entity.estoque.dto.RelEstoqueGeralDTO.class,
				columns = {@ColumnResult(name = "ref",type = String.class),
						@ColumnResult(name = "descicao",type = String.class),
						@ColumnResult(name = "estoqueTotal",type = BigDecimal.class),
						@ColumnResult(name = "totalRecebido",type = BigDecimal.class),
						})	
			}
		)

public class EntradaEstoque extends PersistentEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7776577594427864306L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinTable(name="TabelaListaBarrasEstoque",joinColumns= @JoinColumn(name="ItemInventario_id"),
	inverseJoinColumns= @JoinColumn(name="EntradaEstoque_id"))
	private List<ItemEstoqueEntrada> listaDeItensEntrada = new ArrayList<ItemEstoqueEntrada>();
	
	@Getter
	@Setter
	@ManyToOne
	private Fornecedor fornecedor;
	
	@Getter
	@Setter
	private LocalDate dataCriacao;
	
	@Getter
	@Setter
	private LocalTime horaCriacao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoAtualizaEstoque tipoAtualiza;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private boolean atualizado;
	
	@Getter
	@Setter
	private boolean fabrica=false;
	
	@Getter
	@Setter
	private boolean recebimentoProduto;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal valorPedidoEntrada= new BigDecimal("0",mc);
	
}
