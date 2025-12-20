package br.com.nsym.application.controller.financial;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.CaixaException;
import br.com.ibrcomp.exception.DevolucaoException;
import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.EstoqueRuntimeException;
import br.com.ibrcomp.exception.FinanceiroException;
import br.com.ibrcomp.exception.RegraNegocioException;
import br.com.ibrcomp.exception.TotaisCFeException;
import br.com.ibrcomp.exception.TributosException;
import br.com.ibrcomp.interceptor.RollbackOn;
import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.application.controller.nfe.tools.CalculaTributos;
import br.com.nsym.domain.misc.CpfCnpjUtils;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.misc.ImpressoraACBr;
import br.com.nsym.domain.misc.LocalizaRegex;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.estoque.Estoque;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.MovimentoCaixa;
import br.com.nsym.domain.model.entity.financeiro.ParcelaStatus;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.SaldoCaixa;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamentoSimples;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoLancamento;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.financeiro.tools.CaixaUtil;
import br.com.nsym.domain.model.entity.financeiro.tools.MovimentoEnum;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.financeiro.tools.StatusCaixa;
import br.com.nsym.domain.model.entity.financeiro.tools.StatusConferencia;
import br.com.nsym.domain.model.entity.fiscal.Item;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.DestinatarioCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.EmitenteCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.entity.tools.CaixaFinalidade;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.tools.FiscalStatus;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.venda.DestinatarioTransferencia;
import br.com.nsym.domain.model.entity.venda.EmitenteVenda;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.CaixaRepository;
import br.com.nsym.domain.model.repository.financeiro.CreditoRepository;
import br.com.nsym.domain.model.repository.financeiro.CustoProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.FormaDePagementoRepository;
import br.com.nsym.domain.model.repository.financeiro.MovimentoCaixaRepository;
import br.com.nsym.domain.model.repository.financeiro.RecebimentoParcialRepository;
import br.com.nsym.domain.model.repository.financeiro.SaldoCaixaRepository;
import br.com.nsym.domain.model.repository.financeiro.tools.ParcelasNfeRepository;
import br.com.nsym.domain.model.repository.fiscal.NcmEstoqueRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.DestinatarioCFeRepository;
import br.com.nsym.domain.model.repository.fiscal.sat.CFeRepository;
import br.com.nsym.domain.model.repository.fiscal.sat.EmitenteCfeRepository;
import br.com.nsym.domain.model.repository.fiscal.sat.ItemCFeRepository;
import br.com.nsym.domain.model.repository.venda.AgPedidoRepository;
import br.com.nsym.domain.model.repository.venda.ItemPedidoRepository;
import br.com.nsym.domain.model.repository.venda.PedidoRepository;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class CaixaBean extends AbstractBeanEmpDS<AgPedido> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7648477324354337881L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_EVEN);
	
	@Getter
	@Setter
	private Configuration configUser;
	
	@Getter
	@Setter
	private EmpUser empresaUsuario;
	
	@Inject
	private ImpressoraACBr impressora;
	
	@Inject
	private AcbrComunica acbr;
	
	@Getter
	@Setter
	private List<Pedido> listaPedidosSelecionados= new ArrayList<Pedido>();
	
	@Getter
	@Setter
	private Pedido pedido;

	@Inject
	private ItemPedidoRepository itemDao;
	
	@Getter
	private AbstractLazyModel<Pedido> listPedidoModel;
	
	@Inject
	private PedidoRepository pedidoDao;
	
	@Getter
	@Setter
	private AgPedido agrupado;
	
	@Inject
	private AgPedidoRepository agrupadoDao;
	
	@Getter
	@Setter
	private LocalDate dataInicial = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataAbertura = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataPesquisa = LocalDate.now();
	
	@Getter
	@Setter
	private Long controle; 
	
	@Getter
	@Setter
	private BigDecimal totalGT = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalBruto = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal valorAbertura = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal valorFundo = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal resto = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal troco = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalRecebido = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal acrescimo = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal desconto = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal valorRecebido = new BigDecimal("0");
	
	@Getter
	@Setter
	private boolean tipoDesconto = true;
	
	@Getter
	@Setter
	private boolean tipoAcrescimo = false;
	
	@Getter
	@Setter
	private Caixa caixa;
	
	@Inject
	private CaixaUtil caixaUtil;
	
	@Getter
	@Setter
	private List<Caixa> listaDeCaixasAberto = new ArrayList<Caixa>();
	
	@Inject
	private CaixaRepository caixaDao;
	
	@Getter
	@Setter
	private SaldoCaixa saldo;
	
	@Getter
	@Setter
	private List<SaldoCaixa> listaSaldo = new ArrayList<SaldoCaixa>();
	
	@Inject
	private SaldoCaixaRepository saldoDao;
	
	@Getter
	@Setter
	private RecebimentoParcial recebimentoParcial;
	
	@Inject
	private RecebimentoParcialRepository recebimentoDao;
	
	@Getter
	@Setter
	private FormaDePagamento forma;
	
	@Getter
	@Setter
	private List<ParcelasNfe> listaParcelasPagamento = new ArrayList<ParcelasNfe>();
	
	@Inject
	private ParcelasNfeRepository parcelaDao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPagamento tipoPagamento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPagamentoSimples tipoSimples;
	
	@Getter
	@Setter
	private HashMap<FormaDePagamento, BigDecimal> hasForma = new HashMap<FormaDePagamento, BigDecimal>();
	
	@Getter
	@Setter
	private HashMap<String,BigDecimal> hasTipoPagSimples = new HashMap<String, BigDecimal>();
	
	@Getter
	@Setter
	private List<RecebimentoParcial> listaRecebimentos = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	private List<FormaDePagamento> listaPagamentos = new ArrayList<FormaDePagamento>();

	@Setter
	private List<Map.Entry<FormaDePagamento, BigDecimal>> listaFormaPagamentoInserido = new ArrayList<Map.Entry<FormaDePagamento,BigDecimal>>();
	
	@Getter
	@Setter
	private HashMap<FormaDePagamento, BigDecimal> hashRecebimentoParcialCaixa = new HashMap<FormaDePagamento, BigDecimal>();
	
	@Inject
	private FormaDePagementoRepository pagamentoDao;
	
	@Getter
	private AbstractLazyModel<AgPedido> agPedidoModel;
	
	@Getter
	private AbstractLazyModel<Caixa> caixaModel;
	
	@Getter
	@Setter
	private Caixa caixaConferencia = new Caixa();
	
	@Getter
	private List<Caixa> listaCaixas = new ArrayList<Caixa>();
	
	@Getter
	private List<AgPedido> listaAgPedido= new ArrayList<AgPedido>();
	
	@Getter
	private List<Pedido> listaTransferencia = new ArrayList<Pedido>();
	
	@Getter
	private List<Pedido> novaListaDevolvidos= new ArrayList<Pedido>();
	
	// variaveis para o Extrato Caixa
	
	@Getter
	@Setter
	private List<RecebimentoParcial> listaValoresAberturaCaixa = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	private List<RecebimentoParcial> listaValoresFechamentoCaixa = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	private List<RecebimentoParcial> listaRecebimentosCaixa = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	private AbstractDataModel<MovimentoCaixa> listaMovimentacaoSangria = new AbstractDataModel<MovimentoCaixa>();
	
	@Getter
	@Setter
	private MovimentoCaixa movimento;
	
	@Inject
	private MovimentoCaixaRepository movimentoDao;
	
	@Getter
	@Setter
	private BigDecimal totalSaldo = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalFechamento = new BigDecimal("0");
	
	// fim
	
	@Getter
	@Setter
	private int numeroCaixa = 1 ;
	
	@Getter
	@Setter
	private Long numParcela = 1L;
	
	@Getter
	@Setter
	private boolean viewListPedidos= false;
	
	@Inject
	private CreditoRepository creditoDao;
	
	@Getter
	@Setter
	private Credito credito = new Credito();
	
	// VARIAVEIS PARA GERAR CFe
	
	@Getter
	@Setter
	private CFe cfe;
	
	@Inject
	private CFeRepository cfeDao;
	
	@Inject
	private ItemCFeRepository itemCfeDao;
	
	@Inject
	private CalculaTributos calculaTributos;
	
	@Getter
	@Setter
	private boolean satEmitido = false;
	
	@Getter
	@Setter
	private DestinatarioCFe destino = new DestinatarioCFe();
	
	@Inject
	private DestinatarioCFeRepository destinoDao;
	
	@Getter
	@Setter
	private String documento;
	
	@Inject
	private EmitenteCfeRepository emitenteDao;
	
	// Utilitario com todas as funï¿½ï¿½es de estoque
	@Inject
	private EstoqueUtil estoqueUtil;
		
	//Estoque fiscal
	@Inject
	private NcmEstoqueRepository ncmDao;
	
	@Getter
	@Setter
	transient boolean imprimeCupom = false;
	
	@Getter
	@Setter
	private boolean botaoTransferenciaAutomatica = false;
	
	@Inject
	private EmpresaRepository empDao;
	
	@Inject
	private FilialRepository filDao;
	
	@Inject
	private CustoProdutoRepository custoDao;
	
	@Inject
	private ProdutoRepository produtoDao;
	
	@Inject
	private BarrasEstoqueRepository estoqueGeralDao;
	
	@Getter
	private DateTimeFormatter formatador = DateTimeFormatter
	.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	@PostConstruct
	public void init(){
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}

	@Override
	public AgPedido setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AgPedido setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void initializeRelFechamento() {
		this.viewState = ViewState.LISTING;
		this.caixaConferencia = new Caixa();
		this.dataInicial = LocalDate.now();
		this.dataFinal = LocalDate.now();
		this.caixaModel = getLazyCaixas();
	}
	
	/**
	 * Verifica se sistema esta com o modulo frente de caixa ativado
	 * @return false = nao ativado true = ativado
	 */
	public boolean imprimeCupomVenda()  {
		try {
			boolean resp = false;
			if (this.configUser.isVendaCaixa()) {
				resp = true;
			}
			return resp;
		}catch (Exception c) {
			this.addError(true, "caixa.error",c.getMessage());
			return false;
		}
	}
	
	public void initializeExtratoCaixa(Long id) {
		if (id != null) {
			this.caixa = caixaDao.pegaCaixa(id, pegaIdEmpresa(), pegaIdFilial());
			if (this.caixa != null) {
				this.viewState = ViewState.DETAILING;
				this.movimento = new MovimentoCaixa();
				this.listaSaldo = this.saldoDao.listaSaldoDisponivel(this.caixa, pegaIdEmpresa(), pegaIdFilial());
				this.listaValoresAberturaCaixa = this.recebimentoDao.listaRecebimentosPorLivroCaixa(this.caixa, MovimentoEnum.ABre, pegaIdEmpresa(), pegaIdFilial());
				this.listaValoresFechamentoCaixa = this.recebimentoDao.listaRecebimentosPorLivroCaixa(this.caixa, MovimentoEnum.Fech, pegaIdEmpresa(), pegaIdFilial());
				this.listaMovimentacaoSangria = new AbstractDataModel<MovimentoCaixa>(this.movimentoDao.listaMovimentacaoSangriaPorCaixa(this.caixa, pegaIdEmpresa(), pegaIdFilial()));
				this.totalFechamento = calculaTotalListaRecebimento(this.listaValoresFechamentoCaixa);
				this.totalSaldo = calculaTotalListaSaldo(this.listaSaldo);
				this.listaRecebimentosCaixa = this.recebimentoDao.listaRecebimentosPorLivroCaixa(this.caixa, MovimentoEnum.Rec, pegaIdEmpresa(), pegaIdFilial());
//				this.listaAgPedido = this.agrupadoDao.pegaAgPedidoPorCaixa(this.caixa, pegaIdEmpresa(), pegaIdFilial());
//				this.listaPedidosSelecionados = this.pedidoDao.pegaPedidosPorCaixa(this.caixa);
			}
		}
	}
	
	public void geraListaPedidosCaixa() {
		this.setViewListPedidos(true);
		this.listaPedidosSelecionados = this.pedidoDao.pegaPedidosPorCaixa(this.caixa);
	}
	// faz a soma e retorna o resultado 
	public BigDecimal calculaTotalListaSaldo(List<SaldoCaixa> listaTotais ) {
		BigDecimal resultado = new BigDecimal("0");
		for (SaldoCaixa forma : listaTotais) {
			if (forma.getForma() != TipoPagamentoSimples.Crl) {
				resultado = resultado.add(forma.getValor());
			}
		}
		return resultado;
	}
	
	public BigDecimal calculaTotalListaRecebimento(List<RecebimentoParcial> lista) {
		BigDecimal resultado = new BigDecimal("0");
		for (RecebimentoParcial rec : lista) {
			resultado = resultado.add(rec.getValorRecebido().subtract(rec.getTroco()));
		}
		return resultado;
	}
	
	@Override // Tela CaixaRecebimento
	public void initializeListing() {
		// TODO Auto-generated method stub
		try {
			this.caixa = caixaUtil.retornaCaixa(CaixaFinalidade.nao);
			if (this.caixa != null) {
				this.viewState = ViewState.ADDING;
				setBotaoTransferenciaAutomatica(this.empresaUsuario.getEmp().isTranferAutomatico());
				this.listPedidoModel = getLazyPedidosAbertos();
				this.listaPedidosSelecionados = new ArrayList<>();
				this.totalGT = new BigDecimal("0");
				this.pedido = new Pedido();
				this.agrupado = new AgPedido();
				this.dataPesquisa = LocalDate.now();
				this.dataInicial = LocalDate.now();
				this.dataFinal = LocalDate.now();
				this.listaAgPedido = this.agrupadoDao.listaAgPedidoPorStatus(dataInicial, dataFinal, pegaIdEmpresa(), pegaIdFilial(),PedidoStatus.AgR);
				this.novaListaDevolvidos = new ArrayList<Pedido>();
			}else {
				this.viewState = ViewState.DETAILING;
			}
		}catch (CaixaException c) {
			this.addError(true, "caixa.error", c.getMessage());
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	// Tela CaixaReceber - Lanï¿½a os pagamentos 
	public void initializeFormCaixa(Long id) {
		// TODO Auto-generated method stub
		try {
			if (id != null) {
				this.caixa = caixaUtil.retornaCaixa(CaixaFinalidade.rece);
				//				this.caixa.setSaldoCaixa(this.saldoDao.listaSaldoDisponivel(this.caixa, pegaIdEmpresa(), pegaIdFilial()));
				if (this.caixa == null) {
					this.viewState = ViewState.DETAILING;
				}else {
					this.viewState = ViewState.ADDING;
				}
				System.out.println("ID recuperada: " + id);
				this.agrupado = this.agrupadoDao.encontraAgPedidoPorId(id);
				if (this.agrupado.getDestinatario() != null) {
					this.credito = this.creditoDao.retornaCredito(this.agrupado.getDestinatario().getCliente(),  this.agrupado.getDestinatario().getFornecedor(), this.agrupado.getDestinatario().getColaborador(), pegaIdEmpresa(), pegaIdFilial());
					if (this.credito.getId() == null) {
						if (this.agrupado.getDestinatario().getCliente() != null ) {
							this.credito.setCliente(this.agrupado.getDestinatario().getCliente());
						}
						if (this.agrupado.getDestinatario().getFornecedor() != null ) {
							this.credito.setFornecedor(this.agrupado.getDestinatario().getFornecedor());
						}
						if (this.agrupado.getDestinatario().getColaborador() != null ) {
							this.credito.setColaborador(this.agrupado.getDestinatario().getColaborador());
						}
					}
				}
				
				this.listaPagamentos= this.pagamentoDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(),false ,false);
				this.totalBruto = this.agrupado.getValorTotal().add(this.agrupado.getDesconto()).subtract(this.agrupado.getAcrescimo()).subtract(this.agrupado.getFrete());
				this.totalGT = this.agrupado.getValorTotal();
				this.resto = this.agrupado.getValorTotal();
				this.totalRecebido = new BigDecimal("0");
				this.valorRecebido = new BigDecimal("0");
				this.desconto = this.agrupado.getDesconto();
				this.acrescimo = this.agrupado.getAcrescimo().add(this.agrupado.getFrete());
				this.tipoDesconto = true;
				this.troco = new BigDecimal("0");
				this.forma = new FormaDePagamento();
				this.recebimentoParcial = new RecebimentoParcial();
				this.listaRecebimentos = new ArrayList<RecebimentoParcial>();
				this.listaParcelasPagamento = new ArrayList<ParcelasNfe>();
				this.listaPedidosSelecionados = this.pedidoDao.pegaPedidosPorAgPedido(this.agrupado);

			}else {
				throw new CaixaException(this.translate("caixa.recebimento.agPedido.null"));
			}
		}catch (CaixaException c) {
			this.addError(true, "caixa.error",c.getMessage());
		}catch (Exception e) {
			// TODO: handle exception			
			this.addError(true, "exception.error.fatal",e.getMessage());
		}
	}
	
	public void novaListaAgPedidos() { 
		this.listaAgPedido = this.agrupadoDao.listaAgPedidoPorStatus(dataInicial, dataFinal, pegaIdEmpresa(), pegaIdFilial(),PedidoStatus.AgR);
	}
	
	public void recalculaTotais() {
		try {
			if (this.configUser.isDesconto()) {
				System.out.println("Estou recalculando os valores dos pedidos");
				BigDecimal valorDesconto = new BigDecimal("0");
				BigDecimal valorAcrescimo = new BigDecimal("0");
				if (this.tipoDesconto) {
					if (this.configUser.getPorcentagemDesconto().compareTo(this.desconto) == -1) {
						throw new RegraNegocioException(translate("regraNegocio.discount.greater.than.allowed"));
					}
				}else {
					if (this.configUser.getPorcentagemDesconto().compareTo(caixaUtil.retornaPorcentagemDesconto(this.agrupado.getValorBruto(), this.desconto)) == -1) {
						throw new RegraNegocioException(translate("regraNegocio.discount.greater.than.allowed"));
					}
				}
				if (this.totalGT.compareTo(this.resto) == 0) {
					if (this.agrupado.getValorBruto().compareTo(this.agrupado.getValorTotal()) != 0) {
						this.agrupado.setValorTotal(this.agrupado.getValorBruto());
						this.agrupado.setDesconto(new BigDecimal("0"));
						this.agrupado.setAcrescimo(new BigDecimal("0"));
						this.agrupado.setFrete(new BigDecimal("0"));
						
					}
					this.totalGT = this.agrupado.getValorTotal();
					this.resto = this.agrupado.getValorTotal();
					this.agrupado.setDesconto(new BigDecimal("0"));
					this.agrupado.setAcrescimo(new BigDecimal("0"));
					System.out.println("estou dentro da lista formaPagamentos = 0");
					if (this.desconto.compareTo(new BigDecimal("0"))==0 && this.acrescimo.compareTo(new BigDecimal("0"))>0) { // acrescimo
						valorAcrescimo = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.acrescimo, false, this.tipoAcrescimo);
						this.totalGT = this.totalGT.add(valorAcrescimo);
						this.resto = this.resto.add(valorAcrescimo);
						this.agrupado.setAcrescimo(valorAcrescimo);
					}else {
						if (this.desconto.compareTo(new BigDecimal("0"))>0 && this.acrescimo.compareTo(new BigDecimal("0"))==0) { // desconto
							valorDesconto = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.desconto, true, this.tipoDesconto);
							this.totalGT = this.totalGT.subtract(valorDesconto);
							this.resto = this.resto.subtract(valorDesconto);
							this.agrupado.setDesconto(valorDesconto);
						}else { // desconto e acrï¿½scimo. Sendo que primeiro se aplica o desconto para depois se aplicar o acrï¿½scimo
							valorDesconto = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.desconto, true, this.tipoDesconto);
							valorAcrescimo = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.acrescimo, false, this.tipoAcrescimo);
							this.totalGT = this.totalGT.subtract(valorDesconto);
							this.resto = this.resto.subtract(valorDesconto);
							this.totalGT = this.totalGT.add(valorAcrescimo);
							this.resto = this.resto.add(valorAcrescimo);
							this.agrupado.setDesconto(valorDesconto);
							this.agrupado.setAcrescimo(valorAcrescimo);
						}
					}
				}else {
					throw new CaixaException(translate("caixa.recalcula.formaPagamento.notEmpty"));
				}
			}else {
				throw new RegraNegocioException(translate("regraNegocio.not.discount"));
			}
		}catch (CaixaException c) {
			this.addError(true, "caixa.error", c.getMessage());
		}catch (RegraNegocioException r) {
			this.addWarning(true, "caixa.error", r.getMessage());
		}
	}
	
	public void onRowSelectAgPedido(SelectEvent event)throws IOException,CaixaException{
		this.agrupado = (AgPedido)event.getObject();
		this.resto = this.agrupado.getValorRecebido();
	}
	
	public void onRowSelectPedidoDev(SelectEvent event)throws IOException,CaixaException{
		this.pedido = (Pedido)event.getObject();
//		this.resto = this.agrupado.getValorRecebido();
	}
	
//	public void onRowSelectMotivo(SelectEvent event)throws IOException{
//		this.movimento = (MovimentoCaixa)event.getObject();
//	}
	
	public void selectMotivo(MovimentoCaixa movimento){
		this.movimento = movimento;
	}
	
	
	public String toViewExtrato(Long id) {
		return toExtratoCaixa(this.caixaConferencia.getId());
	}
	
	public void onRowSelectCaixa(SelectEvent event)throws IOException{
		this.caixaConferencia = (Caixa)event.getObject();
		this.viewState = ViewState.DETAILING;
	}
	
	public String telaReceberAgrupado() {
		try {
			if (this.agrupado != null) {
				return toTelaReceber(this.agrupado.getId());
			}else {
				throw new CaixaException(translate("caixa.notSelect.AgPedido"));
			}
		}catch (CaixaException c) {
			// TODO: handle exception
			this.addErrorNew(true, "messagesDialog","caixa.error", c.getMessage());
			return null;
		}
	}
	
	/**toTela
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaAgPedido(){

		for (AgPedido agPedido : listaAgPedido) {
			System.out.println("AgPedido IBr: " + agPedido.getId());
		}
		this.updateAndOpenDialog("PesquisaAgPedidoDialog", "dialogPesquisaAgPedido");
	}
	
	/**toTela
	 * Exibe o dialog com a lista de Devoluï¿½ï¿½es a serem confirmadas
	 */
	public void telaListaDevConfirm(){
		geraNovaListaDevolucoes();
		this.updateAndOpenDialog("ConfirmDevolucaoDialog", "dialogConfirmDevolucao");
	}
	
	public void geraNovaListaDevolucoes() {
		
		this.novaListaDevolvidos =  this.pedidoDao.listaPedidoPorTipoEStatus(dataInicial, dataFinal, pegaIdEmpresa(), pegaIdFilial(),PedidoTipo.DEV,PedidoStatus.AgR);
	}
	
	public void geraListaTransferencia() {
		if (this.getUsuarioAutenticado().getIdFilial() != null){ // entende que uma filial esta tentando gerar a lista
			this.listaTransferencia = this.pedidoDao.listaTransferenciaPorTipoStatusDestinoCaixa(this.dataInicial, this.dataFinal, this.empDao.findById(pegaIdEmpresa(),false), this.filDao.findById(pegaIdFilial(), false),PedidoTipo.TRA,PedidoStatus.AgR,false,false);
		}else { // entende que a matriz esta tentando gerar a lista
			this.listaTransferencia = this.pedidoDao.listaTransferenciaPorTipoStatusDestinoCaixa(this.dataInicial, this.dataFinal, this.empDao.findById(pegaIdEmpresa(),false),null,PedidoTipo.TRA,PedidoStatus.AgR,false,true);
		}
	}
	/**toTela
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaTranferConfirm(){

		geraListaTransferencia();
		this.updateAndOpenDialog("ConfirmTransferenciaDialog", "dialogConfirmTransferencia");
	}
	
	
	@Transactional
	public void confirmaTransferencia() {
		this.pedido.setPedidoStatus(PedidoStatus.REC);
		this.pedido.setDataRecebimento(LocalDate.now());
		this.pedido.setHoraRecebimento(LocalTime.now());
		this.pedido.setCaixa(this.caixa);
		this.pedidoDao.save(this.pedido);
	}
	
	/**
	 * Metodo que remove o agrupamento, desvinculando os pedidos 
	 */
	@Transactional
	public String removeAgrupamento() {
		try {
			if (this.agrupado != null) {
				List<Pedido> listaPedidos = new ArrayList<Pedido>();
				listaPedidos = this.agrupado.getListaPedidosRecebidos();
				for (Pedido pedido : listaPedidos) {
					pedido.setAgrupado(null);
					pedido.setPedidoStatus(PedidoStatus.AgR);
					pedidoDao.save(pedido);
				}
				agrupadoDao.delete(this.agrupado);
				return toListRecebimento();
			}else {
				throw new CaixaException(translate("caixa.notSelect.AgPedido"));
			}
		}catch (CaixaException c) {
			// TODO: handle exception
			this.addErrorNew(true, "messagesDialog","caixa.error", c.getMessage());
			return null;
		}
	}
	

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
//		this.agrupado = this.agrupadoDao.localizaAgPedido(id);
		
	}
	
	/**
	 * Inicialilza Form Abertura de caixa
	 * 
	 * verifica se tem algum caixa aberto, caso encontre ele carrega o caixa , caso contrario faz uma pesquisa pelo ï¿½ltimo caixa fechado e 
	 * retorna o saldo anterior para abertura do novo caixa. obs: caso na data de abertura ja tenha um caixa fechado, ele reabre o caixa 
	 * definindo o livro caixa como REABERTURA (somente reabre caixa do dia!)
	 * @throws CaixaException 
	 * 
	 */
	@Transactional
	public void initializeFormAbertura(){
		try {
			Caixa caixaAbertoHoje = new Caixa();
			this.viewState = ViewState.ADDING;
			List<Caixa> listaCaixaTemp = new ArrayList<Caixa>();
			this.saldo = new SaldoCaixa();
			this.recebimentoParcial = new RecebimentoParcial();
			this.tipoSimples = TipoPagamentoSimples.Din;
			this.listaPagamentos= this.pagamentoDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(),false ,false);
			this.forma = new FormaDePagamento();
			this.valorAbertura = new BigDecimal("0");
			System.out.println("data: " + LocalDate.now().minusDays(1L));
			caixaAbertoHoje = caixaUtil.retornaAberturaCaixa(CaixaFinalidade.aber);
			//		caixaAbertoHoje = this.caixaDao.pegaCaixaAbertoUsuario(getUsuarioAutenticado().getName(), LocalDate.now(),StatusCaixa.Abe, pegaIdEmpresa(), pegaIdFilial(),null);
			if (caixaAbertoHoje != null) {
				System.out.println("Caixa Aberto encontrado! - 1");
				if (caixaAbertoHoje.isAberto()) {
					this.viewState = ViewState.LISTING;
					this.addInfo(true, "caixa.isOpen");
					System.out.println("Caixa aberto hoje!");
//					this.caixa = caixaAbertoHoje;
//					this.hasTipoPagSimples = caixaUtil.geraTotaisPorTipoPagamentoSimples(caixaAbertoHoje.getListaRecebimentoCaixa(),this.hasTipoPagSimples);
				}else {// reabre o caixa setando livroCaixa para REABERTO o inserindo hora da reabertura
					System.out.println("Caixa Reaberto - 2");
					this.viewState = ViewState.LISTING;
					this.caixa = caixaAbertoHoje;
					this.caixa.setLivroCaixa(MovimentoEnum.Reab);
					this.caixa.setStatusCaixa(StatusCaixa.Abe);
					this.hasTipoPagSimples = caixaUtil.geraTotaisPorTipoPagamentoSimples(caixaAbertoHoje.getListaRecebimentoCaixa(),this.hasTipoPagSimples);
					this.addInfo(true, "caixa.isReOpen");
				}
			}else {
				System.out.println("Caixa Aberto de Hoje nï¿½o encontrado, procurando caixas abertos em outras datas! - 3");
				listaCaixaTemp = this.caixaDao.pegaCaixasEmAberto(getUsuarioAutenticado().getName(),LocalDate.now().minusDays(10), StatusCaixa.Abe,pegaIdEmpresa(), pegaIdFilial());
				LocalDate maiorData = LocalDate.now().minusDays(5);
				if (listaCaixaTemp.size() >0) {
					System.out.println("Encontrado caixa aberto em algum dia do passado! - 4"); // solicitar o fechamento do caixa!!!! para abrir um novo!
					for (Caixa caixa : listaCaixaTemp) {
						if (caixa.isAberto()) {
							if (caixa.getDataAbertura().isAfter(maiorData)) {
								maiorData = caixa.getDataAbertura();
							}
						}
					}
					caixaAbertoHoje = this.caixaDao.pegaCaixaAbertoUsuario(getUsuarioAutenticado().getName(), maiorData,StatusCaixa.Abe, pegaIdEmpresa(), pegaIdFilial(),null,CaixaFinalidade.fech);

					if (caixaAbertoHoje != null) {
						System.out.println("Carregando caixa do dia " + maiorData + " Aberto!");
						this.caixa = caixaAbertoHoje;
						this.addInfo(true, "caixa.isOpen");
						this.hasTipoPagSimples = caixaUtil.geraTotaisPorTipoPagamentoSimples(caixaAbertoHoje.getListaRecebimentoCaixa(),this.hasTipoPagSimples);
						this.viewState = ViewState.LISTING;
					}
				}else {
					this.viewState = ViewState.ADDING;
					System.out.println("Nï¿½o encontrado nenhum caixa Aberto no banco de dados - 5");
					maiorData = LocalDate.now().minusDays(10);
					listaCaixaTemp = this.caixaDao.pegaCaixasEmAberto(getUsuarioAutenticado().getName(),maiorData, StatusCaixa.Fec,pegaIdEmpresa(), pegaIdFilial());
					Long id = 0l;
					if (listaCaixaTemp.size() >0) {
						System.out.println("Localizado os caixas ja fechado - 6");
						for (Caixa caixa : listaCaixaTemp) {
							if (caixa.isAberto() == false) {
								if (caixa.getDataAbertura().isAfter(maiorData)) {
									maiorData = caixa.getDataAbertura();
									System.out.println("Caixa Fechado: Maior Data --> " + maiorData);
								}
							}						
						}
						if (listaCaixaTemp.size() >1) {
							System.out.println("Estou no caixa Fechando dentro de lista > 1");
							listaCaixaTemp = this.caixaDao.pegaListaCaixaAbertoUsuario(getUsuarioAutenticado().getName(), maiorData,StatusCaixa.Fec, pegaIdEmpresa(), pegaIdFilial(), null);
							System.out.println("tamanho lista " + listaCaixaTemp.size());
							if (maiorData.isEqual(LocalDate.now())) {
								this.numeroCaixa = listaCaixaTemp.size();
							}else {
								this.numeroCaixa = 1;
							}

							for (Caixa caixa : listaCaixaTemp) {
								if (caixa.getId() > id) {
									id = caixa.getId();
								}
							}
							System.out.println("estou iniciando a pesquisa de caixa com ID");
							listaCaixaTemp = this.caixaDao.pegaListaCaixaAbertoUsuario(getUsuarioAutenticado().getName(), maiorData,StatusCaixa.Fec, pegaIdEmpresa(), pegaIdFilial(), id);
						}
						if (listaCaixaTemp.size() == 1) {
							System.out.println("listaCaixaTemp == 1 transferindo o caixa ");
							caixaAbertoHoje = listaCaixaTemp.get(0);
							if (maiorData.isEqual(LocalDate.now())) {
								this.numeroCaixa =caixaAbertoHoje.getNumeroTurno()+ 1 ;
							}else {
								this.numeroCaixa = 1;
							}
						}
						if (caixaAbertoHoje != null) {
							System.out.println("Encontrado o ultimo caixa fechado para base de novo caixa - 7 ");
							this.caixa = new Caixa();
							this.caixa.setAberto(true);
							if (caixaAbertoHoje.getConferencia() == StatusConferencia.Ok) {
								this.hasTipoPagSimples = caixaUtil.hashTipoSimplesLimpo();
								if (caixaAbertoHoje.getFundoCaixa() != null) {
									this.hasTipoPagSimples.put(TipoPagamento.Din.toString(),caixaAbertoHoje.getFundoCaixa());
								}

							}else {
								this.hasTipoPagSimples = caixaUtil.hashTipoSimplesLimpo();
							}
						}
					}else {
						System.out.println("Nï¿½o existe nenhum caixa fechado no sistema, Abrindo um caixa novo -8");
						this.caixa = new Caixa();
						this.caixa.setAberto(true);
						this.hasTipoPagSimples = caixaUtil.hashTipoSimplesLimpo();
					}

				}
			}
		}catch (CaixaException c) {
			this.addError(true, "caixa.error",c.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal",e.getMessage());
		}
	}
	
	public void initializeFormFechamento() {
		try {
			this.viewState = ViewState.ADDING;
			this.caixa = caixaUtil.retornaCaixa(CaixaFinalidade.fech);
			this.caixa.setListaFechamentoCaixa(new ArrayList<RecebimentoParcial>());
			this.tipoSimples = TipoPagamentoSimples.Din;
			if (this.caixa != null) {
				this.caixa.setListaRecebimentoCaixa(this.recebimentoDao.listaRecebimentosPorCaixa(this.caixa,pegaIdEmpresa(), pegaIdFilial()));
				System.out.println("Caixa: " + this.caixa.getUsuario());
				this.valorFundo = new BigDecimal("0");
				this.recebimentoParcial = new RecebimentoParcial();
				this.valorAbertura = new BigDecimal("0");
				this.hasTipoPagSimples = caixaUtil.hashTipoSimplesLimpo();
			}else {
				System.out.println("Caixa = nulo");
			}
		}catch (CaixaException c) {
			this.viewState = ViewState.LISTING;
			this.addWarning(true, "caixa.error", c.getMessage());
		}catch (Exception e ) {
			this.viewState = ViewState.LISTING;
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}

	
	public TipoPagamentoSimples[] listaTipoPagamento() {
		return TipoPagamentoSimples.values();
	}
	
	/**
	 * Usado no form CaixaAbertura e CaixaFechamento gerando a lista para o dataTable
	 * @return
	 */
	public List<Map.Entry<String, BigDecimal>> getGeraListaTipoPagamento(){
		Set<Map.Entry<String, BigDecimal>> TipoPagamentoSet = this.hasTipoPagSimples.entrySet();
		return new ArrayList<Map.Entry<String,BigDecimal>> (TipoPagamentoSet);
	}
	
	public List<Map.Entry<FormaDePagamento, BigDecimal>> getListaFormaPagamentoInserido(){
		Set<Map.Entry<FormaDePagamento, BigDecimal>> TipoPagamentoSet = this.hashRecebimentoParcialCaixa.entrySet();
		return new ArrayList<Map.Entry<FormaDePagamento,BigDecimal>> (TipoPagamentoSet);
	}
	
	public void addRecebimentoParcial(FormaDePagamento tipo,BigDecimal valor)throws CaixaException{
		if (tipo == null) {
			throw new CaixaException(translate("caixa.error.forma.notSelect"));
		}
		if (valor.compareTo(new BigDecimal("0"))<1) {
			throw new CaixaException(translate("caixa.error.valor.isEmpty"));
		}
		System.out.println("Resultado comparacao: " + valor.compareTo(this.resto) + " tipo Pagamento informado: " + tipo.getTipoPagamento()
				+ "Equals tipopagamento com ! : " + !tipo.getTipoPagamento().equals(TipoPagamento.Din));
		if (this.resto.compareTo(new BigDecimal("0"))>0) {
			if (valor.compareTo(this.resto)>0 && !tipo.getTipoPagamento().equals(TipoPagamento.Din)) {
				throw new CaixaException(this.translate("caixa.valorMaiorQuePermitido"));
			}else {
				if (tipo.getTipoPagamento().equals(TipoPagamento.Din)) {
					if (valor.compareTo(this.resto)>0) {
						this.totalRecebido = this.totalRecebido.add(valor);
						this.troco = valor.subtract(this.resto);
						this.resto = this.totalGT.subtract(this.totalRecebido).add(this.troco);
					}else {
						this.totalRecebido = this.totalRecebido.add(valor);
						this.resto = this.totalGT.subtract(this.totalRecebido);
					}
				}else {
					if (tipo.getTipoPagamento().equals(TipoPagamento.Crl)) {
						this.credito.setSaldoCreditoDevolucao(this.credito.getSaldoCreditoDevolucao().subtract(valor));
					}
					this.totalRecebido = this.totalRecebido.add(valor);
					this.resto = this.totalGT.subtract(this.totalRecebido);
				}
				if (this.hashRecebimentoParcialCaixa.containsKey(tipo)) {
					this.hashRecebimentoParcialCaixa.put(tipo, this.hashRecebimentoParcialCaixa.get(tipo).add(valor));
				}else {
					this.hashRecebimentoParcialCaixa.put(tipo, valor);
				}
			}
		}else {
			throw new CaixaException(translate("caixa.falta.equal.to.zero"));
		}
	}
	
	public void removeRecebimentoParcial(FormaDePagamento forma) {
		if (this.hashRecebimentoParcialCaixa.containsKey(forma)) {
			if (this.hashRecebimentoParcialCaixa.size() == 1) {
				this.totalRecebido = new BigDecimal("0");
				this.resto = this.totalGT;
				this.troco = new BigDecimal("0");
			}else {
				this.totalRecebido = this.totalRecebido.subtract(this.hashRecebimentoParcialCaixa.get(forma));
				this.resto = this.totalGT.subtract(this.totalRecebido);
			}
			if (this.resto.compareTo(new BigDecimal("0"))>0) {
				this.troco = new BigDecimal("0");
			}
			if (forma.getTipoPagamento().equals(TipoPagamento.Crl)) {
				this.credito.setSaldoCreditoDevolucao(this.credito.getSaldoCreditoDevolucao().add(this.hashRecebimentoParcialCaixa.get(forma)));
			}
			this.hashRecebimentoParcialCaixa.remove(forma);
			// removendo as parcelas que corresponde a forma de pagamento
			List<ParcelasNfe> listaTempRemover = new ArrayList<ParcelasNfe>();
			for(Iterator<ParcelasNfe> parc = this.listaParcelasPagamento.iterator(); parc.hasNext();) {
				ParcelasNfe parcela = parc.next();
				if (parcela.getFormaPag().equals(forma)) {
					listaTempRemover.add(parcela);
				}
			}
			this.listaParcelasPagamento.removeAll(listaTempRemover);
		}
	}
	
	
	public void zeraValor(String forma) {
		if (this.hasTipoPagSimples.containsKey(forma.toString())) {
			System.out.println("Zerando Valor");
			this.hasTipoPagSimples.replace(forma.toString(),new BigDecimal("0"));
		}else {
			this.addError(true, "Meio de pagamento nao consta na lista");
		}
	}
	
	@Transactional
	public void doCloseCaixa() throws IOException {
//		try {
			if (this.caixa != null) {
				if (this.caixa.isAberto()) {
					System.out.println("Dentro do caixa Aberto");
					this.caixa.setDataFechamento(LocalDate.now());
					this.caixa.setHoraFechamento(LocalTime.now());
					this.caixa.setStatusCaixa(StatusCaixa.Fec);
					this.caixa.setLivroCaixa(MovimentoEnum.Fech);
					this.caixa.setFundoCaixa(this.hasTipoPagSimples.get(TipoPagamentoSimples.Fun.toString()));
					for (TipoPagamentoSimples tipoPagSimples : TipoPagamentoSimples.values()) {
						if (this.hasTipoPagSimples.containsKey(tipoPagSimples.toString())) {
							this.recebimentoParcial = new RecebimentoParcial();
							this.recebimentoParcial.setCaixa(this.caixa);
							this.recebimentoParcial.setDeleted(false);
							this.recebimentoParcial.setTipoPagamento(caixaUtil.converteTipoSimples(tipoPagSimples));
							this.recebimentoParcial.setValorRecebido(this.hasTipoPagSimples.get(tipoPagSimples.toString()));
							this.recebimentoParcial.setLivroCaixa(MovimentoEnum.Fech);
							this.caixa.getListaFechamentoCaixa().add(this.recebimentoParcial);
						}
					}
					// Faz a conferï¿½ncia do caixa e seta o StatusConferencia
					if (conferenciaDeCaixa(this.caixa)) {
						this.caixa.setConferencia(StatusConferencia.Ok);
					}else {
						this.caixa.setConferencia(StatusConferencia.Error);
					}
					this.caixa.setAberto(false);
				this.caixa = this.caixaDao.save(this.caixa);
				// imprimir resumo digitado pelo operador de caixa!
				this.impressora.imprimiSaldoFechandoOperado(this.caixa, this.configUser);
				this.viewState = ViewState.LISTING;
				this.addInfo(true, "caixa.close.sucess");
				}
			}
//		}catch (HibernateException h) {
//			this.addError(true, "hibernate.persist.fail", h.getMessage());
//			System.out.println(h.getMessage());
//		}catch (NullPointerException n) {
//			this.addError(true, "nullPointer.null", n.getMessage());
//			System.out.println(n.getMessage());
//		}catch (Exception e) {
//			this.addError(true, "exception.error.fatal", e.getMessage());
//			System.out.println(e.getMessage());
//		}
	}
	
	/**
	 *  Conferï¿½ncia de caixa
	 * @param caixa
	 * @return True = Caixa conferido sem erro   False = Caixa conferido mas possui divergï¿½ncias
	 */
	public boolean conferenciaDeCaixa(Caixa caixa) {
		HashMap<String, BigDecimal> recebiveis = new HashMap<String, BigDecimal>();
		HashMap<String, BigDecimal> fechamento = new HashMap<String, BigDecimal>();
		HashMap<String, String > resultado = new HashMap<String, String>(); 
		BigDecimal fundoFechamento = new BigDecimal("0",mc);
		fundoFechamento = caixa.getFundoCaixa();
		recebiveis = caixaUtil.saldoDisponivel(caixa);
		fechamento = caixaUtil.geraTotaisPorTipoPagamentoSimples(caixa.getListaFechamentoCaixa(),fechamento);
		boolean resposta = true;
		for (TipoPagamentoSimples formaDePagamento : TipoPagamentoSimples.values()) {
			if (formaDePagamento.equals(TipoPagamentoSimples.Din)) {
				BigDecimal somaFundoCaixa = new BigDecimal("0");
				BigDecimal somaFundoCaixaAbertura = new BigDecimal("0");
				
				somaFundoCaixa = fechamento.get(formaDePagamento.toString()).setScale(3,RoundingMode.HALF_EVEN).add(fundoFechamento).setScale(3,RoundingMode.HALF_EVEN);
				somaFundoCaixaAbertura = recebiveis.get(formaDePagamento.toString()).setScale(3,RoundingMode.HALF_EVEN).add(recebiveis.get(TipoPagamentoSimples.Fun.toString()).setScale(3,RoundingMode.HALF_EVEN));
				System.out.println("Valor soma Din + fundo " + somaFundoCaixa);
				if (somaFundoCaixaAbertura.compareTo(somaFundoCaixa.setScale(3,RoundingMode.HALF_EVEN))==0) {
					resultado.put(formaDePagamento.toString(), "OK");
				}else {
						resultado.put(formaDePagamento.toString(), "Erro");
				}
			}else {
				if (formaDePagamento.equals(TipoPagamentoSimples.Fun) || formaDePagamento.equals(TipoPagamentoSimples.Crl)) {
					System.out.println("nao calcular!!!");
				}else {
					if (recebiveis.get(formaDePagamento.toString()).setScale(3,RoundingMode.HALF_EVEN).compareTo(fechamento.get(formaDePagamento.toString()).setScale(3,RoundingMode.HALF_EVEN)) == 0) {
						resultado.put(formaDePagamento.toString(), "OK");
					}else {
						if (recebiveis.get(formaDePagamento.toString()).setScale(3,RoundingMode.HALF_EVEN).compareTo(fechamento.get(formaDePagamento.toString()).setScale(3,RoundingMode.HALF_EVEN)) == 1) {
							resultado.put(formaDePagamento.toString(), "Faltando");
						}else {
							resultado.put(formaDePagamento.toString(), "Passando");
						}
					}
				}
			}
		}
		for (TipoPagamentoSimples formaResul : TipoPagamentoSimples.values()) {
			System.out.println("Dentro do verifica resultado.");
			if (resultado.containsKey(formaResul.toString())) {
				System.out.println("1 Saldo R$:" + recebiveis.get(formaResul.toString()) + " - " + formaResul.toString());
				System.out.println("1 Fechamento R$:" + fechamento.get(formaResul.toString()) + " - " + formaResul.toString());
				if (resultado.get(formaResul.toString()) != "OK") {
					System.out.println(resultado.get(formaResul.toString()) + " " + formaResul.toString());
					System.out.println("Saldo R$:" + recebiveis.get(formaResul.toString()) + " - " + formaResul.toString());
					System.out.println("Fechamento R$:" + fechamento.get(formaResul.toString()) + " - " + formaResul.toString());
					resposta = false;
				}
			}
		}
		return resposta;
	}
	
	/**
	 * Insere os valores na respectivo Tipo de pagamento
	 */
	public void insereVal() {
		System.out.println("iniciando a inclusï¿½o do valor  R$ "  + this.valorAbertura);
		BigDecimal tot = new BigDecimal("0");
		if (this.hasTipoPagSimples.containsKey(this.tipoSimples.toString())) {
			System.out.println("Forma de pagamento localizada");
			tot = this.hasTipoPagSimples.get(this.tipoSimples.toString()).add(this.valorAbertura);
			System.out.println("O valor a ser inseridor ï¿½ de R$ " + this.valorAbertura + " com o meio de pagamento " + this.tipoSimples.toString() + " saldo atual ï¿½ R$ " + tot);
			this.hasTipoPagSimples.replace(this.tipoSimples.toString(), tot);
		}else {
			this.addError(true, "Meio de pagamento nao consta na lista");
		}
		this.valorAbertura = new BigDecimal("0");
	}
	
	/**
	 * Insere os valores na respectivo Tipo de pagamento
	 */
	public void insereValFechamento() {
		System.out.println("iniciando a inclusï¿½o do valor  R$ "  + this.valorAbertura);
		BigDecimal tot = new BigDecimal("0");
		if (this.hasTipoPagSimples.containsKey(this.tipoSimples.toString())) {
			System.out.println("Forma de pagamento localizada");
			tot = this.hasTipoPagSimples.get(this.tipoSimples.toString()).add(this.valorAbertura);
			System.out.println("O valor a ser inseridor ï¿½ de R$ " + this.valorAbertura + " com o meio de pagamento " + this.tipoSimples.toString() + " saldo atual ï¿½ R$ " + tot);
			this.hasTipoPagSimples.replace(this.tipoSimples.toString(), tot);
			
		}else {
			this.addError(true, "Meio de pagamento nao consta na lista");
		}
	}
	
	
	@Transactional
	public void doSaveAbertura() {
		try {
			if (this.caixa.getId() != null) { // Caixa Jï¿½ aberto
				this.addError(true, "caixa.isOpen");
			}else {// Novo Caixa
				this.caixa.setDataAbertura(LocalDate.now());
				this.caixa.setHoraAbertura(LocalTime.now());
				this.caixa.setUsuario(getUsuarioAutenticado().getName());
				this.caixa.setStatusCaixa(StatusCaixa.Abe);
				this.caixa.setLivroCaixa(MovimentoEnum.ABre);
				this.caixa.setNumeroTurno(this.numeroCaixa);
				for (TipoPagamentoSimples tipoSimples : TipoPagamentoSimples.values()) {
					if (this.hasTipoPagSimples.containsKey(tipoSimples.toString())) {
						this.recebimentoParcial = new RecebimentoParcial();
						this.recebimentoParcial.setCaixa(this.caixa);
						this.recebimentoParcial.setDeleted(false);
						this.recebimentoParcial.setValorRecebido(this.hasTipoPagSimples.get(tipoSimples.toString()));
						this.recebimentoParcial.setLivroCaixa(MovimentoEnum.ABre);
						this.recebimentoParcial.setTipoPagamento(caixaUtil.converteTipoSimples(tipoSimples));
						this.caixa.getListaRecebimentoCaixa().add(this.recebimentoParcial);
						
					}
				}
				this.caixa = caixaUtil.preencheSaldoCaixa(this.caixa,MovimentoEnum.ABre,this.listaRecebimentos);
				this.caixa = this.caixaDao.save(this.caixa);
				this.viewState = ViewState.LISTING;
				this.addInfo(true, "caixa.open.sucess");
				
			}
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (CaixaException c) {
			this.addError(true, "caixa.error", c.getMessage());
		}catch (NullPointerException n) {
			this.addError(true, "nullPointer.null", n.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
		
	}
	
	public HashMap<FormaDePagamento, BigDecimal> geraHashDeFormaDePagamentoLimpa(List<FormaDePagamento> lista){
		HashMap<FormaDePagamento, BigDecimal> hasFormaTemp = new HashMap<FormaDePagamento, BigDecimal>();
		for (FormaDePagamento formaDePagamento : lista) {
			hasFormaTemp.put(formaDePagamento, new BigDecimal("0"));
		}
		return hasFormaTemp;
	}
	
	
	/**
	 * cria uma lista baseado no HASHMAP de forma de pagamentos para exibiï¿½ï¿½o como os valores
	 * @return
	 */
	public List<Map.Entry<FormaDePagamento, BigDecimal>> getFormas(){ 
		Set<Map.Entry<FormaDePagamento, BigDecimal>> formaDePagamentoSet = this.hasForma.entrySet();
		return new ArrayList<Map.Entry<FormaDePagamento,BigDecimal>> (formaDePagamentoSet);
	}
	
	/**
	 * cria uma lista Vazia para Forma de pagamentos a ser preenchida de acordo com a seleï¿½ï¿½o do usuï¿½rio
	 * @return
	 */
	public List<Map.Entry<FormaDePagamento, BigDecimal>> getHasFormaVazia(){
		return new ArrayList<Map.Entry<FormaDePagamento,BigDecimal>>();
	}
	
	public void aberturaCaixaForm() {
		this.caixa = new Caixa();
		
	}
	
	public void fechamentoCaixaForm() {
		this.listaDeCaixasAberto = caixaDao.pegaCaixasEmAberto(getUsuarioAutenticado().getName(),LocalDate.now().minusDays(10),StatusCaixa.Abe,pegaIdEmpresa(), pegaIdFilial());
		this.caixa = caixaDao.pegaCaixaAbertoUsuario(getUsuarioAutenticado().getName(),LocalDate.now(),StatusCaixa.Abe,pegaIdEmpresa(),pegaIdFilial(),null,CaixaFinalidade.fech);
		
	}
	
	public void novaLista() {
		this.listaPedidosSelecionados = new ArrayList<Pedido>();
		this.totalGT = new BigDecimal("0");
		this.getLazyPedidosAbertos();
	}
	
	/**
	 * Lista de Pedidos em Aberto modo Lazy
	 * @return lista de pedidos
	 */
	public AbstractLazyModel<Pedido> getLazyPedidosAbertos(){
		this.listPedidoModel = new AbstractLazyModel<Pedido>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8240850644683518994L;

			@Override
			public List<Pedido> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<Pedido> page = pedidoDao.listaPedidosEmitidosPorIntervaloDataEmAberto(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(), pageRequest,null,null);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = pedidoDao.listaPedidosEmitidosPorIntervaloDataEmAberto(dataInicial,dataFinal, pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				return page.getContent();
			}
		};
		
		return listPedidoModel;
	}
	
	/**
	 * Lista de Caixas modo Lazy
	 * @return lista de caixas
	 */
	public AbstractLazyModel<Caixa> getLazyCaixas(){
		this.caixaModel = new AbstractLazyModel<Caixa>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8240850644683518994L;

			@Override
			public List<Caixa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<Caixa> page = caixaDao.pegaListaCaixaLazy(dataInicial,dataFinal,StatusCaixa.Fec, getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(), pageRequest,null,null);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = caixaDao.pegaListaCaixaLazy(dataInicial,dataFinal,StatusCaixa.Fec, getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				return page.getContent();
			}
		};
		
		return caixaModel;
	}
	
	/**
	 * 	Método que agrupa os pedidos selecionandos em um unico pedido (Grande Pedido) permitindo receber 
	 * todos os pedidos selecionados de uma unica vez.
	 * @throws CaixaException 
	 */
	@Transactional
	public String agrupa() throws CaixaException {
		try {
			this.totalGT = new BigDecimal("0");
			BigDecimal desconto = new BigDecimal("0");
			BigDecimal frete = new BigDecimal("0");
			BigDecimal acrescimo = new BigDecimal("0");
			
			for (Pedido pedido : listaPedidosSelecionados) {
				this.agrupado.getListaPedidosRecebidos().add(pedido);
				this.agrupado.getListaItensAgrupados().addAll(pedido.getListaItensPedido());
				pedido.setPedidoStatus(PedidoStatus.Agp);
				this.totalGT = this.totalGT.add(pedido.getValorTotalPedido());
				this.totalBruto = this.totalBruto.add(pedido.getValorTotalProdutos());
				desconto = desconto.add(pedido.getDesconto());
				frete = frete.add(pedido.getValorFrete());
				acrescimo = acrescimo.add(pedido.getOutrasDespesas());
				if (this.pedido.getDestino() != null) {
					this.agrupado.setDestinatario(pedido.getDestino());
				}
			}
			
			this.agrupado.setValorTotal(this.totalGT);
			this.agrupado.setValorBruto(this.totalBruto);
			this.agrupado.setDesconto(desconto);
			this.agrupado.setFrete(frete);
			this.agrupado.setAcrescimo(acrescimo);
			this.agrupado.setDataCriacao(LocalDate.now());
			this.agrupado.setHoraCriacao(LocalTime.now());
			this.agrupado.setStatus(PedidoStatus.AgR);
			this.agrupado.setCaixa(this.caixa);
			if (this.listaPedidosSelecionados.size() != 0) {
				if (this.listaPedidosSelecionados.size() > 20) {
					throw new CaixaException(translate("caixa.limit.pedidos"));
				}
				this.agrupado = this.agrupadoDao.save(this.agrupado);
			}else {
				throw new CaixaException(this.translate("caixa.list.isEmpty"));
			}
			for (Pedido pedido : listaPedidosSelecionados) {
				pedido.setAgrupado(this.agrupado);
				pedidoDao.save(pedido);

			}
			return toTelaReceber(this.agrupado.getId());
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	public String pegaCnpjCpf(Pedido pedido) {
		String resultado = "";
		if (pedido.getDestino().getCliente()== null && pedido.getDestino().getColaborador() == null && pedido.getDestino().getFilial() == null 
				&& pedido.getDestino().getEmpresa() == null && pedido.getDestino().getFornecedor() == null ) {
			resultado = "";
		}else {
			switch (pedido.getTipoPesquisa()) {
			case CLI:
				if (pedido.getDestino().getCliente().getCnpj() == "" || pedido.getDestino().getCliente().getCnpj() == null) {
					if (pedido.getDestino().getCliente().getCpf() == "" || pedido.getDestino().getCliente().getCpf() == null ) {
						resultado = "";
					}else {
						resultado = pedido.getDestino().getCliente().getCpf();
					}
				}else {
					resultado = pedido.getDestino().getCliente().getCnpj();
				}
				break;
			case COL:
				if (pedido.getDestino().getCliente().getCpf() == "" || pedido.getDestino().getCliente().getCpf() == null ) {
					resultado = "";
				}else {
					resultado = pedido.getDestino().getCliente().getCpf();
				}
				break;
			case FOR:
				if (pedido.getDestino().getFornecedor().getCnpj() == "" || pedido.getDestino().getFornecedor().getCnpj() == null) {
					if (pedido.getDestino().getFornecedor().getCpf() == "" || pedido.getDestino().getFornecedor().getCpf() == null ) {
						resultado = "";
					}else {
						resultado = pedido.getDestino().getFornecedor().getCpf();
					}
				}else {
					resultado = pedido.getDestino().getFornecedor().getCnpj();
				}
				break;
			case FIL:
				resultado = pedido.getDestino().getFilial().getCnpj();
				break;
			case MAT:
				resultado = pedido.getDestino().getEmpresa().getCnpj();
				break;
			default:
				break;
			}
		}
		return resultado;
	}
	
	public void onRowSelect(SelectEvent event)throws IOException {
		System.out.println( event.getObject());
		boolean pedVerifica = true;
		boolean naoTemNaLista = true;
		this.pedido = (Pedido) event.getObject();
		if (this.listaPedidosSelecionados.isEmpty()) {
			this.listaPedidosSelecionados.add(this.pedido);
			this.totalGT = this.totalGT.add(pedido.getValorTotalPedido());
		}else {
			for (Pedido ped : this.listaPedidosSelecionados) {
				if (pegaCnpjCpf(this.pedido).equals(pegaCnpjCpf(ped))) {
					pedVerifica = false;
				}
			}
			if (pedVerifica == false) {
				for (Pedido pedverificado : this.listaPedidosSelecionados) {
					if (this.pedido.equals(pedverificado)) {
						naoTemNaLista = false;
					}
				}
				if (naoTemNaLista) {
					this.listaPedidosSelecionados.add(this.pedido);
					this.totalGT = this.totalGT.add(pedido.getValorTotalPedido());
				}
			}
		}
	}
	
	public void onUnSelect(UnselectEvent event) throws IOException{
		this.listaPedidosSelecionados.remove((Pedido) event.getObject());
	}
	public void localizaNaListaPedido() {
		try {
			boolean temNaLista = false;
			boolean naoTemNaLista = true;
			boolean pedVerifica = true;
			Pedido pedTemp = new Pedido();

			if (this.controle != null) {
				pedTemp = this.pedidoDao.localizaPedidoPorControle(this.controle,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.PVE,PedidoStatus.AgR);
				if (pedTemp == null) {
					throw new CaixaException(this.translate("caixa.order.notFound"));
				}else {
					temNaLista = true;
				}
			}else {
				for (Pedido itemLista : this.listPedidoModel) {
					if (itemLista.getControle().getControle() == this.controle) {
						temNaLista = true;
						pedTemp = itemLista;
					}
				}
			}
			if (temNaLista && this.listaPedidosSelecionados.isEmpty()) {
				this.pedido = pedTemp;
				this.listaPedidosSelecionados.add(pedTemp);
				this.totalGT = this.totalGT.add(pedTemp.getValorTotalPedido());
			}else {
				if (temNaLista && pedTemp != null) {
					for (Pedido itemListaSelecionados : this.listaPedidosSelecionados) {
						if (itemListaSelecionados.equals(pedTemp)) {
							naoTemNaLista = false;
						}
					}
					if (naoTemNaLista) {
						for (Pedido pedidoVerifica : this.listaPedidosSelecionados) {
							if (pegaCnpjCpf(pedidoVerifica).equals(pegaCnpjCpf(pedTemp))) {
								pedVerifica = false;
							}
						}
						if (pedVerifica == false) {
							this.pedido = pedTemp;
							this.listaPedidosSelecionados.add(pedTemp);
							this.totalGT = this.totalGT.add(pedTemp.getValorTotalPedido());
						}
					}else {
						throw new CaixaException(this.translate("Pedido jÃ¡ consta na lista de selecionados"));
					}
				}
			}
		}catch (CaixaException c) {
			this.addWarning(true, "caixa.Warning", c.getMessage());
		}
	}
		
	/**
	 * MÃ©todo que permite remover da listaPedidosSelecionados um pedido. 
	 * @param itemSelect
	 */
	public void excluiItem(Pedido pedidoSelect){
		try{
			this.listaPedidosSelecionados.remove(pedidoSelect);
			this.totalGT = this.totalGT.subtract(pedidoSelect.getValorTotalPedido());
		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}
	
	/**
	 * Redireciona para tela Principal
	 * @return Dashboard
	 */
	public String toTelaPrincipal() {
		return "/main/dashboard.xhtml?faces-redirect=true";
	}
	
	public String toListRecebimento() {
		return "formCaixaRecebimento.xhtml?faces-redirect=true";
	}
	
	/**
	 * Redireciona para tela Principal
	 * @return Dashboard
	 */
	public String toTelaReceber(Long idAgPedido) {
		return "/main/financial/caixa/formCaixaReceber.xhtml?faces-redirect=true&idAgPedido=" + idAgPedido;
	}
	
	public String toExtratoCaixa(Long idCaixa) {
		return "/main/financial/caixa/relatorio/formExtratoFechamentoCaixa.xhtml?faces-redirect=true&idCaixa=" + idCaixa;
	}
	
	public TipoPagamentoSimples[] getlistaTipoPagamentoSimples() {
		return TipoPagamentoSimples.values();
	}
	
	public void inserePagamento()   {
		try {
			if (this.forma == null) {
				throw new CaixaException(this.translate("caixa.payment.notSelect"));
			}else {
				boolean pagamentoAceito = false;
				for (TipoPagamentoSimples tipo : TipoPagamentoSimples.values()) {
					if (this.forma.getTipoPagamento().equals(caixaUtil.converteTipoSimples(tipo))){
						pagamentoAceito = true;
					}
				}
				if (this.forma.getTipoPagamento().equals(TipoPagamento.Crl)){
					if (this.credito.getSaldoCreditoDevolucao().compareTo(new BigDecimal("0"))>0 && this.credito.getSaldoCreditoDevolucao().compareTo(this.valorRecebido)>=0) {
						pagamentoAceito= true;
					}else {
						throw new CaixaException(this.translate("caixa.payment.credit.equal.zero"));
					}
				}
				if (pagamentoAceito == false) {
						throw new CaixaException(this.translate("caixa.payment.notAllowed") + TipoPagamento.Spg.toString());
				}else {
					addRecebimentoParcial(this.forma, this.valorRecebido);
					this.listaParcelasPagamento.addAll(preencheParcelamento(this.forma, this.valorRecebido,this.troco));
					this.valorRecebido = new BigDecimal("0");
					this.forma = new FormaDePagamento();
				}
			}
		}catch (CaixaException c) {
			this.addWarning(true, "caixa.error", c.getMessage());
		}catch (NullPointerException n) {
			this.addError(true, "caixa.error", n.getMessage());
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	
	/**
	 * Preenche e os campos do AgPedido, Pedido e ItemPedido 
	 * @param Lista de Pedidos
	 * @param AgPedido
	 * @throws CaixaException
	 */
	@Transactional
	public void preencheListasPedidosERateiaAcrescimoDescontoNosItens(List<Pedido> lista,AgPedido gt) throws CaixaException,HibernateException,Exception {
		MathContext mc = new MathContext(20,RoundingMode.HALF_EVEN);
		BigDecimal descontoPorItem = new BigDecimal("0");
		BigDecimal acrescimoPorItem  = new BigDecimal("0");
		BigDecimal porcentagemDesc = new BigDecimal("0");
		BigDecimal porcentagemAcresc = new BigDecimal("0");
		List<ItemPedido> listaItem = new ArrayList<ItemPedido>();
		BigDecimal descontoPedido =new BigDecimal("0");
		BigDecimal acrescimoPedido =new BigDecimal("0");
		BigDecimal acrescimoFracaoPedido =new BigDecimal("0");
		int quantPedidos = lista.size();
		boolean desc = false;
		boolean acres =false;
		if (gt == null) {
			throw new CaixaException(translate("caixa.AgPedido.isNull"));
		}
		if (gt.getDesconto().compareTo(new BigDecimal("0"))>0 ) {
			porcentagemDesc = (gt.getValorBruto().subtract(gt.getDesconto())).divide(gt.getValorBruto(),mc).setScale(10,RoundingMode.HALF_EVEN);			
			desc = true;
			System.out.println("Estou dentro do gtDesconto  desc = " + desc + "porcentagem = " + porcentagemDesc);
		}
		if (gt.getAcrescimo().compareTo(new BigDecimal("0"))>0 ) {
			porcentagemAcresc = (gt.getValorBruto().add(gt.getAcrescimo())).divide(gt.getValorBruto(),mc).setScale(10,RoundingMode.HALF_EVEN);		
			acres= true;
			System.out.println("Estou dentro do gTacrescimo acres = " + acres);
		}
		if (quantPedidos > 0 && quantPedidos < 21) {
			for(Iterator<Pedido> pedIterator = lista.iterator(); pedIterator.hasNext();) {
				Pedido ped = pedIterator.next();				
				listaItem =  pedidoDao.pegaPedidoPorId(ped.getId()).getListaItensPedido();
				System.out.println("Recuperei a lista de itens do pedido! total de itens: " + listaItem.size());
				if (acres && desc == false ) { // acrescimo
					acrescimoPedido = (ped.getValorTotalProdutos().multiply(porcentagemAcresc,mc)).subtract(ped.getValorTotalProdutos());
					System.out.println("Estou dentro do acrescimo");
					BigDecimal totAcrescimo = new BigDecimal("0");
					for (ItemPedido itemPedido : listaItem) {
							acrescimoPorItem = (itemPedido.getValorTotalBruto().multiply(porcentagemAcresc,mc)).subtract(itemPedido.getValorTotalBruto()).setScale(2,RoundingMode.HALF_EVEN);
							itemPedido.setValorDespesas(acrescimoPorItem);
							itemPedido.setValorTotal(itemPedido.getValorTotalBruto().add(acrescimoPorItem));			
							totAcrescimo = totAcrescimo.add(acrescimoPorItem);
					}
					// calculando possivel diferenï¿½a no acrescimo
					BigDecimal restoAcrescItem = new BigDecimal("0");
					restoAcrescItem = totAcrescimo.subtract(acrescimoPedido);
					if (restoAcrescItem.compareTo(new BigDecimal("0"))>0) {
						listaItem.get(0).setValorDespesas(listaItem.get(0).getValorDespesas().add(restoAcrescItem));
						listaItem.get(0).setValorTotal(listaItem.get(0).getValorTotal().add(restoAcrescItem));
					}
				}
				if (desc && acres == false) { // Desconto
					descontoPedido = ped.getValorTotalProdutos().subtract((ped.getValorTotalProdutos().multiply(porcentagemDesc,mc)).setScale(2,RoundingMode.HALF_EVEN));
					System.out.println("Desconto Pedido =  " + descontoPedido);
					BigDecimal totDescItem = new BigDecimal("0");
					for (ItemPedido itemPedido : listaItem) {
						descontoPorItem = itemPedido.getValorTotalBruto().subtract((itemPedido.getValorTotalBruto().multiply(porcentagemDesc,mc)).setScale(2,RoundingMode.HALF_EVEN));
						itemPedido.setDesconto(descontoPorItem);
						itemPedido.setValorTotal(itemPedido.getValorTotalBruto().subtract(descontoPorItem));			
						totDescItem = totDescItem.add(descontoPorItem);
					}
					// calculando possivel diferenï¿½a no desconto
					BigDecimal restoDescItem = new BigDecimal("0");
					restoDescItem = totDescItem.subtract(descontoPedido);
					if (restoDescItem.compareTo(new BigDecimal("0"))>0) {
						listaItem.get(0).setDesconto(listaItem.get(0).getDesconto().add(restoDescItem));
						listaItem.get(0).setValorTotal(listaItem.get(0).getValorTotal().add(restoDescItem));
					}
				}
				if (desc && acres) { // desconto e acrescimo
					descontoPedido = ped.getValorTotalProdutos().subtract((ped.getValorTotalProdutos().multiply(porcentagemDesc,mc)).setScale(2,RoundingMode.HALF_EVEN));
					acrescimoPedido = (ped.getValorTotalProdutos().multiply(porcentagemAcresc,mc)).subtract(ped.getValorTotalProdutos().setScale(2,RoundingMode.HALF_EVEN));
					
					BigDecimal totAcrescimo = new BigDecimal("0");
					BigDecimal totDescItem = new BigDecimal("0");
					for (ItemPedido itemPedido : listaItem) {
						acrescimoPorItem = (itemPedido.getValorTotalBruto().multiply(porcentagemAcresc,mc)).subtract(itemPedido.getValorTotalBruto()).setScale(2,RoundingMode.HALF_EVEN);
						descontoPorItem = itemPedido.getValorTotalBruto().subtract((itemPedido.getValorTotalBruto().multiply(porcentagemDesc,mc)).setScale(2,RoundingMode.HALF_EVEN));
						
						itemPedido.setValorDespesas(acrescimoPorItem);
						itemPedido.setValorTotal(itemPedido.getValorTotalBruto().add(acrescimoPorItem).subtract(descontoPorItem));			
						itemPedido.setDesconto(descontoPorItem);
						
						totDescItem = totDescItem.add(descontoPorItem);
						totAcrescimo = totAcrescimo.add(acrescimoPorItem);
						
					}
					// calculando possivel diferenï¿½a no acrescimo
					BigDecimal restoDescItem = new BigDecimal("0");
					BigDecimal restoAcrescItem = new BigDecimal("0");
					restoAcrescItem = totAcrescimo.subtract(acrescimoPedido);
					restoDescItem = totDescItem.subtract(descontoPedido);
					if (restoAcrescItem.compareTo(new BigDecimal("0"))>0) {
						listaItem.get(0).setValorDespesas(listaItem.get(0).getValorDespesas().add(restoAcrescItem));
						listaItem.get(0).setValorTotal(listaItem.get(0).getValorTotal().add(restoAcrescItem).subtract(restoDescItem));
					}
					
				}
				ped.setListaItensPedido(listaItem);
				ped.setDesconto(descontoPedido.setScale(2,RoundingMode.HALF_EVEN));
				ped.setOutrasDespesas(acrescimoPedido.setScale(2,RoundingMode.HALF_EVEN));
				ped.setValorTotalPedido(ped.getValorTotalProdutos().subtract(descontoPedido).add(acrescimoPedido).setScale(2,RoundingMode.HALF_EVEN));
				ped.setPedidoStatus(PedidoStatus.REC);
				ped.setCaixa(gt.getCaixa());
				ped.setHoraRecebimento(LocalTime.now());
				ped.setDataRecebimento(LocalDate.now());
				
			}
			// calculo da diferenï¿½a no desconto e no acrescimo
			BigDecimal fracao = new BigDecimal("0");
			BigDecimal totalDesconto = new BigDecimal("0");
			BigDecimal totalAcrescimo = new BigDecimal("0");
			for (Pedido pedTemp : lista) {
				totalDesconto = totalDesconto.add(pedTemp.getDesconto());
				totalAcrescimo = totalAcrescimo.add(pedTemp.getOutrasDespesas());
			}
			fracao = totalDesconto.subtract(gt.getDesconto());
			acrescimoFracaoPedido = totalAcrescimo.subtract(gt.getAcrescimo());
			if (acrescimoFracaoPedido.compareTo(new BigDecimal("0"))>0) {
				lista.get(0).setOutrasDespesas((lista.get(0).getOutrasDespesas().add(acrescimoFracaoPedido,mc)).setScale(2,RoundingMode.HALF_EVEN));
//				this.pedidoDao.save(lista.get(0));
			}
			if (fracao.compareTo(new BigDecimal("0"))>0) {
				lista.get(0).setDesconto((lista.get(0).getDesconto().add(fracao,mc)).setScale(2,RoundingMode.HALF_EVEN));
			}
			for (Pedido ped : lista) {
				this.pedidoDao.save(ped);
			}
		}else {
			if (quantPedidos > 20 ) {
				throw new CaixaException(translate("caixa.limit.pedidos"));
			}
			throw new CaixaException(translate("caixa.caixa.list.isEmpty"));
		}
	}
	
	@Transactional
	public List<RecebimentoParcial> geraRecebimentoParcialeParcelas()  {
		List<RecebimentoParcial> listaTempRecParcial = new ArrayList<RecebimentoParcial>();
		System.out.println("Dentro do geraRecebimentoParcialParcelas");
		
		for(FormaDePagamento pagamento : this.listaPagamentos) {
			if (this.hashRecebimentoParcialCaixa.containsKey(pagamento)) {
				System.out.println("preenchendo o recParcial " + pagamento.getDescricao() + " valor: " + this.hashRecebimentoParcialCaixa.get(pagamento));
				RecebimentoParcial recParcial  = new RecebimentoParcial();
				recParcial.setFormaPagamento(pagamento);
				recParcial.setValorRecebido(this.hashRecebimentoParcialCaixa.get(pagamento));
				recParcial.setLivroCaixa(MovimentoEnum.Rec);
				recParcial.setTipoPagamento(pagamento.getTipoPagamento());
				if (pagamento.getTipoPagamento().equals(TipoPagamento.Din) && this.troco.compareTo(new BigDecimal("0"))>0) {
					recParcial.setTroco(this.troco);
				}
				recParcial.setCaixa(this.caixa);
				System.out.println("geraRecebimentoParcialParcelas antes do save");
				recParcial = recebimentoDao.save(recParcial);
				System.out.println("geraRecebimentoParcialParcelas depois do save  ID : " + recParcial.getId() );
				listaTempRecParcial.add(recParcial);
			}
		}
		
		return listaTempRecParcial;
	}
	
	@Transactional
	public List<ParcelasNfe> geraListaParcelaNfePreenchinda(RecebimentoParcial recParcial) throws FinanceiroException{
		try {
		List<ParcelasNfe> listaTempAddParcelaToRecebimentoParcial = new ArrayList<ParcelasNfe>();
		for(Iterator<ParcelasNfe> parc = this.listaParcelasPagamento.iterator();parc.hasNext();) {
			ParcelasNfe parcela = parc.next();
			if (parcela.getFormaPag().equals(recParcial.getFormaPagamento())) {
				System.out.println("Adicionando Parcela no recParcial");
				if (parcela.getFormaPag().isIntegraFinanceiro()) {
					if (parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Din) || 
							parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Pix) ||
							parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Dbc) ||
							parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Tbc)) {
								parcela.setStatus(ParcelaStatus.REC);
								parcela.setDataRecebimento(LocalDate.now());
								parcela.setValorRecebido(parcela.getValorParcela().add(parcela.getValorRecebido()));
								parcela.setValorOriginal(parcela.getValorParcela());
								parcela.setValorParcela(parcela.getValorParcela().subtract(parcela.getValorRecebido()));
								parcela.setAgPedido(this.agrupado);
						}else {
								parcela.setStatus(ParcelaStatus.ABE);
						}
					parcela.setFinanceiro(true);
					parcela.setConta(parcela.getFormaPag().getContaCorrente());
				}else {
					parcela.setStatus(ParcelaStatus.NAO);
					parcela.setFinanceiro(false);
				}
				parcela.setQRecorrencia(this.listaParcelasPagamento.size());
				parcela.setTipoPagamento(parcela.getFormaPag().getTipoPagamento());
				parcela.setTipoLancamento(TipoLancamento.tpCredito);
				parcela.setRecebimentoParcial(recParcial);
				parcela = parcelaDao.save(parcela);
				listaTempAddParcelaToRecebimentoParcial.add(parcela);
			}
		}
			return listaTempAddParcelaToRecebimentoParcial;
		}catch (Exception e) {
			throw new FinanceiroException(this.translate("Não foi possivel gerar o financeiro - " )+ e.getMessage());
		}
		
	}
	
	public void removePagamento(FormaDePagamento forma) {
		removeRecebimentoParcial(forma);
	}
	
	/**
	 * Método que gera as parcelas conforme a formaDePagamento Informado
	 * obs: o campo Controle da ParcelasNfe não é preenchido!
	 * @param forma
	 * @param valor
	 * @return List<ParcelasNfe>
	 */
	@Transactional
	public List<ParcelasNfe> preencheParcelamento(FormaDePagamento forma,BigDecimal valor,BigDecimal troco){
		try{
//			nova formula para calculo dos vencimentos 
// 			d1 d2 d2+(dn*(np*2)) ou criar uma lista com as parcelas e informar a quantidade de dias para cada vencimento
//			de qualquer forma será necessário que o cliente informe os campos em FORMA DE PAGAMENTO
			List<ParcelasNfe> listaParcelamento = new ArrayList<ParcelasNfe>();
			LocalDate hoje = LocalDate.now();
			MathContext precisao = new MathContext(20, RoundingMode.HALF_UP);
			BigDecimal somaDasParcelas = new BigDecimal("0");
			BigDecimal resultado = new BigDecimal("0");
			BigDecimal valorCadaParcela = new BigDecimal("0");
			BigDecimal valorEntrada = new BigDecimal("0");
			BigDecimal total = new BigDecimal("0");
			ParcelasNfe parcelaTemporaria = new ParcelasNfe();
			String parc= ""+forma.getParcelas();
			System.out.println("quantidade parcelas " + forma.getParcelas());
			total = new BigDecimal(valor.intValue());
			// só irá fazer os cálculos caso  valor Total da nota for maior que 0 e numero de parcelas tambem maior que zero
			if ((valor.compareTo(new BigDecimal("0")) > 0  && forma.getParcelas() > 0) || (forma.getTipoPagamento().equals(TipoPagamento.Spg))){
				if (forma.getTipoPagamento().equals(TipoPagamento.Spg)){
					parcelaTemporaria.setNumParcela(1L);
//					parcelaTemporaria.setControle(this.nfe.getNumeroNota());
					parcelaTemporaria.setValorParcela(valor);
					parcelaTemporaria.setVencimento(hoje);
					parcelaTemporaria.setFormaPag(forma);
					parcelaTemporaria.setAgPedido(this.agrupado);
					listaParcelamento.add(parcelaTemporaria);
				}else{
					if (forma.getTipoPagamento().equals(TipoPagamento.Din) && troco.compareTo(new BigDecimal("0"))> 0) {
						parcelaTemporaria.setNumParcela(this.numParcela);
//						parcelaTemporaria.setControle(this.nfe.getNumeroNota());
						parcelaTemporaria.setValorParcela(valor.subtract(troco));
						parcelaTemporaria.setVencimento(hoje);
						parcelaTemporaria.setFormaPag(forma);
						parcelaTemporaria.setAgPedido(this.agrupado);
						listaParcelamento.add(parcelaTemporaria);
					}else {
						if (forma.isEntrada()) {
							BigDecimal porcentagemEntrada = new BigDecimal("100").subtract(new BigDecimal(forma.getValorEntrada().toString())).divide(new BigDecimal("100"),precisao).setScale(2, RoundingMode.HALF_UP);
							valorEntrada = total.subtract(total.multiply(new BigDecimal(porcentagemEntrada.toString()),precisao)).setScale(2, RoundingMode.HALF_UP);
							System.out.println("entrada: " + valorEntrada);
							total = total.subtract(valorEntrada);
							parc = new BigDecimal(parc).subtract(new BigDecimal(1)).toString();
							parcelaTemporaria.setNumParcela(this.numParcela);
							parcelaTemporaria.setValorParcela(valorEntrada);
							parcelaTemporaria.setVencimento(hoje);
							parcelaTemporaria.setFormaPag(forma);
							parcelaTemporaria.setAgPedido(this.agrupado);
							listaParcelamento.add(parcelaTemporaria);
							parcelaTemporaria = new ParcelasNfe();
							this.numParcela++;
						}
						valorCadaParcela = total.divide(new BigDecimal(parc),precisao).setScale(2, RoundingMode.HALF_UP);
						System.out.println("valor de cada parcela com arredondamento para baixo 2 casas decimais" + valorCadaParcela);
						somaDasParcelas = valorCadaParcela.multiply(new BigDecimal(parc)).add(valorEntrada);
						System.out.println("soma das parcelas = " + somaDasParcelas);
						resultado = valor.subtract(somaDasParcelas);
						System.out.println("valor da nota - soma das parcelas = " + resultado);
						
						for (int i = 0 ; i < new BigDecimal(parc).intValue() ; i++){
							parcelaTemporaria.setFormaPag(forma);
							parcelaTemporaria.setValorParcela(valorCadaParcela);
							if ( i == 0 ){
								System.out.println("Estou no parcela temporaria i = 0 ");
								parcelaTemporaria.setVencimento(hoje.plusDays(forma.getIntervalo()+forma.getCarencia()));
							}else{
								System.out.println("estou no parcela Temporaria onde i = " + i);
								parcelaTemporaria.setVencimento(hoje.plusDays((forma.getIntervalo()* (i+1))+forma.getCarencia()));
							}
							parcelaTemporaria.setNumParcela(this.numParcela);
							parcelaTemporaria.setAgPedido(this.agrupado);
							listaParcelamento.add(parcelaTemporaria);
							parcelaTemporaria = new ParcelasNfe();
							this.numParcela++;
						}
						if ((valor.compareTo(somaDasParcelas) < 0) || (valor.compareTo(somaDasParcelas) > 0)){
							listaParcelamento.get(0).setValorParcela(valorCadaParcela.add(resultado));
							System.out.println("Valor da primeira parcela " + listaParcelamento.get(0).getValorParcela().toString());
						}
						//					this.nfe.setFormaPagamento(this.formaPag);
						for (ParcelasNfe parcelasNfe : listaParcelamento) {
							System.out.println("Estou dentro do foreach listaParcelamentos");
							System.out.println(parcelasNfe.getVencimento());
							System.out.println(parcelasNfe.getValorParcela());
							System.out.println(parcelasNfe.getFormaPag().getTipoPagamento().name());
						}
					}
				}
			}

			return listaParcelamento;
		}catch (HibernateException h){
			this.addError(true, "Nï¿½o foi possivel apagar as parcelas", h.getCause());
			return new ArrayList<ParcelasNfe>();
		}catch (Exception e) {
			this.addError(true, "Nï¿½o sei qual foi a causa do erro", e.getCause());
			return new ArrayList<ParcelasNfe>();
		}
		
	}
	@Transactional
	public String  creditoUtilizado() throws HibernateException, CaixaException, Exception {
		boolean inseriuCredito = false;
		String resultado = "";
		if ( this.credito.getSaldoCreditoDevolucao().compareTo(new BigDecimal("0"))>0) {
			for (Entry<FormaDePagamento, BigDecimal> paga : listaFormaPagamentoInserido) {
				if (paga.getKey().getTipoPagamento().equals(TipoPagamento.Crl)) {
					inseriuCredito= true;
				}
			}
		}else {
			inseriuCredito =  true;
		}
		if (inseriuCredito == false) {
			this.updateAndOpenDialog("dlg3id","dlg3");
		}else {
			resultado =  salvaEImprimi();
		}
		return resultado;
	}
	
	@Transactional
	public String salvaEImprimi() {
		try {
			String retorna = gravaRecebimentoCaixa();
			if (retorna != null) {
				if (imprimeCupomVenda()) {
					for (int i = 0 ; i< this.configUser.getQuantViaVenda().intValue(); i++) {
						this.pedido = this.agrupado.getListaPedidosRecebidos().get(0); // isso sÃ³ Ã© possivel porque no modo frente de caixa nÃ£o permite agrupamento de pedidos!
						impressora.imprimirCupomPdv(this.pedido,this.configUser.isCabecalhoPDV(),this.getUsuarioAutenticado().getConfig());
					}
					retorna =  newPedido();
				}else {
					retorna =  toListRecebimento();
				}
			}
			return retorna;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	@Transactional
	public String gravaRecebimentoCaixa() {
		try {
			if(this.agrupado != null) {
				if (this.resto.compareTo(new BigDecimal("0"))==0) {
					this.agrupado.setStatus(PedidoStatus.REC);
					this.agrupado.setDataRec(LocalDate.now());
					this.agrupado.setHoraRec(LocalTime.now());
					this.agrupado.setCaixa(this.caixa);
					preencheListasPedidosERateiaAcrescimoDescontoNosItens(this.listaPedidosSelecionados,this.agrupado);
					// gerando lista de RecebimentoParcial e persistindo no banco!
					List<RecebimentoParcial> listParcialTemp = new ArrayList<RecebimentoParcial>();
					if (this.cfe == null) {
						listParcialTemp = geraRecebimentoParcialeParcelas(); // ja grava no banco o recebimento
					}else {
						listParcialTemp = this.cfe.getListaRecebimentosAgrupados();
					}
					if (listParcialTemp.size() > 0 ) {
						for (RecebimentoParcial recebimentoParcial : listParcialTemp) {
								recebimentoParcial.setAgrupado(this.agrupado);
								recebimentoParcial= this.recebimentoDao.save(recebimentoParcial);
							}
					}
					// gerando lista de Parcelas de pagamento e persistindo no banco!
					List<ParcelasNfe> listaParcelaTemp = new ArrayList<ParcelasNfe>();
					for (RecebimentoParcial rec : listParcialTemp) {
						listaParcelaTemp.addAll(geraListaParcelaNfePreenchinda(rec)); //jï¿½ salva no banco a parcela
					}
					String controleID ="PedidoGT : ";
					for (Pedido ped : this.agrupado.getListaPedidosRecebidos()) {
						controleID = controleID + ped.getControle().getId()+" - ";
					} 
					for (ParcelasNfe parcelasNfe : listaParcelaTemp) {
						parcelasNfe.setAgPedido(this.agrupado);
						parcelasNfe.setDescricao(controleID +this.agrupado.getDestinatario().nome() +" - Valor do PedidoGT R$ " + this.agrupado.getValorTotal().setScale(2,RoundingMode.HALF_DOWN));
						parcelasNfe = parcelaDao.save(parcelasNfe);
					}	
					this.caixa = caixaUtil.preencheSaldoCaixa(this.caixa, MovimentoEnum.Rec, listParcialTemp);
					this.caixaDao.save(this.caixa); // atualiza o saldo do caixa
					this.agrupado.setListRecebimentoParcial(listParcialTemp);
					this.agrupado.setListaParcelas(listaParcelaTemp);
					this.agrupado.setValorRecebido(this.totalGT);
					this.creditoDao.save(this.credito);
					this.agrupado = this.agrupadoDao.save(this.agrupado);
				}else {
					throw new CaixaException(translate("caixa.rec.valorAberto"));
				}
			}
			return  "OK";
		}catch (CaixaException c) {
			this.addError(true,"caixa.error", c.getMessage());
			return null;
		}catch (FinanceiroException f) {
			this.addError(true,"caixa.error", f.getMessage());
			return null;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
		
	}
	
	public String newPedido() {
		return "/main/Vendas/pdv/formPDV.xhtml?faces-redirect=true";
	}
	
	public Credito geraCreditoDevolucao(Pedido pedido) throws DevolucaoException {
		Credito credito = new Credito();
		if (pedido.getDestino().informaClassePreenchida().equalsIgnoreCase("cliente")) {
				credito = this.creditoDao.retornaCredito(pedido.getDestino().getCliente(), null, null,pegaIdEmpresa(),pegaIdFilial());
				credito.setCliente(pedido.getDestino().getCliente());
		}else {
			if (pedido.getDestino().informaClassePreenchida().equalsIgnoreCase("fornecedor")) {
				credito = this.creditoDao.retornaCredito(null, pedido.getDestino().getFornecedor(), null, pegaIdEmpresa(), pegaIdFilial());
				credito.setFornecedor(pedido.getDestino().getFornecedor());
			}else {
				if (pedido.getDestino().informaClassePreenchida().equalsIgnoreCase("colaborador")) {
					credito = this.creditoDao.retornaCredito(null, null, pedido.getDestino().getColaborador(),pegaIdEmpresa(), pegaIdFilial());
					credito.setColaborador(pedido.getDestino().getColaborador());
				}else {
					throw new DevolucaoException(this.translate("devolucao.error.customer.not.informed"));
				}
			}
		}
		credito.setSaldoCreditoDevolucao(credito.getSaldoCreditoDevolucao().add(pedido.getValorTotalPedido()));
		return credito;
		
	}
	
	@Transactional
	public void confirmaDevolucao() throws DevolucaoException {
//		try {
			if (this.pedido.getId() != null) {
				if (this.pedido.getPedidoTipo().equals(PedidoTipo.DEV)) {
//					if(this.pedido.)
					this.pedido.setPedidoStatus(PedidoStatus.REC);
					this.pedido.setDataRecebimento(LocalDate.now());
					this.pedido.setHoraRecebimento(LocalTime.now());
					this.pedido.setCaixa(this.caixa);
//					geraCreditoDevolucao(this.pedido);
					this.credito = geraCreditoDevolucao(this.pedido);
					this.creditoDao.save(this.credito);
					this.pedidoDao.save(this.pedido);
				}
			}
//		}catch (HibernateException h) {
//			this.addError(true,"exception.error.fatal",h.getMessage());
//		}catch (Exception e) {
//			this.addError(true,"exception.error.fatal",e.getMessage());
//		}
	}
	public EmitenteCFe preencheEmitente(){
		EmitenteCFe emissor = new EmitenteCFe();
		if (this.empresaUsuario.getFil() == null){
			emissor.setEmpresa(this.empresaUsuario.getEmp());
		}else{
			emissor.setFilial(this.empresaUsuario.getFil());
		}
		return emissor;
	}
	
	public DestinatarioCFe preencheDestinatario() {
		DestinatarioCFe destino = new DestinatarioCFe();
		if (this.agrupado.getDestinatario() != null) {
			switch (this.agrupado.getDestinatario().informaClassePreenchida()) {
			case "cliente":
				if (this.agrupado.getDestinatario().getCliente().getCnpj()== "" || this.agrupado.getDestinatario().getCliente().getCnpj() == null ) {
					destino.setCpf(this.agrupado.getDestinatario().getCliente().getCpf());
				}else {
					destino.setCnpj(this.agrupado.getDestinatario().getCliente().getCnpj());
				}
				destino.setNome(this.agrupado.getDestinatario().getCliente().getRazaoSocial());
				
				break;
			case "fornecedor":
				if (this.agrupado.getDestinatario().getFornecedor().getCnpj()== "" || this.agrupado.getDestinatario().getFornecedor().getCnpj() == null ) {
					destino.setCpf(this.agrupado.getDestinatario().getFornecedor().getCpf());
				}else {
					destino.setCnpj(this.agrupado.getDestinatario().getFornecedor().getCnpj());
				}
				destino.setNome(this.agrupado.getDestinatario().getFornecedor().getRazaoSocial());
				break;
			case "colaborador":
					destino.setCpf(this.agrupado.getDestinatario().getColaborador().getCpf());
					destino.setNome(this.agrupado.getDestinatario().getColaborador().getNome());
				break;
			default:
				break;
			} 
		}else {
			
		}
		return destino;
	}
	
	public boolean detinoNull() {
		boolean resultado = false;
		
		return resultado;
	}
	
	@Transactional
	public String  informadoCPF() {
		this.satEmitido = geraCupomCPFInformado(true);
		this.closeDialog("dlgCPFCFe");
		if (this.satEmitido) {
			if (imprimeCupomVenda()) {
				return newPedido();
			}else {
				return toListRecebimento();
			}
		}else {
			return null;
		}
	}
	
	@Transactional
	public boolean geraCupomCPFInformado(boolean emite) {
		try {
		if (emite) {
			preencheListasPedidosERateiaAcrescimoDescontoNosItens(this.listaPedidosSelecionados,this.agrupado);
			boolean cupomSatEmitido = doEmitir();
			if(cupomSatEmitido) {
				for (int i = 0; this.listaPedidosSelecionados.size()>i ; i++) {
					this.listaPedidosSelecionados.get(i).setFiscalStatus(FiscalStatus.ES);
				}
				gravaRecebimentoCaixa();
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage() );
			this.addError(true, "rel: ", e.getCause());
			return false;
		}
	}
	
	@Transactional
	public String geraCFe() {
		try {
			if (this.resto.compareTo(new BigDecimal("0"))==0) {
				// verificando se NFCe ativado
				try {
					if (AbstractBeanEmpDS.<Boolean>campoEmpUser(this.empresaUsuario,Filial::isNFCeAtivo,Empresa::isNFCeAtivo ).booleanValue()) {
						
					}else {
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// inicializando o CFe
				this.cfe = new CFe();
				// preenchendo o destinatario
				if (this.agrupado.getDestinatario().informaClassePreenchida().equalsIgnoreCase("null")== false) {
					this.satEmitido = geraCupomCPFInformado(true);
				}else {
					this.openDialog("dlgCPFCFe");

					System.out.println("Sai do UpdateAndOpenDialog");
				}
				if (this.satEmitido) {
					if (imprimeCupomVenda()) {
						return newPedido();
					}else {
						return toListRecebimento();
					}
				}else {
					return null;
				}
			}else {
				throw new CaixaException(translate("caixa.rec.valorAberto"));
			}

		}catch (CaixaException c) {
			this.addError(true,"caixa.error", c.getMessage());
			return null;
		}catch (Exception e) {
			// TODO: handle exception
			this.addError(true, "exception.error.fatal", e.getMessage() );
			this.addError(true, "rel: ", e.getCause());
			return null;
		}
	}
	
	public String pegaVersaoSat() {
		 if (this.empresaUsuario.getFil() != null) {
			 if (this.empresaUsuario.getFil().getVersaoSat() == null) {
				 return ApplicationUtils.getConfiguration("versao.sat");
			 }else {
				 return this.empresaUsuario.getFil().getVersaoSat().toString();
			 }
		 }else {
			 if (this.empresaUsuario.getEmp().getVersaoSat() == null) {
				 return ApplicationUtils.getConfiguration("versao.sat");
			 }else {
				 return this.empresaUsuario.getEmp().getVersaoSat().toString();
			 }
		 }
	}

	@Transactional
	public boolean doEmitir() {
		try {
			boolean nfceAtivado = AbstractBeanEmpDS.<Boolean>campoEmpUser(this.empresaUsuario,Filial::isNFCeAtivo,Empresa::isNFCeAtivo ).booleanValue();
			String nomeArquivo = "cxsat" + removerAcentos(this.getUsuarioAutenticado().getName()+this.getUsuarioAutenticado().getIdEmpresa()).replace(" ", "").trim();
			String respostaAcbrLocal = "";
			List<ItemCFe> listaItemTemp = new ArrayList<ItemCFe>();
			this.cfe.setListaItem(listaItensCFePreenchida(this.agrupadoDao.preencheAgPedidoComItensPedido(this.agrupado.getId()).getListaItensAgrupados()));
			this.cfe.setListaRecebimentosAgrupados(geraRecebimentoParcialeParcelas());
			EmitenteCFe emitente = preencheEmitente();
			//			emitente = this.emitenteDao.save(emitente);
			if (this.documento != null) {
				preencheDestinatario(this.documento);
			}else {
				this.destino = preencheDestinatario();
			}
			//			this.destino = this.destinoDao.save(this.destino);

			this.cfe.setEmitente(emitente);
			this.cfe.setDestinatario(this.destino);

			if (this.cfe.getListaItem().size() > 0) {
				listaItemTemp = calculaTributos.preencheListaDeItensCfe(this.cfe.getListaItem(), this.cfe,pegaIdEmpresa(),pegaIdFilial());
			}else {
				throw new TributosException(this.translate("tributosException.listaEmpty"));
			}
			if (listaItemTemp.size() > 0) {
				this.cfe.setListaItem(listaItemTemp);
				for (ItemCFe itemCFe : listaItemTemp) {
					this.cfe.setValorTotalTributos(this.cfe.getValorTotalTributos().add(itemCFe.getValorTotalTributoItem()));
				}
			}else {
				throw new TributosException(this.translate("tributosException.listaEmpty"));
			}

			//		this.cfe.setValorTotalProdutos(this.totalCFe);
			this.cfe = calculaTributos.calculaTotaisCFe(this.cfe);
			this.cfe.setDesconto(this.agrupado.getDesconto());
			String retornoAcbr = "";
			if (nfceAtivado) { // criar o arquivo de envio NFCE
				acbr.criarArqIniMaqRemota(pegaConexao(), nomeArquivo, nfce, FinalidadeNfe.NO, true);
				// criarArqIniMaqRemota é o que cria a NFe que tambem será usado para o NFC-e 
				// necessario alterar  metodo para a reforma tributaria
//				acbr.criarArqIniMaqRemota(pegaConexao(), nomeArquivo, this.cfe,pegaVersaoSat());
//				retornoAcbr = acbr.satCriarEnviarCFe(pegaConexao(), nomeArquivo);
			}else {
				acbr.criarArqIniSatCaixaMaqRemota(pegaConexao(), nomeArquivo, this.cfe,pegaVersaoSat());
				retornoAcbr = acbr.satCriarEnviarCFe(pegaConexao(), nomeArquivo);
			}
			SatResposta satResposta = validaRetornoCFe(retornoAcbr); 
			System.out.println("Valido: " + satResposta.isValido() + "\n Código:" + satResposta.codigoRetorno);
			if (satResposta.isValido()) {
				this.cfe.setCaminho(satResposta.getPatch());
				this.cfe.setStatusEmissao(StatusNfe.EN);
				this.cfe.setNumeroNota(satResposta.getNumero());
				this.cfe.setEmitido(true);
				System.out.println("Campos CFE preenchidos caminho:" + this.cfe.getCaminho() + " Chave de acesso:"
						+ this.cfe.getNumeroNota() + " Emitido:" + this.cfe.getStatusEmissao() + " emitido:" + this.cfe.isEmitido());
				acbr.geraPDFExtratoVenda(pegaConexao(), this.cfe.getCaminho(), this.cfe.getNumeroNota().trim() + ".pdf");
				acbr.satImprimiExtratoVenda(pegaConexao(), this.cfe.getCaminho());
				this.addInfo(true, satResposta.motivo);
				this.destino.setCfe(this.cfe);
				//				this.destino = this.destinoDao.save(this.destino);

				//				this.cfe = this.cfeDao.save(this.cfe);

				if (this.cfe.getId() != null) {
					//			System.out.println("IBR - salvando os itens da cfe");
					for (ItemCFe item : listaItemTemp) {
						item.setCfe(this.cfe);
						//						this.itemCfeDao.save(item);
					}
				}
				this.cfe.setListaItem(listaItemTemp);
				this.cfe = this.cfeDao.save(this.cfe);

				return true;
			}else {
				this.addError(true, satResposta.motivo);
				return false;
			}
			//			}else {
			//				this.addError(true, "caixa.error.save");
			//				return false;
			//			}

		}catch (TributosException t){
			this.addError(true,"exception.error.fatal",t.getMessage());
			return false;
		}catch (TotaisCFeException tc) {
			this.addError(true,tc.getMessage());
			return false;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage() );
			this.addError(true, "rel: ", e.getCause());
			return false;
		}

	}
	
	public void testaConversaoLista() throws TributosException {
		listaItensCFePreenchida(this.agrupadoDao.preencheAgPedidoComItensPedido(this.agrupado.getId()).getListaItensAgrupados());
	}
	
	public List<ItemCFe> listaItensCFePreenchida(List<ItemPedido> listaPedidos) throws TributosException{
//	public void listaItensCFePreenchida(List<ItemPedido> listaPedidos) throws TributosException{
		// gerando a lista de itens
				List<ItemCFe> listaItensCFe = new ArrayList<ItemCFe>();
				for (Item itemPedido : listaPedidos) {
					ItemCFe item = new ItemCFe();
					item.setProduto(itemPedido.getProduto());
					item.setQuantidade(itemPedido.getQuantidade());
					item.setValorTotal(itemPedido.getValorTotal());
					item.setValorTotalBruto(itemPedido.getValorTotalBruto());
					item.setValorUnitario(itemPedido.getValorUnitario());
					item.setDesconto(itemPedido.getDesconto());
					item.setValorDespesas(itemPedido.getValorDespesas());
					item.setPorcentagem(itemPedido.isPorcentagem());
					item.setRef(itemPedido.getRef());
					listaItensCFe.add(item);
				}
			return listaItensCFe;
	}
	
	public void preencheDestinatario(String doc) {
		int i = 0;
		i = CpfCnpjUtils.isCpfOrCnpjOrNull(doc);
		boolean valido = CpfCnpjUtils.isValid(doc);
		if (i == 1 && valido ) {
			this.destino.setCnpj(doc);
		}else if (i == 2 && valido) {
				this.destino.setCpf(doc);
		}else {
			this.destino.setCnpj("");
			this.destino.setCpf("");
		}
		
	}
	
	public SatResposta validaRetornoCFe(String resposta) {
		SatResposta satResposta = new SatResposta();
		String resultadoUp = resposta.toUpperCase();
		LocalizaRegex localiza = new LocalizaRegex();
		if (localiza.localizaPalavra(resultadoUp, "CODIGODERETORNO")) {
			int inicio = 0;
			int fim = 0;
			inicio = resultadoUp.indexOf("CODIGODERETORNO");
			fim = resultadoUp.indexOf("NUMEROSESSAO");
			String codigoRetorno = resultadoUp.substring(inicio+16,fim);
			System.out.println("Cï¿½digo: " + codigoRetorno);
			switch (codigoRetorno.trim()) {
			case "6000":
				inicio = resultadoUp.indexOf("ARQUIVO=");
				fim = resultadoUp.indexOf("CODIGODERETORNO");
				String patch= resultadoUp.substring(inicio+8, fim);
				System.out.println("Tamanho: "+patch.length() + " resultado: " + new BigDecimal(patch.length()).subtract(new BigDecimal("50")).intValue());
				String numero = patch.substring(new BigDecimal(patch.length()).subtract(new BigDecimal("50")).intValue(),new BigDecimal(patch.length()).subtract(new BigDecimal("6")).intValue());
				System.out.println("Patch: " + patch + "\n" + "Chave:  " + numero);
				satResposta.setValido(true);
				satResposta.setMotivo("Emitido com sucesso");
				satResposta.setCodigoRetorno(codigoRetorno);
				satResposta.setPatch(patch);
				satResposta.setNumero(numero);
				break;
			case "6001":
				satResposta.setValido(false);
				satResposta.setMotivo("Codigo de ativaÃ§Ã£o invÃ¡lido");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6002":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT ainda nÃ£o ativado.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6003":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT nÃ£o vinculado ao AC");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6004":
				satResposta.setValido(false);
				satResposta.setMotivo("VinculaÃ§Ã£o do AC nÃ£o confere.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6005":
				satResposta.setValido(false);
				satResposta.setMotivo("Tamanho do CF-e-SAT superior a 1.500KB");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6006":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT bloqueado pelo contribuinte");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6007":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT bloqueado pela SEFAZ");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6008":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT bloqueado por falta de comunicaÃ§Ã£o");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6009":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT bloqueado, cÃ³digo de ativaÃ§Ã£o incorreto");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6010":
				satResposta.setValido(false);
				satResposta.setMotivo("Erro de validaÃ§Ã£o do conteÃºdo.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6011":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT bloqueado por vencimento do certificado digital.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6097":
				satResposta.setValido(false);
				satResposta.setMotivo("NÃºmero de sessÃ£o invÃ¡lido");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6098":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT em processamento. Tente novamente.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6099":
				satResposta.setValido(false);
				satResposta.setMotivo("Erro desconhecido na emissÃ£o.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			default:
				satResposta.setValido(false);
				satResposta.setMotivo("Motivo desconhecido");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;

			}
			return satResposta;
		}else {
			satResposta.setValido(false);
			satResposta.setCodigoRetorno("erro");
			satResposta.setMotivo("Codigo de Retorno nao encontrado");
			return satResposta;
		}
	}
	
	public static class SatResposta{

		@Getter
		@Setter
		String motivo = "";

		@Getter
		@Setter
		String codigoRetorno="";

		@Getter
		@Setter
		boolean valido = false;

		@Getter
		@Setter
		String patch = "";

		@Getter
		@Setter
		String numero = "";
	}
	
	
	//------------------------------------------------------------------------------------------
	//					Transferencia
	//------------------------------------------------------------------------------------------
	
	public Long idDestinatario(Pedido ped) {
		DestinatarioTransferencia emitente = new DestinatarioTransferencia();
		emitente = ped.getDestinoTransferencia();
		Long resposta = null;
		if (emitente.getFilial() != null) {
			resposta = emitente.getFilial().getId();
		}
		return resposta;
	}
	
	public Long idFilialEmitente(Pedido ped) {
		EmitenteVenda emitente = new EmitenteVenda();
		emitente = ped.getEmitente();
		Long resposta = null;
		if (emitente.getFilial() != null) {
			resposta = emitente.getFilial().getId();
		}
		return resposta;
	}
	
	@Transactional
	public void confirmaRecebimentoTransferencia() {
	    if (this.pedido.getId() == null) {
	        throw new RuntimeException(this.translate("estoqueException.transferencia.null"));
	    }

	    try {
	        this.pedido = this.pedidoDao.pegaTransferenciaPorId(this.pedido.getId());

	        if (this.empresaUsuario.getEmp().isTranferAutomatico()) {
	            for (ItemPedido itemPedido : this.pedido.getListaItensPedido()) {
	                Produto itemTemp = this.produtoDao.pegaProdutoID(
	                    itemPedido.getProduto().getId(), pegaIdEmpresa(), pegaIdFilial());

	                if (itemTemp == null) continue;
	                
	                	ProdutoCusto custoOrigem = estoqueUtil.pegaCustoOrigemTransferencia(
	                			itemTemp, pegaIdEmpresa(), idFilialEmitente(this.pedido));

	                	ProdutoCusto custoDestino = estoqueUtil.pegaCustoDestinoTransferencia(
	                			itemTemp, pegaIdEmpresa(), idDestinatario(this.pedido));
	                	
	                	System.out.println("pegaCustoDestinoTransferencia");
                		System.out.println("Custo: " + custoDestino.getCusto());
                		System.out.println("preco " + custoDestino.getPreco1());
                		System.out.println("C.Antigo: "+ custoDestino.getCustoAnterior());
                		System.out.println("C.Atual: "+ custoDestino.getCusto());
	                	System.out.println("id filial " + idDestinatario(this.pedido));
	                	System.out.println("-----------------------------");
	                	
	                	ProdutoCusto custoAtualizado = new ProdutoCusto();
	                	if (empresaUsuario.getEmp().isTranferPreco()) {
	                		custoAtualizado = estoqueUtil.atualizaCustoComPreco(custoDestino, custoOrigem,itemTemp);
	                		System.out.println("Estou detro do transfere preço");
	                	}else {
	                		custoAtualizado = estoqueUtil.atualizaCustoSemPreco(custoDestino, custoOrigem,itemTemp);
	                	}
	                	System.out.println("Antes do CalculaCustoMedio custoAtualizado");
                		System.out.println("Custo: " + custoAtualizado.getCusto());
                		System.out.println("preco " + custoAtualizado.getPreco1());
                		System.out.println("C.Antigo: "+ custoAtualizado.getCustoAnterior());
                		System.out.println("C.Atual: "+ custoAtualizado.getCusto());
                		custoAtualizado.setIdEmpresa(pegaIdEmpresa());
                		custoAtualizado.setIdFilial(idDestinatario(this.pedido));
                		
	                	if (calcularCustoMedio(pegaIdEmpresa(),idDestinatario(this.pedido))) {
	                		ProdutoCusto CustoMedio = estoqueUtil.calculaCustoMedioTransferencia(
	                				itemPedido, custoAtualizado.getCusto(), custoAtualizado,pegaIdEmpresa() ,idDestinatario(this.pedido),verificaEmpCustoMedio()
	                				);
	                		System.out.println("Dentro do CalculaCustoMedio");
	                		System.out.println("Custo: " + CustoMedio.getCusto());
	                		System.out.println("preco " + CustoMedio.getPreco1());
	                		System.out.println("C.Antigo: "+ CustoMedio.getCustoAnterior());
	                		System.out.println("C.Atual: "+ CustoMedio.getCusto());
	                		System.out.println("C.Atual: "+ CustoMedio.getCustoMedio());
	                		this.custoDao.save(CustoMedio);
	                	}else {
	                		this.custoDao.save(custoAtualizado);
	                	}

	                atualizaEstoqueTransferenciaItem(itemPedido);
	            }

	            this.pedido.setTransferenciaConcluida(true);
	        }

	        this.pedido.setPedidoStatus(PedidoStatus.REC);
	        this.pedido.setDataRecebimento(LocalDate.now());
	        this.pedido.setHoraRecebimento(LocalTime.now());
	        this.pedido.setCaixa(this.caixa);

	        this.pedidoDao.save(this.pedido);
	        this.addInfo(true, "transfer.sucess");
	    
	    }catch (EstoqueRuntimeException er) {
	    	this.addError(true, "caixa.error", er.getMessage());
	    } catch (EstoqueException ex) {
	    	// nada a fazer ja é feito automatico o rollback e a exibiçao de mensagem.
	    	 this.addError(true, "caixa.error", ex.getMessage());
	    } catch (HibernateException e) {
	        this.addError(true, "caixa.error", e.getMessage());
	    }
	}
	
	public boolean calcularCustoMedio(Long empr,Long fili) {
		boolean calcularCustoMedioTemp = false;
		if (fili != null) {
				
			if (this.filDao.findById(fili, false).isGerarCustoMedio()) {
				calcularCustoMedioTemp= true;
			}
		}else {
			if (this.empDao.findById(empr, false).isGerarCustoMedio()) {
				calcularCustoMedioTemp = true;
			}
		}
		return calcularCustoMedioTemp;
	}
	
	@RollbackOn({EstoqueException.class,HibernateException.class})
	public void atualizaEstoqueTransferenciaItem(ItemPedido itemEstoque)throws EstoqueException,HibernateException  {
			Estoque estoqueTemp = new Estoque();
				// atualizando o estoque dos produtos
				estoqueTemp = this.estoqueUtil.preencheEstoqueItem(itemEstoque, pegaIdEmpresa(), idDestinatario(this.pedido));
				estoqueTemp = this.estoqueUtil.acrescentaEstoqueRecebimentoMaterial(estoqueTemp, itemEstoque.getQuantidade(), pegaIdEmpresa(), idDestinatario(this.pedido), false,true,true);
				estoqueTemp.getBarrasEstoque().setIdEmpresa(pegaIdEmpresa());
				estoqueTemp.getBarrasEstoque().setIdFilial(idDestinatario(this.pedido));
				itemEstoque.setBarras(this.estoqueGeralDao.save(estoqueTemp.getBarrasEstoque()));

	}

	
	public boolean verificaEmpCustoMedio() {
		boolean retorno = false;
		if (this.getUsuarioAutenticado().getIdFilial() != null) {
			if (this.empresaUsuario.getFil().isGerarCustoMedio() ) {
				retorno = true;
			}
		}else {
			if (this.empresaUsuario.getEmp().isGerarCustoMedio()) {
				retorno = true;
			}
		}
		return retorno;
	}
	
	
}	
