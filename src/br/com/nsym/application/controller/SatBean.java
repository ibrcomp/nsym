package br.com.nsym.application.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.TotaisCFeException;
import br.com.ibrcomp.exception.TributosException;
import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.nfe.tools.CalculaTributos;
import br.com.nsym.application.controller.nfe.tools.CupomFiscalCaixa;
import br.com.nsym.application.controller.nfe.tools.CupomFiscalFactory;
import br.com.nsym.application.controller.nfe.tools.NfceEmissaoResultado;
import br.com.nsym.application.controller.nfe.tools.NfceEmissaoService;
import br.com.nsym.application.controller.nfe.tools.NfceService;
import br.com.nsym.domain.misc.CpfCnpjUtils;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.misc.FinanceiroTools;
import br.com.nsym.domain.misc.LocalizaRegex;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.estoque.Estoque;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.financeiro.tools.TabelaPreco;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.DestinatarioCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.EmitenteCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.CustoProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.FormaDePagementoRepository;
import br.com.nsym.domain.model.repository.financeiro.tools.ParcelasNfeRepository;
import br.com.nsym.domain.model.repository.fiscal.NcmEstoqueRepository;
import br.com.nsym.domain.model.repository.fiscal.nfce.NfceRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.DestinatarioCFeRepository;
import br.com.nsym.domain.model.repository.fiscal.sat.CFeRepository;
import br.com.nsym.domain.model.repository.fiscal.sat.EmitenteCfeRepository;
import br.com.nsym.domain.model.repository.fiscal.sat.ItemCFeRepository;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class SatBean extends AbstractBeanEmpDS<CFe> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private AcbrComunica acbr;
	
	@Getter
	@Setter
	private String emailCfe;

	@Getter
	@Setter
	private DadosDeConexaoSocket infConexao;

	@Getter
	private AbstractLazyModel<CFe> cfeModel;

	@Getter
	private AbstractLazyModel<Produto> produtoModel;

	@Getter
	@Setter
	private TabelaPreco tabelaSelecionada = TabelaPreco.TA;

	@Inject
	private EmpresaRepository empresaDao;

	@Inject
	private FilialRepository filialDao;

	@Getter
	@Setter
	private ItemCFe item = new ItemCFe();

	@Getter
	@Setter
	private CFe cfe = new CFe();
	
	@Getter
	@Setter
	private EmitenteCFe emitente = new EmitenteCFe();
	
	@Inject
	private EmitenteCfeRepository emitenteDao;

	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_EVEN);

	@Inject
	private CFeRepository cfeDao;

	@Inject
	private ProdutoRepository produtoDao;

	@Getter
	@Setter
	private Produto produto = new Produto();
	
	@Getter
	@Setter
	private BigDecimal precoVenda= new BigDecimal("0");

	@Inject
	private ItemCFeRepository itemDao;

	@Getter
	@Setter
	private ItemCFe itemSelecionado = new ItemCFe();

	@Getter
	@Setter
	private List<ItemCFe> listaItemTemp = new ArrayList<>();

	@Getter
	@Setter
	private List<ItemCFe> listaItemRemover = new ArrayList<>();

	@Setter
	private AbstractDataModel<ItemCFe> listaItemModel; 

	@Inject
	private FormaDePagementoRepository formaPagDao;

	@Getter
	private List<FormaDePagamento> listaFormasDePagamento = new ArrayList<>();

	@Getter
	@Setter
	private FormaDePagamento formaPag = new FormaDePagamento();

	@Getter
	@Setter
	private List<ParcelasNfe> listaParcelamento = new ArrayList<>();

	@Getter
	@Setter
	private List<ParcelasNfe> parcelasRemover = new ArrayList<ParcelasNfe>();

	@Inject
	private ParcelasNfeRepository parcelaDao;

	@Inject
	private FinanceiroTools auxCalculo;

	@Getter
	@Setter
	private String ref = "";

	@Getter
	@Setter
	private BigDecimal quantidade = new BigDecimal("0");
	@Getter
	@Setter
	private BigDecimal totalItem =new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal totalCFe=new BigDecimal("0");

	@Getter
	@Setter
	private boolean descontoPercentual;

	@Getter
	@Setter
	private BigDecimal desPercVal=new BigDecimal("0");

	@Getter
	@Setter
	private ProdutoCusto custoProduto;

	@Inject
	private CustoProdutoRepository custoDao;

	@Setter
	@Getter
	private BigDecimal tempValorUnitario = new BigDecimal("0");

	@Getter
	@Setter
	private String respostaAcbrLocal;

	@Getter
	@Setter
	private DestinatarioCFe destinatario;

	@Inject
	private DestinatarioCFeRepository destinatarioDao;

	@Getter
	@Setter
	private String documento;

	@Getter
	private CpfCnpjUtils util;

	@Getter
	@Setter
	private BigDecimal totalDesconto = new BigDecimal("0");

	@Getter
	@Setter
	private BigDecimal tempTotalUnitario = new BigDecimal("0");

	@Getter
	@Setter
	private LocalDate dataIncial = LocalDate.now();

	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now();

	@Getter
	@Setter
	private String nomeArquivo;

	@Inject
	private CalculaTributos calculaTributos;

	@Inject
	private LocalizaRegex localiza;
	
	@Getter
	@Setter
	private PDFOptions pdfOpt = new PDFOptions();
	
	@Getter
	@Setter
	private BigDecimal totalListaCFe = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalListaNfce = new BigDecimal("0");
	
	@Getter
	@Setter
	@Transient
	private List<BarrasEstoque> listaBarrasTemp = new ArrayList<>();
	
	@Getter
	@Setter
	private List<ItemCFe> listaItemPodeExcluir = new ArrayList<ItemCFe>();

	@Getter
	@Setter
	private BarrasEstoque barrasEstoque; 
	
	@Inject
	private BarrasEstoqueRepository barrasDao;
	
	@Inject
	private NfceService nfceService;

	@Inject
	private NfceEmissaoService nfceEmissaoService;
	
	@Getter
	@Setter
	private Nfce nfce = new Nfce();
	
	@Getter
	@Setter
	private EmpUser empresaUsuario;
	
	@Getter
	@Setter
	private Configuration configUser;
	
	// Utilitario com todas as fun  es de estoque
	@Inject
	private EstoqueUtil estoqueUtil;
	
	//Estoque fiscal
	@Inject
	private NcmEstoqueRepository ncmDao;
	
	@Inject
	private NfceRepository nfceDao;

	@Getter
	private AbstractLazyModel<Nfce> nfceModel;
	
	@Getter
	private CupomFiscalCaixa cupomSelecionado;


	@Override
	public CFe setIdFilial(Long idFilial) {
		return null;
	}

	@Override
	public void initializeListing() {
		this.viewState = ViewState.ADDING;
		this.cfeModel = getLazycfe();
		this.nfceModel = getLazyNfce();
		this.nomeArquivo = "sat" + removerAcentos(this.getUsuarioAutenticado().getName()+this.getUsuarioAutenticado().getIdEmpresa()).replace(" ", "").trim();
		this.totalListaCFe = cfeDao.totalCfePeriodo(dataIncial, dataFinal, pegaIdEmpresa(), pegaIdFilial());
		this.totalListaNfce = nfceDao.totalNfcePeriodo(dataIncial, dataFinal, pegaIdEmpresa(), pegaIdFilial());
	}

	@Override
	public CFe setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@PostConstruct
	public void init(){
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}

	/**
	 * Gera a lista de Cfe emitidos por periodo em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<CFe> getLazycfe(){
		this.cfeModel = new AbstractLazyModel<CFe>() {

			/**
			 *
			 */
			private static final long serialVersionUID = 3940283352722272413L;

			@Override
			public List<CFe> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<CFe> page = cfeDao.listaCFeEmitidoPorIntervaloData(dataIncial,dataFinal, pegaIdEmpresa(),pegaIdFilial(),pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return cfeModel;
	}
	
	public AbstractLazyModel<Nfce> getLazyNfce() {
	    this.nfceModel = new AbstractLazyModel<Nfce>() {
	        private static final long serialVersionUID = 1L;

	        @Override
	        public List<Nfce> load(int first, int pageSize, String sortField, SortOrder sortOrder,
	                               Map<String, Object> filters) {

	            PageRequest pageRequest = new PageRequest();
	            pageRequest.setFirstResult(first)
	                       .withPageSize(pageSize)
	                       .sortingBy(sortField, "inclusion")
	                       .withDirection(sortOrder.name());

	            // IDEAL: ter método no repositório igual ao do CFeRepository
	            Page<Nfce> page = nfceDao.listaNfceEmitidoPorIntervaloData(
	                    dataIncial, dataFinal, pegaIdEmpresa(), pegaIdFilial(), pageRequest);

	            this.setRowCount(page.getTotalPagesInt());
	            return page.getContent();
	        }
	    };
	    return nfceModel;
	}

	/**
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaBarras(){
		this.updateAndOpenDialog("PesquisaBarrasDialog", "dialogPesquisaBarras");
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
		System.out.println("Iniciando Insers o de imagem no PDF");
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
			pdf.add(new Paragraph ("Relat rio de CFe"));
			pdf.add(Chunk.NEWLINE);
			pdf.add(new Paragraph("Total: R$ " + this.totalListaCFe.setScale(2,RoundingMode.HALF_EVEN).toString()));
			pdf.add(Chunk.NEWLINE);
		}
	}

	public void preencheDestinatario(String doc) {
		int i = 0;
		i = CpfCnpjUtils.isCpfOrCnpjOrNull(doc);
		boolean valido = CpfCnpjUtils.isValid(doc);
		if (i == 1 && valido ) {
			this.destinatario.setCnpj(doc);
		}else if (i == 2 && valido) {
				this.destinatario.setCpf(doc);
		}else {
			this.destinatario.setCnpj("");
			this.destinatario.setCpf("");
		}
		
	}

	public void filtroPeriodoListaCfe() {
		this.cfeModel = getLazycfe();
		this.totalListaCFe = cfeDao.totalCfePeriodo(dataIncial, dataFinal, pegaIdEmpresa(), pegaIdFilial());
		this.nfceModel = getLazyNfce();
		this.totalListaCFe = nfceDao.totalNfcePeriodo(dataIncial, dataFinal, pegaIdEmpresa(), pegaIdFilial());
	}


//	@Transactional
//	public void geraNfce() {
//		try {
//			preencheCupom();
//			NfceEmissaoResultado res = nfceEmissaoService.emitir(pegaConexaoNFce(), this.nomeArquivo, this.cfe, pegaIdEmpresa(), pegaIdFilial(), true);
//			this.nfce = (res != null ? res.getNfce() : null);
//			if (res == null || !res.isValido()) {
//				this.addError(true, res != null ? res.getMotivo() : "Falha ao emitir NFC-e (retorno nulo)");
//				return;
//			}
//			this.addInfo(true, "NFC-e emitida com sucesso: " + res.getNumero() + " (Série " + res.getSerie() + ")");
//		} catch (Exception e) {
//			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
//		}
//	}
	
	/** 
	 * Inicializa  o da pagina em modo de Adi  o ou Edi  o
	 * @param idCFe
	 */
	public void initializeForm(Long idCFe) {
		if (idCFe == null) {
			this.viewState = ViewState.ADDING;
			this.barrasEstoque = new BarrasEstoque();
			this.produto = new Produto();
			this.destinatario = new DestinatarioCFe();
			this.ref = "";
			this.documento = "";
			this.listaParcelamento = new ArrayList<>();
			this.cfe = new CFe();
			this.nfce = new Nfce();
			this.quantidade = new BigDecimal("0");
			this.item = new ItemCFe();
			this.desPercVal = new BigDecimal("0");
			this.custoProduto = new ProdutoCusto();
			this.listaItemTemp = new ArrayList<ItemCFe>();
			this.listaItemModel = getListaItemModel();
			this.totalCFe = new BigDecimal("0");
			this.nomeArquivo = "sat" + removerAcentos(this.getUsuarioAutenticado().getName()+this.getUsuarioAutenticado().getIdEmpresa()).replace(" ", "").trim();
			this.listaFormasDePagamento = this.formaPagDao.listaFormaDePagamento(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		} else {
			this.produto = new Produto();
			this.viewState = ViewState.EDITING;
			this.cfe = this.cfeDao.pegaCfeLazy(idCFe, getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial());
			this.nfce = new Nfce();
			this.listaItemTemp = this.cfe.getListaItem();
			this.totalCFe = this.cfe.getValorTotalProdutos();
			this.totalDesconto = this.cfe.getDesconto();
			this.listaFormasDePagamento = this.formaPagDao.listaFormaDePagamento(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
			if (this.cfe.getFormaPagamento() != null){
				this.formaPag = this.formaPagDao.findById(this.cfe.getFormaPagamento().getId(), false);
				this.cfe.setListaParcelas(this.parcelaDao.listaParcelasPorCFe(this.cfe, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial()));
				this.listaParcelamento = this.cfe.getListaParcelas();
				this.parcelasRemover.addAll(this.cfe.getListaParcelas());
			}

			this.listaItemRemover = this.itemDao.listaDeItensPorCFe(this.cfe, pegaIdEmpresa(), pegaIdFilial());
			System.out.println("Tamanho da lista de itens : " + this.listaItemRemover.size());
			if (this.cfe.getDestinatario() != null) {
				this.destinatario = this.cfe.getDestinatario();
				if (this.destinatario.getCnpj() != null) {
					this.documento = CpfCnpjUtils.adcionaCaracteresEspeciais(this.destinatario.getCnpj());
				}else {
					if (this.destinatario.getCpf() != null) {
						this.documento = CpfCnpjUtils.adcionaCaracteresEspeciais(this.destinatario.getCpf());
					}
				}
			}
			this.nomeArquivo = "sat" + removerAcentos(this.getUsuarioAutenticado().getName()+this.getUsuarioAutenticado().getIdEmpresa()).replace(" ", "").trim();
		}
	}

	public void preencheParcelamento(){
		if (totalCFe.compareTo(new BigDecimal(0))==1) {
			this.listaParcelamento = auxCalculo.preencheParcelamento(this.formaPag, this.totalCFe, 1L); 
		}
	}
	
	/**
	 * M todo para definir o pre o do produto conforme sele  o da tabela de pre os
	 */
	public void definePreco() {
		if (this.produto.getId() != null) {
			System.out.println("Estou dentro do definePreco produto != de null");
			switch (this.tabelaSelecionada) {
			case TA:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco1();
				System.out.println("Setando pre o1");
				break;
			case TB:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco2();
				System.out.println("Setando pre o2");
				break;
			case TC:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco3();
				System.out.println("Setando pre o3");
				break;
			case TD:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco4();
				System.out.println("Setando pre o4");
				break;
			case TE:
				this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco5();
				System.out.println("Setando pre o5");
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
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Produto> getLazyProduto(){
		this.produtoModel = new AbstractLazyModel<Produto>() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2192727694333471231L;

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

	public void onRowSelectProduto(SelectEvent event)throws IOException{
		this.produto = (Produto) event.getObject();
		this.ref = this.produto.getReferencia();
		this.precoVenda = this.produto.getListaCustoProduto().get(0).getPreco1();
	}
	
	/**
	 * M todo que seta o c digo de barras quando selecionado em uma lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectBarras(SelectEvent event)throws IOException{
		this.barrasEstoque = (BarrasEstoque) event.getObject();
		this.item.setBarras(this.barrasEstoque);
}

	
	@Transactional
	public void onRowSelect(SelectEvent event) throws IOException {
	    CFe selecionado = (CFe) event.getObject();
	    this.nfce = null;
	    if (selecionado == null || selecionado.getId() == null) {
	        return;
	    }

	    this.cfe = cfeDao.pegaCfeLazy(selecionado.getId(), pegaIdEmpresa(), pegaIdFilial());
	    this.cupomSelecionado = CupomFiscalFactory.fromCfe(this.cfe);

	    this.viewState = ViewState.EDITING;
	}

	@Transactional
	public void onRowSelectNfce(SelectEvent event) {
		Nfce selecionado = (Nfce) event.getObject();
		this.cfe = null;
		if (selecionado == null || selecionado.getId() == null) return;

		this.nfce = nfceDao.pegaNfceLazy(selecionado.getId(), pegaIdEmpresa(), pegaIdFilial());
		this.cupomSelecionado = CupomFiscalFactory.fromNfce(this.nfce);
		this.viewState = ViewState.EDITING;
	}

	public void telaListaProduto(){
		this.produtoModel = getLazyProduto();
		this.updateAndOpenDialog("PesquisaProdutoSatDialog", "dialogPesquisaProdutoSat");
	}

	@Transactional
	public void excluiItem(ItemCFe itemSelect){
		try{
			this.itemSelecionado = itemSelect;
			System.out.println("inico da exclusao do item");
			if (!this.cfe.getListaItem().isEmpty() && this.itemSelecionado != null){
				this.cfe.getListaItem().remove(itemSelect);
				this.totalCFe = this.totalCFe.subtract(itemSelect.getValorTotal());
				this.totalDesconto = this.totalDesconto.subtract(itemSelect.getDesconto());
				if (this.formaPag != null) {
					preencheParcelamento();
				}
				if (this.viewState == ViewState.EDITING) {
					if (itemSelect.getId() != null) {
						System.out.println("Estou no Exclui Item devolvendo a quantidade do item para o estoque! item ID: " + itemSelect.getId());
						this.listaItemPodeExcluir.add(itemSelect);
					}
				}
			}
			this.listaItemModel = getListaItemModel();
			this.addWarning(true, "cfe.list.delete", itemSelect.getProduto().getReferencia());
		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}

	public AbstractDataModel<ItemCFe> getListaItemModel(){
		this.listaItemModel = new AbstractDataModel<ItemCFe>(this.cfe.getListaItem());
		return this.listaItemModel;
	}

	public void localizaProduto() {
		try {
			this.item = new ItemCFe();
			BigDecimal valorDeDesconto = new BigDecimal("0");
			BigDecimal percentualDesconto = new BigDecimal("0");
			if (this.descontoPercentual){
				percentualDesconto = this.desPercVal.divide(new BigDecimal("100"),mc).setScale(2,RoundingMode.HALF_EVEN);
			}
			if (this.ref != null && this.quantidade.compareTo(new BigDecimal("0")) == 1 ){
				this.barrasEstoque = this.barrasDao.encontraBarrasPorEmpresa(this.ref, getUsuarioAutenticado().getIdEmpresa(), pegaIdFilial());
				if (this.barrasEstoque != null ) {
					this.produto = this.produtoDao.findById(this.barrasEstoque.getProdutoBase().getId(), false);
					if(this.produto != null) {
						this.item.setBarras(this.barrasEstoque);
					}
				}else {
					this.produto = this.produtoDao.pegaProdutoRef(this.ref, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
					this.listaBarrasTemp = this.barrasDao.listaBarrasPorProduto(this.produto, pegaIdEmpresa(), pegaIdFilial());
					if (this.listaBarrasTemp !=null && this.listaBarrasTemp.size() > 1) {
						// chamar lista para selecionar a barras que esta sendo vendida e setar para This.produto.
						telaListaBarras();
					}else {
						if (this.listaBarrasTemp != null && this.listaBarrasTemp.size() == 1) {
//							this.produto = this.produtoDao.findById(this.listaBarrasTemp.get(0).getProdutoBase().getId(),false);
							this.produto = this.produtoDao.pegaProdutoID(this.listaBarrasTemp.get(0).getProdutoBase().getId(), pegaIdEmpresa(),pegaIdFilial());
							this.item.setBarras(this.listaBarrasTemp.get(0));
						}
					}
				}
//				this.produto = this.produtoDao.pegaProdutoRef(this.getRef(), getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
				System.out.println("Estou apos localizar o produto: " + this.produto.getDescricao());
				setaCusto();
				System.out.println("Estou apos setar o custo do produto: " + this.custoProduto.getCusto());
				if (this.produto != null){	
					System.out.println("adicionando item na lista!");
					this.item.setProduto(this.produto);
					this.item.setPorcentagem(this.descontoPercentual);
					switch (tabelaSelecionada) {
					case TA:
						if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0 ){
							if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
								this.item.setValorUnitario(this.custoProduto.getPreco1().setScale(3,RoundingMode.HALF_EVEN));
							}else {
								this.item.setValorUnitario(this.precoVenda);
							}
							this.item.setQuantidade(this.quantidade);
							this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							break;
						}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
							if (this.isDescontoPercentual()){
								this.item.setQuantidade(this.quantidade);
								valorDeDesconto = this.custoProduto.getPreco1().multiply(percentualDesconto);
								this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
								System.out.println("desconto : "+this.item.getDesconto());
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco1().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);	
								}
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}else{
								valorDeDesconto = this.desPercVal;
								this.item.setDesconto(valorDeDesconto);
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco1().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}
						}
					case TB:
						if  (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
							if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
								this.item.setValorUnitario(this.custoProduto.getPreco2().setScale(3,RoundingMode.HALF_EVEN));
							}else {
								this.item.setValorUnitario(this.precoVenda);
							}
							this.item.setQuantidade(this.quantidade);
							this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							break;
						}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
							if (this.isDescontoPercentual()){
								this.item.setQuantidade(this.quantidade);
								valorDeDesconto = this.custoProduto.getPreco2().multiply(percentualDesconto);
								this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco2().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}else{
								valorDeDesconto = this.desPercVal;
								this.item.setDesconto(valorDeDesconto);
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco2().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}
						}
					case TC:
						if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
							if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
								this.item.setValorUnitario(this.custoProduto.getPreco3().setScale(3,RoundingMode.HALF_EVEN));
							}else {
								this.item.setValorUnitario(this.precoVenda);
							}
							this.item.setQuantidade(this.quantidade);
							this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							break;
						}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
							if (this.isDescontoPercentual()){
								this.item.setQuantidade(this.quantidade);
								valorDeDesconto = this.custoProduto.getPreco3().multiply(percentualDesconto);
								this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco3().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}else{
								valorDeDesconto = this.desPercVal;
								this.item.setDesconto(valorDeDesconto);
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco3().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}
						}
					case TD:
						if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
							if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
								this.item.setValorUnitario(this.custoProduto.getPreco4().setScale(3,RoundingMode.HALF_EVEN));
							}else {
								this.item.setValorUnitario(this.precoVenda);
							}
							this.item.setQuantidade(this.quantidade);
							this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							break;
						}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
							if (this.isDescontoPercentual()){
								this.item.setQuantidade(this.quantidade);
								valorDeDesconto = this.custoProduto.getPreco4().multiply(percentualDesconto);
								this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco4().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}else{
								valorDeDesconto = this.desPercVal;
								this.item.setDesconto(valorDeDesconto);
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco4().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}
						}
					case TE:
						if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 0){
							if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
								this.item.setValorUnitario(this.custoProduto.getPreco5().setScale(3,RoundingMode.HALF_EVEN));
							}else {
								this.item.setValorUnitario(this.precoVenda);
							}
							this.item.setQuantidade(this.quantidade);
							this.item.setValorTotal(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
							break;
						}else if (this.desPercVal.compareTo(new BigDecimal("0.00")) == 1){
							if (this.isDescontoPercentual()){
								this.item.setQuantidade(this.quantidade);
								valorDeDesconto = this.custoProduto.getPreco5().multiply(percentualDesconto);
								this.item.setDesconto(valorDeDesconto.multiply(this.item.getQuantidade()));
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco5().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}else{
								valorDeDesconto = this.desPercVal;
								this.item.setDesconto(valorDeDesconto);
								if (this.precoVenda.compareTo(new BigDecimal("0"))== 0) {
									this.item.setValorUnitario(this.custoProduto.getPreco5().setScale(3,RoundingMode.HALF_EVEN));
								}else {
									this.item.setValorUnitario(this.precoVenda);
								}
								this.item.setQuantidade(this.quantidade);
								this.cfe.setDesconto(this.cfe.getDesconto().add(this.item.getDesconto()).setScale(2,RoundingMode.HALF_EVEN));
								this.item.setValorTotal((this.item.getValorUnitario().multiply(this.quantidade)).subtract(this.item.getDesconto()).setScale(3,RoundingMode.HALF_EVEN));
								this.item.setValorTotalBruto(this.quantidade.multiply(this.item.getValorUnitario()).setScale(3,RoundingMode.HALF_EVEN));
								break;
							}
						}
					default:
						break;
					}
					this.tempValorUnitario = this.item.getValorUnitario();
					this.tempTotalUnitario  = this.item.getValorTotal();
					this.cfe.getListaItem().add(this.item);
					this.totalCFe = this.totalCFe.add(this.item.getValorTotal());
					this.totalDesconto = this.totalDesconto.add(this.item.getDesconto());
					this.ref = "";
					this.precoVenda = new BigDecimal("0");
					if (this.formaPag.getId() != null) {
						preencheParcelamento();
					}
				}
			}
		}catch (Exception e) {
			this.addError(true, "Erro na fun ao de localizacao de produto: " +  e.getMessage() + " " + e.getCause());
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

	public TabelaPreco[] getTabelaPreco(){
		return TabelaPreco.values();
	}
	
	@Transactional
	public void doExcluir() {
		if (this.cupomSelecionado == null) {
			this.addError(true, "Selecione um cupom (CFe ou NFC-e) antes de imprimir.");
			return;
		}

		try {
			Long origemId = this.cupomSelecionado.getOrigemId();

			boolean isNfceSelecionada = (this.nfce != null && this.nfce.getId() != null && Objects.equals(origemId, this.nfce.getId()));
			if (isNfceSelecionada) {
				if (!this.nfce.isEmitido()) { // Emissao NFCE
					if (this.nfce.getStatusEmissao().equals(StatusNfe.SA)||this.nfce.getStatusEmissao().equals(StatusNfe.EE) )  {
						this.nfceDao.delete(this.nfce);
					}
				}
			}else {
				if (this.cfe.getId() != null) {
					if (this.cfe.getStatusEmissao().equals(StatusNfe.SA)||this.cfe.getStatusEmissao().equals(StatusNfe.EE) )  {
						//					this.destinatarioDao.delete(this.cfe.getDestinatario());
						//					this.emitenteDao.delete(this.cfe.getEmitente());
						this.cfeDao.delete(this.cfe);
					}else {
						this.addWarning(true, "error.delete.sat", this.cfe.getNumeroNota());
					}
				}
			}
		}catch (HibernateException h) {

			this.addError(true, "Erro ao excluir da base de dados {0}", h.getMessage());
			this.respostaAcbrLocal = ("Erro ao excluir da base de dados " + h.getMessage());

		}catch(Exception e ) {
			this.addError(true, "Erro desconhecido : {0}", e.getMessage());
			this.respostaAcbrLocal  = ("Erro desconhecido : "+ e.getMessage());
		}
	}
	
	/**
	 * M todo que recebe o ESTOQUE do item do pedido 
	 *
	 * @param item
	 * @return
	 * @throws EstoqueException 
	 */
	public Estoque pegaEstoque (ItemCFe item) throws EstoqueException {
//		try {
		return estoqueUtil.preencheEstoqueItem(item, pegaIdEmpresa(), pegaIdFilial());
//		}catch (EstoqueException e) {
//			this.addError(true, "exception.error.fatal", e.getMessage());
//			return null;
//		}
	}
	
	public boolean permiteFiscalEstoqueNegativo() {
		return this.empresaUsuario.getEmp().isEstoqueFiscalNegativo();
	}
	
	/**
	 * M todo que retira do estoque a quantidade informada, caso
	 * BarrasEstoque ou NcmEstoque esteja nulo ser  criado uma base com quantidade = 0 para depois negativar.
	 * @param listaItem
	 * @throws HibernateException
	 * @throws EstoqueException
	 */
	
	@Transactional
	public void retiraDoEstoque(List<ItemCFe> listaItem) throws HibernateException, EstoqueException {
			Estoque estoqueTemp = new Estoque();
			for (ItemCFe itemPedidoTemp : listaItem) {
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				if (permiteFiscalEstoqueNegativo() == false) {
					if (estoqueTemp.getNcmEstoque().getEstoque().compareTo(itemPedidoTemp.getQuantidade()) >= 0 ) {
						estoqueTemp = estoqueUtil.subtraiEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), true,false);
						ncmDao.save(estoqueTemp.getNcmEstoque());
					}else {
						throw new EstoqueException(this.translate("estoque.nfe.emite.item.outOfStock"));
					}
				}else {
					try {
					estoqueTemp = estoqueUtil.subtraiEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), true,false);
					ncmDao.save(estoqueTemp.getNcmEstoque());
					}catch (EstoqueException e) {
						throw new EstoqueException(this.translate("hibernate.persist.fail.barrasEstoque"));
					}
				}
			}
	}
	
	/**
	 * M todo que devolve para o estoque a quantidade que anteriormente havia sido retirado.
	 * @param listaItem
	 */
	
	@Transactional
	public void devolveParaEstoque(List<ItemCFe> listaItem) throws EstoqueException, HibernateException {
			Estoque estoqueTemp = new Estoque();
			for (ItemCFe itemPedidoTemp : listaItem) {
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				estoqueTemp = estoqueUtil.acrescentaEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), true,false);
				ncmDao.save(estoqueTemp.getNcmEstoque());	
				
			}
	}

	@Transactional
	public String doSalvar() {
		try {
			boolean nfceAtivado = AbstractBeanEmpDS.<Boolean>campoEmpUser(this.empresaUsuario,Filial::isNFCeAtivo,Empresa::isNFCeAtivo ).booleanValue();
			preencheCupom();
			if (nfceAtivado) { // NCFE Salvar 
				for (ParcelasNfe parcelasNfe : this.listaParcelamento) {
					parcelasNfe.setCfe(this.cfe);
				}
				this.cfe.getListaParcelas().addAll(this.listaParcelamento);
				this.cfe.setStatusEmissao(StatusNfe.SA);
				this.nfce = nfceEmissaoService.criaSalvaNfce(this.cfe,pegaIdEmpresa(),pegaIdFilial());
				this.viewState = ViewState.EDITING;
				
				if (this.nfce == null ) {
					this.addError(true,"Error.save.Nfce");
					return null;
				}
				this.cfe = null;
				return toListSat();
			}
		
			if (this.cfe.getId() == null){
				
				if (this.cfe.getListaItem().size() > 0) {
					this.listaItemTemp = calculaTributos.preencheListaDeItensCfe(this.cfe.getListaItem(), pegaIdEmpresa(),pegaIdFilial());
				}else {
					throw new TributosException(this.translate("tributosException.listaEmpty"));
				}
				if (this.listaItemTemp.size() > 0) {
					this.cfe.setListaItem(this.listaItemTemp);
				}else {
					throw new TributosException(this.translate("tributosException.listaEmpty"));
				}
				if (this.documento != null) {
					preencheDestinatario(this.documento);
				}
//				this.destinatario = destinatarioDao.save(this.destinatario);
				this.emitente = preencheEmitente();
//				this.emitente = this.emitenteDao.save(this.emitente);
				this.cfe.setEmitente(this.emitente);
				this.cfe.setDestinatario(this.destinatario);
				//		this.cfe.setValorTotalProdutos(this.totalCFe);
				this.cfe.setDesconto(this.totalDesconto);
				this.cfe.setFormaPagamento(this.formaPag);
				this.cfe = calculaTributos.calculaTotaisCFe(this.cfe);
				this.cfe.setEmitido(false);
				this.cfe.setStatusEmissao(StatusNfe.SA);
				
				// baixar estoque geral
				retiraDoEstoque(this.listaItemTemp);
				
//					System.out.println("IBR - salvando os itens da cfe");
					for (ItemCFe item : this.listaItemTemp) {
						item.setCfe(this.cfe);
//						itemDao.save(item);
					}

					System.out.println("IBR - salvando o pagamento da cfe");
					for (ParcelasNfe parcelasNfe : this.listaParcelamento) {
						parcelasNfe.setCfe(this.cfe);
						
//						parcelaDao.save(parcelasNfe);
						System.out.println("estou dentro do foreach parcelas");
					}
					this.cfe.setListaParcelas(this.listaParcelamento);
					this.cfeDao.save(this.cfe);
				this.viewState = ViewState.EDITING;
				this.addInfo(true, "save.sucess");
				//				Thread.sleep (10000);
				//				changeToEdit(this.cfe.getId());
				return toListSat();
			}else {
				System.out.println("Estou gravando os dados dentro do ALTERAR");
				
				if (!this.listaItemPodeExcluir.isEmpty()) {
					devolveParaEstoque(this.listaItemPodeExcluir);
					for (ItemCFe itemExclui : this.listaItemPodeExcluir) {
						this.itemDao.delete(itemExclui);
					}
					for (ParcelasNfe parcela : this.listaParcelamento) {
						this.parcelaDao.delete(parcela);
					}
					
				}
				//				preparaBaseParaAlterar();
				this.cfe.setValorTotalProdutos(this.totalCFe);
				this.cfe.setDesconto(this.totalDesconto);
				this.cfe.setFormaPagamento(this.formaPag);
				//				}
				//Persistindo as altera  es nos itens da CFe
				System.out.println("IBR - salvando os itens da cfe");
				for (ItemCFe item : this.cfe.getListaItem()) {
					if (this.item.getCfe() == null) {
						item.setCfe(this.cfe);
					}
				}
				
				List<ItemCFe> listaTemp = new ArrayList<ItemCFe>();
				for (ItemCFe itemDiferente : this.cfe.getListaItem()) {
					if (itemDiferente.getId() == null) {
						listaTemp.add(itemDiferente);
					}
				}
				if (listaTemp.size() > 0) {
					retiraDoEstoque(listaTemp);
				}
				this.cfe = calculaTributos.calculaTotaisCFe(this.cfe);
				preencheParcelamento();
				//Persistindo a nova forma de pagamento / parcelamento
				System.out.println("IBR - salvando o pagamento da cfe");
				for (ParcelasNfe parcelasNfe : this.listaParcelamento) {
					parcelasNfe.setCfe(this.cfe);
					System.out.println("estou dentro do foreach parcelas");
				}
				this.cfe.setListaParcelas(this.listaParcelamento);
				this.cfeDao.save(this.cfe);
				this.addInfo(true, "save.update");
				return toListSat();
			}
		}catch (EstoqueException es) {
			this.addError(true, "caixa.error", es.getMessage());
			this.respostaAcbrLocal = (this.translate("caixa.error") + es.getMessage());
			return null;
		}catch (TotaisCFeException tot) {
			this.addError(true, "caixa.error", tot.getMessage());
			this.respostaAcbrLocal = (this.translate("caixa.error") + tot.getMessage());
			return null;
		}catch (TributosException tri) {
			this.addError(true, "hibernate.persist.fail", tri.getMessage());
			this.respostaAcbrLocal = (this.translate("hibernate.persist.fail") + tri.getMessage());
			return null;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			this.respostaAcbrLocal = (this.translate("hibernate.persist.fail") + h.getMessage());
			return null;

		}catch(Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			this.respostaAcbrLocal  = (this.translate("exception.error.fatal") + e.getMessage());
			return null;
		}
	}

	@Transactional
	public void preparaBaseParaAlterar() {
		ParcelasNfe parcelaTemp = new ParcelasNfe();
		ItemCFe itemTemp = new ItemCFe();
		if (!this.parcelasRemover.isEmpty()) {
			for (int i = 0 ; this.parcelasRemover.size() > i; i++) {
				parcelaTemp = this.parcelaDao.findById(this.parcelasRemover.get(i).getId(),false);
				this.parcelaDao.delete(parcelaTemp);
				//				this.parcelasRemover = this.parcelaDao.listaParcelasPorCFe(this.cfe, pegaIdEmpresa(), pegaIdFilial());
			}
			//			for (ParcelasNfe parcela : this.parcelasRemover) {
			//				this.parcelaDao.delete(parcela);
			//			}
		}
		if (!this.listaItemRemover.isEmpty()) {
			System.out.println("SATIBR - dentro do isEMPTY Tamanho da lista antes do for : " + this.listaItemRemover.size());
			for (int c = 0 ; this.listaItemRemover.size() > c; c++) {
				System.out.println("Tamanho da lista de itens dentro do for : " + this.listaItemRemover.size());
				System.out.println("Total de vezes do loop : "+ c);
				itemTemp = this.itemDao.findById(this.listaItemRemover.get(c).getId(), false);
				this.itemDao.delete(itemTemp);

			}
		}

	}

	public EmitenteCFe preencheEmitente(){
		EmitenteCFe emissor = new EmitenteCFe();
		if (this.getUsuarioAutenticado().getIdFilial() == null){
			emissor.setEmpresa(this.empresaDao.findById(pegaIdEmpresa(), false));
		}else{
			emissor.setFilial(this.filialDao.findById(pegaIdFilial(), false));
		}
		return emissor;
	}

	public SatResposta validaRetornoCFe(String resposta) {
		SatResposta satResposta = new SatResposta();
		String resultadoUp = resposta.toUpperCase();
		if (localiza.localizaPalavra(resultadoUp, "CODIGODERETORNO")) {
			int inicio = 0;
			int fim = 0;
			inicio = resultadoUp.indexOf("CODIGODERETORNO");
			fim = resultadoUp.indexOf("NUMEROSESSAO");
			String codigoRetorno = resultadoUp.substring(inicio+16,fim);
			System.out.println("C digo: " + codigoRetorno);
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
				satResposta.setMotivo("C digo de ativa  o inv lido");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6002":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT ainda n o ativado.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6003":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT n o vinculado ao AC");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6004":
				satResposta.setValido(false);
				satResposta.setMotivo("Vincula  o do AC n o confere.");
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
				satResposta.setMotivo("SAT bloqueado por falta de comunica  o");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6009":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT bloqueado, c digo de ativa  o incorreto");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6010":
				satResposta.setValido(false);
				satResposta.setMotivo("Erro de valida  o do conte do.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6011":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT bloqueado por vencimento do certificado digital.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6097":
				satResposta.setValido(false);
				satResposta.setMotivo("N mero de sess o inv lido");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6098":
				satResposta.setValido(false);
				satResposta.setMotivo("SAT em processamento. Tente novamente.");
				satResposta.setCodigoRetorno(codigoRetorno);
				break;
			case "6099":
				satResposta.setValido(false);
				satResposta.setMotivo("Erro desconhecido na emiss o.");
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
	/**
	 * Retorna form de envio de email avulso do SAT
	 */
	public void telaEmailAvulso(){
		this.updateAndOpenDialog("enviaEmailCFeId", "dialogEnviaEmailCFe");
	}

	public void enviarEmailSat() {

		if (this.cupomSelecionado == null) {
			this.addError(true, "Selecione um cupom (CFe ou NFC-e) antes de enviar o e-mail.");
			return;
		}
		if (this.emailCfe == null || this.emailCfe.trim().isEmpty()) {
			this.addError(true, "Informe o e-mail de destino.");
			return;
		}

		try {
			Long origemId = this.cupomSelecionado.getOrigemId();

			// 1) Se o cupom selecionado vier de NFC-e (origemId == nfce.id), envia NFC-e
			if (this.nfce != null && this.nfce.getId() != null && origemId != null && origemId.equals(this.nfce.getId())) {

				String caminhoXml = (this.nfce.getCaminhoXml() != null ? this.nfce.getCaminhoXml().trim() : "");
				if (caminhoXml.isEmpty()) {
					this.addError(true, "Email com a NFC-e não foi enviado! XML da NFC-e não informado.");
					return;
				}

				this.respostaAcbrLocal = acbr.nfeEnviarEmail(
						pegaConexao(),
						emailCfe,
						caminhoXml,
						true,   // envia PDF
						"",     // assunto (opcional - usar configurado no ACBr)
						"",     // cópia (opcional)
						"",     // anexos adicionais (opcional)
						""      // reply-to (opcional)
				);
				return;
			}

			// 2) Se o cupom selecionado vier de CFe (origemId == cfe.id), envia CFe
			if (this.cfe != null && this.cfe.getId() != null && origemId != null && origemId.equals(this.cfe.getId())) {

				if (!this.cfe.isEmitido()) {
					this.addError(true, "Email com a CFe não foi enviado! Cupom não emitido.");
					return;
				}

				this.respostaAcbrLocal = acbr.satEnviarEmailCFe(
						pegaConexao(),
						emailCfe,
						this.cfe.getCaminho(),
						String.valueOf(this.cfe.getNumeroNota()));
				return;
			}

			// 3) Fallback: tenta NFC-e se tiver id e xml, senão tenta CFe
			if (this.nfce != null && this.nfce.getId() != null) {
				String caminhoXml = (this.nfce.getCaminhoXml() != null ? this.nfce.getCaminhoXml().trim() : "");
				if (!caminhoXml.isEmpty()) {
					this.respostaAcbrLocal = acbr.nfeEnviarEmail(pegaConexao(), emailCfe, caminhoXml, true, "", "", "", "");
					return;
				}
			}
			if (this.cfe != null && this.cfe.getId() != null) {
				if (this.cfe.isEmitido()) {
					this.respostaAcbrLocal = acbr.satEnviarEmailCFe(pegaConexao(), emailCfe, this.cfe.getCaminho(), String.valueOf(this.cfe.getNumeroNota()));
					return;
				}
			}

			this.addError(true, "Não foi possível identificar o tipo de cupom selecionado (CFe/NFC-e).");

		} catch (IOException e) {
			this.addError(true, "caixa.error", e.getMessage());
		}
	}


	public void imprimiSat() {
		if (this.cupomSelecionado == null) {
			this.addError(true, "Selecione um cupom (CFe ou NFC-e) antes de imprimir.");
			return;
		}

		try {
			Long origemId = this.cupomSelecionado.getOrigemId();

			boolean isNfceSelecionada = (this.nfce != null && this.nfce.getId() != null && Objects.equals(origemId, this.nfce.getId()));
			boolean isCfeSelecionada = (this.cfe != null && this.cfe.getId() != null && Objects.equals(origemId, this.cfe.getId()));

			if (isNfceSelecionada) {
				if (!this.nfce.isEmitido()) {
					this.addError(true, "NFC-e ainda não foi emitida!");
					return;
				}
				String alvo = (this.nfce.getCaminhoXml() != null ? this.nfce.getCaminhoXml().trim() : "");
				if (alvo.isEmpty()) {
					alvo = (this.nfce.getChaveAcesso() != null ? this.nfce.getChaveAcesso().trim() : "");
				}
				if (alvo.isEmpty()) {
					this.addError(true, "Não foi possível imprimir: NFC-e sem XML/Chave.");
					return;
				}
				this.respostaAcbrLocal = acbr.nfeImprimirDanfce(pegaConexao(), alvo);
				return;
			}

			if (isCfeSelecionada) {
				if (!this.cfe.isEmitido()) {
					this.addError(true, "CFe ainda nao foi emitido!");
					return;
				}

				this.respostaAcbrLocal = acbr.satImprimiExtratoVenda(pegaConexao(), this.cfe.getCaminho());
				return;
			}

			// Fallback: tenta NFC-e se tiver XML; senão tenta CFe
			if (this.nfce != null && this.nfce.getId() != null && this.nfce.isEmitido()) {
				String alvo = (this.nfce.getCaminhoXml() != null ? this.nfce.getCaminhoXml().trim() : "");
				if (!alvo.isEmpty()) {
					this.respostaAcbrLocal = acbr.nfeImprimirDanfce(pegaConexao(), alvo);
					return;
				}
			}
			if (this.cfe != null && this.cfe.getId() != null && this.cfe.isEmitido()) {
				this.respostaAcbrLocal = acbr.satImprimiExtratoVenda(pegaConexao(), this.cfe.getCaminho());
				return;
			}

			this.addError(true, "Não foi possível identificar o tipo de cupom selecionado (CFe/NFC-e).");

		} catch (IOException e) {
			this.addError(true, "caixa.error", e.getMessage());
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
	public void doEmitirPelaLista()throws TributosException, TotaisCFeException {

		if (this.cupomSelecionado == null) {
			this.addError(true, "Selecione um cupom (CFe ou NFC-e) antes de imprimir.");
			return;
		}

		try {
			Long origemId = this.cupomSelecionado.getOrigemId();

			boolean isNfceSelecionada = (this.nfce != null && this.nfce.getId() != null && Objects.equals(origemId, this.nfce.getId()));
			boolean isCfeSelecionada = (this.cfe != null && this.cfe.getId() != null && Objects.equals(origemId, this.cfe.getId()));
			if (isNfceSelecionada) {
				if (!this.nfce.isEmitido()) { // Emissao NFCE
					this.cupomSelecionado.setItens(calculaTributos.preencheListaDeItensCfe(this.cupomSelecionado.getItens(),pegaIdEmpresa(),pegaIdFilial()));
					Nfce nfcTemp = new Nfce();
						nfcTemp.setListaRecebimentosAgrupados(nfceDao.pegaListaRecebimentoParcial(origemId, pegaIdEmpresa(), pegaIdFilial()));
					for (RecebimentoParcial rec : nfcTemp.getListaRecebimentosAgrupados()) {
						System.out.println("recebimento parcial :" + rec.getValorRecebido() + " tamanho: "+ nfcTemp.getListaRecebimentosAgrupados().size());
					}
					NfceEmissaoResultado res = nfceEmissaoService.emitirCupomSalvo(pegaConexao(), nomeArquivo, this.cupomSelecionado,this.nfce,nfcTemp.getListaRecebimentosAgrupados() ,pegaIdEmpresa(), pegaIdFilial(), true);
					this.nfce = (res != null ? res.getNfce() : null);
					if (res == null || !res.isValido()) {
						this.addError(true, res != null ? res.getMotivo() : "Falha ao emitir NFC-e (retorno nulo)");
					}
					this.addInfo(true, "NFC-e emitida com sucesso: " + res.getNumero() + " (Série " + res.getSerie() + ")");
				}
			}
			if (isCfeSelecionada) {
				if (!this.cfe.isEmitido()) {
					if (this.cfe.getId() == null) {
						this.cfe.setListaItem(calculaTributos.preencheListaDeItensCfe(this.cfe.getListaItem(), pegaIdEmpresa(),pegaIdFilial()));
					}else {
						this.cfe.setListaItem(this.itemDao.listaDeItensPorCFe(this.cfe, pegaIdEmpresa(), pegaIdFilial()));
						this.cfe.setListaItem(calculaTributos.preencheListaDeItensCfe(this.cfe.getListaItem(), pegaIdEmpresa(),pegaIdFilial()));
					}
					this.respostaAcbrLocal = acbr.criarArqIniSatMaqRemota(pegaConexao(), this.nomeArquivo, this.cfe,pegaVersaoSat());
					String retornoAcbr = acbr.satCriarEnviarCFe(pegaConexao(), this.nomeArquivo);
					SatResposta satResposta = validaRetornoCFe(retornoAcbr); 
					System.out.println("Valido: " + satResposta.isValido() + "\n C digo:" + satResposta.codigoRetorno);
					if (satResposta.isValido()) {
						this.cfe.setCaminho(satResposta.getPatch());
						this.cfe.setStatusEmissao(StatusNfe.EN);
						this.cfe.setNumeroNota(satResposta.getNumero());
						this.cfe.setEmitido(true);
						System.out.println("Campos CFE preenchidos caminho:" + this.cfe.getCaminho() + " Chave de acesso:"
								+ this.cfe.getNumeroNota() + " Emitido:" + this.cfe.getStatusEmissao() + " emitido:" + this.cfe.isEmitido());
						this.respostaAcbrLocal = acbr.geraPDFExtratoVenda(pegaConexao(), this.cfe.getCaminho(), this.cfe.getNumeroNota().trim() + ".pdf");
						this.respostaAcbrLocal = acbr.satImprimiExtratoVenda(pegaConexao(), this.cfe.getCaminho());
						this.addInfo(true, satResposta.motivo);
						this.cfe = this.cfeDao.save(this.cfe);
					}else {

						this.addError(true, satResposta.motivo);
					}
				}else {
					this.addWarning(true, "CFe j  emitido!");
				}
			}
		}catch (IOException i) {
			this.addError(true, "caixa.error", i.getMessage());
		}catch (Exception e) {
			this.addError(true, "caixa.error", e.getMessage());
		}
	}
	
	public void preencheCupom() throws TributosException, TotaisCFeException {
		if (this.cfe.getListaItem().size() > 0) {
			this.listaItemTemp = calculaTributos.preencheListaDeItensCfe(this.cfe.getListaItem(),pegaIdEmpresa(),pegaIdFilial());
		}else {
			throw new TributosException(this.translate("tributosException.listaEmpty"));
		}
		if (this.listaItemTemp.size() > 0) {
			this.cfe.setListaItem(this.listaItemTemp);
		}else {
			throw new TributosException(this.translate("tributosException.listaEmpty"));
		}
		if (this.documento != null) {
			preencheDestinatario(this.documento);
		}
		this.emitente = preencheEmitente();
		//		this.emitente = this.emitenteDao.save(this.emitente);
		//		this.destinatario = destinatarioDao.save(this.destinatario);

		this.cfe.setEmitente(this.emitente);
		this.cfe.setDestinatario(this.destinatario);
		//		this.cfe.setValorTotalProdutos(this.totalCFe);
		this.cfe = calculaTributos.calculaTotaisCFe(this.cfe);
		this.cfe.setDesconto(this.totalDesconto);
		this.cfe.setFormaPagamento(this.formaPag);



		//		if (this.cfe.getId() != null) {
		//			System.out.println("IBR - salvando os itens da cfe");
		for (ItemCFe item : this.listaItemTemp) {
			item.setCfe(this.cfe);
			//				itemDao.save(item);
		}

		System.out.println("IBR - salvando o pagamento da cfe");
		for (ParcelasNfe parcelasNfe : this.listaParcelamento) {
			parcelasNfe.setCfe(this.cfe);
			//				parcelaDao.save(parcelasNfe);
			System.out.println("estou dentro do foreach parcelas");
		}
		//		}
		this.cfe.setListaItem(this.listaItemTemp);
		this.cfe = this.cfeDao.save(this.cfe);
	}

	@Transactional
	public String doEmitir() {
		try {
			boolean nfceAtivado = AbstractBeanEmpDS.<Boolean>campoEmpUser(this.empresaUsuario,Filial::isNFCeAtivo,Empresa::isNFCeAtivo ).booleanValue();
			preencheCupom();
			if (nfceAtivado) { // NCFE emissao
				NfceEmissaoResultado res = nfceEmissaoService.emitir(pegaConexao(), nomeArquivo, this.cfe, pegaIdEmpresa(), pegaIdFilial(), true,false);
				this.nfce = (res != null ? res.getNfce() : null);
				if (res == null || !res.isValido()) {
					this.addError(true, res != null ? res.getMotivo() : "Falha ao emitir NFC-e (retorno nulo)");
					return null;
				}
				this.addInfo(true, "NFC-e emitida com sucesso: " + res.getNumero() + " (Série " + res.getSerie() + ")");
				return toListSat();
			} else {
				this.respostaAcbrLocal = acbr.criarArqIniSatMaqRemota(pegaConexao(), this.nomeArquivo, this.cfe,pegaVersaoSat());
				String retornoAcbr = acbr.satCriarEnviarCFe(pegaConexao(), this.nomeArquivo);
				SatResposta satResposta = validaRetornoCFe(retornoAcbr); 
				System.out.println("Valido: " + satResposta.isValido() + "\n C digo:" + satResposta.codigoRetorno);
				if (satResposta.isValido()) {
					this.cfe.setCaminho(satResposta.getPatch());
					this.cfe.setStatusEmissao(StatusNfe.EN);
					this.cfe.setNumeroNota(satResposta.getNumero());
					this.cfe.setEmitido(true);
					System.out.println("Campos CFE preenchidos caminho:" + this.cfe.getCaminho() + " Chave de acesso:"
							+ this.cfe.getNumeroNota() + " Emitido:" + this.cfe.getStatusEmissao() + " emitido:" + this.cfe.isEmitido());
					this.respostaAcbrLocal = acbr.geraPDFExtratoVenda(pegaConexao(), this.cfe.getCaminho(), this.cfe.getNumeroNota().trim() + ".pdf");
					this.respostaAcbrLocal = acbr.satImprimiExtratoVenda(pegaConexao(), this.cfe.getCaminho());
					this.addInfo(true, satResposta.motivo);
					this.destinatario.setCfe(this.cfe);
	//				this.destinatario = this.destinatarioDao.save(this.destinatario);
					this.cfe = this.cfeDao.save(this.cfe);


				}else {

					this.addError(true, satResposta.motivo);
				}
			}
			return toListSat();

		}catch (TributosException t){
			this.addError(true,"exception.error.fatal",t.getMessage());
			return null;
		}catch (TotaisCFeException tc) {
			this.addError(true,tc.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage() );
			this.addError(true, "rel: ", e.getCause());
			return null;
		}

	}

	public void consultaStatusSat() {
		try {
			this.respostaAcbrLocal = acbr.satConsultaStatus(pegaConexao());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.addError(true, "caixa.error", e.getMessage());
		}
	}

	public String toListSat() {
		return "formListSat.xhtml?faces-redirect=true";
	}

	public String newCFe() {
		return "formSatAvulso.xhtml?faces-redirect=true";
	}

	public String changeToEdit(Long idCFe) {
		return "formSatAvulso.xhtml?faces-redirect=true&idCFe=" + idCFe;
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

}
