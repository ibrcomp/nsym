package br.com.nsym.application.controller;

import java.io.File;
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

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Transient;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;

import org.hibernate.HibernateException;
import org.primefaces.component.export.PDFOptions;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.CaixaException;
import br.com.ibrcomp.exception.DevolucaoException;
import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.RelatoriosException;
import br.com.ibrcomp.interceptor.Guarded;
import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.relatorios.RelatorioVendas;
import br.com.nsym.domain.misc.Controle;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.misc.ImpressoraACBr;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.dto.RelVendasFabricanteDTO;
import br.com.nsym.domain.model.entity.estoque.Estoque;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.financeiro.tools.CaixaUtil;
import br.com.nsym.domain.model.entity.financeiro.tools.TabelaPreco;
import br.com.nsym.domain.model.entity.fiscal.NcmEstoque;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.CaixaFinalidade;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.tools.ControlePedido;
import br.com.nsym.domain.model.entity.tools.FiscalStatus;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.tools.TipoMedida;
import br.com.nsym.domain.model.entity.venda.CupomVendaDTO;
import br.com.nsym.domain.model.entity.venda.DestinatarioPedido;
import br.com.nsym.domain.model.entity.venda.DestinatarioTransferencia;
import br.com.nsym.domain.model.entity.venda.EmitenteVenda;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.entity.venda.RelComissaoColaboradoresDTO;
import br.com.nsym.domain.model.entity.venda.RelatorioEncomendaPedidos;
import br.com.nsym.domain.model.entity.venda.RelatorioVendasDTO;
import br.com.nsym.domain.model.entity.venda.TipoTransacao;
import br.com.nsym.domain.model.entity.venda.Transacao;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.ClienteRepository;
import br.com.nsym.domain.model.repository.cadastro.ColaboradorRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.CustoProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.FormaDePagementoRepository;
import br.com.nsym.domain.model.repository.fiscal.NcmEstoqueRepository;
import br.com.nsym.domain.model.repository.venda.AgPedidoRepository;
import br.com.nsym.domain.model.repository.venda.ControlePedidoRepository;
import br.com.nsym.domain.model.repository.venda.ItemPedidoRepository;
import br.com.nsym.domain.model.repository.venda.PedidoRepository;
import br.com.nsym.domain.model.repository.venda.TransacaoRepository;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class PedidoBean extends AbstractBeanEmpDS<Pedido> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2820019828248790885L;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_EVEN);

	@Inject
	private ImpressoraACBr impressora;
	
	@Inject
	private AcbrComunica acbr;
	
	@Getter
	@Setter
	private Pedido pedido;
	
	@Inject
	private EmpresaRepository empresaDao;
		
	@Inject
	private FilialRepository filialDao;

	@Inject	
	private PedidoRepository pedidoDao;
	
	@Getter
	@Setter
	private ControlePedido controlePedido;
	
	@Inject
	private Controle controle;

	@Getter
	@Setter
	private boolean deleted = false;

	@Inject
	private ControlePedidoRepository controleDao;

	@Getter
	private AbstractLazyModel<Pedido> pedidoModel;
	
	@Getter
	@Setter
	private List<Pedido> listaPedidoReceber = new ArrayList<Pedido>();
	
	// Utilitario com todas as fun��es de estoque
	@Inject
	private EstoqueUtil estoqueUtil;
	
	// Estoque Geral
	@Inject
	private BarrasEstoqueRepository barrasDao;
	
	//Estoque fiscal
	@Inject
	private NcmEstoqueRepository ncmDao;
	
	@Getter
	@Setter
	private BarrasEstoque barrasEstoque;
	
	@Getter
	@Setter
	private NcmEstoque ncmEstoque;
	
	@Getter
	@Setter
	private List<ItemPedido> listaItensPedido = new ArrayList<ItemPedido>();
	
	@Getter
	@Setter
	private ItemPedido item; 
	
	@Getter
	@Setter
	private Estoque estoque;
	
	@Getter
	@Setter
	private LocalDate dataInicial = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now();
	
	@Getter
	@Setter
	private List<Transacao> listaDeTransacao = new ArrayList<Transacao>();
	
	@Inject
	private TransacaoRepository transacaoDao;
	
	@Getter
	@Setter
	private List<Colaborador> listaDeAtendentes = new ArrayList<Colaborador>();
	
	@Inject
	private ColaboradorRepository colaboradorDao;
	
	@Getter
	@Setter
	private Colaborador colaborador = new Colaborador();
	
	@Getter
	@Setter
	private List<Colaborador> listaColaborador = new ArrayList<>();

	@Getter
	private AbstractLazyModel<Produto> produtoModel;
	
	@Inject
	private ProdutoRepository produtoDao;
	
	@Getter
	@Setter
	private ProdutoCusto custoProduto;
	
	@Getter
	@Setter
	private Produto produto ;
	
	@Getter
	@Setter
	private String ref = "";

	@Getter
	@Setter
	private BigDecimal quantidade = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal precoVenda= new BigDecimal("0");
	
	@Getter
	@Setter
	private boolean descontoPercentual;

	@Getter
	@Setter
	private BigDecimal desPercVal=new BigDecimal("0");
	
	@Getter
	@Setter
	private TabelaPreco tabelaSelecionada = TabelaPreco.TA;
	
	@Setter
	@Getter
	private BigDecimal tempValorUnitario = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal tempTotalUnitario = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalPedido = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalDesconto = new BigDecimal("0");
	
	@Setter
	private AbstractDataModel<ItemPedido> listaItemModel; 
	
	@Getter
	@Setter
	private ItemPedido itemSelecionado = new ItemPedido();
	
	@Inject
	private FormaDePagementoRepository formaPagDao;

	@Getter
	private List<FormaDePagamento> listaFormasDePagamento = new ArrayList<>();
	
	@Getter
	@Setter
	@Transient
	private List<BarrasEstoque> listaBarrasTemp = new ArrayList<>(); 
	
	@Getter
	@Setter
	private DestinatarioPedido destino;
	
	@Getter
	@Setter
	private String destinatario;
	
	@Getter
	@Setter
	private String nome;

	@Getter
	@Setter
	private Cliente cliente;

	@Getter
	@Setter
	private List<Cliente> listaCliente = new ArrayList<Cliente>();

	@Getter
	@Setter
	private List<Filial> listaFilial = new ArrayList<Filial>();

	@Getter
	@Setter
	private List<Fornecedor> listaFornecedor = new ArrayList<Fornecedor>();

	@Getter
	@Setter
	private Empresa empresa;

	@Getter
	@Setter
	@Inject
	private FornecedorRepository fornecedorDao;

	@Getter
	@Setter
	@Inject
	private ClienteRepository clienteDao;
	
	@Getter
	@Setter
	private String cnpj;
	
	@Getter
	@Setter
	private String razao;

	@Getter
	@Setter
	private String endereco;

	@Getter
	@Setter
	private String bairro;

	@Getter
	@Setter
	private String municipio;

	@Getter
	@Setter
	private String uf;

	@Getter
	@Setter
	private Fornecedor fornecedor;

	@Getter
	@Setter
	private Filial filial;
	
	@Getter
	@Setter
	private String meuip;
	
//	@Inject
//	private EmitenteVendaRepository emitenteDao;
	
	@Inject
	private ItemPedidoRepository itemPedidoDao;
	
	@Getter
	@Setter
	private Configuration configUser;
	
	@Getter
	@Setter
	private EmpUser empresaUsuario;
	
	@Getter
	@Setter
	private boolean habilita = false;
	
	@Getter
	@Setter
	private List<ItemPedido> listaItemPedidoExcluir = new ArrayList<ItemPedido>();
	
	@Getter
	@Setter
	private List<ItemPedido> listaItemPedidoCompara = new ArrayList<ItemPedido>();
	
	@Getter
	@Setter
	private PDFOptions pdfOpt = new PDFOptions();
	
	@Getter
	@Setter
	private BigDecimal totallistaPedidoTemp = new BigDecimal("0");
	
	@Inject
	private CustoProdutoRepository custoDao;
	
	@Inject
	private RelatorioVendas relatorios;
	
	@Getter
	@Setter
	private List<Filial> listaDeFiliais = new ArrayList<Filial>();
	
	@Getter
	@Setter
	private boolean transferenciaHabilitado = false;
	
	@Getter
	@Setter
	private DestinatarioTransferencia destinoTransferencia;
	
	@Getter
	@Setter
	private Transacao transacaoT;
	
	@Getter
	@Setter
	private BigDecimal quatPecas = new BigDecimal("0");
	
	@Getter
	@Setter
	private AbstractDataModel<Pedido> listaPedidosPorPeriodo = new AbstractDataModel<Pedido>();
	
	@Getter
	@Setter
	private AgPedido agrupado = new AgPedido();
	
	@Inject
	private AgPedidoRepository agrupadoDao;
	
	@Inject
	private CaixaUtil caixaUtil;
	
	@Getter
	@Setter
	transient Caixa caixa;
	
	@Getter
	@Setter
	transient boolean caixaVenda = false;
	@Getter
	@Setter
	transient boolean apenasVenda = false;
	
	@Getter
	@Setter
	private LocalDate dataPrevisao = LocalDate.now() ;
	
	@Getter
	@Setter
	private String obs;
	
	@Getter
	@Setter
	private boolean habilitaPrevisao = false;
	
	@Getter
	@Setter
	private String filtroNome;
	
	
	@Getter
	private DateTimeFormatter formatador = DateTimeFormatter
	.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	 
	
	@Override
	public Pedido setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pedido setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
//		this.pedidoModel = getLazyPedido();
		this.viewState = ViewState.LISTING;
		this.listaPedidosPorPeriodo = new AbstractDataModel<Pedido>(this.pedidoDao.produtosVendidosPorPeriodo(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial()));
		this.totallistaPedidoTemp = this.pedidoDao.totalPedidosPeriodo(dataInicial, dataFinal, pegaIdEmpresa(), pegaIdFilial());

	
//		System.out.println("Total: " + this.totallistaPedidoTemp);

	}
	/**
	 * Gerador de lista de pedidos recebidos com Status de venda e recebido
	 */
	public void geraListaPedidosRecebidos() {
		this.viewState = ViewState.LISTING;
		this.listaPedidosPorPeriodo = new AbstractDataModel<Pedido>(this.pedidoDao.listaPedidoPorTipoEStatus(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(),PedidoTipo.PVE,PedidoStatus.REC));
	}
	
	
	public void geraListaAtualizada() {
		this.pedidoModel = getLazyPedido();
		this.totallistaPedidoTemp = this.pedidoDao.totalPedidosPeriodo(dataInicial, dataFinal, pegaIdEmpresa(), pegaIdFilial());
		System.out.println("Total: " + this.totallistaPedidoTemp);
	}
	
	
	public PDFOptions criaPdfOpt() {
		pdfOpt.setFacetBgColor("#3370a8");// Azul
		pdfOpt.setFacetFontSize("12");
		pdfOpt.setFacetFontColor("#ffffff");// cinza
		pdfOpt.setFacetFontStyle("BOLD");
		pdfOpt.setCellFontColor("#000000"); // Preto
		pdfOpt.setCellFontSize("8");

		return pdfOpt;
	}
	
	public void preProcessPDF(Object document) throws IOException,
	BadElementException, DocumentException {
		System.out.println("Iniciando Insers�o de imagem no PDF");
		Document pdf = (Document) document;
		ServletContext servletContext = (ServletContext)
		FacesContext.getCurrentInstance().getExternalContext().getContext();
		System.out.println(servletContext.getRealPath(""));
		String logo =servletContext.getRealPath("") + File.separator + "resources" +
				File.separator +"nsym"+ File.separator +"img" +File.separator +"ibrcompPHOTOSHOP.png";
		if (pdf.isOpen()) {
			pdf.add(Image.getInstance(logo));
		}else {
			pdf.open();
			pdf.add(Image.getInstance(logo));
			pdf.add(Chunk.NEWLINE);
			pdf.add(new Paragraph(" "));
			pdf.add(Chunk.NEWLINE);
			pdf.add(new Paragraph("Relat�rio de Vendas"));
			pdf.add(Chunk.NEWLINE);
			pdf.add(new Paragraph("Total: R$ " + this.totallistaPedidoTemp.setScale(2,RoundingMode.HALF_EVEN).toString()));
			pdf.add(Chunk.NEWLINE);
		}
	}
	
//	"resources" +
	public BigDecimal onSummaryRow() {
		BigDecimal total = new BigDecimal("0");
		List<Pedido> listaTemp = new ArrayList<Pedido>();
		listaTemp = this.pedidoModel.getModelSource();
		if (listaTemp != null) {
			for (Pedido pedido : listaTemp) {
				total = total.add(pedido.getValorTotalPedido());
			}
		}
		return total;
	}
	
	
	@PostConstruct
	public void init(){
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}
	
	public void initializeFormTransferencia(Long id) {
		try {
			this.listaDeFiliais = this.filialDao.listaDeFiliaisPorEmpresa(pegaIdEmpresa(), false);
			if (this.listaDeFiliais.isEmpty()) {
				throw new CaixaException(this.translate("emp.notExist.filial"));
			}else {
				this.transferenciaHabilitado = true;
			}
			this.listaDeTransacao = this.transacaoDao.listaTodasAsTransacoesPorFilial(false,TipoTransacao.tra,pegaIdEmpresa(), pegaIdFilial());
			this.listaDeAtendentes = this.colaboradorDao.listaColaboradorPorFilial(false,pegaIdEmpresa(), pegaIdFilial());
			this.colaborador = new Colaborador();
			this.quantidade = this.configUser.getQuantidadePadraoPDV();
			if (id == null) {
				this.viewState = ViewState.ADDING;
				this.pedido = new Pedido();
				this.destino = new  DestinatarioPedido();
				this.destinoTransferencia = new DestinatarioTransferencia();
				if (this.empresaUsuario.getFil() != null) {
					if (this.listaDeFiliais.size() == 1) {
						this.pedido.setDestinoMatriz(true);
						this.habilitaInserirTransf();
					}
				}
				
			}else {
				this.viewState = ViewState.EDITING;
				
			}
		}catch (CaixaException e) {
			// TODO: handle exception
			this.addError(true, "caixa.error", e.getMessage());
		}
	}
	
	public void initializeFormDevolucao(Long id) {
		this.listaDeTransacao = transacaoDao.listaTodasAsTransacoesPorFilial(false,TipoTransacao.dev,pegaIdEmpresa(), pegaIdFilial());
		this.listaDeAtendentes = this.colaboradorDao.listaColaboradorPorFilial(false,pegaIdEmpresa(), pegaIdFilial());
		this.quantidade = this.configUser.getQuantidadePadraoPDV();
		if (id == null) { // pedido novo
			this.viewState = ViewState.ADDING;
			this.habilita = false;
			this.pedido = new Pedido();
			this.quatPecas = new BigDecimal("0");
			this.colaborador = new Colaborador();
			this.estoque = new Estoque();
			this.barrasEstoque = new BarrasEstoque();
			this.destino = new  DestinatarioPedido();
			this.pedido.setTipoPesquisa(TipoPesquisa.CLI);
			this.controlePedido= new ControlePedido();
		}
	}
	/**
	 * Verifica se empresa esta com a configuração de venda + caixa integrados ativado
	 * @return false = nao permite fazer a venda  true = permite fazer a venda
	 * @throws CaixaException
	 */
	public boolean permiteVendaCaixa()  {
		try {
			boolean resp = false;
			
			if (this.configUser.isVendaCaixa()) {
				this.caixa = this.caixaUtil.retornaCaixa(CaixaFinalidade.nao);
				if (this.caixa.getId() != null) {
					resp = true;
				}
			}else {
				this.apenasVenda= true;
			}
			return resp;
		}catch (CaixaException c) {
			this.addError(true, "caixa.error",c.getMessage());
			return false;
		}
	}
	
	public boolean visualizaPrevisao() {
		boolean resposta = false;
		if (getUsuarioAutenticado().getIdFilial() != null) {
			if (this.empresaUsuario.getFil().isPrevisaEntrega() ) {
				resposta = true;
			}
		}else {
			if (this.empresaUsuario.getEmp().isPrevisaEntrega()) {
				resposta = true;
			}
		}
		return resposta;
	}
	
	public void initializeRelFechamento() {
		this.viewState = ViewState.DETAILING;
		this.dataInicial = LocalDate.now();
		this.dataFinal = LocalDate.now();
		this.filtroNome = "";
	}
	
	@Override
	public void initializeForm(Long id) {
		try {
			this.listaDeTransacao = this.transacaoDao.listaTodasAsTransacoesPorFilial(false,TipoTransacao.ven,pegaIdEmpresa(), pegaIdFilial());
			this.listaDeAtendentes = this.colaboradorDao.listaColaboradorPorFilial(false,pegaIdEmpresa(), pegaIdFilial());
			this.listaFormasDePagamento = this.formaPagDao.listaFormaDePagamento(pegaIdEmpresa(), pegaIdFilial());
			this.quantidade = this.configUser.getQuantidadePadraoPDV();
			this.caixaVenda = permiteVendaCaixa();
			if (id == null) { // pedido novo
					this.viewState = ViewState.ADDING;
					this.habilita = false;
					this.pedido = new Pedido();
					this.quatPecas = new BigDecimal("0");
					this.colaborador = new Colaborador();
					this.dataPrevisao = LocalDate.now();
					this.obs = "";
					//			this.transacao = new Transacao();
					if(this.configUser.getTransacaoPadrao() != null && this.pedido.getTransacao() == null) {
						this.pedido.setTransacao(this.transacaoDao.pegaTransacaoPorId(this.configUser.getTransacaoPadrao()));
						//				this.transacao = this.pedido.getTransacao();
						this.pedido.setPagamento(this.pedido.getTransacao().getPagamentoPadrao());
						this.tabelaSelecionada = this.pedido.getTransacao().getTabelaPadrao();
					}
					this.estoque = new Estoque();
					this.barrasEstoque = new BarrasEstoque();
					this.destino = new  DestinatarioPedido();
					this.pedido.setTipoPesquisa(TipoPesquisa.CLI);
					this.controlePedido= new ControlePedido();

			}else { // altera�o ou visualiza��o pedido j� salvo 
				this.viewState = ViewState.EDITING;
				this.habilita = true;
				this.pedido = this.pedidoDao.pegaPedidoPorId(id);
				this.dataPrevisao = this.pedido.getPrevisaoEntrega();
				this.habilitaPrevisao = true;
				this.obs = this.pedido.getObs();
				//			this.transacao = this.transacaoDao.pegaTransacaoPorId(this.pedido.getTransacao().getId());
				this.colaborador = this.colaboradorDao.pegaColaboradorPorID(this.pedido.getAtendente().getId());
				//			this.pedido.setTransacao(this.transacao);
				this.listaItemPedidoCompara = this.pedido.getListaItensPedido();
				this.tabelaSelecionada = this.pedido.getTransacao().getTabelaPadrao();
				this.destino =  this.pedido.getDestino();
				if (this.pedido != null) {
					this.totalPedido  = this.pedido.getValorTotalPedido();
					this.totalDesconto = this.pedido.getDesconto();
					for (ItemPedido item : this.pedido.getListaItensPedido()) {
						this.quatPecas = this.getQuatPecas().add(item.getQuantidade());
					}
					switch (this.pedido.getTipoPesquisa()) {
						case CLI:
							if (this.pedido.getDestino().getCliente() != null) {
								this.destinatario = this.pedido.getDestino().getCliente().getRazaoSocial();
							}else {
								this.destinatario = "";
							}
							break;
						case FIL:
							if (this.pedido.getDestino().getCliente() != null) {
								this.destinatario = this.pedido.getDestino().getFilial().getRazaoSocial();
							}else {
								this.destinatario = "";
							}
							break;
						case COL:
							if (this.pedido.getDestino().getCliente() != null) {
								this.destinatario = this.pedido.getDestino().getColaborador().getNome();
							}else {
								this.destinatario = "";
							}
							break;
						case FOR:
							if (this.pedido.getDestino().getCliente() != null) {
								this.destinatario = this.pedido.getDestino().getFornecedor().getRazaoSocial();
							}else {
								this.destinatario = "";
							}
							break;
						case MAT:
							if (this.pedido.getDestino().getCliente() != null) {
								this.destinatario = this.pedido.getDestino().getEmpresa().getRazaoSocial();
							}else {
								this.destinatario = "";
							}
							break;
						default :
							this.destinatario = "";
							break;
					}
				}
			}
//		}catch (CaixaException c) {
//			this.addError(true, "caixa.error",c.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal",e.getMessage());
		}
	}
	
	public void pegaIp() {
		this.meuip = this.meuIP();
	}
	

	/**
	 * Gera a lista de tributos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Pedido> getLazyPedido(){
		this.pedidoModel = new AbstractLazyModel<Pedido>() {

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

				Page<Pedido> page = pedidoDao.listaPedidosEmitidosPorIntervaloData(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(), pageRequest,null,null);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = pedidoDao.listaPedidosEmitidosPorIntervaloData(dataInicial,dataFinal, pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				return page.getContent();
			}
		};
		
			
		
		return pedidoModel;
	}
	
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Produto> getLazyProduto(){
		this.produtoModel = new AbstractLazyModel<Produto>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3097078596870201247L;

			@Override
			public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<Produto> page = produtoDao.pegaPageProdutoComCustoComEstoque(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = produtoDao.pegaPageProdutoComEstoqueComCustoComFiltro(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return produtoModel;
	}
	/**
	 * redireciona para a pagina com o ID da NFE a ser editado
	 * 
	 * @param nfeID
	 * 
	 * @returna pagina de edi��o de NFE
	 */
	public String changeToEdit(Long idPedido) {
		if (this.pedido.getPedidoStatus().equals(PedidoStatus.Agp)) {
			this.addWarning(true,"venda.agrupado.editing.notAllowed");
			return null;
		}else {
			if (this.pedido.getPedidoTipo().equals(PedidoTipo.PVE) ) {
				return "formPDV.xhtml?faces-redirect=true&idVenda=" + idPedido;
			}else {
				if (this.pedido.getPedidoTipo().equals(PedidoTipo.TRA)) {
					return "formTransferencia.xhtml?faces-redirect=true&idVenda=" + idPedido; 
				}else {
					this.addWarning(true, "operation.not.allowed",this.pedido.getPedidoTipo().toString());
					return null;
				}
			}
		}
	}

	/**
	 * redireciona para Cadastramento de nova NFE / edi��o de NFE j� cadastrado
	 * @return pagina de inclusao de NFE
	 */
	public String newPedido() {
		return "formPDV.xhtml?faces-redirect=true";
	}
	
	public String newTransfere() {
		return "formTransferencia.xhtml?faces-redirect=true";
	}
	
	public String newDevolucao() {
		return "formDevolucao.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListPedido() {
		return "formListVendas.xhtml?faces-redirect=true";
	}
	
	/**
	 * Redireciona para tela Principal
	 * @return Dashboard
	 */
	public String toTelaPrincipal() {
		return "/main/dashboard.xhtml?faces-redirect=true";
	}
	/**toTela
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaProduto(){
		this.produtoModel = getLazyProduto();
		this.updateAndOpenDialog("PesquisaProdutoPDVDialog", "dialogPesquisaProdutoPDV");
	}
	
	/**
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaBarras(){
		this.updateAndOpenDialog("PesquisaBarrasDialog", "dialogPesquisaBarras");
	}
	
	/**
	 * M�todo que seta o Bar quando selecionado em uma lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectProduto(SelectEvent event)throws IOException{
		this.produto = (Produto) event.getObject();
		this.ref = this.produto.getReferencia();
		definePreco();
	}
	
	/**
	 * M�todo que seta o c�digo de barras quando selecionado em uma lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectBarras(SelectEvent event)throws IOException{
		this.barrasEstoque = (BarrasEstoque) event.getObject();
		this.item.setBarras(this.barrasDao.findById(this.barrasEstoque.getId(), false));
}
	
	public void habilitaInserir() {
		if (this.colaborador != null) {
			this.habilita = true;
			this.habilitaPrevisao = true;
		}
	}
	
	
	public void habilitaInserirTransf() {
		if (this.pedido.getTransacao() != null) {
			if (pedido.isDestinoMatriz()) {
				if (this.colaborador != null) {
					this.colaborador = this.colaboradorDao.pegaColaboradorPorID(this.colaborador.getId());
					this.pedido.setAtendente(this.colaborador);
					this.habilita=true;
					this.destino.setEmpresa(this.empresaUsuario.getEmp());
					this.destinoTransferencia.setEmpresa(this.empresaUsuario.getEmp());
					this.tabelaSelecionada = this.pedido.getTransacao().getTabelaPadrao();
				}
			}else {
				if (verificaTransDestino()) {
					if (this.colaborador != null) {
						this.colaborador = this.colaboradorDao.pegaColaboradorPorID(this.colaborador.getId());
						this.pedido.setAtendente(this.colaborador);
						this.habilita = true;
						this.destino.setFilial(this.destinoTransferencia.getFilial());
						this.tabelaSelecionada = this.pedido.getTransacao().getTabelaPadrao();
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Verifica se foi definido uma empresa para efetuar a transferencia
	 * @return true= preenchido false = n�o preenchido
	 */
	public boolean verificaTransDestino() {
		boolean resultado = false;
		if (this.destinoTransferencia.getEmpresa() != null) {
			resultado = true;
		}else {
			if (this.destinoTransferencia.getFilial() != null) {
				resultado = true;
			}
		}
		return resultado;
	}
	
	
	public void setaTabela() {
		if (this.pedido.getTransacao() == null) {
			this.tabelaSelecionada = TabelaPreco.TA;
		}else {
			this.tabelaSelecionada = this.pedido.getTransacao().getTabelaPadrao();
		}
	}
	
	/**
	 * Método para definir o preço do produto conforme seleção da tabela de preços
	 */
	public void definePreco() {
		if (this.produto != null) {
			System.out.println("Estou dentro do definePreco produto != de null");
			switch (this.tabelaSelecionada) {
			case TA:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco1();
				System.out.println("Setando pre�o1");
				break;
			case TB:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco2();
				System.out.println("Setando pre�o2");
				break;
			case TC:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco3();
				System.out.println("Setando pre�o3");
				break;
			case TD:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco4();
				System.out.println("Setando pre�o4");
				break;
			case TE:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco5();
				System.out.println("Setando pre�o5");
				break;
			case TZ:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getCusto();
				System.out.println("Setando custo");
				break;
			default:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco1();
				System.out.println("Setando default");
				break;
			}
		}
	}
	
	/**
	 * M�todo que recebe o ESTOQUE do item do pedido 
	 *
	 * @param item
	 * @return
	 * @throws EstoqueException 
	 */
	public Estoque pegaEstoque (ItemPedido item) throws EstoqueException {
//		try {
		return estoqueUtil.preencheEstoqueItem(item, pegaIdEmpresa(), pegaIdFilial());
//		}catch (EstoqueException e) {
//			this.addError(true, "exception.error.fatal", e.getMessage());
//			return null;
//		}
	}
	
	public boolean permiteEstoqueNegativo() {
		return this.empresaUsuario.getEmp().isEstoqueNegativo();
	}
	
	/**
	 * M�todo que retira do estoque a quantidade informada, caso
	 * BarrasEstoque ou NcmEstoque esteja nulo ser� criado uma base com quantidade = 0 para depois negativar.
	 * @param listaItem
	 * @throws EstoqueException
	 */
	
	@Transactional
	public void retiraDoEstoque(List<ItemPedido> listaItem) throws EstoqueException {
		try {
			Estoque estoqueTemp = new Estoque();
			for (ItemPedido itemPedidoTemp : listaItem) {
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				if (permiteEstoqueNegativo() == false) {
					if (estoqueTemp.getBarrasEstoque().getTotalEstoque().compareTo(itemPedidoTemp.getQuantidade()) >= 0 ) {
						estoqueTemp = estoqueUtil.subtraiEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true);
						barrasDao.save(estoqueTemp.getBarrasEstoque());
					}else {
						throw new EstoqueException(this.translate("estoque.nfe.emite.item.outOfStock"));
					}
				}else {
					estoqueTemp = estoqueUtil.subtraiEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true);
					barrasDao.save(estoqueTemp.getBarrasEstoque());
				}
			}
		}catch (EstoqueException e) {
			throw new EstoqueException(this.translate("hibernate.persist.fail.barrasEstoque"));
		}
	}
	/**
	 * M�todo que devolve para o estoque a quantidade que anteriormente havia sido retirado.
	 * @param listaItem
	 */
	
	@Transactional
	public void devolveParaEstoque(List<ItemPedido> listaItem) throws EstoqueException{
		try {
			Estoque estoqueTemp = new Estoque();
			for (ItemPedido itemPedidoTemp : listaItem) {
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				estoqueTemp = estoqueUtil.acrescentaEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true);
				barrasDao.save(estoqueTemp.getBarrasEstoque());	
				
			}
		}catch (EstoqueException e) {
			throw new EstoqueException(this.translate("hibernate.persist.fail.barrasEstoque"));
		}
	}
	
	@Transactional
	public ControlePedido pegaNumeroControle() {
		try {
			ControlePedido control = this.controle.retornaNumeroControleDisponivel(pegaIdEmpresa(), pegaIdFilial());
			if (control != null) {
				control = this.controle.saveControle(control);
			}
			return control;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail",h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	public void onRowSelect(SelectEvent event)throws IOException{
		this.pedido = ((Pedido) event.getObject());
		if (this.pedido.getPedidoStatus() == PedidoStatus.CAN ) {
			this.addWarning(true, "pedidoPDV.status.cancelOrRec");
		}else {
			if (this.pedido.getPedidoStatus() == PedidoStatus.REC) {
				this.viewState = ViewState.PRINTING;
			}else {
				this.viewState = ViewState.EDITING;
			}
		}
	}
	
	public void setaCusto(){
		this.custoProduto = new ProdutoCusto();
		if (!this.produto.getListaCustoProduto().isEmpty() || this.produto.getListaCustoProduto() != null){
			if (this.produto.getListaCustoProduto().size() == 1){
				for (ProdutoCusto custo : this.produto.getListaCustoProduto()) {
					if ((custo.getIdEmpresa() == this.pegaIdEmpresa()) && (custo.getIdFilial() == this.pegaIdFilial() )){
						this.custoProduto = custo;
					}
				}

			}
		}
	}
	
	/**
	 * fun��o para verifica��o de config para permitir ou nao estoqueNCM negativo.
	 * @param item
	 * @return boolean 
	 * @throws EstoqueException 
	 */

	public boolean permiteEmissao(ItemPedido item) throws EstoqueException {
		boolean permissao= true;
		System.out.println("Estou dentro do permiteEmissao");
		Estoque estoqueTemp = pegaEstoque(item);
		System.out.println("passei pelo pegaEstoque");
		if (estoqueTemp.getBarrasEstoque() == null) {
			if (this.empresaUsuario.getEmp().isEstoqueNegativo() == false) {
				System.out.println("estoque fiscal = null ");
				permissao = false;
			}
		}else {
			if (this.empresaUsuario.getEmp().isEstoqueNegativo() == false) {
				if (estoqueTemp.getBarrasEstoque().getTotalEstoque().compareTo(new BigDecimal("0")) > 0 ) {
					if (item.getQuantidade().compareTo(estoqueTemp.getBarrasEstoque().getTotalEstoque()) < 1 ) {
						System.out.println("estoque fiscal = positivo compare =" + item.getQuantidade().compareTo(estoqueTemp.getBarrasEstoque().getTotalEstoque())+ "Estoque: " + estoqueTemp.getBarrasEstoque().getTotalEstoque());
						permissao = true;
					}else {
						System.out.println("estoque fiscal = negativo compare =" + item.getQuantidade().compareTo(estoqueTemp.getBarrasEstoque().getTotalEstoque())+ "Estoque: " + estoqueTemp.getBarrasEstoque().getTotalEstoque());
						permissao = false;
					}
				}else {
					permissao = false;
				}
			}
		}
		System.out.println("antes de retornar a permissao true ou false (fun��o permitEmissao)");
		return permissao; 
	}
	
	public ItemPedido encontraProduto(String codigo) {
		ItemPedido itemTemp = new ItemPedido();
		itemTemp.setRef(codigo);
			this.barrasEstoque = this.barrasDao.encontraBarrasPorEmpresa(codigo, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
			if (this.barrasEstoque != null ) {
				this.produto = this.produtoDao.pegaProdutoID(this.barrasEstoque.getProdutoBase().getId(), pegaIdEmpresa(),pegaIdFilial());
//				this.produto = this.produtoDao.findById(this.barrasEstoque.getProdutoBase().getId(), false);
				if(this.produto != null) {
					itemTemp.setBarras(this.barrasEstoque);
//					this.produto.getListaCustoProduto().add(this.custoDao.pegaProdutoCusto(this.produto, pegaIdEmpresa(), pegaIdFilial()));
//					itemTemp.setProduto(this.produto);
				}
			}else {
				this.produto = this.produtoDao.pegaProdutoRef(codigo, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
				this.listaBarrasTemp = this.barrasDao.listaBarrasPorProduto(this.produto, pegaIdEmpresa(), pegaIdFilial());
				if (this.listaBarrasTemp !=null && this.listaBarrasTemp.size() > 1) {
					// chamar lista para selecionar a barras que esta sendo vendida e setar para This.produto.
					telaListaBarras();
				}else {
					if (this.listaBarrasTemp != null && this.listaBarrasTemp.size() == 1) {
//						this.produto = this.produtoDao.findById(this.listaBarrasTemp.get(0).getProdutoBase().getId(), false);
						this.produto = this.produtoDao.pegaProdutoID(this.listaBarrasTemp.get(0).getProdutoBase().getId(), pegaIdEmpresa(),pegaIdFilial());
//						this.produto.getListaCustoProduto().add(this.custoDao.pegaProdutoCusto(this.produto, pegaIdEmpresa(), pegaIdFilial()));
						itemTemp.setBarras(this.listaBarrasTemp.get(0));
//						itemTemp.setProduto(this.produto);
					}
				}
			}
		return itemTemp;
	}
	
	public void localizaProduto() {
		try {
			this.item = new ItemPedido();
			BigDecimal valorDeDesconto = new BigDecimal("0");
			BigDecimal percentualDesconto = new BigDecimal("0");
			if (this.descontoPercentual){
				percentualDesconto = this.desPercVal.divide(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN);
			}
			if (this.ref != null && this.quantidade.compareTo(new BigDecimal("0")) == 1 ){
				this.item = encontraProduto(this.ref);
				System.out.println("Estou apos localizar o produto: " + this.produto.getDescricao());
				setaCusto();
				System.out.println("Estou apos setar o custo do produto: " + this.custoProduto.getCusto());
				if (this.produto != null){	
					System.out.println("adicionando item na lista!");
					this.item.setProduto(this.produto);
					this.item.setQuantidade(this.quantidade);
//					permiteEmissao(this.item)	
					if (true) {
						this.item.setPorcentagem(this.descontoPercentual);
						switch (this.tabelaSelecionada) {
						case TA:
							if (this.custoProduto.getPreco1().compareTo(new BigDecimal("0")) < 1) {
								throw new EstoqueException(this.translate("price.preco1.equal.null"));
							}
							if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0 ){
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco1().setScale(4,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								break;
							}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
								if (this.isDescontoPercentual()){
									this.item.setQuantidade(this.quantidade);
									valorDeDesconto = this.custoProduto.getPreco1().multiply(percentualDesconto);
									this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
									System.out.println("desconto : "+this.item.getDesconto());
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco1().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);	
									}
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}else{
									valorDeDesconto = this.desPercVal;
									this.item.setDesconto(valorDeDesconto);
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco1().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.item.setQuantidade(this.quantidade);
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}
							}
						case TB:
							if (this.custoProduto.getPreco2().compareTo(new BigDecimal("0")) < 1) {
								throw new EstoqueException(this.translate("price.preco2.equal.null"));
							}
							if  (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco2().setScale(4,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								break;
							}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
								if (this.isDescontoPercentual()){
									this.item.setQuantidade(this.quantidade);
									valorDeDesconto = this.custoProduto.getPreco2().multiply(percentualDesconto);
									this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco2().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}else{
									valorDeDesconto = this.desPercVal;
									this.item.setDesconto(valorDeDesconto);
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco2().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.item.setQuantidade(this.quantidade);
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}
							}
						case TC:
							if (this.custoProduto.getPreco3().compareTo(new BigDecimal("0")) < 1) {
								throw new EstoqueException(this.translate("price.preco3.equal.null"));
							}
							if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco3().setScale(4,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								break;
							}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
								if (this.isDescontoPercentual()){
									this.item.setQuantidade(this.quantidade);
									valorDeDesconto = this.custoProduto.getPreco3().multiply(percentualDesconto);
									this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco3().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}else{
									valorDeDesconto = this.desPercVal;
									this.item.setDesconto(valorDeDesconto);
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco3().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.item.setQuantidade(this.quantidade);
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}
							}
						case TD:
							if (this.custoProduto.getPreco4().compareTo(new BigDecimal("0")) < 1) {
								throw new EstoqueException(this.translate("price.preco4.equal.null"));
							}
							if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco4().setScale(4,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								break;
							}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
								if (this.isDescontoPercentual()){
									this.item.setQuantidade(this.quantidade);
									valorDeDesconto = this.custoProduto.getPreco4().multiply(percentualDesconto);
									this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco4().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}else{
									valorDeDesconto = this.desPercVal;
									this.item.setDesconto(valorDeDesconto);
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco4().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.item.setQuantidade(this.quantidade);
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}
							}
						case TE:
							if (this.custoProduto.getPreco5().compareTo(new BigDecimal("0")) < 1) {
								throw new EstoqueException(this.translate("price.preco5.equal.null"));
							}
							if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco5().setScale(4,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								break;
							}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
								if (this.isDescontoPercentual()){
									this.item.setQuantidade(this.quantidade);
									valorDeDesconto = this.custoProduto.getPreco5().multiply(percentualDesconto);
									this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco5().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}else{
									valorDeDesconto = this.desPercVal;
									this.item.setDesconto(valorDeDesconto);
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getPreco5().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.item.setQuantidade(this.quantidade);
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}
							}
						case TZ:
							if (this.custoProduto.getCusto().compareTo(new BigDecimal("0")) < 1 || this.custoProduto.getCusto() == null) {
								throw new EstoqueException(this.translate("price.custo.equal.null"));
							}else {
							if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getCusto().setScale(4,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
								break;
							}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
								if (this.isDescontoPercentual()){
									this.item.setQuantidade(this.quantidade);
									valorDeDesconto = this.custoProduto.getCusto().multiply(percentualDesconto);
									this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getCusto().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}else{
									valorDeDesconto = this.desPercVal;
									this.item.setDesconto(valorDeDesconto);
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.item.setValorUnitario(this.custoProduto.getCusto().setScale(4,RoundingMode.HALF_EVEN));
									}else {
										this.item.setValorUnitario(this.precoVenda);
									}
									this.item.setQuantidade(this.quantidade);
									this.pedido.setDesconto(this.pedido.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
									this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
									break;
								}
							}
							}
						default:
							break;
						}
						this.tempValorUnitario = this.item.getValorUnitario();
						this.tempTotalUnitario  = this.item.getValorTotal();
						this.pedido.getListaItensPedido().add(this.item);
						this.totalPedido = this.totalPedido.add(this.item.getValorTotal());
						this.totalDesconto = this.totalDesconto.add(this.item.getDesconto());
						this.ref = "";
						this.precoVenda = new BigDecimal("0");
						this.quatPecas = this.getQuatPecas().add(this.item.getQuantidade());
						this.quantidade = this.configUser.getQuantidadePadraoPDV();
						this.desPercVal = new BigDecimal("0");
						//					if (this.formaPag.getId() != null) {
						//						preencheParcelamento();
						//					}
					}
//					}else{
//						this.addError(true, "estoque.nfe.emite.item.outOfStock", produto.getDescricao());
//					}	
				}else {
					this.addError(true, "produto.notsearch", this.ref);
				}
			}else {
				this.addWarning(true, "produto.refOrQuantNull");
			}
		}catch (EstoqueException t) {
			this.addError(true, "caixa.error", t.getMessage());
		}catch (Exception e) {
			System.out.println(e);
			this.addError(true, "Erro na função de localizacao de produto: " +  e.getMessage() + " " + e.getCause() );
		}
	}
	
	public TabelaPreco[] getTabelaPreco(){
		return TabelaPreco.values();
	}
	
	public AbstractDataModel<ItemPedido> getListaItemModel(){
		this.listaItemModel = new AbstractDataModel<ItemPedido>(this.pedido.getListaItensPedido());
		return this.listaItemModel;
	}
	
	public void prosseguir() {
		this.habilita = true;
	}
	
	@Transactional
	public void excluiItem(ItemPedido itemSelect) {
		try{
//			if (this.pedido.getId() != null) {
//				this.itemSelecionado =  this.itemPedidoDao.pegaReferencia(itemSelect.getId());
//			}else {
				this.itemSelecionado = itemSelect;
//			}
			boolean permiteRemover = false;
			System.out.println("inico da exclusao do item");
			if (this.pedido.getListaItensPedido().size() >1 ) {
				permiteRemover = true;
			}else {
				if (this.viewState == ViewState.ADDING) {
					permiteRemover = true;
				}
			}
			if (permiteRemover) {
				if (!this.pedido.getListaItensPedido().isEmpty() && this.itemSelecionado != null ){
					this.pedido.getListaItensPedido().remove(this.itemSelecionado);
					this.totalPedido = this.totalPedido.subtract(this.itemSelecionado.getValorTotal());
					this.totalDesconto = this.totalDesconto.subtract(this.itemSelecionado.getDesconto());
					this.pedido.setValorTotalPedido(this.totalPedido);
					this.pedido.setValorTotalProdutos(this.pedido.getValorTotalProdutos().subtract(this.itemSelecionado.getValorTotalBruto()));
					this.pedido.setDesconto(this.pedido.getDesconto().subtract(this.itemSelecionado.getDesconto()));
					this.quatPecas = this.getQuatPecas().subtract(this.itemSelecionado.getQuantidade());
					if (this.viewState == ViewState.EDITING) {
						if (itemSelect.getId() != null) {
							System.out.println("Estou no Exclui Item devolvendo a quantidade do item para o estoque! item ID: " + this.itemSelecionado.getId());
							this.listaItemPedidoExcluir.add(this.itemSelecionado);
						}
					}
				}
				if (itemSelect.getPedido() != null) {
					this.pedido = this.pedidoDao.save(this.pedido);
					initializeForm(this.pedido.getId());
				}
				this.listaItemModel = getListaItemModel();
				this.addWarning(true, "cfe.list.delete", itemSelect.getProduto().getReferencia());
			}else {
				throw new IllegalAccessException(this.translate("pedidoException.excluiItem.minimo"));
			}

		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}
	
	public EmitenteVenda preencheEmitenteVenda(){
		EmitenteVenda emissor = new EmitenteVenda();
		if (this.getUsuarioAutenticado().getIdFilial() == null){
			emissor.setEmpresa(this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false));
		}else{
			emissor.setFilial(this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false));
		}
		return emissor;
	}
	
	/**
	 * M�todo que imprimi romaneio seguindo as preferencias do usu�rio e salva o pedido
	 * @throws IOException 
	 */
	@Transactional
	public void print() throws IOException  {
		
		try {
			this.pedido.setValorTotalPedido(this.totalPedido);
			this.pedido.setControle(pegaNumeroControle());
			this.pedido.setDestino(this.destino);
			this.pedido.setEmitente(preencheEmitenteVenda());
			impressora.imprimirCupomPdv(this.pedido,true,this.getUsuarioAutenticado().getConfig());
			
		}catch (HibernateException h) {

			this.addError(true, "Erro ao persistir no banco: {0}", h.getMessage());
//			this.respostaAcbrLocal = ("Erro ao persistir no banco: " + h.getMessage());
//			return null;
		}catch(Exception e ) {
			this.addError(true, "Erro desconhecido : {0}", e.getMessage());
//			this.respostaAcbrLocal  = ("Erro desconhecido : "+ e.getMessage());
//			return null;
		}
//		System.out.println(impressora.impressoraAtivada(pegaConexao()));
//		impressora.imprimir(pegaConexao(), "ibrahim");
		
		for (String imp : impressora.listaPortasUsb(pegaConexao())) {
			System.out.println(imp);
		}
	}
	
	@Transactional
	public void exibeNumeroControle() {
		pegaNumeroControle();
		System.out.println("numero do controle: " + this.controlePedido.getControle());
	}
	
	@Guarded("salvarCaixa")
	@Transactional
	public String salvaECaixa() {
		try {
			String retorno = salvaPedido();			
			if (retorno != null) {
				this.agrupado.getListaPedidosRecebidos().add(this.pedido);
				this.agrupado.getListaItensAgrupados().addAll(this.pedido.getListaItensPedido());
				pedido.setPedidoStatus(PedidoStatus.Agp);
				if (this.pedido.getDestino() != null) {
					this.agrupado.setDestinatario(this.pedido.getDestino());
				}

				this.agrupado.setValorTotal(this.pedido.getValorTotalPedido());
				this.agrupado.setValorBruto(this.pedido.getValorTotalProdutos());
				this.agrupado.setDesconto(this.pedido.getDesconto());
				this.agrupado.setFrete(this.pedido.getValorFrete());
				this.agrupado.setAcrescimo(this.pedido.getOutrasDespesas());
				this.agrupado.setDataCriacao(LocalDate.now());
				this.agrupado.setHoraCriacao(LocalTime.now());
				this.agrupado.setStatus(PedidoStatus.AgR);
				//			this.agrupado.setCaixa(this.caixa);

				this.agrupado = agrupadoDao.save(this.agrupado);


				this.pedido.setAgrupado(this.agrupado);
				this.pedidoDao.save(this.pedido);


				System.out.println("ID pedido : " + this.agrupado.getId());
				retorno = "/main/financial/caixa/formCaixaReceber.xhtml?faces-redirect=true&idAgPedido=" + this.agrupado.getId();
			}	
			return retorno;
		}catch (DevolucaoException dev) {
			this.addError(true, "caixa.error", dev.getMessage());
			return null;
		}catch (EstoqueException estoque) {
			this.addError(true, "caixa.error", estoque.getMessage());
			return null;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	@Guarded("salvarPrint")
	@Transactional
	public String salvaEImprimi() {
		try {
			String retorno = salvaPedido();
			if (retorno != null) {
				if (this.configUser.isCupomPDF()) {
					criaPDFACupom();
				}else {
					for (int i = 0 ; i< this.configUser.getQuantViaVenda().intValue(); i++) {
						impressora.imprimirCupomPdv(this.pedido,this.configUser.isCabecalhoPDV(),this.getUsuarioAutenticado().getConfig());
					}
				}
			}	
			return retorno;
		}catch (DevolucaoException dev) {
			this.addError(true, "caixa.error", dev.getMessage());
			return null;
		}catch (EstoqueException estoque) {
			this.addError(true, "caixa.error", estoque.getMessage());
			return null;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	@Guarded("salvarPedido")
	@Transactional
	public String salvaPedido() throws HibernateException, EstoqueException, DevolucaoException   {
		try {
			if (!this.pedido.getListaItensPedido().isEmpty()) {
				if (this.pedido.getId() == null) { // pedido novo
					EmitenteVenda emitente = new EmitenteVenda();
					emitente = preencheEmitenteVenda();
					this.pedido.setControle(pegaNumeroControle());
					this.pedido.setDestino(this.destino);
					this.pedido.setAtendente(this.colaborador);
					if (this.dataPrevisao != null) {
						if (this.dataPrevisao.compareTo(LocalDate.now()) < 1 ) {
							this.pedido.setAtivaEncomenda(false);
						}else {
							this.pedido.setAtivaEncomenda(true);
							this.pedido.setPrevisaoEntrega(this.dataPrevisao);
						}
					}else {
						this.dataPrevisao = LocalDate.now();
						this.pedido.setAtivaEncomenda(false);
					}
					this.pedido.setObs(this.obs);
					this.listaItensPedido = this.pedido.getListaItensPedido();
					
					this.pedido.setValorTotalPedido(this.totalPedido);
					this.pedido.setValorTotalProdutos(this.totalPedido.add(this.totalDesconto));

					emitente.setPedido(this.pedido);
					this.pedido.setEmitente(emitente);
					switch (this.pedido.getTransacao().getTipoTransacao()) {
					case tra:
						this.pedido.setPedidoTipo(PedidoTipo.TRA);
						if (this.pedido.isDestinoMatriz() == false) {
							this.pedido.setDestinoTransferencia(this.destinoTransferencia);
						}else {
							this.destinoTransferencia.setEmpresa(this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false));
							this.pedido.setDestinoTransferencia(this.destinoTransferencia);
						}
						// baixar estoque geral
						retiraDoEstoque(listaItensPedido);
						break;
					case dev:
						// definir forma de pagamento CREDITO e disponibilizar na ficha do cliente!
						this.pedido.setPedidoTipo(PedidoTipo.DEV);
						// devolver para o estoque
						devolveParaEstoque(listaItensPedido);
						break;
					case ven:
						this.pedido.setPagamento(this.formaPagDao.findById(this.pedido.getPagamento().getId(), false));
						this.pedido.setPedidoTipo(PedidoTipo.PVE);
						// baixar estoque geral
						retiraDoEstoque(listaItensPedido);
						break;

					default:
						break;
					}
					this.pedido.setPedidoStatus(PedidoStatus.AgR);
					this.pedido.setFiscalStatus(FiscalStatus.NE);
					for (ItemPedido item : this.listaItensPedido) {
						item.setPedido(this.pedido);
					}
					if (this.pedido.getPedidoTipo().equals(PedidoTipo.DEV)) {
						if (clientePreenchido()) {
							this.pedido = this.pedidoDao.save(this.pedido);
						}else {
							throw new DevolucaoException(this.translate("devolucaoException.customer.not.informed"));
						}
					}else {
						this.pedido = this.pedidoDao.save(this.pedido);
					}



				}else {// altera��o de pedido
					if (!this.listaItemPedidoExcluir.isEmpty()) {
						for (ItemPedido itemExclui : this.listaItemPedidoExcluir) {
							this.itemPedidoDao.delete(itemExclui);
						}
						devolveParaEstoque(this.listaItemPedidoExcluir);
					}
					List<ItemPedido> listaTemp = new ArrayList<ItemPedido>();
					for (ItemPedido item : this.pedido.getListaItensPedido()) {
						if (item.getPedido() == null) {
							item.setPedido(this.pedido);
							listaTemp.add(item);
						}
					}
					if (listaTemp.size() > 0) {
						retiraDoEstoque(listaTemp);
					}
					this.listaItensPedido = this.pedido.getListaItensPedido();
					this.pedido.setAtendente(this.colaborador);
					this.pedido.setDestino(this.destino);
					this.pedido.setValorTotalPedido(this.totalPedido);
					this.pedido.setValorTotalProdutos(this.totalPedido.add(this.totalDesconto));
					this.pedido.setDesconto(this.totalDesconto);
					this.pedido.setObs(this.obs);
					if (this.dataPrevisao != null) {
						if (this.dataPrevisao.compareTo(LocalDate.now()) < 1 ) {
							this.pedido.setAtivaEncomenda(false);
						}else {
							this.pedido.setAtivaEncomenda(true);
							this.pedido.setPrevisaoEntrega(this.dataPrevisao);
						}
					}else {
						this.dataPrevisao = LocalDate.now();
						this.pedido.setAtivaEncomenda(false);
					}
					this.pedido = this.pedidoDao.save(this.pedido);
				}
				if(this.pedido.getTransacao().getTipoTransacao().equals(TipoTransacao.tra)) {
					return newTransfere();
				}else {
					if (this.pedido.getTransacao().getTipoTransacao().equals(TipoTransacao.dev)){
						return newDevolucao();
					}else {
						return newPedido();
					}
				}
			}else {
				this.addError(true,"pedidoItens.isEmpty");
				return null;
			}
		}catch (DevolucaoException dev) {
			this.addError(true, "caixa.error", dev.getMessage());
			return null;
		}catch (EstoqueException estoque) {
			this.addError(true, "caixa.error", estoque.getMessage());
			return null;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Retorna form de pesquisa de destinatario 
	 */

	public void telaResultadoDestinatario(){
		this.updateAndOpenDialog("listaResultadoDialog","dialogListaResultado");
	}
	
	/**
	 * Pesquisa Destinatario
	 */
	public void pesquisaDestinatario(){
		System.out.println(this.destinatario + "tipo pesquisa =: "+this.pedido.getTipoPesquisa());
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.CLI){
			System.out.println("fiz cli");
			this.listaCliente = this.clienteDao.localizaClientePorRazaoSocial(this.destinatario, getUsuarioAutenticado().getIdEmpresa());
			telaResultadoDestinatario();
		}
		
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.COL){
			System.out.println("fiz cli");
			this.listaColaborador = this.colaboradorDao.pesquisaTexto(this.destinatario, getUsuarioAutenticado().getIdEmpresa(), null);
			telaResultadoDestinatario();
		}
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.FIL){
			System.out.println("fiz fil");
			this.listaFilial = this.filialDao.pesquisaTexto(this.destinatario, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
			telaResultadoDestinatario();
		}
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.FOR){
			System.out.println("fiz for");
			this.listaFornecedor = this.fornecedorDao.pesquisaTexto(this.destinatario, getUsuarioAutenticado().getIdEmpresa(), null);
			telaResultadoDestinatario();
		}
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.MAT){
			System.out.println("fiz mat");
			this.empresa = this.empresaDao.findById(getUsuarioAutenticado().getIdEmpresa(), false);
			this.destino.setEmpresa(this.empresa);
//			this.razao = this.destino.getEmpresa().getRazaoSocial();
//			this.cnpj = this.destino.getEmpresa().getCnpj();
//			this.endereco = this.destino.getEmpresa().getEndereco().getEndereco().getLogradouro() +", "+ this.destino.getEmpresa().getEndereco().getNumero();
//			this.bairro = this.destino.getEmpresa().getEndereco().getEndereco().getBairro();
//			this.municipio = this.destino.getEmpresa().getEndereco().getEndereco().getLocalidade();
//			this.uf = this.destino.getEmpresa().getEndereco().getEndereco().getUf().name();
		}
	}
	
	/**
	 * Evento que controla o item da lista selecionado na tela de pesquisa
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectPesquisa(SelectEvent event)throws IOException{

		if (this.pedido.getTipoPesquisa() == TipoPesquisa.CLI){
			this.addInfo(true, "O Destinatario " + ((Cliente)event.getObject()).getRazaoSocial()+ " foi selecionado");
			this.cliente = ((Cliente) event.getObject());
			this.destino.setCliente(this.cliente);
			this.destino.setColaborador(null);
			this.destino.setFilial(null);
			this.destino.setEmpresa(null);
			this.destino.setFornecedor(null);
			this.destinatario = this.cliente.getRazaoSocial();
			this.pedido.setUfDestino(this.cliente.getEndereco().getEndereco().getUf());
			this.pedido.setNome(this.cliente.getRazaoSocial());
			// preenchendo os label da pedido
			this.razao = this.destino.getCliente().getRazaoSocial();
			if (this.destino.getCliente().getCnpj() == null){
				this.cnpj = this.destino.getCliente().getCpf();
				this.pedido.setIndFinal("1");
			}else{
				this.cnpj = this.destino.getCliente().getCnpj();
				if (this.destino.getCliente().getInscEstadual()== null){
					this.pedido.setIndFinal("1");
				}else{
					this.pedido.setIndFinal("0");
				}
			}
			if (this.destino.getCliente().getEndereco().getLogradouro() != "" || !this.destino.getCliente().getEndereco().getLogradouro().isEmpty()) {			
				this.endereco = this.destino.getCliente().getEndereco().getLogradouro() +", "+ this.destino.getCliente().getEndereco().getNumero();
				this.bairro = this.destino.getCliente().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getCliente().getEndereco().getEndereco().getLogra() +", "+ this.destino.getCliente().getEndereco().getNumero();
				this.bairro = this.destino.getCliente().getEndereco().getEndereco().getBairro();
			}
			this.municipio = this.destino.getCliente().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getCliente().getEndereco().getEndereco().getUf().name();
		}
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.FOR){
			this.addInfo(true, "O Destinatario " + ((Fornecedor)event.getObject()).getRazaoSocial()+ " foi selecionado");
			this.fornecedor = (Fornecedor) event.getObject();
			this.destino.setFornecedor(this.fornecedor);
			this.destino.setCliente(null);
			this.destino.setColaborador(null);
			this.destino.setFilial(null);
			this.destino.setEmpresa(null);
			this.destinatario = this.destino.getFornecedor().getRazaoSocial();
			this.pedido.setNome(this.fornecedor.getRazaoSocial());
			this.pedido.setUfDestino(this.fornecedor.getEndereco().getEndereco().getUf());
			// preenchendo os label da pedido
			this.razao = this.destino.getFornecedor().getRazaoSocial();
			if (this.destino.getFornecedor().getCnpj().isEmpty()){
				this.cnpj = this.destino.getFornecedor().getCpf();
				this.pedido.setIndFinal("1");
			}else{
				this.cnpj = this.destino.getFornecedor().getCnpj();
				if (this.destino.getFornecedor().getInscEstadual().isEmpty()){
					this.pedido.setIndFinal("1");
				}else{
					this.pedido.setIndFinal("0");
				}
			}
			if (this.destino.getFornecedor().getEndereco().getLogradouro() != "" || !this.destino.getFornecedor().getEndereco().getLogradouro().isEmpty()) {			
				this.endereco = this.destino.getFornecedor().getEndereco().getLogradouro() +", "+ this.destino.getFornecedor().getEndereco().getNumero();
				this.bairro = this.destino.getFornecedor().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getFornecedor().getEndereco().getEndereco().getLogra() +", "+ this.destino.getFornecedor().getEndereco().getNumero();
				this.bairro = this.destino.getFornecedor().getEndereco().getEndereco().getBairro();
			}
			this.municipio = this.destino.getFornecedor().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getFornecedor().getEndereco().getEndereco().getUf().name();
		}
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.FIL){
			this.addInfo(true, "O Destinatario " + ((Filial)event.getObject()).getRazaoSocial()+ " foi selecionado");
			this.destino.setFilial((Filial) event.getObject());
			this.destino.setCliente(null);
			this.destino.setColaborador(null);
			this.destino.setEmpresa(null);
			this.destino.setFornecedor(null);
			this.destinatario = this.destino.getFilial().getRazaoSocial();
			this.pedido.setNome(this.filial.getRazaoSocial());
			this.pedido.setUfDestino(((Filial) event.getObject()).getEndereco().getEndereco().getUf());
			// preenchendo os label da pedido
			this.razao = this.destino.getFilial().getRazaoSocial();
			this.cnpj = this.destino.getFilial().getCnpj();
			
			if (this.destino.getFilial().getEndereco().getLogradouro() != "" || !this.destino.getFilial().getEndereco().getLogradouro().isEmpty()) {			
				this.endereco = this.destino.getFilial().getEndereco().getLogradouro() +", "+ this.destino.getFilial().getEndereco().getNumero();
				this.bairro = this.destino.getFilial().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getFilial().getEndereco().getEndereco().getLogra() +", "+ this.destino.getFilial().getEndereco().getNumero();
				this.bairro = this.destino.getFilial().getEndereco().getEndereco().getBairro();
			}
			this.municipio = this.destino.getFilial().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getFilial().getEndereco().getEndereco().getUf().name();
			this.pedido.setIndFinal("0");
		}
		if (this.pedido.getTipoPesquisa() == TipoPesquisa.COL) {// Colaborador
			this.addInfo(true, "O Destinatario " + ((Colaborador)event.getObject()).getNome()+ " foi selecionado");
			this.destino.setCliente(null);
			this.destino.setColaborador((Colaborador)event.getObject());
			this.destino.setFilial(null);
			this.destino.setEmpresa(null);
			this.destino.setFornecedor(null);
			this.destinatario = this.destino.getColaborador().getNome();
			
			this.pedido.setUfDestino(this.destino.getColaborador().getEndereco().getEndereco().getUf());
			this.pedido.setNome(this.destino.getColaborador().getNome());
			// preenchendo os label da pedido
			this.razao = this.destino.getColaborador().getNome();
			this.cnpj = this.destino.getColaborador().getCpf();
			this.pedido.setIndFinal("1");
			
			if (this.destino.getColaborador().getEndereco().getLogradouro() != "" || !this.destino.getColaborador().getEndereco().getLogradouro().isEmpty()) {			
				this.endereco = this.destino.getColaborador().getEndereco().getLogradouro() +", "+ this.destino.getColaborador().getEndereco().getNumero();
				this.bairro = this.destino.getColaborador().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getColaborador().getEndereco().getEndereco().getLogra() +", "+ this.destino.getColaborador().getEndereco().getNumero();
				this.bairro = this.destino.getColaborador().getEndereco().getEndereco().getBairro();
			}
			this.municipio = this.destino.getColaborador().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getColaborador().getEndereco().getEndereco().getUf().name();
		}
		System.out.println("Estou o RowSelectPesquisa");
		this.pedido.setDestino(this.destino);

	}
	
	public TipoPesquisa[] getListaTipoPesquisa(){
		return TipoPesquisa.values();
	}
	
	@ToString
	@EqualsAndHashCode
	public static class Destino{
		
		@Getter
		@Setter
		private String nome;
		
		@Getter
		@Setter
		private String documento;
		
	}
	
	public Destino pegaNomeCliente(Pedido ped) {
		Destino resultado = new Destino();
		if (ped.getDestino() != null) {
			if (ped.getDestino().getCliente() != null) {
				resultado.setNome(ped.getDestino().getCliente().getRazaoSocial());
				if (ped.getDestino().getCliente().getCnpj() == null) {
					resultado.setDocumento(ped.getDestino().getCliente().getCpf());
				}else {
					resultado.setDocumento(ped.getDestino().getCliente().getCnpj());
				}
			}
			if (ped.getDestino().getColaborador() != null) {
				resultado.setNome(ped.getDestino().getColaborador().getNome());
				resultado.setDocumento(ped.getDestino().getColaborador().getCpf());
			}
			if (ped.getDestino().getFornecedor() != null) {
				resultado.setNome(ped.getDestino().getFornecedor().getRazaoSocial());
				if ( ped.getDestino().getFornecedor().getCnpj() == null) {
					resultado.setDocumento(ped.getDestino().getFornecedor().getCpf());
				}else {
					resultado.setDocumento(ped.getDestino().getFornecedor().getCnpj());
				}
			}
			if (ped.getDestino().getFilial() != null) {
				resultado.setNome( ped.getDestino().getFilial().getRazaoSocial());
				resultado.setDocumento(ped.getDestino().getFilial().getCnpj());
			}
			if (ped.getDestino().getEmpresa() != null) {
				resultado.setNome(ped.getDestino().getEmpresa().getRazaoSocial());
				resultado.setDocumento(ped.getDestino().getEmpresa().getCnpj());
			}
		}
		return resultado;
	}
	
	@Transactional
	public String salvaPedidosPDFA4() throws RelatoriosException, HibernateException, EstoqueException, DevolucaoException {
			String retorno = salvaPedido();
			if (retorno == null) {
				throw new RelatoriosException("N�o foi possivel salvar o pedido, erro nos itens");
			}
			return retorno;
	}
	public void geraPDF4Cliente(Pedido lista) {
		this.pedido = this.pedidoDao.pegaPedidoPorId(lista.getId());
		criaPDFA4();
	}
	
	public void criaPDFACupom() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			List<CupomVendaDTO> itensTransf = new ArrayList<>();
			List<ItemPedido> itens = this.pedido.getListaItensPedido();
			int i = 1 ;
			BigDecimal totalItens = new BigDecimal("0");
			for (Iterator<ItemPedido> iterator = itens.iterator(); iterator.hasNext();) {
				System.out.println("Dentro do interator");
				ItemPedido itemOriginal =  iterator.next();
				CupomVendaDTO itemNovo = new CupomVendaDTO();
				// convertendo o item do pedido para o DTO 
				itemNovo.setItem(i++);
				if (itemOriginal.getRef() != null) {
					itemNovo.setRef(itemOriginal.getRef());
				}else {
					throw new RelatoriosException("Produto sem referencia");
				}
				if (itemOriginal.getProduto().getDescricao() != null) {
					itemNovo.setDesc(itemOriginal.getProduto().getDescricao());
				}else {
					throw new RelatoriosException("Produto sem descri��o");
				}
				if (itemOriginal.getBarras() != null) {
					if (itemOriginal.getBarras().getTamanho() != null) {
						itemNovo.setTamanho(itemOriginal.getBarras().getTamanho().getTamanho());
					}
					if (itemOriginal.getBarras().getCor() != null) {
						itemNovo.setCor(itemOriginal.getBarras().getCor().getNome());
					}
				}else {
					itemNovo.setTamanho("");
					itemNovo.setCor("");
				}
				itemNovo.setQuantidade(itemOriginal.getQuantidade());
				if (itemOriginal.getProduto().getTipoMedida() != null) {
					itemNovo.setUnidade(itemOriginal.getProduto().getTipoMedida().getSigla());
				}else {
					itemNovo.setUnidade(TipoMedida.UN.getSigla());
				}
				itemNovo.setVlUnitario(itemOriginal.getValorUnitario());
				itemNovo.setValorTotal(itemOriginal.getValorTotal());
				totalItens = totalItens.add(itemOriginal.getQuantidade());
				itensTransf.add(itemNovo); // adicionado o item convertido a listagem
			}
			Destino destino = new Destino();
			destino = pegaNomeCliente(this.pedido);
			String data = this.pedido.getDataEmissao().format(formatter);
			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (itensTransf.size() > 0) {
				parametros =  geraParametros();
				parametros.put("empresa",razaoEmpresaLogada());
				parametros.put("emailEmp",emailEmpresaLogada());
				parametros.put("cli", destino.getNome() );
				if (destino.getDocumento() != null) {
					parametros.put("doc", destino.getDocumento());
				}else {
					parametros.put("doc", "nulo");
				}
				if (this.pedido.getAtendente().getApelido() != null || this.pedido.getAtendente().getApelido() != "") {
					parametros.put("colab", this.pedido.getAtendente().getApelido());
				}else {
					parametros.put("colab", this.pedido.getAtendente().getNome());
				}
				parametros.put("trans", this.pedido.getTransacao().getDescricao());
				parametros.put("pag", this.pedido.getPagamento().getDescricao());
				parametros.put("control", this.pedido.getControle().getControle());
				parametros.put("id", this.pedido.getControle().getId());
				parametros.put("tot", this.pedido.getValorTotalPedido());
				parametros.put("desc", this.pedido.getDesconto());
				parametros.put("pec", totalItens);
				parametros.put("men", this.getUsuarioAutenticado().getConfig().getMensPDV());
				parametros.put("data", data);
				
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(itensTransf,false);
				String path ="/WEB-INF/Relatorios/Vendas/RelCupomPDV.jrxml";
				relatorios.visualizaPDF(path, parametros, "Proposta"+this.pedido.getId(),jrBean);
					
				System.out.println("Teste ibr dois");
			}else {
				System.out.println("Lista vazia!");
				throw new RelatoriosException("Lista Vazia!");
			}
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch(RelatoriosException r) {
			this.addError(true, "caixa.error", r.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void criaPDFA4() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			List<CupomVendaDTO> itensTransf = new ArrayList<>();
			List<ItemPedido> itens = this.pedido.getListaItensPedido();
			int i = 1 ;
			BigDecimal totalItens = new BigDecimal("0");
			for (Iterator<ItemPedido> iterator = itens.iterator(); iterator.hasNext();) {
				System.out.println("Dentro do interator");
				ItemPedido itemOriginal =  iterator.next();
				CupomVendaDTO itemNovo = new CupomVendaDTO();
				// convertendo o item do pedido para o DTO 
				itemNovo.setItem(i++);
				if (itemOriginal.getRef() != null) {
					itemNovo.setRef(itemOriginal.getRef());
				}else {
					throw new RelatoriosException("Produto sem referência");
				}
				if (itemOriginal.getProduto().getDescricao() != null) {
					itemNovo.setDesc(itemOriginal.getProduto().getDescricao());
				}else {
					throw new RelatoriosException("Produto sem descrição");
				}
				if (itemOriginal.getBarras() != null) {
					if (itemOriginal.getBarras().getTamanho() != null) {
						itemNovo.setTamanho(itemOriginal.getBarras().getTamanho().getTamanho());
					}
					if (itemOriginal.getBarras().getCor() != null) {
						itemNovo.setCor(itemOriginal.getBarras().getCor().getNome());
					}
				}else {
					itemNovo.setTamanho("");
					itemNovo.setCor("");
				}
				itemNovo.setQuantidade(itemOriginal.getQuantidade());
				if (itemOriginal.getProduto().getTipoMedida() != null) {
					itemNovo.setUnidade(itemOriginal.getProduto().getTipoMedida().getSigla());
				}else {
					itemNovo.setUnidade(TipoMedida.UN.getSigla());
				}
				itemNovo.setVlUnitario(itemOriginal.getValorUnitario());
				itemNovo.setValorTotal(itemOriginal.getValorTotal());
				totalItens = totalItens.add(itemOriginal.getQuantidade());
				itensTransf.add(itemNovo); // adicionado o item convertido a listagem
			}
			Destino destino = new Destino();
			destino = pegaNomeCliente(this.pedido);
			String data = this.pedido.getDataEmissao().format(formatter);
			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (itensTransf.size() > 0) {
				parametros =  geraParametros();
				parametros.put("empresa",razaoEmpresaLogada());
				parametros.put("emailEmp",emailEmpresaLogada());
				parametros.put("cli", destino.getNome() );
				if (destino.getDocumento() != null) {
					parametros.put("doc", destino.getDocumento());
				}else {
					parametros.put("doc", "nulo");
				}
				if (this.pedido.getAtendente().getApelido() != null || this.pedido.getAtendente().getApelido() != "") {
					parametros.put("colab", this.pedido.getAtendente().getApelido());
				}else {
					parametros.put("colab", this.pedido.getAtendente().getNome());
				}
				parametros.put("trans", this.pedido.getTransacao().getDescricao());
				if (this.pedido.getPagamento() != null) {
					parametros.put("pag", this.pedido.getPagamento().getDescricao());
				}
				parametros.put("control", this.pedido.getControle().getControle());
				parametros.put("id", this.pedido.getControle().getId());
				parametros.put("tot", this.pedido.getValorTotalPedido());
				parametros.put("desc", this.pedido.getDesconto());
				parametros.put("pec", totalItens);
				parametros.put("men", this.getUsuarioAutenticado().getConfig().getMensPDV());
				parametros.put("data", data);
				
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(itensTransf,false);
				String path ="/WEB-INF/Relatorios/Vendas/RelCupomPDVA4.jrxml";
				relatorios.visualizaPDF(path, parametros, "Proposta"+this.pedido.getId(),jrBean);
					
				System.out.println("Teste ibr dois");
			}else {
				System.out.println("Lista vazia!");
				throw new RelatoriosException("Lista Vazia!");
			}
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch(RelatoriosException r) {
			this.addError(true, "caixa.error", r.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// apenas gera o PDF
	public void geraPDFA4() {
		try {
			this.pedido = this.pedidoDao.pegaPedidoPorId(this.pedido.getId());
			criaPDFA4();
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Guarded("salvarPdfCupom")
	@Transactional
	public void imiprimirPDFCupom() {
		try {
			salvaPedidosPDFA4();
			criaPDFACupom();
//			return resposta;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
//			return null;
		}catch(RelatoriosException r) {
			this.addError(true, "caixa.error", r.getMessage());
//			return null;
		} catch (Exception e) {
			e.printStackTrace();
//			return null;
		}
	}
	@Guarded("salvarPrintPDF")
	@Transactional
	public void imiprimirPDF() {
		try {
			salvaPedidosPDFA4();
			criaPDFA4();
			
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
			
	public void reimprimiVenda() throws IOException {
		Pedido pedidoTemp = new Pedido();
//		FormaDePagamento pag = new FormaDePagamento(); 
		pedidoTemp = this.pedidoDao.pegaPedidoPorId(this.pedido.getId());
//		pag = (formaPagDao.findById(this.pedido.getPagamento().getId(), false));
//		pedidoTemp.setPagamento(pag);
		impressora.imprimirReimpressao(pedidoTemp,true,this.getUsuarioAutenticado().getConfig());
	}
	
	@Transactional
	public void doExcluir() {
		try {
			this.pedido =  this.pedidoDao.pegaPedidoPorId(this.pedido.getId());
			if (this.pedido.getPedidoTipo().equals(PedidoTipo.DEV)) {
				this.pedido.setPedidoStatus(PedidoStatus.CAN);
				retiraDoEstoque(this.pedido.getListaItensPedido());
			}else {
				this.pedido.setPedidoStatus(PedidoStatus.CAN);
				devolveParaEstoque(this.pedido.getListaItensPedido());
			}
			this.pedidoDao.save(this.pedido);
		}catch (EstoqueException estoque) {
			this.addError(true, "Não foi possivel baixar o estoque, motivo: ", estoque.getMessage());
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
		
	}
	
	public Map<String, Object> geraParametros(){
		Map<String, Object> parametros =  new HashMap<String,Object>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String dataIni = this.dataInicial.format(formatter);
		String dataFim = this.dataFinal.format(formatter);
		parametros.put("dataIni", dataIni);
		parametros.put("dataFim", dataFim);
		return parametros;
	}
	
	public void geraVendidosFabricanteDepartamento() throws JRException, IOException, RelatoriosException{
		try {
			List<RelVendasFabricanteDTO> data = this.pedidoDao.maisVendidosPorFabricante(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.PVE,PedidoStatus.REC );
			List<RelVendasFabricanteDTO> dataDevolvidos = this.pedidoDao.maisVendidosPorFabricante(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.DEV,PedidoStatus.REC );
			BigDecimal totalPer = new BigDecimal("0");
			BigDecimal totVendido = new BigDecimal("0");
			for (RelVendasFabricanteDTO dev : dataDevolvidos) {
				System.out.println("Lista Devolvidos");
				System.out.println(dev.toString());
			}
			for (RelVendasFabricanteDTO ven : data) {
				System.out.println("Lista Vendidos");
				System.out.println(ven.toString());
			}
//			boolean encontrado = false;
			// recalculando listagem lenvado em consideraçao devolução de produtos
			if (data.size()>0) {
				totalPer = data.get(0).getTotalPecas();
				totVendido= data.get(0).getTotalVendido();
				if (dataDevolvidos.size() >0) {
					totalPer= totalPer.subtract(dataDevolvidos.get(0).getTotalPecas());
					totVendido = totVendido.subtract(dataDevolvidos.get(0).getTotalVendido());
					for (Iterator<RelVendasFabricanteDTO> iterator = dataDevolvidos.iterator(); iterator.hasNext();) {
						System.out.println("Dentro do interator");
						RelVendasFabricanteDTO devolvidos =  iterator.next();
//						encontrado=false
						for (RelVendasFabricanteDTO vendas : data) {
							if (devolvidos.getIdDep() != null && devolvidos.getIdFab() != null) {
								if (vendas.getIdDep() != null && vendas.getIdFab() != null) { 
									if ( (devolvidos.getIdFab().compareTo(vendas.getIdFab())==0) && (devolvidos.getIdDep().compareTo(vendas.getIdDep())==0) ) {
										vendas.setQuantidade(vendas.getQuantidade().subtract(devolvidos.getQuantidade()));
										vendas.setTotalPecas(vendas.getTotalPecas().subtract(devolvidos.getTotalPecas()));
										vendas.setTotalValor(vendas.getTotalValor().subtract(devolvidos.getTotalValor()));
										vendas.setValorMedio(vendas.getTotalValor().divide(vendas.getQuantidade(),mc).setScale(2,RoundingMode.HALF_EVEN));
										vendas.setPart((vendas.getQuantidade().divide(totalPer,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
										vendas.setPartVenda((vendas.getTotalValor().divide(totVendido,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
									}
								}
							}
							if (devolvidos.getIdDep() == null && devolvidos.getIdFab() != null) {
								if (vendas.getIdDep() == null && vendas.getIdFab() != null) { 
									if ( devolvidos.getIdFab().compareTo(vendas.getIdFab()) == 0)  {
										vendas.setQuantidade(vendas.getQuantidade().subtract(devolvidos.getQuantidade()));
										vendas.setTotalPecas(vendas.getTotalPecas().subtract(devolvidos.getTotalPecas()));
										vendas.setTotalValor(vendas.getTotalValor().subtract(devolvidos.getTotalValor()));
										vendas.setValorMedio(vendas.getTotalValor().divide(vendas.getQuantidade(),mc).setScale(2,RoundingMode.HALF_EVEN));
										vendas.setPart((vendas.getQuantidade().divide(totalPer,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
										vendas.setPartVenda((vendas.getTotalValor().divide(totVendido,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
									}
								}
							}
							if (devolvidos.getIdDep() != null && devolvidos.getIdFab() == null) {
								if (vendas.getIdDep() != null && vendas.getIdFab() == null) {
									if ( (devolvidos.getIdDep().compareTo(vendas.getIdDep())==0) ) {
										vendas.setQuantidade(vendas.getQuantidade().subtract(devolvidos.getQuantidade()));
										vendas.setTotalPecas(vendas.getTotalPecas().subtract(devolvidos.getTotalPecas()));
										vendas.setTotalValor(vendas.getTotalValor().subtract(devolvidos.getTotalValor()));
										vendas.setValorMedio(vendas.getTotalValor().divide(vendas.getQuantidade(),mc).setScale(2,RoundingMode.HALF_EVEN));
										vendas.setPart((vendas.getQuantidade().divide(totalPer,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
										vendas.setPartVenda((vendas.getTotalValor().divide(totVendido,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
									}
								}
							}
							if (devolvidos.getIdDep() == null && devolvidos.getIdFab() == null) {
								if (vendas.getIdDep() == null && vendas.getIdFab() == null) {
									vendas.setQuantidade(vendas.getQuantidade().subtract(devolvidos.getQuantidade()));
									vendas.setTotalPecas(vendas.getTotalPecas().subtract(devolvidos.getTotalPecas()));
									vendas.setTotalValor(vendas.getTotalValor().subtract(devolvidos.getTotalValor()));
									vendas.setValorMedio(vendas.getTotalValor().divide(vendas.getQuantidade(),mc).setScale(2,RoundingMode.HALF_EVEN));
									vendas.setPart((vendas.getQuantidade().divide(totalPer,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
									vendas.setPartVenda((vendas.getTotalValor().divide(totVendido,mc)).multiply(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN));
								}
							}
						}
					}
				}
			}else {
				System.out.println("Lista vazia!");
				throw new RelatoriosException("Lista Vazia!");
			}
			
			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (data.size() > 0) {
				parametros =  geraParametros();
				parametros.put("empresa",razaoEmpresaLogada());
				parametros.put("totalPecas", totalPer);
				parametros.put("totalVendido", totVendido);
				
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(data,false);
				
				String path ="/WEB-INF/Relatorios/Vendas/RelVendasFabricanteDepartamentoM.jrxml";
				relatorios.visualizaPDF(path, parametros, "VendasFabricanteDepartamento",jrBean);
					
				System.out.println("Teste ibr dois");
			}else {
				System.out.println("Lista vazia!");
				throw new RelatoriosException("Lista Vazia!");
			}
		}catch(RelatoriosException r) {
			this.addError(true, "caixa.error", r.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String razaoEmpresaLogada() {
		if (pegaIdFilial() != null) {
			return this.configEmpUser().getFil().getRazaoSocial();
		}else {
			return this.configEmpUser().getEmp().getRazaoSocial();
		}
	}
	
	public String foneEmpresaLogada() {
		String telefone = "";
		if (pegaIdFilial() != null) {
			telefone = "("+this.configEmpUser().getFil().getContato().get(0).getFone().get(0).getDdd()+") "+ this.configEmpUser().getFil().getContato().get(0).getFone().get(0).getFone() + " ";
		}else {
			telefone = "("+this.configEmpUser().getEmp().getContato().get(0).getFone().get(0).getDdd()+") "+ this.configEmpUser().getEmp().getContato().get(0).getFone().get(0).getFone() + " ";
		}
		return telefone;
	}
	
	public String emailEmpresaLogada() {
		String email = "";
		if (pegaIdFilial() != null) {
			email =  this.configEmpUser().getFil().getEmailNFE().getEmail();
		}else {
			email =  this.configEmpUser().getEmp().getEmailNFE().getEmail();
		}
		return email;
	}
	
		public void geraMaisVendidosJasper() throws JRException, IOException, RelatoriosException{
			try {
				List<RelatorioVendasDTO> data = this.pedidoDao.maisVendidos(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.PVE,PedidoStatus.REC );
				List<RelatorioVendasDTO> dataDevolvidos = this.pedidoDao.maisVendidos(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.DEV,PedidoStatus.REC );
				List<RelatorioVendasDTO> dataFinalDevolvidos = new ArrayList<RelatorioVendasDTO>();
				BigDecimal totalPer = new BigDecimal("0"); 
				BigDecimal totalDev = new BigDecimal("0");
				BigDecimal totPec = new BigDecimal("0");
				BigDecimal totalResultado = new BigDecimal("0");
				boolean encontrado = false;
				if (data.size()>0) {
					totalPer = new BigDecimal(data.get(0).getTotalPeriodo().toString());
					if (dataDevolvidos.size()>0) {
						totalDev = new BigDecimal(dataDevolvidos.get(0).getTotalPeriodo().toString());
						totPec =  data.get(0).getTotalPecas().subtract(dataDevolvidos.get(0).getTotalPecas());
						totalResultado = totalPer.subtract(totalDev);
					}else {
						totPec = data.get(0).getTotalPecas(); 
						totalResultado = totalPer;
					}
					
					for (Iterator<RelatorioVendasDTO> iterator = dataDevolvidos.iterator(); iterator.hasNext();) {
						System.out.println("Dentro do interator");
						RelatorioVendasDTO devolvidos =  iterator.next();
						encontrado=false;
						// localiza se o produto devolvido existe na listagem de produtos vendidos caso encontrado 
						// recalcula os campos e o adciona a uma nova lista preenchida
						for (RelatorioVendasDTO vendidos : data) {
							System.out.println("barras id " + vendidos.getBarras() );
							System.out.println("barras id " + devolvidos.getBarras() );
							if (devolvidos.getBarras().compareTo(vendidos.getBarras())==0) {
								System.out.println("barras id == " + vendidos.getBarras() + " - " + devolvidos.getBarras() );
								encontrado = true;
								BigDecimal medio = new BigDecimal("0");
								vendidos.setQuant(vendidos.getQuant().subtract(devolvidos.getQuant()));
								vendidos.setTotal(vendidos.getTotal().subtract(devolvidos.getTotal()));
								System.out.println("quantidade: " + vendidos.getQuant());
								System.out.println("total: " + vendidos.getTotal());
								if (vendidos.getTotal().compareTo(new BigDecimal("0"))== 0 || vendidos.getQuant().compareTo(new BigDecimal("0"))==0) {
									medio = new BigDecimal("0");
								}else {
									medio = vendidos.getTotal().divide(vendidos.getQuant(),mc).setScale(2,RoundingMode.HALF_EVEN);
								}
								vendidos.setVl_Med_Un(medio);
							}
						}
						if (encontrado == false) {
							RelatorioVendasDTO devolvidoCorrigido = new RelatorioVendasDTO();
							devolvidoCorrigido = devolvidos;
							devolvidoCorrigido.setQuant(devolvidos.getQuant().multiply(new BigDecimal("-1")).setScale(2,RoundingMode.HALF_EVEN));
							devolvidoCorrigido.setVl_Med_Un(devolvidos.getVl_Med_Un().multiply(new BigDecimal("-1")).setScale(2,RoundingMode.HALF_EVEN));
							devolvidoCorrigido.setTotal(devolvidos.getTotal().multiply(new BigDecimal("-1")).setScale(2,RoundingMode.HALF_EVEN));						
							dataFinalDevolvidos.add(devolvidoCorrigido);
						}
					}
					data.addAll(dataFinalDevolvidos);
				}else {
					System.out.println("Lista vazia!");
					throw new RelatoriosException("Lista Vazia!");
				}
				Map<String, Object> parametros =  new HashMap<String,Object>();
				if (data.size() > 0) {
					parametros =  geraParametros();
					parametros.put("empresaID", data.get(0).getMatriz());
					parametros.put("totalPecas", totPec);
					parametros.put("totalPeriodo", totalResultado);
					parametros.put("filialID", data.get(0).getFilial());
					JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(data,false);
					if (pegaIdFilial() != null) {
						String path ="/WEB-INF/Relatorios/Vendas/RelVendidosFilial.jrxml";
						relatorios.visualizaPDF(path, parametros, "MaisVendidosFilial",jrBean);
					}else {
						String path = "/WEB-INF/Relatorios/Vendas/RelVendidosMatriz.jrxml";
						relatorios.visualizaPDF(path, parametros, "MaisVendidosMatriz",jrBean);
					}
					System.out.println("Teste ibr dois");
				}else {
					System.out.println("Lista vazia!");
					throw new RelatoriosException("Lista Vazia!");
				}
			}catch(RelatoriosException r) {
				this.addError(true, "caixa.error", r.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	public void geraVendasColaborador() {
		try {
			List<RelComissaoColaboradoresDTO> data = new ArrayList<>();
			List<RelComissaoColaboradoresDTO> dataDevolvidos = new ArrayList<>();
			if (this.filtroNome == null || this.filtroNome == "") {
				data = this.pedidoDao.comissaoColaboradores(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.PVE,PedidoStatus.REC ,null);
				dataDevolvidos = this.pedidoDao.comissaoColaboradores(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.DEV,PedidoStatus.REC,null );
			}else {
				data = this.pedidoDao.comissaoColaboradores(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.PVE,PedidoStatus.REC,this.filtroNome );
				dataDevolvidos = this.pedidoDao.comissaoColaboradores(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.DEV,PedidoStatus.REC,this.filtroNome );
			}
//			List<RelComissaoColaboradoresDTO> dataFinalDevolvidos = new ArrayList<RelComissaoColaboradoresDTO>();
			
			boolean encontrado = false;
			// Gerando relatorio deduzindo a devolução da comissao.
			if (data.size()>0) {
				if (dataDevolvidos.size()>0) {
					for (Iterator<RelComissaoColaboradoresDTO> iterator = dataDevolvidos.iterator(); iterator.hasNext();) {
						System.out.println("Dentro do interator");
						RelComissaoColaboradoresDTO devolvidos =  iterator.next();
						encontrado = false;
						for (RelComissaoColaboradoresDTO vendas : data) {
							if (devolvidos.getId().compareTo(vendas.getId())==0) {
								encontrado = true;
								vendas.setTotal(vendas.getTotal().subtract(devolvidos.getTotal()));
							}
						}
						if (encontrado == false) {
							devolvidos.setTotal(devolvidos.getTotal().multiply(new BigDecimal("-1"),mc).setScale(2,RoundingMode.HALF_EVEN));
							data.add(devolvidos);							
						}
					}
				}
			}else {
				System.out.println("Lista vazia!");
				throw new RelatoriosException("Lista Vazia!");
			}
			
			
			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (data.size() > 0) {
				parametros =  geraParametros();
				parametros.put("empresaID", data.get(0).getMatriz());
				parametros.put("filialID", data.get(0).getFilial());
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(data,false);
					String path = "/WEB-INF/Relatorios/Vendas/RelVendasVendedorMatriz.jrxml";
					relatorios.visualizaPDF(path, parametros, "VendaColaboradorMatriz",jrBean);
			}else {
				throw new RelatoriosException("Lista Vazia!");
			}
		}catch(RelatoriosException r) {
			this.addError(true, "caixa.error", r.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void geraVendasProgramadas() {
		try {
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			RelatorioEncomendaPedidos relEncomendas = new RelatorioEncomendaPedidos();
			List<RelatorioEncomendaPedidos> data = new ArrayList<>();
			List<Pedido> listapedido = new ArrayList<>();
			listapedido = this.pedidoDao.listaPedidoProgramados(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),PedidoTipo.PVE,PedidoStatus.AgR );
			System.out.println("Tamanho da lista TypeQuery = " + listapedido.size());
			for (Pedido ped : listapedido) {
				relEncomendas.setId(ped.getControle().getId());
				relEncomendas.setControle(ped.getControle().getControle().toString());
				relEncomendas.setEmissao(ped.getDataEmissao().format(formato));
				relEncomendas.setNome(ped.getDestino().nome());
				relEncomendas.setPrevisto(ped.getPrevisaoEntrega().format(formato));
				relEncomendas.setTotal(ped.getValorTotalPedido());
				data.add(relEncomendas);
				relEncomendas = new RelatorioEncomendaPedidos();
			}
			Empresa emp = new Empresa();
			Filial fil = new Filial();
			emp = this.empresaUsuario.getEmp();
			if (pegaIdFilial() != null) {
				fil = this.empresaUsuario.getFil();
			}
			
			
			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (data.size() > 0) {
				System.out.println("relatorio ");
				parametros =  geraParametros();
				parametros.put("empresaID", emp.getRazaoSocial());
				parametros.put("filialID", fil.getRazaoSocial());
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(data,false);
					String path = "/WEB-INF/Relatorios/Vendas/RelVendasProgramadas.jrxml";
					relatorios.visualizaPDF(path, parametros, "Programacao_Pedidos",jrBean);
			}else {
				throw new RelatoriosException("Lista Vazia!");
			}
		}catch(RelatoriosException r) {
			this.addError(true, "caixa.error", r.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean clientePreenchido() {
		boolean resposta = false;
		if (this.destino.getCliente() != null) {
			resposta = true;
		}
		if (this.destino.getFornecedor()!= null) {
			resposta = true;
		}
		if (this.destino.getColaborador()!= null) {
			resposta = true;
		}
		return resposta;
	}
	
}
