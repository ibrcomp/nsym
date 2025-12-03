package br.com.nsym.application.controller.nfe;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.hibernate.HibernateException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.NfeException;
import br.com.ibrcomp.exception.RegraNegocioException;
import br.com.ibrcomp.exception.RelatoriosException;
import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.application.controller.nfe.tools.CalculaTributos;
import br.com.nsym.application.controller.nfe.tools.FormulasDosImpostos;
import br.com.nsym.application.controller.nfe.tools.NumeroSemUtilizacaoNFe;
import br.com.nsym.application.controller.nfe.tools.TipoOperacao;
import br.com.nsym.application.controller.relatorios.RelatorioVendas;
import br.com.nsym.domain.misc.CpfCnpjUtils;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.misc.LocalizaRegex;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.entity.estoque.Estoque;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.ParcelaStatus;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoLancamento;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.financeiro.tools.TabelaPreco;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.dto.RelNatOperacaoDTO;
import br.com.nsym.domain.model.entity.fiscal.nfe.CartaCorrecao;
import br.com.nsym.domain.model.entity.fiscal.nfe.DI;
import br.com.nsym.domain.model.entity.fiscal.nfe.Destinatario;
import br.com.nsym.domain.model.entity.fiscal.nfe.Emitente;
import br.com.nsym.domain.model.entity.fiscal.nfe.II;
import br.com.nsym.domain.model.entity.fiscal.nfe.Inutilizacao;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.Lacre;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeReferenciada;
import br.com.nsym.domain.model.entity.fiscal.nfe.Transportador;
import br.com.nsym.domain.model.entity.fiscal.tools.AliqEstado;
import br.com.nsym.domain.model.entity.fiscal.tools.FinalidadeNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.FiscalStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.entity.tools.TipoFrete;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.ClienteRepository;
import br.com.nsym.domain.model.repository.cadastro.ColaboradorRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.cadastro.NumeroSemUtilizacaoNFeRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.cadastro.TransportadoraRepository;
import br.com.nsym.domain.model.repository.financeiro.CustoProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.FormaDePagementoRepository;
import br.com.nsym.domain.model.repository.financeiro.tools.ParcelasNfeRepository;
import br.com.nsym.domain.model.repository.fiscal.DIRepository;
import br.com.nsym.domain.model.repository.fiscal.IiRepository;
import br.com.nsym.domain.model.repository.fiscal.NcmEstoqueRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.CartaCorrecaoRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.InutilizacaoRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.ItemNfeRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.LacreRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.NfeReferenciadaRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.NfeRepository;
import br.com.nsym.domain.model.repository.tools.ConfigurationRepository;
import br.com.nsym.domain.model.repository.venda.AgPedidoRepository;
import br.com.nsym.domain.model.repository.venda.PedidoRepository;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class NfeBean extends AbstractBeanEmpDS<Nfe>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private FormulasDosImpostos calcula;


	@Inject
	private AcbrComunica acbr;

	@Getter
	@Setter
	private DadosDeConexaoSocket infConexao;

	@Getter
	@Setter
	private String respostaAcbrLocal;

	@Inject
	private NumeroSemUtilizacaoNFeRepository numeroDao;

	@Getter
	@Setter
	private NumeroSemUtilizacaoNFe numeroNFe = new NumeroSemUtilizacaoNFe();

	@Getter
	@Setter
	private Nfe nfe = new Nfe();

	@Getter
	@Setter
	private II ii = new II();

	@Inject
	private IiRepository iiDao;

	@Inject
	private NfeRepository nfeDao;

	@Getter
	@Setter
	private Destinatario destino  = new Destinatario();

	@Getter
	@Setter
	private Cliente cliente ;

	@Getter
	@Setter
	private Fornecedor fornecedor;

	@Getter
	@Setter
	private Transportadora transporte = new Transportadora();

	@Getter
	@Setter
	private Transportador transp = new Transportador();

	@Inject
	private TransportadoraRepository transporteDao;

	@Getter
	private List<Transportadora> listaTransporte = new ArrayList<>();

	@Getter
	@Setter
	private Empresa empresa;

	@Getter
	@Setter
	private Filial filial;

	@Getter
	@Setter
	private Colaborador colaborador;

	@Inject
	private ColaboradorRepository colaboradorDao;

	@Getter
	@Setter
	private String destinatario;

	@Getter
	@Setter
	private AliqEstado alicotaEtado;

	@Inject
	private ClienteRepository clienteDao;

	@Getter
	private List<Cliente> listaCliente = new ArrayList<>();

	@Getter	
	private List<Filial> listaFilial = new ArrayList<>();

	@Getter
	@Setter
	private List<ItemNfe> listaItensPersistidosExcluir = new ArrayList<>();

	@Inject
	private FilialRepository filialDao;

	@Getter
	private List<Empresa> listaEmpresa = new ArrayList<>();

	@Inject
	private TributosRepository natOperacaoDao;

	@Inject
	private EmpresaRepository empresaDao;

	@Getter
	private List<Fornecedor> listaFornecedor = new ArrayList<>();

	@Inject
	private FornecedorRepository fornecedorDao;

	@Getter
	private AbstractLazyModel<Nfe> nfeModel;

	@Getter
	@Setter
	private Produto produto;

	@Inject
	private ProdutoRepository produtoDao;

	@Getter
	private AbstractLazyModel<Produto> produtoModel;

	@Getter
	@Setter
	private ProdutoCusto custoProduto;

	@Inject
	private CustoProdutoRepository custoDao;

	@Getter
	@Setter
	private ItemNfe itemNfe = new ItemNfe();

	@Inject
	private LocalizaRegex localiza;

	@Getter
	@Setter
	private ItemNfe itemSelecionado =  new ItemNfe();

	@Getter
	@Setter
	private transient List<ItemNfe> listaTemporariaItens = new ArrayList<>();

	@Getter
	@Setter 
	private transient List<ItemNfe> listaTempItensDelete = new ArrayList<>();

	@Setter
	private AbstractDataModel<ItemNfe> listaItemModel;

	@Inject
	private ItemNfeRepository itemNfeDao;

	@Getter
	@Setter
	private boolean Deleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdTributos = false;

	@Getter
	@Setter
	private String razao;


	@Getter
	@Setter
	private String nomeTransportadora;

	@Getter
	@Setter
	private String cnpj;

	@Getter
	@Setter
	private String endereco;

	@Getter
	@Setter
	private String bairro ;

	@Getter
	@Setter
	private String municipio;

	@Getter
	@Setter
	private String uf;

	@Getter
	@Setter
	private BigDecimal valorFrete = new BigDecimal("0.0");

	@Getter
	@Setter
	private LocalDateTime emissao = LocalDateTime.now();

	@Getter
	@Setter
	private LocalDateTime entSai = LocalDateTime.now().plusMinutes(1);

	@Getter
	@Setter
	private LocalTime horaSai = LocalTime.now();

	@Getter
	@Setter
	private String ref;

	@Getter
	@Setter
	private CartaCorrecao cce;

	@Inject
	private CartaCorrecaoRepository cceDao;

	@Getter
	@Setter
	private BigDecimal quantidade ;

	@Getter
	@Setter
	private BigDecimal totalItem;

	@Getter
	@Setter
	private BigDecimal totalNfe ;

	@Getter
	@Setter
	private BigDecimal temporario;

	private BigDecimal totalBaseIcms = new BigDecimal("0");

	private BigDecimal totalBaseIcmsSt = new BigDecimal("0");

	private BigDecimal valorIcms = new BigDecimal("0");

	private BigDecimal valorIcmsSt = new BigDecimal("0");

	private BigDecimal valorTotalProdutos = new BigDecimal("0");

	private BigDecimal valorTotalTributos = new BigDecimal("0");

	private BigDecimal valorTotalIpi = new BigDecimal("0");

	private BigDecimal valorTotalNota = new BigDecimal("0");

	private BigDecimal valorTotalPis = new BigDecimal("0");

	private BigDecimal valorTotalCofins = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal precoVenda= new BigDecimal("0");

	@Setter
	@Getter
	private BigDecimal tempValorUnitario = new BigDecimal("0");

	@Setter
	@Getter
	private BigDecimal tempDespesaItem = new BigDecimal("0");

	@Inject
	private CalculaTributos calculaTributos;

	@Getter
	@Setter
	private String nomeArquivo;

	@Getter
	@Setter
	private List<Lacre> listaTemporariaLacres = new ArrayList<>();

	@Inject
	private LacreRepository lacreDao;

	@Getter
	@Setter
	private Lacre lacre = new Lacre();

	@Getter
	@Setter
	private Lacre lacreTemp = new Lacre();

	@Getter
	@Setter
	private FormaDePagamento formaPag = new FormaDePagamento();

	@Inject
	private FormaDePagementoRepository formaPagDao;

	@Inject
	private ParcelasNfeRepository parcelaDao;

	@Getter
	private List<FormaDePagamento> listaFormasDePagamento = new ArrayList<>();

	@Getter
	@Setter
	private TabelaPreco tabelaSelecionada = TabelaPreco.TA;

	@Getter
	@Setter
	private BigDecimal  desPercVal = new BigDecimal("0.00");

	@Getter
	@Setter
	private boolean descontoPercentual = true;

	@Getter
	private FormulasDosImpostos formula = new FormulasDosImpostos();

	@Getter
	@Setter
	private transient Configuration config;

	@Inject
	private ConfigurationRepository configDao;

	@Getter
	@Setter
	private BarrasEstoque barrasEstoque;

	@Getter
	@Setter
	private List<BarrasEstoque> listaBarrasTemp = new ArrayList<BarrasEstoque>();

	@Getter
	@Setter
	private String numeroDI;

	@Getter
	@Setter
	private DI di = new DI(); 

	@Inject
	private DIRepository diDao;

	private transient int row = 0;

	@Getter
	@Setter
	private String justificativa;

	//	@Inject
	//	private ParcelasNfeRepository parcelasDao;

	@Getter
	@Setter
	private List<ParcelasNfe> listaParcelamento = new ArrayList<>();

	@Getter
	@Setter
	private transient List<ParcelasNfe> listaTempParcelas = new ArrayList<>();

	@Getter
	@Setter
	private int rowDataBase = 0;

	private MathContext mc = new MathContext(20, RoundingMode.HALF_EVEN);

	@Getter
	@Setter 
	private boolean podeExcluir = false;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private String path;

	@Getter
	@Setter
	private List<NfeReferenciada> listaTempReferenciada = new ArrayList<>();

	@Getter
	@Setter
	private NfeReferenciada nfeReferenciada;

	@Inject
	private NfeReferenciadaRepository nfeReferenciadaDao;

	@Getter
	@Setter
	private NfeReferenciada nfeRefTemp = new NfeReferenciada();

	// variaveis para inutilizacao de numero de notas

	@Getter
	@Setter
	private Inutilizacao inutiliza = new Inutilizacao();

	@Inject
	private InutilizacaoRepository inutilizaDao;

	@Getter
	@Setter
	private Tributos tributoTemporario = new Tributos();

	@Getter
	@Setter
	private transient ItemNfe itemSelecionadoTemporario = new ItemNfe();

	@Getter
	@Setter
	private List<LocalDate> listaTempDatas = new ArrayList<LocalDate>();

	@Getter
	@Setter
	private Empresa configMatriz;

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
	private LocalDate dataInicial = LocalDate.now();

	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now();

	@Inject
	private RelatorioVendas relatorios;

	@Setter
	@Getter
	private List<Tributos> listaTributos = new ArrayList<Tributos>();

	@Getter
	@Setter
	private Pedido pedido;

	@Inject
	private PedidoRepository pedidoDao;

	@Getter
	@Setter
	private boolean clienteDefinido = false;

	@Getter
	@Setter
	private boolean nfeFromPedido = false;

	@Getter
	@Setter
	private boolean botaoProcesseguir = true;

	@Getter
	@Setter
	private AgPedido agPedido = new AgPedido();

	@Inject
	private AgPedidoRepository agPedidoDao;

	@Getter
	@Setter
	private LocalDateTime dataIni = LocalDateTime.now().minusDays(10);

	@Getter
	@Setter
	private LocalDateTime dataFim = LocalDateTime.now();

	@Getter
	private EmpUser empresaUsuario;

	@Getter
	private Configuration configUser;

	@PostConstruct
	public void init(){
		//		this.nfeModel = getLazyNfe();
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}

	@Override
	public Nfe setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractDataModel<ItemNfe> getListaItemModel(){
		this.listaItemModel = new AbstractDataModel<ItemNfe>(this.listaTemporariaItens);
		return this.listaItemModel;
	}

	//	public List<Tributos> getListaTributos(){
	//		return this.natOperacaoDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(), false);
	//	}
	/**
	 * Gera a lista de NFE em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Nfe> getLazyNfe(){
		this.nfeModel = new AbstractLazyModel<Nfe>() {

			/**
			 *
			 */
			private static final long serialVersionUID = -5296165044543100373L;

			@Override
			public List<Nfe> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				//				Page<Nfe> page = nfeDao.listByStatusFilial(isDeleted() , null,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(),false, pageRequest);
				Page<Nfe> page = nfeDao.listaPeriodoLazyComFiltro(isDeleted() , null,dataIni,dataFim,pegaIdEmpresa(), pegaIdFilial(), pageRequest,null, null,true);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							//							page = nfeDao.listByFilterFilial(false, null, pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							page = nfeDao.listaPeriodoLazyComFiltro(isDeleted() , null,dataIni,dataFim,pegaIdEmpresa(), pegaIdFilial(), pageRequest,filterProperty, filterValue.toString().toUpperCase(),true);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return nfeModel;
	}


	/**
	 * Inicialização da pagina em modo de Adi��o ou Edi��o
	 * @param idNfe
	 */
	public void initializeForm(Long idNfe) {
		this.config = this.configDao.findById(this.getUsuarioAutenticado().getConfig().getId(), false);
		this.configMatriz = this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
		if (idNfe == null) {
			this.nfeFromPedido = false;
			this.barrasEstoque = new BarrasEstoque();
			this.viewState = ViewState.ADDING;
			this.nfe = new Nfe();
			this.cliente = new Cliente();
			this.fornecedor = new Fornecedor();
			this.empresa = new Empresa();
			this.filial = new Filial();
			this.colaborador = new Colaborador();
			this.destino = new Destinatario();
			this.produto = new Produto();
			this.itemNfe = new ItemNfe();
			this.totalNfe = new BigDecimal("0.0");
			this.valorFrete = new BigDecimal("0.0");
			this.temporario = new BigDecimal("0.0");
			this.quantidade = new BigDecimal("0.0");
			this.precoVenda = new BigDecimal("0.0");
			this.transporte = new Transportadora();
			this.transp = new Transportador();
			this.custoProduto = new ProdutoCusto();
			this.itemSelecionado = new ItemNfe();
			this.lacre = new Lacre();
			this.formaPag = new FormaDePagamento();
			this.nfeReferenciada = new NfeReferenciada();
			this.descontoPercentual = true;
			this.listaTempReferenciada = new ArrayList<>();
			this.listaFormasDePagamento = this.formaPagDao.listaFormaDePagamento(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
			this.listaItemModel = getListaItemModel();
			this.nfe.setFinalidadeEmissao(FinalidadeNfe.NO);
			this.nfe.setTipoPesquisa(TipoPesquisa.CLI);

		} else {
			this.barrasEstoque = new BarrasEstoque();
			this.viewState = ViewState.EDITING;
			setVisivelPorIdTributos(true);
			this.nfe = this.nfeDao.pegaNfe(idNfe, pegaIdEmpresa(),pegaIdFilial());
			this.nfeFromPedido = this.nfe.isOrigemPedido();
			if (this.nfe.isOrigemPedido()) {
				this.agPedido = this.agPedidoDao.encontraAgPedidoPorId(this.nfe.getPedido().getAgrupado().getId());
				this.nfe.setListaParcelas(this.agPedidoDao.listaDeParcelasAgPedidoPorId(this.agPedido.getId()));
			}else {
				this.nfe.setListaParcelas(this.parcelaDao.listaParcelasPorNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
			}
			this.nfe.setListaLacres(this.lacreDao.findLacreForNfe(this.nfe, pegaIdEmpresa(), pegaIdFilial()));
			//			this.nfe.setListaItemNfe(this.itemNfeDao.listaItensPorNfe(this.nfe));
			System.out.println("passei pelo localiza NFE - L630");
			// refazendo visualização
			//1º verificando se a finalidade da nfe é de devoluçção
			if (this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
				this.nfe.setListaChavesReferenciada(this.nfeReferenciadaDao.listaNotasReferenciadas(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
				this.setListaTempReferenciada(this.nfe.getListaChavesReferenciada());
				System.out.println("passei pelo geraLIstaChaveReferenciada NFE - L636");
			}
			// preenchendo destinatario nfe
			preencheDestinoETransporte();
			// validando a lista de itens da nota
			if (!this.nfe.getListaItemNfe().isEmpty()){
				System.out.println("Estou dentro da listadeItensNfe preenchida! L642");
				this.listaTemporariaItens = this.nfe.getListaItemNfe();
				this.row = 0;
				for (ItemNfe item : this.listaTemporariaItens) {
					item.setRow(row);
					this.row++;
				} 
			}else{
				this.listaTemporariaItens = this.itemNfeDao.listaItensPorNfe(this.nfe);
				if (this.listaTemporariaItens != null && !this.listaTemporariaItens.isEmpty()){
					this.nfe.setListaItemNfe(this.listaTemporariaItens);
				}else {
					this.listaTemporariaItens = new ArrayList<>();
				}
			}
			System.out.println("passei preenchi lista de itens - L657");
			// preenchendo a Lista Lazy de itens
			this.listaItemModel = getListaItemModel();
			// Preenchendo a forma de pagamento
			if (this.nfe.getFormaPagamento() != null){
				System.out.println("entrei no nfe com forma de pagamento - L652");
				this.formaPag = this.formaPagDao.findById(this.nfe.getFormaPagamento().getId(), false);
				if(this.nfe.getListaParcelas() != null && !this.nfe.getListaParcelas().isEmpty() ) {
					this.listaParcelamento = this.nfe.getListaParcelas();
				}else {
					this.listaParcelamento = this.parcelaDao.listaParcelasPorNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
					this.nfe.setListaParcelas(this.listaParcelamento);
				}
				for (ParcelasNfe parce : this.listaParcelamento) {
					this.listaTempParcelas.add(parce);
					
				}
				for (ParcelasNfe parcela : this.listaParcelamento) {
					System.out.println(" numero controle " + parcela.getControle());					
				}
			}
			// preenchendo a lisata de formas de pagamentos 
			this.listaFormasDePagamento = this.formaPagDao.listaFormaDePagamento(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
			System.out.println("passei pelo preenche pagamenteos - L676");
			// Preenchendo o lacre
			if (this.nfe.getListaLacres() != null && !this.nfe.getListaLacres().isEmpty()) {
				this.listaTemporariaLacres = this.nfe.getListaLacres();
			}else {
				this.listaTemporariaLacres = this.lacreDao.findLacreForNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
				if (this.listaTemporariaLacres == null){
					this.listaTemporariaLacres = new ArrayList<>();
				}
				this.nfe.setListaLacres(this.listaTemporariaLacres);
			}
			System.out.println("passei pelo preenche lacre - L686");
			// preenchendo o transporte
			System.out.println("passei pelo prenche transporte - L693");
			// inicializando variaveis do sistema
			this.destino = this.nfe.getDestino();
			this.produto = new Produto();
			this.itemNfe = new ItemNfe();
			this.totalNfe = this.nfe.getValorTotalProdutos();
			this.valorFrete = this.nfe.getValorFrete();
			this.temporario = new BigDecimal("0");
			this.quantidade = new BigDecimal("0");
			this.transp = this.nfe.getTransportador();
			this.itemSelecionado = new ItemNfe();
			this.lacre = new Lacre();
			this.descontoPercentual = true;

			this.totalBaseIcms = this.nfe.getBaseIcms();
			this.valorIcms = this.nfe.getValorIcms();
			this.totalBaseIcmsSt = this.nfe.getBaseIcmsSubstituicao();
			this.valorIcmsSt = this.nfe.getValorIcmsSubstituicao();

			this.valorTotalProdutos = this.nfe.getValorTotalProdutos();
			this.valorTotalNota =this.nfe.getValorTotalNota();
			this.totalNfe = this.nfe.getValorTotalNota().setScale(2,RoundingMode.HALF_EVEN);
			this.desPercVal = this.nfe.getDesconto();
			this.respostaAcbrLocal = this.nfe.getRespostaFinalAcbr();
			// gerando nome padrao dos arquivos 
			this.nomeArquivo = this.getUsuarioAutenticado().getName()+pegaIdEmpresa();
			System.out.println("passei Fim do inicializaForm- L719");

		}
	}

	public void preencheDestinoETransporte() {
		if (this.nfe.getDestino().getCliente() != null){
			this.cliente = this.clienteDao.findById(this.nfe.getDestino().getCliente().getId(), false);
			this.razao = this.cliente.getRazaoSocial();
			if (this.cliente.getTipoCliente() == TipoCliente.CfC){
				System.out.println( this.cliente.getCpf());
				this.cnpj= this.cliente.getCpf();
				this.nfe.setIndFinal("1");
			}else{
				System.out.println(this.cliente.getCnpj());
				this.cnpj=this.cliente.getCnpj();
			}
			System.out.println("passei preenche cliente- L643");
			this.emissao = this.nfe.getDataEmissao();
			this.entSai = this.nfe.getDataSaida();
			if (this.cliente.getEndereco().getLogradouro() == "" || this.cliente.getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.cliente.getEndereco().getEndereco().getLogra();
				this.bairro = this.cliente.getEndereco().getEndereco().getBairro();
			}else {
				this.endereco = this.cliente.getEndereco().getLogradouro();
				this.bairro = this.cliente.getEndereco().getBairro();
			}
			System.out.println("passei preenche endereço - L653");
			this.municipio = this.cliente.getEndereco().getEndereco().getLocalidade();
			this.uf = this.cliente.getEndereco().getEndereco().getUf().toString();
		}
		if (this.nfe.getDestino().getFornecedor() != null){
			this.fornecedor = this.fornecedorDao.findById(this.nfe.getDestino().getFornecedor().getId(), false);
			this.razao = this.fornecedor.getRazaoSocial();
			if (this.fornecedor.getTipoCliente() == TipoCliente.CfC){
				this.cnpj= this.fornecedor.getCpf();
				this.nfe.setIndFinal("1");
			}else{
				this.cnpj=this.fornecedor.getCnpj();
			}
			this.emissao = this.nfe.getDataEmissao();
			this.entSai = this.nfe.getDataSaida();
			if (this.fornecedor.getEndereco().getLogradouro() == "" || this.fornecedor.getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.fornecedor.getEndereco().getEndereco().getLogra();
				this.bairro = this.fornecedor.getEndereco().getEndereco().getBairro();
			}else {
				this.endereco = this.fornecedor.getEndereco().getLogradouro();
				this.bairro = this.fornecedor.getEndereco().getBairro();
			}
			this.bairro = this.fornecedor.getEndereco().getEndereco().getBairro();
			this.municipio = this.fornecedor.getEndereco().getEndereco().getLocalidade();
			this.uf = this.fornecedor.getEndereco().getEndereco().getUf().toString();
		}
		System.out.println("passei preenchi fornecedor - L678");
		if (this.nfe.getDestino().getEmpresa() != null){
			this.empresa = this.empresaDao.findById(this.nfe.getDestino().getEmpresa().getId(), false);
			this.razao = this.empresa.getRazaoSocial();
			this.cnpj= this.empresa.getCnpj();
			this.emissao = this.nfe.getDataEmissao();
			this.entSai = this.nfe.getDataSaida();
			if (this.empresa.getEndereco().getLogradouro() == "" || this.empresa.getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.empresa.getEndereco().getEndereco().getLogra();
				this.bairro = this.empresa.getEndereco().getEndereco().getBairro();
			}else {
				this.endereco = this.empresa.getEndereco().getLogradouro();
				this.bairro = this.empresa.getEndereco().getBairro();
			}
			this.municipio = this.empresa.getEndereco().getEndereco().getLocalidade();
			this.uf = this.empresa.getEndereco().getEndereco().getUf().toString();
		}
		if (this.nfe.getDestino().getFilial() != null){
			this.filial = this.filialDao.findById(this.nfe.getDestino().getFilial().getId(), false);
			this.razao = this.filial.getRazaoSocial();
			this.cnpj= this.filial.getCnpj();
			this.emissao = this.nfe.getDataEmissao();
			this.entSai = this.nfe.getDataSaida();
			if (this.filial.getEndereco().getLogradouro() == "" || this.filial.getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.filial.getEndereco().getEndereco().getLogra();
				this.bairro = this.filial.getEndereco().getEndereco().getBairro();
			}else {
				this.endereco = this.filial.getEndereco().getLogradouro();
				this.bairro = this.filial.getEndereco().getBairro();
			}
			this.municipio = this.filial.getEndereco().getEndereco().getLocalidade();
			this.uf = this.filial.getEndereco().getEndereco().getUf().toString();
		}
		if (this.nfe.getDestino().getColaborador() != null){

		}
		if (this.nfe.isClienteRetira()){
			switch (this.nfe.getTipoPesquisa()) {
				case CLI:
					this.transporte.setRazaoSocial(this.cliente.getRazaoSocial());
					this.transporte.setEndereco(this.cliente.getEndereco());
					if (this.cliente.getCnpj() == null){
						this.transporte.setCpf(this.cliente.getCpf());
					}else{
						this.transporte.setCnpj(this.cliente.getCnpj());
						this.transporte.setInscEstadual(this.cliente.getInscEstadual());
					}	
					break;
				case FIL:
					this.transporte.setRazaoSocial(this.filial.getRazaoSocial());
					this.transporte.setEndereco(this.filial.getEndereco());
					this.transporte.setCnpj(this.filial.getCnpj());
					this.transporte.setInscEstadual(this.filial.getInscEstadual());
					break;
				case FOR:
					this.transporte.setRazaoSocial(this.fornecedor.getRazaoSocial());
					this.transporte.setEndereco(this.fornecedor.getEndereco());
					if (this.fornecedor.getCnpj().isEmpty()){
						this.transporte.setCpf(this.fornecedor.getCpf());
					}else{
						this.transporte.setCnpj(this.fornecedor.getCnpj());
						this.transporte.setInscEstadual(this.fornecedor.getInscEstadual());
					}	
					break;
				case MAT:
					this.transporte.setRazaoSocial(this.empresa.getRazaoSocial());
					this.transporte.setEndereco(this.empresa.getEndereco());
					this.transporte.setCnpj(this.empresa.getCnpj());
					this.transporte.setInscEstadual(this.empresa.getInscEstadual());
					break;

				default:
					break;
			}  

			if (this.transporte.getCnpj() == null){
				this.transp.setRetiraDoc(this.transporte.getCpf());
			}else{
				this.transp.setRetiraDoc(this.transporte.getCnpj());
				this.transp.setRetiraInsc(this.transporte.getInscEstadual());
			}
			this.transp.setRetiraNome(this.transporte.getRazaoSocial());
			if (this.transporte.getEndereco().getLogradouro() == "" || this.transporte.getEndereco().getLogradouro().isEmpty()) {
				this.transp.setRetiraEnd(this.transporte.getEndereco().getEndereco().getLogra());
			}else {
				this.transp.setRetiraEnd(this.transporte.getEndereco().getLogradouro());
			}
			this.transp.setRetiraMunicipio(this.transporte.getEndereco().getEndereco().getLocalidade());
			this.transp.setRetiraUf(this.transporte.getEndereco().getEndereco().getUf().name());
		}else{
			if (this.nfe.getTransportador().getTransportadora() != null) {
				this.transporte = this.nfe.getTransportador().getTransportadora();
			}else {
				this.transporte = new Transportadora();
				System.out.println("zerei o transporte");
			}
		}
	}

	/**
	 * Consulta Status Servi�o Nfe 
	 * @throws IOException 
	 */

	public void statusServicoNfe() throws IOException{
		//		this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
		this.respostaAcbrLocal = acbr.consultaStatusNFE(infConexao);
	}
	/**
	 * Gera o arquivo ini para nfe no servidor
	 * @throws IOException 
	 * @throws NfeException 
	 */
	@Transactional
	public void geraNfe() throws IOException, NfeException{
		//daqui at�
		//		String resp;
		//		this.nfe = this.nfeDao.findById(this.nfe.getId(), false);
		//		this.nfe.setListaLacres(this.lacreDao.findLacreForNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
		//		this.nfe.setListaParcelas(this.parcelaDao.listaParcelasPorNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
		//		this.setTransp(this.nfe.getTransportador());
		//		this.nomeArquivo = this.getUsuarioAutenticado().getName()+this.getUsuarioAutenticado().getIdEmpresa();
		//
		//		if (this.nfe.getNumeroNota() == null){
		//			this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
		//		}
		// aqui!!!! � temporario
		this.respostaAcbrLocal = this.acbr.criarArqIniMaqRemota(infConexao, this.nomeArquivo , this.nfe, FinalidadeNfe.NO,false).toUpperCase();
		//		System.out.println("resposta do gera nfe : " + this.respostaAcbrLocal);
		//		System.out.println("estou no geraNFE  tamanho da resposta : " + this.respostaAcbrLocal.length());

	}

	public Emitente preencheEmitente(){
		Emitente emissor = new Emitente();
		if (this.getUsuarioAutenticado().getIdFilial() == null){
			emissor.setEmpresa(this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false));
		}else{
			emissor.setFilial(this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false));
		}
		return emissor;
	}

	/**
	 * @throws IOException 
	 * @throws NfeException 
	 * 
	 */
	public void criarArqNfeAcbr() throws IOException, NfeException{
		//		this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
		this.acbr.criarArqIniMaqRemota(infConexao, null, this.nfe, FinalidadeNfe.NO,false).toUpperCase();
	}

	/**
	 * envia a nfe para o acbr
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void enviaNFe() throws InterruptedException, IOException{
		String retorno;
		//		this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
		retorno = this.acbr.enviaComandoACBr(this.infConexao, "NFe.CriarNFe(\""+"C:\\ibrcomp\\tmp\\"+this.nomeArquivo+".ini\",5)").toUpperCase();
		System.out.println("resposta do envia nfe: " + this.respostaAcbrLocal);
		System.out.println("retorno " +retorno);
		this.respostaAcbrLocal = retorno;
		System.out.println(this.respostaAcbrLocal.length());
		System.out.println(this.respostaAcbrLocal.substring(this.respostaAcbrLocal.length()-52,this.respostaAcbrLocal.length()-8));
		this.nfe.setChaveAcesso(retorno.substring(retorno.length()-52,retorno.length()-8));
		System.out.println(retorno.substring(4));
		this.nfe.setCaminhoXml(retorno.substring(4));
		System.out.println("chave de acesso armazenado na nfe : " + this.nfe.getChaveAcesso());
		System.out.println("ibr");
	}

	/**
	 * Inicializa��o da pagina em modo listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
		this.nfeModel = getLazyNfe();
	}

	/**
	 * Retorna form de pesquisa de destinatario 
	 */

	public void telaResultadoDestinatario(){
		this.updateAndOpenDialog("listaResultadoDialog","dialogListaResultado");
	}
	/**
	 * Exibe tela para pesquisa de destinatario 
	 */
	public void  telaPesquisaDestinatario(Pedido pedido) {
		this.updateAndOpenDialog("encontraDestinatarioDialog","dialogEncontraDestinatario");

	}

	/**
	 * Exibe dialog Lista de Pedidos 
	 */
	public void telaListaDePedidos() {
		this.updateAndOpenDialog("listaDePedidosDialog","dialogListaDePedidos");
	}

	/**
	 *  Retorna tela Cancela Nfe
	 */

	public void telaCancelaNfe(){
		this.nfe = this.nfeDao.findById(this.nfe.getId(), false);
		this.justificativa = new String();
		this.respostaAcbrLocal = new String();
		this.updateAndOpenDialog("cancelaNfeId", "dialogCancelaNfe");		
	}

	/**
	 * Retorna tela Carta de Corre��o NFe
	 */
	public void telaCCeNfe(){
		this.cce = new CartaCorrecao();
		this.cce.setDhEvento(LocalDateTime.now());
		this.nomeArquivo = this.getUsuarioAutenticado().getName()+this.getUsuarioAutenticado().getIdEmpresa();
		this.respostaAcbrLocal = new String();
		this.updateAndOpenDialog("cceNfeId", "dialogCceNfe");
	}

	/**
	 * Retorna form de pesquisa de Transportadoras
	 */
	public void telaResultadoTransporte(){
		this.updateAndOpenDialog("listaTransporteDialog", "dialogListaTransporte");
	}

	/**
	 * Retorna form de envio de email avulso
	 */
	public void telaEmailAvulso(){
		this.updateAndOpenDialog("enviaEmailNfeId", "dialogEnviaEmailNfe");
	}


	/**
	 * redireciona para a pagina com o ID da NFE a ser editado
	 * 
	 * @param nfeID
	 * 
	 * @returna pagina de edi��o de NFE
	 */
	public String changeToEdit(Long nfeID) {
		return "formCadNfe.xhtml?faces-redirect=true&nfeID=" + nfeID;
	}

	/**
	 * redireciona para Cadastramento de nova NFE / edi��o de NFE j� cadastrado
	 * @return pagina de inclusao de NFE
	 */
	public String newNfe() {
		return "formCadNfe.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListNfe() {
		return "formListNfe.xhtml?faces-redirect=true";
	}

	/**
	 * Evento que controla o item da lista selecionado 
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "A Nfe " + ((Nfe)event.getObject()).getNumeroNota()+ " foi selecionado");
		this.nfe = (Nfe) event.getObject();
		this.destino = this.nfe.getDestino();
		if (!this.nfe.getStatusEmissao().equals(StatusNfe.IN)) {
			if (this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
				this.nfe.setListaChavesReferenciada(this.nfeReferenciadaDao.listaNotasReferenciadas(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
				this.setListaTempReferenciada(this.nfe.getListaChavesReferenciada());
			}
		}
		setVisivelPorIdTributos(true);
		switch(this.nfe.getStatusEmissao()){
			case EN:{
				this.setPodeExcluir(false);
				break;
			}
			case SA:{
				this.setPodeExcluir(true);
				break;
			}
			case EE:{
				this.setPodeExcluir(true);
				break;
			}
			case CA:{
				this.setPodeExcluir(false);
				break;
			}
			default:
				this.setPodeExcluir(false);
				break;
		}
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		if (this.nfe.getFormaPagamento() != null){
			this.formaPag = this.formaPagDao.findById(this.nfe.getFormaPagamento().getId(), false);
			this.listaParcelamento = this.parcelaDao.listaParcelasPorNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
			this.listaTempParcelas = this.listaParcelamento;
		}
		if (this.nfe.getDestino().getCliente() != null){
			this.email = this.nfe.getDestino().getCliente().getEmailNFE().getEmail();
		}else if (this.nfe.getDestino().getFornecedor() != null){ // fornecedor
			this.email = this.nfe.getDestino().getFornecedor().getEmailNFE().getEmail();
		}else if (this.nfe.getDestino().getEmpresa() != null){ // empresa
			this.email = this.nfe.getDestino().getEmpresa().getEmailNFE().getEmail();
		}else if (this.nfe.getDestino().getFilial() != null){ // filial
			this.email = this.nfe.getDestino().getFilial().getEmailNFE().getEmail();
		}else { // colaborador
			this.email = this.nfe.getDestino().getColaborador().getEmail().getEmail();
		}

		if (this.nfe.isClienteRetira()){
			clienteRetira();
		}
		this.infConexao = pegaConexao();

	}

	public void onRowSelectItemNfe(SelectEvent event)throws IOException{
		this.itemSelecionado = (ItemNfe) event.getObject();
		System.out.println("selecionei o item : " + this.itemSelecionado.getProduto().getDescricao());

	}

	public void excluiItem(ItemNfe itemSelect){
		try{
			this.itemSelecionado = itemSelect;
			BigDecimal valorDeDesconto = new BigDecimal("0");
			BigDecimal percentualDesconto = new BigDecimal("0");
			BigDecimal fundoCPRetidoST = new BigDecimal("0");
			System.out.println("inico da exclusao do item");
			if (!listaTemporariaItens.isEmpty() && this.itemSelecionado != null){
				for (int i = 0 ; i < listaTemporariaItens.size(); i++){
					if (listaTemporariaItens.get(i).getRow() == this.itemSelecionado.getRow()){
						System.out.println("encontrado o itemSelecionado");
						listaTemporariaItens.remove(i);
						if (this.itemSelecionado.getDesconto().compareTo(new BigDecimal("0")) == 1){
							if (this.itemSelecionado.isPorcentagem()){
								percentualDesconto =new BigDecimal("100").subtract(this.itemSelecionado.getValorTotal().divide(this.itemSelecionado.getValorUnitario().multiply(this.itemSelecionado.getQuantidade()),mc).multiply(new BigDecimal("100")));
								valorDeDesconto = (this.itemSelecionado.getValorUnitario().multiply(this.itemSelecionado.getQuantidade())).multiply(percentualDesconto.divide(new BigDecimal("100"),mc));
								System.out.println("Percentual de desconto " + percentualDesconto);
								System.out.println("Valor desconto " + valorDeDesconto);
								this.nfe.setDesconto(this.nfe.getDesconto().subtract(valorDeDesconto));
							}else{
								valorDeDesconto = (this.itemSelecionado.getValorUnitario().multiply(this.itemSelecionado.getQuantidade())).subtract(this.itemSelecionado.getValorTotal());
								System.out.println("Valor de desconto "+ valorDeDesconto);
								this.nfe.setDesconto(this.nfe.getDesconto().subtract(valorDeDesconto));
							}
						}

						if (this.itemSelecionado.getPFCP().compareTo(new BigDecimal("0")) == 1 ){
							System.out.println("NfeBean linha 584 - Dentro do if getPfcp");
							this.nfe.setVFCP(this.nfe.getVFCP().subtract(itemSelecionado.getVFCP()));
							if (this.itemSelecionado.getCst() == "60" || this.itemSelecionado.getCst() == "500"){
								fundoCPRetidoST = this.formula.geraSTRetido(this.itemSelecionado.getValorTotal().divide(this.itemSelecionado.getQuantidade()), this.itemSelecionado.getProduto().getUfOrigem(), this.nfe.getUfDestino(), this.itemSelecionado.getProduto().getMvaFornecedor());
								this.nfe.setVFCPSTRet(this.nfe.getVFCPSTRet().subtract(fundoCPRetidoST));
							}else{
								this.nfe.setVFCPUFDest(this.nfe.getVFCPUFDest().subtract(this.itemSelecionado.getVFCPUFDest()));
							}
						}
						System.out.println("NfeBean linha 891 : calculando Total Tributos");
						System.out.println(this.valorTotalTributos);
						System.out.println("NfeBean linha 607 : verificando se existe partilha de icms");
						if (this.itemSelecionado.getVICMSUFDest().compareTo(new BigDecimal("0")) == 1){
							System.out.println("Dentro do if VICMSUFDest");
							this.nfe.setVICMSUFDest(this.nfe.getVICMSUFDest().subtract(this.itemSelecionado.getVICMSUFDest()));
							System.out.println(this.nfe.getVICMSUFDest());
						}
						if (this.itemNfe.getVICMSUFRemet().compareTo(new BigDecimal("0")) == 1){
							System.out.println("NfeBean dentro do if VICMSUFRemet");
							this.nfe.setVICMSUFRemet(this.nfe.getVICMSUFRemet().subtract(this.itemSelecionado.getVICMSUFRemet()));
							System.out.println(this.nfe.getVICMSUFRemet());
						}

						this.nfe.setValorTotalTributos(this.nfe.getValorTotalTributos().subtract(this.itemSelecionado.getValorTotalTributoItem()));
						System.out.println(this.nfe.getValorTotalTributos());

						this.totalBaseIcms = this.totalBaseIcms.subtract(this.itemSelecionado.getBaseICMS());
						this.nfe.setBaseIcms(this.totalBaseIcms.setScale(2, RoundingMode.HALF_EVEN));
						this.valorIcms = this.valorIcms.subtract(this.itemSelecionado.getValorIcms());
						this.nfe.setValorIcms(this.valorIcms);
						this.totalBaseIcmsSt = this.totalBaseIcmsSt.subtract(this.itemSelecionado.getBaseICMSSt());
						this.nfe.setBaseIcmsSubstituicao(this.totalBaseIcmsSt);  
						this.valorIcmsSt = this.valorIcmsSt.subtract(this.itemSelecionado.getValorIcmsSt());
						this.nfe.setValorIcmsSubstituicao(this.valorIcmsSt.setScale(2, RoundingMode.HALF_EVEN));

						this.valorTotalPis = this.nfe.getValorTotalPis().subtract(this.itemSelecionado.getValorPis());
						this.nfe.setValorTotalPis(this.valorTotalPis);

						this.valorTotalCofins = this.nfe.getValorTotalCofins().subtract(this.itemSelecionado.getValorCofins());
						this.nfe.setValorTotalCofins(this.valorTotalCofins);

						this.nfe.setValorTotalProdutos(this.nfe.getValorTotalProdutos().subtract(this.itemSelecionado.getValorTotalBruto()));
						this.nfe.setValorTotalIpi(this.nfe.getValorTotalIpi().subtract(this.itemSelecionado.getValorIPI()));
						this.valorTotalProdutos = this.valorTotalProdutos.subtract(this.itemSelecionado.getValorTotalBruto());
						this.valorTotalNota =this.valorTotalNota.subtract(this.itemSelecionado.getValorTotal()).subtract(this.itemSelecionado.getValorFrete()).subtract(this.itemSelecionado.getValorSeguro()).subtract(this.itemSelecionado.getValorIcmsSt()).subtract(this.itemSelecionado.getValorIPI()).subtract(this.itemSelecionado.getValorDespesas());
						this.nfe.setValorTotalNota(this.valorTotalNota.setScale(2, RoundingMode.HALF_EVEN));
						this.totalNfe = this.nfe.getValorTotalProdutos().subtract(this.nfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN);
						this.nfe.setListaItemNfe(this.listaTemporariaItens);

					}
				}
				// removendo item em caso de j� p�rsistido no banco de dados
				if (itemSelect.getId() != null) { // caso possui ID � porque j� foi persistido!
					//					this.itemNfeDao.delete(itemSelect);
					this.listaItensPersistidosExcluir.add(itemSelect);
				}
				this.listaItemModel = getListaItemModel();
				this.addWarning(true, "nfe.list.delete", itemSelect.getProduto().getReferencia());
			}
		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}

	public void onRowSelectTransporte(SelectEvent event)throws IOException{
		this.transporte = (Transportadora) event.getObject();
		this.nomeTransportadora = "";
	}

	/**
	 * Evento que controla o item da lista selecionado na tela de pesquisa
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectPesquisa(SelectEvent event)throws IOException{

		if (this.nfe.getTipoPesquisa() == TipoPesquisa.CLI){
			this.addInfo(true, "O Destinatario " + ((Cliente)event.getObject()).getRazaoSocial()+ " foi selecionado");
			this.cliente = ((Cliente) event.getObject());
			this.destino.setCliente(this.cliente);
			this.nfe.setUfDestino(this.cliente.getEndereco().getEndereco().getUf());
			this.nfe.setNome(this.cliente.getRazaoSocial());
			// preenchendo os label da nfe
			this.razao = this.destino.getCliente().getRazaoSocial();
			if (this.destino.getCliente().getCnpj() == null){
				this.cnpj = this.destino.getCliente().getCpf();
				this.nfe.setIndFinal("1");
			}else{
				this.cnpj = this.destino.getCliente().getCnpj();
				if (this.destino.getCliente().getInscEstadual()== null){
					this.nfe.setIndFinal("1");
				}else{
					this.nfe.setIndFinal("0");
				}
			}
			if (this.destino.getCliente().getEndereco().getLogradouro() == "" || this.destino.getCliente().getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.destino.getCliente().getEndereco().getEndereco().getLogra() +", "+ this.destino.getCliente().getEndereco().getNumero();
				this.bairro = this.destino.getCliente().getEndereco().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getCliente().getEndereco().getLogradouro() +", "+ this.destino.getCliente().getEndereco().getNumero();
				this.bairro = this.destino.getCliente().getEndereco().getBairro();
			}
			this.municipio = this.destino.getCliente().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getCliente().getEndereco().getEndereco().getUf().name();
		}
		if (this.nfe.getTipoPesquisa() == TipoPesquisa.FOR){
			this.addInfo(true, "O Destinatario " + ((Fornecedor)event.getObject()).getRazaoSocial()+ " foi selecionado");
			this.fornecedor = (Fornecedor) event.getObject();
			this.destino.setFornecedor(this.fornecedor);
			this.nfe.setNome(this.fornecedor.getRazaoSocial());
			this.nfe.setUfDestino(this.fornecedor.getEndereco().getEndereco().getUf());
			// preenchendo os label da nfe
			this.razao = this.destino.getFornecedor().getRazaoSocial();
			if (this.destino.getFornecedor().getCnpj()!= null && this.destino.getFornecedor().getCnpj().isEmpty() && this.nfe.isImportacao() == false){
				this.cnpj = this.destino.getFornecedor().getCpf();
				this.nfe.setIndFinal("1");
			}else{
				if (this.destino.getFornecedor().getCnpj()== null && this.destino.getFornecedor().getTipoCliente() == TipoCliente.Est && this.nfe.isImportacao()) {
					this.nfe.setIndFinal("0");
				}else {
					this.cnpj = this.destino.getFornecedor().getCnpj();
					if (this.destino.getFornecedor().getInscEstadual().isEmpty()){
						this.nfe.setIndFinal("1");
					}else{
						this.nfe.setIndFinal("0");
					}
				}
			}
			if (this.destino.getFornecedor().getEndereco().getLogradouro() == "" || this.destino.getFornecedor().getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.destino.getFornecedor().getEndereco().getLogradouro() +", "+ this.destino.getFornecedor().getEndereco().getNumero();
				this.bairro = this.destino.getFornecedor().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getFornecedor().getEndereco().getEndereco().getLogra() +", "+ this.destino.getFornecedor().getEndereco().getNumero();
				this.bairro = this.destino.getFornecedor().getEndereco().getEndereco().getBairro();
			}
			this.municipio = this.destino.getFornecedor().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getFornecedor().getEndereco().getEndereco().getUf().name();
		}
		if (this.nfe.getTipoPesquisa() == TipoPesquisa.FIL){
			this.addInfo(true, "O Destinatario " + ((Filial)event.getObject()).getRazaoSocial()+ " foi selecionado");
			this.destino.setFilial((Filial) event.getObject());
			this.nfe.setNome(this.filial.getRazaoSocial());
			this.nfe.setUfDestino(((Filial) event.getObject()).getEndereco().getEndereco().getUf());
			// preenchendo os label da nfe
			this.razao = this.destino.getFilial().getRazaoSocial();
			this.cnpj = this.destino.getFilial().getCnpj();
			if (this.destino.getFilial().getEndereco().getLogradouro() == "" || this.destino.getFilial().getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.destino.getFilial().getEndereco().getLogradouro() +", "+ this.destino.getFilial().getEndereco().getNumero();
				this.bairro = this.destino.getFilial().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getFilial().getEndereco().getEndereco().getLogra() +", "+ this.destino.getFilial().getEndereco().getNumero();
				this.bairro = this.destino.getFilial().getEndereco().getEndereco().getBairro();
			}
			this.municipio = this.destino.getFilial().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getFilial().getEndereco().getEndereco().getUf().name();
			this.nfe.setIndFinal("0");
		}
		System.out.println("Estou o RowSelectPesquisa");
		this.nfe.setDestino(this.destino);

	}

	/**
	 * Metodo que possibilita exportar para excel a lista de Nfe
	 */
	public List<Nfe> pegaListaNfe() {
		return nfeDao.listNfeAtivo(pegaIdEmpresa(),pegaIdFilial());
	}

	public void criaExcel() {
		postProcessXLS(pegaListaNfe());
	}

	/**
	 * Criar um documento em excel personalizado.
	 * 
	 * @param document
	 */
	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColorPredefined.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
		}
	}

	public void pesquisaDestinatario(){
		System.out.println(this.destinatario + "tipo pesquisa =: "+this.nfe.getTipoPesquisa());
		if (this.nfe.getTipoPesquisa() == TipoPesquisa.CLI){
			System.out.println("fiz cli");
			this.listaCliente = this.clienteDao.pesquisaTexto(this.destinatario, getUsuarioAutenticado().getIdEmpresa(), null);
			telaResultadoDestinatario();
		}
		if (this.nfe.getTipoPesquisa() == TipoPesquisa.FIL){
			System.out.println("fiz fil");
			this.listaFilial = this.filialDao.pesquisaTexto(this.destinatario, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
			telaResultadoDestinatario();
		}
		if (this.nfe.getTipoPesquisa() == TipoPesquisa.FOR){
			System.out.println("fiz for");
			this.listaFornecedor = this.fornecedorDao.pesquisaTexto(this.destinatario, getUsuarioAutenticado().getIdEmpresa(), null);
			telaResultadoDestinatario();
		}
		if (this.nfe.getTipoPesquisa() == TipoPesquisa.MAT){
			System.out.println("fiz mat");
			this.empresa = this.empresaDao.findById(getUsuarioAutenticado().getIdEmpresa(), false);
			this.destino.setEmpresa(this.empresa);
			this.razao = this.destino.getEmpresa().getRazaoSocial();
			this.cnpj = this.destino.getEmpresa().getCnpj();
			if (this.destino.getEmpresa().getEndereco().getLogradouro() == "" || this.destino.getEmpresa().getEndereco().getLogradouro().isEmpty()) {
				this.endereco = this.destino.getEmpresa().getEndereco().getEndereco().getLogra() +", "+ this.destino.getEmpresa().getEndereco().getNumero();
				this.bairro = this.destino.getEmpresa().getEndereco().getEndereco().getBairro();
			}else {
				this.endereco = this.destino.getEmpresa().getEndereco().getLogradouro() +", "+ this.destino.getEmpresa().getEndereco().getNumero();
				this.bairro = this.destino.getEmpresa().getEndereco().getBairro();
			}
			this.municipio = this.destino.getEmpresa().getEndereco().getEndereco().getLocalidade();
			this.uf = this.destino.getEmpresa().getEndereco().getEndereco().getUf().name();
		}
	}
	public void pesquisaTransporte(){
		this.listaTransporte = this.transporteDao.pesquisaTexto(this.nomeTransportadora, getUsuarioAutenticado().getIdEmpresa());
		if (this.listaTransporte.size() > 0){
			System.out.println(this.listaTransporte.get(0).getRazaoSocial());
		}
		telaResultadoTransporte();
	}

	public TipoPesquisa[] getTipoPesquisa(){
		return TipoPesquisa.values();
	}

	public TipoFrete[] getTipoFrete(){
		return TipoFrete.values();
	}

	public TipoOperacao[] getTipoOperacao(){
		return TipoOperacao.values();
	}

	public FinalidadeNfe[] getFinalidadeNfeValue(){
		return FinalidadeNfe.values();
	}

	/**
	 *  Lista do autocompletar NCM  
	 */
	public List<Tributos> completaNatOperacao(String query) { // Testar!!!!!!!

		List<Tributos> fontePesquisa = this.natOperacaoDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial());

		return fontePesquisa;
	}

	/**
	 * Metodo que possibilita exportar para excel a lista de Nfe
	 */
	public List<Tributos> pegaListaNaturezaOperacao() {
		return this.natOperacaoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial());
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
	 */

	public boolean permiteEmissao(ItemNfe item) {
		boolean permissao= true;
		Estoque estoqueTemp = pegaEstoque(item);
		if (estoqueTemp.getNcmEstoque() == null) {
			if (this.configMatriz.isEstoqueFiscalNegativo() == false) {
				System.out.println("estoque fiscal = null ");
				permissao = false;
			}
		}else {
			if (this.configMatriz.isEstoqueFiscalNegativo() == false) {
				if (estoqueTemp.getNcmEstoque().getEstoque().compareTo(new BigDecimal("0")) > 0 ) {
					if (item.getQuantidade().compareTo(estoqueTemp.getNcmEstoque().getEstoque()) < 1 ) {
						System.out.println("estoque fiscal = positivo compare =" + item.getQuantidade().compareTo(estoqueTemp.getNcmEstoque().getEstoque())+ "Estoque: " + estoqueTemp.getNcmEstoque().getEstoque());
						permissao = true;
					}else {
						System.out.println("estoque fiscal = negativo compare =" + item.getQuantidade().compareTo(estoqueTemp.getNcmEstoque().getEstoque())+ "Estoque: " + estoqueTemp.getNcmEstoque().getEstoque());
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

	@Transactional
	public ItemNfe encontraProduto(String codigo) throws NfeException {
		ItemNfe itemTemp = new ItemNfe();
		this.barrasEstoque = this.barrasDao.encontraBarrasPorEmpresa(codigo, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
		if (this.barrasEstoque != null ) {
			this.produto = this.produtoDao.pegaProdutoID(this.barrasEstoque.getProdutoBase().getId(), pegaIdEmpresa(),pegaIdFilial());
			if(this.produto != null) {
				itemTemp.setBarras(this.barrasEstoque);
			}
		}else {
			this.produto = this.produtoDao.pegaProdutoRef(codigo, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
			if (this.produto == null) {
				throw new NfeException(this.translate("nfeException.error.product.notFound"));
			}else {
				this.listaBarrasTemp = this.barrasDao.listaBarrasPorProduto(this.produto, pegaIdEmpresa(), pegaIdFilial());
				if (this.listaBarrasTemp !=null && this.listaBarrasTemp.size() > 1) {
					// chamar lista para selecionar a barras que esta sendo vendida e setar para This.produto.
					telaListaBarras();
				}else {
					if (this.listaBarrasTemp != null && this.listaBarrasTemp.size() == 1) {
						this.produto = this.produtoDao.pegaProdutoID(this.listaBarrasTemp.get(0).getProdutoBase().getId(), pegaIdEmpresa(),pegaIdFilial());
						itemTemp.setBarras(this.listaBarrasTemp.get(0));
					}else {
						BarrasEstoque barras = new BarrasEstoque();
						barras.setProdutoBase(this.produto);
						itemTemp.setBarras(this.barrasDao.save(barras));
					}
				}
			}
		}
		return itemTemp;
	}

	/**
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaBarras(){
		this.updateAndOpenDialog("PesquisaBarrasDialog", "dialogPesquisaBarras");
	}

	/**
	 * M�todo que seta o c�digo de barras quando selecionado em uma lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectBarras(SelectEvent event)throws IOException{
		this.barrasEstoque = (BarrasEstoque) event.getObject();
		this.itemNfe.setBarras(this.barrasEstoque);
	}
	@Transactional
	public void localizaProduto(){
		try{
			this.itemNfe = new ItemNfe();
			BigDecimal valorDeDesconto = new BigDecimal("0");
			BigDecimal percentualDesconto = new BigDecimal("0");
			BigDecimal fundoCombate = new BigDecimal("0");
			BigDecimal fundoCPRetidoST = new BigDecimal("0");
			BigDecimal vfcpufdest = new BigDecimal("0");

			if (this.listaTemporariaItens.isEmpty()){
				this.rowDataBase = 0;
			}
			if (this.descontoPercentual){
				percentualDesconto = this.desPercVal.divide(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN);
			}
			if (this.ref != null && this.quantidade.compareTo(new BigDecimal("0")) == 1  ){
				this.itemNfe = encontraProduto(this.ref);
				setaCusto();
				System.out.println("Dentro do permissao");
				if (this.produto != null){	
					System.out.println("adicionando item na lista!");
					this.itemNfe.setProduto(this.produto);
					this.itemNfe.setQuantidade(this.quantidade);
					if(permiteEmissao(this.itemNfe)) {
						this.itemNfe.setPorcentagem(this.descontoPercentual);
						switch (tabelaSelecionada) {
							case TA:
								if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0 ){
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.itemNfe.setValorUnitario(this.custoProduto.getPreco1().setScale(3,RoundingMode.HALF_EVEN));
									}else {
										this.itemNfe.setValorUnitario(this.precoVenda);
									}
									this.itemNfe.setQuantidade(this.quantidade);
									this.itemNfe.setValorTotal(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									break;
								}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
									if (this.isDescontoPercentual()){
										this.itemNfe.setQuantidade(this.quantidade);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco1().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										valorDeDesconto = this.itemNfe.getValorUnitario().multiply(percentualDesconto);
										this.itemNfe.setDesconto(valorDeDesconto.multiply(this.itemNfe.getQuantidade()));
										System.out.println("desconto : "+this.itemNfe.getDesconto());
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}else{
										valorDeDesconto = this.desPercVal;
										this.itemNfe.setDesconto(valorDeDesconto);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco1().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										this.itemNfe.setQuantidade(this.quantidade);
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}
								}
							case TB:
								if  (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.itemNfe.setValorUnitario(this.custoProduto.getPreco2().setScale(3,RoundingMode.HALF_EVEN));
									}else {
										this.itemNfe.setValorUnitario(this.precoVenda);
									}
									this.itemNfe.setQuantidade(this.quantidade);
									this.itemNfe.setValorTotal(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									break;
								}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
									if (this.isDescontoPercentual()){
										this.itemNfe.setQuantidade(this.quantidade);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco2().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										valorDeDesconto = this.itemNfe.getValorUnitario().multiply(percentualDesconto);
										this.itemNfe.setDesconto(valorDeDesconto.multiply(this.itemNfe.getQuantidade()));
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}else{
										valorDeDesconto = this.desPercVal;
										this.itemNfe.setDesconto(valorDeDesconto);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco2().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										this.itemNfe.setQuantidade(this.quantidade);
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}
								}
							case TC:
								if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.itemNfe.setValorUnitario(this.custoProduto.getPreco3().setScale(3,RoundingMode.HALF_EVEN));
									}else {
										this.itemNfe.setValorUnitario(this.precoVenda);
									}
									this.itemNfe.setQuantidade(this.quantidade);
									this.itemNfe.setValorTotal(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									break;
								}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
									if (this.isDescontoPercentual()){
										this.itemNfe.setQuantidade(this.quantidade);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco3().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										valorDeDesconto = this.itemNfe.getValorUnitario().multiply(percentualDesconto);
										this.itemNfe.setDesconto(valorDeDesconto.multiply(this.itemNfe.getQuantidade()));
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}else{
										valorDeDesconto = this.desPercVal;
										this.itemNfe.setDesconto(valorDeDesconto);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco3().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										this.itemNfe.setQuantidade(this.quantidade);
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}
								}
							case TD:
								if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.itemNfe.setValorUnitario(this.custoProduto.getPreco4().setScale(3,RoundingMode.HALF_EVEN));
									}else {
										this.itemNfe.setValorUnitario(this.precoVenda);
									}
									this.itemNfe.setQuantidade(this.quantidade);
									this.itemNfe.setValorTotal(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									break;
								}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
									if (this.isDescontoPercentual()){
										this.itemNfe.setQuantidade(this.quantidade);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco4().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										valorDeDesconto = this.itemNfe.getValorUnitario().multiply(percentualDesconto);
										this.itemNfe.setDesconto(valorDeDesconto.multiply(this.itemNfe.getQuantidade()));
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}else{
										valorDeDesconto = this.desPercVal;
										this.itemNfe.setDesconto(valorDeDesconto);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco4().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										this.itemNfe.setQuantidade(this.quantidade);
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}
								}
							case TE:
								if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
									if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
										this.itemNfe.setValorUnitario(this.custoProduto.getPreco5().setScale(3,RoundingMode.HALF_EVEN));
									}else {
										this.itemNfe.setValorUnitario(this.precoVenda);
									}
									this.itemNfe.setQuantidade(this.quantidade);
									this.itemNfe.setValorTotal(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
									break;
								}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
									if (this.isDescontoPercentual()){
										this.itemNfe.setQuantidade(this.quantidade);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco5().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										valorDeDesconto = this.itemNfe.getValorUnitario().multiply(percentualDesconto);
										this.itemNfe.setDesconto(valorDeDesconto.multiply(this.itemNfe.getQuantidade()));
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}else{
										valorDeDesconto = this.desPercVal;
										this.itemNfe.setDesconto(valorDeDesconto);
										if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
											this.itemNfe.setValorUnitario(this.custoProduto.getPreco5().setScale(3,RoundingMode.HALF_EVEN));
										}else {
											this.itemNfe.setValorUnitario(this.precoVenda);
										}
										this.itemNfe.setQuantidade(this.quantidade);
										this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotal((this.itemNfe.getValorUnitario().multiply(this.quantidade)).subtract(this.itemNfe.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
										this.itemNfe.setValorTotalBruto(this.quantidade.multiply(this.itemNfe.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
										break;
									}
								}
							default:
								break;
						}
						this.itemNfe.setRow(this.rowDataBase);
						setTotalItem(this.itemNfe.getValorTotal());
						System.out.println("item: " + this.itemNfe.getProduto().getDescricao());
						System.out.println("nfe: " + this.nfe.getNatOperacao().getDescricao());
						//					System.out.println("cliente "+ this.nfe.getDestino().getCliente().getRazaoSocial());
						this.setItemNfe(this.calculaTributos.preencheImpostos(this.itemNfe, this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
						// this.itemNfe.setObsItem(obsItem);  gerar metodo para adcionar as observacoes ao item 
						System.out.println("Aliq icms preenchido: " + this.itemNfe.getAliqIcms());
						System.out.println("Item calculado Cfop preenchido: " + this.itemNfe.getCfopItem().getCfop());
						//					this.itemNfe.setValorTotal(getTotalItem().setScale(3,RoundingMode.HALF_EVEN));



						if (this.itemNfe.getPFCP().compareTo(new BigDecimal("0")) == 1 ){
							System.out.println("NfeBean linha 882 - Dentro do if getPfcp");
							fundoCombate = fundoCombate.add(this.itemNfe.getVFCP());
							this.nfe.setVFCP(fundoCombate);
							if (this.itemNfe.getCst() == "60" || this.itemNfe.getCst() == "500"){
								fundoCPRetidoST = fundoCPRetidoST.add(this.formula.geraSTRetido(this.itemNfe.getValorTotal().divide(this.itemNfe.getQuantidade()), this.itemNfe.getProduto().getUfOrigem(), this.nfe.getUfDestino(), this.itemNfe.getProduto().getMvaFornecedor()));
								this.nfe.setVFCPSTRet(fundoCPRetidoST);
							}else{
								vfcpufdest = vfcpufdest.add(this.itemNfe.getVFCPUFDest());
								this.nfe.setVFCPUFDest(vfcpufdest);
							}
						}
						System.out.println("NfeBean linha 891 : calculando Total Tributos");
						this.valorTotalTributos = this.valorTotalTributos.add(itemNfe.getValorTotalTributoItem());
						System.out.println(this.valorTotalTributos);
						System.out.println("NfeBean linha 894 : verificando se existe partilha de icms");
						if (this.itemNfe.getVICMSUFDest().compareTo(new BigDecimal("0.00")) == 1){
							System.out.println("Dentro do if VICMSUFDest");
							BigDecimal vic = new BigDecimal("0");
							vic = this.nfe.getVICMSUFDest().add(this.itemNfe.getVICMSUFDest());
							this.nfe.setVICMSUFDest(vic);
							System.out.println(this.nfe.getVICMSUFDest());
						}
						if (this.itemNfe.getVICMSUFRemet().compareTo(new BigDecimal("0.00")) == 1){
							System.out.println("NfeBean dentro do if VICMSUFRemet");
							BigDecimal VICMSUFRemet = new BigDecimal("0");
							VICMSUFRemet = this.nfe.getVICMSUFRemet().add(this.itemNfe.getVICMSUFRemet());
							this.nfe.setVICMSUFRemet(VICMSUFRemet);
							System.out.println(this.nfe.getVICMSUFRemet());
						}
						System.out.println("NfeBean linha 901 : concluido");
						this.nfe.setValorTotalTributos(this.valorTotalTributos);
						System.out.println(this.nfe.getValorTotalTributos());
						totalBaseIcms = totalBaseIcms.add(this.itemNfe.getBaseICMS().setScale(2,RoundingMode.HALF_EVEN));
						this.nfe.setBaseIcms(totalBaseIcms.setScale(2, RoundingMode.HALF_EVEN));
						this.valorIcms = this.valorIcms.add(this.itemNfe.getValorIcms()).setScale(3, RoundingMode.DOWN);
						this.nfe.setValorIcms(this.valorIcms.setScale(2, RoundingMode.HALF_EVEN));
						totalBaseIcmsSt = totalBaseIcmsSt.add(this.itemNfe.getBaseICMSSt());
						this.nfe.setBaseIcmsSubstituicao(totalBaseIcmsSt);  
						valorIcmsSt = valorIcmsSt.add(this.itemNfe.getValorIcmsSt());
						this.nfe.setValorIcmsSubstituicao(valorIcmsSt.setScale(2, RoundingMode.HALF_EVEN));
						valorTotalProdutos = valorTotalProdutos.add(this.itemNfe.getValorTotalBruto());
						this.nfe.setValorTotalProdutos(valorTotalProdutos.setScale(2, RoundingMode.HALF_EVEN));

						valorTotalIpi = valorTotalIpi.add(this.itemNfe.getValorIPI());
						this.nfe.setValorTotalIpi(valorTotalIpi);
						valorTotalPis = valorTotalPis.add(this.itemNfe.getValorPis());
						this.nfe.setValorTotalPis(valorTotalPis);
						valorTotalCofins = valorTotalCofins.add(this.itemNfe.getValorCofins());
						this.nfe.setValorTotalCofins(this.valorTotalCofins);
						valorTotalNota = valorTotalNota.add(this.itemNfe.getValorTotal()).add(this.itemNfe.getValorFrete()).add(this.itemNfe.getValorSeguro()).add(this.itemNfe.getValorIcmsSt()).add(this.itemNfe.getValorIPI()).add(this.itemNfe.getValorDespesas());
						this.nfe.setValorTotalNota(valorTotalNota.setScale(2, RoundingMode.HALF_EVEN));
						this.totalNfe = this.nfe.getValorTotalProdutos().subtract(this.nfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN);
						this.tempValorUnitario = this.itemNfe.getValorUnitario();
						this.ref = new String();
						this.precoVenda = new BigDecimal("0.00");
						//						this.itemNfe = new ItemNfe();
						this.rowDataBase++;
						if (pegaEnquadramentoEmitente().equals(Enquadramento.SimplesNacional) && !this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
							this.nfe.setBaseIcms(new BigDecimal("0"));
							this.totalBaseIcms = new BigDecimal("0");
							this.nfe.setValorIcms(new BigDecimal("0"));
							this.valorIcms = new BigDecimal("0");
						}
						//			this.nfe.setListaItemNfe(this.listaTemporariaItens);
						//			this.ref = new String();


						//			if (this.nfe.getValorFrete() != null){
						//				this.nfe.getValorFrete().add(this.itemNfe.get);
						//				gerar regra para ratear o frete para todos os itens da nfe
						//			}
						//			if (this.nfe.getValorSeguro() != null ){
						// gerar regra para ratear o seguro para todos os produtos
						//			}
						//			this.nfe.getDesconto().add(this.itemNfe.getDesconto);
						// gerar regra para desconto
						// verificar o campo outrasDespesas acessorias como � informado nos itens
						//			this.temporario = new BigDecimal(this.totalItem.toString());
						//			this.totalNfe = this.totalNfe.add(this.temporario);

						//			this.quantidade = new BigDecimal("0");
						this.listaTemporariaItens.add(this.itemNfe);
						this.addInfo(true, "produto.insert", this.ref);
					}else{
						this.addError(true, "estoque.nfe.emite.item.outOfStock", produto.getDescricao());
					}	
				}else {
					this.addError(true, "produto.notsearch", this.ref);
				}
			}
		}catch (NfeException n) {
			this.addError(true, n.getMessage(), this.ref);
		}catch (Exception e) {
			if (this.nfe.getDestino() != null){
				if (this.produto != null ) {
					if (this.produto.getNcm() == null){
						this.addError(true, "produto.missingInf", this.getRef());
					}
				}
				switch (this.nfe.getTipoPesquisa()) {
					case CLI:
						if (this.destino.getCliente().getEnquadramento() == null){
							this.addError(true, "destinatario.missingEnquadramento", this.destino.getCliente().getRazaoSocial());
						}
						break;
					case FIL:
						if (this.destino.getFilial().getEnquadramento() == null){
							this.addError(true, "destinatario.missingEnquadramento", this.destino.getFilial().getRazaoSocial());
						}
						break;
					case FOR:
						if (this.destino.getFornecedor().getEnquadramento() == null){
							this.addError(true, "destinatario.missingEnquadramento", this.destino.getFornecedor().getRazaoSocial());
						}
						break;
					case MAT:
						if (this.destino.getEmpresa().getEnquadramento() == null){
							this.addError(true, "destinatario.missingEnquadramento", this.destino.getEmpresa().getRazaoSocial());
						}
						break;
					default:
						break;
				}
			}else{
				this.addError(true, "Informe um Destinat�rio");
			}
		}
	}
	/**
	 * Verifica se Numero da NFE esta Disponivel
	 */

	public NumeroSemUtilizacaoNFe numeroNfeDisponivel(Long numero) {
		NumeroSemUtilizacaoNFe numeroDisponivel = new NumeroSemUtilizacaoNFe();
		List<NumeroSemUtilizacaoNFe> listaNumDisponivel = new ArrayList<>();
		listaNumDisponivel = this.numeroDao.retornaListaDeNumeroNFeDisponivel(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		if (!listaNumDisponivel.isEmpty()){
			for (NumeroSemUtilizacaoNFe num : listaNumDisponivel) {
				if (num.getNumeroLivre() == numero) {
					numeroDisponivel = num;
				}
			}
		}
		return numeroDisponivel;
	}

	/**
	 * Regra para definir numero para notaFiscal
	 * 
	 */
	@Transactional
	public Long pegaUltimoNumeroDiponivel() {
		Long numeroDisponivel = 0l;
		int ultimoIndiceDaLista = 0 ;
		NumeroSemUtilizacaoNFe numNFE = new NumeroSemUtilizacaoNFe();
		List<NumeroSemUtilizacaoNFe> listaNumDisponivel = new ArrayList<>();
		listaNumDisponivel = this.numeroDao.retornaListaDeNumeroNFeDisponivel(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		if (getUsuarioAutenticado().getIdFilial() != null){
			if (!listaNumDisponivel.isEmpty()){
				for (NumeroSemUtilizacaoNFe numero : listaNumDisponivel) {
					if (numero.isBloqueado() == false){
						numero.setBloqueado(true);
						this.numeroNFe = this.numeroDao.save(numero);

						break;
					}
				}
				numeroDisponivel = this.numeroNFe.getNumeroLivre();
			}else{
				numeroDisponivel = this.filialDao.findById(getUsuarioAutenticado().getIdFilial(), false).getNumeroNFe();
				numeroDisponivel = numeroDisponivel + 1 ;
				this.numeroNFe.setNumeroLivre(numeroDisponivel);
				this.numeroNFe.setBloqueado(true);
				this.numeroNFe = this.numeroDao.save(this.numeroNFe);
				this.filial = this.filialDao.findById(getUsuarioAutenticado().getIdFilial(), false);
				this.filial.setNumeroNFe(numeroDisponivel);
				this.filial = this.filialDao.save(this.filial);
			}
		}else{
			if (!listaNumDisponivel.isEmpty()){
				for (NumeroSemUtilizacaoNFe numero : listaNumDisponivel) {
					if (numero.isBloqueado() == false){
						numero.setBloqueado(true);
						this.numeroNFe = this.numeroDao.save(numero);
						break;
					}
				}
				numeroDisponivel = this.numeroNFe.getNumeroLivre();
			}else{
				numeroDisponivel = this.empresaDao.findById(getUsuarioAutenticado().getIdEmpresa(), false).getNumeroNFe();
				System.out.println(numeroDisponivel);
				numeroDisponivel = numeroDisponivel + 1 ;
				System.out.println(numeroDisponivel);
				this.numeroNFe.setNumeroLivre(numeroDisponivel);
				this.numeroNFe.setBloqueado(true);
				this.numeroNFe = this.numeroDao.save(this.numeroNFe);
				this.empresa = this.empresaDao.findById(getUsuarioAutenticado().getIdEmpresa(), false);
				this.empresa.setNumeroNFe(numeroDisponivel);
				this.empresa = this.empresaDao.save(this.empresa);
			}
		}

		return numeroDisponivel;

	}


	public void addLacreToList(){
		if( this.lacreTemp  != null){
			this.listaTemporariaLacres.add(this.lacreTemp);
			this.nfe.setListaLacres(this.listaTemporariaLacres);
		}
		limpaLacreTemp();
	}

	@Transactional
	public void removeLacreFromList(){
		if (!this.listaTemporariaLacres.isEmpty()){
			for (int i = 0 ; i <  this.listaTemporariaLacres.size(); i++){
				if (this.listaTemporariaLacres.get(i).getLacre() == this.lacre.getLacre()){
					this.lacreTemp = this.listaTemporariaLacres.get(i);
					this.listaTemporariaLacres.remove(i);
				}
			}
			if (this.viewState == ViewState.EDITING){
				this.lacreDao.delete(this.lacreTemp);
			}
			this.nfe.setListaLacres(this.listaTemporariaLacres);
			limpaLacreTemp();
		}
	}

	@Transactional
	public void preencheParcelamento(){
		try{
			//			nova formula para calculo dos vencimentos 
			// 			d1 d2 d2+(dn*(np*2)) ou criar uma lista com as parcelas e informar a quantidade de dias para cada vencimento
			//			de qualquer forma ser� necess�rio que o cliente informe os campos em FORMA DE PAGAMENTO
			this.listaParcelamento = new ArrayList<>();
			LocalDate hoje = LocalDate.now();
			MathContext precisao = new MathContext(20, RoundingMode.HALF_UP);
			Long numParcela = 1L;
			BigDecimal somaDasParcelas = new BigDecimal("0");
			BigDecimal resultado = new BigDecimal("0");
			BigDecimal valorCadaParcela = new BigDecimal("0");
			BigDecimal valor = this.nfe.getValorTotalNota();
			BigDecimal valorEntrada = new BigDecimal("0");
			ParcelasNfe parcelaTemporaria = new ParcelasNfe();
			String parc= ""+this.formaPag.getParcelas();
			System.out.println("quantidade parcelas " + this.formaPag.getParcelas());
			// s� ir� fazer os c�lculos caso  valor Total da nota for maior que 0 e numero de parcelas tambem maior que zero
			if ((valor.compareTo(new BigDecimal("0")) > 0  && this.formaPag.getParcelas() > 0) || (this.formaPag.getTipoPagamento().equals(TipoPagamento.Spg))){
				if (this.nfe.getNumeroNota() == null){
					this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
				}
				if (this.formaPag.getTipoPagamento().equals(TipoPagamento.Spg)){
					parcelaTemporaria.setNumParcela(1L);
					parcelaTemporaria.setControle(this.nfe.getNumeroNota());
					parcelaTemporaria.setValorParcela(this.nfe.getValorTotalNota());
					parcelaTemporaria.setVencimento(hoje);
					parcelaTemporaria.setFormaPag(this.formaPag);
					parcelaTemporaria.setStatus(ParcelaStatus.NAO);
					parcelaTemporaria.setFinanceiro(false);
					parcelaTemporaria.setConta(this.formaPag.getContaCorrente());
					this.listaParcelamento.add(parcelaTemporaria);
					this.nfe.setFormaPagamento(this.formaPag);
				}else{
					if (this.formaPag.isEntrada()) {
						BigDecimal porcentagemEntrada = new BigDecimal("100").subtract(new BigDecimal(this.formaPag.getValorEntrada().toString())).divide(new BigDecimal("100"),precisao).setScale(2, RoundingMode.HALF_UP);
						valorEntrada = valor.subtract(valor.multiply(new BigDecimal(porcentagemEntrada.toString()),precisao)).setScale(2, RoundingMode.HALF_UP);
						System.out.println("entrada: " + valorEntrada);
						valor = valor.subtract(valorEntrada);
						System.out.println("valor da NFE: " + valor);
						parc = new BigDecimal(parc).subtract(new BigDecimal(1)).toString();
						parcelaTemporaria.setNumParcela(numParcela);
						parcelaTemporaria.setControle(this.nfe.getNumeroNota());
						parcelaTemporaria.setValorParcela(valorEntrada);
						parcelaTemporaria.setVencimento(hoje);
						parcelaTemporaria.setFormaPag(this.formaPag);
						parcelaTemporaria.setStatus(ParcelaStatus.NAO);
						parcelaTemporaria.setFinanceiro(false);
						parcelaTemporaria.setConta(this.formaPag.getContaCorrente());
						this.listaParcelamento.add(parcelaTemporaria);
						parcelaTemporaria = new ParcelasNfe();
						numParcela++;
					}
					valorCadaParcela = valor.divide(new BigDecimal(parc),precisao).setScale(2, RoundingMode.HALF_UP);
					System.out.println("valor de cada parcela com arredondamento para baixo 2 casas decimais" + valorCadaParcela);
					somaDasParcelas = valorCadaParcela.multiply(new BigDecimal(parc)).add(valorEntrada);
					resultado = this.nfe.getValorTotalNota().subtract(somaDasParcelas);
					System.out.println("valor da nota - soma das parcelas = " + resultado);

					for (int i = 0 ; i < new BigDecimal(parc).intValue() ; i++){
						parcelaTemporaria.setFormaPag(this.formaPag);
						parcelaTemporaria.setValorParcela(valorCadaParcela);
						parcelaTemporaria.setStatus(ParcelaStatus.NAO);
						parcelaTemporaria.setFinanceiro(false);
						parcelaTemporaria.setConta(this.formaPag.getContaCorrente());
						if ( i == 0 ){
							System.out.println("Estou no parcela temporaria i = 0 ");
							parcelaTemporaria.setVencimento(hoje.plusDays(this.formaPag.getIntervalo()+this.formaPag.getCarencia()));
						}else{
							System.out.println("estou no parcela Temporaria onde i = " + i);
							parcelaTemporaria.setVencimento(hoje.plusDays((this.formaPag.getIntervalo()* (i+1)+this.formaPag.getCarencia())));
						}
						parcelaTemporaria.setNumParcela(numParcela);
						parcelaTemporaria.setControle(this.nfe.getNumeroNota());
						this.listaParcelamento.add(parcelaTemporaria);
						parcelaTemporaria = new ParcelasNfe();
						numParcela++;
					}
					if ((this.nfe.getValorTotalNota().compareTo(somaDasParcelas) < 0) || (this.nfe.getValorTotalNota().compareTo(somaDasParcelas) > 0)){
						this.listaParcelamento.get(0).setValorParcela(valorCadaParcela.add(resultado));
						System.out.println("Valor da primeira parcela " + this.listaParcelamento.get(0).getValorParcela().toString());
					}
					this.nfe.setFormaPagamento(this.formaPag);
					for (ParcelasNfe parcelasNfe : this.listaParcelamento) {
						System.out.println("Estou dentro do foreach listaParcelamentos");
						System.out.println(parcelasNfe.getVencimento());
						System.out.println(parcelasNfe.getValorParcela());
						System.out.println(parcelasNfe.getFormaPag().getTipoPagamento().name());
					}
				}
			}
			// m�todo que seta as datas anteriormente preenchidas
			if (this.listaTempDatas != null || !this.listaTempDatas.isEmpty()) {
				System.out.println("Estou preenchendo as datas manualmente");
				if (new BigDecimal(this.listaTempDatas.size()).compareTo(new BigDecimal(this.listaParcelamento.size())) == 0) {
					for (int i=0; i < this.listaParcelamento.size() ; i++) {
						this.listaParcelamento.get(i).setVencimento(this.listaTempDatas.get(i));
					}
				}
			}

			this.nfe.setListaParcelas(listaParcelamento);
		}catch (HibernateException h){
			this.addError(true, "N�o foi possivel apagar as parcelas", h.getCause());
		}catch (Exception e) {
			this.addError(true, "N�o sei qual foi a causa do erro", e.getCause());
		}
	}

	@Transactional
	public void geraFinanceiroNFE(Nfe nf) {
		try {
			List<ParcelasNfe> listaParcelas = nfeDao.pegaNfeComParcelas(nf.getId(), pegaIdEmpresa(), pegaIdFilial()).getListaParcelas();
			System.out.println("Total de parcelas: " + listaParcelas.size());
			if (listaParcelas.size() >0) {
				for(Iterator<ParcelasNfe> parc = listaParcelas.iterator();parc.hasNext();) {
					ParcelasNfe parcela = parc.next();
					System.out.println("Adicionando Parcela no recParcial");
					if (parcela.getFormaPag().isIntegraFinanceiro()) {
						if (parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Din) || 
								parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Pix) ||
								parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Dbc) ||
								parcela.getFormaPag().getTipoPagamento().equals(TipoPagamento.Tbc)) {
							parcela.setStatus(ParcelaStatus.REC);
							parcela.setDataRecebimento(LocalDate.now());
						}else {
							parcela.setStatus(ParcelaStatus.ABE);
						}
						parcela.setFinanceiro(true);
						parcela.setConta(parcela.getFormaPag().getContaCorrente());
					}else {
						parcela.setStatus(ParcelaStatus.NAO);
						parcela.setFinanceiro(false);
					}
					parcela.setTipoPagamento(parcela.getFormaPag().getTipoPagamento());
					parcela.setTipoLancamento(TipoLancamento.tpCredito);
					parcela.setQRecorrencia(listaParcelas.size());
					parcela = parcelaDao.save(parcela);
				}
				this.nfe.setFinanceiroGerado(true);
				this.nfeDao.save(this.nfe);
			}else {
				throw new NfeException(this.translate("financial.list.empty"));
			}
			this.addInfo(true,"financial.create.sucess", "NFE: " + nf.getNumeroNota());
		}catch (HibernateException h){
			this.addError(true, "hibernate.persist.fail", h.getCause());
		}catch (NfeException n) {
			this.addError(true, "financeial.error", n.getCause());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getCause());
		}
	}

	public TabelaPreco[] getTabelaPreco(){
		return TabelaPreco.values();
	}

	public void limpaLacreTemp(){
		this.lacreTemp = new Lacre();
	}


	public void preencheTotais(){
		try{
			BigDecimal fundoCombate = new BigDecimal("0");
			BigDecimal fundoCPRetidoST = new BigDecimal("0");
			BigDecimal vfcpufdest = new BigDecimal("0");
			BigDecimal VICMSUFDes = new BigDecimal("0");
			BigDecimal VICMSUFRemet = new BigDecimal("0");
			BigDecimal vIITotal = new BigDecimal("0");
			BigDecimal acrescimoTotalNaParcela = new BigDecimal("0");
			BigDecimal acrescimoPorParcela = new BigDecimal("0");
			BigDecimal totalDasParcelas = new BigDecimal("0");
			this.valorIcms = new BigDecimal("0");
			this.nfe.setBaseIcms(new BigDecimal("0"));
			this.nfe.setVICMSUFDest(new BigDecimal("0"));
			this.nfe.setVICMSUFRemet(new BigDecimal("0"));
			totalBaseIcms = new BigDecimal("0");
			valorTotalProdutos = new BigDecimal("0");
			valorTotalNota = new BigDecimal("0"); 
			valorTotalPis = new BigDecimal("0");
			valorTotalIpi = new BigDecimal("0");
			valorTotalCofins = new BigDecimal("0");
			this.valorTotalTributos = new BigDecimal("0");
			this.nfe.setValorFrete(valorFrete);
			BigDecimal totItens = new BigDecimal(this.listaTemporariaItens.size());
			for (ItemNfe itemNfe : this.listaTemporariaItens) {
				if (this.valorFrete.compareTo(new BigDecimal("0"))>0 && totItens.compareTo(new BigDecimal("0"))>0){
					BigDecimal valorFreteFracionado =  new BigDecimal(this.valorFrete.divide(totItens).toString());
					itemNfe.setValorFrete(valorFreteFracionado);
				}
				itemNfe = this.calculaTributos.preencheImpostos(itemNfe, this.nfe, pegaIdEmpresa(), pegaIdFilial());
				if (itemNfe.getPFCP().compareTo(new BigDecimal("0")) == 1 ){
					System.out.println("NfeBean linha 882 - Dentro do if getPfcp");
					fundoCombate = fundoCombate.add(itemNfe.getVFCP());
					this.nfe.setVFCP(fundoCombate);
					if (itemNfe.getCst() == "60" || itemNfe.getCst() == "500"){
						fundoCPRetidoST = fundoCPRetidoST.add(this.formula.geraSTRetido(itemNfe.getValorTotal().divide(itemNfe.getQuantidade()), itemNfe.getProduto().getUfOrigem(), this.nfe.getUfDestino(), itemNfe.getProduto().getMvaFornecedor()));
						this.nfe.setVFCPSTRet(fundoCPRetidoST);
					}else{
						vfcpufdest = vfcpufdest.add(itemNfe.getVFCPUFDest());
						this.nfe.setVFCPUFDest(vfcpufdest);
					}
				}
				System.out.println("NfeBean linha 891 : calculando Total Tributos");
				this.valorTotalTributos = this.valorTotalTributos.add(itemNfe.getValorTotalTributoItem());
				if (itemNfe.getVICMSUFDest().compareTo(new BigDecimal("0.00")) == 1){
					System.out.println("Dentro do if VICMSUFDest");
					VICMSUFDes = this.nfe.getVICMSUFDest().add(itemNfe.getVICMSUFDest());
					this.nfe.setVICMSUFDest(VICMSUFDes);
					System.out.println(this.nfe.getVICMSUFDest());
				}
				if (itemNfe.getVICMSUFRemet().compareTo(new BigDecimal("0.00")) == 1){
					System.out.println("NfeBean dentro do if VICMSUFRemet");
					VICMSUFRemet = this.nfe.getVICMSUFRemet().add(itemNfe.getVICMSUFRemet());
					this.nfe.setVICMSUFRemet(VICMSUFRemet);
					System.out.println(this.nfe.getVICMSUFRemet());
				}

				System.out.println("NfeBean linha 901 : concluido");
				this.nfe.setValorTotalTributos(this.valorTotalTributos);
				System.out.println(this.nfe.getValorTotalTributos());
				totalBaseIcms = totalBaseIcms.add(itemNfe.getBaseICMS().setScale(2,RoundingMode.HALF_EVEN));
				System.out.println("BaseICMS item :" + itemNfe.getBaseICMS());
				this.nfe.setBaseIcms(totalBaseIcms.setScale(2, RoundingMode.HALF_EVEN));
				System.out.println("Base icms NFE: " + this.nfe.getBaseIcms());
				this.valorIcms = this.valorIcms.add(itemNfe.getValorIcms()).setScale(3, RoundingMode.DOWN);
				System.out.println("Valor total Icms Valor ICMS: " + this.valorIcms);
				System.out.println("Valor item ICMS : " + itemNfe.getValorIcms());
				this.nfe.setValorIcms(this.valorIcms.setScale(2, RoundingMode.HALF_EVEN));
				totalBaseIcmsSt = totalBaseIcmsSt.add(itemNfe.getBaseICMSSt());
				this.nfe.setBaseIcmsSubstituicao(totalBaseIcmsSt);  
				valorIcmsSt = valorIcmsSt.add(itemNfe.getValorIcmsSt());
				this.nfe.setValorIcmsSubstituicao(valorIcmsSt.setScale(2, RoundingMode.HALF_EVEN));
				valorTotalProdutos = valorTotalProdutos.add(itemNfe.getValorTotalBruto());
				this.nfe.setValorTotalProdutos(valorTotalProdutos.setScale(2, RoundingMode.HALF_EVEN));

				valorTotalIpi = valorTotalIpi.add(itemNfe.getValorIPI());
				System.out.println("TOTAL IPI " + this.valorTotalIpi);

				this.nfe.setValorTotalIpi(valorTotalIpi);
				valorTotalPis = valorTotalPis.add(itemNfe.getValorPis());
				System.out.println("TOTAL PIS " + this.valorTotalPis);
				this.nfe.setValorTotalPis(valorTotalPis);
				valorTotalCofins = valorTotalCofins.add(itemNfe.getValorCofins());
				System.out.println("TOTAL Cofins " + this.valorTotalCofins);
				this.nfe.setValorTotalCofins(this.valorTotalCofins);
				if (this.nfe.isImportacao()) {
					this.nfe.setValorTotalNota(totalBaseIcms.setScale(2, RoundingMode.HALF_EVEN));
					vIITotal = vIITotal.add(itemNfe.getIi().getVII());
				}else {
					valorTotalNota = valorTotalNota.add(itemNfe.getValorTotal()).add(itemNfe.getValorFrete()).add(itemNfe.getValorSeguro()).add(itemNfe.getValorIcmsSt()).add(itemNfe.getValorIPI()).add(itemNfe.getValorDespesas());
					this.nfe.setValorTotalNota(valorTotalNota.setScale(2, RoundingMode.HALF_EVEN));
				}
				if (pegaEnquadramentoEmitente().equals(Enquadramento.SimplesNacional) && !this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
					this.nfe.setBaseIcms(new BigDecimal("0"));
					this.totalBaseIcms = new BigDecimal("0");
					this.nfe.setValorIcms(new BigDecimal("0"));
					this.valorIcms = new BigDecimal("0");
				}
			}
			if (this.nfe.isImportacao()) {
				this.nfe.setValorTotalII(vIITotal);
			}
			if (this.nfeFromPedido == false) {
				preencheParcelamento();
			}else {
				if (this.nfe.getNumeroNota() == null){
					this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
					System.out.println("cheia - numero da nota :" + this.nfe.getNumeroNota());
				}

				for (ParcelasNfe parc : this.listaParcelamento) {
					totalDasParcelas = totalDasParcelas.add(parc.getValorParcela());
					parc.setControle(this.nfe.getNumeroNota());
				}
				if (valorTotalNota.compareTo(totalDasParcelas) > 0) {
					acrescimoTotalNaParcela = valorTotalNota.subtract(valorTotalProdutos);
					acrescimoPorParcela = acrescimoTotalNaParcela.divide(new BigDecimal(this.nfe.getListaParcelas().size()),mc).setScale(2,RoundingMode.HALF_EVEN);
					for (ParcelasNfe parc : this.listaParcelamento) {
						parc.setValorParcela(parc.getValorParcela().add(acrescimoPorParcela));
					}
				}
			}
			System.out.println("base icms " + this.totalBaseIcms );
			System.out.println("Total Nota: " + this.valorTotalNota);
		}catch (Exception e){
			this.addError(true, "Erro ao recalcular nota");
		}

	}

	@Transactional
	public String doSalvar(){
		try{
			//			BigDecimal acrescimoTotalNaParcela = new BigDecimal("0");
			//			BigDecimal acrescimoPorParcela = new BigDecimal("0");
			//			BigDecimal totalDasParcelas = new BigDecimal("0");
			System.out.println("IBR - Antes do nfe.getId");
			// armazendando as datas preenchidas manualmente para caso seja feita alguma altera��o n�o seja necess�rio preencher novamente.
			for (ParcelasNfe parcela : this.listaParcelamento) {
				System.out.println("armazendado vencimentos alterados");
				this.listaTempDatas.add(parcela.getVencimento());
			}

			preencheTotais(); // criar uma forma para executar apenas quando salva a primeira vez e nao volte a ser chamado quando transmitir!
			if (this.nfe.getId() == null){
				System.out.println("IBR - Dentro do nfe.getId = null");
				if (this.nfe.getNumeroNota() == null){
					this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
				}
				this.nfe.setEmitente(preencheEmitente());
				//		this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
				this.nfe.setDataSaida(entSai);
				this.nfe.setHoraSaida(horaSai);
				//				this.transp.setNfe(this.nfe);
				this.nfe.setTransportador(this.transp);
				System.out.println("IBR - definindo o status da nfe");
				this.nfe.setStatusEmissao(StatusNfe.SA);
				this.nfe.setListaItemNfe(this.listaTemporariaItens);
				this.nfe.setListaLacres(this.listaTemporariaLacres);
				System.out.println("IBR - setando a lista de itens");

				this.nfe = this.nfeDao.save(this.nfe);
				System.out.println("IBR - salvando a nfe");
				if (this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
					for(NfeReferenciada nfeRef : this.listaTempReferenciada){
						nfeRef.setNfe(this.nfe);
						this.nfeReferenciadaDao.save(nfeRef);
					}
				}
				System.out.println("IBR - salvando os itens da nfe");
				// alterando o pediddo para definir destinatario e lincando a nfe ao pedido
				if (this.nfeFromPedido) {
					this.nfe.setPedido(this.pedido);
					this.nfe.setOrigemPedido(true);
					if (this.nfe.getTipoPesquisa().equals(TipoPesquisa.CLI)) {
						this.pedido.getDestino().setCliente(this.nfe.getDestino().getCliente());
						this.pedido.setNome(this.nfe.getDestino().getCliente().getRazaoSocial());
					}else {
						if (this.nfe.getTipoPesquisa().equals(TipoPesquisa.FOR)) {
							this.pedido.getDestino().setFornecedor(this.nfe.getDestino().getFornecedor());
							this.pedido.setNome(this.nfe.getDestino().getFornecedor().getRazaoSocial());
						}else {
							this.pedido.getDestino().setColaborador(this.nfe.getDestino().getColaborador());
							this.pedido.setNome(this.nfe.getDestino().getColaborador().getNome());
						}
					}
					this.pedido.setFiscalStatus(FiscalStatus.GA);
					this.pedido = this.pedidoDao.save(this.pedido);
				}
				for (ItemNfe item : listaTemporariaItens) {
					item.setNfe(this.nfe);
					this.itemNfeDao.save(item);
				}
				System.out.println("IBR - salvando os lacres da nfe");
				for (Lacre lacre : listaTemporariaLacres) {
					lacre.setNfe(this.nfe);
					this.lacreDao.save(lacre);
					System.out.println("estou dentro do foreach lacre");
				}
				System.out.println("IBR - salvando o pagamento da nfe");
				//				if (this.nfeFromPedido) {
				//					for (ParcelasNfe parc : this.listaParcelamento) {
				//						totalDasParcelas = totalDasParcelas.add(parc.getValorParcela());
				//						parc.setControle(this.nfe.getNumeroNota());
				//					}
				//					if (valorTotalNota.compareTo(totalDasParcelas) > 0  ) {
				//						acrescimoTotalNaParcela = valorTotalNota.subtract(valorTotalProdutos);
				//						acrescimoPorParcela = acrescimoTotalNaParcela.divide(new BigDecimal(this.nfe.getListaParcelas().size()),mc).setScale(2,RoundingMode.HALF_EVEN);
				//						for (ParcelasNfe parc : this.listaParcelamento) {
				//							parc.setValorParcela(parc.getValorParcela().add(acrescimoPorParcela));
				//						}
				//					}
				//				}else {
				for (ParcelasNfe parcelasNfe : this.listaParcelamento) {
					parcelasNfe.setNfe(this.nfe);
					if (!parcelasNfe.getStatus().equals(ParcelaStatus.NAO)) {
						parcelasNfe.setControle(this.nfe.getNumeroNota());
					}
					this.parcelaDao.save(parcelasNfe);
					System.out.println("estou dentro do foreach parcelas");
				}
				//				}
				this.nfe.setListaParcelas(this.listaParcelamento);

				// Baixa no estoque 
				retiraDoEstoque(listaTemporariaItens);

				this.addInfo(true, "save.sucess");

			}else{
				System.out.println("IBR - estou antes no StatusNfe");
				if ((this.nfe.getStatusEmissao() != StatusNfe.EN ) && (this.nfe.getStatusEmissao() != StatusNfe.CA ) && (this.nfe.getStatusEmissao() != StatusNfe.IN)){
					System.out.println("IBR - dentro o if StatusNfe = " + this.nfe.getStatusEmissao().toString());
					this.nfe.setEmitente(preencheEmitente());
					//		this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
					this.nfe.setDataSaida(entSai);
					this.nfe.setHoraSaida(horaSai);
					//				this.transp.setNfe(this.nfe);
					if (this.nfe.getStatusEmissao() == null){
						this.nfe.setStatusEmissao(StatusNfe.SA);
					}
					this.nfe.setFormaPagamento(this.formaPag);
					this.nfe.setTransportador(this.transp);
					this.nfe.setListaItemNfe(this.listaTemporariaItens);
					this.nfe = this.nfeDao.save(this.nfe);
					if (this.numeroNFe.isBloqueado() == false){
						this.numeroNFe.setBloqueado(true);
						this.numeroNFe = this.numeroDao.save(this.numeroNFe);
					}
					if (this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
						for(NfeReferenciada nfeRef : this.listaTempReferenciada){
							nfeRef.setNfe(this.nfe);
							this.nfeReferenciadaDao.save(nfeRef);
						}
					}
					//					alterando o pediddo para definir destinatario e lincando a nfe ao pedido
					if (this.nfe.isOrigemPedido()) {
						//						this.nfe.setPedido(this.pedido);
						//						this.nfe.setOrigemPedido(true);
						if (this.nfe.getTipoPesquisa().equals(TipoPesquisa.CLI)) {
							this.pedido.getDestino().setCliente(this.nfe.getDestino().getCliente());
							this.pedido.setNome(this.nfe.getDestino().getCliente().getRazaoSocial());
						}else {
							if (this.nfe.getTipoPesquisa().equals(TipoPesquisa.FOR)) {
								this.pedido.getDestino().setFornecedor(this.nfe.getDestino().getFornecedor());
								this.pedido.setNome(this.nfe.getDestino().getFornecedor().getRazaoSocial());
							}else {
								this.pedido.getDestino().setColaborador(this.nfe.getDestino().getColaborador());
								this.pedido.setNome(this.nfe.getDestino().getColaborador().getNome());
							}
						}
						this.pedido.setFiscalStatus(FiscalStatus.GA);
						this.pedido = this.pedidoDao.save(this.pedido);
					}
					if (this.listaItensPersistidosExcluir.size() > 0) {
						for (ItemNfe itemExcluir : this.listaItensPersistidosExcluir) {
							this.itemNfeDao.delete(itemExcluir);
						}
					}
					for (ItemNfe item : listaTemporariaItens) {
						item.setNfe(this.nfe);
						this.itemNfeDao.save(item);
					}
					for (Lacre lacre : listaTemporariaLacres) {
						lacre.setNfe(this.nfe);
						this.lacreDao.save(lacre);
						System.out.println("estou dentro do foreach lacre");
					}
					// Exclui as antigas parcelas SE a listaTemp for diferente da listaParcelas
					if (this.listaTempParcelas != null || !this.listaTempParcelas.isEmpty()){
						if (!this.listaParcelamento.containsAll(this.listaTempParcelas)){
							for (ParcelasNfe parcela : this.listaTempParcelas) {
								if (parcela.getId() != null){
									this.parcelaDao.delete(parcela);
								}
							}
						}
					}
					for (ParcelasNfe parcelasNfe : listaParcelamento) {
						parcelasNfe.setNfe(this.nfe);
						parcelasNfe.setControle(this.nfe.getNumeroNota());
						if (!parcelasNfe.getStatus().equals(ParcelaStatus.NAO)) {
							//							parcelasNfe.setControle(this.nfe.getNumeroNota());
						}
						this.parcelaDao.save(parcelasNfe);
						System.out.println("estou dentro do foreach parcelas");
					}
					this.addInfo(true, "save.update");
				}else{
					this.addWarning(true, "error.update.nfeUso");
				}
			}
			return toListNfe();
		}catch (Exception e){
			liberaNumeroNfe();
			return toListNfe();
		}

	}

	@Transactional
	public void transmiteNfe(){
		try {
			if (!this.nfe.getStatusEmissao().equals(StatusNfe.CA) && !this.nfe.getStatusEmissao().equals(StatusNfe.EN)){
				String retorno ="";
				String CStat="";
				String resulXmotivo="";
				String protocolo="";
				String caminhoXML="";
				String resultado="";
				String chave="";
				String caminhoProv="";
				String res="";				

				//				this.infConexao = new DadosDeConexaoSocket("localhost", 3434);

				retorno = this.acbr.enviaComandoACBr(this.infConexao, "NFe.CriarEnviarNFe(\""+"C:\\ibrcomp\\tmp\\"+this.nomeArquivo+".ini\",1,1,1,,0,,1)").toUpperCase();
				//			retorno = this.acbr.enviaNFe(this.infConexao, "NFe.EnviarNFe("+this.nfe.getCaminhoXml()+",1,1,1,1)");
				System.out.println("resposta do envia nfe: " + this.respostaAcbrLocal);
				System.out.println("retorno " +retorno);
				this.respostaAcbrLocal = retorno;
				List<String> linhasRetorno = linhaTexto(retorno);
				for(String lin : linhasRetorno) {
					if (localiza.localizaPalavra(lin, "OK:")){
						this.path = lin.substring(3,lin.length()).trim();
						System.out.println("path capturado: " + this.path);
					}
				}
				if (localiza.localizaPalavra(retorno, "ENVIO")) {
					if (localiza.localizaPalavra(retorno, "NFE"+this.nfe.getNumeroNota())) {
						resultado = retorno.substring(retorno.indexOf("NFE"+this.nfe.getNumeroNota()));
						// melhorando o codigo para que nao quebre quando atualizar o acbr
						List<String> linhasArquivo = linhaTexto(resultado);
						for (String string : linhasArquivo) {
							System.out.println("String linha arquivo: " + string);
							if (localiza.localizaPalavra(string, "CSTAT=")){
								CStat = string.substring(6).trim();
							}
							if (localiza.localizaPalavra(string, "XMOTIVO=")){
								resulXmotivo = string.substring(8).trim();
							}
							if (localiza.localizaPalavra(string, "NPROT=")){
								protocolo = string.substring(6).trim();
							}
							if (localiza.localizaPalavra(string, "CHDFE=") || localiza.localizaPalavra(string, "CHNFE=")){
								chave = string.substring(6).trim();
							}
							System.out.println("Chave: "+ chave);
							System.out.println("exibindo Resultado:" + resultado);
							System.out.println("Protocolo: "+protocolo);
						}
					}else {
//						resultado = retorno.substring(retorno.indexOf("ENVIO"));
						List<String> linhasArquivo = linhaTexto(retorno);
						for (String string : linhasArquivo) {
							if (localiza.localizaPalavra(string, "CSTAT=")){
								CStat = string.substring(6).trim();
							}
							if (localiza.localizaPalavra(string, "XMOTIVO=")){
								resulXmotivo = string.substring(8).trim();
							}
							if (localiza.localizaPalavra(string, "OK:")){
								System.out.println("path capturado: " + this.path);
								chave = string.substring(this.path.length()-48,this.path.length()-4).trim();
								System.out.println("chave preenchida ENVIO: " + chave);
							}
						}
						
					}
					System.out.println("CodigoCstat: " +CStat + "tam: "+ CStat.length());
					System.out.println("Xmotivo: "+resulXmotivo);
				}
				System.out.println("Path caminho: " + this.path);
				//			this.path = this.acbr.enviaComandoACBr(this.infConexao, "NFe.GetPathNFe").substring(4);
				BigDecimal codigo = new BigDecimal(CStat.trim());
				if (codigo.compareTo(new BigDecimal("100")) == 0 || codigo.compareTo(new BigDecimal("103")) == 0 
						|| codigo.compareTo(new BigDecimal("104")) == 0 || codigo.compareTo(new BigDecimal("105")) == 0){
					System.out.println("Estou dentro do CStat = 100");
					caminhoXML = this.path.trim();
					this.nfe.setCaminhoXml(chave.trim()+"-nfe.xml");
					this.nfe.setStatusEmissao(StatusNfe.EN);
					this.nfe.setChaveAcesso(chave);
					this.nfe.setRespostaFinalAcbr(resulXmotivo);

					this.nfe.setProtocoloAutorizacao(protocolo);
					this.acbr.enviaComandoACBr(this.infConexao, "NFE.ImprimirDANFEPDF("+caminhoXML+")");
					//				enviaEmail();
				}else{
					if (codigo.compareTo(new BigDecimal("204") ) == 0){
						this.path = this.acbr.enviaComandoACBr(this.infConexao, "NFe.GetPathNFe").substring(4);
						//					caminhoXML = this.acbr.enviaComandoACBr(this.infConexao, "NFe.GetPathNFe").substring(4);
						caminhoProv = this.path +"\\"+chave.trim()+"-NFE.XML"; 
						System.out.println("caminho xml: " + caminhoProv);
						res = this.acbr.enviaComandoACBr(this.infConexao,"ACBr.FilesExists("+caminhoProv+")").substring(4).trim().toUpperCase();
						this.addInfo(true, res);
						System.out.println(res);
						if (res.trim().equalsIgnoreCase("TRUE")){
							this.nfe.setCaminhoXml(chave.trim()+"-nfe.xml");
							this.nfe.setStatusEmissao(StatusNfe.EN);
							this.nfe.setChaveAcesso(chave);
						}
					}else{
						this.nfe.setStatusEmissao(StatusNfe.EE);
					}
				}

				System.out.println(this.nfe.getCaminhoXml());
			}else {
				this.addWarning(true, "nfe.transmit.fail");
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e.getStackTrace() +"  Mensagem: " + e.getMessage());
		}
	}


	@Transactional
	public void cancelaNfe(){
		try {
			String retorno="";
			String CStat="";
			String resulXmotivo="";
			String protocolo="";
			String resposta="";
			String arquivo="";
			int procuraIni = 0;
			int procuraFim=0;
			int procuraXmotivo = 0;
			int procuraProtocolo = 0;
			if (this.nfe.getStatusEmissao() == StatusNfe.CA){
				this.addWarning(true, "nfe.cancel.isCancel");
			}else{
				//				this.nfe = this.nfeDao.findById(this.nfe.getId(), false);
				//				this.nfe.setListaLacres(this.lacreDao.findLacreForNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
				//				this.nfe.setListaParcelas(this.parcelaDao.listaParcelasPorNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
				//				this.nfe.setListaItemNfe(this.itemNfeDao.listaItensPorNfe(this.nfe));
				//				this.setTransp(this.nfe.getTransportador());
				//				this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
				if (getUsuarioAutenticado().getIdFilial() == null){
					retorno = this.acbr.enviaComandoACBr(this.infConexao, "NFe.CancelarNFe("+this.nfe.getChaveAcesso()+","+this.justificativa+","+ removerAcentos(this.nfe.getEmitente().getEmpresa().getCnpj())+")").toUpperCase();
				}else{
					retorno = this.acbr.enviaComandoACBr(this.infConexao, "NFe.CancelarNFe("+this.nfe.getChaveAcesso()+","+this.justificativa+","+removerAcentos(this.nfe.getEmitente().getFilial().getCnpj()+")")).toUpperCase();
				}
				if (retorno.substring(0,4).equals("ERRO")){
					this.addError(true, "error.cancel.nfe");
				}else{
					procuraIni = retorno.indexOf("[CANCELAMENTO]");
					resposta = retorno.substring(procuraIni);

					// melhorando o codigo para que nao quebre quando atualizar o acbr
					List<String> linhasArquivo = linhaTexto(resposta);
					for (String string : linhasArquivo) {
						if (localiza.localizaPalavra(string, "CSTAT=")){
							CStat = string.substring(6).trim();
						}
						if (localiza.localizaPalavra(string, "NPROT=")){
							protocolo = string.substring(6).trim();
						}
						if (localiza.localizaPalavra(string, "ARQUIVO=")){
							arquivo = string.substring(8).trim();
						}

					}
					BigDecimal codigo = new BigDecimal(CStat.trim());
					System.out.println(codigo + " = antes do if");
					if (codigo.compareTo(new BigDecimal("135")) == 0 || codigo.compareTo(new BigDecimal("128")) == 0 || codigo.compareTo(new BigDecimal("136")) == 0 || codigo.compareTo(new BigDecimal("155")) == 0){
						System.out.println("estou dentro do if CStat");
						this.path = this.acbr.enviaComandoACBr(this.infConexao,"NFe.GetPathCan").substring(4);
						arquivo = arquivo.substring(this.path.length());
						this.respostaAcbrLocal = resposta;
						this.nfe.setCaminhoCancelado(arquivo);
						this.nfe.setProtCancelado(protocolo);
						this.nfe.setMotivoCancelado(this.justificativa);
						this.nfe.setRespostaFinalAcbr(this.respostaAcbrLocal);
						this.nfe.setStatusEmissao(StatusNfe.CA);
						this.nfe = this.nfeDao.save(this.nfe);
						this.listaTemporariaItens = this.itemNfeDao.listaItensPorNfe(this.nfe);
						devolveParaEstoqueCancelaNFE(this.listaTemporariaItens);
					}
					System.out.println(CStat);

				}
				this.respostaAcbrLocal="Resultado: "+retorno;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public List<String> linhaTexto(String texto){
		List<String> linhaString = new ArrayList<>();
		Scanner sc = new Scanner(texto).useDelimiter("\\n");
		while(sc.hasNext()){
			linhaString.add(sc.next());
		}
		sc.close();
		return linhaString;
	}

	@Transactional
	public void emiteXML() throws IOException,NfeException{
		//		try{
		String resp;
		System.out.println("EmiteXML ibr");
		this.nfe = this.nfeDao.pegaNfe(this.nfe.getId(), pegaIdEmpresa(),pegaIdFilial());
		System.out.println("pesquisa nota EmiteXML ibr");
		this.nfe.setListaLacres(this.lacreDao.findLacreForNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
		//			this.nfe.setListaItemNfe(this.itemNfeDao.listaItensPorNfe(this.nfe));
		this.nfe.setFormaPagamento(this.formaPagDao.findById(this.nfe.getFormaPagamento().getId(), false));
		this.nfe.setListaParcelas(this.parcelaDao.listaParcelasPorNfe(this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
		//			this.nfe.setListaItemNfe(this.itemNfeDao.listaItensPorNfe(this.nfe));
		//			this.listaTemporariaItens = this.nfe.getListaItemNfe();
		//			this.listaParcelamento = this.nfe.getListaParcelas();
		//			this.listaTemporariaLacres = this.nfe.getListaLacres();
		System.out.println("Antes de inserir transporte ibr");
		this.setTransp(this.nfe.getTransportador());
		System.out.println("EmiteXML ibr depois de inserir transporte");
		this.nomeArquivo = this.getUsuarioAutenticado().getName()+this.getUsuarioAutenticado().getIdEmpresa();

		if (this.nfe.getNumeroNota() == null){
			this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
		}
		System.out.println("EmiteXML ibr antes de gerar arquivo");
		geraNfe();
		System.out.println("EmiteXML ibr depois de gerar arquivo");
		this.nfe.setRespostaAcbr(this.respostaAcbrLocal);
		resp = this.respostaAcbrLocal.substring(0,2);
		System.out.println(resp);
		if (resp.equals("OK")){
			transmiteNfe();
			this.nfe.setRespostaFinalAcbr(this.respostaAcbrLocal);
		}else{
			this.nfe.setRespostaAcbr(this.respostaAcbrLocal);
		}
		salvaTransmitido();
		//		}catch (Exception e){
		//			salvaTransmitido();
		//			this.addError(true, "error.geraxml");
		//		}
	}

	@Transactional
	public String salvaTransmitido() {
		try {
			if (this.nfe.getId() == null){
				System.out.println("IBR - Dentro do nfe.getId = null");
				if (this.nfe.getNumeroNota() == null){
					this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
				}
				this.nfe.setEmitente(preencheEmitente());
				//		this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
				this.nfe.setDataSaida(entSai);
				this.nfe.setHoraSaida(horaSai);
				//				this.transp.setNfe(this.nfe);
				this.nfe.setTransportador(this.transp);
				System.out.println("IBR - definindo o status da nfe");
				this.nfe.setStatusEmissao(StatusNfe.SA);
				this.nfe.setListaItemNfe(this.listaTemporariaItens);
				this.nfe.setListaParcelas(this.listaParcelamento);
				this.nfe.setListaLacres(this.listaTemporariaLacres);
				System.out.println("IBR - setando a lista de itens");

				this.nfe = this.nfeDao.save(this.nfe);
				System.out.println("IBR - salvando a nfe");
				if (this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
					for(NfeReferenciada nfeRef : this.listaTempReferenciada){
						nfeRef.setNfe(this.nfe);
						this.nfeReferenciadaDao.save(nfeRef);
					}
				}
				System.out.println("IBR - salvando os itens da nfe");
				for (ItemNfe item : listaTemporariaItens) {
					item.setNfe(this.nfe);
					this.itemNfeDao.save(item);
				}
				System.out.println("IBR - salvando os lacres da nfe");
				for (Lacre lacre : listaTemporariaLacres) {
					lacre.setNfe(this.nfe);
					this.lacreDao.save(lacre);
					System.out.println("estou dentro do foreach lacre");
				}
				System.out.println("IBR - salvando o pagamento da nfe");
				for (ParcelasNfe parcelasNfe : listaParcelamento) {
					parcelasNfe.setNfe(this.nfe);
					parcelasNfe.setControle(this.nfe.getNumeroNota());
					this.parcelaDao.save(parcelasNfe);
					System.out.println("estou dentro do foreach parcelas");
				}
				this.addInfo(true, "save.sucess");

			}else{
				System.out.println("IBR - estou antes no StatusNfe");
				if ((this.nfe.getStatusEmissao() != StatusNfe.EN ) && (this.nfe.getStatusEmissao() != StatusNfe.CA ) && (this.nfe.getStatusEmissao() != StatusNfe.IN)){
					System.out.println("IBR - dentro o if StatusNfe = " + this.nfe.getStatusEmissao().toString());
					this.nfe.setEmitente(preencheEmitente());
					//		this.nfe.setNumeroNota(pegaUltimoNumeroDiponivel());
					this.nfe.setDataSaida(entSai);
					this.nfe.setHoraSaida(horaSai);
					//				this.transp.setNfe(this.nfe);
					if (this.nfe.getStatusEmissao() == null){
						this.nfe.setStatusEmissao(StatusNfe.SA);
					}
					this.nfe.setFormaPagamento(this.formaPag);
					this.nfe.setTransportador(this.transp);
					this.nfe.setListaItemNfe(this.listaTemporariaItens);
					
					this.nfe = this.nfeDao.save(this.nfe);
					
					if (this.numeroNFe.isBloqueado() == false){
						this.numeroNFe.setBloqueado(true);
						this.numeroNFe = this.numeroDao.save(this.numeroNFe);
					}
					if (this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
						for(NfeReferenciada nfeRef : this.listaTempReferenciada){
							nfeRef.setNfe(this.nfe);
							this.nfeReferenciadaDao.save(nfeRef);
						}
					}

					for (ItemNfe item : listaTemporariaItens) {
						item.setNfe(this.nfe);
						this.itemNfeDao.save(item);
					}
					for (Lacre lacre : listaTemporariaLacres) {
						lacre.setNfe(this.nfe);
						this.lacreDao.save(lacre);
						System.out.println("estou dentro do foreach lacre");
					}
					// Exclui as antigas parcelas SE a listaTemp for diferente da listaParcelas

					if (this.listaTempParcelas != null || !this.listaTempParcelas.isEmpty()){
						if (!this.listaParcelamento.containsAll(this.listaTempParcelas)){
							for (ParcelasNfe parcela : this.listaTempParcelas) {
								if (parcela.getId() != null){
									this.parcelaDao.delete(parcela);
								}
							}	
						}
					}
					for (ParcelasNfe parcelasNfe : listaParcelamento) {
						parcelasNfe.setNfe(this.nfe);
						parcelasNfe.setControle(this.nfe.getNumeroNota());
						this.parcelaDao.save(parcelasNfe);
						System.out.println("estou dentro do foreach parcelas");
					}
					this.addInfo(true, "save.update");
				}else{
					this.addWarning(true, "error.update.nfeUso");
				}
			}

			if (this.nfe.isOrigemPedido()) {
				this.pedido = this.pedidoDao.pegaPedidoPorId(this.nfe.getPedido().getId());
				this.pedido.setFiscalStatus(FiscalStatus.GT);
				this.pedidoDao.save(this.pedido);
			}
			return toListNfe();
		}catch (Exception e){
			//		liberaNumeroNfe();
			return toListNfe();
		}
	}

	@Transactional
	public void liberaNumeroNfe(){
		try{
			this.numeroNFe.setBloqueado(false);
			this.numeroDao.save(this.numeroNFe);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Transactional
	public void doExcluir(){
		try{
			if (this.isPodeExcluir()){
				if (this.nfe.getId() != null){
					//					this.nfe = this.nfeDao.findById(this.nfe.getId(), false);
					System.out.println(this.nfe.getNumeroNota());

					if (this.nfe.getStatusEmissao() != StatusNfe.EN || this.nfe.getStatusEmissao() != StatusNfe.CA){
						if (this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
							for (int i = 0; this.nfe.getListaChavesReferenciada().size() > i ; i++){
								this.nfeReferenciadaDao.delete(this.nfe.getListaChavesReferenciada().get(i));
							}
							//							for (NfeReferenciada nfRef : this.nfe.getListaChavesReferenciada()) {
							//								this.nfeReferenciadaDao.delete(nfRef);
							//							}
						}
						this.numeroNFe =this.numeroDao.retornaNumero(this.nfe.getNumeroNota(), getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
						this.listaTemporariaItens = this.itemNfeDao.listaItensPorNfe(this.nfe);
						this.numeroNFe.setBloqueado(false);
						this.numeroDao.save(this.numeroNFe);
						if (!this.listaTemporariaItens.isEmpty()){
							// devolve para o estoque fiscal
							devolveParaEstoque(this.listaTemporariaItens);
							System.out.println("estou dentro do lista itens");
							for (ItemNfe item : this.listaTemporariaItens) {
								this.itemNfeDao.delete(item);					
							}
						}
						this.nfeDao.delete(this.nfe);
						System.out.println(this.nfe.getNumeroNota());
						if (this.nfe.isOrigemPedido()) {
							this.pedido = this.pedidoDao.pegaPedidoPorId(this.nfe.getPedido().getId());
							this.pedido.setFiscalStatus(FiscalStatus.NE);
							this.pedidoDao.save(this.pedido);
						}
						this.addInfo(true, "delete.sucess", this.nfe.getNumeroNota());
					}else{
						this.addError(true, "error.delete.nf", this.nfe.getNumeroNota());
					}
				}
			}else{
				this.addInfo(true, "error.nfe.delete");
			}
		}catch(Exception e){
			this.addError(true, "error.delete" + e, this.nfe.getNumeroNota());
		}

	}

	@Transactional
	public void enviaCCe(){
		try{
			String resp="";
			String cstat="";
			String nProt="";
			String xMotivo="";
			String arquivo="";
			int posA = 0 ;
			this.cce.setNfe(this.nfe);
			//			this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
			this.cce.setRespostaAcbrEvento(this.acbr.criaCCe(this.cce,this.infConexao, this.nomeArquivo).toUpperCase());
			if (this.cce.getRespostaAcbrEvento().trim().equalsIgnoreCase("OK:")){
				this.cce.setRespostaAcbrEvento(this.acbr.enviaComandoACBr(this.infConexao, "NFe.EnviarEvento(\""+"C:\\ibrcomp\\tmp\\cce"+this.nomeArquivo+".ini\")").toUpperCase());
				posA = this.cce.getRespostaAcbrEvento().indexOf("[EVENTO001]");
				resp = this.cce.getRespostaAcbrEvento().substring(posA);
				List<String> linhasResposta = linhaTexto(resp);
				for (String string : linhasResposta) {
					if (localiza.localizaPalavra(string, "CSTAT=")){
						cstat = string.substring(6).trim();
					}
					if (localiza.localizaPalavra(string, "XMOTIVO=")){
						xMotivo = string.substring(8).trim();
					}
					if (localiza.localizaPalavra(string, "NPROT=")){
						nProt = string.substring(6).trim();
					}
					if (localiza.localizaPalavra(string, "ARQUIVO=")){
						arquivo = string.substring(8).trim();
					}
				}
				System.out.println("Lendo resposta: " +resp);
				System.out.println("Lendo cstat: " + cstat);
				System.out.println("Lendo xMotivo: "+ xMotivo);
				System.out.println("Lendo nProt: " + nProt);
				if (cstat.trim().equalsIgnoreCase("135")){
					this.path = this.acbr.enviaComandoACBr(this.infConexao,"NFe.GetPathCCE").substring(4);

					arquivo = arquivo.substring(this.path.length());
					System.out.println(arquivo);
					this.cce.setCaminhoEvento(arquivo.trim());
					this.cce.setProtocolo(nProt);
					this.cce.setXMotivo(xMotivo);
					imprimirCCe();
					this.cce = this.cceDao.save(this.cce);
					this.addInfo(true, "save.event.sucess");
				}

			}
		}catch (Exception e){
			System.out.println(e);
		}
	}

	public void imprimirCCe() throws IOException{
		//		this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
		this.acbr.enviaComandoACBr(this.infConexao,"NFe.ImprimirEvento("+this.path+this.cce.getCaminhoEvento()+","+this.cce.getNfe().getCaminhoXml()+",\"\",2,false)");
	}

	public void enviaEmail(){
		try{
			//			this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
			if (this.nfe.getDestino().getCliente() != null){
				this.acbr.enviaComandoACBr(this.infConexao,"NFe.EnviarEmail("+this.nfe.getDestino().getCliente().getEmailNFE().getEmail()+","+this.nfe.getCaminhoXml()+",1)");
			}else if (this.nfe.getDestino().getFornecedor() != null){ // fornecedor
				this.acbr.enviaComandoACBr(this.infConexao,"NFe.EnviarEmail("+this.nfe.getDestino().getFornecedor().getEmailNFE().getEmail()+","+this.nfe.getCaminhoXml()+",1)");
			}else if (this.nfe.getDestino().getEmpresa() != null){ // empresa
				this.acbr.enviaComandoACBr(this.infConexao,"NFe.EnviarEmail("+this.nfe.getDestino().getEmpresa().getEmailNFE().getEmail()+","+this.nfe.getCaminhoXml()+",1)");
			}else if (this.nfe.getDestino().getFilial() != null){ // filial
				this.acbr.enviaComandoACBr(this.infConexao,"NFe.EnviarEmail("+this.nfe.getDestino().getFilial().getEmailNFE().getEmail()+","+this.nfe.getCaminhoXml()+",1)");
			}else { // colaborador
				this.acbr.enviaComandoACBr(this.infConexao,"NFe.EnviarEmail("+this.nfe.getDestino().getColaborador().getEmail().getEmail()+","+this.nfe.getCaminhoXml()+",1)");
			}
			this.addInfo(true, "send.email.nf");
		}catch (Exception e){
			System.out.println(e);
		}
	}

	public void enviaEmailAvulso() throws IOException{
		//		this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
		this.respostaAcbrLocal = this.acbr.enviaComandoACBr(this.infConexao,"NFe.EnviarEmail("+this.email+","+this.nfe.getCaminhoXml()+",1)");
	}

	public void imprimiNfe() throws IOException{
		//		this.infConexao = new DadosDeConexaoSocket("localhost", 3434);
		LocalDate dataAtual = LocalDate.now();
		String mesAtual = new BigDecimal(dataAtual.getMonthValue()).toString();
		String anoAtual = new BigDecimal(dataAtual.getYear()).toString();
		System.out.println(mesAtual);
		System.out.println(anoAtual);
		if (mesAtual.length() <2){
			mesAtual = "0"+mesAtual;
		}
		String mesAnoAtual = anoAtual+mesAtual;
		System.out.println(mesAnoAtual);

		String  pastaMes =	new BigDecimal(this.nfe.getDataEmissao().getMonthValue()).toString();
		String pastaAno = new BigDecimal(this.nfe.getDataEmissao().getYear()).toString();
		String caminhoOriginal; 
		if (pastaMes.length() <2){
			pastaMes = "0"+pastaMes;
		}

		String anoMesNfe = pastaAno+pastaMes;
		caminhoOriginal = this.acbr.enviaComandoACBr(this.infConexao, "NFe.GetPathNFe");

		String alteraPasta = caminhoOriginal.replace(mesAnoAtual, anoMesNfe).substring(4);
		System.out.println(alteraPasta);
		this.acbr.enviaComandoACBr(this.infConexao, "NFE.ImprimirDanfe("+alteraPasta+"\\"+this.nfe.getCaminhoXml()+")");
	}

	public Uf[] listaUf(){
		return Uf.values();
	}

	public void insereListaReferenciada(){
		try {
			boolean jaExiste = false;
			if (!this.nfeRefTemp.getChaveReferenciada().isEmpty() || !this.nfeRefTemp.getChaveReferenciada().isBlank()||this.nfeRefTemp.getChaveReferenciada()!= null) {
				if (this.listaTempReferenciada.size() > 0){
					for (int i = 0 ; this.listaTempReferenciada.size() > i;i++ ){

						for (NfeReferenciada ref : this.listaTempReferenciada) {
							if (ref.getChaveReferenciada() != "" || ref.getChaveReferenciada()!= null) {
								if (ref.getChaveReferenciada().equalsIgnoreCase(this.nfeRefTemp.getChaveReferenciada())){
									jaExiste=true;
								}
							}
						}
						if (jaExiste == true){
							this.addInfo(true, "error.add.nfeRef.exits");
						}else{
							if (this.nfeRefTemp.getChaveReferenciada() != "" || this.nfeRefTemp.getChaveReferenciada()!= null) {
								this.listaTempReferenciada.add(this.nfeRefTemp);
							}else {
								throw new Exception( this.translate("nfe.chaveReferenciadaIsNull"));
							}
						}
					}
				}else{
					this.listaTempReferenciada.add(this.nfeRefTemp);
				}
				this.nfeRefTemp = new NfeReferenciada();
			}else {
				throw new Exception( this.translate("nfe.chaveReferenciadaIsNull"));
			}
		}catch (Exception e) {
			this.addErrorNew(true, "messageDialog", "caixa.error",e.getMessage());
		}
	}

	public void removeNfeReferenciada(){
		if (!this.listaTempReferenciada.isEmpty()){
			for (int i = 0 ; i <  this.listaTempReferenciada.size(); i++){
				if (this.listaTempReferenciada.get(i).getChaveReferenciada() == this.nfeRefTemp.getChaveReferenciada()){
					this.listaTempReferenciada.remove(i);
				}
			}
			this.nfeRefTemp = new NfeReferenciada();
		}
	}

	public void saveReferenciada(){
		this.nfe.setListaChavesReferenciada(this.listaTempReferenciada);
	}

	public void onRowSelectNfeReferenciada(SelectEvent event)throws IOException{
		this.nfeRefTemp = (NfeReferenciada) event.getObject();
	}

	public void excluiChaveReferenciada(NfeReferenciada nfRef) {
		this.listaTempReferenciada.remove(nfRef);
	}

	public void exibeNfeReferenciada(){
		if (!(this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.NO))){
			this.updateAndOpenDialog("nfeReferenciadaId", "dialogNfeReferenciada");
		}
	}

	public void clienteRetira(){


		if (this.nfe.isClienteRetira()){
			switch (this.nfe.getTipoPesquisa()) {
				case CLI:
					this.transporte.setRazaoSocial(this.destino.getCliente().getRazaoSocial());
					this.transporte.setEndereco(this.destino.getCliente().getEndereco());
					if (this.destino.getCliente().getCnpj() == null){
						this.transporte.setCpf(this.destino.getCliente().getCpf());
					}else{
						this.transporte.setCnpj(this.destino.getCliente().getCnpj());
						this.transporte.setInscEstadual(this.destino.getCliente().getInscEstadual());
					}	
					break;
				case FIL:
					this.transporte.setRazaoSocial(this.destino.getFilial().getRazaoSocial());
					this.transporte.setEndereco(this.destino.getFilial().getEndereco());
					this.transporte.setCnpj(this.destino.getFilial().getCnpj());
					this.transporte.setInscEstadual(this.destino.getFilial().getInscEstadual());
					break;
				case FOR:
					this.transporte.setRazaoSocial(this.destino.getFornecedor().getRazaoSocial());
					this.transporte.setEndereco(this.destino.getFornecedor().getEndereco());
					if (this.destino.getFornecedor().getCnpj().isEmpty()){
						this.transporte.setCpf(this.destino.getFornecedor().getCpf());
					}else{
						this.transporte.setCnpj(this.destino.getFornecedor().getCnpj());
						this.transporte.setInscEstadual(this.destino.getFornecedor().getInscEstadual());
					}	
					break;
				case MAT:
					this.transporte.setRazaoSocial(this.destino.getEmpresa().getRazaoSocial());
					this.transporte.setEndereco(this.destino.getEmpresa().getEndereco());
					this.transporte.setCnpj(this.destino.getEmpresa().getCnpj());
					this.transporte.setInscEstadual(this.destino.getEmpresa().getInscEstadual());
					break;

				default:
					break;
			}  

			if (this.transporte.getCnpj() == null){
				this.transp.setRetiraDoc(this.transporte.getCpf());
			}else{
				this.transp.setRetiraDoc(this.transporte.getCnpj());
				this.transp.setRetiraInsc(this.transporte.getInscEstadual());
			}
			this.transp.setRetiraNome(this.transporte.getRazaoSocial());
			if (this.transporte.getEndereco().getLogradouro() == "" || this.transporte.getEndereco().getLogradouro().isEmpty()) {
				this.transp.setRetiraEnd(this.transporte.getEndereco().getEndereco().getLogra());
			}else {
				this.transp.setRetiraEnd(this.transporte.getEndereco().getLogradouro());
			}
			this.transp.setRetiraMunicipio(this.transporte.getEndereco().getEndereco().getLocalidade());
			this.transp.setRetiraUf(this.transporte.getEndereco().getEndereco().getUf().name());
		}else{
			this.transporte = new Transportadora();
			System.out.println("zerei o transporte");
		}
	}

	public Enquadramento pegaEnquadramentoEmitente(){
		Filial filialTemp = new Filial();
		Empresa empresaTemp = new Empresa();

		if (this.getUsuarioAutenticado().getIdFilial() !=null){
			filialTemp = this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false);
			return filialTemp.getEnquadramento();
		}else{
			empresaTemp = this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
			return empresaTemp.getEnquadramento();
		}
	}

	public void  telaInutiliza(){
		this.updateAndOpenDialog("inuNfeId", "dialogInuNfe");
	}

	/*
	 * Fun��o geraInutilizacao de faixa de n�meros
	 * -Inutliza a faixa de n�meros
	 * -Salva no banco de dados
	 * -Cria nfe com o n�mero inutilizado 
	 */

	@Transactional
	public void geraInutilizacao(){
		try{
			String respo="";
			String cnpj = "";
			String serie = "";
			if (this.getUsuarioAutenticado().getIdFilial() != null){
				System.out.println("estou pegando os dados da filial");
				this.filial = this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false);
				cnpj = this.filial.getCnpj();
				serie = this.filial.getSerie(); 
			}else{
				System.out.println("estou pegando os dados da empresa");
				this.empresa = this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(),false);
				cnpj = this.empresa.getCnpj();
				serie = this.empresa.getSerie(); 
			}
			System.out.println("NFE.INUTILIZARNFE(\""+CpfCnpjUtils.retiraCaracteresEspeciais(cnpj)+"\",\""+this.inutiliza.getCJustificativa()+"\","+LocalDate.now().getYear()+",55,"+serie+","+this.inutiliza.getNNumInicial()+","+this.inutiliza.getNNumFinal()+")");
			respo = this.acbr.enviaComandoACBr(pegaConexao(), "NFE.INUTILIZARNFE(\""+CpfCnpjUtils.retiraCaracteresEspeciais(cnpj)+"\",\""+this.inutiliza.getCJustificativa()+"\","+LocalDate.now().getYear()+",55,"+serie+","+this.inutiliza.getNNumInicial()+","+this.inutiliza.getNNumFinal()+")").toUpperCase();
			this.respostaAcbrLocal = respo;
			this.inutiliza.setRespostaAcbrEvento(respo);
			String resumo="";
			String cstat="";
			String xMotivo="";
			String nProt="";		
			String caminho="";
			String cTmp="";
			if (respo.substring(0,4).equals("ERRO")){
				this.addError(true, "error.cancel.nfe");
			}else{
				resumo = respo.substring(respo.indexOf("[INUTILIZACAO]")).toUpperCase();
				List<String> linhaResposta = linhaTexto(resumo);
				for (String string : linhaResposta) {
					if (localiza.localizaPalavra(string, "CSTAT=")){
						cstat = string.substring(6);
					}
					if (localiza.localizaPalavra(string, "XMOTIVO=")){
						xMotivo = string.substring(8);
					}
					if (localiza.localizaPalavra(string, "NPROT=")){
						nProt = string.substring(6);
					}
					if (localiza.localizaPalavra(string, "ARQUIVO=")){
						caminho= string.substring(8);
					}
				}
				BigDecimal codigo = new BigDecimal(cstat.trim());
				System.out.println(codigo + " = antes do if");
				if (codigo.compareTo(new BigDecimal("102")) == 0 ){
					this.inutiliza.setCStat(cstat);
					System.out.println(xMotivo);
					this.inutiliza.setXMotivo(xMotivo);
					System.out.println(nProt);
					this.inutiliza.setProtocolo(nProt);
					System.out.println(caminho);
					cTmp = caminho.substring(caminho.length()-59);
					System.out.println("cTmp caminho" + cTmp);
					this.inutiliza.setCaminhoEvento(cTmp);
					this.inutiliza = this.inutilizaDao.save(this.inutiliza);
					BigDecimal nInicial = new BigDecimal(this.inutiliza.getNNumInicial());
					BigDecimal nFinal = new BigDecimal(this.inutiliza.getNNumFinal());
					BigDecimal contadorInu = new BigDecimal("1").add(nFinal).subtract(nInicial);
					System.out.println("numero de nfes"+contadorInu);
					int numeroNfe = nInicial.intValue();
					for (int i = 0 ; i < contadorInu.intValue() ; i++ ){
						this.nfe = new Nfe();
						this.nfe.setNumeroNota(new BigDecimal(numeroNfe).longValue());
						this.nfe.setFinalidadeEmissao(FinalidadeNfe.NO);
						this.nfe.setStatusEmissao(StatusNfe.IN);
						this.nfe.setNome("Nota Inutilizada");
						this.nfe.setInutilizada(this.inutiliza);
						this.nfe.setRespostaAcbr(respo);
						this.nfeDao.save(this.nfe);
						NumeroSemUtilizacaoNFe numeroTemp = numeroNfeDisponivel(new BigDecimal(numeroNfe).longValue());
						if (numeroTemp.getId() != null) {
							if (!numeroTemp.isBloqueado()) {
								numeroTemp.setBloqueado(true);
								this.numeroDao.save(numeroTemp);
							}
						}else {
							numeroTemp = new NumeroSemUtilizacaoNFe();
							numeroTemp.setNumeroLivre(new BigDecimal(numeroNfe).longValue());
							numeroTemp.setBloqueado(true);
							this.numeroDao.save(numeroTemp);
						}
						numeroNfe++;
					}
				}
			}
		}catch (Exception e) {
			//			 TODO: handle exception
			System.out.println(e.getStackTrace());
		}

	}

	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Produto> getLazyProduto(){
		this.produtoModel = new AbstractLazyModel<Produto>() {

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

	public void  telaListaProduto(){
		this.produtoModel = getLazyProduto();
		this.updateAndOpenDialog("PesquisaProdutoDialog", "dialogPesquisaProduto");
	}

	public void onRowSelectProduto(SelectEvent event)throws IOException{
		this.produto = (Produto) event.getObject();
		this.ref = this.produto.getReferencia();
		definePreco();
	}

	/**
	 * M�todo para definir o pre�o do produto conforme sele��o da tabela de pre�os
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

	public void cadastraImpostos(ItemNfe item){
		this.itemSelecionadoTemporario = item;
		System.out.println(this.itemSelecionadoTemporario.getCfopItem() +" cfop item");
		this.updateAndOpenDialog("impostosNfeDialog","dialogImpostosNfe");
	}

	public void salvaImpostoAvulso(){
		//		int i = 0;
		//		for (ItemNfe itemNfe : listaTemporariaItens) {
		//			if (itemNfe.equals(this.itemSelecionadoTemporario)){
		//				listaTemporariaItens.get(i).setTributo(tributoTemporario);
		//			}
		//			i++;
		//		}
		for (ItemNfe itemTempImp : listaTemporariaItens) {
			if (itemTempImp.equals(this.itemSelecionadoTemporario)) {
				//				itemNfe.setTributo(this.tributoTemporario);
				itemNfe.setValorDespesas(tempDespesaItem);
				itemNfe.setIi(this.ii);
			}
		}
		//		this.tributoTemporario = new Tributos();
		this.ii = new II();
		this.tempDespesaItem = new BigDecimal("0");
	}

	@Override
	public Nfe setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * M�todo que retira do estoque fiscal apenas
	 * @param listaItem
	 */
	@Transactional
	public void retiraDoEstoque(List<ItemNfe> listaItem) {
		try {
			Estoque estoqueTemp = new Estoque();
			for (ItemNfe itemPedidoTemp : listaItem) {				
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				estoqueTemp = estoqueUtil.subtraiEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), true,permiteBaixaEstoqueGeral());
				ncmDao.save(estoqueTemp.getNcmEstoque());		
				//				barrasDao.save(estoqueTemp.getBarrasEstoque());
			}
		} catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail",h.getMessage());
		} catch (EstoqueException e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}

	/**
	 * M�todo que recebe o ESTOQUE do item do pedido 
	 *
	 * @param item
	 * @return
	 */
	public Estoque pegaEstoque (ItemNfe item) {
		System.out.println("preenchendo o estoque do item ");
		return estoqueUtil.preencheEstoqueItemNfe(item, pegaIdEmpresa(), pegaIdFilial());
	}
	/**
	 * Método que retorna a config da empresa para baixa de estoque na emissao de NFE.
	 * @return boolean true para permitir e false para NÃO permitir
	 */
	public boolean permiteBaixaEstoqueGeral() {
		boolean resultado = false;
		EmpUser empresaUsuario = this.configEmpUser();
		if (this.getUsuarioAutenticado().getIdFilial() != null) {
			resultado = empresaUsuario.getFil().isBaixaEstoqueGeral();
		}else {
			resultado = empresaUsuario.getEmp().isBaixaEstoqueGeral();
		}
		return resultado;
	}

	/**
	 * M�todo que devolve o estoque fiscal quando uma nota � EXCLUIDA!
	 * @param listaItem
	 */
	@Transactional
	public void devolveParaEstoque(List<ItemNfe> listaItem) {
		try {
			Estoque estoqueTemp = new Estoque();
			for (ItemNfe itemPedidoTemp : listaItem) {
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				estoqueTemp = estoqueUtil.acrescentaEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), true,permiteBaixaEstoqueGeral());
				ncmDao.save(estoqueTemp.getNcmEstoque());
				//				barrasDao.save(estoqueTemp.getBarrasEstoque());
			}
		} catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail",h.getMessage());
		} catch (EstoqueException e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}

	/**
	 * M�todo que devolve o estoque fiscal quando uma nota � Cancelada (NFE CANCELA)!
	 * @param listaItem
	 */
	@Transactional
	public void devolveParaEstoqueCancelaNFE(List<ItemNfe> listaItem) {
		try {
			Estoque estoqueTemp = new Estoque();
			for (ItemNfe itemPedidoTemp : listaItem) {
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				if (estoqueTemp != null) {
					estoqueTemp = estoqueUtil.acrescentaEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), true,permiteBaixaEstoqueGeral());
					ncmDao.save(estoqueTemp.getNcmEstoque());
					//					barrasDao.save(estoqueTemp.getBarrasEstoque());
				}
			}
		} catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail",h.getMessage());
		} catch (EstoqueException e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}

	public void initializeRelFiscal() {
		this.dataInicial = LocalDate.now();
		this.dataFinal = LocalDate.now();
		this.viewState = ViewState.LISTING;
		this.listaTributos = this.natOperacaoDao.listaTodosTributosAtivos(pegaIdEmpresa(), pegaIdFilial());
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
	public String razaoEmpresaLogada() {
		if (pegaIdFilial() != null) {
			return this.configEmpUser().getFil().getRazaoSocial();
		}else {
			return this.configEmpUser().getEmp().getRazaoSocial();
		}
	}

	public void geraRelNaturezaOperacao() {
		try {

			List<RelNatOperacaoDTO> data = this.nfeDao.relNatOperacao(this.dataInicial,this.dataFinal,pegaIdEmpresa(),pegaIdFilial(),tributoTemporario,StatusNfe.EN);
			BigDecimal totalNFePeriodo = new BigDecimal("0");
			if (data.size()>0) {
				for (Iterator<RelNatOperacaoDTO> iterator = data.iterator(); iterator.hasNext();) {
					System.out.println("Dentro do interator");
					RelNatOperacaoDTO nfe =  iterator.next();
					totalNFePeriodo = totalNFePeriodo.add(nfe.getTotalNota());
				}
			}

			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (data.size() > 0) {
				parametros =  geraParametros();
				parametros.put("empresa",razaoEmpresaLogada());
				parametros.put("totalNFePeriodo",totalNFePeriodo);
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(data,false);
				String path = "/WEB-INF/Relatorios/Fiscal/RelNatOpera.jrxml";
				relatorios.visualizaPDF(path, parametros, "RelNatOperacao" ,jrBean);
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
	/**
	 * Descobre onde o Destinatario foi cadastrado
	 * @param pedido
	 * @return String - cliente/fornecedor/colaborador ou NULL caso nao preenchido
	 */
	public String descobreTipoDeDestinatario(Pedido pedido) {
		if (pedido.getDestino().informaClassePreenchida() != "null" || !pedido.getDestino().informaClassePreenchida().equalsIgnoreCase("null")) {
			return pedido.getDestino().informaClassePreenchida();
		}else {
			return null;
		}
	}

	/**
	 * Verifica se o pedido consta um destinatario preenchido
	 * @param pedido
	 * @return true - preechido / false - não preenchido
	 */
	public boolean destinatarioPreenchido(Pedido pedido) {
		boolean preenchido = false;
		if (pedido.getDestino().classePreenchida() != null) {
			preenchido = true;

		}
		return preenchido;
	}


	public boolean destinatarioDefinidoNFe(Nfe nfe) {
		boolean definido = false;
		String tipoDestinatario = "";
		if (nfe.getDestino() == null) {
			definido = false;
		}else {
			tipoDestinatario = nfe.getDestino().retornaTipoDestinatario();
			if (!tipoDestinatario.equalsIgnoreCase("null")) {
				definido = true;
				if (tipoDestinatario.equalsIgnoreCase("cliente")) {
					this.nfe.setTipoPesquisa(TipoPesquisa.CLI);
				}else {
					if (tipoDestinatario.equalsIgnoreCase("fornecedor")) {
						this.nfe.setTipoPesquisa(TipoPesquisa.FOR);
					}else {
						this.nfe.setTipoPesquisa(TipoPesquisa.COL);
					}
				}
			}
		}
		this.setClienteDefinido(definido);
		return definido;
	}

	public String emitirNFePedido(Pedido pedido) {
		System.out.println("estou dentro do emitirNFePedido");
		if (destinatarioPreenchido(pedido)) { // preenchido
			//descobre a tabela 
			this.setClienteDefinido(true);
			return "/main/fiscal/formCadNfePedido.xhtml?faces-redirect=true&pedidoID=" + pedido.getId();
		}else { // nao preenchido
			this.setClienteDefinido(false);
			System.out.println("Destinatario nao preenchido");
			//Exibe tela para localizar destinatario
			return "/main/fiscal/formCadNfePedido.xhtml?faces-redirect=true&pedidoID=" + pedido.getId();
		}

	}

	public void verificaCliente(Pedido pedido) {
		if (destinatarioPreenchido(pedido)) { // preenchido
			//descobre a tabela 
			String tabela = descobreTipoDeDestinatario(pedido);

			if (tabela == "cliente") {
				this.nfe.getDestino().setCliente(this.clienteDao.findById(pedido.getDestino().getCliente().getId(), false));
			}else {
				if (tabela == "fornecedor") {
					this.nfe.getDestino().setFornecedor(this.fornecedorDao.findById(pedido.getDestino().getFornecedor().getId(), false));
				}else {
					this.nfe.getDestino().setColaborador(this.colaboradorDao.findById(pedido.getDestino().getColaborador().getId(), false));
				}
			}

		}else { // nao preenchido
			System.out.println("Destinatario nao preenchido");
			//Exibe tela para localizar destinatario
			//			this.telaPesquisaDestinatario();
		}
	}

	public String nfePedido(Pedido idPedido) {
		return emitirNFePedido(idPedido);
		//		return "/main/fiscal/formCadNfePedido.xhtml?faces-redirect=true&pedidoID=" + idPedido.getId();
	}

	/**
	 * Inicialização da pagina em modo de Nova Nfe com pedido de venda
	 * @param idNfe
	 */
	public void initializeFormCadNfePedido(Long pedidoID) {
		this.nfeFromPedido = true;
		this.config = this.configDao.findById(this.getUsuarioAutenticado().getConfig().getId(), false);
		this.configMatriz = this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
		this.formaPag = new FormaDePagamento();
		this.listaFormasDePagamento = this.formaPagDao.listaFormaDePagamento(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		if (pedidoID != null) {
			// iniciando variaveis do sistema
			this.cliente = new Cliente();
			this.fornecedor = new Fornecedor();
			this.empresa = new Empresa();
			this.filial = new Filial();
			this.colaborador = new Colaborador();
			this.destino = new Destinatario();
			this.produto = new Produto();
			this.itemNfe = new ItemNfe();
			this.totalNfe = new BigDecimal("0.0");
			this.valorFrete = new BigDecimal("0.0");
			this.temporario = new BigDecimal("0.0");
			this.quantidade = new BigDecimal("0.0");
			this.precoVenda = new BigDecimal("0.0");
			this.transporte = new Transportadora();
			this.transp = new Transportador();
			this.custoProduto = new ProdutoCusto();
			this.itemSelecionado = new ItemNfe();
			this.lacre = new Lacre();
			this.formaPag = new FormaDePagamento();
			this.nfeReferenciada = new NfeReferenciada();
			this.descontoPercentual = true;
			this.listaTempReferenciada = new ArrayList<>();
			this.listaTemporariaItens = new ArrayList<>();
			this.nfe = new Nfe();
			this.nfe.setOrigemPedido(true);

			this.nfe.setTipoPesquisa(TipoPesquisa.CLI);

			this.pedido = this.pedidoDao.pegaPedidoPorId(pedidoID);
			// definindo natureza de operação
			this.nfe.setNatOperacao(this.pedido.getTransacao().getTributoPadrao());
			// definindo finalidade nfe
			if (this.pedido.getPedidoTipo().compareTo(PedidoTipo.PVE)== 0) {
				this.nfe.setFinalidadeEmissao(FinalidadeNfe.NO);
			}else {
				if (this.pedido.getPedidoTipo().compareTo(PedidoTipo.TRA)==0) {
					this.nfe.setFinalidadeEmissao(FinalidadeNfe.NO);
				}else {
					this.nfe.setFinalidadeEmissao(FinalidadeNfe.DV);
				}
			}
			this.barrasEstoque = new BarrasEstoque();
			setVisivelPorIdTributos(true);
			// pegar agPedido para pegar parcelaspagamento
			this.agPedido = this.agPedidoDao.encontraAgPedidoPorId(this.pedido.getAgrupado().getId());
			this.nfe.setListaParcelas(this.parcelaDao.listaParcelasPorAgPedido(this.agPedido));
			System.out.println("tamanho da lista de parcelas: " + this.nfe.getListaParcelas().size());
			this.nfe.setFormaPagamento(this.nfe.getListaParcelas().get(0).getFormaPag());
			//			 Preenchendo a forma de pagamento
			if (this.nfe.getFormaPagamento() != null){
				System.out.println("entrei no nfe com forma de pagamento - L652");
				this.formaPag = this.formaPagDao.findById(this.nfe.getFormaPagamento().getId(), false);
				if(this.nfe.getListaParcelas() != null && !this.nfe.getListaParcelas().isEmpty() ) {
					this.listaParcelamento = this.nfe.getListaParcelas();
				}else {
					this.listaParcelamento =this.agPedidoDao.listaDeParcelasAgPedidoPorId(this.agPedido.getId());
					this.nfe.setListaParcelas(this.listaParcelamento);
				}
			}
			this.nfe.setListaLacres(this.lacreDao.findLacreForNfe(this.nfe, pegaIdEmpresa(), pegaIdFilial()));
			this.viewState = ViewState.ADDING;
			// Definindo cliente da NFE
			if (destinatarioPreenchido(this.pedido)) { // se preenchido ja popula os campos.
				this.botaoProcesseguir = false;
				String tabela = descobreTipoDeDestinatario(pedido);
				if (tabela == "cliente") {
					this.cliente = this.clienteDao.findById(pedido.getDestino().getCliente().getId(), false);
					this.destino.setCliente(this.cliente);
					this.endereco = this.cliente.getEndereco().getLogradouro();
					this.bairro = this.cliente.getEndereco().getBairro();
					this.municipio = this.cliente.getEndereco().getEndereco().getLocalidade();
					this.uf = this.cliente.getEndereco().getEndereco().getUf().name();
					this.razao = this.cliente.getRazaoSocial();					
					if(this.destino.getCliente().getTipoCliente().compareTo(TipoCliente.CfC)== 0) {
						this.cnpj = this.destino.getCliente().getCpf();		
						this.nfe.setIndFinal("1");
					}else {
						if (this.destino.getCliente().getTipoCliente().compareTo(TipoCliente.Est)== 0) {
							this.cnpj = this.destino.getCliente().getIdEstrangeiro();
							this.nfe.setIndFinal("1");
						}else {
							this.cnpj = this.destino.getCliente().getCnpj();
							if (this.destino.getCliente().getInscEstadual()== null){
								this.nfe.setIndFinal("1");
							}else{
								this.nfe.setIndFinal("0");
							}
						}
					}
				}else {
					if (tabela == "fornecedor") {
						this.destino.setFornecedor(this.fornecedorDao.findById(pedido.getDestino().getFornecedor().getId(), false));
						this.endereco = this.destino.getFornecedor().getEndereco().getLogradouro();
						this.bairro = this.destino.getFornecedor().getEndereco().getBairro();
						this.municipio = this.destino.getFornecedor().getEndereco().getEndereco().getLocalidade();
						this.uf = this.destino.getFornecedor().getEndereco().getEndereco().getUf().name();
						this.razao = this.destino.getFornecedor().getRazaoSocial();
						if(this.destino.getFornecedor().getTipoCliente().compareTo(TipoCliente.CfC)== 0) {
							this.cnpj = this.destino.getFornecedor().getCpf();	
							this.nfe.setIndFinal("1");
						}else {
							this.cnpj = this.destino.getFornecedor().getCnpj();
							if (this.destino.getCliente().getInscEstadual()== null){
								this.nfe.setIndFinal("1");
							}else{
								this.nfe.setIndFinal("0");
							}
						}
					}else {
						this.destino.setColaborador(this.colaboradorDao.findById(pedido.getDestino().getColaborador().getId(), false));
						this.endereco = this.destino.getColaborador().getEndereco().getLogradouro();
						this.bairro = this.destino.getColaborador().getEndereco().getBairro();
						this.municipio = this.destino.getColaborador().getEndereco().getEndereco().getLocalidade();
						this.uf = this.destino.getColaborador().getEndereco().getEndereco().getUf().name();
						this.razao = this.destino.getColaborador().getNome();
						this.cnpj = this.destino.getColaborador().getCpf();	
						this.nfe.setIndFinal("1");
					}
				}
				this.nfe.setUfDestino(Uf.valueOf(this.uf));
				this.nfe.setNome(this.razao);
				this.nfe.setDestino(this.destino);
				importaItensPedido();
			}else {// caso nao preenchido exibe a tela com os campos inicializados vazio para depois de preencher o cliente fazer a importação do pedido
				this.addWarning(true,"nfe.pedido.noClient");
				System.out.println("cliente nao preenchido!!!!!!");
			}
		}
	}
	public void importaItensPedido() {
		try {
			if (destinatarioDefinidoNFe(this.nfe)) {
				if (this.listaTemporariaItens.size() != 0) {
					throw new RegraNegocioException(this.translate("nfePedido.error.isImport"));
				}else {
					if (this.pedido.getListaItensPedido().size() ==0) {
						throw new NfeException(this.translate("nfe.erro.listItens.empty"));
					}
					List<ItemNfe> listaConvertidaItemPedido = new ArrayList<ItemNfe>();
					BigDecimal valorDeDesconto = new BigDecimal("0");
					//					BigDecimal percentualDesconto = new BigDecimal("0");
					BigDecimal fundoCombate = new BigDecimal("0");
					BigDecimal fundoCPRetidoST = new BigDecimal("0");
					BigDecimal vfcpufdest = new BigDecimal("0");
					int i = 1 ;  
					for (ItemPedido itemPedido : this.pedido.getListaItensPedido()) {
						this.itemNfe = new ItemNfe();
						this.itemNfe.setQuantidade(itemPedido.getQuantidade());
						this.itemNfe.setProduto(itemPedido.getProduto());
						this.itemNfe.setValorUnitario(itemPedido.getValorUnitario());
						this.itemNfe.setValorTotalBruto(itemPedido.getValorTotalBruto());
						this.itemNfe.setValorTotal(itemPedido.getValorTotal());
						this.itemNfe.setDesconto(itemPedido.getDesconto());
						this.itemNfe.setBarras(itemPedido.getBarras());
						this.itemNfe.setPorcentagem(itemPedido.isPorcentagem());


						// calculando os impostos

						this.itemNfe.setRow(this.rowDataBase);
						setTotalItem(this.itemNfe.getValorTotal());
						this.setItemNfe(this.calculaTributos.preencheImpostos(this.itemNfe, this.nfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
						System.out.println("Calculado os tributos!" + i);
						i++;


						if (this.itemNfe.getPFCP() == null) {
							this.itemNfe.setPFCP(new BigDecimal("0"));
						}
						if (this.itemNfe.getPFCP().compareTo(new BigDecimal("0")) == 1 ){
							System.out.println("NfeBean linha 882 - Dentro do if getPfcp");
							fundoCombate = fundoCombate.add(this.itemNfe.getVFCP());
							this.nfe.setVFCP(fundoCombate);
							if (this.itemNfe.getCst() == "60" || this.itemNfe.getCst() == "500"){
								fundoCPRetidoST = fundoCPRetidoST.add(this.formula.geraSTRetido(this.itemNfe.getValorTotal().divide(this.itemNfe.getQuantidade()), this.itemNfe.getProduto().getUfOrigem(), this.nfe.getUfDestino(), this.itemNfe.getProduto().getMvaFornecedor()));
								this.nfe.setVFCPSTRet(fundoCPRetidoST);
							}else{
								vfcpufdest = vfcpufdest.add(this.itemNfe.getVFCPUFDest());
								this.nfe.setVFCPUFDest(vfcpufdest);
							}
						}

						System.out.println("NfeBean linha 891 : calculando Total Tributos");
						this.valorTotalTributos = this.valorTotalTributos.add(itemNfe.getValorTotalTributoItem());
						System.out.println(this.valorTotalTributos);
						System.out.println("NfeBean linha 894 : verificando se existe partilha de icms");
						if (this.itemNfe.getVICMSUFDest().compareTo(new BigDecimal("0.00")) == 1){
							System.out.println("Dentro do if VICMSUFDest");
							BigDecimal vic = new BigDecimal("0");
							vic = this.nfe.getVICMSUFDest().add(this.itemNfe.getVICMSUFDest());
							this.nfe.setVICMSUFDest(vic);
							System.out.println(this.nfe.getVICMSUFDest());
						}
						if (this.itemNfe.getVICMSUFRemet().compareTo(new BigDecimal("0.00")) == 1){
							System.out.println("NfeBean dentro do if VICMSUFRemet");
							BigDecimal VICMSUFRemet = new BigDecimal("0");
							VICMSUFRemet = this.nfe.getVICMSUFRemet().add(this.itemNfe.getVICMSUFRemet());
							this.nfe.setVICMSUFRemet(VICMSUFRemet);
							System.out.println(this.nfe.getVICMSUFRemet());
						}
						this.nfe.setOrigemPedido(true);
						this.nfe.setDesconto(this.nfe.getDesconto().add(this.itemNfe.getDesconto()));
						System.out.println("NfeBean linha 901 : concluido");
						this.nfe.setValorTotalTributos(this.valorTotalTributos);
						System.out.println(this.nfe.getValorTotalTributos());
						totalBaseIcms = totalBaseIcms.add(this.itemNfe.getBaseICMS().setScale(2,RoundingMode.HALF_EVEN));
						this.nfe.setBaseIcms(totalBaseIcms.setScale(2, RoundingMode.HALF_EVEN));
						this.valorIcms = this.valorIcms.add(this.itemNfe.getValorIcms()).setScale(3, RoundingMode.DOWN);
						this.nfe.setValorIcms(this.valorIcms.setScale(2, RoundingMode.HALF_EVEN));
						totalBaseIcmsSt = totalBaseIcmsSt.add(this.itemNfe.getBaseICMSSt());
						this.nfe.setBaseIcmsSubstituicao(totalBaseIcmsSt);  
						valorIcmsSt = valorIcmsSt.add(this.itemNfe.getValorIcmsSt());
						this.nfe.setValorIcmsSubstituicao(valorIcmsSt.setScale(2, RoundingMode.HALF_EVEN));
						valorTotalProdutos = valorTotalProdutos.add(this.itemNfe.getValorTotalBruto());
						this.nfe.setValorTotalProdutos(valorTotalProdutos.setScale(2, RoundingMode.HALF_EVEN));

						valorTotalIpi = valorTotalIpi.add(this.itemNfe.getValorIPI());
						this.nfe.setValorTotalIpi(valorTotalIpi);
						valorTotalPis = valorTotalPis.add(this.itemNfe.getValorPis());
						this.nfe.setValorTotalPis(valorTotalPis);
						valorTotalCofins = valorTotalCofins.add(this.itemNfe.getValorCofins());
						this.nfe.setValorTotalCofins(this.valorTotalCofins);
						valorTotalNota = valorTotalNota.add(this.itemNfe.getValorTotal()).add(this.itemNfe.getValorFrete()).add(this.itemNfe.getValorSeguro()).add(this.itemNfe.getValorIcmsSt()).add(this.itemNfe.getValorIPI()).add(this.itemNfe.getValorDespesas());
						this.nfe.setValorTotalNota(valorTotalNota.setScale(2, RoundingMode.HALF_EVEN));
						this.totalNfe = this.nfe.getValorTotalProdutos().subtract(this.nfe.getDesconto()).setScale(2,RoundingMode.HALF_EVEN);
						this.tempValorUnitario = this.itemNfe.getValorUnitario();
						this.ref = new String();
						this.precoVenda = new BigDecimal("0.00");
						this.rowDataBase++;
						if (pegaEnquadramentoEmitente().equals(Enquadramento.SimplesNacional) && !this.nfe.getFinalidadeEmissao().equals(FinalidadeNfe.DV)){
							this.nfe.setBaseIcms(new BigDecimal("0"));
							this.totalBaseIcms = new BigDecimal("0");
							this.nfe.setValorIcms(new BigDecimal("0"));
							this.valorIcms = new BigDecimal("0");
						}
						listaConvertidaItemPedido.add(this.itemNfe);
					}
					this.listaTemporariaItens.addAll(listaConvertidaItemPedido);
					this.botaoProcesseguir = false;
				}
			}else {
				throw new RegraNegocioException(this.translate("nfe.pedido.noClient"));
			}
		}catch (NfeException n) {
			this.addError(true,n.getMessage());
		}catch (RegraNegocioException r) {
			this.addError(true,"caixa.error",r.getMessage());
		}catch (Exception e) {
			// TODO: handle exception
			this.addError(true,"caixa.error",e.getMessage());
		}
	}

	public void localizaDI() {
		this.di = this.diDao.pegaDICriteria(false, this.numeroDI, pegaIdEmpresa(), pegaIdFilial(), true);
		if (this.di != null) {
			this.nfe.setDi(this.di);
			this.addInfo(true,"Localizado!");
		}else {
			this.addInfo(true,"N�o Localizado");
		}
	}
}
