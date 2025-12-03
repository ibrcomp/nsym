package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.apache.tools.ant.taskdefs.condition.Os;
import org.hibernate.HibernateException;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.FabricaException;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.Secao;
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.entity.estoque.Estoque;
import br.com.nsym.domain.model.entity.fabrica.Cortador;
import br.com.nsym.domain.model.entity.fabrica.FichaTecnica;
import br.com.nsym.domain.model.entity.fabrica.ItemFichaTecnica;
import br.com.nsym.domain.model.entity.fabrica.MaterialModelo;
import br.com.nsym.domain.model.entity.fabrica.Modelo;
import br.com.nsym.domain.model.entity.fabrica.Producao;
import br.com.nsym.domain.model.entity.fabrica.Risco;
import br.com.nsym.domain.model.entity.fabrica.Servico;
import br.com.nsym.domain.model.entity.fabrica.util.GradeProducao;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.OsFt;
import br.com.nsym.domain.model.entity.fabrica.util.SequenciaLinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.StatusAndamento;
import br.com.nsym.domain.model.entity.fabrica.util.TipoEnfesto;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.tools.Finalidade;
import br.com.nsym.domain.model.entity.tools.TipoControleEstoque;
import br.com.nsym.domain.model.entity.tools.TipoMedida;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.cadastro.SecaoRepository;
import br.com.nsym.domain.model.repository.cadastro.TamanhoRepository;
import br.com.nsym.domain.model.repository.fabrica.CortadorRepository;
import br.com.nsym.domain.model.repository.fabrica.FichaTecnicaRepository;
import br.com.nsym.domain.model.repository.fabrica.GradeProducaoRepository;
import br.com.nsym.domain.model.repository.fabrica.ItemFichaTecnicaRepository;
import br.com.nsym.domain.model.repository.fabrica.LinhaProducaoRepository;
import br.com.nsym.domain.model.repository.fabrica.MaterialModeloRepository;
import br.com.nsym.domain.model.repository.fabrica.ModeloRepository;
import br.com.nsym.domain.model.repository.fabrica.ProducaoRepository;
import br.com.nsym.domain.model.repository.fabrica.RiscoRepository;
import br.com.nsym.domain.model.repository.fabrica.ServicoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FabricaBean extends AbstractBeanEmpDS<Produto> {

	/**
	 *
	 */
	private static final long serialVersionUID = 4955429729082588847L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	private Produto produto;
	
	@Inject
	private ProdutoRepository produtoDao;
	
	@Getter
	@Setter
	private Modelo modelo;
	
	@Getter
	@Setter
	private MaterialModelo materialModelo;
	
	@Inject
	private MaterialModeloRepository materialModeloDao;
	
	@Getter
	@Setter
	private List<MaterialModelo> listaDeMateriaisParaExcluir = new ArrayList<>();
	
	@Getter
	@Setter
	private List<MaterialModelo> listaDeMateriaisParaFichaTecnica = new ArrayList<>();
	
	@Inject
	private ModeloRepository modeloDao;
	
	@Getter
	private AbstractLazyModel<Modelo> modeloModel;
	
	@Getter
	private AbstractLazyModel<Produto> produtoModel;
	
	@Getter
	@Setter
	private String ref;
	
	@Getter
	@Setter
	private BigDecimal quantidade = new BigDecimal("0");
	
	@Getter
	@Setter
	private  DualListModel<Tamanho> tamanhos ;
	
	@Getter
	@Setter
	private List<Tamanho> listaDeTamanhos = new ArrayList<>();
	
	@Getter
	@Setter
	private  List<Tamanho> listaDeTamanhosSelecionados = new ArrayList<>();
	
	
	@Inject
	private TamanhoRepository tamanhoDao;
	
	@Inject
	private SecaoRepository secaoDao; 
	
	/**
	 * Inicio variaveis Producao
	 */
	
	@Getter
	private AbstractLazyModel<Producao> producaoModel;
	
	@Getter
	@Setter
	private Producao producao;
	
	@Inject
	private ProducaoRepository producaoDao;
	
	@Getter
	@Setter
	private transient List<Tamanho> listaTamanhoTemp = new ArrayList<>();
	
	@Getter
	@Setter
	private List<GradeProducao> listaGrade = new ArrayList<>();
	
	@Getter
	@Setter
	private List<GradeProducao> listaGradeRemover = new ArrayList<>();
	
	@Inject
	private GradeProducaoRepository gradeDao;
	
	@Getter
	@Setter
	private FichaTecnica ficha;
	
	@Inject
	private FichaTecnicaRepository fichaDao;
	
	@Getter
	@Setter
	private  List<FichaTecnica> listaFichaTecnica = new ArrayList<>();
	
	@Inject
	private LinhaProducaoRepository linhaDao;
	
	@Getter
	@Setter
	private transient BigDecimal totalGradeTemp = new BigDecimal("0");
	
	@Getter
	@Setter
	private ItemFichaTecnica itemFicha = new ItemFichaTecnica();
	
	@Inject
	private ItemFichaTecnicaRepository itemFichaDao;
	
	@Getter
	@Setter
	private List<ItemFichaTecnica> listaItemPedidoExcluir = new ArrayList<ItemFichaTecnica>();
	
	@Setter
	private AbstractDataModel<ItemFichaTecnica> listaItemModel; 
	
	@Getter
	@Setter
	private BarrasEstoque barras = new BarrasEstoque();
	
	@Inject
	private BarrasEstoqueRepository barrasDao;

	// Utilitario com todas as funï¿½ï¿½es de estoque
	@Inject
	private EstoqueUtil estoqueUtil;

	@Getter
	@Setter
	private EmpUser empresaUsuario;
	
	@Getter
	@Setter
	private List<BarrasEstoque> listaBarras = new ArrayList<>();
	
	@Getter
	@Setter
	@Transient
	private List<BarrasEstoque> listaBarrasTemp = new ArrayList<>(); 
	
	@Getter
	@Setter
	private Configuration configUser;
	
	@Getter
	@Setter
	private boolean produtoVisivel = false;
	
	@Getter
	@Setter
	private ProdutoCusto custoProduto ;
	
	@Getter
	@Setter
	private Cortador cortador ;
	
	@Getter
	private AbstractLazyModel<Cortador> cortadorModel ; 
	
	@Inject
	private CortadorRepository cortadorDAO;
	
	@Getter
	@Setter
	private Risco risco ;
	
	@Getter
	private AbstractLazyModel<Risco> riscoModel ; 
	
	@Inject
	private RiscoRepository riscoDAO;
	
	@Getter
	@Setter
	private Servico servico ;
	
	@Getter
	private AbstractLazyModel<Servico> servicoModel ; 
	
	@Inject
	private ServicoRepository servicoDao;
	
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

	@PostConstruct
	public void init(){
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}
	
/**
 * Inicio classe Modelo
 */
	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.modeloModel = getModeloModelLazy();
		
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		try {
			this.materialModelo = new MaterialModelo();
			this.produto = new Produto();
			this.ref = "";
			this.quantidade = new BigDecimal("0");
			this.listaDeTamanhos = this.tamanhoDao.listaPorFilial(pegaIdEmpresa(), null);
			if (id == null) {
				this.viewState = ViewState.ADDING;
				this.modelo = new Modelo();
				this.tamanhos = new DualListModel<Tamanho>(this.listaDeTamanhos,new  ArrayList<>());
			}else {
				this.viewState = ViewState.EDITING;
				this.modelo = this.modeloDao.pegaModeloPorID(false,id,pegaIdEmpresa(),pegaIdFilial(),true);
				this.listaDeTamanhosSelecionados =  this.modeloDao.pegaTamanhosDisponiveisPorModelo(false,id,pegaIdEmpresa(),pegaIdFilial(),true);
				this.modelo.setTamanhosDisponiveis(this.listaDeTamanhosSelecionados);
				for (Tamanho tamanho : this.listaDeTamanhosSelecionados) {
					this.listaDeTamanhos.remove(tamanho);
				}
				this.tamanhos = new DualListModel<Tamanho>(this.listaDeTamanhos,this.listaDeTamanhosSelecionados);
			}
		}catch(NoResultException nr) {
			this.addError(true,"hibernate.noResult",nr.getMessage());
		}catch(Exception e) {
			this.addError(true,"exception.error.fatal",e.getMessage());
		}
		
	}
	
	public AbstractLazyModel<Modelo> getModeloModelLazy(){

		this.modeloModel = new AbstractLazyModel<Modelo>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 8205337253783791182L;

			@Override
			public List<Modelo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "vencimento")
				.withDirection(sortOrder.name());
				Page<Modelo> page = new Page<Modelo>();
				page = modeloDao.listaLazyComFiltro(false, false, pegaIdEmpresa(), pegaIdFilial(), pageRequest, null, null, true);
								if (filters != null){
									for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
										try{
											String filterProperty = it.next();
											Object filterValue = filters.get(filterProperty);
											page = modeloDao.listaLazyComFiltro(false,false,pegaIdEmpresa(), pegaIdFilial(), pageRequest,filterProperty, filterValue.toString(),true);
										} catch(Exception e) {
											System.out.println(e.getMessage());
										}
									}
								}
				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return modeloModel;
	}

	public void onRowSelect(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.modelo = (Modelo)event.getObject();
	}
	
	/**
	 * redireciona para a pagina com o ID do Titulo.
	 * 
	 * @param recID
	 * 
	 * @returna pagina de edicao de titulo
	 */
	public String changeToEditModelo(Long modId) {
		return "formCadModelos.xhtml?faces-redirect=true&modId=" + modId;
	}
	
	public String toListModelo() {
		return "formListModelos.xhtml?faces-redirect=true";
	}
	
	public String newModelo() {
		return "formCadModelos.xhtml?faces-redirect=true";
	}
	/**toTela
	 * Exibe o dialog com a lista de materiais
	 */
	public void telaListaMaterial(){
		this.produtoModel = getLazyMaterial();
		this.updateAndOpenDialog("PesquisaMaterialFabricaDialog", "dialogPesquisaMaterialFabrica");
	}
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Produto> getLazyMaterial(){
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
				
				Page<Produto> page = produtoDao.pegaPageMaterialComEstoqueComCustoComFiltro(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest,null,null);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = produtoDao.pegaPageMaterialComEstoqueComCustoComFiltro(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
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
	
	public void onRowSelectMaterial(SelectEvent event)throws IOException{
		
		this.produto = (Produto)event.getObject();
		this.ref = this.produto.getReferencia();
	}
	
	@Transactional
	public void excluiItem(MaterialModelo itemSelect){
		try{
			this.materialModelo = itemSelect;
			boolean permiteRemover = false;
			System.out.println("inico da exclusao do item");
			if (this.modelo.getListaDeMaterias().size() >1 ) {
				permiteRemover = true;
			}else {
				if (this.viewState == ViewState.ADDING) {
					permiteRemover = true;
				}
			}
			if (permiteRemover) {
				if (!this.modelo.getListaDeMaterias().isEmpty() && this.materialModelo != null ){
					this.modelo.getListaDeMaterias().remove(itemSelect);
					if (this.viewState == ViewState.EDITING) {
						if (itemSelect.getId() != null) {
							System.out.println("Estou no Exclui Item devolvendo a quantidade do item para o estoque! item ID: " + itemSelect.getId());
							this.listaDeMateriaisParaExcluir.add(itemSelect);	teste					
						}
					}
					//				if (this.formaPag != null) {
					//					preencheParcelamento();
					//				}
				}
				if (itemSelect.getModelo() != null) {
					this.modelo = this.modeloDao.save(this.modelo);
					initializeForm(this.modelo.getId());
				}
				this.modeloModel = getModeloModelLazy();
				this.addWarning(true, "cfe.list.delete", itemSelect.getProduto().getReferencia());
			}else {
				throw new IllegalAccessException(this.translate("pedidoException.excluiItem.minimo"));
			}

		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}
	
	/**
	 * Método que insere os materiais necessarios para a confecção do modelo
	 */
	public void insereMaterial() {
		try {
			if (this.produto == null || this.produto.getId() == null) {
				if (this.ref != null || !this.ref.isEmpty() ) {
					this.produto = this.produtoDao.pegaMaterialRef(this.ref,pegaIdEmpresa(), pegaIdFilial());
					if (this.produto == null || this.produto.getId() == null) {
						throw new FabricaException(this.translate("fabrica.material.notFound")+ this.ref);
					}
				}
			}else {
				if (this.ref.compareTo(this.produto.getReferencia()) != 0) {
					this.produto = this.produtoDao.pegaMaterialRef(this.ref,pegaIdEmpresa(), pegaIdFilial());
					if (this.produto == null || this.produto.getId() == null) {
						throw new FabricaException(this.translate("fabrica.material.notFound")+ this.ref);
					}
				}
				if (this.quantidade.compareTo(new BigDecimal("0"))<=0) {
					throw new FabricaException(this.translate("fabrica.material.amountInvalid"));
				}
			}
			this.materialModelo.setProduto(this.produto);
			this.materialModelo.setQuant(this.quantidade);
			this.modelo.getListaDeMaterias().add(this.materialModelo);
			this.materialModelo = new MaterialModelo();
			this.produto = new Produto();
			this.ref = "";
			this.quantidade = new BigDecimal("0");
		}catch(FabricaException f) {
			this.materialModelo = new MaterialModelo();
			this.produto = new Produto();
			this.ref = "";
			this.quantidade = new BigDecimal("0");
			this.addWarning(true,f.getMessage());
		}
	}
	/**
	 *  Lista do autocompletar Secao  
	 */
	public List<Secao> completaSecao(String query) { // Testar!!!!!!!

		List<Secao> fontePesquisa = this.secaoDao.pesquisaTexto(query, pegaIdEmpresa());

		return fontePesquisa;
	}
	
	@Transactional
	public void doSalvar() {
		try {
			if (this.modelo.getId() == null) {
				this.modelo.setTamanhosDisponiveis(this.tamanhos.getTarget());
				for (MaterialModelo  material : this.modelo.getListaDeMaterias()) {
					material.setModelo(this.modelo);
				}
			}else {
				this.modelo.setTamanhosDisponiveis(new ArrayList<>());
				this.modelo = this.modeloDao.save(this.modelo);
				for (MaterialModelo  material : this.modelo.getListaDeMaterias()) {
					material.setModelo(this.modelo);
				}
				this.modelo.setTamanhosDisponiveis(this.tamanhos.getTarget());
			}	
			this.modelo = this.modeloDao.save(this.modelo);
			this.addInfo(true,"save.sucess",this.modelo.getDescricao());
		}catch(HibernateException h) {
			this.addError(true,"hibernate.persist.fail",h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal",e.getMessage());
		}
	}
/**
 * Inicio Módulo Producao	
 */
	public void initializeListingProducao() {
		this.viewState = ViewState.LISTING;
		this.producaoModel = getProducaoLazyModel();
	}
	
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Producao> getProducaoLazyModel(){
		this.producaoModel = new AbstractLazyModel<Producao>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3097078596870201247L;

			@Override
			public List<Producao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());
				
				Page<Producao> page = producaoDao.listaLazyComFiltro(false, false, pegaIdEmpresa(), pegaIdFilial(), pageRequest, null, null, true);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = producaoDao.listaLazyComFiltro(false, false, pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty , filterValue.toString().toUpperCase(), true);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return producaoModel;
	}
	

	/** 
	 * Os redirecionamento para Edição
	 * @param modId
	 * @return
	 */
	public String changeToEditOP(Long opID) {
		return "formCadProducao.xhtml?faces-redirect=true&opID=" + opID;
	}
	
	public String toListOP() {
		return "formListProducao.xhtml?faces-redirect=true";
	}
	
	public String toListFichaTecnica(Long id) {
		return "formListFichaTecnica.xhtml?faces-redirect=true&opID=" + id;
	}
	
	public String newFichaTecnica(Long id) {
		
		return "formFichaTecnica.xhtml?faces-redirect=true&opID="+id+"&fichaID=" + null;
	}
	
	public String newOP() {
		return "formCadProducao.xhtml?faces-redirect=true";
	}
	
	/**
	 * RowSelect para Producao
	 */
	public void onRowSelectOP(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.producao = (Producao)event.getObject();
	}
	
	public void initializeFormOP(Long id) {
		try {
			
			if (id == null) {
				this.viewState = ViewState.ADDING;
				this.producao = new Producao();
				this.modelo = new Modelo();
				this.listaGrade = new ArrayList<>();
				this.totalGradeTemp = new BigDecimal("0");
				this.produto = new Produto();
				this.custoProduto = new ProdutoCusto();
			}else {
				this.viewState = ViewState.EDITING;
				this.producao = this.producaoDao.pegaProducaoComGradePreenchida(false, id, pegaIdEmpresa(), pegaIdFilial(), true);
				if (this.producao.getProduto() != null) {
					this.produto = this.produtoDao.pegaProdutoID(this.producao.getProduto().getId(), pegaIdEmpresa(), pegaIdFilial());
					setaCusto();
				}else {
					this.produto = new Produto();
					this.custoProduto = new ProdutoCusto();
				}
				if (this.producao != null) {
					this.modelo = this.modeloDao.pegaModeloPorID(false, this.producao.getModelo().getId(), pegaIdEmpresa(), pegaIdFilial(), true);
					if (this.modelo == null) {
						this.modelo = new Modelo();
					}
					this.listaGrade = this.producao.getListaGrade();
					this.geraTotalGrade();
				}
			}
		}catch(NoResultException nr) {
			this.addError(true,"hibernate.noResult",nr.getMessage());
		}catch(Exception e) {
			this.addError(true,"exception.error.fatal",e.getMessage());
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
	
	public List<Modelo> listaModelos(){
		return this.modeloDao.pegaListaDeModelosComMateriais(false,pegaIdEmpresa(), pegaIdFilial(), true);
	}
	
	@Transactional
	public void pegaGradeRisco(){
		try {
			System.out.println("estou no pegaGradeRisco!!");
			if (this.risco.getId() != null) {
				if (this.risco.getModelo().equals(this.modelo)) {
					this.listaGrade = this.gradeDao.pegaGradeDisponiveisPorRisco(false, this.risco.getId(), pegaIdEmpresa(), pegaIdFilial(), true);
				}else {
					if (this.listaGradeRemover.size() == 0) {
						this.listaGradeRemover.addAll(this.listaGrade);
					}
					this.listaGrade =  new ArrayList<>();
					this.listaGrade = geraGrade();
				}
			}else {
				if (this.listaGrade.size() >0) {
					this.listaGrade =  new ArrayList<>();
				}
				this.listaGrade = geraGrade();
			}
			this.geraTotalGrade();
		}catch(FabricaException fb) {
			this.addError(true,fb.getMessage());
		}
	}
		@Transactional
	public List<GradeProducao> geraGrade() throws FabricaException{
		if (this.modelo != null ) {
			if (this.modelo.getId() != null) {
				listaTamanhoTemp = modeloDao.pegaTamanhosDisponiveisPorModelo(false, this.modelo.getId(), pegaIdEmpresa(), pegaIdFilial(), true);
			}
		}else {
			throw new FabricaException("Modelo nao selecionado!");
		}
		if (listaTamanhoTemp.size() >0) {
			GradeProducao gradeTemp = new GradeProducao();
			for (Tamanho tamanho : listaTamanhoTemp) {
				gradeTemp.setTamanho(tamanho);
				gradeTemp.setQuantidade(1l);
				this.listaGrade.add(gradeTemp);
				gradeTemp = new GradeProducao();
			}
		}
		System.out.println("tamaho da lista Grade = " + this.listaGrade.size());
		return this.listaGrade;
	}
	
	public void onCellEdit(CellEditEvent event) {
        Long oldValue = (Long)event.getOldValue();
        Long newValue = (Long)event.getNewValue();
         
        if(newValue != 0 && newValue != (oldValue)) {
        	this.listaGrade.get(event.getRowIndex()).setQuantidade(new BigDecimal(newValue).longValue());
        }
        this.geraTotalGrade();
    }
	
	@Transactional
	public void pegaGrade(){
		try {
			System.out.println("estou no pegaGrade!!");
			if (this.producao.getId() != null) {
				if (this.producao.getModelo().equals(this.modelo)) {
					this.listaGrade = this.gradeDao.pegaGradeDisponiveisPorProducao(false, this.producao.getId(), pegaIdEmpresa(), pegaIdFilial(), true);
				}else {
					if (this.listaGradeRemover.size() == 0) {
						this.listaGradeRemover.addAll(this.listaGrade);
					}
					this.listaGrade = geraGrade();
				}
			}else {
				System.out.println("modelo selecionado = "+ this.modelo.getDescricao()); 
				this.produto = this.produtoDao.pegaProdutoRef(this.modelo.getReferencia(), pegaIdEmpresa(), pegaIdFilial());
				setaCusto();
				if (this.produto == null) {
					this.produto = new Produto();
					this.produto.setReferencia(this.modelo.getReferencia());
				}
				this.listaGrade = geraGrade();
			}
			this.geraTotalGrade();
//			return this.listaGrade;
		}catch(FabricaException fb) {
			this.addError(true,fb.getMessage());
		}
	}
	
	@Transactional
	public void doSalvarOP() {
		try {
			if (this.producao.getId() == null) {
				this.producao.setModelo(this.modelo);
				this.producao.setRef(this.modelo.getReferencia());
				this.producao.setDescricao(this.modelo.getDescricao());
				for (GradeProducao gradeProducao : listaGrade) {
					gradeProducao.setProducao(this.producao);
				}
				this.producao.setListaGrade(this.listaGrade);
				List<MaterialModelo> listaTempMaterialModeloNewOP = new ArrayList<>();
				for (MaterialModelo materialModeloTemp : this.modelo.getListaDeMaterias()) {
					MaterialModelo mat = new MaterialModelo();
					mat.setDeleted(false);
					mat.setModelo(materialModeloTemp.getModelo());
					mat.setProducao(this.producao);
					mat.setProduto(materialModeloTemp.getProduto());
					mat.setQuant(materialModeloTemp.getQuant());
					mat.setPossuiFichaTecnicas(false);
					listaTempMaterialModeloNewOP.add(mat);
				}
//				 pegaSequenciaProducao(false, this.producao.getId(), pegaIdEmpresa(), null, false)
				for (SequenciaLinhaProducao etapa : this.linhaDao.pegaLinhaProducaoComSequencia(false, this.producao.getSequenciaProducao().getId(), pegaIdEmpresa(), null, false).getSequenciaProducao() ) {
					if (etapa.getIndice() == 1l) {
						this.producao.setEtapa(etapa.getEtapa());
						this.producao.setAndamento(StatusAndamento.AGU);
					}
				}
				this.producao.setListaDeMateriais(listaTempMaterialModeloNewOP);
			}else {
				if (this.listaGradeRemover.size() >0) {
					for (GradeProducao gradeRemover : listaGradeRemover) {
						gradeDao.delete(gradeRemover);
					}
				}
				for (GradeProducao gradeProducao : listaGrade) {
					gradeProducao.setProducao(this.producao);
				}
				this.producao.setModelo(this.modelo);
				this.producao.setListaGrade(this.listaGrade);
			}
			// cadastrando produto base para producao
			if (this.produto.getId()== null) {
				boolean refExistente = produtoDao.jaExiste(this.produto.getReferencia(), pegaIdEmpresa());
				if (refExistente == false) {
					this.produto.setModelo(this.modelo);
					this.custoProduto.setProduto(this.produto);
					this.produto.getListaCustoProduto().add(this.custoProduto);
					this.produto.setDeleted(false);
					this.produto.setFabricado(true);
					this.produto.setFinalidade(Finalidade.Rev);
					this.produto.setTipoMedida(TipoMedida.UN);
					this.produto.setTamanhos(this.listaDeTamanhosSelecionados);
					this.produto.setTipoEstoque(TipoControleEstoque.BA);;
					this.produto = this.produtoDao.save(this.produto);
					this.producao.setProduto(this.produto);
				}else {
					throw new HibernateException("Referencia ja existe no cadastro de produtos");
				}
			}
			this.producao.setProduto(this.produto);
			this.producao = this.producaoDao.save(this.producao);
			this.addInfo(true,"save.sucess",this.producao.getId());
		}catch(HibernateException h) {
			this.addError(true,"hibernate.persist.fail",h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal",e.getMessage());
		}
	}
	
	public void insereFichaTecncia(MaterialModelo mat) {
		this.materialModelo = mat;
		this.updateAndOpenDialog("fichaTecMaterialDialog", "dialogFichaTecMaterial");
	}
	
	public String exibeFichaTecnica(Long fichaID) {
		return "formFichaTecnica.xhtml?faces-redirect=true&fichaID=" + fichaID;
	}
	
	public String exibeOs(Long opID) {
		return "formOS.xhtml?faces-redirect=true&opID=" + opID;
	}
	
	public void initializeFichaTecnica(String idOPs, String idFicha) {
		try {
			this.listaFichaTecnica = new ArrayList<>();
			this.listaBarrasTemp = new ArrayList<>();
			this.barras = new BarrasEstoque();
			this.producao = new Producao();
			this.produto = new Produto();
			 if (!idFicha.contentEquals("null")) {
				Long idFichas = Long.valueOf(idFicha);
				this.viewState = ViewState.EDITING;
				this.ficha = this.fichaDao.pegaFichaTecnica(pegaIdEmpresa(),pegaIdFilial(),idFichas);
				if (this.ficha != null) {
					this.producao = this.producaoDao.pegaProducaoComGradePreenchida(false, this.ficha.getProducao().getId(), pegaIdEmpresa(), null, false);
					this.ficha.setItens(this.fichaDao.pegaItensFicha(false, this.ficha.getId(), pegaIdEmpresa(), null, false));
					if (this.producao != null) {
						this.modelo = this.modeloDao.pegaModeloPorID(false, this.producao.getModelo().getId(), pegaIdEmpresa(), pegaIdFilial(), true);
					}
					this.listaGrade =this.producao.getListaGrade();
					this.geraTotalGrade();
					if (this.ficha.getTotalCorte() == null) {
						this.ficha.setTotalCorte(new BigDecimal("0"));
					}
					if (this.ficha.getTotalCorte().compareTo(new BigDecimal("0"))<1) {
						this.geraTotalCorte();
					}
				}
			}else {
				this.viewState = ViewState.ADDING;
				this.materialModelo= new MaterialModelo();
				if (!idOPs.contentEquals("null")) {
					Long idOP = Long.valueOf(idOPs);
					this.producao = this.producaoDao.pegaProducaoComGradePreenchida(false, idOP, pegaIdEmpresa(), pegaIdFilial(), true);
					this.listaFichaTecnica = this.producaoDao.pegaListaDeFichasTecnicas(false, idOP, pegaIdEmpresa(), pegaIdFilial(), true);
					this.modelo = this.modeloDao.pegaModeloPorID(false, this.producao.getModelo().getId(), pegaIdEmpresa(), pegaIdFilial(), true);
				}
				this.ficha = new FichaTecnica();
				this.ficha.setProducao(this.producao);
				this.listaDeMateriaisParaFichaTecnica = this.producaoDao.pegaListaDeMateriaisSemFichasTecnicas(false, this.producao.getId(), pegaIdEmpresa(), pegaIdFilial(), true);
				if (this.listaDeMateriaisParaFichaTecnica.size()<1) {
					this.viewState = ViewState.DISABLED;
					throw new FabricaException (this.translate("fabricaException.ficha.complete"));
				}
				this.listaGrade = this.gradeDao.pegaGradeDisponiveisPorProducao(false, this.producao.getId(), pegaIdEmpresa(), pegaIdFilial(), true);
			}
		}catch (FabricaException fb) {
			this.addError(true,fb.getMessage());
		}catch(NoResultException nr) {
			this.addError(true,"hibernate.noResult",nr.getMessage());
		}catch(Exception e) {
			this.addError(true,"exception.error.fatal",e.getMessage());
		}
	}
	
	public void initializeListingFichaTecnica(Long id) {
		this.viewState = ViewState.LISTING;
		if (id != null){
			this.producao = this.producaoDao.pegaProducaoComListaDeFichasTecnicas(false, id, pegaIdEmpresa(), pegaIdFilial(), true);
			System.out.println(+this.producao.getId());
			this.listaFichaTecnica = this.producao.getListaDeFichasTecnicas();
		}
	}
	
	public void onRowSelectFichaTecnica(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.ficha = (FichaTecnica)event.getObject();
	}
	
	@Transactional
	public void doSalvarFicha() {
		try {
			if (this.ficha.getId() == null) {
//				this.materialModelo.set
				this.materialModelo.setPossuiFichaTecnicas(true);
				this.materialModelo.setProducao(this.producao);
				this.materialModeloDao.save(this.materialModelo);
				this.ficha.setProducao(this.producao);
				this.ficha.setMateriaPrima(this.materialModelo.getProduto());
				this.listaFichaTecnica.add(this.ficha);
				this.producao.setListaDeFichasTecnicas(listaFichaTecnica);
				this.ficha.setAndamento(StatusAndamento.AND);
				if (this.ficha.getItens() != null) {
					for (ItemFichaTecnica item : this.ficha.getItens()) {
						item.setFicha(this.ficha);
					}
				}
				retiraDoEstoque(this.ficha.getItens());
				this.producaoDao.save(this.producao);
			}else {
				// alteração de fichaTecnica
				// metodo para devolver produtos para o estoque e depois retirar o que foi utilizado
				if (!this.listaItemPedidoExcluir.isEmpty()) {
					devolveParaEstoque(this.listaItemPedidoExcluir);
					for (ItemFichaTecnica itemExclui : this.listaItemPedidoExcluir) {
						this.itemFichaDao.delete(itemExclui);
					}
				}
				List<ItemFichaTecnica> listaTemp = new ArrayList<ItemFichaTecnica>();
				for (ItemFichaTecnica item : this.ficha.getItens()) {
					if (item.getFicha() == null) {
						item.setFicha(this.ficha);
						listaTemp.add(item);
					}
				}
				if (listaTemp.size() > 0) {
					retiraDoEstoque(listaTemp);
				}
				this.ficha = this.fichaDao.save(this.ficha);
			}
			this.addInfo(true,"save.sucess",this.ficha.getId());
//		}catch(FabricaException f) {
			
		}catch(HibernateException h) {
			this.addError(true,"hibernate.persist.fail",h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal",e.getMessage());
			
		}
	}
	
	@Transactional
	public void concluiFicha(FichaTecnica fichaTemp) {
		try {
			if (!fichaTemp.getAndamento().equals(StatusAndamento.CON)) {
				fichaTemp.setAndamento(StatusAndamento.CON);
				this.fichaDao.save(fichaTemp);
			}
			Producao op = new Producao();
			boolean concluirOP = true;
			boolean feitoTodasAsFichas = true;
			op = this.producaoDao.pegaProducaoComListaDeFichasTecnicas(false, fichaTemp.getProducao().getId(), pegaIdEmpresa(), null, false);
			op.setListaDeMateriais(this.producaoDao.pegaListaDeMateriaisSemFichasTecnicas(false, fichaTemp.getProducao().getId(), pegaIdEmpresa(), null, false));
			if (op.getId() != null) {
				for (MaterialModelo material : op.getListaDeMateriais()) {
					if (!material.isPossuiFichaTecnicas()) {
						feitoTodasAsFichas = false;
					}
				}
				for (FichaTecnica ficha : op.getListaDeFichasTecnicas()) {
					if (!ficha.getAndamento().equals(StatusAndamento.CON)) {
						concluirOP = false;
					}
				}
				if (concluirOP && feitoTodasAsFichas) {
					op.setAndamento(StatusAndamento.CON);
					this.producaoDao.save(op);
				}
			}
		}catch(HibernateException h) {
			this.addError(true,"hibernate.persist.fail",h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal",e.getMessage());

		}
	}
	
	public List<LinhaProducao> listaLinhaProd(){
		return linhaDao.listaCriteriaPorFilial(pegaIdEmpresa(), null,true ,false);
	}
	
	public void geraTotalGrade() {
		this.totalGradeTemp = new BigDecimal("0");
		for (GradeProducao grade : this.listaGrade) {
			this.totalGradeTemp = this.totalGradeTemp.add(new BigDecimal(grade.getQuantidade()));
		}
	}
	
	public void geraTotalCorte() {
		this.ficha.setTotalCorte(this.ficha.getNumFolhas().multiply(totalGradeTemp,mc).setScale(2, RoundingMode.HALF_EVEN));
	}
	
	@Transactional
	public void toExcluirOP(Producao prodSelect) {
		try {			
			this.producaoDao.delete(prodSelect);
			this.addInfo(true,"delete.sucess");
		}catch (HibernateException h){
			this.addError(true,"hibernate.persist.fail",h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal",e.getMessage());
			
		}
	}
	
	public ItemFichaTecnica encontraProduto(String codigo) throws FabricaException {
		ItemFichaTecnica itemTemp = new ItemFichaTecnica();
		itemTemp.setRef(codigo);
			this.barras = this.barrasDao.encontraBarrasPorEmpresa(codigo, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
			if (this.barras != null ) {
				this.produto = this.produtoDao.pegaProdutoID(this.barras.getProdutoBase().getId(), pegaIdEmpresa(),pegaIdFilial());
//				this.produto = this.produtoDao.findById(this.barrasEstoque.getProdutoBase().getId(), false);
				if(this.produto != null) {
					itemTemp.setBarras(this.barras);
//					this.produto.getListaCustoProduto().add(this.custoDao.pegaProdutoCusto(this.produto, pegaIdEmpresa(), pegaIdFilial()));
//					itemTemp.setProduto(this.produto);
				}
				System.out.println("localizou direto o produto" );
			}else {
				this.produto = this.produtoDao.pegaProdutoRefFab(codigo, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
				if (this.produto.equals(this.ficha.getMateriaPrima())) {
					this.listaBarrasTemp = this.barrasDao.listaBarrasPorProduto(this.produto, pegaIdEmpresa(), pegaIdFilial());
				}else {
					throw new FabricaException(this.translate("fabrica.material.notEqual"));
				}
				if (this.listaBarrasTemp !=null && this.listaBarrasTemp.size() > 1) {
					// chamar lista para selecionar a barras que esta sendo vendida e setar para This.produto.
					System.out.println("ListaBarrasTemp  - Tela Lista barras - tamanho  = " + this.listaBarrasTemp.size());
					telaListaBarras();
				}else {
					if (this.listaBarrasTemp != null && this.listaBarrasTemp.size() == 1) {
						System.out.println("ListaBarrasTemp  - lista=1 - tamanho  = " + this.listaBarrasTemp.size());
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
			if (this.ref != null && this.quantidade.compareTo(new BigDecimal("0")) == 1 ){
				this.produto = new Produto();
				this.itemFicha = this.encontraProduto(this.ref);
				if(this.itemFicha.getBarras() !=  null) {
					if (this.viewState == ViewState.ADDING) {
						this.itemFicha.setProduto(this.materialModelo.getProduto());
					}else {
						this.itemFicha.setProduto(this.ficha.getMateriaPrima());
					}
					this.itemFicha.setQuantidade(this.quantidade);
					this.ficha.getItens().add(this.itemFicha);
				}else {
					throw new FabricaException(this.translate("fabrica.material.notFound"));
				}
			}
			this.ref = "";
			this.itemFicha = new ItemFichaTecnica();
		}catch (FabricaException fb) {
			this.addError(true,fb.getMessage(),this.ref);
		}
	}
	
	/**
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaBarras(){
		this.updateAndOpenDialog("PesquisaBarrasDialog", "dialogPesquisaBarras");
	}
	
	public AbstractDataModel<ItemFichaTecnica> getListaItemModel(){
		this.listaItemModel = new AbstractDataModel<ItemFichaTecnica>(this.ficha.getItens());
		return this.listaItemModel;
	}
	
	/**
	 * Mï¿½todo que retira do estoque a quantidade informada, caso
	 * BarrasEstoque ou NcmEstoque esteja nulo serï¿½ criado uma base com quantidade = 0 para depois negativar.
	 * @param listaItem
	 * @throws HibernateException
	 * @throws EstoqueException
	 */
	
	@Transactional
	public void retiraDoEstoque(List<ItemFichaTecnica> listaItem) throws HibernateException, EstoqueException {
		Estoque estoqueTemp = new Estoque();
		for (ItemFichaTecnica itemPedidoTemp : listaItem) {
			estoqueTemp = pegaEstoque(itemPedidoTemp);
			if (permiteEstoqueNegativoFab() == false) {
				if (estoqueTemp.getBarrasEstoque().getTotalEstoque().compareTo(itemPedidoTemp.getQuantidade()) >= 0 ) {
					estoqueTemp = estoqueUtil.subtraiEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true);
					barrasDao.save(estoqueTemp.getBarrasEstoque());
				}else {
					throw new EstoqueException(this.translate("estoque.nfe.emite.item.outOfStock"));
				}
			}else {
				try {
					estoqueTemp = estoqueUtil.subtraiEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true);
					barrasDao.save(estoqueTemp.getBarrasEstoque());
				}catch (EstoqueException e) {
					throw new EstoqueException(this.translate("hibernate.persist.fail.barrasEstoque"));
				}
			}
		}
	}
	
	/**
	 * Mï¿½todo que recebe o ESTOQUE do item do pedido 
	 *
	 * @param item
	 * @return
	 * @throws EstoqueException 
	 */
	public Estoque pegaEstoque (ItemFichaTecnica item) throws EstoqueException {
//		try {
		return estoqueUtil.preencheEstoqueItem(item, pegaIdEmpresa(), pegaIdFilial());
	}
	
	public boolean permiteEstoqueNegativoFab() {
		return this.empresaUsuario.getEmp().isEstoqueNegativoFab();
	}
	/**
	 * Mï¿½todo que devolve para o estoque a quantidade que anteriormente havia sido retirado.
	 * @param listaItem
	 */
	
	@Transactional
	public void devolveParaEstoque(List<ItemFichaTecnica> listaItem) throws EstoqueException, HibernateException {
			Estoque estoqueTemp = new Estoque();
			for (ItemFichaTecnica itemPedidoTemp : listaItem) {
				estoqueTemp = pegaEstoque(itemPedidoTemp);
				estoqueTemp = estoqueUtil.acrescentaEstoque(estoqueTemp, itemPedidoTemp.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true);
				barrasDao.save(estoqueTemp.getBarrasEstoque());	
				
			}
	}
	
	@Transactional
	public void excluiItemFicha(ItemFichaTecnica itemSelect){
		try{
			this.itemFicha = itemSelect;
			boolean permiteRemover = false;
			System.out.println("inico da exclusao do item");
			if (this.ficha.getItens().size() >1 ) {
				permiteRemover = true;
			}else {
				if (this.viewState == ViewState.ADDING) {
					permiteRemover = true;
				}
			}
			if (permiteRemover) {
				if (!this.ficha.getItens().isEmpty() && this.itemFicha != null ){
					this.ficha.getItens().remove(itemSelect);
					if (this.viewState == ViewState.EDITING) {
						if (itemSelect.getId() != null) {
							System.out.println("Estou no Exclui Item devolvendo a quantidade do item para o estoque! item ID: " + itemSelect.getId());
							this.listaItemPedidoExcluir.add(itemSelect);
						}
					}
					//				if (this.formaPag != null) {
					//					preencheParcelamento();
					//				}
				}
				if (itemSelect.getFicha() != null) {
					this.ficha = this.fichaDao.save(this.ficha);
					initializeFichaTecnica(null,this.ficha.getId().toString());
				}
				this.listaItemModel = getListaItemModel();
				this.addWarning(true, "cfe.list.delete", itemSelect.getProduto().getReferencia());
			}else {
				throw new IllegalAccessException(this.translate("fabricaException.excluiItem.minimo"));
			}

		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}
	
	/**toTela
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaBarrasFicha(){
		if (this.viewState == ViewState.ADDING) {
			this.listaBarras = this.barrasDao.listaBarrasPorProduto(this.materialModelo.getProduto(), pegaIdEmpresa(), pegaIdFilial());
		}else {
			this.listaBarras = this.barrasDao.listaBarrasPorProduto(this.ficha.getMateriaPrima(), pegaIdEmpresa(), pegaIdFilial());
		}
		this.updateAndOpenDialog("pesquisaBarrasMaterialDialog", "dialogPesquisaBarrasMaterial");
	}
	
	public void onRowSelectBarras(SelectEvent event) {
		this.barras = (BarrasEstoque) event.getObject();
		this.ref = this.barras.getBarras();
	}
	
	public void onRowSelectBarrasItem(SelectEvent event) {
		this.barras = (BarrasEstoque) event.getObject();
		this.itemFicha.setBarras(this.barras);
		this.ref = this.barras.getBarras();
	}
	
	
	/**
	 * Rotina para avançar na etapa de fabricação
	 * @param prod
	 */
	@Transactional
	public void nextEtapa(Producao prod) {
		try {
			Long numEtapaAtual = 0L;
			Long numNextEtapa = 0L;
			Long indiceUltimaEtapa = 0L;
			if (prod.getAndamento().equals(StatusAndamento.CON)) {
				List<SequenciaLinhaProducao> listaEtapas = new ArrayList<>();
				listaEtapas =  this.producaoDao.pegaSequenciaProducao(false, prod.getId(), pegaIdEmpresa(), null, false);
				indiceUltimaEtapa = new BigDecimal(listaEtapas.size()).longValue();
				System.err.println("totla listaEtapas = " + indiceUltimaEtapa);
				for (SequenciaLinhaProducao etapa : listaEtapas) {
					if (etapa.getEtapa().equals(prod.getEtapa())) {

						numEtapaAtual = etapa.getIndice();
					}
				}
				if (numEtapaAtual == 0L) {
					System.out.println("estou no if etapaAtual = 0L");
					throw new FabricaException(this.translate("fabricaException.op.numEtapaError"));
				}else {
					System.out.println("adicionando 1 a etapa atual :  "+ numEtapaAtual);
					numEtapaAtual++;
					numNextEtapa = numEtapaAtual;
				}

				if (numNextEtapa <= indiceUltimaEtapa ) {
					for (SequenciaLinhaProducao etapa : listaEtapas) {
						if (etapa.getIndice() == numNextEtapa) {
							prod.setEtapa(etapa.getEtapa());
							prod.setAndamento(StatusAndamento.AND);
							this.producaoDao.save(prod);
						}
					}
				}else {
					prod.setAndamento(StatusAndamento.FIM);
					this.producaoDao.save(prod);
				}
			}else {
				if (prod.getAndamento().equals(StatusAndamento.AGU)) {
					
				}else {
					throw new FabricaException(this.translate("fabricaException.op.stageNotCompleted"));
				}
			}

		}catch (FabricaException fb) {
			this.addError(true,fb.getMessage());
		}catch(HibernateException h) {
			this.addError(true,"hibernate.persist.fail",h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal",e.getMessage());

		}
	}
	/**
	 * Método que retorna o tipo de tela a ser chamada
	 * @param prod
	 * @return nome da tela
	 */
	public OsFt chamaTela(Producao prod) {
		if (prod.getEtapa().isUsarOS() == true && prod.getEtapa().isUsarFichaTec() == false ) {
			return OsFt.OS;
		}else {
			if (prod.getEtapa().isUsarFichaTec() == true && prod.getEtapa().isUsarOS() == false ) {
				return OsFt.FT;
			}else { // ambos falso ou ambos verdadeiro
				return OsFt.NA;
			}
		}
	}
	
//	/**
//	 * método que em conjunto com o método chamaTela torna visível ou não o botão de ação na listagem de Produção
//	 * @param prod
//	 * @return boolean true/false
//	 */
//	public boolean acaoVisivel(Producao prod) {
//		String tela = chamaTela(prod);
//		boolean resultado = false;
//		if (tela == "OS" && prod.getAndamento().equals(StatusAndamento.AGU)) {
//			resultado = true;
//		}else {
//			if (tela == "FT" && (prod.getAndamento().equals(StatusAndamento.AGU) || prod.getAndamento().equals(StatusAndamento.AND))) {
//				resultado = true;
//			}
//		}
//		return resultado;
//	}
	
	/**
	 * método que em conjunto com o método chamaTela torna visível ou não o botão de ação na listagem de Produção
	 * @param prod
	 * @return boolean true/false
	 */
	public OsFt acaoVisivel(Producao prod) {
		OsFt tela = chamaTela(prod);
		OsFt resultado = OsFt.NA;
		if (tela == OsFt.OS && prod.getAndamento().equals(StatusAndamento.AGU)) {
			resultado = OsFt.OS;
		}else {
			if (tela == OsFt.FT && (prod.getAndamento().equals(StatusAndamento.AGU) || prod.getAndamento().equals(StatusAndamento.AND))) {
				resultado = OsFt.FT;
			}
		}
		return resultado;
	}
	
	/**
	 * Permite visualização FichaTécnica
	 */
	public boolean renderedFT(Producao prod) {
		boolean resultado = false;
		if (acaoVisivel(prod) == OsFt.FT ) {
			resultado = true;
		}
		return resultado;
	}
	
	/**
	 * Permite visualização FichaTécnica
	 */
	public boolean renderedOS(Producao prod) {
		boolean resultado = false;
		if (acaoVisivel(prod) == OsFt.OS ) {
			resultado = true;
		}
		return resultado;
	}
	
	/*--------------------------------------------------------------------------------------------------------------------
	  											 Inicio funcoes CORTADOR/MODELISTA
	 *--------------------------------------------------------------------------------------------------------------------*/
	
	/**
	 * Inicio Form Lista Risco
	 */
	public void initializeListingCortador() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.cortadorModel = getCortadorModelLazy();

	}
	/**
	 * Inicializa a página preenchendo todos os campos necessários e inicializando todas as variáveis
	 * @param id - ID do Cortador a ser inicializado, caso NULL, entende-se que será um novo registro
	 */
	public void initializeFormCortador(Long id) {
		if (id == null) { // novo Cadastro
			this.viewState = ViewState.ADDING;
			this.cortador = new Cortador();
		}else { // alteração
			this.viewState = ViewState.EDITING;
			this.cortador = this.cortadorDAO.findById(id, false);
		}
	}
	
	/**
	 * Lista de Cortador/Modelista
	 * @return
	 */
	public String toListCortador() {
		return "formListCortador.xhtml?faces-redirect=true" ;
	}
	
	/**
	 * Abre o form para o registro de um NOVO Cortador
	 * @return
	 */
	public String newCortador() {
		return "formCadCortador.xhtml?faces-redirect=true" ;
	}
	/**
	 * Inicializa o form para edição dos dados do cortador
	 * @param idCortador - ID do cortador
	 * @return caminho com id para inicialização
	 */
	public String viewCortador(Long idCortador) {
		return "formCadCortador.xhtml?faces-redirect=true&idCortador=" + idCortador;
	}
	
	public AbstractLazyModel<Cortador> getCortadorModelLazy(){

		this.cortadorModel = new AbstractLazyModel<Cortador>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 4707512014940313279L;

			@Override
			public List<Cortador> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "vencimento")
				.withDirection(sortOrder.name());
				Page<Cortador> page = new Page<Cortador>();
				page = cortadorDAO.listaLazyComFiltro(false, false, pegaIdEmpresa(), pegaIdFilial(), pageRequest, null, null, true);
								if (filters != null){
									for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
										try{
											String filterProperty = it.next();
											Object filterValue = filters.get(filterProperty);
											page = cortadorDAO.listaLazyComFiltro(false,false,pegaIdEmpresa(), pegaIdFilial(), pageRequest,filterProperty, filterValue.toString(),true);
										} catch(Exception e) {
											System.out.println(e.getMessage());
										}
									}
								}
				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return cortadorModel;
	}
	
	public void onRowSelectCortador(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.cortador = (Cortador)event.getObject();
	}
	
	@Transactional
	public String doSalvarCortador() {
		try {
			this.cortador.setDeleted(false);
			this.cortador = this.cortadorDAO.save(this.cortador);
			return newCortador();
		}catch (HibernateException h){
			this.addError(true,"caixa.error", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true,"caixa.error", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Desabilita o serviço
	 */
	@Transactional
	public String doExcluirCortador() {
		try {
			this.cortador.setDeleted(true);
			this.cortador = this.cortadorDAO.save(this.cortador);
			return toListCortador();
		}catch (HibernateException h){
			this.addError(true,"caixa.error", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true,"caixa.error", e.getMessage());
			return null;
		}
		
	}
	
	/*--------------------------------------------------------------------------------------------------------------------
	  												Inicio funcoes Risco 
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	/**
	 * Inicio Form Lista Risco
	 */
	public void initializeListingRisco() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.riscoModel = getRiscoModelLazy();

	}

	/**
	 * Inicialização do form Risco 
	 * @param id - ID do risco a ser inicializado, caso NULL, entende-se que será um novo registro
	 */
	public void initializeFormRisco(Long id) {
		if (id == null) {// novo cadastro
			this.viewState = ViewState.ADDING;
			this.risco = new Risco();
			this.modelo = new Modelo();
		}else { // alteração
			this.viewState = ViewState.EDITING;
			this.risco = this.riscoDAO.pegaRiscoComGradePreenchida(false, id, pegaIdEmpresa(), pegaIdFilial(), true);
			if (this.risco.getId() != null) {
				this.listaGrade = this.risco.getGrade();
				this.modelo = this.risco.getModelo();
			}
		}
		
	}
	
	public AbstractLazyModel<Risco> getRiscoModelLazy(){

		this.riscoModel = new AbstractLazyModel<Risco>() {


			/**
			 *
			 */
			private static final long serialVersionUID = 2159241621182785523L;

			@Override
			public List<Risco> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "vencimento")
				.withDirection(sortOrder.name());
				Page<Risco> page = new Page<Risco>();
				page = riscoDAO.listaLazyComFiltro(false, false, pegaIdEmpresa(), pegaIdFilial(), pageRequest, null, null, true);
								if (filters != null){
									for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
										try{
											String filterProperty = it.next();
											Object filterValue = filters.get(filterProperty);
											page = riscoDAO.listaLazyComFiltro(false,false,pegaIdEmpresa(), pegaIdFilial(), pageRequest,filterProperty, filterValue.toString(),true);
										} catch(Exception e) {
											System.out.println(e.getMessage());
										}
									}
								}
				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return riscoModel;
	}
	
	/**
	 * Lista de Cortador/Modelista
	 * @return
	 */
	public String toListRisco() {
		return "formListRisco.xhtml?faces-redirect=true" ;
	}
	
	/**
	 * Abre o form para o registro de um NOVO Risco
	 * @return 
	 */
	public String newRisco() {
		return "formCadRisco.xhtml?faces-redirect=true";
	}
	
	/**
	 * Inicializa o form para edição dos dados do risco
	 * @param idRisco - ID do risco
	 * @return caminho com id para inicialização
	 */
	public String viewRisco(Long idRisco) {
		return "formCadRisco.xhtml?faces-redirect=true&idRisco=" + idRisco;
	}
	
	public void onRowSelectRisco(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.risco = (Risco)event.getObject();
	}
	/**
	 * Salva o risco
	 * @return
	 */
	@Transactional
	public String doSalvarRisco() {
		try {
			this.risco.setDeleted(false);
			this.risco.setModelo(this.modelo);
			for (GradeProducao gradeP : this.listaGrade) {
				gradeP.setRisco(this.risco);
			}
			this.risco.setGrade(this.listaGrade);
			this.risco = this.riscoDAO.save(this.risco);
			return newRisco();
		}catch (HibernateException h){
			this.addError(true,"caixa.error", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true,"caixa.error", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Desabilita o Risco
	 */
	@Transactional
	public String doExcluirRisco() {
		try {
			this.risco.setDeleted(true);
			this.risco = this.riscoDAO.save(this.risco);
			return toListRisco();
		}catch (HibernateException h){
			this.addError(true,"caixa.error", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true,"caixa.error", e.getMessage());
			return null;
		}
		
	}
	
	public TipoEnfesto[] listaTipoEnfesto() {
		return TipoEnfesto.values();
	}
	
	/*--------------------------------------------------------------------------------------------------------------------
		Inicio funcoes Serviços 
	 * ------------------------------------------------------------------------------------------------------------------*/
	
	/**
	 * Inicio Form Lista serviços
	 */
	public void initializeListingServico() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.servicoModel = getServicoModelLazy();

	}

	/**
	 * Inicialização do form servico 
	 * @param id - ID do risco a ser inicializado, caso NULL, entende-se que será um novo registro
	 */
	public void initializeFormServico(Long id) {
		if (id == null) {// novo cadastro
			this.viewState = ViewState.ADDING;
			this.servico = new Servico();
		}else { // alteração
			this.viewState = ViewState.EDITING;
			this.servico = this.servicoDao.findById(id, false);
		}
	}
	
	public AbstractLazyModel<Servico> getServicoModelLazy(){

		this.servicoModel = new AbstractLazyModel<Servico>() {


			/**
			 *
			 */
			private static final long serialVersionUID = 2159241621182785523L;

			@Override
			public List<Servico> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "vencimento")
				.withDirection(sortOrder.name());
				Page<Servico> page = new Page<Servico>();
				page = servicoDao.listaLazyComFiltro(false, false, pegaIdEmpresa(), pegaIdFilial(), pageRequest, null, null, true);
								if (filters != null){
									for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
										try{
											String filterProperty = it.next();
											Object filterValue = filters.get(filterProperty);
											page = servicoDao.listaLazyComFiltro(false,false,pegaIdEmpresa(), pegaIdFilial(), pageRequest,filterProperty, filterValue.toString(),true);
										} catch(Exception e) {
											System.out.println(e.getMessage());
										}
									}
								}
				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return servicoModel;
	}
	
	/**
	 * Abre o form para o registro de um NOVO Serviço
	 * @return 
	 */
	public String newServico() {
		return "formCadServicos.xhtml?faces-redirect=true";
	}
	
	/**
	 * Inicializa o form para edição dos dados do serviço
	 * @param idServico - ID do serviço
	 * @return caminho com id para inicialização
	 */
	public String viewServico(Long idServico) {
		return "formCadServicos.xhtml?faces-redirect=true&idServico=" + idServico;
	}
	
	/**
	 * Lista de Serviços/Lavagens
	 * @return
	 */
	public String toListServico() {
		return "formListServicos.xhtml?faces-redirect=true" ;
	}
	
	
	public void onRowSelectServico(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.servico = (Servico)event.getObject();
	}
	
	/**
	 * Persiste no banco de dados
	 */
	@Transactional
	public String doSalvarServico() {
		try {
			this.servico.setDeleted(false);
			this.servico = this.servicoDao.save(this.servico);
			return newServico();
		}catch (HibernateException h){
			this.addError(true,"caixa.error", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true,"caixa.error", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Desabilita o serviço
	 */
	@Transactional
	public String doExcluirServico() {
		try {
			this.servico.setDeleted(true);
			this.servico = this.servicoDao.save(this.servico);
			return toListServico();
		}catch (HibernateException h){
			this.addError(true,"caixa.error", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true,"caixa.error", e.getMessage());
			return null;
		}
		
	}
	
}
