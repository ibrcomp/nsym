package br.com.nsym.domain.model.entity.venda;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.tools.TabelaPreco;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(uniqueConstraints= {@UniqueConstraint(columnNames = {"codigo", "id_empresa"})})
public class Transacao extends PersistentEntity{


	/**
	 *
	 */
	private static final long serialVersionUID = -5103287461942534120L;

	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TabelaPreco tabelaPadrao = TabelaPreco.TA;
	
	@Getter
	@Setter
	private boolean tabelaPorItem = false;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="pagamento_ID")
	private FormaDePagamento pagamentoPadrao;
	
	@Getter
	@Setter
	private boolean colaboradorComissao = false;
	
	@Getter
	@Setter
	private boolean gerenciaComissao = false;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tributo_ID")
	private Tributos tributoPadrao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoTransacao tipoTransacao = TipoTransacao.ven; 
	
	@Getter
	@Setter
	@OneToMany(mappedBy="transacao",fetch=FetchType.LAZY)
	private List<Pedido> listaPedidos = new ArrayList<Pedido>();



}
