package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import br.com.ibrcomp.exception.EstoqueRuntimeException;
import br.com.ibrcomp.exception.RelatoriosException;
import br.com.ibrcomp.interceptor.RollbackOn;
import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.relatorios.RelatorioVendas;
import br.com.nsym.domain.misc.EstoqueUtil;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.estoque.EntradaEstoque;
import br.com.nsym.domain.model.entity.estoque.Estoque;
import br.com.nsym.domain.model.entity.estoque.ItemEstoqueEntrada;
import br.com.nsym.domain.model.entity.estoque.itemRecebimentoProduto;
import br.com.nsym.domain.model.entity.estoque.dto.RelEstoqueGeralDTO;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.entity.tools.TipoAtualizaEstoque;
import br.com.nsym.domain.model.entity.venda.EmitenteVenda;
import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.cadastro.BarrasEstoqueRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.cadastro.ProdutoRepository;
import br.com.nsym.domain.model.repository.estoque.EntradaEstoqueRepository;
import br.com.nsym.domain.model.repository.estoque.ItemEstoqueEntradaRepository;
import br.com.nsym.domain.model.repository.financeiro.CustoProdutoRepository;
import br.com.nsym.domain.model.repository.venda.PedidoRepository;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class EstoqueGeralBean extends AbstractBeanEmpDS<EntradaEstoque> {
	
	@Getter
	@Setter
	private EntradaEstoque entrada;
	
	@Inject
	private EntradaEstoqueRepository entradaDao;
	
	@Getter
	@Setter
	private AbstractLazyModel<EntradaEstoque> entradaModel;
	
	@Setter
	private AbstractDataModel<ItemEstoqueEntrada> listaItemModel;
	
	@Getter
	@Setter
	private AbstractLazyModel<Produto> produtoModel;
	
	@Getter
	@Setter
	private List<ItemEstoqueEntrada> listaItem = new ArrayList<ItemEstoqueEntrada>();
	
	@Getter
	@Setter
	private ItemEstoqueEntrada item = new ItemEstoqueEntrada();
	
	@Inject
	private ItemEstoqueEntradaRepository itemEstoqueEntradaDao;
	
	@Getter
	@Setter
	private List<ItemEstoqueEntrada> listaItemParaExcluir = new ArrayList<ItemEstoqueEntrada>();
	
	@Inject
	private EstoqueUtil estoqueUtil;
	
	@Getter
	@Setter
	private BarrasEstoque estoque = new BarrasEstoque();
	
	@Inject
	private BarrasEstoqueRepository estoqueGeralDao;
	
	@Inject
	private ProdutoRepository produtoDao;
	
	@Getter
	@Setter
	private Produto produto = new Produto();
	
	@Getter
	@Setter
	private LocalDate dataInicial = LocalDate.now().minusDays(1);
	
	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now(); 
	
	
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
	private String ref;
	
	@Getter
	@Setter
	private BigDecimal quantidade = new BigDecimal("0");

	@Getter
	@Setter
	private List<BarrasEstoque> listaBarrasTemp = new ArrayList<BarrasEstoque>();
	
	@Getter
	@Setter
	private TipoAtualizaEstoque tipoEstoque;
	
	// variaves módulo EstoqueGeralProduto
	
	@Getter
	@Setter
	private EntradaEstoque entradaProduto;
	
	@Getter
	@Setter
	private EntradaEstoque entradaRecebimentoProdutoSelecionado;
	
	@Getter
	@Setter
	private itemRecebimentoProduto itemRecebimentoProduto;
	
	@Getter
	@Setter
	private AbstractLazyModel<EntradaEstoque> recebimentoProdutoModel;
	
	@Getter
	@Setter
	private Fornecedor fornecedor ;
	
	@Getter
	@Setter
	private String razaoFornecedor;

	@Getter
	@Setter
	private List<Fornecedor> listaFornecedores = new ArrayList<Fornecedor>();
	
	@Inject
	private FornecedorRepository fornecedorDao;
	
	@Inject
	private CustoProdutoRepository custoDao;
		
	@Setter
	private AbstractDataModel<ItemEstoqueEntrada> listaItemRecProdutoModel;
	
	@Getter
	@Setter
	private BigDecimal precoCusto= new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalRecebimento= new BigDecimal("0");
	
	@Getter
	@Setter
	private ProdutoCusto custoProduto = new ProdutoCusto();
	
	@Getter
	@Setter
	private boolean calcularCusto = false;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Inject
	private RelatorioVendas relatorios;
	
	@Getter
	@Setter
	boolean isFabrica = false;
	
/**
 * Variaveis Transferencia
 */
	@Getter
	@Setter
	private Pedido pedido;
	
	@Inject
	private PedidoRepository pedidoDao;
	
	@Inject
	private EmpresaRepository empDao;
	
	@Inject
	private FilialRepository filDao;
	
	@Getter
	@Setter
	private boolean estoqueDifereZero = true;
	
	@Getter
	@Setter
	private boolean exportExcel = false;
	
	@Getter
	private List<Pedido> listaRecebimentoTransferencia = new ArrayList<Pedido>();
	
	@Getter
	@Setter
	boolean botaoConfirma = false;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2720490907303196717L;
	
	@PostConstruct
	public void init(){
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}


	@Override
	public EntradaEstoque setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntradaEstoque setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.entrada = new EntradaEstoque();
		this.entradaModel = getLazyEntradaModel();
		
	}
	
	public void atualizaListaInventario() {
		this.entradaModel = getLazyEntradaModel();
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.entrada = new EntradaEstoque();
			this.entrada.setAtualizado(false);
		}else {
			this.viewState = ViewState.EDITING;
			this.entrada = this.entradaDao.pegaEntradaEstoquePorID(id);
			this.tipoEstoque = this.entrada.getTipoAtualiza();
			habilitaInserir();
		}
		
	}
	
	/**
	 * Lista de Pedidos em Aberto modo Lazy
	 * @return lista de pedidos
	 */
	public AbstractLazyModel<EntradaEstoque> getLazyEntradaModel(){
		this.entradaModel = new AbstractLazyModel<EntradaEstoque>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8240850644683518994L;

			@Override
			public List<EntradaEstoque> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<EntradaEstoque> page = entradaDao.listaEntradaEstoquePorIntervalo(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(), pageRequest,null,null,false);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = entradaDao.listaEntradaEstoquePorIntervalo(dataInicial,dataFinal, pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase(),false);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				return page.getContent();
			}
		};
		
		return entradaModel;
	}
	public AbstractLazyModel<Produto> getLazyProduto(boolean fab){
		this.produtoModel = new AbstractLazyModel<Produto>() {

			/**
			 *
			 */
			private static final long serialVersionUID = 7061870973805283652L;

			@Override
			public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());
				Page<Produto> page = new Page<>(new ArrayList<>(), 0l);
				if (fab) {
					page = produtoDao.pegaPageProdutoComCustoComEstoqueFabrica(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest);
				}else {
					page = produtoDao.pegaPageProdutoComCustoComEstoque(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest);
				}
				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							if (fab) {
								page = produtoDao.pegaPageProdutoComEstoqueComCustoComFiltroFabrica(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							}else {
								page = produtoDao.pegaPageProdutoComEstoqueComCustoComFiltro(false,null,pegaIdEmpresa(), null,pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							}
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
	
	public void onRowSelect(SelectEvent event)throws IOException {
		this.entrada = ((EntradaEstoque) event.getObject()); 
		this.viewState = ViewState.EDITING;
	}
	
	public String toListEntradaEstoque() {
		return "formListEntradaEstoque.xhtml?faces-redirect=true";
	}
	
	public String toEntradaEstoque() {
		return "formEntradaEstoque.xhtml?faces-redirect=true";
	}
	
	/**
	 * Redireciona para tela Principal
	 * @return Dashboard
	 */
	public String toEditEntradaEstoque(Long idEntrada) {
		return "/main/estoque/formEntradaEstoque.xhtml?faces-redirect=true&idEntrada=" + idEntrada;
	}
	
	public TipoAtualizaEstoque[] listaTipoInventario() {
		return TipoAtualizaEstoque.values();
	}
	
	public void habilitaInserir() {
		try {
			if ( this.tipoEstoque != null) {
				this.setHabilita(true);
				this.entrada.setTipoAtualiza(this.tipoEstoque);
			}else {
				throw new EstoqueException(translate("nullPointer.null"));
			}
		}catch (EstoqueException e) {
			this.addError(true, "estoque.recebimento.error", e.getMessage());
		}
	}
	
	public void telaPesquisaProduto(){
		this.produtoModel = getLazyProduto(this.isFabrica);
		this.updateAndOpenDialog("PesquisaProdutoInventarioDialog", "dialogPesquisaProdutoInventario");		
	}
	

	/**
	 * Exibe o dialog com a lista de codigo de barras do produtos
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
		this.estoque = (BarrasEstoque) event.getObject();
		this.item.setBarras(this.estoque);
	}
	
	/**
	 * M�todo que seta o Bar quando selecionado em uma lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectProduto(SelectEvent event)throws IOException{
		this.produto = (Produto) event.getObject();
		this.ref = this.produto.getReferencia();
//		definePreco();
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
	
	public void localizaProduto() {
		try {
			this.item = new ItemEstoqueEntrada();
			if (this.ref != null && this.quantidade.compareTo(new BigDecimal("0")) > -1 ){
				this.estoque = this.estoqueGeralDao.encontraBarrasPorEmpresa(this.ref, getUsuarioAutenticado().getIdEmpresa(),pegaIdFilial());
				if (this.estoque != null ) {
					this.produto = this.produtoDao.findById(this.estoque.getProdutoBase().getId(), false);
					if(this.produto != null) {
						this.item.setBarras(this.estoque);
					}
				}else {
					if (this.isFabrica) {
						this.produto = this.produtoDao.pegaProdutoRefFab(this.ref, getUsuarioAutenticado().getIdEmpresa(),null);
					}else {
						this.produto = this.produtoDao.pegaProdutoRef(this.ref, getUsuarioAutenticado().getIdEmpresa(),null);
					}
					this.listaBarrasTemp  = this.estoqueGeralDao.listaBarrasPorProduto(this.produto, pegaIdEmpresa(), pegaIdFilial());
					if (this.listaBarrasTemp !=null && this.listaBarrasTemp.size() > 1) {
						// chamar lista para selecionar a barras que esta sendo vendida e setar para This.produto.
						telaListaBarras();
					}else {
						if (this.listaBarrasTemp != null && this.listaBarrasTemp.size() == 1) {
							this.produto = this.produtoDao.findById(this.listaBarrasTemp.get(0).getProdutoBase().getId(),false);
							this.estoque = this.listaBarrasTemp.get(0);
						}
					}
				}
				System.out.println("Estou apos localizar o produto: " + this.produto.getDescricao());
				setaCusto();
//				System.out.println("Estou apos setar o custo do produto: " + this.custoProduto.getCusto());
				
				if (this.produto != null){	
					System.out.println("adicionando item na lista!");
						this.item.setProduto(this.produto);
						this.item.setCustoRecebimento(this.precoCusto);
						this.item.setQuantidade(this.quantidade);
						this.item.setBarras(this.estoque);
						if (this.precoCusto.compareTo(new BigDecimal("0"))>0) {
							this.item.setValorTotal((this.precoCusto.multiply(this.quantidade,mc)).setScale(2,RoundingMode.HALF_EVEN));
							this.totalRecebimento = this.totalRecebimento.add((this.precoCusto.multiply(this.quantidade,mc))).setScale(2,RoundingMode.HALF_EVEN);
						}
						if (this.entrada != null) {
							this.entrada.getListaDeItensEntrada().add(this.item);
						}
						if (this.entradaProduto != null) {
							this.entradaProduto.getListaDeItensEntrada().add(this.item);
						}
				}
				this.ref = "";
				this.precoCusto= new BigDecimal("0");
				this.quantidade = new BigDecimal("0");
			}
		}catch (Exception e) {
			this.addError(true, "Erro na fun�ao de localizacao de produto: " +  e.getMessage() + " " + e.getCause());
		}
	}
	
	public AbstractDataModel<ItemEstoqueEntrada> getListaItemModel(){
		this.listaItemModel = new AbstractDataModel<ItemEstoqueEntrada>(this.entrada.getListaDeItensEntrada());
		return this.listaItemModel;
	}
	
	public void removeItemEstoque(ItemEstoqueEntrada select) {
		if (this.entrada.isAtualizado() == false) {
			if (!this.entrada.getListaDeItensEntrada().isEmpty()){
				this.entrada.getListaDeItensEntrada().remove(select);
				if (this.totalRecebimento.compareTo(new BigDecimal("0"))>0) {
					this.totalRecebimento = this.totalRecebimento.subtract(select.getValorTotal());
				}
			}
			if (this.viewState == ViewState.EDITING) {
				if (select.getId() != null) {
					System.out.println("Estou no Exclui Item ! item ID: " + select.getId());
					this.listaItemParaExcluir.add(select);
				}
			}
			this.listaItemModel = getListaItemModel();
		}
	}
	
	@Transactional
	public String toSave() {
		try {
			if (this.entrada.getId() == null) { // Novo lan�amento de estoque
				this.entrada.setTipoAtualiza(this.tipoEstoque);
				for (ItemEstoqueEntrada item : this.entrada.getListaDeItensEntrada()) {
					item.setEntrada(this.entrada);
				}
				this.entrada.setDataCriacao(LocalDate.now());
				this.entrada.setHoraCriacao(LocalTime.now());
				this.entrada.setAtualizado(false);
				this.entrada.setRecebimentoProduto(false);
				this.entradaDao.save(this.entrada);
				
			}else { // Alterando lan�amento de estoque j� salvo
				if (this.entrada.isAtualizado() == false) {
					if (!this.listaItemParaExcluir.isEmpty()) {
						for (ItemEstoqueEntrada itemExclui : this.listaItemParaExcluir) {
							this.itemEstoqueEntradaDao.delete(itemExclui);
						}
					}
					this.entrada.setTipoAtualiza(this.tipoEstoque);
					for (ItemEstoqueEntrada item : this.entrada.getListaDeItensEntrada()) {
						if (item.getEntrada() == null) {
							item.setEntrada(this.entrada);
						}
					}
					this.entradaDao.save(this.entrada);
				}
			}
			return toListEntradaEstoque();
		}catch (Exception e) {
			// TODO: handle exception
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Método que recebe o ESTOQUE do item do inventario 
	 *
	 * @param item
	 * @return
	 */
	public Estoque pegaEstoque (ItemEstoqueEntrada item) {
		try {
		return estoqueUtil.preencheEstoqueItem(item, pegaIdEmpresa(), pegaIdFilial());
		}catch (EstoqueException e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	@Transactional
	public void atualiza() throws HibernateException, EstoqueException {
//		try {
			Estoque estoqueTemp = new Estoque();
			//Carrega a lista de itens que estava em LAZY
			this.entrada = this.entradaDao.pegaEntradaEstoquePorID(this.entrada.getId());
			switch (this.entrada.getTipoAtualiza()) {
			case Total:  // atualiza a lista e zera o que nao foi informado
					List<BarrasEstoque> listaZeroEstoque = this.estoqueGeralDao.listaEstoqueExistentePorEmpresaEFilial(pegaIdEmpresa(), pegaIdFilial());
					
					for (ItemEstoqueEntrada item : this.entrada.getListaDeItensEntrada()) {
						estoqueTemp = pegaEstoque(item);
						listaZeroEstoque.remove(estoqueTemp.getBarrasEstoque());
						estoqueTemp = this.estoqueUtil.insereQuantidadeExataDoEstoque(estoqueTemp, item.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false, true);
						this.estoqueGeralDao.save(estoqueTemp.getBarrasEstoque());	
					}
					for (BarrasEstoque barrasEstoque : listaZeroEstoque) {
						Estoque temp = new Estoque();
						temp.setBarrasEstoque(barrasEstoque);
						// criar opçao para zerar totalComprado
						estoqueTemp = this.estoqueUtil.zeraEstoque(temp, pegaIdEmpresa(), pegaIdFilial(), false,true,false);
						this.estoqueGeralDao.save(estoqueTemp.getBarrasEstoque());	
					}
					this.entrada.setAtualizado(true);
					this.entradaDao.save(this.entrada);	
				break;

			case ParcTotal: // define os valores informados como sendo o existente em estoque
				for (ItemEstoqueEntrada itemEstoque : this.entrada.getListaDeItensEntrada()) {
					estoqueTemp = pegaEstoque(itemEstoque);
					estoqueTemp = this.estoqueUtil.insereQuantidadeExataDoEstoque(estoqueTemp, itemEstoque.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true);
					itemEstoque.setBarras(this.estoqueGeralDao.save(estoqueTemp.getBarrasEstoque()));	
				}
				this.entrada.setAtualizado(true);
				this.entradaDao.save(this.entrada);
				break;

			default:
				break;
			}
//		}catch (EstoqueException e) {
//			
//		}
	}
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------	

	// modulo EstoqueGeral RecebimentoProduto
	
	public void initializeListingProduto() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.entradaProduto = new EntradaEstoque();
		System.out.println("Inicio LazyRecebimentoProdutoModel");
		this.recebimentoProdutoModel = getLazyRecebimentoProdutoModel(this.isFabrica);
		
	}
	
	public void initializeListingProdutoFab() {
		// TODO Auto-generated method stub
		this.isFabrica=true;
		this.viewState = ViewState.LISTING;
		this.entradaProduto = new EntradaEstoque();
		System.out.println("Inicio LazyRecebimentoProdutoModel");
		this.recebimentoProdutoModel = getLazyRecebimentoProdutoModel(this.isFabrica);
		
	}
	public void initializeRecProdForm(Long id) {
		// TODO Auto-generated method stub
		this.calcularCusto = calcularCustoMedio();
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.entradaProduto = new EntradaEstoque();
			this.fornecedor = new Fornecedor();
			this.entradaProduto.setAtualizado(false);
			this.entradaProduto.setRecebimentoProduto(true);
			this.precoCusto= new BigDecimal("0");
			this.totalRecebimento = new BigDecimal("0");
		}else {
			this.viewState = ViewState.EDITING;
			this.setHabilita(true);
			this.entradaProduto = this.entradaDao.pegaEntradaEstoquePorID(id);
			this.fornecedor = this.fornecedorDao.findById(this.entradaProduto.getFornecedor().getId(), false);
			this.razaoFornecedor = this.fornecedor.getRazaoSocial();
//			this.listaItemRecProdutoModel = getListaItemRecProdutoModel();
//			this.tipoEstoque = this.entrada.getTipoAtualiza();
			this.precoCusto = new BigDecimal("0");
			this.totalRecebimento = this.entradaProduto.getValorPedidoEntrada();
		}
	}
	public void initializeRecProdFormFabrica(Long id) {
		// TODO Auto-generated method stub
		this.calcularCusto = calcularCustoMedio();
		this.isFabrica = true;
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.entradaProduto = new EntradaEstoque();
			this.fornecedor = new Fornecedor();
			this.entradaProduto.setAtualizado(false);
			this.entradaProduto.setRecebimentoProduto(true);
			this.precoCusto= new BigDecimal("0");
			this.totalRecebimento = new BigDecimal("0");
		}else {
			this.viewState = ViewState.EDITING;
			this.setHabilita(true);
			this.entradaProduto = this.entradaDao.pegaEntradaEstoquePorID(id);
			this.fornecedor = this.fornecedorDao.findById(this.entradaProduto.getFornecedor().getId(), false);
			this.razaoFornecedor = this.fornecedor.getRazaoSocial();
//			this.listaItemRecProdutoModel = getListaItemRecProdutoModel();
//			this.tipoEstoque = this.entrada.getTipoAtualiza();
			this.precoCusto = new BigDecimal("0");
			this.totalRecebimento = this.entradaProduto.getValorPedidoEntrada();
		}
	}
	
	/**
	 * Lista de Pedidos em Aberto modo Lazy
	 * @return lista de pedidos
	 */
	public AbstractLazyModel<EntradaEstoque> getLazyRecebimentoProdutoModel(boolean fab){
		this.recebimentoProdutoModel = new AbstractLazyModel<EntradaEstoque>() {

			/**
			 *
			 */
			private static final long serialVersionUID = -4528606680909138650L;

			@Override
			public List<EntradaEstoque> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());
				Page<EntradaEstoque> page = entradaDao.listaRecProdPorIntervalo(dataInicial,dataFinal,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(), pageRequest,null,null,true,fab);
				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = entradaDao.listaRecProdPorIntervalo(dataInicial,dataFinal, pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase(),true,fab);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				return page.getContent();
			}
		};
		
		return recebimentoProdutoModel;
	}
	
	public void telaRecebimentoTransferencia(){
		this.dataInicial = LocalDate.now().minusDays(7);
		geraListaRecebimentoTransferencia();
		this.updateAndOpenDialog("RecebimentoTransferenciaDialog", "dialogRecebimentoTransferencia");		
	}
	
	public String toEntradaProduto() {
		return "formEntradaProduto.xhtml?faces-redirect=true";
	}
	
	public String toEntradaProdutoFab() {
		return "formEntradaProdutoFab.xhtml?faces-redirect=true";
	}
	
	public String toEditEntradaProduto(Long idEntrada) {
		return "/main/estoque/formEntradaProduto.xhtml?faces-redirect=true&idEntrada=" + idEntrada;
	}
	
	public String toEditEntradaProdutoFab(Long idEntrada) {
		return "/main/fabrica/Estoque/formEntradaProdutoFab.xhtml?faces-redirect=true&idEntrada=" + idEntrada;
	}
	
	public String toListRecebimentoProdutoFab() {
		return "formListRecebimentoProdutoFab.xhtml?faces-redirect=true";
	}
	
	public String toListRecebimentoProduto() {
		return "formListRecebimentoProduto.xhtml?faces-redirect=true";
	}
	/**
	 * Método que cálcula o custo medio do produto e atualiza o custo
	 * @param lista
	 * @throws EstoqueException
	 */
	@Transactional
	public void calculaCustoMedio(List<ItemEstoqueEntrada> lista,boolean custoMedioEmp) throws EstoqueException {
		ProdutoCusto custo = new ProdutoCusto();
		BigDecimal valorTotalEstoqueAnterior = new BigDecimal("0",mc);
		BigDecimal custoAntigo = new BigDecimal("0",mc);
		BigDecimal custoAtual = new BigDecimal("0",mc);
		BigDecimal custoMedio = new BigDecimal("0",mc);
		BigDecimal valorTotalRecebidoHoje = new BigDecimal("0",mc);
		BigDecimal quantidadeEstoqueAntigo = new BigDecimal("0",mc);
		BigDecimal quantidadeEstoqueRecebidoHoje = new BigDecimal("0",mc);

		for (ItemEstoqueEntrada itemEstoque :lista) {
			// Calculando o valor total do estoque antigo

			custo = itemEstoque.getProduto().getListaCustoProduto().get(0);
			custoAtual=itemEstoque.getCustoRecebimento();
			quantidadeEstoqueAntigo = estoqueUtil.estoqueTotalAnteriorPorProduto(itemEstoque.getProduto(), pegaIdEmpresa(), pegaIdFilial());
			if (custo.getCustoMedio().compareTo(new BigDecimal("0"))>0) {
				custoAntigo=custo.getCustoMedio();
			}else {
				if (custo.getCustoAnterior().compareTo(new BigDecimal("0"))>0) { // garantindo que o custo tenha um valor maior que zero
					custoAntigo = custo.getCustoAnterior();
				}else {
					if (custoMedioEmp) {
						if (custo.getCusto().compareTo(new BigDecimal("0"))==0) {
							custo.setCusto(itemEstoque.getCustoRecebimento());
							custoAntigo = custo.getCusto();
						}else {
							if (custo.getCusto().compareTo(new BigDecimal("0"))>0) {
								custoAntigo = custo.getCusto();
							}
						}
					}else {
						if (custo.getCusto().compareTo(new BigDecimal("0"))>0) {
							custoAntigo = custo.getCusto();
						} else {
							throw new EstoqueException(translate("estoqueException.custo.zero"));
						}
					}
				}
			}
			if (quantidadeEstoqueAntigo.compareTo(new BigDecimal("0"))> 0) {
				valorTotalEstoqueAnterior = custoAntigo.multiply(quantidadeEstoqueAntigo,mc).setScale(2,RoundingMode.HALF_EVEN);
				System.out.println("custo antigo = " + custoAntigo + "vl estoque antigo = " + valorTotalEstoqueAnterior);
			}else {
				valorTotalEstoqueAnterior = new BigDecimal("0");
				quantidadeEstoqueAntigo = new BigDecimal("0");
			}
			// Calculado o valor do estoque recebido hoje
			quantidadeEstoqueRecebidoHoje = itemEstoque.getQuantidade();
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
	
	public boolean calcularCustoMedio() {
		boolean calcularCustoMedioTemp = false;
		if (pegaIdFilial() != null) {
			if (this.empresaUsuario.getFil().isGerarCustoMedio()) {
				calcularCustoMedioTemp= true;
			}
		}else {
			if (this.empresaUsuario.getEmp().isGerarCustoMedio()) {
				calcularCustoMedioTemp = true;
			}
		}
		return calcularCustoMedioTemp;
	}
	@Transactional
	public void atualizaRecProd()  {
		try {
			Estoque estoqueTemp = new Estoque();
			
			
			this.entradaProduto = this.entradaDao.pegaEntradaEstoquePorID(this.entradaProduto.getId());
			// atualizando o estoque
			for (ItemEstoqueEntrada itemEstoque : this.entradaProduto.getListaDeItensEntrada()) {
				// atualizando o estoque dos produtos
				estoqueTemp = pegaEstoque(itemEstoque);
				estoqueTemp = this.estoqueUtil.acrescentaEstoqueRecebimentoMaterial(estoqueTemp, itemEstoque.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true,true);
				itemEstoque.setBarras(this.estoqueGeralDao.save(estoqueTemp.getBarrasEstoque()));
			}
			// efetuando o calculo de custo m�dio
			if (calcularCustoMedio()) {
				calculaCustoMedio(this.entradaProduto.getListaDeItensEntrada(),verificaEmpCustoMedio());
			}
			
			this.entradaProduto.setAtualizado(true);
			this.entradaDao.save(this.entradaProduto);

		}catch (EstoqueException e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}catch (Exception x) {
			this.addError(true, "exception.error.fatal", x.getMessage());
		}
	}
	
	@Transactional
	public void entradaRecebimentoProdutoSelecionado(EntradaEstoque entradaSelect){
		try{
			this.entradaRecebimentoProdutoSelecionado = entradaSelect;
			System.out.println("inico da exclusao do EstoqueProduto");
			if (this.entradaRecebimentoProdutoSelecionado != null) {
				if (this.entradaRecebimentoProdutoSelecionado.isAtualizado()) {
					throw new IllegalAccessException(this.translate("estoqueException.atualizado.delete"));
				}else {
					this.entradaDao.delete(this.entradaRecebimentoProdutoSelecionado);
				}
			}else {
				throw new IllegalAccessException(this.translate("estoqueException.estoqueProduto.null"));
			}
			
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}
	
	@Transactional
	public String toSaveRecProd() {
		try {
			if (this.entradaProduto.getId() == null) { // Novo lan�amento de estoque
				for (ItemEstoqueEntrada item : this.entradaProduto.getListaDeItensEntrada()) {
					item.setEntrada(this.entradaProduto);
				}
				this.entradaProduto.setFornecedor(this.fornecedor);
				this.entradaProduto.setDataCriacao(LocalDate.now());
				this.entradaProduto.setHoraCriacao(LocalTime.now());
				this.entradaProduto.setAtualizado(false);
				this.entradaProduto.setRecebimentoProduto(true);
				this.entradaProduto.setValorPedidoEntrada(this.totalRecebimento);
				this.entradaProduto.setFabrica(this.isFabrica);
				this.entradaDao.save(this.entradaProduto);
				
			}else { // Alterando lan�amento de estoque j� salvo
				if (this.entradaProduto.isAtualizado() == false) {
					if (!this.listaItemParaExcluir.isEmpty()) {
						for (ItemEstoqueEntrada itemExclui : this.listaItemParaExcluir) {
							this.itemEstoqueEntradaDao.delete(itemExclui);
						}
					}
					for (ItemEstoqueEntrada item : this.entradaProduto.getListaDeItensEntrada()) {
						if (item.getEntrada() == null) {
							item.setEntrada(this.entradaProduto);
						}
					}
					this.entradaProduto.setValorPedidoEntrada(this.totalRecebimento);
					this.entradaDao.save(this.entradaProduto);
				}
			}
			if (this.isFabrica) {
				return toListRecebimentoProdutoFab();
			}else {
				return toListRecebimentoProduto();
			}
		}catch (Exception e) {
			// TODO: handle exception
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	public void onRowSelectPesquisa(SelectEvent event)throws IOException{
		this.fornecedor = ((Fornecedor) event.getObject());
		System.out.println("estou no rowSelectPesquisa");
		if (this.fornecedor.getId() != null) {
			System.out.println("estou dentro do fornecedor != null");
			this.razaoFornecedor = this.fornecedor.getRazaoSocial();
			this.setHabilita(true);
		}
	}
	
	public void onRowSelectRecProduto(SelectEvent event)throws IOException {
		this.entradaProduto = (EntradaEstoque) event.getObject();
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Retorna form de pesquisa de fornecedores
	 */
	public void telaResultadoFornecedor(){
		this.updateAndOpenDialog("listaResultadoDialog","dialogListaResultado");
	}
	
	public void pesquisaFornecedor() {
		this.listaFornecedores = this.fornecedorDao.localizaFornecedorPorRazaoSocial(this.razaoFornecedor, pegaIdEmpresa());
		telaResultadoFornecedor();   
	}
	public AbstractDataModel<ItemEstoqueEntrada> getListaItemRecProdutoModel(){
		this.listaItemRecProdutoModel = new AbstractDataModel<ItemEstoqueEntrada>(this.entradaProduto.getListaDeItensEntrada());
		return this.listaItemRecProdutoModel;
	}
	
	public void removeItemRecProdutoEstoque(ItemEstoqueEntrada select) {
		if (this.entradaProduto.isAtualizado() == false) {
			if (!this.entradaProduto.getListaDeItensEntrada().isEmpty()){
				this.entradaProduto.getListaDeItensEntrada().remove(select);
				if (this.totalRecebimento.compareTo(new BigDecimal("0"))>0) {
					this.totalRecebimento = this.totalRecebimento.subtract(select.getValorTotal());
				}
			}
			if (this.viewState == ViewState.EDITING) {
				if (select.getId() != null) {
					System.out.println("Estou no Exclui Item ! item ID: " + select.getId());
					this.listaItemParaExcluir.add(select);
				}
			}
			this.listaItemRecProdutoModel = getListaItemRecProdutoModel();
		}
	}
	
	public void initializeRelEstoque() {
		this.viewState = ViewState.LISTING;
		this.dataFinal = LocalDate.now();
		this.dataInicial = LocalDate.now();
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
	
	public void geraEstoqueAll() {
		try {
			
			List<RelEstoqueGeralDTO> data = this.entradaDao.relEstoqueGeral(pegaIdEmpresa(),pegaIdFilial(),estoqueDifereZero);
			
			BigDecimal totalRecebido = new BigDecimal("0");
			BigDecimal totalEstoque = new BigDecimal("0");
			for (RelEstoqueGeralDTO res : data) {
				if (res.getTotalRecebido() != null && res.getEstoqueTotal() != null) {
					totalRecebido = totalRecebido.add(res.getTotalRecebido());
					totalEstoque = totalEstoque.add(res.getEstoqueTotal());
				}
			}
			
			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (data.size() > 0) {
				parametros =  geraParametros();
				parametros.put("totRec", totalRecebido);
				parametros.put("totEstoque", totalEstoque);
				parametros.put("empresa",razaoEmpresaLogada());
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(data,false);
					String path = "/WEB-INF/Relatorios/Estoque/RelEstoqueGeral.jrxml";
					if (exportExcel) {
						relatorios.visualizaXLS(path, parametros, "RelEstoqueGeral" ,jrBean);
					}else {
						relatorios.visualizaPDF(path, parametros, "RelEstoqueGeral" ,jrBean);
					}
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
	
	public void initializeRecTransferenciaForm(Long id) {
		if (id == null) {
			this.addError(true,"caixa.error"," nenhuma transferencia encontrada");
		}else {
			this.viewState = ViewState.ADDING;
			this.pedido = this.pedidoDao.pegaTransferenciaPorId(id);
		}
		
	}
	
	public void onRowSelectTransferencia(SelectEvent event)throws IOException {
		this.pedido = (Pedido) event.getObject();
		this.botaoConfirma=true;
	}
	
	/**
	 * m�todo que retorna a ID da Filial emitente do pedido ou NULL caso nao tenha
	 * sido uma filial o emissor.
	 * @param ped
	 * @return idFilial
	 */
	
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
	    try {
	    	validaOperacao();
	    	
	        pedido = pedidoDao.pegaTransferenciaPorId(pedido.getId());

	        for (ItemPedido itemPedido : pedido.getListaItensPedido()) {
	            Produto produto = produtoDao.pegaProdutoID(
	                itemPedido.getProduto().getId(), pegaIdEmpresa(), pegaIdFilial()
	            );

	            if (produto == null) continue;
	            
	            ProdutoCusto custoOrigem = estoqueUtil.pegaCustoOrigemTransferencia(
            			produto, pegaIdEmpresa(), idFilialEmitente(this.pedido));

            	ProdutoCusto custoDestino = estoqueUtil.pegaCustoDestinoTransferencia(
            			produto, pegaIdEmpresa(), pegaIdFilial());
	            
            	if (empresaUsuario.getEmp().isTranferPreco()) {
            		custoDestino = estoqueUtil.atualizaCustoComPreco(custoDestino, custoOrigem,produto);
            	}else {
            		custoDestino = estoqueUtil.atualizaCustoSemPreco(custoDestino, custoOrigem,produto);
            	}
            	if (calcularCustoMedio()) {
            		custoDestino = estoqueUtil.calculaCustoMedioTransferencia(
            				itemPedido, custoDestino.getCusto(), custoDestino,pegaIdEmpresa() ,pegaIdFilial(),verificaEmpCustoMedio()
            				);
            	}
            	custoDao.save(custoDestino);

            	atualizaEstoqueTransferenciaItem(itemPedido);
	        }

	        pedido.setTransferenciaConcluida(true);
	        pedidoDao.save(pedido);
	        addInfo(true, "transfer.sucess");

	    } catch (EstoqueException e) {
	        addError(true, "caixa.error", e.getMessage());
	    } catch (EstoqueRuntimeException er) {
	        addError(true, "caixa.error", er.getMessage());
	    } catch (Exception e) {
	        addError(true, "caixa.error", e.getMessage());
	    }
	}
	
	
	@RollbackOn({EstoqueException.class})
	private void validaOperacao()throws EstoqueException {
		if (empresaUsuario.getEmp().isTranferAutomatico()) {
			throw new EstoqueException(translate("estoqueException.transfer.automatic"));
		}
		if (pedido.getId() == null) {
			throw new EstoqueException(translate("estoqueException.transferencia.null"));
		}
	}
	
	@RollbackOn({EstoqueException.class,HibernateException.class})
	public void atualizaEstoqueTransferenciaItem(ItemPedido itemEstoque)throws EstoqueException,HibernateException  {
			Estoque estoqueTemp = new Estoque();
				// atualizando o estoque dos produtos
				estoqueTemp = estoqueUtil.preencheEstoqueItem(itemEstoque, pegaIdEmpresa(), pegaIdFilial());
				estoqueTemp = this.estoqueUtil.acrescentaEstoqueRecebimentoMaterial(estoqueTemp, itemEstoque.getQuantidade(), pegaIdEmpresa(), pegaIdFilial(), false,true,true);
				itemEstoque.setBarras(this.estoqueGeralDao.save(estoqueTemp.getBarrasEstoque()));

	}
	
	/**
	 * Método que cálcula o custo medio do produto e atualiza o custo
	 * @param lista
	 * @throws EstoqueException
	 */
	public ProdutoCusto calculaCustoMedioTransferencia(ItemPedido itemEstoque,BigDecimal custoAtual,ProdutoCusto custoItem, boolean custoMedioEmp) throws EstoqueException {
		ProdutoCusto custo = new ProdutoCusto();
		BigDecimal valorTotalEstoqueAnterior = new BigDecimal("0",mc);
		BigDecimal custoAntigo = new BigDecimal("0",mc);
		BigDecimal custoMedio = new BigDecimal("0",mc);
		BigDecimal valorTotalRecebidoHoje = new BigDecimal("0",mc);
		BigDecimal quantidadeEstoqueAntigo = new BigDecimal("0",mc);
		BigDecimal quantidadeEstoqueRecebidoHoje = new BigDecimal("0",mc);

			// Calculando o valor total do estoque antigo

			custo = custoItem;
			quantidadeEstoqueAntigo = estoqueUtil.estoqueTotalAnteriorPorProduto(itemEstoque.getProduto(), pegaIdEmpresa(), pegaIdFilial());
			if (custo.getCustoMedio().compareTo(new BigDecimal("0"))>0) {
				custoAntigo=custo.getCustoMedio();
			}else {
				if (custo.getCustoAnterior().compareTo(new BigDecimal("0"))>0) { // garantindo que o custo tenha um valor maior que zero
					custoAntigo = custo.getCustoAnterior();
				}else {
					if (custoMedioEmp) {
						if (custo.getCusto().compareTo(new BigDecimal("0"))==0) {
							custo.setCusto(custoAtual);
							custoAntigo = custo.getCusto();
						}else {
							if (custo.getCusto().compareTo(new BigDecimal("0"))>0) {
								custoAntigo = custo.getCusto();
							}
						}
					}else {
						if (custo.getCusto().compareTo(new BigDecimal("0"))>0) {
							custoAntigo = custo.getCusto();
						} else {
							throw new EstoqueException(translate("estoqueException.custo.zero"));
						}
					}
				}
			}
			valorTotalEstoqueAnterior = custoAntigo.multiply(quantidadeEstoqueAntigo,mc).setScale(2,RoundingMode.HALF_EVEN);
			System.out.println("custo antigo = " + custoAntigo + "vl estoque antigo = " + valorTotalEstoqueAnterior);
			
			// Calculado o valor do estoque recebido hoje
			quantidadeEstoqueRecebidoHoje = itemEstoque.getQuantidade();
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
			
			return custo;
			
	}
	
	public void geraListaRecebimentoTransferencia() {
		this.botaoConfirma = false;
		this.pedido = new Pedido();
		if (this.getUsuarioAutenticado().getIdFilial() != null){ // entende que uma filial esta tentando gerar a lista
			this.listaRecebimentoTransferencia = this.pedidoDao.listaTransferenciaPorTipoStatusDestino(dataInicial, dataFinal, this.empDao.findById(pegaIdEmpresa(),false),this.filDao.findById(pegaIdFilial(), false),PedidoTipo.TRA,PedidoStatus.REC,false,false);
		}else { // entende que a matriz esta tentando gerar a lista
			this.listaRecebimentoTransferencia = this.pedidoDao.listaTransferenciaPorTipoStatusDestino(dataInicial, dataFinal, this.empDao.findById(pegaIdEmpresa(),false),null,PedidoTipo.TRA,PedidoStatus.REC,false,true);
		}
	}
	
}
