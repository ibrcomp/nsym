package br.com.nsym.domain.model.entity.venda;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.ControlePedido;
import br.com.nsym.domain.model.entity.tools.FiscalStatus;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.tools.Uf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="pedido",uniqueConstraints = {@UniqueConstraint(columnNames={"controle","id_empresa","id_filial"})})
@SqlResultSetMapping(
		name= "MaisVendidosMapping",
		classes = {
			@ConstructorResult(targetClass = br.com.nsym.domain.model.entity.venda.RelatorioVendasDTO.class,
				columns = {@ColumnResult(name = "ref",type = String.class),
						@ColumnResult(name = "descricao",type = String.class),
						@ColumnResult(name = "quant",type = BigDecimal.class),
						@ColumnResult(name = "valor_Un",type = BigDecimal.class),
						@ColumnResult(name = "vl_Med_Un",type = BigDecimal.class),
						@ColumnResult(name = "total",type = BigDecimal.class),
						@ColumnResult(name = "tamanho",type = String.class),
						@ColumnResult(name = "cor",type = String.class),
						@ColumnResult(name = "totalPeriodo",type = BigDecimal.class),
						@ColumnResult(name = "totalPecas",type = BigDecimal.class),
						@ColumnResult(name = "matriz",type = String.class),
						@ColumnResult(name = "filial",type = String.class),
						@ColumnResult(name = "barras",type = BigInteger.class),
						})	
			}
		)
@SqlResultSetMapping(
		name= "ComissaoColaboradorMapping",
		classes = {
			@ConstructorResult(targetClass = br.com.nsym.domain.model.entity.venda.RelComissaoColaboradoresDTO.class,
				columns = {@ColumnResult(name = "colaborador",type = String.class),
						@ColumnResult(name = "descricao",type = String.class),
						@ColumnResult(name = "total",type = BigDecimal.class),
						@ColumnResult(name = "matriz",type = String.class),
						@ColumnResult(name = "filial",type = String.class),
						@ColumnResult(name = "id",type = BigInteger.class),
						})	
			}
		)
@SqlResultSetMapping(
		name= "RelVendasFabricanteDepartamentoMapping",
		classes = {
			@ConstructorResult(targetClass = br.com.nsym.domain.model.entity.cadastro.dto.RelVendasFabricanteDTO.class,
				columns = {@ColumnResult(name = "fabricante",type = String.class),
						@ColumnResult(name = "departamento",type = String.class),
						@ColumnResult(name = "quantidade",type = BigDecimal.class),
						@ColumnResult(name = "totalPecas",type = BigDecimal.class),
						@ColumnResult(name = "part",type = BigDecimal.class),
						@ColumnResult(name = "valorMedio",type = BigDecimal.class),
						@ColumnResult(name = "totalValor",type = BigDecimal.class),
						@ColumnResult(name = "totalVendido",type = BigDecimal.class),
						@ColumnResult(name = "partVenda",type = BigDecimal.class),
						@ColumnResult(name = "matriz",type = String.class),
						@ColumnResult(name = "filial",type = String.class),
						@ColumnResult(name = "idFab",type = BigInteger.class),
						@ColumnResult(name = "idDep",type = BigInteger.class),
						})	
			}
		)
public class Pedido extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 289360339745960371L;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="controle")
	private ControlePedido controle;
	
	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL,fetch =FetchType.LAZY)
	private EmitenteVenda emitente;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="Transacao")
	private Transacao transacao ;
	
	@Getter
	@Setter
	private LocalDate dataEmissao = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataRecebimento ;
	
	@Getter
	@Setter
	private LocalTime horaRecebimento ;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorDescontoTotal;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalPedido;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Colaborador_id")
	private Colaborador atendente;
	
	@Getter
	@Setter
	@OneToOne(cascade=CascadeType.ALL,fetch = FetchType.LAZY)
	private DestinatarioPedido destino;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPesquisa tipoPesquisa;
	
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinTable(name="TabelaItensPedido",joinColumns= @JoinColumn(name="Pedido_Id"),
	inverseJoinColumns= @JoinColumn(name="ItemPedido_Id"))
	private List<ItemPedido> listaItensPedido = new ArrayList<ItemPedido>();
	
	@Getter
	@Setter
	private boolean entrega;
	
	@Getter
	@Setter
	private String localEntrega;
	
	@Getter
	@Setter
	private String obs;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Guia")
	private Colaborador guia;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Pagamento")
	private FormaDePagamento pagamento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PedidoStatus pedidoStatus;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PedidoTipo pedidoTipo;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private FiscalStatus fiscalStatus;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal BaseIcms = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal ValorIcms = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal baseIcmsSubstituicao = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorIcmsSubstituicao = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalProdutos = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorFrete = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorSeguro = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal desconto = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalPis = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalCofins = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal outrasDespesas = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalIpi = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalNota = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalTributos = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCP = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCPST = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCPSTRet = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vIPIDevol = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal vCFe = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorIcmsDesonerado = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vFCPUFDest = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vICMSUFDest = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal vICMSUFRemet = new BigDecimal("0");
	 
	@Getter
	@Setter
	private String vCFeLei12741;
	
	@Getter
	@Setter
	private String vAcresSubtot;
	
	@Getter
	@Setter
	private String vDescSubtot;
	
	@Getter
	@Setter
	private String vTroco;
	
	@Getter
	@Setter
	private String cMP;
	
	@Getter
	@Setter
	private String vMP;
	
	@Getter
	@Setter
	private String indFinal;
	
//	@Getter
//	@Setter
//	@ManyToOne
//	@JoinColumn(name="Pagamento_ID")
//	private FormaDePagamento formaPagamento;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="cfe",cascade = CascadeType.REMOVE)
	private List<ParcelasNfe> listaParcelas = new ArrayList<>();
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalPisST = new BigDecimal("0");
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valorTotalCofinsSt = new BigDecimal("0");
	
	@Getter
	@Setter
	private String caminho;
	
	@Getter
	@Setter
	private String nome;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Uf ufDestino;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="AgPedido_ID")
	private AgPedido agrupado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Caixa_ID")
	private Caixa caixa;
	
	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private DestinatarioTransferencia destinoTransferencia;
	
	@Getter
	@Setter
	private boolean destinoMatriz = false;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="pedido")
	private List<Nfe> listaDeNFe = new ArrayList<Nfe>();
	
	@Getter
	@Setter
	private boolean transferenciaConcluida = false;
	
	@Getter
	@Setter
	private LocalDate previsaoEntrega  = LocalDate.now();
	
	@Getter
	@Setter
	private boolean ativaEncomenda = false;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(BaseIcms, ValorIcms, agrupado, caixa, controle, dataEmissao,
				dataRecebimento, desconto, horaRecebimento, listaItensPedido, listaParcelas, nome, pagamento,
				pedidoStatus, pedidoTipo, tipoPesquisa, transacao, vTroco, valorDescontoTotal, valorFrete,
				valorTotalNota, valorTotalPedido, valorTotalProdutos);
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
		Pedido other = (Pedido) obj;
		return Objects.equals(BaseIcms, other.BaseIcms) && Objects.equals(ValorIcms, other.ValorIcms)
				&& Objects.equals(agrupado, other.agrupado) && Objects.equals(caixa, other.caixa)
				&& Objects.equals(controle, other.controle) && Objects.equals(dataEmissao, other.dataEmissao)
				&& Objects.equals(dataRecebimento, other.dataRecebimento) && Objects.equals(desconto, other.desconto)
				&& Objects.equals(horaRecebimento, other.horaRecebimento)
				&& Objects.equals(listaItensPedido, other.listaItensPedido)
				&& Objects.equals(listaParcelas, other.listaParcelas) && Objects.equals(nome, other.nome)
				&& Objects.equals(pagamento, other.pagamento) && pedidoStatus == other.pedidoStatus
				&& pedidoTipo == other.pedidoTipo && tipoPesquisa == other.tipoPesquisa
				&& Objects.equals(transacao, other.transacao) && Objects.equals(vTroco, other.vTroco)
				&& Objects.equals(valorDescontoTotal, other.valorDescontoTotal)
				&& Objects.equals(valorFrete, other.valorFrete) && Objects.equals(valorTotalNota, other.valorTotalNota)
				&& Objects.equals(valorTotalPedido, other.valorTotalPedido)
				&& Objects.equals(valorTotalProdutos, other.valorTotalProdutos);
	}

	



}
