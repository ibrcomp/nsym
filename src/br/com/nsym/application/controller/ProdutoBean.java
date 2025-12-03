package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.hibernate.HibernateException;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.BarrasException;
import br.com.ibrcomp.exception.EstoqueException;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.misc.CodigoBarrasUtil;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Cor;
import br.com.nsym.domain.model.entity.cadastro.Departamento;
import br.com.nsym.domain.model.entity.cadastro.Fabricante;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.Secao;
import br.com.nsym.domain.model.entity.cadastro.SubSecao;
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.Finalidade;
import br.com.nsym.domain.model.entity.tools.TipoControleEstoque;
import br.com.nsym.domain.model.entity.tools.TipoMedida;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.CoresRepository;
import br.com.nsym.domain.model.repository.cadastro.DepartamentoRepository;
import br.com.nsym.domain.model.repository.cadastro.FabricanteRepository;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.cadastro.SecaoRepository;
import br.com.nsym.domain.model.repository.cadastro.SubSecaoRepository;
import br.com.nsym.domain.model.repository.cadastro.TamanhoRepository;
import br.com.nsym.domain.model.repository.fiscal.NCMRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ProdutoBean extends AbstractBeanEmpDS<Produto>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	private AbstractLazyModel<Produto> produtoModel;

	@Inject
	private ProdutoRepository produtoDao;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private TipoControleEstoque visivelControleEstoque ;

	@Getter
	@Setter
	private boolean barrasVisivel = true;

	@Getter
	@Setter
	private boolean tamanhoVisivel = false;

	@Getter
	@Setter
	private boolean corVisivel = false;

	@Getter 
	@Setter
	private Produto produto;

	@Getter
	@Setter
	private boolean visivelPorIdProduto = false;

	@Getter
	@Setter
	private ProdutoCusto custoProduto;

//	@Inject
//	private CustoProdutoRepository custoDao;

	@Inject
	private DepartamentoRepository departamentoDao;

	@Inject
	private SubSecaoRepository subSecaoDao;

	@Inject
	private SecaoRepository secaoDao;

	@Inject
	private FabricanteRepository fabricanteDao;

	@Inject
	private NCMRepository ncmDao;

	@Getter
	@Setter
	private Fornecedor fornecedor = new Fornecedor();

	@Inject
	private FornecedorRepository fornecedorDao;

	@Getter
	@Setter
	private Fornecedor selectFornecedor;

	@Getter
	@Setter
	private transient List<Fornecedor> listaTemporaria = new ArrayList<>();

	@Getter
	private List<Tributos> listaTributosAtivos = new ArrayList<>();

	@Inject
	private TributosRepository tributoDao;

	// DualListModel para o PickList do primefaces - Cor - Tamanho - Barras

	@Getter
	@Setter
	private transient List<Cor> corSource = new ArrayList<Cor>();
	@Getter
	@Setter
	private transient List<Cor> corTarget = new ArrayList<Cor>();

	@Getter
	@Setter
	private transient DualListModel<Cor> cores;

	@Getter
	@Setter
	private transient List<Tamanho> tamanhoSource = new ArrayList<Tamanho>();
	@Getter
	@Setter
	private transient List<Tamanho> tamanhoTarget = new ArrayList<Tamanho>();

	@Getter
	@Setter
	private transient DualListModel<Tamanho> tamanhos;

	// Lista que recebe os Cï¿½digos de barras com COR-Tamanho

	@Getter
	@Setter
	private transient List<BarrasEstoque> listaBarras = new ArrayList<>();

	@Getter
	@Setter
	private BarrasEstoque barrasUni = new BarrasEstoque();

	@Inject
	private BarrasEstoqueRepository barrasDao;

	// variï¿½veis necessï¿½rias para geraï¿½ï¿½o dos cï¿½digos de barras com tamanho e cor
	// sendo opcional inserir tamanho, cor ou uma das duas
	// caso nao selecione ele cria automaticamente COR =UNICA Tamanho=Unico

	@Inject
	private CoresRepository corDao;

	@Inject
	private TamanhoRepository tamanhoDao;

	@Getter
	@Setter
	private List<BarrasEstoque> listaBarrasExcluir = new ArrayList<>();

	@Getter
	@Setter
	private boolean temBarrasExcluir = false;

	@Inject
	private CodigoBarrasUtil barrasUtil;

	// Incio tabelas controle de estoque

	// estoque geral
//	private ProdutoEstoque estoque = new ProdutoEstoque();

	@Getter
	@Setter
	private BigDecimal numeroDisponivel = new BigDecimal("1");

	@Getter
	@Setter
	private transient List<ProdutoCusto> listaTempCusto = new ArrayList<>();

	@Getter
	@Setter
	private transient List<Produto> listaTempProduto = new ArrayList<>();
	
	@Inject
	private EstoqueUtil estoqueUtil;
	
	private MathContext mc = new MathContext(20,RoundingMode.HALF_EVEN);
	
	@Getter
	@Setter
	private BigDecimal margemA = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private BigDecimal margemB = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private BigDecimal margemC = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private BigDecimal margemD = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	private BigDecimal margemE = new BigDecimal("0",mc);
	
	private boolean isRevenda = false;
	
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Produto> getLazyProduto(){
		this.produtoModel = new AbstractLazyModel<Produto>() {

			/**
			 *
			 */
			private static final long serialVersionUID = -5197500519977819243L;

			@Override
			public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<Produto> page = produtoDao.pegaPageProdutoComCustoComEstoque(isDeleted,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = produtoDao.pegaPageProdutoComEstoqueComCustoComFiltro(isDeleted,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
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
	 * Inicializaï¿½ï¿½o da pagina em modo de Adiï¿½ï¿½o ou Ediï¿½ï¿½o
	 * @param idProduto
	 */
	public void initializeForm(Long idProduto) {
		this.listaTributosAtivos = this.tributoDao.listaTodosTributosAtivos(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		this.corSource = this.corDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
		this.tamanhoSource = this.tamanhoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
		this.corTarget = new ArrayList<Cor>();
		this.tamanhoTarget = new ArrayList<Tamanho>();
		this.cores = new DualListModel<Cor>(corSource,corTarget);
		this.tamanhos = new DualListModel<Tamanho>(tamanhoSource,tamanhoTarget);
		this.isRevenda = true;
		if (idProduto == null) {
			this.viewState = ViewState.ADDING;
			this.produto  = new Produto();
			this.produto.setTipoEstoque(TipoControleEstoque.BA);
			this.fornecedor = new Fornecedor();
			this.selectFornecedor = new Fornecedor();
			this.custoProduto = new ProdutoCusto();
//			this.estoque = new ProdutoEstoque();
			//			this.departamento = new Departamento();
			//			this.secao = new Secao();
			//			this.subSecao = new SubSecao();
			//			this.fabricante = new Fabricante();
		} else {
			this.viewState = ViewState.EDITING;
					setVisivelPorIdProduto(true);
			this.produto = this.produtoDao.pegaProdutoComFornecedores(idProduto, this.pegaIdEmpresa(),this.pegaIdFilial());
			List<BarrasEstoque>listaTempBarras = new ArrayList<>(); 
			listaTempBarras = this.barrasDao.listaBarrasPorProduto(this.produto, this.pegaIdEmpresa(), this.pegaIdFilial());
			if (listaTempBarras == null){
				this.produto.setListaBarras(new ArrayList<>());
				this.setListaBarras(new ArrayList<>());
			}else{
				this.produto.setListaBarras(listaTempBarras);
				this.setListaBarras(listaTempBarras);
			}
			//			this.produto.setListaBarras(this.barrasDao.listaBarrasPorProduto(this.produto, this.pegaIdEmpresa()));
//			this.estoque = new ProdutoEstoque();
			setaCusto();
			//			this.estoque = this.produto.getEstoqueGeral();
			this.listaTemporaria = this.produto.getFornecedores();
			this.barrasUni = new BarrasEstoque();
			setaTipoControleEstoque();
			// atualizando as margens;
			this.calculaMargem();

		}
	}
	
	/**
	 * Metodo que calcula o estoque total independente de tamanho ou cor
	 * @return String com a somatoria
	 */
	public String calculaEstoqueTotal(){
		
		return estoqueUtil.calculaEstoqueTotal(this.listaBarras).toString();
	}
	
	/**
	 * Metodo que calcula o total jï¿½ comprado do produto
	 */
	
	public String calculaTotalComprado(){
		
		return estoqueUtil.calculaTotalComprado(this.listaBarras).toString();
	}
	/**
	 * Metodo que retorna a data da ultima compra
	 * @return Data da ultima compra
	 */
	public LocalDate retornaDataUltimaCompra(){
		return estoqueUtil.retornaUltimaCompra(this.listaBarras);	
	}
	
	/**
	 * Metodo que retorna a data da primeira compra
	 * @return LocalDate
	 */
	
	public LocalDate retornaPrimeiraCompra(){
		return estoqueUtil.retornaPrimeiraCompra(this.listaBarras);
	}
	
	/**
	 * Metodo que retorna o total da ultima compra
	 * @return String 
	 */
	public String retornaTotalUltimaCompra(){
		return estoqueUtil.retornaTotalUltimaCompra(this.listaBarras).toString();
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
	 * Inicializaï¿½ï¿½o da pagina em modo listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
		this.produtoModel = getLazyProduto();
	}
	@Transactional
	public void doSalvar() {
		try{
			if (this.produto.getTipoMedida() == null) {
				throw new IllegalArgumentException(this.translate("produto.typeMedida"));
			}
			if (this.produto.getId() == null){ // save
				if(this.produtoDao.jaExiste(this.produto.getReferencia(),getUsuarioAutenticado().getIdEmpresa())){
					throw new HibernateException(this.translate("hibernate.persist.ref.exist") + this.produto.getReferencia());
				}
					this.custoProduto.setProduto(this.produto);
					this.produto.setDeleted(false);
					this.produto.getListaCustoProduto().add(this.custoProduto);
					
					for (BarrasEstoque barras : this.produto.getListaBarras()) {
						barras.setProdutoBase(this.produto);
					}
					if(this.isRevenda) {
						this.produto.setFinalidade(Finalidade.Rev);
					}
					this.produto = this.produtoDao.save(this.produto);
					this.produto = this.produtoDao.pegaProdutoComFornecedores(this.produto.getId(), getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
					if (this.produto.getListaBarras().size()==0) {
						BarrasEstoque barrasEstoque = new BarrasEstoque(); 						
						barrasEstoque.setProdutoBase(this.produto);
						barrasDao.save(barrasEstoque);
					}
//					this.produto.setListaBarras(this.barrasDao.listaBarrasPorProduto(this.produto, this.pegaIdEmpresa(), this.pegaIdFilial()));
					setaCusto();
//				}
				this.addInfo(true, "save.sucess", this.produto.getDescricao());
			}else{ // update
				for (BarrasEstoque barras : this.produto.getListaBarras()) {
					if (barras.getProdutoBase() == null){
						barras.setProdutoBase(this.produto);
					}
				}
				this.custoProduto.setProduto(this.produto);
				if (this.produto.getListaCustoProduto().isEmpty() && this.custoProduto.getId() == null){
					this.produto.getListaCustoProduto().add(this.custoProduto);
				}else{
					if (this.custoProduto.getId() != null){
						for (int i = 0; this.produto.getListaCustoProduto().size() > i ; i++ ){

							if ((this.produto.getListaCustoProduto().get(i).getId() == this.custoProduto.getId()) &&
									(this.produto.getListaCustoProduto().get(i).getIdEmpresa() == this.custoProduto.getIdEmpresa())&&
									(this.produto.getListaCustoProduto().get(i).getIdFilial() == this.custoProduto.getIdFilial())){
								this.produto.getListaCustoProduto().get(i).setPreco1(this.custoProduto.getPreco1());
								this.produto.getListaCustoProduto().get(i).setPreco2(this.custoProduto.getPreco2());
								this.produto.getListaCustoProduto().get(i).setPreco3(this.custoProduto.getPreco3());
								this.produto.getListaCustoProduto().get(i).setPreco4(this.custoProduto.getPreco4());
								this.produto.getListaCustoProduto().get(i).setPreco5(this.custoProduto.getPreco5());
							}
						}
					}else{
						if (this.pegaIdFilial() != null){
							if (this.custoProduto.getId() == null ){
								this.produto.getListaCustoProduto().add(this.custoProduto);
							}else{
								for (int i = 0; this.produto.getListaCustoProduto().size() > i ; i++ ){

									if ((this.produto.getListaCustoProduto().get(i).getId() == this.custoProduto.getId()) &&
											(this.produto.getListaCustoProduto().get(i).getIdEmpresa() == this.custoProduto.getIdEmpresa())&&
											(this.produto.getListaCustoProduto().get(i).getIdFilial() == this.custoProduto.getIdFilial())){
										this.produto.getListaCustoProduto().get(i).setPreco1(this.custoProduto.getPreco1());
										this.produto.getListaCustoProduto().get(i).setPreco2(this.custoProduto.getPreco2());
										this.produto.getListaCustoProduto().get(i).setPreco3(this.custoProduto.getPreco3());
										this.produto.getListaCustoProduto().get(i).setPreco4(this.custoProduto.getPreco4());
										this.produto.getListaCustoProduto().get(i).setPreco5(this.custoProduto.getPreco5());
									}
								}
							}
						}
						this.addError(true, "codigo.jaexite", "lista de custo nao esta vazia e variavel custoProduto sem ID ");
					}
				}
				if (temBarrasExcluir){
					for (BarrasEstoque barrasExclui : this.listaBarrasExcluir) {
						if (barrasExclui.getTotalEstoque().compareTo(new BigDecimal("0"))==0) {
							this.barrasDao.delete(barrasExclui);
						}else {
							throw new EstoqueException(this.translate("estoqueException.barras.estoque.notEmpty"));
						}
					}
				}
				this.produto = this.produtoDao.save(this.produto);
				this.produto = this.produtoDao.pegaProdutoComFornecedores(this.produto.getId(), getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
				this.produto.setListaBarras(this.barrasDao.listaBarrasPorProduto(this.produto, this.pegaIdEmpresa(), this.pegaIdFilial()));
				setaCusto();
				this.addInfo(true, "save.sucess", this.produto.getDescricao());
			}
		}catch (EstoqueException es) {
			this.addError(true, "caixa.error", es.getMessage());
		}catch (IllegalArgumentException e) {
			this.addError(true, "estoque.recebimento.error", e.getMessage());
		}catch (HibernateException g){
			this.addError(true, "caixa.error", g.getMessage());
		}
	}
	
//	public void populaProduto(){
//		this.produto = this.produtoDao.pegaProdutoComFornecedores(this.produto.getId(), getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
//		setaCusto();
//	}

	/**
	 * define produto como excluido e 
	 * @return para lista de produto
	 */
	@Transactional
	public String doExcluir() {
		try {
			this.produto.setDeleted(true);
			produtoDao.save(this.produto);
			this.addInfo(true, "delete.sucess",this.produto.getDescricao());
			return toListProduto();

		} catch (IllegalArgumentException e) {
			System.out.println("Nï¿½o foi possivel excluir o registro " + e);
			return null;
		}
	}

	/**
	 * redireciona para Cadastramento de novo produto / ediï¿½ï¿½o de produto jï¿½ cadastrado
	 * @return pagina de ediï¿½ï¿½o/inclusao de produto
	 */
	public String newProduto() {
		return "formCadProduto.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListProduto() {
		return "formListProduto.xhtml?faces-redirect=true";
	}
	
	/**
	 * redireciona para a pagina com o ID do produto a ser editado
	 * 
	 * @param produtoID
	 * 
	 * @return
	 */
	public String changeToEdit(Long produtoID) {
		return "formCadProduto.xhtml?faces-redirect=true&produtoID=" + produtoID;
	}

	/**
	 * chama dialog Departamento
	 */
	public void telaDepartamento() {
		this.openDialog("dialogDepartamento");
	}

	/**
	 * chama dialog Seï¿½ï¿½o
	 */
	public void telaSecao() {
		this.openDialog("dialogSecao");
	}

	/**
	 * chama dialog SubSeï¿½ï¿½o
	 */
	public void telaSubSecao() {
		this.openDialog("dialogSubSecao");
	}

	/**
	 * chama dialog Fabricante
	 */
	public void telaFabricante() {
		this.openDialog("dialogFabricante");
	}

	/**
	 * @return a lista de tipos validos para Enquadramento
	 */
	public Enquadramento[] getEnquadramentoType() {
		return Enquadramento.values();
	}

	/**
	 * 
	 * @return a lista de Finalidades do produto
	 */
	public Finalidade[] getFinalidadeType(){
		return Finalidade.values();
	}

	/**
	 * Insere o produto na sessao
	 */
	public void insereProdutoNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		limpaSessao();
		session.setAttribute("produto", produto);

	}

	/**
	 * Evento que controla o item da lista selecionado enviando o id do produto pela url 
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		this.produto = (Produto) event.getObject();
		setVisivelPorIdProduto(true);
		this.viewState = ViewState.EDITING;
	}

	public void limpaFormulario(){
		this.produto = new Produto();
	}


	public void criaExcel(){
		postProcessXLS(this.produtoModel.getWrappedData());
	}

	/**
	 * Criar um documento em excel personalizado.
	 *  @param document
	 */
	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColorPredefined.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
		}
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

	/**
	 *  Lista do autocompletar Secao  
	 */
	public List<Secao> completaSecao(String query) { // Testar!!!!!!!

		List<Secao> fontePesquisa = this.secaoDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa());

		return fontePesquisa;
	}


	public List<Secao> listaSecao(){
		return this.secaoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(),null);
	}

	/**
	 *  Lista do autocompletar SubSecao  
	 */
	public List<SubSecao> completaSubSecao(String query) { // Testar!!!!!!!

		List<SubSecao> fontePesquisa = this.subSecaoDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa());

		return fontePesquisa;
	}


	public List<SubSecao> listaSubSecao(){
		return this.subSecaoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
	}

	/**
	 *  Lista do autocompletar Fabricante 
	 */
	public List<Fabricante> completaFabricante(String query) { // Testar!!!!!!!

		List<Fabricante> fontePesquisa = this.fabricanteDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa());

		return fontePesquisa;
	}


	public List<Fabricante> listaFabricante(){
		return this.fabricanteDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
	}

	/**
	 * lista do autocompletar NCM
	 */
	public List<Ncm> completaNcm(String query){
		List<Ncm> fontePesquisa = this.ncmDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(), null);
		return fontePesquisa;
	}

	public List<Ncm> listaNcm(){
		return this.ncmDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
	}

	/**
	 *  Lista do autocompletar Fornecedor
	 */
	public List<Fornecedor> completaFornecedor(String query) { // Testar!!!!!!!

		List<Fornecedor> fontePesquisa = this.fornecedorDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(),null);

		return fontePesquisa;
	}


	public List<Fornecedor> listaFornecedor(){
		return this.fornecedorDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
	}

	public void addFornecedorList(){
		if( this.fornecedor  != null){
			this.listaTemporaria.add(this.fornecedor);
			this.produto.setFornecedores(this.listaTemporaria);
		}
	}

	public void removeFornecedorList(){
		//		if (!this.produto.getFornecedores().isEmpty()){
		//			for (int i = 0 ; i <  this.produto.getFornecedores().size(); i++){
		//				if (this.produto.getFornecedores().get(i).equals(this.selectFornecedor)){
		//					this.produto.getFornecedores().remove(i);
		//				}
		//			}
		//		}
		if (!this.listaTemporaria.isEmpty()){
			for (int i = 0 ; i <  this.listaTemporaria.size(); i++){
				if (this.listaTemporaria.get(i).equals(this.selectFornecedor)){
					this.listaTemporaria.remove(i);
				}
			}
			this.produto.setFornecedores(this.listaTemporaria);
		}
	}


	public void limpaFornecedor(){
		this.fornecedor = new Fornecedor();
	}

	public TipoMedida[] getTipoMedidaType(){
		return TipoMedida.values();
	}

	/**
	 * lista do autocompletar Tributos
	 */
	public List<Tributos> completaTributos(String query){
		List<Tributos> fontePesquisa = this.tributoDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		return fontePesquisa;
	}

	@Override
	public Produto setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Produto setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	public String geraNumReg(){
		for (int t = 0; this.listaBarras.size() > t; t++){
			for (BarrasEstoque regBarras : this.listaBarras) {
				if (regBarras.getNumReg() != null){
					if (this.numeroDisponivel.compareTo(new BigDecimal(regBarras.getNumReg()))==0){
						this.numeroDisponivel = this.numeroDisponivel.add(new BigDecimal("1"));
						System.out.println("t = "+ t + "disponivel " +this.numeroDisponivel.toString());
					}
				}
			}
		}
		return this.numeroDisponivel.toString();
	}

	// Funï¿½ï¿½o que criar a lista COR X Tamanho para inserir o cï¿½digo de barras
	public void geraListaBarras(){
		try {
			boolean permiteInserirCor = true;
			boolean permiteInserirTamanho = true;
			boolean permiteInserirCorETamanho = true;
			BarrasEstoque barraGerada = new BarrasEstoque();
			System.out.println("cores Source :" + cores.getSource().size() + " Tamanhos:" + tamanhos.getSource().size());
			System.out.println("cores Target :" + cores.getTarget().size() + " Tamanhos:" + tamanhos.getTarget().size());
			int i = 1;

			if (this.produto.getTipoEstoque().equals(TipoControleEstoque.BA)){
				if (this.barrasUni.getBarras() == null) {
					throw new BarrasException(this.translate("barrasException.barrasIsNull"));
				}else {
					this.barrasUni.setBarras(barrasUtil.retornaBarrasComDigitoValido(this.barrasUni.getBarras()));
					if (barrasUtil.validaCodigo(this.barrasUni.getBarras())){
						barraGerada = new BarrasEstoque();
						barraGerada.setBarras(this.barrasUni.getBarras());
						this.barrasUni = new BarrasEstoque();
						if (!barrasJaExiste(barraGerada.getBarras())){
							barraGerada.setNumReg(geraNumReg());
							//					this.listaBarras.add(barraGerada);lista
							this.produto.getListaBarras().add(barraGerada);
						}else{
							this.addError(true, "barras.jaexiste", barraGerada.getBarras());
						}
					}else{
						this.addError(true, "barras.error.quantidade", this.barrasUni.getBarras());
					}
				}
			}else{
//				for (BarrasEstoque barras : this.produto.getListaBarras()) {
//					for (Cor cor : this.cores.getTarget()) {
//						if (barras.getCor().equals(cor)){
//							for (Tamanho tamanho : tamanhos.getTarget()) {
//								if (barras.getTamanho().equals(tamanho)){
//									permiteInserirCorETamanho = false;
//									permiteInserirTamanho = false;
//								}
//							}
//							permiteInserirCor = false;
//						}
//					}
//				}
				
				Set<Cor> coresSel = new HashSet<>(
				        Optional.ofNullable(this.cores.getTarget())
				                .orElse(Collections.emptyList()));

				Set<Tamanho> tamanhosSel = new HashSet<>(
				        Optional.ofNullable(tamanhos.getTarget())
				                .orElse(Collections.emptyList()));

				List<BarrasEstoque> barras = Optional.ofNullable(this.produto.getListaBarras())
				        .orElse(Collections.emptyList());

				for (BarrasEstoque b : barras) {
				    // Se a cor do barras está entre as cores selecionadas, já não pode inserir cor
				    if (coresSel.contains(b.getCor())) {
				        permiteInserirCor = false;

				        // E se, além disso, o tamanho do barras também está selecionado,
				        // então não pode inserir tamanho e nem a combinação cor+tamanho
				        if (tamanhosSel.contains(b.getTamanho())) {
				            permiteInserirTamanho = false;
				            permiteInserirCorETamanho = false;
				            break; // já sabemos que todos ficaram false; podemos sair cedo
				        }
				    }
				}

				if (!(this.cores.getTarget().isEmpty()) && !(this.tamanhos.getTarget().isEmpty())){
					if (permiteInserirCorETamanho){
						System.out.println("tudo preenchido");
						for (Tamanho tamanho : this.tamanhos.getTarget()) {
							for (Cor cor : this.cores.getTarget()) {
								barraGerada = new BarrasEstoque();
								barraGerada.setTamanho(tamanho);
								barraGerada.setCor(cor);
								barraGerada.setNumReg(""+i);
								//							this.listaBarras.add(barraGerada);
								this.produto.getListaBarras().add(barraGerada);
								i++;
							}
						}
					}else{
						if (!permiteInserirCor){
							this.addError(true, "barras.jaExisteCorNaLista");
						}else{
							if (!permiteInserirTamanho){
								this.addError(true, "barras.jaExisteTamanhoNaLista");
							}
						}
					}
				}else {
					if (!(this.cores.getTarget().isEmpty()) && (this.tamanhos.getTarget().isEmpty())){
						if (permiteInserirCor){
							System.out.println("cor preenchido");
							for (Cor cor : cores.getTarget()) {
								barraGerada = new BarrasEstoque();
								barraGerada.setCor(cor);
								barraGerada.setNumReg(""+i);
								//							this.listaBarras.add(barraGerada);
								this.produto.getListaBarras().add(barraGerada);
								i++;
							}
						}else{
							this.addError(true, "barras.jaExisteCorNaLista");
						}
					}else{
						if ((this.cores.getTarget().isEmpty()) && !(this.tamanhos.getTarget().isEmpty())){
							if (permiteInserirTamanho){
								System.out.println("tamanho preenchido");
								for (Tamanho tamanho : tamanhos.getTarget()) {
									barraGerada = new BarrasEstoque();
									barraGerada.setTamanho(tamanho);
									barraGerada.setNumReg(""+i);
									//								this.listaBarras.add(barraGerada);
									this.produto.getListaBarras().add(barraGerada);
									i++;
								}
							}else{
								this.addError(true, "barras.jaExisteTamanhoNaLista");
							}
						}
						if (cores.getTarget().isEmpty() && tamanhos.getTarget().isEmpty()){
							System.out.println("tudo vazio");
							this.addInfo(true, "list.empty.corTamanho");
						}
					}
				}
			}
		}catch (BarrasException b) {
			// TODO: handle exception
			this.addError(true, "caixa.error",b.getMessage());
		}
	}

	public void limpaGeraBarras(){
		this.listaBarras = new ArrayList<>();
		this.produto.setListaBarras(this.listaBarras); // novo
		this.corSource = this.corDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		this.tamanhoSource = this.tamanhoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		this.corTarget = new ArrayList<Cor>();
		this.tamanhoTarget = new ArrayList<Tamanho>();
		this.cores = new DualListModel<Cor>(corSource,corTarget);
		this.tamanhos = new DualListModel<Tamanho>(tamanhoSource,tamanhoTarget);
	}
	public void onCellEdit(CellEditEvent event) {
		try {
			boolean permiteBarras = true;
			int codigoRepetido = 0;
			Object newValue =   event.getNewValue();
			String barraDigito = barrasUtil.retornaBarrasComDigitoValido(newValue.toString());
			if (this.barrasDao.encontraBarrasPorEmpresa(barraDigito, this.getUsuarioAutenticado().getIdEmpresa(), pegaIdFilial()) != null){
				System.out.println("Dentro do cell edit permite barras = null");
				permiteBarras = false;
			}
			for (BarrasEstoque barra: this.produto.getListaBarras()){
				if (barra.getBarras() != null) {
					if (barraDigito.equals(barra.getBarras())) {
						codigoRepetido++;
					}
				}
			}
			if (codigoRepetido > 0){
				permiteBarras = false;
			}
			if (!permiteBarras){
				this.produto.getListaBarras().get(event.getRowIndex()).setBarras("");
				this.addError(true, "barras.jaexiste", newValue.toString());
			}else{
				this.produto.getListaBarras().get(event.getRowIndex()).setBarras(barrasUtil.retornaBarrasComDigitoValido(newValue.toString()));
				this.addInfo(true, "barras.ok", newValue.toString());
			}
		}catch(IllegalArgumentException i) {
			this.addError(true,i.getMessage());
		}catch(Exception e) {
			this.addError(true, e.getMessage());
		}
	}
	/*
	 * Funï¿½ï¿½o que verifica no momento que o usuario digita o codigo de barras se jï¿½ existe na base de dados 
	 * 
	 */
	public void barrasExiste(FacesContext context, UIComponent component, Object value) {
		String consulta = (String) value;
		boolean resultado = true;
		int codigoRepetido = 0;
		logger.debug("\n\t ##valor=" + value + "##\n\t");
		if (consulta != null){
			if (this.barrasDao.encontraBarrasPorEmpresa(consulta, this.getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial()) != null){
				System.out.println("Dentro do cell edit permite barras = null");
				resultado = true;
			}else{
				if (!this.produto.getListaBarras().isEmpty()){
					for (BarrasEstoque barra: this.produto.getListaBarras()){
						if (consulta.equals(barra.getBarras())){
							codigoRepetido++;
						}
					}
					if (codigoRepetido > 0){
						resultado= true;
					}else{
						resultado = false;
					}
				}else{
					resultado = false;
				}
			}
			if(resultado){
				this.addError(true, "barras.jaexiste", consulta);
			}else{
				this.addInfo(true, "barras.ok", consulta);
			}
		}
	}

	/*
	 * Funï¿½ï¿½o que verifica no momento que o usuario digita o codigo de barras se jï¿½ existe na base de dados 
	 * 
	 */
	public boolean barrasJaExiste(String value) {
		String consulta = value;
		boolean resultado = true;
		int codigoRepetido = 0;
		logger.debug("\n\t ##valor=" + consulta + "##\n\t");
		if (consulta != null){
			if (this.barrasDao.encontraBarrasPorEmpresa(consulta, this.getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial()) != null){
				System.out.println("Dentro do cell edit permite barras = null");
				resultado = true;
			}else{
				if (this.produto.getListaBarras().size() != 0){
					for (BarrasEstoque barra: this.produto.getListaBarras()){
						if (consulta.equals(barra.getBarras())){
							codigoRepetido++;
						}
					}
					if (codigoRepetido > 0){
						resultado= true;
					}else{
						resultado = false;
					}
				}else{
					resultado = false;
				}
			}
		}
		return resultado;
	}

	public void testaCodigo(){		
		int digito = 0;
		String valido="";
		System.out.println("bla:"+this.barrasUni.getBarras());
		if (barrasUni.getBarras().length() == 12){
			digito = barrasUtil.retornaDigito(this.barrasUni.getBarras());
			System.out.println("Digito = " + digito);
		}
		if (barrasUni.getBarras().length() == 13){
			valido =  (barrasUtil.validaDigito(this.barrasUni.getBarras()) ? "válido": "inválido");
			System.out.println(valido);
		}
	}

	public void excluiBarras(BarrasEstoque item){
		if (!this.produto.getListaBarras().isEmpty() && item != null) {
			this.produto.getListaBarras().remove(item);
		}
		if (item.getId() != null) {
			this.temBarrasExcluir = true;
			this.listaBarrasExcluir.add(item);
		}
	}

	public TipoControleEstoque[] getTipoControleEstoque(){
		return TipoControleEstoque.values();
	}

	public void setaTipoControleEstoque(){
		if (this.produto.getTipoEstoque() != null){
			if (this.produto.getTipoEstoque().equals(TipoControleEstoque.BA)){
				this.setBarrasVisivel(true);
				this.setCorVisivel(false);
				this.setTamanhoVisivel(false);
			}else if (this.produto.getTipoEstoque().equals(TipoControleEstoque.MO)){
				this.setBarrasVisivel(false);
				this.setCorVisivel(false);
				this.setTamanhoVisivel(true);
			}else{
				this.setBarrasVisivel(false);
				this.setCorVisivel(true);
				this.setTamanhoVisivel(true);
			}
			System.out.println("barras: "+this.isBarrasVisivel() + "COR: "+this.isCorVisivel() + "Tamanho: " + this.isTamanhoVisivel() );
		}
	}
	
	public BigDecimal calculaVenda(BigDecimal custo, BigDecimal margem) {
		BigDecimal resultado = new BigDecimal("0",mc);
		resultado = custo.add(custo.multiply(margem.divide(new BigDecimal("100"),mc)));
		return resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	public BigDecimal calculaMargem(BigDecimal custo, BigDecimal venda) {
		BigDecimal resultado = new BigDecimal("0",mc);
		resultado = (venda.divide(custo,mc)).subtract(new BigDecimal("1")).multiply(new BigDecimal("100"),mc);
		return resultado.setScale(2,RoundingMode.HALF_EVEN);
	}
	
	// Calculo de Valor de venda
	public void calculaVendaA(){		
		if (this.custoProduto.getCustoMedio().compareTo(new BigDecimal("0"))<=0) {
			this.custoProduto.setPreco1(calculaVenda(this.custoProduto.getCusto(), this.margemA));
		}else {
			this.custoProduto.setPreco1(calculaVenda(this.custoProduto.getCustoMedio(), this.margemA));
		}
	}
	public void calculaVendaB(){
		if (this.custoProduto.getCustoMedio().compareTo(new BigDecimal("0"))<=0) {
			this.custoProduto.setPreco2(calculaVenda(this.custoProduto.getCusto(), this.margemB));
		}else {
			this.custoProduto.setPreco2(calculaVenda(this.custoProduto.getCustoMedio(), this.margemB));
		}
	}
	public void calculaVendaC(){
		if (this.custoProduto.getCustoMedio().compareTo(new BigDecimal("0"))<=0) {
			this.custoProduto.setPreco3(calculaVenda(this.custoProduto.getCusto(), this.margemC));
		}else {
			this.custoProduto.setPreco3(calculaVenda(this.custoProduto.getCustoMedio(), this.margemC));
		}
	}
	public void calculaVendaD(){
		if (this.custoProduto.getCustoMedio().compareTo(new BigDecimal("0"))<=0) {
			this.custoProduto.setPreco4(calculaVenda(this.custoProduto.getCusto(), this.margemD));
		}else {
			this.custoProduto.setPreco4(calculaVenda(this.custoProduto.getCustoMedio(), this.margemD));
		}
	}
	public void calculaVendaE(){
		if (this.custoProduto.getCustoMedio().compareTo(new BigDecimal("0"))<=0) {
			this.custoProduto.setPreco5(calculaVenda(this.custoProduto.getCusto(), this.margemE));
		}else {
			this.custoProduto.setPreco5(calculaVenda(this.custoProduto.getCustoMedio(), this.margemE));
		}
	}
	
	public void calculaVenda() {
		try {
			if (this.custoProduto.getCusto() != null) {
				if (this.custoProduto.getCusto().compareTo(new BigDecimal("0")) == 1) {
					if (this.custoProduto.getPreco1() != null) {
						if (this.margemA.compareTo(new BigDecimal("0"))==1) {
							this.calculaVendaA();
						}
					}
					if (this.custoProduto.getPreco2() != null) {
						if (this.margemB.compareTo(new BigDecimal("0"))==1) {
							this.calculaVendaB();
						}
					}
					if (this.custoProduto.getPreco3() != null) {
						if (this.margemC.compareTo(new BigDecimal("0"))==1) {
							this.calculaVendaC();
						}
					}
					if (this.custoProduto.getPreco4() != null) {
						if (this.margemD.compareTo(new BigDecimal("0"))==1) {
							this.calculaVendaD();
						}
					}
					if (this.custoProduto.getPreco5() != null ) {
						if (this.margemE.compareTo(new BigDecimal("0"))==1) {
							this.calculaVendaE();
						}
					}
				}else {
					throw new NullPointerException(this.translate("price.custo.equal.zero"));
				}
			}else {
				throw new NullPointerException(this.translate("price.custo.equal.null"));
			}
				
		}catch (NullPointerException n) {
			// TODO: handle exception
			this.addError(true, "caixa.error", n.getMessage());
			
		}
	}
	
	
	
	
	//Calculo Margem de Lucro
	public void calculaMargemA() {
		this.margemA = calculaMargem(this.custoProduto.getCusto(), this.custoProduto.getPreco1());
	}
	public void calculaMargemB() {
		this.margemB = calculaMargem(this.custoProduto.getCusto(), this.custoProduto.getPreco2());
	}
	public void calculaMargemC() {
		this.margemC = calculaMargem(this.custoProduto.getCusto(), this.custoProduto.getPreco3());
	}
	public void calculaMargemD() {
		this.margemD = calculaMargem(this.custoProduto.getCusto(), this.custoProduto.getPreco4());
	}
	public void calculaMargemE() {
		this.margemE = calculaMargem(this.custoProduto.getCusto(), this.custoProduto.getPreco5());
	}
	
	public void calculaMargem() {
		try {
			if (this.custoProduto.getCusto() != null) {
				if (this.custoProduto.getCusto().compareTo(new BigDecimal("0")) == 1) {
					if (this.custoProduto.getPreco1() != null) {
						if (this.custoProduto.getPreco1().compareTo(new BigDecimal("0"))==1) {
							this.calculaMargemA();
						}
					}
					if (this.custoProduto.getPreco2() != null) {
						if (this.custoProduto.getPreco2().compareTo(new BigDecimal("0"))==1) {
							this.calculaMargemB();
						}
					}
					if (this.custoProduto.getPreco3() != null) {
						if (this.custoProduto.getPreco3().compareTo(new BigDecimal("0"))==1) {
							this.calculaMargemC();
						}
					}
					if (this.custoProduto.getPreco4() != null) {
						if (this.custoProduto.getPreco4().compareTo(new BigDecimal("0"))==1) {
							this.calculaMargemD();
						}
					}
					if (this.custoProduto.getPreco5() != null ) {
						if (this.custoProduto.getPreco5().compareTo(new BigDecimal("0"))==1) {
							this.calculaMargemE();
						}
					}
				}else {
					throw new NullPointerException(this.translate("price.custo.equal.zero"));
				}
			}else {
				throw new NullPointerException(this.translate("price.custo.equal.null"));
			}
				
		}catch (NullPointerException n) {
			// TODO: handle exception
			this.addError(true, "caixa.error", n.getMessage());
			
		}
	}
	
	/**
	 *  Inicio Tudo referente a Fabrica
	 */
	
	/**
	 * Inicializaï¿½ï¿½o da pagina em modo listagem
	 */
	public void initializeListingMaterial() {
		this.viewState = ViewState.LISTING;
		this.produtoModel = getLazyMaterial();
	}
	
	/**
	 * Inicialização da pagina em modo de Adição ou Edição
	 * @param idProduto
	 */
	public void initializeFormFabrica(Long idProduto) {
		this.listaTributosAtivos = this.tributoDao.listaTodosTributosAtivos(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		this.corSource = this.corDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
		this.tamanhoSource = this.tamanhoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
		this.corTarget = new ArrayList<Cor>();
		this.tamanhoTarget = new ArrayList<Tamanho>();
		this.cores = new DualListModel<Cor>(corSource,corTarget);
		this.tamanhos = new DualListModel<Tamanho>(tamanhoSource,tamanhoTarget);
		if (idProduto == null) {
			this.viewState = ViewState.ADDING;
			this.produto  = new Produto();
			this.produto.setTipoEstoque(TipoControleEstoque.BA);
			this.fornecedor = new Fornecedor();
			this.selectFornecedor = new Fornecedor();
			this.custoProduto = new ProdutoCusto();
//			this.estoque = new ProdutoEstoque();
			//			this.departamento = new Departamento();
			//			this.secao = new Secao();
			//			this.subSecao = new SubSecao();
			//			this.fabricante = new Fabricante();
		} else {
			this.viewState = ViewState.EDITING;
					setVisivelPorIdProduto(true);
			this.produto = this.produtoDao.pegaProdutoComFornecedores(idProduto, this.pegaIdEmpresa(),this.pegaIdFilial());
			List<BarrasEstoque>listaTempBarras = new ArrayList<>(); 
			listaTempBarras = this.barrasDao.listaBarrasPorProduto(this.produto, this.pegaIdEmpresa(), this.pegaIdFilial());
			if (listaTempBarras == null){
				this.produto.setListaBarras(new ArrayList<>());
				this.setListaBarras(new ArrayList<>());
			}else{
				this.produto.setListaBarras(listaTempBarras);
				this.setListaBarras(listaTempBarras);
			}
			//			this.produto.setListaBarras(this.barrasDao.listaBarrasPorProduto(this.produto, this.pegaIdEmpresa()));
//			this.estoque = new ProdutoEstoque();
			setaCusto();
			//			this.estoque = this.produto.getEstoqueGeral();
			this.listaTemporaria = this.produto.getFornecedores();
			this.barrasUni = new BarrasEstoque();
			setaTipoControleEstoque();
			// atualizando as margens;
			this.calculaMargem();

		}
	}
	
	/**
	 * Gera a lista de materiais em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Produto> getLazyMaterial(){
		this.produtoModel = new AbstractLazyModel<Produto>() {

			/**
			 *
			 */
			private static final long serialVersionUID = -5197500519977819243L;

			@Override
			public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<Produto> page = produtoDao.pegaPageMaterialComEstoqueComCustoComFiltro(isDeleted,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest,null,null);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = produtoDao.pegaPageMaterialComEstoqueComCustoComFiltro(isDeleted,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
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
	 * redireciona para Cadastramento de novo produto / ediï¿½ï¿½o de produto jï¿½ cadastrado
	 * @return pagina de ediï¿½ï¿½o/inclusao de produto
	 */
	public String newMaterial() {
		return "formCadMaterial.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListMaterial() {
		return "formListMaterial.xhtml?faces-redirect=true";
	}
	
	/**
	 * redireciona para a pagina com o ID do produto a ser editado
	 * 
	 * @param produtoID
	 * 
	 * @return
	 */
	public String changeToEditMaterial(Long produtoID) {
		return "formCadMaterial.xhtml?faces-redirect=true&materialID=" + produtoID;
	}
	
}
