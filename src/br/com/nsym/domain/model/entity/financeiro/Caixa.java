package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.tools.MovimentoEnum;
import br.com.nsym.domain.model.entity.financeiro.tools.StatusCaixa;
import br.com.nsym.domain.model.entity.financeiro.tools.StatusConferencia;
import br.com.nsym.domain.model.entity.venda.Pedido;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Caixa",uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario","dataAbertura","numeroTurno","id_empresa","id_filial"})})
public class Caixa extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -6239807987523991986L;

	@Getter
	@Setter
	private String usuario;
	
	@Getter
	@Setter
	private int numeroTurno;
	
	@Getter
	@Setter
	private LocalDate dataAbertura;
	
	@Getter
	@Setter
	private LocalTime horaAbertura;
	
	@Getter
	@Setter
	private LocalTime horaReabertura;
	
	@Getter
	@Setter
	private LocalDate dataFechamento;
	
	@Getter
	@Setter
	private LocalTime horaFechamento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private StatusCaixa statusCaixa;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private StatusConferencia conferencia;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private MovimentoEnum livroCaixa;
	
	@Getter
	@Setter
	private boolean isAberto = false;
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListaAgPedidoPorCaixa",joinColumns= @JoinColumn(name="Caixa_ID"),
	inverseJoinColumns= @JoinColumn(name="AgPedido_ID"))
	private List<AgPedido> listaAgPedido = new ArrayList<AgPedido>();
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListaAgPedidoPorCaixa",joinColumns= @JoinColumn(name="Caixa_ID"),
	inverseJoinColumns= @JoinColumn(name="Pedido_ID"))
	private List<Pedido> listaPedido = new ArrayList<Pedido>();
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListaRecebimentoCaixa",joinColumns= @JoinColumn(name="Caixa_ID"),
	inverseJoinColumns= @JoinColumn(name="Recebimento_ID"))
	private List<RecebimentoParcial> listaRecebimentoCaixa = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinTable(name="TabelaListaFechamentoCaixa",joinColumns= @JoinColumn(name="Caixa_ID"),
	inverseJoinColumns= @JoinColumn(name="Fechamento_ID"))
	private List<RecebimentoParcial> listaFechamentoCaixa = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name="TabelaSaldoCaixa",joinColumns= @JoinColumn(name="Caixa_ID"),
	inverseJoinColumns= @JoinColumn(name="Saldo_ID"))
	private List<SaldoCaixa> saldoCaixa = new ArrayList<SaldoCaixa>();
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal fundoCaixa = new BigDecimal("0");
	
	
	
	
}
