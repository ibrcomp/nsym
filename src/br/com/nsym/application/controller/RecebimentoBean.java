package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.NfeException;
import br.com.ibrcomp.exception.RecebimentoNFeException;
import br.com.nsym.application.channels.AcbrComunica;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.misc.CodigoBarrasUtil;
import br.com.nsym.domain.misc.CpfCnpjUtils;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.misc.LocalizaRegex;
import br.com.nsym.domain.misc.ReceitaFinder;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Cor;
import br.com.nsym.domain.model.entity.cadastro.Departamento;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.entity.financeiro.ParcelaStatus;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoLancamento;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.NcmEstoque;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.nfe.Destinatario;
import br.com.nsym.domain.model.entity.fiscal.nfe.Emitente;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfeRecebida;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeNaoConfirmada;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeRecebida;
import br.com.nsym.domain.model.entity.fiscal.nfe.Transportador;
import br.com.nsym.domain.model.entity.tools.ManifestacaoDestinatario;
import br.com.nsym.domain.model.entity.tools.ReceitaFederalConsulta;
import br.com.nsym.domain.model.entity.tools.SitNfe;
import br.com.nsym.domain.model.entity.tools.TipoControleEstoque;
import br.com.nsym.domain.model.entity.tools.TipoMedida;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.CoresRepository;
import br.com.nsym.domain.model.repository.cadastro.DepartamentoRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.cadastro.TamanhoRepository;
import br.com.nsym.domain.model.repository.cadastro.TransportadoraRepository;
import br.com.nsym.domain.model.repository.financeiro.CustoProdutoRepository;
import br.com.nsym.domain.model.repository.financeiro.tools.ParcelasNfeRepository;
import br.com.nsym.domain.model.repository.fiscal.CFOPRepository;
import br.com.nsym.domain.model.repository.fiscal.NCMRepository;
import br.com.nsym.domain.model.repository.fiscal.NcmEstoqueRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.ItemNfeRecebidaRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.NfeNaoConfirmadaRepository;
import br.com.nsym.domain.model.repository.fiscal.nfe.NfeRecebidaRepository;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Named
@ViewScoped
public class RecebimentoBean extends AbstractBeanEmpDS<NfeNaoConfirmada> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ReceitaFinder pesquisaReceita;	

	@Getter
	@Setter
	private ReceitaFederalConsulta receita = new ReceitaFederalConsulta();

	@Getter
	@Setter
	private Endereco end = new Endereco();

	@Getter
	@Setter
	private EndComplemento complemento =new EndComplemento();

	@Inject
	private EnderecoRepository endDao;

	@Inject
	private CodigoBarrasUtil barrasUtil;

	@Inject
	private CFOPRepository cfopDao;

	@Inject
	private EndComplementoRepository endComplementoDao;

	@Getter
	@Setter
	private boolean deleted = false;

	@Getter
	@Setter
	private Empresa empresa = new Empresa();

	@Getter
	@Setter
	private Filial filial = new Filial();

	@Getter
	@Setter
	private Fornecedor fornecedor = new Fornecedor();

	@Inject
	private FornecedorRepository fornecedorDao;

	@Getter
	@Setter
	private Tributos tributos = new Tributos();

	@Inject
	private TributosRepository tributosDao;


	@Getter
	@Setter
	private NfeRecebida nfe = new NfeRecebida();

	@Inject
	private NfeRecebidaRepository nfeRecebidaDao;

	@Getter
	@Setter
	private ItemNfeRecebida item = new ItemNfeRecebida();

	@Inject
	private ItemNfeRecebidaRepository itemDao;

	@Getter
	@Setter
	private List<ItemNfeRecebida> listaItens = new ArrayList<>();

	@Getter
	@Setter
	private Produto produto = new Produto();

	@Inject
	private ProdutoRepository produtoDao;

	@Getter
	@Setter
	private Ncm ncm = new Ncm();

	@Inject
	private NCMRepository ncmDao;


	@Inject
	private BarrasEstoqueRepository barrasEstoqueDao ;

	@Getter
	@Setter
	private BarrasEstoque barrasEstoque = new BarrasEstoque();

	@Getter
	@Setter
	private List<BarrasEstoque> listaBarrasEstoque = new ArrayList<>();

	//	@Inject
	//	private ProdutoEstoqueRepository prodEstoqueDao ;

	@Setter
	private AbstractDataModel<ItemNfeRecebida> listaItemModel;

	@Getter
	private AbstractLazyModel<NfeNaoConfirmada> nfeRecebimentoModel;

	@Inject
	private EmpresaRepository empresaDao;

	@Inject
	private FilialRepository filialDao;

	@Inject
	private AcbrComunica acbr;

	@Getter
	@Setter
	private String respostaAcbrLocal = "";

	@Getter
	@Setter
	private String chaveAcesso="";

	@Getter
	@Setter
	private Uf codUf;

	@Getter
	@Setter
	private Transportador transporte = new Transportador();

	@Getter
	@Setter
	private Transportadora transportadora =new Transportadora();

	@Inject
	private TransportadoraRepository transportadoraDao;

	@Getter
	@Setter
	private ParcelasNfe parcelasNfe = new ParcelasNfe();

	@Inject
	private ParcelasNfeRepository parcelasNfeDao;

	@Getter
	@Setter
	private List<ParcelasNfe> listaParcelas = new ArrayList<>();

	@Getter
	@Setter
	private TipoPagamento tipoPagamento;

	@Getter
	@Setter
	private NfeNaoConfirmada nfNaoConfirmada;

	@Inject
	private NfeNaoConfirmadaRepository nfNaoConfirmadaDao ;

	@Inject
	private LocalizaRegex localiza;

	@Getter
	@Setter
	private String texto;

	@Getter
	@Setter
	private String resultadoFinal;

	@Getter
	@Setter
	private List<ItemNfeRecebida> listaItensSelecionados = new ArrayList<>();

	@Getter
	private AbstractLazyModel<Produto> produtoModel;

	@Getter
	@Setter
	private String ref;

	@Getter
	@Setter
	private String qAdicional = "0";

	@Getter
	@Setter
	private NcmEstoque ncmEstoque = new NcmEstoque();

	@Inject
	private NcmEstoqueRepository ncmEstoqueDao;

	//	@Getter
	//	@Setter
	//	private List<BarrasEstoque> listaBarrasTemp = new ArrayList<>();

	@Getter
	@Setter
	private BarrasEstoque barrasTemp = new BarrasEstoque();

	@Inject
	private BarrasEstoqueRepository barrasDao;

	@Getter
	@Setter
	private boolean isItemnfe = false;

	@Getter
	@Setter
	private Cor cor = new Cor();

	@Getter
	@Setter
	private Tamanho tamanho = new Tamanho();

	@Getter
	@Setter
	private List<Cor> listaCores = new ArrayList<>();

	@Inject
	private CoresRepository corDao;

	@Getter
	@Setter
	private List<Tamanho> listaTamanhos = new ArrayList<>();

	@Inject
	private TamanhoRepository tamanhoDao;

	@Getter
	@Setter
	private ProdutoCusto custoProduto = new ProdutoCusto();

	@Inject
	private CustoProdutoRepository custoDao;

	@Inject
	private DepartamentoRepository departamentoDao;
	
	@Getter
	@Setter
	boolean tenta1vez = true;
	
	@Setter
	private AbstractDataModel<NfeNaoConfirmada> listaNaoConfirmada ;
	
	@Getter
	@Setter
	private transient List<NfeNaoConfirmada> listaTempNaoConfirmada = new ArrayList<>();

	@Getter
	@Setter
	private String justificativa;
	
	@Inject
	private EstoqueUtil estoqueUtil;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	private DateTimeFormatter formatador = DateTimeFormatter
	.ofLocalizedDateTime(FormatStyle.SHORT)
	.withLocale(new Locale("pt", "br"));
	
	@Getter
	private DateTimeFormatter formataAaaaMmDd = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");
	
	@Override
	public NfeNaoConfirmada setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NfeNaoConfirmada setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@PostConstruct
	public void init(){
		this.nfeRecebimentoModel = getLazyNfe();
		this.listaTamanhos = this.tamanhoDao.listaTamanhoLazy(this.pegaIdEmpresa(),null);
		this.listaCores = this.corDao.listaCoresLazy(this.pegaIdEmpresa(), null);
	}

	@Override
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
		this.nfNaoConfirmada = new NfeNaoConfirmada();
		this.nfe = new NfeRecebida();
		this.item = new ItemNfeRecebida();
		this.listaTempNaoConfirmada = new ArrayList<>();
	}
	
	public void initializeListingCompra() {
		this.viewState = ViewState.LISTING;
		this.nfNaoConfirmada = new NfeNaoConfirmada();
//		this.nfe = new NfeRecebida();
//		this.item = new ItemNfeRecebida();
		this.listaTempNaoConfirmada = this.nfNaoConfirmadaDao.listaDeNaoConfirmadas(pegaIdEmpresa(), pegaIdFilial());
	}

	public AbstractDataModel<NfeNaoConfirmada> getListaNaoConfirmada(){
		this.listaNaoConfirmada = new AbstractDataModel<NfeNaoConfirmada>(this.listaTempNaoConfirmada);
		return this.listaNaoConfirmada;
	}
	

	@Override
	public void initializeForm(Long id) {
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.nfNaoConfirmada = new NfeNaoConfirmada();
			this.nfe = new NfeRecebida();
			this.item = new ItemNfeRecebida();
			this.cor = new Cor();
			this.tamanho = new Tamanho();
			this.ncm = new Ncm();
			this.produto = new Produto();
			this.custoProduto = new ProdutoCusto();
			this.produto.setListaBarras(new ArrayList<>());
		} else {
			this.viewState = ViewState.EDITING;
			this.produto = new Produto();
			//			if (listaTempBarras == null){
			//				this.produto.setListaBarras(new ArrayList<>());
			//				this.setListaBarras(new ArrayList<>());
			//			}else{
			//				this.produto.setListaBarras(listaTempBarras);
			//				this.setListaBarras(listaTempBarras);
			//			}
			this.produto.setListaBarras(new ArrayList<>());
			this.tamanho = new Tamanho();
			this.cor = new Cor();
			this.custoProduto = new ProdutoCusto();
			this.nfNaoConfirmada = this.nfNaoConfirmadaDao.findById(id, false);
			if (this.nfNaoConfirmada != null){
				this.nfe = this.nfeRecebidaDao.pegaNfe(this.nfNaoConfirmada.getChNfe(), this.pegaIdEmpresa());
				this.listaItens = this.nfe.getListaItemNfe();
			}
			if (this.produto.getTipoEstoque() == null){
				this.produto.setTipoEstoque(TipoControleEstoque.BA);
			}


		}
	}



	public AbstractDataModel<ItemNfeRecebida> getListaItemModel(){
		this.listaItemModel = new AbstractDataModel<ItemNfeRecebida>(this.listaItens);
		return listaItemModel;
	}
	/**
	 * Gera a lista de tributos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<NfeNaoConfirmada> getLazyNfe(){
		this.nfeRecebimentoModel = new AbstractLazyModel<NfeNaoConfirmada>() {

			@Override
			public List<NfeNaoConfirmada> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no RecebimentoBean");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<NfeNaoConfirmada> page = nfNaoConfirmadaDao.listByStatusFilial(isDeleted() , null,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(),null, pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return nfeRecebimentoModel;
	}

	/**
	 * redireciona para Cadastramento de nova NFE / edi��o de NFE j� cadastrado
	 * @return pagina de inclusao de NFE
	 */
	public String newRecebimento() {
		return "formCadRecebimento.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListRecebimento() {
		return "formListRecebimento.xhtml?faces-redirect=true";
	}	

	/**
	 * Abre o Dialog RecebimentoNfe
	 */
	public void toRecebimentoNfe(){
		this.updateAndOpenDialog("recebimentoNfe", "dialogRecebimentoNfe");
	}

	/**
	 * Abre o Dialog ConfirmaRecebimento
	 */
	public void toConfirmaRecebimento(){
		this.updateAndOpenDialog("confirmaRecebimento", "dialogConfirmaRecebimento");
	}
	
	/**
	 * Abre o Dialog Opera��o n�o realizada
	 */
	public void toNaoRealizada(){
		if (this.nfNaoConfirmada.getId() == null) {
			this.addInfo(true, "estoque.recebimento.list.nfNaoConfirmada.notSelect");
		}else {
			this.updateAndOpenDialog("operacaoNaoRealizada", "dialogOperacaoNaoRealizada");
		}
	}
	
	/**
	 * Abre o Dialog Importa��o de NFE com Arquivo XML
	 */
	public void toArquivoXML(){
		this.updateAndOpenDialog("importaArquivoXml", "dialogImportaArquivoXml");
	}

	/**
	 * redireciona para a pagina com o ID da NFE de recebimento a ser editado
	 * 
	 * @param recID
	 * 
	 * @returna pagina de edi��o de recebimento de NFE
	 */
	public String changeToEdit(Long recID) {
		return "formCadRecebimento.xhtml?faces-redirect=true&recID=" + recID;
	}

	/**
	 * Metodo para listagem de NFE que permite selecionar a nfe e armazenar em mem�ria
	 * @param event
	 * @throws IOException
	 */
	@Transactional
	public void onRowSelect(SelectEvent event)throws IOException{
		this.nfNaoConfirmada = (NfeNaoConfirmada) event.getObject();
		if (this.nfNaoConfirmada.isConfirmada() && this.nfNaoConfirmada.getNfeRecebida() != null){
			this.nfe = this.nfeRecebidaDao.pegaNfe(this.nfNaoConfirmada.getChNfe(), this.getUsuarioAutenticado().getIdEmpresa());
			this.addInfo(true, "A Nfe " + ((NfeNaoConfirmada)event.getObject()).getChNfe()+ " foi selecionado");
		}else {
			if (!this.nfNaoConfirmada.isConfirmada()) {
				this.addError(true, "estoque.recebimento.notConfirm");
			}else {
				// criando a nfeNaoConfirmada
//				try {
					this.criaNfeAposConfirma(this.nfNaoConfirmada);
//				}catch (Exception e) {
//					this.addError(true, "estoque.recebimento.nfeItens.dontExist", this.nfNaoConfirmada.getChNfe() , "NfeNaoConfirmada", e.getMessage());
//				}
			}
		}
		this.viewState = ViewState.EDITING;

	}


	public void geraTotalAdicional(){
		this.qAdicional = "0";
		for (ItemNfeRecebida itemNfeRecebida : listaItensSelecionados) {
			this.qAdicional = new BigDecimal(this.qAdicional).add(new BigDecimal(itemNfeRecebida.getQCom())).toString();
		}

	}

	public void onRowSelectProduto(SelectEvent event)throws IOException{
		this.produto = (Produto) event.getObject();
		this.produto = this.produtoDao.findById(this.produto.getId(), false); 
		this.produto.setTipoEstoque(TipoControleEstoque.BA);
		this.ncm = this.produto.getNcm();
		this.ref = this.produto.getReferencia();
		setaBarrasEstoque();
		if (isItemnfe){
			isItemnfe = false;
			edicaoItemListaSelecionados();
		}
	}

	public void setaBarrasEstoque(){
		if (this.produto != null){
			this.listaBarrasEstoque = this.barrasDao.pegaListaEstoque(this.produto.getId(), this.pegaIdEmpresa(),this.pegaIdFilial());
		}
		this.ncmEstoque = this.ncmEstoqueDao.pegaNcmComEstoque(this.produto.getNcm(), this.pegaIdEmpresa(),this.pegaIdFilial());
	}

	public void onrowSelectItemNovo(SelectEvent event)throws IOException{
		this.item = (ItemNfeRecebida) event.getObject();
		this.ncmEstoque = new NcmEstoque();
		converteItemNfeXProduto(this.item);
		this.produto.setTipoEstoque(TipoControleEstoque.BA);
		this.ncm = this.ncmDao.localizaNCM(this.item.getNcm(), pegaIdEmpresa());
		this.custoProduto.setCusto(new BigDecimal(this.item.getVUnCom()));
	}

	public String achaTipoMedida(String tipo){
		try{
			String resultado = "";
			resultado = TipoMedida.valueOf(tipo).getSigla();
			return resultado;
		}catch (IllegalArgumentException i){
			return TipoMedida.UN.getSigla();
		}catch (NullPointerException n){
			return TipoMedida.UN.getSigla();
		}
	}

	public void converteItemNfeXProduto(ItemNfeRecebida item){
		Produto novoProduto = new Produto();
		Ncm ncmTemp = new Ncm();
		// procucar se ja existe o codigo no banco de dados
		boolean codigoExiste = this.produtoDao.jaExiste(item.getCProd(), this.pegaIdEmpresa());
		// procurar se ja existe NCM cadastrado no Banco de dados	
		//		boolean ncmExiste = this.ncmDao.jaExiste(item.getNcm(), this.pegaIdEmpresa());
		ncmTemp = this.ncmDao.localizaNCM(item.getNcm(), this.pegaIdEmpresa());
		if (ncmTemp != null){
			//		if (ncmExiste){
			//			ncmTemp = ncmDao.localizaNCM(item.getNcm(), this.pegaIdEmpresa());

			if (!codigoExiste){
				novoProduto.setReferencia(item.getCProd());
				novoProduto.setBarras(item.getEan());
				novoProduto.setDescricao(item.getXProduto());
				novoProduto.setNcm(ncmTemp);
				novoProduto.setRefFornecedor(item.getCProd());
				novoProduto.setTipoMedida(TipoMedida.valueOf(achaTipoMedida(item.getUCom())));

			}else{
				this.addError(true, "recebimento.nfe.new.item.exist", item.getCProd());
			}
		}
		this.produto = novoProduto;
		//		return novoProduto;
	}

	public TipoControleEstoque[] getTipoControleEstoque(){
		return TipoControleEstoque.values();
	}

	public void edicaoItemListaSelecionados(){
		this.viewState = ViewState.ADDING;
		this.cor = new Cor();
		this.tamanho = new Tamanho();
		this.item = new ItemNfeRecebida();
		this.ncm = new Ncm();
		this.produto = new Produto();
		this.custoProduto = new ProdutoCusto();
		this.updateAndOpenDialog("EditaItemListaSelecionadosDialog", "dialogEditaItemListaSelecionados");
	}

	public boolean codigoBarrasJaUtilizado(String barrasTemp){

		boolean resultado = barrasDao.jaExisteParaEmpresaEFilial(barrasTemp.trim(), pegaIdEmpresa(), pegaIdFilial());
		if (this.listaBarrasEstoque != null){
			if (this.listaBarrasEstoque.size()>1){
				if (resultado == false){
					for (BarrasEstoque barrasEstoque : this.listaBarrasEstoque) {
						if (barrasTemp.trim().equalsIgnoreCase(barrasEstoque.getBarras())){
							resultado = true;
						}
					}
				}
			}
		}
		return resultado;
	}

	public void preencheBarrasEstoque(ItemNfeRecebida itemNfeRecebida){
		if (codigoBarrasJaUtilizado(itemNfeRecebida.getEan()) == false){ // novo codigoBarras
			this.barrasEstoque = new BarrasEstoque();
			this.barrasEstoque.setProdutoBase(this.produto);
			this.barrasEstoque.setTotalEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
			this.barrasEstoque.setQuantidadeAcrescentada(new BigDecimal(itemNfeRecebida.getQCom()));
			this.barrasEstoque.setTotalComprado(new BigDecimal(itemNfeRecebida.getQCom()));
			this.barrasEstoque.setBarras(itemNfeRecebida.getEan());
			this.barrasEstoque.setUltimaCompra(LocalDate.now());
			if (this.produto.getTipoEstoque().equals(TipoControleEstoque.MA)){
				this.barrasEstoque.setTamanho(this.tamanhoDao.localizaPorNome(this.tamanho.getTamanho(),pegaIdEmpresa()));	
				this.barrasEstoque.setCor(this.corDao.findById(this.cor.getId(), false));
			}
			if (this.produto.getTipoEstoque().equals(TipoControleEstoque.MO)){
				System.out.println("ibr - " + this.tamanho.getTamanho());
				this.barrasEstoque.setTamanho(this.tamanhoDao.localizaPorNome(this.tamanho.getTamanho(),pegaIdEmpresa()));
			}
			this.barrasEstoque.setTotalUltimaCompra(new BigDecimal(itemNfeRecebida.getQCom()));
			if (this.ncmEstoque != null){
				if (this.ncmEstoque.getEstoque() == null){
					this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
				}else{
					if (this.ncmEstoque.getEstoque().compareTo(new BigDecimal("0"))== 0){
						this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
					}else{
						this.ncmEstoque.setEstoque(this.ncmEstoque.getEstoque().add(new BigDecimal(itemNfeRecebida.getQCom())));
					}
				}
				this.ncmEstoque.setNcm(this.ncm);
			}else{
				this.ncmEstoque = new NcmEstoque();
				this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
				this.ncmEstoque.setNcm(this.ncm);
				//				this.ncmEstoque = this.ncmEstoqueDao.save(this.ncmEstoque);
				this.addInfo(true, "NcmEstoque nulo, criado um novo e colocado o estoque");
			}
		}else{ // atualiza apenas estoque, nao cadastra novo codigo de barras!
			this.barrasEstoque = this.barrasDao.encontraBarrasPorEmpresaEFilialEProduto(itemNfeRecebida.getEan(),this.produto, pegaIdEmpresa(),pegaIdFilial());
			if (this.barrasEstoque == null){
				throw new HibernateException("Codigo de barras: "+itemNfeRecebida.getEan()+" Produto Selecionado : "+this.produto.getReferencia() +" Erro: C�digo de Barras Pertece a outro Produto " );
			}
			if (this.barrasEstoque != null){
				//				if (this.barrasEstoque.getProdutoBase().getId() == this.produto.getId()){
				this.barrasEstoque.setEstoqueAnterior(this.barrasEstoque.getTotalEstoque());
				this.barrasEstoque.setTotalEstoque(this.barrasEstoque.getTotalEstoque().add(new BigDecimal(itemNfeRecebida.getQCom())));
				this.barrasEstoque.setTotalComprado(this.barrasEstoque.getTotalComprado().add(new BigDecimal(itemNfeRecebida.getQCom())));
				this.barrasEstoque.setQuantidadeAcrescentada(new BigDecimal(itemNfeRecebida.getQCom()));
				this.barrasEstoque.setUltimaCompra(LocalDate.now());
				this.barrasEstoque.setTotalUltimaCompra(new BigDecimal(itemNfeRecebida.getQCom()));
				if (this.produto.getTipoEstoque().equals(TipoControleEstoque.MA)){
					this.barrasEstoque.setTamanho(this.tamanho);
					this.barrasEstoque.setCor(this.cor);
				}
				if (this.produto.getTipoEstoque().equals(TipoControleEstoque.MO)){
					this.barrasEstoque.setTamanho(this.tamanho);
				}
				if (this.ncmEstoque != null){
					if (this.ncmEstoque.getEstoque() == null){
						this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
					}else{
						if (this.ncmEstoque.getEstoque().compareTo(new BigDecimal("0"))== 0){
							this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
						}else{
							this.ncmEstoque.setEstoque(this.ncmEstoque.getEstoque().add(new BigDecimal(itemNfeRecebida.getQCom())));
						}
					}
					this.ncmEstoque.setNcm(this.ncm);
				}
				//				}else{
				//					throw new IllegalArgumentException("C�digo de barras pertece a outro produto");
				//				}
			}
		}
	}

	public String verificaEan(ItemNfeRecebida item){
		//		try{
		if (item.getEan() == null){
			return barrasUtil.geradorEan(item.getCProd().trim());
		}else{
			if (barrasUtil.requisitosEan(item.getEan())){
				return item.getEan();
			}else{
				if (barrasUtil.barrasValido(item.getEan()) && barrasUtil.isNumerico(item.getEan()) ){
					return barrasUtil.geradorEan(item.getEan());
				}else{
					return barrasUtil.geradorEan(item.getCProd().trim());
				}
			}
		}
		//		}catch (Exception e){
		//			throw new IllegalArgumentException("Erro codigo EAN");
		//		}
	}
	
	@Transactional
	public void preecheCustoProduto(ItemNfeRecebida itemRecebido) throws EstoqueException {		
		ProdutoCusto custo = new ProdutoCusto();
		BigDecimal valorTotalEstoqueAnterior = new BigDecimal("0",mc);
		BigDecimal custoAntigo = new BigDecimal("0",mc);
		BigDecimal custoAtual = new BigDecimal("0",mc);
		BigDecimal custoMedio = new BigDecimal("0",mc);
		BigDecimal valorTotalRecebidoHoje = new BigDecimal("0",mc);
		BigDecimal quantidadeEstoqueAntigo = new BigDecimal("0",mc);
		BigDecimal quantidadeEstoqueRecebidoHoje = new BigDecimal("0",mc);
		
			// Calculando o valor total do estoque antigo
			custo = this.produto.getListaCustoProduto().get(0);
			
			// preenchendo os campos com os valores recebidos da nota fiscal de entrada
			custoAtual=new BigDecimal(itemRecebido.getVUnCom());
			quantidadeEstoqueAntigo = estoqueUtil.estoqueTotalAnteriorPorProduto(this.produto, pegaIdEmpresa(), pegaIdFilial());
			
			if (custo.getCustoMedio().compareTo(new BigDecimal("0"))>0) {
				custoAntigo=custo.getCustoMedio();
			}else {
				if (custo.getCustoAnterior().compareTo(new BigDecimal("0"))>0) { // garantindo que o custo tenha um valor maior que zero
					custoAntigo = custo.getCustoAnterior();
				}else {
					if (custo.getCusto().compareTo(new BigDecimal("0"))>0) {
						custoAntigo = custo.getCusto();
					} else {
						throw new EstoqueException(translate("estoqueException.custo.zero"));
					}
				}
			}
			valorTotalEstoqueAnterior = custoAntigo.multiply(quantidadeEstoqueAntigo,mc).setScale(2,RoundingMode.HALF_EVEN);
			System.out.println("custo antigo = " + custoAntigo + "vl estoque antigo = " + valorTotalEstoqueAnterior);
			
			// Calculado o valor do estoque recebido hoje
			quantidadeEstoqueRecebidoHoje = new BigDecimal(itemRecebido.getQCom());
			System.out.println("quantidade recebida = " + quantidadeEstoqueRecebidoHoje);
			if (custoAtual.compareTo(new BigDecimal("0"))>0) { // garantindo que o custo tenha um valor maior que zero
				valorTotalRecebidoHoje = custoAtual.multiply(quantidadeEstoqueRecebidoHoje,mc).setScale(2,RoundingMode.HALF_EVEN);
				System.out.println("Valor total Recebido hoje = " + valorTotalRecebidoHoje);
			}else {
				throw new EstoqueException(translate("estoqueException.custo.zero"));
			}
			
			// Calculado o custo médio
			custoMedio = ((valorTotalEstoqueAnterior.add(valorTotalRecebidoHoje,mc).setScale(2,RoundingMode.HALF_EVEN)))
					.divide((quantidadeEstoqueRecebidoHoje.add(quantidadeEstoqueAntigo,mc)),mc).setScale(2,RoundingMode.HALF_EVEN);
			custo.setCustoMedio(custoMedio);
			custo.setCustoAnterior(custoAntigo);
			custo.setCusto(custoAtual);
			this.custoDao.save(custo);
			
	}

	@Transactional
	public void atualizaEstoqueGrupo(){
		try{
			if (this.produto != null){
				setaBarrasEstoque();
				for (ItemNfeRecebida itemNfeRecebida : listaItensSelecionados) {
					if (itemNfeRecebida.isEstoqueAtualizado()){
						this.addError(true, "Produto já Atualizado");
					}else{
						if (this.produto.getTipoEstoque().equals(TipoControleEstoque.BA)){ // tipo controle estoque = B�sico	
							String ean = verificaEan(itemNfeRecebida);
							System.out.println(ean + "erro ean" + itemNfeRecebida.getCProd());
							itemNfeRecebida.setEan(ean);
							preencheBarrasEstoque(itemNfeRecebida);
							preecheCustoProduto(itemNfeRecebida);
						}else{
							this.addError(true, "estoque.recebimento.tipoControle.difBasico",	this.produto.getReferencia());
						}
					}
					itemNfeRecebida.setEstoqueAtualizado(true);
					this.itemDao.save(itemNfeRecebida);
					this.barrasEstoque = this.barrasDao.save(this.barrasEstoque);
					this.ncmEstoque = this.ncmEstoqueDao.save(this.ncmEstoque);
				}
			}else{ // produto nulo
				throw new RuntimeException("Erro produto nao definido!");
			}
		}catch (IllegalArgumentException i){
			this.addError(true, "exception.error.fatal", i.getLocalizedMessage());
		}catch (RuntimeException r){
			this.addError(true, "exception.error.fatal", r.getLocalizedMessage());
		}catch (Exception e){
			System.out.println(e);
			this.addError(true, "exception.error.fatal", e.getCause() + " : " + e.getLocalizedMessage());
		}
	}

	public List<Produto> geraListaProdutoParaEdicao(){
		if (this.produto != null){
			setaBarrasEstoque();
			this.ncmEstoque = this.ncmEstoqueDao.pegaNcmComEstoque(this.produto.getNcm(), this.pegaIdEmpresa(),this.pegaIdFilial());
			for (ItemNfeRecebida itemNfeRecebida : listaItensSelecionados) {
				if (itemNfeRecebida.isEstoqueAtualizado()){
					this.addError(true, "Produto j� Atualizado");
				}else{
					if (this.produto.getTipoEstoque().equals(TipoControleEstoque.MA)){// Tipo controle = MAXIMO
						if (this.barrasEstoque == null){
							this.barrasEstoque = new BarrasEstoque();
							this.barrasEstoque.setProdutoBase(this.produto);
							//				this.barrasEstoque.setCor(cor);
							this.barrasEstoque.setTotalEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
							this.barrasEstoque.setTotalComprado(new BigDecimal(itemNfeRecebida.getQCom()));
							this.barrasEstoque.setUltimaCompra(LocalDate.now());
							this.barrasEstoque.setTotalUltimaCompra(new BigDecimal(itemNfeRecebida.getQCom()));
							if (this.ncmEstoque != null){
								if (this.ncmEstoque.getEstoque() == null){
									this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
								}else{
									if (this.ncmEstoque.getEstoque().compareTo(new BigDecimal("0"))== 0){
										this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
									}else{
										this.ncmEstoque.setEstoque(this.ncmEstoque.getEstoque().add(new BigDecimal(itemNfeRecebida.getQCom())));
									}
								}
								this.ncmEstoque.setNcm(this.ncm);
							}else{
								this.ncmEstoque = new NcmEstoque();
								this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
								this.ncmEstoque.setNcm(this.ncm);
								//								this.ncmEstoque = this.ncmEstoqueDao.save(this.ncmEstoque);
								this.addInfo(true, "NcmEstoque nulo, criado um novo e colocado o estoque");
							}
							if (itemNfeRecebida.getEan() != null){
								if (itemNfeRecebida.getEan().length() > 5 ){
									System.out.println("estou dentro do listaBarrasTemp ");
									if (!this.barrasDao.jaExisteParaEmpresa(itemNfeRecebida.getEan().trim(), this.pegaIdEmpresa())){
										this.barrasEstoque.setBarras(itemNfeRecebida.getEan().trim());
									}else{
										this.addError(true, "Codigo de barras ja existe");
										//										throw new Exception("C�digo de barras ja existe");
									}
								}
							}
						}else{
							this.barrasEstoque.setTotalEstoque(this.barrasEstoque.getTotalEstoque().add(new BigDecimal(itemNfeRecebida.getQCom())));
							this.barrasEstoque.setTotalComprado(this.barrasEstoque.getTotalComprado().add(new BigDecimal(itemNfeRecebida.getQCom())));
							this.barrasEstoque.setTotalUltimaCompra(new BigDecimal(itemNfeRecebida.getQCom()));
							this.barrasEstoque.setUltimaCompra(LocalDate.now());
							if (this.ncmEstoque != null){
								if (this.ncmEstoque.getEstoque() == null){
									this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
								}else{
									if (this.ncmEstoque.getEstoque().compareTo(new BigDecimal("0"))== 0){
										this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
									}else{
										this.ncmEstoque.setEstoque(this.ncmEstoque.getEstoque().add(new BigDecimal(itemNfeRecebida.getQCom())));
									}
								}
								this.ncmEstoque.setNcm(this.ncm);
							}else{
								this.ncmEstoque = new NcmEstoque();
								this.ncmEstoque.setEstoque(new BigDecimal(itemNfeRecebida.getQCom()));
								this.ncmEstoque.setNcm(this.ncm);
								this.addInfo(true, "NcmEstoque nulo, criado um novo e colocado o estoque");
							}
						}
						itemNfeRecebida.setEstoqueAtualizado(true);
						this.itemDao.save(itemNfeRecebida);
						this.barrasEstoque = this.barrasDao.save(this.barrasEstoque);
						this.ncmEstoque = this.ncmEstoqueDao.save(this.ncmEstoque);
					}
					if (this.produto.getTipoEstoque().equals(TipoControleEstoque.MO)){// Tipo controle = MEDIO

					}
				}
			}
		}
		return null;
	}
	public String pegaCnpjUsuarioAutenticado(){
		Filial filTemp = new Filial();
		Empresa empTemp = new Empresa();
		if (this.getUsuarioAutenticado().getIdFilial() != null){
			filTemp = filialDao.findById( this.getUsuarioAutenticado().getIdFilial(), false);
			return CpfCnpjUtils.retiraCaracteresEspeciais(filTemp.getCnpj());
		}else {
			empTemp = empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
			return CpfCnpjUtils.retiraCaracteresEspeciais(empTemp.getCnpj());
		}
	}
	
	public UfCnpjEmitente preencheEmitente(){
		UfCnpjEmitente emit = new UfCnpjEmitente();
		Emitente emissor = new Emitente();
		Endereco end = new Endereco();
		if (this.getUsuarioAutenticado().getIdFilial() == null){
			emissor.setEmpresa(this.empresaDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false));
			end = endDao.findById(emissor.getEmpresa().getEndereco().getEndereco().getId(), false);
			emit.setUf(end.getUf().getIbgeUf().toString());
			emit.setCnpj(CpfCnpjUtils.retiraCaracteresEspeciais(emissor.getEmpresa().getCnpj()));
		}else{
			emissor.setFilial(this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false));
			end = endDao.findById(emissor.getFilial().getEndereco().getEndereco().getId(), false);
			emit.setUf(end.getUf().getIbgeUf().toString());
			emit.setCnpj(CpfCnpjUtils.retiraCaracteresEspeciais(emissor.getFilial().getCnpj()));
		}
		return emit;
	}
	
	
	
	@ToString
	@EqualsAndHashCode
	public static class UfCnpjEmitente{
		@Getter
		@Setter
		private String uf;
		@Getter
		@Setter
		private String cnpj;
	}
	

	@ToString
	@EqualsAndHashCode
	public static class Nsu{
		@Getter
		@Setter
		private String maxNSU;
		
		@Getter
		@Setter
		private String ultNSU;
		
		@Getter
		@Setter
		private String nsuCapturado;
	}
	
	/**
	 * m�todo que retorna uma String com as notas capturados do sefaz atraves da NSU informada
	 * @param nsu
	 * @return
	 * @throws IOException 
	 */
	@Transactional
	public String notasEmitidaCompra(Nsu nsu) throws IOException {
		return acbr.distribuicaoDFe(pegaConexao(), preencheEmitente().getUf() , 
				preencheEmitente().getCnpj(), new BigDecimal(nsu.getUltNSU()).toString());
	}
	
	/**
	 * Metodo que retorna a NFE fazendo a consulta atraves da NSU
	 * @param nsu
	 * @return Stringresposta ACBR 
	 * @throws IOException
	 */
	public String retornaNFEPorNSU(String nsu) throws IOException{
		return acbr.distribuicaoDFePorNSU(pegaConexao(), preencheEmitente().getUf() , 
				preencheEmitente().getCnpj(), nsu); 
	}
	
	/**
	 * m�todo que retorna Nsu preenchida apos consulta ao banco de dados.
	 * @return Nsu
	 */
	@Transactional
	public Nsu pegaNsuEmitente() {
		Nsu nsu = new Nsu();
		if (this.getUsuarioAutenticado().getIdFilial() == null) {
			this.empresa = empresaDao.findById(getUsuarioAutenticado().getIdEmpresa(), false);
			nsu.setMaxNSU(this.empresa.getMaxNSU());
			if (this.empresa.getUltNSU() == null|| this.empresa.getUltNSU() =="") {
				nsu.setUltNSU("0");
			}else {
				nsu.setUltNSU(this.empresa.getUltNSU());
			}
			nsu.setNsuCapturado(this.empresa.getNsuCapturado());
		}else {
			this.filial = filialDao.findById(getUsuarioAutenticado().getIdFilial(), false);
			nsu.setMaxNSU(this.filial.getMaxNSU());
			if (this.filial.getUltNSU() == null || this.filial.getUltNSU() =="") {
				nsu.setUltNSU("0");
			}else {
				nsu.setUltNSU(this.filial.getUltNSU());
			}
			nsu.setNsuCapturado(this.filial.getNsuCapturado());
		}
		return nsu;
	}
	
	/**
	 * metodo que pega aqruivo da maquina com nome nsu.txt 
	 * @return
	 * @throws IOException 
	 */
	@Transactional
	public String leArquivoMaquina() throws IOException {
		String loadFile = "ACBr.LoadFromFile(\"C:\\ibrcomp\\import\\"+"nsu.txtt"+")";
		String arquivo =acbr.enviaComandoACBr(pegaConexao(), loadFile);

		return arquivo;
	}
	
	@Transactional
	public void preencheListaNfeNaoConfirmada() throws RecebimentoNFeException  {
		try {
			this.listaTempNaoConfirmada = listaNotasEmitidasCompra();
		} catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	public void exibeLeituraNFEentrada() throws IOException {
		this.respostaAcbrLocal = limiteResposta(leArquivoMaquina());
		System.out.println(this.respostaAcbrLocal);
	}
	
	public String  limiteResposta(String respostaAcbr) {
		String regex = "[DistribuicaoDFe]";
		
		boolean encontrado = localiza.localizaPalavra(respostaAcbr, regex);
		if (encontrado) {
		
			int ini =respostaAcbr.indexOf("[DistribuicaoDFe]");
			int fim = respostaAcbr.indexOf("ultNSU")+23;
			String respEvento = respostaAcbr.substring(ini,fim);
			return respEvento;
		
//			Scanner in = new Scanner(respEvento);
//			while (in.hasNextLine()) {
//				String line = in.nextLine();
//				System.out.println("achaNSU " + line);
//				if (localiza.localizaPalavra(line, "maxNSU=")) {
//					nsu.setMaxNSU(line.substring(7, line.length()).trim());
//					System.out.println("achaNsu Max " + nsu.getMaxNSU() );
//				}
//				if (localiza.localizaPalavra(line, "ultNSU=")) {
//					nsu.setUltNSU(line.substring(7, line.length()).trim());
//					System.out.println("achaNsu ultNSU " + nsu.getUltNSU() );
//				}
//			}
//			in.close();
		}else {
			return  "vazio";
		}
	}
	/**
	 * método que recupera da sefaz a NFE atraves da NSU da nota já confirmada
	 * @throws IOException 
	 */
	@Transactional
	public void criaNfeAposConfirma(NfeNaoConfirmada nfeNaoConfirmada) throws IOException {
//		try {
//			String resposta = this.retornaNFEPorNSU(nfeNaoConfirmada.getNsu()).toUpperCase();
			String resposta = acbr.distribuicaoDFePorChaveNfe(pegaConexao(), nfeNaoConfirmada.getUfOrigem().getIbgeUf().toString(), pegaCnpjUsuarioAutenticado(), this.nfNaoConfirmada.getChNfe().trim());
			this.salvaNfeEntrada(nfeNaoConfirmada,resposta);
			
//		}catch (HibernateException h) {
//			this.addError(true, "hibernate.persist.fail", h.getMessage());
//		}catch (Exception e) {
//			this.addError(true, "exception.error.fatal", e.getMessage());
//		}
	}
	
	/**
	 * m�todo que recupera da sefaz uma lista com as notas de compra da Matriz/filial a ser importado para o sistema 
	 * e seta no cadastro da empresa o retorno da ultNSU capturada.
	 * @return List<NfeNaoConfirmada>
	 */
	@Transactional
	public List<NfeNaoConfirmada> listaNotasEmitidasCompra(){
		try {
			List<NfeNaoConfirmada> listaTempNfeNaoConfirmada = new ArrayList<>();
			Nsu nsu = pegaNsuEmitente();
			String resultado = notasEmitidaCompra(nsu).toUpperCase();
			// atualizando ultNSU
			Nsu nsuEmp = achaNSUComTexto(resultado);
			atualizaNsu(nsuEmp);
			
			// persistindo no banco de dados a ultNSU
			listaTempNfeNaoConfirmada =  converteTxtEmNfeNaoConfirmada(resultado);
			saveListNfeNaoConfirmada(listaTempNfeNaoConfirmada);
			return listaTempNfeNaoConfirmada;
		} catch (RecebimentoNFeException e) {
			// TODO Auto-generated catch block
			this.addError(true, "estoque.recebimento.error", e.getMessage());
			return new ArrayList<NfeNaoConfirmada>();
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return new ArrayList<NfeNaoConfirmada>();
		}
	}
	
	@Transactional
	private void saveListNfeNaoConfirmada(List<NfeNaoConfirmada> lista) {
		try {
			int i = 0;
			for (NfeNaoConfirmada nf : lista) {
				if (!this.nfNaoConfirmadaDao.nfeNaoConfirmadaSalva(nf.getChNfe(), getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial())){
					nf.setManifesto(ManifestacaoDestinatario.Nma);
					this.nfNaoConfirmadaDao.save(nf);
					i++;
				}
			}
			this.addInfo(true, "estoque.recebimento.list.nfNaoConfirmada.save", i);
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	@Transactional
	public void atualizaNsu(Nsu nsu) {
		try {
			if (this.getUsuarioAutenticado().getIdFilial() == null){
				this.empresa = empresaDao.findById(getUsuarioAutenticado().getIdEmpresa(), false);
				this.empresa.setMaxNSU(nsu.getMaxNSU());
				this.empresa.setUltNSU(nsu.getUltNSU());
				this.empresaDao.save(this.empresa);
			}else{
				this.filial = filialDao.findById(getUsuarioAutenticado().getIdFilial(), false);
				this.filial.setMaxNSU(nsu.getMaxNSU());
				this.filial.setUltNSU(nsu.getUltNSU());
				this.filialDao.save(this.filial);
			}
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	@Transactional
	public Nsu achaNSUComTexto(String respostaAcbr) {
		Nsu nsu = new Nsu();
		String regex = "[DISTRIBUICAODFE]";
		
		boolean encontrado = localiza.localizaPalavra(respostaAcbr, regex);
		if (encontrado) {
		
			int ini =respostaAcbr.indexOf("[DISTRIBUICAODFE]");
			int fim = respostaAcbr.indexOf("ULTNSU")+23;
			String respEvento = respostaAcbr.substring(ini,fim);
		
			Scanner in = new Scanner(respEvento);
			while (in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println("achaNSU " + line);
				if (localiza.localizaPalavra(line, "MAXNSU=")) {
					nsu.setMaxNSU(line.substring(7, line.length()).trim());
					System.out.println("achaNsu Max " + nsu.getMaxNSU() );
				}
				if (localiza.localizaPalavra(line, "ULTNSU=")) {
					nsu.setUltNSU(line.substring(7, line.length()).trim());
					System.out.println("achaNsu ultNSU " + nsu.getUltNSU() );
				}
			}
			in.close();
		}else {
			nsu = null;
		}
		return nsu;
	}
	/**
	 * M�todo utilizado apenas na implanta��o do sistema para criar um inicio para o sistema 
	 * @return Nsu
	 */
	@Transactional
	public void achaNSU() {
		try {
			String resp = acbr.achaNSU(pegaConexao(), preencheEmitente().getUf() , preencheEmitente().getCnpj(), "000000000000000");
			Nsu nsu = new Nsu();
			nsu = achaNSUComTexto(resp);
			if (nsu != null) {
				atualizaNsu(nsu);
			}
			this.addInfo(true, "nsu.implantacao.sucess", nsu.ultNSU);
		}catch (HibernateException h){
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	@Transactional
	public void confirmaOperacao() throws IOException {
		try {
			if (this.nfNaoConfirmada.getId() == null) {
				this.addInfo(true, "estoque.recebimento.list.nfNaoConfirmada.notSelect");
			}else {
				this.respostaAcbrLocal = confirmaNfeNaoConfirmada(this.nfNaoConfirmada);
			}
		} catch (RecebimentoNFeException e) {
			// TODO Auto-generated catch block
			this.addError(true, "caixa.error", e.getMessage());
		}
	}
	
	@Transactional
	public void desconheceOperacao() throws IOException {
		if (this.nfNaoConfirmada.getId() == null) {
			this.addInfo(true, "estoque.recebimento.list.nfNaoConfirmada.notSelect");
		}else {
			this.respostaAcbrLocal = desconheceNfeNaoConfirmada(this.nfNaoConfirmada);
		}
	}
	
	@Transactional
	public void operacaoNaoRealizada() throws IOException {
		this.respostaAcbrLocal = operacaoNaoRealizadaDaNfe(this.nfNaoConfirmada);
	}
	
	@Transactional
	public String operacaoNaoRealizadaDaNfe(NfeNaoConfirmada nfe) throws IOException{
		String resp = acbr.operacaoNaoRealizada(pegaConexao(), Uf.AN.getIbgeUf().toString(), pegaCnpjUsuarioAutenticado(), 
				nfe.getChNfe().trim(),getUsuarioAutenticado().getName()+ nfe.getChNfe().substring(34),this.justificativa);
		int ini =resp.indexOf("[Evento001]");
		int fim = 0;
		String respEvento = resp.substring(ini);
		boolean ok =	localiza.localizaPalavra(respEvento, "CStat=135");
		if (ok){
			nfe.setConfirmada(true);
			nfe.setManifesto(ManifestacaoDestinatario.Onr);
			this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(nfe);
		}else{
			ok = localiza.localizaPalavra(respEvento, "CStat=573"); 
			if (ok){
				nfe.setConfirmada(true);
				nfe.setManifesto(ManifestacaoDestinatario.Onr);
				this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(nfe);
			}else{
				throw new IllegalArgumentException(this.translate("estoque.recebimento.confirmaOperacao"));
			}
		}
		return resp;
	}
	
	@Transactional
	public String desconheceNfeNaoConfirmada(NfeNaoConfirmada nfe) throws IOException{
		String resp = acbr.desconheceOperacao(pegaConexao(), Uf.AN.getIbgeUf().toString(), pegaCnpjUsuarioAutenticado(), 
				nfe.getChNfe().trim(),getUsuarioAutenticado().getName()+ nfe.getChNfe().substring(34));
		int ini =resp.indexOf("[Evento001]");
		int fim = 0;
		String respEvento = resp.substring(ini);
		boolean ok =	localiza.localizaPalavra(respEvento, "CStat=135");
		if (ok){
			nfe.setConfirmada(true);
			nfe.setManifesto(ManifestacaoDestinatario.Dop);
			this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(nfe);
		}else{
			ok = localiza.localizaPalavra(respEvento, "CStat=573"); 
			if (ok){
				nfe.setConfirmada(true);
				nfe.setManifesto(ManifestacaoDestinatario.Dop);
				this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(nfe);
			}else{
				throw new IllegalArgumentException(this.translate("estoque.recebimento.confirmaOperacao"));
			}
		}
		return resp;
	}
	
	@Transactional
	public String confirmaNfeNaoConfirmada(NfeNaoConfirmada nfe) throws RecebimentoNFeException, IOException{
		String resp = acbr.confirmaOperacao(pegaConexao(), Uf.AN.getIbgeUf().toString(), pegaCnpjUsuarioAutenticado(), 
				nfe.getChNfe().trim(),getUsuarioAutenticado().getName()+ nfe.getChNfe().substring(34));
		int ini =resp.indexOf("[Evento001]");
		int fim = 0;
		String respEvento = resp.substring(ini);
		boolean ok =	localiza.localizaPalavra(respEvento, "CStat=135");
		if (ok){
			nfe.setConfirmada(true);
			nfe.setManifesto(ManifestacaoDestinatario.Cop);
			this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(nfe);
		}else{
			ok = localiza.localizaPalavra(respEvento, "CStat=573"); 
			if (ok){
				nfe.setConfirmada(true);
				nfe.setManifesto(ManifestacaoDestinatario.Cop);
				this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(nfe);
			}else{
				ok = localiza.localizaPalavra(respEvento, "CStat=136"); 
				if (ok) {
					nfe.setConfirmada(true);
					nfe.setManifesto(ManifestacaoDestinatario.Cop);
					this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(nfe);
					throw new RecebimentoNFeException(this.translate("recebimentoException.confirmaOperacao.136"));
				}else {
					throw new IllegalArgumentException(this.translate("estoque.recebimento.confirmaOperacao"));
				}
			}
		}
		return resp;
	}

	/**
	 * Metodo que salva a nfe e itens da nfe no sistema
	 * @param nfeNao
	 * @param respAcbr
	 * @throws IOException 
	 */
	@Transactional
	public NfeRecebida salvaNfeEntrada(NfeNaoConfirmada nfeNao, String respAcbr) throws IOException{
//		try{
			String xml="";
			String arquivo = "";
			String resultado = "";
			System.out.println("exibindo resp ...." + respAcbr.length());
			System.out.println("exibindo resp ...." + respAcbr);
			boolean achei = localiza.localizaPalavra(respAcbr,"ResDFe001");
			System.out.println("achou? " + achei);
			if (achei){
				int iniN = respAcbr.indexOf("ResDFe001");
				System.out.println("inicio do arquivo: " + iniN);
				boolean acheiNFeXml = false;
				resultado = respAcbr.substring(iniN,respAcbr.length());
				System.out.println("exibindo resultado" + resultado );
				System.out.println("exibindo resultado tamanho: " + resultado.length() );
				
				Scanner in = new Scanner(resultado);
				while (in.hasNextLine()) {
				    String line = in.nextLine();
				    System.out.println("ibrahim " + line);
				    if (localiza.localizaPalavra(line, "XML=")) {
				    	 xml = (line.substring(4, line.length()).trim());
				    }
				    if (localiza.localizaPalavra(line, "arquivo=")) {
				    	arquivo = line.substring(8,line.length()).trim();
				    }
				}
				in.close();
				acheiNFeXml = localiza.localizaPalavra(arquivo,"-nfe.xml");
				if (acheiNFeXml){

					if (!acbr.arquvioExiste(pegaConexao(),nfeNao.getChNfe().trim())){
						this.respostaAcbrLocal = acbr.criaArqXMLNfeCompras(pegaConexao(), xml, nfeNao.getChNfe().trim());
						achei = localiza.localizaPalavra(this.respostaAcbrLocal, "OK");
						if (achei){
							this.addInfo(true, "estoque.recebimento.geraXml.sucess");
						}else{
							this.addError(true, "estoque.recebimento.geraXml.fail");
						}
					}
					NfeRecebida nfTemp = converteXmlNfe(xml);
					// Salva a Nota no sistema
					if (this.nfe != null){
						this.nfe = this.nfeRecebidaDao.save(nfTemp);
						if (this.nfe.getId() != null){
							this.addInfo(true, "NFE tem ID  estou na linha 914 - RecebimentoBean");
						}
						List<ItemNfeRecebida> listaTemp = new ArrayList<>();
						listaTemp = nfTemp.getListaItemNfe();
						for (ItemNfeRecebida itemNfe : listaTemp) {
							itemNfe.setNfeRecebida(this.nfe);
							itemNfe = this.itemDao.save(itemNfe);
						}
						if (listaTemp != null){
							this.nfe.setListaItemNfe(listaTemp);
						}
						if (nfeNao.getNfeRecebida() == null) {
							nfeNao.setNfeRecebida(this.nfe);
							this.nfNaoConfirmadaDao.save(nfeNao);
						}
						if (nfTemp.getListaParcelas().size() > 0) {
							for (ParcelasNfe parc : nfTemp.getListaParcelas()) {
								parc.setNfeRecebida(this.nfe);
								this.parcelasNfeDao.save(parc);
							}
						}
					}
				}else{
					throw new IllegalArgumentException(this.translate("estoque.recebimento.xmlNaoDisponivel") +" " + nfeNao.getChNfe().trim());
				}

				//				System.out.println("antes do comando acbr distribuicaoDFe");
			}else{
				throw new IllegalArgumentException(this.translate("estoque.recebimento.nfeNaoExiste"));
			}
			return this.nfe;
//		}catch (HibernateException h){
//			throw new HibernateException(this.translate("hibernate.persist.fail"));
//		}
	}

	public void dialogNfeDetalhes(){
		this.updateAndOpenDialog("nfeRecebidaDetalhes", "dialogNfeRecebidaDetalhes");
	}

	@Transactional
	public void geraXML(){
		dialogNfeDetalhes();
	}

	public void telaPesquisaProduto(){
		this.produtoModel = getLazyProduto();
		this.updateAndOpenDialog("PesquisaProdutoDialog", "dialogPesquisaProduto");		
	}

	public void telaPesquisaProdutoItemLista(ItemNfeRecebida ite){
		isItemnfe = true;
		this.item = ite;
		this.produtoModel = getLazyProduto();
		this.updateAndOpenDialog("PesquisaProdutoDialog", "dialogPesquisaProduto");	

	}

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
	
	@Transactional
	public void  consultaNfe(String chave) throws Throwable{
			manifestaNFe(chave,Uf.AN);
			
	}
	
	public void testeLeitura() throws IOException {

		String xml = "ACBr.LoadFromFile(\"C:\\ibrcomp\\NFeEntrada\\"+"teste.txtt"+")";
		String arquivo =acbr.enviaComandoACBr(pegaConexao(), xml);

		List<NfeNaoConfirmada> listaTempNfeNaoConfirmada = new ArrayList<>();
		NfeNaoConfirmada nfeTemp = new NfeNaoConfirmada();
		//		Nsu nsu = pegaNsuEmitente();
		//		String resultado = notasEmitidaCompra(nsu);
		String resultado = arquivo;
		int ini = 0;
		int fim = 0;
		int fimPrimeiro = 0;
		String resNFeFim = "";
		// inserindo fim de bloco no resultado
		String resultadoEmBloco = "";
		Scanner ent = new Scanner(resultado);
		while (ent.hasNextLine()) {
			String line = ent.nextLine();
			if (line.trim().isEmpty()) {
				resultadoEmBloco = resultadoEmBloco + "%%\n";
			}else {
				resultadoEmBloco = resultadoEmBloco + line + "\n";
			}
			if (ent.hasNextLine() == false) {
				resultadoEmBloco = resultadoEmBloco + "%%\n";
			}
		}
		ent.close();
		System.out.println("Resultado em bloco \n" + resultadoEmBloco);

		int vezes = localiza.count(resultadoEmBloco, "ResNFe");
		System.out.println("encontrou a expressao ResNFe " + vezes + "vezes");
		List<String> listaString = new ArrayList<String>();
		HashMap<BigDecimal, BigDecimal> hashIndice = localiza.listaIndice(resultadoEmBloco, "\\bResNFe");

		for (int i = 0 ; i < hashIndice.size(); i++) {
			System.out.println("lista i = "+ i +"posicao: " + i+ " indice: " + hashIndice.get(new BigDecimal(i)) + "tamanho do Hash = " + hashIndice.size());
			ini = i;
			if (i == hashIndice.size()-1) {
				// resolver ultimo grupo de string

				resNFeFim = resultadoEmBloco.substring(hashIndice.get(new BigDecimal(ini)).intValue());

			}else {
				fim = i+1;
				listaString.add(resultadoEmBloco.substring(hashIndice.get(new BigDecimal(ini)).intValue(), hashIndice.get(new BigDecimal(fim)).intValue()));
			}
			System.out.println("ini " + ini +" fim " + fim);
		}
		HashMap<BigDecimal, BigDecimal> hashFimArquivo = localiza.listaIndice(resNFeFim, "%%");
		for (int i = 0 ; i < hashFimArquivo.size(); i++) {
			System.out.println("lista i = "+ i +"posicao: " + i+ " indice: " + hashFimArquivo.get(new BigDecimal(i)) + "tamanho do Hash = " + hashFimArquivo.size());
		}
		String fimDeArquivo = resNFeFim.substring(0, hashFimArquivo.get(new BigDecimal(0)).intValue());
		listaString.add(fimDeArquivo);

		System.out.println("Tamanho lista String = " + listaString.size());
		for (String  resNFe : listaString) {
			System.out.println("ibrahim contador " + fimPrimeiro++ +"resNFE: " +resNFe);
			
			Scanner in = new Scanner(resNFe.toUpperCase());
			while (in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println("achaNSU " + line);
				if (localiza.localizaPalavra(line, "CNPJCPF=")) {
					nfeTemp.setCnpjCpf(line.substring(8, line.length()).trim());
					System.out.println("listaNotasEmitidasCompra " + nfeTemp.getCnpjCpf() );
				}
				if (localiza.localizaPalavra(line, "IE=")) {
					nfeTemp.setIe(line.substring(3, line.length()).trim());
				}
				if (localiza.localizaPalavra(line, "CHNFE=")) {
					nfeTemp.setChNfe(line.substring(6, line.length()).trim());
				}
				if (localiza.localizaPalavra(line, "DHEMI=")) {
					int tam = line.substring(6, line.length()).length();
					if (tam >= 18 ) {
//						nfeTemp.setDhEmissao(LocalDate.parse(line.substring(6, line.length()-8).trim(), formatador)) ;
						nfeTemp.setDhEmissao(line.substring(6, line.length()-8).trim());
					}else {
//						nfeTemp.setDhEmissao(LocalDate.parse(line.substring(6, line.length()).trim(), formatador)) ;
						nfeTemp.setDhEmissao(line.substring(6, line.length()).trim());
					}
				}
				if (localiza.localizaPalavra(line, "NPROT=")) {
					nfeTemp.setNProtocolo(line.substring(6, line.length()).trim());
				}
				if (localiza.localizaPalavra(line, "XNOME=")) {
					nfeTemp.setXNome(line.substring(6, line.length()).trim());
				}
				if (localiza.localizaPalavra(line, "VNF=")) {
					nfeTemp.setValorNf(line.substring(4, line.length()).trim());
				}
				if (localiza.localizaPalavra(line, "NSU=")) {
					nfeTemp.setNsu(line.substring(4, line.length()).trim());
				}
				if (localiza.localizaPalavra(line, "CSITNFE=")) {
					String res = line.substring(8,line.length()).trim();
					if (new BigDecimal(res).compareTo(new BigDecimal("1"))==0) {
						nfeTemp.setSituacao(SitNfe.Aut);
					}else if (new BigDecimal(res).compareTo(new BigDecimal("2"))==0) {
						nfeTemp.setSituacao(SitNfe.Den);
					}else {
						nfeTemp.setSituacao(SitNfe.Can);
					}
				}
			}
			in.close();		
			System.out.println("chave: " + nfeTemp.getChNfe());
			nfeTemp.setUfOrigem(retornaUfPorIbge(nfeTemp.getChNfe().substring(0, 2)));
			listaTempNfeNaoConfirmada.add(nfeTemp);
			nfeTemp = new NfeNaoConfirmada();

			int i = 0;
			for (NfeNaoConfirmada nfeNaoConfirmada : listaTempNfeNaoConfirmada) {
				System.out.println("item: " + i++);
				System.out.println(nfeNaoConfirmada.toString());
			}

		}
	}

	public List<NfeNaoConfirmada> converteTxtEmNfeNaoConfirmada(String txt) throws RecebimentoNFeException {
		List<NfeNaoConfirmada> listaTempNfeNaoConfirmada = new ArrayList<>();
		NfeNaoConfirmada nfeTemp = new NfeNaoConfirmada();
//		Nsu nsu = pegaNsuEmitente();
//		String resultado = notasEmitidaCompra(nsu);
		String resultado = txt;
		int ini = 0;
		int fim = 0;
		int fimPrimeiro = 0;
		String resNFeFim = "";
		// inserindo fim de bloco no resultado
		String resultadoEmBloco = "";
		Scanner ent = new Scanner(resultado);
		while (ent.hasNextLine()) {
			String line = ent.nextLine();
			if (line.trim().isEmpty()) {
				resultadoEmBloco = resultadoEmBloco + "%%\n";
			}else {
				resultadoEmBloco = resultadoEmBloco + line + "\n";
			}
			if (ent.hasNextLine() == false) {
				resultadoEmBloco = resultadoEmBloco + "%%\n";
			}
		}
		ent.close();
		System.out.println("Resultado em bloco \n" + resultadoEmBloco);

//		int vezes = localiza.count(resultadoEmBloco, "RESNFE");
		int	vezes = localiza.count(resultadoEmBloco, "RESDFE");
		System.out.println("encontrou a expressao ResDFe " + vezes + "vezes");
		if ( vezes !=0) {
			List<String> listaString = new ArrayList<String>();
			HashMap<BigDecimal, BigDecimal> hashIndice = new HashMap<>();
//			if (vezes >0) {
//				 hashIndice = localiza.listaIndice(resultadoEmBloco, "\\bRESNFE");
//			}else {
				 hashIndice = localiza.listaIndice(resultadoEmBloco, "\\bRESDFE");
//			}

			for (int i = 0 ; i < hashIndice.size(); i++) {
				System.out.println("lista i = "+ i +"posicao: " + i+ " indice: " + hashIndice.get(new BigDecimal(i)) + "tamanho do Hash = " + hashIndice.size());
				ini = i;
				if (i == hashIndice.size()-1) {
					// resolver ultimo grupo de string

					resNFeFim = resultadoEmBloco.substring(hashIndice.get(new BigDecimal(ini)).intValue());

				}else {
					fim = i+1;
					listaString.add(resultadoEmBloco.substring(hashIndice.get(new BigDecimal(ini)).intValue(), hashIndice.get(new BigDecimal(fim)).intValue()));
				}
				System.out.println("ini " + ini +" fim " + fim);
			}
			HashMap<BigDecimal, BigDecimal> hashFimArquivo = localiza.listaIndice(resNFeFim, "%%");
			for (int i = 0 ; i < hashFimArquivo.size(); i++) {
				System.out.println("lista i = "+ i +"posicao: " + i+ " indice: " + hashFimArquivo.get(new BigDecimal(i)) + "tamanho do Hash = " + hashFimArquivo.size());
			}
			String fimDeArquivo = resNFeFim.substring(0, hashFimArquivo.get(new BigDecimal(0)).intValue());
			listaString.add(fimDeArquivo);

			System.out.println("Tamanho lista String = " + listaString.size());
			for (String  resNFe : listaString) {
				System.out.println("ibrahim contador " + fimPrimeiro++ +"resNFE: " +resNFe);

				Scanner in = new Scanner(resNFe.toUpperCase());
				while (in.hasNextLine()) {
					String line = in.nextLine();
					System.out.println("achaNSU " + line);
					if (localiza.localizaPalavra(line, "CNPJCPF=")) {
						nfeTemp.setCnpjCpf(line.substring(8, line.length()).trim());
						System.out.println("listaNotasEmitidasCompra " + nfeTemp.getCnpjCpf() );
					}
					if (localiza.localizaPalavra(line, "IE=")) {
						nfeTemp.setIe(line.substring(3, line.length()).trim());
					}
					if (localiza.localizaPalavra(line, "CHDFE=")) {
						nfeTemp.setChNfe(line.substring(6, line.length()).trim());
					}
					if (localiza.localizaPalavra(line, "DHEMI=")) {
						int tam = line.substring(6, line.length()).length();
						if (tam >= 18 ) {
//							nfeTemp.setDhEmissao(LocalDate.parse(line.substring(6, line.length()-8).trim(), formatador)) ;
							nfeTemp.setDhEmissao(line.substring(6, line.length()-8).trim());
						}else {
//							nfeTemp.setDhEmissao(LocalDate.parse(line.substring(6, line.length()).trim(), formatador)) ;
							nfeTemp.setDhEmissao(line.substring(6, line.length()).trim());
						}
					}
					if (localiza.localizaPalavra(line, "NPROT=")) {
						nfeTemp.setNProtocolo(line.substring(6, line.length()).trim());
					}
					if (localiza.localizaPalavra(line, "XNOME=")) {
						nfeTemp.setXNome(line.substring(6, line.length()).trim());
					}
					if (localiza.localizaPalavra(line, "VNF=")) {
						nfeTemp.setValorNf(line.substring(4, line.length()).trim());
					}
					if (localiza.localizaPalavra(line, "NSU=")) {
						nfeTemp.setNsu(line.substring(4, line.length()).trim());
					}
					if (localiza.localizaPalavra(line, "CSITNFE=")) {
						String res = line.substring(8,line.length()).trim();
						if (new BigDecimal(res).compareTo(new BigDecimal("1"))==0) {
							nfeTemp.setSituacao(SitNfe.Aut);
						}else if (new BigDecimal(res).compareTo(new BigDecimal("2"))==0) {
							nfeTemp.setSituacao(SitNfe.Den);
						}else {
							nfeTemp.setSituacao(SitNfe.Can);
						}
					}
				}
				in.close();		
				System.out.println("chave: " + nfeTemp.getChNfe());
				nfeTemp.setUfOrigem(retornaUfPorIbge(nfeTemp.getChNfe().substring(0, 2)));
				listaTempNfeNaoConfirmada.add(nfeTemp);
				nfeTemp = new NfeNaoConfirmada();

				int i = 0;
				for (NfeNaoConfirmada nfeNaoConfirmada : listaTempNfeNaoConfirmada) {
					System.out.println("item: " + i++);
					System.out.println(nfeNaoConfirmada.toString());
				}
			}
			return listaTempNfeNaoConfirmada;
		}else {
			throw new RecebimentoNFeException(this.translate("estoque.recebimento.error.webservice"));
		}
	}
	
//	List<NfeNaoConfirmada> listaTempNfeNaoConfirmada = new ArrayList<>();
//	NfeNaoConfirmada nfeTemp = new NfeNaoConfirmada();
////	Nsu nsu = pegaNsuEmitente();
////	String resultado = notasEmitidaCompra(nsu);
//	String resultado = txt;
//	String regex = "";
//	String regexFim= "";
//	int contador = 001;
//	int contaFim = 001;
//	int ini = 0;
//	int fim = 0;
//	int fimPrimeiro=0;
//	
//	
//	boolean encontrado = localiza.localizaPalavra(resultado, "ResNFe001");
//	int vezes = localiza.count(resultado, "ResNFe");
//	System.out.println("encontrou a expressao ResNFe " + vezes + "vezes");
//	if (encontrado) {
//		System.out.println("Estou dentro do encontrador ResNFe001");
//		for(contador = 001 ;contador <= vezes; contador++ ) {
//			contaFim = contador + 1;
//			if (contador >9 && contador <100 ){
//				regex = "[ResNFe"+"0"+contador+"]";
//			}else if (contador <= 9){
//				regex = "[ResNFe"+"00"+contador+"]";
//			}else{
//				regex= "[ResNFe"+contador+"]";
//			}
//			if (contaFim >9 && contaFim <100 ){
//				regexFim = "[ResNFe"+"0"+contaFim+"]";
//			}else if (contaFim <= 9){
//				regexFim = "[ResNFe"+"00"+contaFim+"]";
//			}else{
//				regexFim= "[ResNFe"+contaFim+"]";
//			}
//			if (localiza.localizaPalavra(resultado,"\\" +regex)) {
//				ini =resultado.indexOf(regex);
//			}
//			System.out.println("Regex ResNFe = " + regex);
////			ini =resultado.indexOf(regex);
//			System.out.println("regexFim  = " + regexFim);
////			System.out.println("achou? "+localiza.localizaPalavra(resultado, "ProEve001"));
//			if (localiza.localizaPalavra(resultado,"\\"+regexFim)) {
//				fim = resultado.indexOf(regexFim);
//			}else {
//				fim = resultado.toUpperCase().indexOf("XNOME=");
//			}
////			if (contaFim <= vezes) {
////				fim = resultado.indexOf(regexFim);
////			}else {
////				boolean t1 = localiza.localizaPalavra(resultado, "ProEve001");
////				boolean t2 = localiza.localizaPalavra(resultado, "ResEve001");
////				if (t1 && t2) {
////					fimPrimeiro = resultado.indexOf("[ProEve001]");
////					fim = resultado.indexOf("[ResEve001]");
////					if (fim > fimPrimeiro) {
////						fim = fimPrimeiro;
////					}
////				}else if(t1) {
////					fim = resultado.indexOf("[ProEve001]");
////				}else if (t2) {
////					fim = resultado.indexOf("[ResEve001]");
////				} else {
////					if (t1 == false && t2 == false) {
////						fim = resultado.length();
////					}
////				}
////			}
//			System.out.println("ini " + ini +" fim " + fim);
//			String respEvento = resultado.substring(ini,fim);
//			Scanner in = new Scanner(respEvento.toUpperCase());
//			while (in.hasNextLine()) {
//				String line = in.nextLine();
//				System.out.println("achaNSU " + line);
//				if (localiza.localizaPalavra(line, "CNPJCPF=")) {
//			    	nfeTemp.setCnpjCpf(line.substring(8, line.length()).trim());
//			    	System.out.println("listaNotasEmitidasCompra " + nfeTemp.getCnpjCpf() );
//			    }
//			    if (localiza.localizaPalavra(line, "IE=")) {
//			    	nfeTemp.setIe(line.substring(3, line.length()).trim());
//			    }
//			    if (localiza.localizaPalavra(line, "CHNFE=")) {
//			    	nfeTemp.setChNfe(line.substring(6, line.length()).trim());
//			    }
//			    if (localiza.localizaPalavra(line, "DHEMI=")) {
//			    	int tam = line.substring(6, line.length()).length();
//			    	if (tam >= 18 ) {
//			    		nfeTemp.setDhEmissao(line.substring(6, line.length()-8).trim());
//			    	}else {
//			    		nfeTemp.setDhEmissao(line.substring(6, line.length()).trim());
//			    	}
//			    }
//			    if (localiza.localizaPalavra(line, "NPROT=")) {
//			    	nfeTemp.setNProtocolo(line.substring(6, line.length()).trim());
//			    }
//			    if (localiza.localizaPalavra(line, "XNOME=")) {
//			    	nfeTemp.setXNome(line.substring(6, line.length()).trim());
//			    }
//			    if (localiza.localizaPalavra(line, "VNF=")) {
//			    	nfeTemp.setValorNf(line.substring(4, line.length()).trim());
//			    }
//			    if (localiza.localizaPalavra(line, "NSU=")) {
//			    	nfeTemp.setNsu(line.substring(4, line.length()).trim());
//			    }
//			    if (localiza.localizaPalavra(line, "CSITNFE=")) {
//			    	String res = line.substring(8,line.length()).trim();
//			    	if (new BigDecimal(res).compareTo(new BigDecimal("1"))==0) {
//			    		nfeTemp.setSituacao(SitNfe.Aut);
//			    	}else if (new BigDecimal(res).compareTo(new BigDecimal("2"))==0) {
//			    		nfeTemp.setSituacao(SitNfe.Den);
//			    	}else {
//			    		nfeTemp.setSituacao(SitNfe.Can);
//			    	}
//			    }
//			}
//			in.close();		
//			System.out.println("chave: " + nfeTemp.getChNfe());
//			nfeTemp.setUfOrigem(retornaUfPorIbge(nfeTemp.getChNfe().substring(0, 2)));
//			listaTempNfeNaoConfirmada.add(nfeTemp);
//			nfeTemp = new NfeNaoConfirmada();
//		}
//	}
	
	public Uf retornaUfPorIbge(String cod) {
		Uf ufTemp = Uf.AC;
		for (Uf uf : getListaUf()) {
			if (uf.getIbgeUf().compareTo(new BigDecimal(cod))==0) {
				ufTemp = uf;
			}
		}
		return ufTemp;
	}
	
	public Uf[] getListaUf(){
		return Uf.values();
	}
	
	public void leRespostaAcbrResumido(String resposta) throws RecebimentoNFeException  {
			
			this.nfNaoConfirmada = converteTxtEmNfeNaoConfirmada(resposta).get(0);
			this.nfNaoConfirmada.setUfOrigem(this.codUf);
			this.nfNaoConfirmada.setConfirmada(false);
	}

	@Transactional
	public void confirmandoNfe(){
		try{
			// verificando no banco de dados se a chave de acesso ja existe na base de dados
			boolean nfeJaConfirmada = this.nfNaoConfirmadaDao.nfeNaoConfirmadaEncontrada(chaveAcesso, this.getUsuarioAutenticado().getIdEmpresa(), this.getUsuarioAutenticado().getIdFilial());
			if (!nfeJaConfirmada){

			}
		}catch (Exception e){

		}
	}
	/**
	 * Metodo que persiste a nfe n�o confirmada na base de dados
	 * @param respostaAcbr
	 */
	@Transactional
	public void salvaNfeNaoConfrimada(String respostaAcbr) {
		try {
			leRespostaAcbrResumido(respostaAcbr);				
				
		} catch (HibernateException e) {
			throw new HibernateException("Erro ao persistir no banco de dados" + e.getCause());
		} catch (Exception ex) {
			this.addError(true,"Erro na convers�o dos dados" + ex.getCause());
		}
	}
	
	
	@Transactional	
	public void manifestaNFe(String chave, Uf uf ) throws Throwable {
		try {
			this.respostaAcbrLocal = acbr.confirmaOperacao(pegaConexao(),uf.getIbgeUf().toString(),pegaCnpjUsuarioAutenticado(), chave, 
					getUsuarioAutenticado().getName()+chave.substring(34)).toUpperCase();
			String regex = "[EVENTO001]";
			boolean encontrado = localiza.localizaPalavra(this.respostaAcbrLocal, regex);
			if (encontrado) {
				int inicioArquivo = this.respostaAcbrLocal.indexOf("[EVENTO001]");
				String resposta = this.respostaAcbrLocal.substring(inicioArquivo);
				if (localiza.localizaPalavra(resposta, "CSTAT=573")){ // duplicidade de evento
					System.out.println("nfNaoconfirmada = " + this.nfNaoConfirmada.isConfirmada() );
					if (! this.nfNaoConfirmada.isConfirmada()) {
						this.nfNaoConfirmada.setConfirmada(true);
						this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(this.nfNaoConfirmada);
					}
					this.addWarning(true, resposta.substring(resposta.lastIndexOf("XMOTIVO"), resposta.lastIndexOf("ARQUIVO=")));
				}else {
					if (localiza.localizaPalavra(resposta, "CSTAT=657")){ //informado estado autorizador incorreto
						this.addError(true, resposta.substring(resposta.lastIndexOf("XMOTIVO"), resposta.lastIndexOf("ARQUIVO=")));
						if (tenta1vez) {
							tenta1vez = false;
							consultaNfe(chave); // consulta no ambiente nacional
						}
					}else {
						if (localiza.localizaPalavra(resposta, "CSTAT=135")){ //Manifesta��o conclu�da com sucesso
							
							this.nfNaoConfirmada.setConfirmada(true);
							this.nfNaoConfirmada = this.nfNaoConfirmadaDao.save(this.nfNaoConfirmada);
							this.addInfo(true, resposta.substring(resposta.lastIndexOf("XMOTIVO"), resposta.lastIndexOf("ARQUIVO=")));
						}else { // erro desconhecido
							throw new Exception("Erro desconhecido, ACBR: " +  resposta.substring(resposta.lastIndexOf("XMOTIVO"), resposta.lastIndexOf("ARQUIVO="))); 
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw new Exception("Erro de Confirma��o de Ci�ncia de opera��o junto ao SEFAZ - " + e.getMessage() + " - " + e.getCause());
		}
	}
	
	@Transactional
	public void importaNFE() {
		
		try {
			
			if (this.nfNaoConfirmada.getNfeRecebida() == null && this.nfNaoConfirmada.isConfirmada()) {
				if (this.nfNaoConfirmada.getUfOrigem() == null) {
					this.nfNaoConfirmada.setUfOrigem(retornaUfPorIbge(this.nfNaoConfirmada.getChNfe().substring(0,2)));
				}
			this.nfe = salvaNfeEntrada(this.nfNaoConfirmada,  acbr.distribuicaoDFePorChaveNfe(pegaConexao(), this.nfNaoConfirmada.getUfOrigem().getIbgeUf().toString(), pegaCnpjUsuarioAutenticado(), this.nfNaoConfirmada.getChNfe().trim()));
			this.addInfo(true, "estoque.recebimento.import.sucess",this.nfNaoConfirmada.getChNfe());
			}else {
//				if (!this.nfNaoConfirmada.isConfirmada()) {
//			
//					if (tenta1vez ) {
//						tenta1vez = false;
//						manifestaNFe(this.nfNaoConfirmada.getChNfe(),Uf.AN);
//						importaNFE();
//					
//					}
//				}else {
					this.addWarning(true, "estoque.recebimento.notConfirm", this.nfNaoConfirmada.getChNfe());
//				}
			}
		}catch (IllegalArgumentException ia){
			this.addError(true, ia.getLocalizedMessage(), ia.getLocalizedMessage());
		}catch (RuntimeException r){
			this.addError(true, r.getLocalizedMessage(), chaveAcesso.trim());
		}catch (Exception e ){
			this.addError(true, "exception.error.fatal", e.getCause() + " : " + e.getLocalizedMessage());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.getCause();
		}
	}
	
	
	@Transactional
	public void testaRecebimentoDfe(){
		try {
			// 1� verificar no banco se esta nota est� lan�ada
			this.nfNaoConfirmada = new NfeNaoConfirmada();
			boolean nfeJaConfirmada = this.nfNaoConfirmadaDao.nfeNaoConfirmadaEncontrada(this.chaveAcesso, this.getUsuarioAutenticado().getIdEmpresa(), this.getUsuarioAutenticado().getIdFilial());
			if (!nfeJaConfirmada) { // if com condi��o se ela N�O consta na base de dados
				// 2� Pesquiso a Danfe para importar o cabe�alho da nfe e criar no banco um Danfe n�o confirmada
				this.respostaAcbrLocal = this.acbr.distribuicaoDFePorChaveNfe(pegaConexao(), this.codUf.getIbgeUf().toString(), pegaCnpjUsuarioAutenticado(), this.chaveAcesso.trim()).toUpperCase();
				salvaNfeNaoConfrimada(this.respostaAcbrLocal);
				System.out.println("Gerado Danfe n�o confirmada na base");
				this.addInfo(false, "Gerado danfe n�o confirmada na base");
				// 3� confirmar opera��o para permitir o download do xml e criar a nota no sistema
				manifestaNFe(this.chaveAcesso,this.nfNaoConfirmada.getUfOrigem());
				// 4� Consulta Danfe CONFIRMADA para baixar XML e Importar os dados para o sistema
				if (this.nfNaoConfirmada.isConfirmada()) {
					//		tentado com uf do emitente			
					this.nfe = salvaNfeEntrada(this.nfNaoConfirmada, this.acbr.distribuicaoDFePorChaveNfe(pegaConexao(), this.nfNaoConfirmada.getUfOrigem().getIbgeUf().toString(), pegaCnpjUsuarioAutenticado(), this.nfNaoConfirmada.getChNfe().trim()));
					this.addInfo(true, "Manifesta��o de Confirma��o de opera��o e Importa��o de NFe concluida com sucesso! ");
				}
			
			}else {
				this.addWarning(true, "estoque.recebimento.import.exist", chaveAcesso);
			}
		}catch (IllegalArgumentException ia){
			this.addError(true, ia.getLocalizedMessage(), ia.getLocalizedMessage());
		}catch (RuntimeException r){
			this.addError(true, r.getLocalizedMessage() + "{0}", this.chaveAcesso.trim());
		}catch (Exception e ){
			this.addError(true, "exception.error.fatal", e.getCause() + " : " + e.getLocalizedMessage());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.getCause();
		}
	}
	
	


	public String toAtualizaEstoque(Long id){
		return "formAtualizaEstoque.xhtml?faces-redirect=true&estoqueID=" + id;
	}
	
	/**
	 * Metodo que salva a nfe e itens da nfe no sistema
	 * @param nfeNao
	 * @param respAcbr
	 * @throws IOException 
	 */
	@Transactional
	public void salvaNfeEntradaPorXML() throws IOException{
		try{
			String xml = "ACBr.LoadFromFile(\"C:\\ibrcomp\\NFeEntrada\\"+this.chaveAcesso+"-nfe.xmll"+")";
			String arquivo =acbr.enviaComandoACBr(pegaConexao(), xml);
			NfeNaoConfirmada nfeNao = new NfeNaoConfirmada();
			nfeNao = this.nfNaoConfirmadaDao.pegaNfePorChaveDeAcesso(this.chaveAcesso.trim(), pegaIdEmpresa(), pegaIdFilial());
			NfeRecebida nfTemp = converteXmlNfe(arquivo);
			// Salva a Nota no sistema
			if (nfeNao != null){
				if (nfeNao.getNfeRecebida() == null) {
					this.nfe = this.nfeRecebidaDao.save(nfTemp);
				}
				if (this.nfe.getId() != null){
					this.addInfo(true, "NFE tem ID  estou na linha 914 - RecebimentoBean");
				}
				List<ItemNfeRecebida> listaTemp = new ArrayList<>();
				listaTemp = nfTemp.getListaItemNfe();
				for (ItemNfeRecebida itemNfe : listaTemp) {
					itemNfe.setNfeRecebida(this.nfe);
					itemNfe = this.itemDao.save(itemNfe);
				}
				if (listaTemp != null){
					this.nfe.setListaItemNfe(listaTemp);
				}
				if (nfTemp.getListaParcelas().size() > 0) {
					for (ParcelasNfe parc : nfTemp.getListaParcelas()) {
						parc.setNfeRecebida(this.nfe);
						this.parcelasNfeDao.save(parc);
					}
				}
				if (nfeNao.getNfeRecebida() == null) {
					nfeNao.setNfeRecebida(this.nfe);
					this.nfNaoConfirmadaDao.save(nfeNao);
				}

			}else{
				nfeNao = new NfeNaoConfirmada();
				nfeNao.setChNfe(nfTemp.getChaveAcesso());
				nfeNao.setCnpjCpf(nfTemp.getEmitente().getFornecedor().getCnpj());
				nfeNao.setXNome(nfTemp.getEmitente().getFornecedor().getRazaoSocial());
				nfeNao.setIe(nfTemp.getEmitente().getFornecedor().getInscEstadual());
				nfeNao.setNumero(new BigDecimal(nfTemp.getNumeroNota()).toString());
				nfeNao.setDhEmissao(nfTemp.getDataEmissao().toString());
//				nfeNao.setDhEmissao(nfTemp.getDataEmissao().toLocalDate());
//				nfeNao.setDhEmissao(nfTemp.getDataEmissao().toLocalDate());
				nfeNao.setValorNf(nfTemp.getValorTotalNota().toString());
				nfeNao.setNProtocolo(nfTemp.getProtocoloAutorizacao());
				nfeNao.setUfOrigem(nfTemp.getEmitente().getFornecedor().getEndereco().getEndereco().getUf());
				nfeNao.setDataRecebimento(LocalDateTime.now());
				nfeNao.setManifesto(ManifestacaoDestinatario.Nma);
				nfeNao.setConfirmada(true);
				
				this.nfe = this.nfeRecebidaDao.save(nfTemp);
				if (this.nfe.getId() != null){
					this.addInfo(true, "NFE tem ID  estou na linha 914 - RecebimentoBean");
				}
				List<ItemNfeRecebida> listaTemp = new ArrayList<>();
				listaTemp = nfTemp.getListaItemNfe();
				for (ItemNfeRecebida itemNfe : listaTemp) {
					itemNfe.setNfeRecebida(this.nfe);
					itemNfe = this.itemDao.save(itemNfe);
				}
				if (nfTemp.getListaParcelas().size() > 0) {
					for (ParcelasNfe parc : nfTemp.getListaParcelas()) {
						parc.setNfeRecebida(this.nfe);
						this.parcelasNfeDao.save(parc);
					}
				}
				if (listaTemp != null){
					this.nfe.setListaItemNfe(listaTemp);
				}
				
				nfeNao.setNfeRecebida(this.nfe);
				this.nfNaoConfirmadaDao.save(nfeNao);
				
//					throw new IllegalArgumentException(this.translate("estoque.recebimento.nfeNaoExiste" + nfeNao.getChNfe().trim()));
			}

				//				System.out.println("antes do comando acbr distribuicaoDFe");
//			}else{
//				throw new IllegalArgumentException(this.translate("estoque.recebimento.nfeNaoExiste"));
//			}
		}catch (HibernateException h){
			throw new HibernateException(this.translate("hibernate.persist.fail"));
		}
	}

	/**
	 * 	Funcao que converte o XML de uma NFE para uma Nfe do sistema apenas para Recebimentos de produtos
	 * @param xml
	 * @return Nfe
	 * @throws IOException 
	 */
	@Transactional
	public NfeRecebida converteXmlNfe(String xml) throws IOException{
		//		try{
		xml = xml.toUpperCase();
		System.out.println("IBR - " + xml);
		String cnpj="";
		String uf="";
		NfeRecebida nfeTemp = new NfeRecebida();
		ItemNfeRecebida itemNfeTemp = new ItemNfeRecebida();
		List<ItemNfeRecebida> listaItemNfeTemp = new ArrayList<>();
		List<ParcelasNfe> listaParcelas = new ArrayList<>();

		// dividindo o XML em grupos 

		// ide
		int inicio = xml.indexOf("<IDE>");
		int fim = xml.indexOf("</IDE>");
		String ide = xml.substring(inicio+5, fim);

		// emit
		inicio = xml.indexOf("<EMIT>");
		fim = xml.indexOf("</EMIT>");
		String emit = xml.substring(inicio+6, fim);

		// dest
		inicio = xml.indexOf("<DEST>");
		fim = xml.indexOf("</DEST>");
		String dest = xml.substring(inicio+6, fim);

		// transp
		inicio = xml.indexOf("<TRANSP>");
		fim = xml.indexOf("</TRANSP>");
		String trasnp = xml.substring(inicio+8, fim);

		// pagamento
		inicio = xml.indexOf("<PAG>");
		fim = xml.indexOf("</PAG>");
		String pagamento = xml.substring(inicio+5, fim);

		// blocoItem  - bloco com todos os itens da nfe
		inicio = xml.indexOf("<DET NITEM=");
		fim = xml.indexOf("<TOTAL>");
		String blocoItem = xml.substring(inicio, fim);
		System.out.println(blocoItem);

		// calculando a quantidade de itens na nfe - resultado: totalItem
		int totalItem = 0;
		//		int contadorItem = 1;
		String itemPesquisa= "(<DET NITEM=)";
		totalItem = localiza.count(blocoItem, itemPesquisa);
		System.out.println("total de itens : " + totalItem);


		// definindo os dados b�sicos da nfe

		//setando numero da nfe
		inicio = ide.indexOf("<NNF>");
		fim = ide.indexOf("</NNF>");
		nfeTemp.setNumeroNota(new BigDecimal(ide.substring(inicio+5, fim)).longValue());
		// setando horario de emissao
		inicio = ide.indexOf("<DHEMI>");
		fim = ide.indexOf("</DHEMI>");
		System.out.println("dia da nfe: "+ ide.substring(inicio+15,inicio+17));
		System.out.println("mes da nfe: "+ ide.substring(inicio+12,inicio+14));
		System.out.println("ano da nfe: "+ ide.substring(inicio+7,inicio+11));
		LocalDate data = LocalDate.of(new BigDecimal(ide.substring(inicio+7,inicio+11)).intValue(),new BigDecimal(ide.substring(inicio+12,inicio+14)).intValue(), new BigDecimal(ide.substring(inicio+15,inicio+17)).intValue());
		LocalTime time = LocalTime.of(new BigDecimal(ide.substring(inicio+18,inicio+19)).intValue(), new BigDecimal(ide.substring(inicio+21,inicio+22)).intValue(), new BigDecimal(ide.substring(inicio+24,inicio+25)).intValue());
		LocalDateTime dhEmi = LocalDateTime.of(data,time);
		nfeTemp.setDataEmissao(dhEmi);

		// definindo o emitente
		inicio = emit.indexOf("<CNPJ>");
		fim = emit.indexOf("</CNPJ>");
		cnpj = emit.substring(inicio+6, fim);
		this.fornecedor = this.fornecedorDao.localizaPorCnpj(CpfCnpjUtils.adcionaCaracteresEspeciais(cnpj), this.getUsuarioAutenticado().getIdEmpresa());
		if (this.fornecedor != null){
			Emitente emitente = new Emitente();
			emitente.setFornecedor(this.fornecedor);
			nfeTemp.setEmitente(emitente);
		}else{
			this.addError(true, "recebimento.nfe.emitente.notExists", cnpj);
			this.addInfo(true, "recebimento.nfe.emitente.info.notExists");
		}

		// definindo destinatario
		Destinatario destino = new Destinatario();
		inicio = dest.indexOf("<CNPJ>");
		fim = dest.indexOf("</CNPJ>");
		cnpj = dest.substring(inicio+6, fim);
		this.empresa = this.empresaDao.localizaPorCnpj(CpfCnpjUtils.adcionaCaracteresEspeciais(cnpj), null);
		if (this.empresa == null){
			this.filial = this.filialDao.localizaPorCnpj(CpfCnpjUtils.adcionaCaracteresEspeciais(cnpj), this.getUsuarioAutenticado().getIdFilial());
			if (this.filial != null){
				destino.setFilial(this.filial);
				nfeTemp.setDestino(destino);
			} else {
				this.addError(true, "recebimento.nfe.destino.notExists", cnpj);
				this.addInfo(true, "recebimento.nfe.destino.info.notExists", cnpj );
			}
		}else{
			destino.setEmpresa(this.empresa);
			nfeTemp.setDestino(destino);
		}



		// definindo itens da nfe
		int indiceItem = 1;
		for (int i = 0 ; i < totalItem; i++){
			itemNfeTemp = new ItemNfeRecebida();
			String blocoNitem ="";
			System.out.printf("<det nItem=\"%s\">",indiceItem);
			inicio = blocoItem.indexOf("<DET NITEM=\""+indiceItem+"\">");
			String blocoTemp = blocoItem.substring(inicio);
			inicio= blocoTemp.indexOf("<DET NITEM=\""+indiceItem+"\">");
			fim = blocoTemp.indexOf("</DET>");
			String nItem = blocoTemp.substring(inicio , fim);

			inicio = nItem.indexOf("<PROD>");
			fim = nItem.indexOf("</PROD>");
			blocoNitem = nItem.substring(inicio+6, fim);
			System.out.println("BlocoNitem : " + blocoNitem);

			inicio = blocoNitem.indexOf("<CPROD>");
			fim = blocoNitem.indexOf("</CPROD>");
			itemNfeTemp.setCProd(blocoNitem.substring(inicio+7,fim));
			inicio = blocoNitem.indexOf("<NCM>");
			fim = blocoNitem.indexOf("</NCM>");
			itemNfeTemp.setNcm(blocoNitem.substring(inicio+5,fim));
			inicio = blocoNitem.indexOf("<XPROD>");
			fim = blocoNitem.indexOf("</XPROD>");
			itemNfeTemp.setXProduto(blocoNitem.substring(inicio+7, fim));
			inicio = blocoNitem.indexOf("<UCOM>");
			fim = blocoNitem.indexOf("</UCOM>");
			itemNfeTemp.setUCom(blocoNitem.substring(inicio+6, fim).toUpperCase());

			inicio = blocoNitem.indexOf("<CFOP>");
			fim = blocoNitem.indexOf("</CFOP>");
			itemNfeTemp.setCfop(blocoNitem.substring(inicio+6, fim));

			inicio = blocoNitem.indexOf("<VUNCOM>");
			fim = blocoNitem.indexOf("</VUNCOM>");
			itemNfeTemp.setVUnCom(blocoNitem.substring(inicio+8, fim));


			boolean acheiEan = localiza.localizaPalavra(blocoNitem, "<CEAN>");
			if (acheiEan){
				inicio = blocoNitem.indexOf("<CEAN>");
				fim = blocoNitem.indexOf("</CEAN>");
				itemNfeTemp.setEan(blocoNitem.substring(inicio+6,fim));

				inicio = blocoNitem.indexOf("<QCOM>");
				fim = blocoNitem.indexOf("</QCOM>");
				itemNfeTemp.setQCom(blocoNitem.substring(inicio+6,fim));


			}else{
				inicio = blocoNitem.indexOf("<QCOM>");
				fim = blocoNitem.indexOf("</QCOM>");
				itemNfeTemp.setQCom(blocoNitem.substring(inicio+6,fim));


			}
			// inserindo impostos
			// icms
			inicio = nItem.indexOf("<ICMS>");
			fim = nItem.indexOf("</ICMS>");
			String blocoIcms = nItem.substring(inicio+6, fim);
			// rotina para validar cst 60
			boolean cst60 = localiza.localizaPalavra(blocoIcms, "<CST>");
			if (cst60 == false) {
				boolean csosn = localiza.localizaPalavra(blocoIcms, "<CSOSN>");
				if (csosn == true) {
					inicio = blocoIcms.indexOf("<CSOSN>"); //continuar
					fim = blocoIcms.indexOf("</CSOSN>");
					itemNfeTemp.setCst(blocoIcms.substring(inicio+7, fim));
					if (itemNfeTemp.getCst().equalsIgnoreCase("500")){
						inicio = blocoIcms.indexOf("<VBCSTRET>");
						fim = blocoIcms.indexOf("</VBCSTRET>");
						itemNfeTemp.setBaseICMSSt(new BigDecimal(blocoIcms.substring(inicio+10, fim)));
					}
				}
			}else {
				inicio = blocoIcms.indexOf("<CST>");
				fim = blocoIcms.indexOf("</CST>");
				itemNfeTemp.setCst(blocoIcms.substring(inicio+5, fim));

				if (itemNfeTemp.getCst().equalsIgnoreCase("60")){
					inicio = blocoIcms.indexOf("<VBCSTRET>");
					fim = blocoIcms.indexOf("</VBCSTRET>");
					itemNfeTemp.setBaseICMSSt(new BigDecimal(blocoIcms.substring(inicio+10, fim)));

				}else{

					inicio = blocoIcms.indexOf("<PICMS>");
					fim = blocoIcms.indexOf("</PICMS>");
					itemNfeTemp.setAliqIcms(new BigDecimal(blocoIcms.substring(inicio+7, fim)));
					inicio = blocoIcms.indexOf("<MODBC>");
					fim = blocoIcms.indexOf("</MODBC>");
					itemNfeTemp.setModBC(blocoIcms.substring(inicio+6, fim));
					itemPesquisa= "<PICMSST>";
					boolean acheiIcmsSt = localiza.localizaPalavra(blocoNitem, itemPesquisa);
					if (acheiIcmsSt){
						inicio = blocoIcms.indexOf("<PICMSST>");
						fim = blocoIcms.indexOf("</PICMSST>");
						itemNfeTemp.setAliqIcmsSt(new BigDecimal(blocoIcms.substring(inicio+9, fim)));
						inicio = blocoIcms.indexOf("<VBCST>");
						fim = blocoIcms.indexOf("</VBCST>");
						itemNfeTemp.setBaseICMSSt(new BigDecimal(blocoIcms.substring(inicio+7, fim)));
					}
					inicio = blocoIcms.indexOf("<VBC>");
					fim = blocoIcms.indexOf("</VBC>");
					itemNfeTemp.setBaseICMS(new BigDecimal(blocoIcms.substring(inicio+5, fim)));
				}

			}
			inicio = blocoIcms.indexOf("<ORIG>");
			fim = blocoIcms.indexOf("</ORIG>");
			itemNfeTemp.setOrigem(new BigDecimal(blocoIcms.substring(inicio+6, fim)).intValue());
			
			// ipi
			itemPesquisa= "<IPI>";
			boolean acheiIpi = localiza.localizaPalavra(blocoNitem, itemPesquisa);
			if (acheiIpi){
				inicio = nItem.indexOf("<IPI>");
				fim = nItem.indexOf("</IPI>");
				String blocoIpi = nItem.substring(inicio+5,fim);

				inicio = blocoIpi.indexOf("<CST>");
				fim = blocoIpi.indexOf("</CST>");
				itemNfeTemp.setCstIpi(blocoIpi.substring(inicio+5, fim));
				inicio = blocoIpi.indexOf("<CENQ>");
				fim = blocoIpi.indexOf("</CENQ>");
				itemNfeTemp.setCEnq(blocoIpi.substring(inicio+6,fim));
				boolean pIpi = localiza.localizaPalavra(blocoIpi, "<PIPI>");
				if (pIpi) {
					inicio = blocoIpi.indexOf("<PIPI>");
					fim = blocoIpi.indexOf("</PIPI>");
					itemNfeTemp.setAliqIPI(new BigDecimal(blocoIpi.substring(inicio+6,fim)));
					inicio = blocoIpi.indexOf("<VIPI>");
					fim = blocoIpi.indexOf("</VIPI>");
					itemNfeTemp.setValorIPI(new BigDecimal(blocoIpi.substring(inicio+6,fim)));
				}
			}
			// pis
			itemPesquisa= "<PIS>";
			boolean acheiPis = localiza.localizaPalavra(blocoNitem, itemPesquisa);
			if (acheiPis){
				inicio = nItem.indexOf("<PIS>");
				fim = nItem.indexOf("</PIS>");
				String blocoPis = nItem.substring(inicio+5, fim);

				inicio = blocoPis.indexOf("<CST>");
				fim = blocoPis.indexOf("</CST>");
				itemNfeTemp.setCstPis(new BigDecimal(blocoPis.substring(inicio+5, fim)).intValue());
				inicio = blocoPis.indexOf("<PPIS>");
				fim = blocoPis.indexOf("</PPIS>");
				itemNfeTemp.setAliqPis(new BigDecimal(blocoPis.substring(inicio+6, fim)));
				inicio = blocoPis.indexOf("<VPIS>");
				fim = blocoPis.indexOf("</VPIS>");
				itemNfeTemp.setValorPis(new BigDecimal(blocoPis.substring(inicio+6, fim)));
			}

			// cofins
			itemPesquisa= "<COFINS>";
			boolean acheiCofins = localiza.localizaPalavra(blocoNitem, itemPesquisa);
			if (acheiCofins){
				inicio = nItem.indexOf("<COFINS>");
				fim = nItem.indexOf("</COFINS>");
				String blocoCofins = nItem.substring(inicio+8, fim);

				inicio = blocoCofins.indexOf("<PCOFINS>");
				fim = blocoCofins.indexOf("</PCOFINS>");
				itemNfeTemp.setAliqCofins(new BigDecimal(blocoCofins.substring(inicio+9, fim)));
				inicio = blocoCofins.indexOf("<VCOFINS>");
				fim = blocoCofins.indexOf("</VCOFINS>");
				itemNfeTemp.setValorCofins(new BigDecimal(blocoCofins.substring(inicio+9, fim)));
				inicio = blocoCofins.indexOf("<CST>");
				fim = blocoCofins.indexOf("</CST>");
				itemNfeTemp.setCstCofins(new BigDecimal(blocoCofins.substring(inicio+5, fim)).intValue());
			}
			//			itemNfeTemp.setCfopItem(cfopTemp);
			//			itemNfeTemp.setProduto(produtoTemp);
			listaItemNfeTemp.add(itemNfeTemp);
			System.out.println("Lista produto : "+ itemNfeTemp.getXProduto());
			indiceItem++;
			//			System.out.println("Produto: " + produtoTemp.getDescricao());

		}
		nfeTemp.setListaItemNfe(listaItemNfeTemp);
		// definindo transportadora da nfe
		boolean transp = localiza.localizaPalavra(trasnp, "<CNPJ>");
		if (transp) {
			inicio = trasnp.indexOf("<CNPJ>");
			fim = trasnp.indexOf("</CNPJ>");
			cnpj =  trasnp.substring(inicio+6,fim);
			inicio = trasnp.indexOf("<UF>");
			fim = trasnp.indexOf("</UF>");
			uf = trasnp.substring(inicio+4,fim);


			this.transportadora = this.transportadoraDao.localizaPorCnpj(cnpj, this.getUsuarioAutenticado().getIdEmpresa());
			if (this.transportadora != null){
				this.transporte.setTransportadora(this.transportadora);

			}else{
				Transportadora transporta = new Transportadora();
				Endereco ende = new Endereco();
				EndComplemento comple = new EndComplemento();
				this.receita = this.pesquisaReceita.retornoConsultaSintegra(this.pesquisaReceita.consultaCadSintegra(pegaConexao(), cnpj, UfSigla.valueOf(uf)));
				System.out.println("IBR - TransportadoraConsulta: "+this.receita);
				if (this.receita != null){
					if (!this.transportadoraDao.procuraCnpj(CpfCnpjUtils.adcionaCaracteresEspeciais(this.receita.getReceitaCNPJ()), this.getUsuarioAutenticado().getIdEmpresa())){
						transporta.setCnpj(CpfCnpjUtils.adcionaCaracteresEspeciais(this.receita.getReceitaCNPJ()));
						transporta.setTemCnpj(true);
						transporta.setRazaoSocial(this.receita.getReceitaRazao());
						transporta.setInscEstadual(this.receita.getReceitaIE());
						transporta = this.transportadoraDao.save(transporta);
						//					transporta.setEnquadramento(Enquadramento.valueOf(this.receita.getReceitaRegime()));
						ende = this.endDao.listCep(this.receita.getReceitaCep());
						System.out.println(ende.getId());
						ende = this.endDao.procuraCepBase(this.receita.getReceitaCep());
						comple.setNumero(this.receita.getReceitaNumero());
						comple.setComplemento(this.receita.getReceitaComplemento());
						comple.setLogradouro(this.receita.getReceitaLogradouro());
						comple.setBairro(this.receita.getReceitaBairro());
						comple.setEndereco(ende);
						comple.setTransportadora(transporta);
						comple = this.endComplementoDao.save(comple);
						transporta.setEndereco(comple);
						transporta = this.transportadoraDao.save(transporta);
						this.transporte.setTransportadora(transporta);
					}else{
						this.transporte.setTransportadora(this.transportadoraDao.localizaPorCnpj(this.receita.getReceitaCNPJ(), this.getUsuarioAutenticado().getIdEmpresa()));
					}
				}else{
					this.addError(true, "recebimento.nfe.transporte.notExists", cnpj);
					this.addInfo(true, "recebimento.nfe.transporte.info.notExists", cnpj);
				}
			}
		}

		// Cobran�a **Duplicata Mercantil
		String indice= "<COBR>";
		boolean acheiCobr = localiza.localizaPalavra(xml, indice);
		int totalDup = 0;
		String blocoCob = "";
		if (acheiCobr){
			inicio = xml.indexOf("<COBR>");
			fim = xml.indexOf("</COBR>");
			blocoCob = xml.substring(inicio+6, fim);

			indice= "<DUP>";
			totalDup = localiza.count(blocoCob, indice);
			System.out.println("Numero de duplicas � : " + totalDup );
		}
		//definindo pagamento
		inicio = pagamento.indexOf("<TPAG>");
		fim = pagamento.indexOf("</TPAG>");
		String tPag = pagamento.substring(inicio+6, fim);
		System.out.println("IBR - Tipo Pagamento: " + tPag);
		for (TipoPagamento tipo : TipoPagamento.values()) {
			if (tipo.getCod().equals(tPag)){
				this.tipoPagamento = tipo;
			}
		}
		//		this.tipoPagamento = this.tipoPagamento.achaPagamentoPorCodigo(tPag);
		ParcelasNfe parcela = new ParcelasNfe();
		String vPag = "";
		switch (this.tipoPagamento){
		case Bol: //Boleto
			if (totalDup > 0 ) {
				List<String> listaDuplicatas = new ArrayList<>();
				listaDuplicatas = localiza.listaString(xml,"<DUP>","</DUP>");
				for (String  str : listaDuplicatas) {
					System.out.println("lista : " + str);
				}
				for (String  dp : listaDuplicatas) {
					
					inicio = dp.indexOf("<NDUP>");
					fim = dp.indexOf("</NDUP>");
					String numDup = dp.substring(inicio+6, fim);
					
					inicio = dp.indexOf("<DVENC>");
					fim = dp.indexOf("</DVENC>");
					String venci = dp.substring(inicio+7, fim);
					
					inicio = dp.indexOf("<VDUP>");
					fim = dp.indexOf("</VDUP>");
					String vlDup = dp.substring(inicio+6, fim);
					
					
					parcela = new ParcelasNfe();
					parcela.setNumParcela(new BigDecimal(numDup).longValue());
					parcela.setControle(nfeTemp.getNumeroNota());
					parcela.setValorParcela(new BigDecimal(vlDup));
					parcela.setValorOriginal(new BigDecimal(vlDup));
					parcela.setVencimento(LocalDate.parse(venci,formataAaaaMmDd));
					parcela.setTipoPagamento(this.tipoPagamento);
					parcela.setTipoLancamento(TipoLancamento.tpDebito);
					
					listaParcelas.add(parcela);
				}
			}else {
				parcela.setControle(nfeTemp.getNumeroNota());
				parcela.setNumParcela(1L);
				
				inicio = pagamento.indexOf("<VPAG>");
				fim = pagamento.indexOf("</VPAG>");
				vPag = pagamento.substring(inicio+6, fim);
				parcela.setValorParcela(new BigDecimal(vPag));
				parcela.setValorOriginal(new BigDecimal(vPag));
				parcela.setVencimento(data);
				parcela.setTipoPagamento(this.tipoPagamento);
				parcela.setTipoLancamento(TipoLancamento.tpDebito);
				listaParcelas.add(parcela);
			}
			break;
		case Car:
			break;
		case Cde:
			break;
		case Che:
			break;
		case Crl:
			break;
		case Din:
			parcela.setControle(nfeTemp.getNumeroNota());
			parcela.setNumParcela(1L);
			
			inicio = pagamento.indexOf("<VPAG>");
			fim = pagamento.indexOf("</VPAG>");
			vPag = pagamento.substring(inicio+6, fim);
			parcela.setValorParcela(new BigDecimal(vPag));
			parcela.setValorOriginal(new BigDecimal(vPag));
			parcela.setVencimento(data);
			parcela.setTipoPagamento(this.tipoPagamento);
			parcela.setTipoLancamento(TipoLancamento.tpDebito);
			listaParcelas.add(parcela);
			break;
		case Dbc:
			break;
		case Dpm: // duplica mercantil
			if (totalDup > 0 ) {
				List<String> listaDuplicatas = new ArrayList<>();
				listaDuplicatas = localiza.listaString(xml,"<DUP>","</DUP>");
				for (String  dp : listaDuplicatas) {
					
					inicio = dp.indexOf("<NDUP>");
					fim = dp.indexOf("</NDUP>");
					String numDup = dp.substring(inicio+6, fim);
					
					inicio = dp.indexOf("<DVENC>");
					fim = dp.indexOf("</DVENC>");
					String venci = dp.substring(inicio+7, fim);
					
					inicio = dp.indexOf("<VDUP>");
					fim = dp.indexOf("</VDUP>");
					String vlDup = dp.substring(inicio+6, fim);
					
					
					parcela = new ParcelasNfe();
					parcela.setNumParcela(new BigDecimal(numDup).longValue());
					parcela.setControle(nfeTemp.getNumeroNota());
					parcela.setValorParcela(new BigDecimal(vlDup));
					parcela.setValorOriginal(new BigDecimal(vlDup));
					parcela.setVencimento(LocalDate.parse(venci,formataAaaaMmDd));
					parcela.setTipoPagamento(this.tipoPagamento);
					parcela.setTipoLancamento(TipoLancamento.tpDebito);
					
					listaParcelas.add(parcela);
				}
			}else {
				parcela.setControle(nfeTemp.getNumeroNota());
				parcela.setNumParcela(1L);
				
				inicio = pagamento.indexOf("<VPAG>");
				fim = pagamento.indexOf("</VPAG>");
				vPag = pagamento.substring(inicio+6, fim);
				parcela.setValorParcela(new BigDecimal(vPag));
				parcela.setValorOriginal(new BigDecimal(vPag));
				parcela.setVencimento(data);
				parcela.setTipoPagamento(this.tipoPagamento);
				parcela.setTipoLancamento(TipoLancamento.tpDebito);
				listaParcelas.add(parcela);
			}
			break;
		case Pfd:
			break;
		case Pix:
			parcela.setControle(nfeTemp.getNumeroNota());
			parcela.setNumParcela(1L);
			
			inicio = pagamento.indexOf("<VPAG>");
			fim = pagamento.indexOf("</VPAG>");
			vPag = pagamento.substring(inicio+6, fim);
			parcela.setValorParcela(new BigDecimal(vPag));
			parcela.setValorOriginal(new BigDecimal(vPag));
			parcela.setVencimento(data);
			parcela.setTipoPagamento(this.tipoPagamento);
			parcela.setTipoLancamento(TipoLancamento.tpDebito);
			listaParcelas.add(parcela);
			break;
		case Tbc:
			break;
		case Out:
			if (totalDup > 0 ) {
				List<String> listaDuplicatas = new ArrayList<>();
				listaDuplicatas = localiza.listaString(xml,"<DUP>","</DUP>");
				for (String  dp : listaDuplicatas) {
					
					inicio = dp.indexOf("<NDUP>");
					fim = dp.indexOf("</NDUP>");
					String numDup = dp.substring(inicio+6, fim);
					
					inicio = dp.indexOf("<DVENC>");
					fim = dp.indexOf("</DVENC>");
					String venci = dp.substring(inicio+7, fim);
					
					inicio = dp.indexOf("<VDUP>");
					fim = dp.indexOf("</VDUP>");
					String vlDup = dp.substring(inicio+6, fim);
					
					
					parcela = new ParcelasNfe();
					parcela.setNumParcela(new BigDecimal(numDup).longValue());
					parcela.setControle(nfeTemp.getNumeroNota());
					parcela.setValorParcela(new BigDecimal(vlDup));
					parcela.setValorOriginal(new BigDecimal(vlDup));
					parcela.setVencimento(LocalDate.parse(venci,formataAaaaMmDd));
					parcela.setTipoPagamento(this.tipoPagamento);
					parcela.setTipoLancamento(TipoLancamento.tpDebito);
					
					listaParcelas.add(parcela);
				}
			}else {
				parcela.setControle(nfeTemp.getNumeroNota());
				parcela.setNumParcela(1L);
				
				inicio = pagamento.indexOf("<VPAG>");
				fim = pagamento.indexOf("</VPAG>");
				vPag = pagamento.substring(inicio+6, fim);
				parcela.setValorParcela(new BigDecimal(vPag));
				parcela.setValorOriginal(new BigDecimal(vPag));
				parcela.setVencimento(data);
				parcela.setTipoPagamento(this.tipoPagamento);
				parcela.setTipoLancamento(TipoLancamento.tpDebito);
				listaParcelas.add(parcela);
			}
			break;
		case Spg:
			break;
		case Val:
			break;
		case Vco:
			break;
		case Vpr:
			break;
		case Vre:
			break;
		default:
			break;
		}
		// preenchendo a nfe com as parcelas de pagamento
		nfeTemp.setListaParcelas(listaParcelas);
		//definindo cobranca
		/*
		 * � necessario desenvolver o modulo financeiro para criar as formas de pagamento
		 */

		// definindo totais da nfe
		inicio = xml.indexOf("<TOTAL>");
		fim = xml.indexOf("</TOTAL>");
		String blocoTotal = xml.substring(inicio+7, fim);
		System.out.println("Total: " + blocoTotal);

		inicio = blocoTotal.indexOf("<VBC>");
		fim = blocoTotal.indexOf("</VBC>");
		System.out.println("vBC: " + blocoTotal.substring(inicio+5, fim));
		nfeTemp.setBaseIcms(new BigDecimal(blocoTotal.substring(inicio+5, fim)));

		inicio = blocoTotal.indexOf("<VBCST>");
		fim = blocoTotal.indexOf("</VBCST>");
		nfeTemp.setBaseIcmsSubstituicao(new BigDecimal(blocoTotal.substring(inicio+7, fim)));

		inicio = blocoTotal.indexOf("<VICMS>");
		fim = blocoTotal.indexOf("</VICMS>");
		nfeTemp.setValorIcms(new BigDecimal(blocoTotal.substring(inicio+7, fim)));

		inicio = blocoTotal.indexOf("<VICMSDESON>");
		fim = blocoTotal.indexOf("</VICMSDESON>");
		nfeTemp.setValorIcmsDesonerado(new BigDecimal(blocoTotal.substring(inicio+12, fim)));

		boolean achei = localiza.localizaPalavra(blocoTotal, "<VFCPUFDEST>");
		if (achei){
			inicio = blocoTotal.indexOf("<VFCPUFDEST>");
			fim = blocoTotal.indexOf("</VFCPUFDEST>");
			nfeTemp.setVFCPUFDest(new BigDecimal(blocoTotal.substring(inicio+12, fim)));
		}
		achei = localiza.localizaPalavra(blocoTotal, "<VICMSUFDEST>");
		if (achei){
			inicio = blocoTotal.indexOf("<VICMSUFDEST>");
			fim = blocoTotal.indexOf("</VICMSUFDEST>");
			nfeTemp.setVICMSUFDest(new BigDecimal(blocoTotal.substring(inicio+13, fim)));
		}

		achei = localiza.localizaPalavra(blocoTotal, "<VICMSUFREMET>");
		if (achei){
			inicio = blocoTotal.indexOf("<VICMSUFREMET>");
			fim = blocoTotal.indexOf("</VICMSUFREMET>");
			nfeTemp.setVICMSUFRemet(new BigDecimal(blocoTotal.substring(inicio+14, fim)));
		}

		inicio = blocoTotal.indexOf("<VFCP>");
		fim = blocoTotal.indexOf("</VFCP>");
		nfeTemp.setVFCP(new BigDecimal(blocoTotal.substring(inicio+6, fim)));

		//			inicio = blocoTotal.indexOf("<vICMS>");
		//			fim = blocoTotal.indexOf("</vICMS>");
		//			nfeTemp.setValorIcmsSubstituicao(valorIcmsSubstituicao);

		inicio = blocoTotal.indexOf("<VFCPST>");
		fim = blocoTotal.indexOf("</VFCPST>");
		nfeTemp.setVFCPST(new BigDecimal(blocoTotal.substring(inicio+8, fim)));

		inicio = blocoTotal.indexOf("<VFCPSTRET>");
		fim = blocoTotal.indexOf("</VFCPSTRET>");
		nfeTemp.setVFCPSTRet(new BigDecimal(blocoTotal.substring(inicio+11, fim)));

		inicio = blocoTotal.indexOf("<VPROD>");
		fim = blocoTotal.indexOf("</VPROD>");
		nfeTemp.setValorTotalProdutos(new BigDecimal(blocoTotal.substring(inicio+7, fim)));

		inicio = blocoTotal.indexOf("<VFRETE>");
		fim = blocoTotal.indexOf("</VFRETE>");
		nfeTemp.setValorFrete(new BigDecimal(blocoTotal.substring(inicio+8, fim)));

		inicio = blocoTotal.indexOf("<VSEG>");
		fim = blocoTotal.indexOf("</VSEG>");
		nfeTemp.setValorSeguro(new BigDecimal(blocoTotal.substring(inicio+6, fim)));

		inicio = blocoTotal.indexOf("<VDESC>");
		fim = blocoTotal.indexOf("</VDESC>");
		nfeTemp.setDesconto(new BigDecimal(blocoTotal.substring(inicio+7, fim)));

		inicio = blocoTotal.indexOf("<VIPI>");
		fim = blocoTotal.indexOf("</VIPI>");
		nfeTemp.setValorTotalIpi(new BigDecimal(blocoTotal.substring(inicio+6, fim)));

		inicio = blocoTotal.indexOf("<VIPIDEVOL>");
		fim = blocoTotal.indexOf("</VIPIDEVOL>");
		nfeTemp.setVIPIDevol(new BigDecimal(blocoTotal.substring(inicio+11, fim)));

		inicio = blocoTotal.indexOf("<VPIS>");
		fim = blocoTotal.indexOf("</VPIS>");
		nfeTemp.setValorTotalPis(new BigDecimal(blocoTotal.substring(inicio+6, fim)));

		inicio = blocoTotal.indexOf("<VCOFINS>");
		fim = blocoTotal.indexOf("</VCOFINS>");
		nfeTemp.setValorTotalCofins(new BigDecimal(blocoTotal.substring(inicio+9, fim)));

		inicio = blocoTotal.indexOf("<VOUTRO>");
		fim = blocoTotal.indexOf("</VOUTRO>");
		nfeTemp.setOutrasDespesas(new BigDecimal(blocoTotal.substring(inicio+8, fim)));

		inicio = blocoTotal.indexOf("<VNF>");
		fim = blocoTotal.indexOf("</VNF>");
		nfeTemp.setValorTotalNota(new BigDecimal(blocoTotal.substring(inicio+5, fim)));

		achei = localiza.localizaPalavra(blocoTotal, "<VTOTTRIB>");
		if (achei){
			inicio = blocoTotal.indexOf("<VTOTTRIB>");
			fim = blocoTotal.indexOf("</VTOTTRIB>");
			nfeTemp.setValorTotalTributos(new BigDecimal(blocoTotal.substring(inicio+10, fim)));
		}
		// Fim do Totais nfe

		inicio = xml.indexOf("<PROTNFE");
		fim = xml.indexOf("</PROTNFE>");
		String blocoProtocolo = xml.substring(inicio+8, fim);
		//setando o Protocolo de autorizacao da nfe
		inicio = blocoProtocolo.indexOf("<NPROT>");
		fim = blocoProtocolo.indexOf("</NPROT>");
		nfeTemp.setProtocoloAutorizacao(blocoProtocolo.substring(inicio+7, fim));
		//setando a chave da nfe
		inicio = blocoProtocolo.indexOf("<CHNFE>"); 
		fim = blocoProtocolo.indexOf("</CHNFE>");
		nfeTemp.setChaveAcesso(blocoProtocolo.substring(inicio+7, fim)); 


		return nfeTemp;
		//		}catch (Exception e){
		//			logger.error(e.getMessage());
		//			return null;
		//		}
	}

	/**
	 *  Lista do autocompletar Departamento  
	 */
	public List<Departamento> completaDepartamento(String query) { // Testar!!!!!!!

		List<Departamento> fontePesquisa = this.departamentoDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa());

		return fontePesquisa;
	}


	public List<Departamento> listaDepartamento(){
		return this.departamentoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
	}
	
	public boolean calcularCustoMedio() {
		boolean calcularCustoMedioTemp = false;
		if (pegaIdFilial() != null) {
			if (this.configEmpUser().getFil().isGerarCustoMedio()) {
				calcularCustoMedioTemp= true;
			}
		}else {
			if (this.configEmpUser().getEmp().isGerarCustoMedio()) {
				calcularCustoMedioTemp = true;
			}
		}
		return calcularCustoMedioTemp;
	}

	@Transactional
	public void doSalvarNewProduto(){
		//		try{
		if (this.produto.getId() == null){
			setaBarrasEstoque();
			String ean = verificaEan(this.item);
			System.out.println(ean + "erro ean" + this.item.getCProd());
			boolean emUso = codigoBarrasJaUtilizado(ean);
			if (emUso){
				this.item.setEan("");
				ean=verificaEan(this.item);
			}
			this.item.setEan(ean);
			preencheBarrasEstoque(this.item);
			this.item.setEstoqueAtualizado(true);
			this.itemDao.save(this.item);
			List<Fornecedor> listaTempFornecedores = new ArrayList<>();
			this.fornecedor = this.fornecedorDao.findById(this.nfe.getEmitente().getFornecedor().getId(), false);
			System.out.println(this.nfe.getEmitente().getFornecedor().getId());
			listaTempFornecedores.add(this.fornecedor);

			if (this.produto.getId() == null){ // save
				if(!this.produtoDao.jaExiste(this.produto.getReferencia(),getUsuarioAutenticado().getIdEmpresa())){
					this.produto.setDeleted(false);
					this.produto = this.produtoDao.save(this.produto);

				}
				if (this.produto.getId() != null){
					this.barrasEstoque.setProdutoBase(this.produto);
					this.produto.setFornecedores(listaTempFornecedores);
					
					this.ncmEstoque = this.ncmEstoqueDao.save(this.ncmEstoque);
					this.custoProduto.setProduto(this.produto);
					this.produto.getListaCustoProduto().add(this.custoProduto);
					this.produto.getListaBarras().add(this.barrasEstoque);
					this.produtoDao.save(this.produto);
					//					this.produto = this.produtoDao.pegaProdutoComFornecedores(this.produto.getId(), getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
				}
			}
			//			this.produto.setFornecedores(listaTempFornecedores);
			//			this.barrasEstoque.setProdutoBase(this.produto);
			//			this.ncmEstoque = this.ncmEstoqueDao.save(this.ncmEstoque);
			//			this.custoProduto.setProduto(this.produto);
			//			this.produto.getListaCustoProduto().add(this.custoProduto);
			//			this.produto.getListaBarras().add(this.barrasEstoque);
			//			this.produto = this.produtoDao.save(this.produto);
			System.out.println("recebimento - Custo1: " + this.custoProduto.getCusto());
			System.out.println("recebimento - preco1: " + this.custoProduto.getPreco1());

		}else{ // produto nulo
			throw new RuntimeException("Erro n�o � um novo produto");
		}
		//		}catch (IllegalArgumentException i){
		//			this.addError(true, "exception.error.fatal", i.getLocalizedMessage());
		//		}catch (RuntimeException r){
		//			this.addError(true, "exception.error.fatal", r.getLocalizedMessage());
		//		}catch (Exception e){
		//			System.out.println(e);
		//			this.addError(true, "exception.error.fatal", e.getCause() + " : " + e.getLocalizedMessage());
		//		}
	}
	public void exibeTamanho(){
		System.out.print("IBR - " + this.tamanho + " id: " + this.tamanho.getId() );
	}
	@Transactional
	public void geraFinanceiroNFEEntrada(NfeNaoConfirmada nf) {
		try {
			NfeRecebida nfRec = new NfeRecebida();
			nfRec = this.nfeRecebidaDao.pegaNfeRecebidaComParcelas(nf.getNfeRecebida().getId(), pegaIdEmpresa(), pegaIdFilial());
			List<ParcelasNfe> listaParcelas = nfRec.getListaParcelas();
			if (listaParcelas.size() >0 && !nfRec.isFinanceiroGerado()) {
				for(Iterator<ParcelasNfe> parc = listaParcelas.iterator();parc.hasNext();) {
					ParcelasNfe parcela = parc.next();
					System.out.println("Adicionando Parcela no recParcial");
					if (parcela.getTipoPagamento().equals(TipoPagamento.Din) || 
							parcela.getTipoPagamento().equals(TipoPagamento.Pix) ||
							parcela.getTipoPagamento().equals(TipoPagamento.Dbc) ||
							parcela.getTipoPagamento().equals(TipoPagamento.Tbc)) {
						parcela.setStatus(ParcelaStatus.REC);
					}else {
						parcela.setStatus(ParcelaStatus.ABE);
					}
					parcela.setQRecorrencia(listaParcelas.size());
					parcela.setDescricao(nf.getXNome());
					parcela.setFinanceiro(true);
					parcela.setTipoLancamento(TipoLancamento.tpDebito);
					parcela = parcelasNfeDao.save(parcela);
				}			
				nfRec.setFinanceiroGerado(true);
				nf.setFinanceiroCriado(true);
				nf = nfNaoConfirmadaDao.save(nf);
				nfRec = nfeRecebidaDao.save(nfRec);
			}else {
				if (nfRec.isFinanceiroGerado()) {
					throw new NfeException(this.translate("financial.hasAlreadyBeenCreated"));
				}else {
					throw new NfeException(this.translate("financial.list.empty"));
				}
			}
			this.addInfo(true,"financial.create.sucess", "Raz�o Social " + nf.getXNome() + "NSU: " + nf.getNsu());
		}catch (HibernateException h){
			this.addError(true, "hibernate.persist.fail", h.getCause());
		}catch (NfeException n) {
			this.addError(true, "financeial.error", n.getCause());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getCause());
		}
	}
}
