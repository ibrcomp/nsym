package br.com.nsym.application.controller.financial;

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
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.CaixaException;
import br.com.ibrcomp.exception.FinanceiroException;
import br.com.ibrcomp.exception.RegraNegocioException;
import br.com.ibrcomp.exception.RelatoriosException;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.application.controller.relatorios.RelatorioVendas;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.AgTitulo;
import br.com.nsym.domain.model.entity.financeiro.AgTituloIDDTO;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.CartaoInf;
import br.com.nsym.domain.model.entity.financeiro.ChequeInf;
import br.com.nsym.domain.model.entity.financeiro.ContaCorrente;
import br.com.nsym.domain.model.entity.financeiro.ParcelaStatus;
import br.com.nsym.domain.model.entity.financeiro.ParcelaStatusSimplificada;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamentoSimples;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoLancamento;
import br.com.nsym.domain.model.entity.financeiro.tools.AgendaDTO;
import br.com.nsym.domain.model.entity.financeiro.tools.AgendaDTOPdf;
import br.com.nsym.domain.model.entity.financeiro.tools.CaixaUtil;
import br.com.nsym.domain.model.entity.financeiro.tools.MovimentoEnum;
import br.com.nsym.domain.model.entity.financeiro.tools.Parcelas;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoPesquisa;
import br.com.nsym.domain.model.entity.tools.CaixaFinalidade;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.repository.cadastro.ClienteRepository;
import br.com.nsym.domain.model.repository.cadastro.ColaboradorRepository;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.financeiro.AgTituloRepository;
import br.com.nsym.domain.model.repository.financeiro.CaixaRepository;
import br.com.nsym.domain.model.repository.financeiro.ContaCorrenteRepository;
import br.com.nsym.domain.model.repository.financeiro.RecebimentoParcialRepository;
import br.com.nsym.domain.model.repository.financeiro.tools.ParcelasNfeRepository;
import br.com.nsym.domain.model.repository.venda.AgPedidoRepository;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class AgendaBean extends AbstractBeanEmpDS<Parcelas>{
	/**
	 *
	 */
	private static final long serialVersionUID = -6431079689198313213L;
	
	@Getter
	@Setter
	private ParcelasNfe parcela = new ParcelasNfe();
	
	@Inject
	private ParcelasNfeRepository parcelasDao;
	
	@Getter
	private AbstractLazyModel<ParcelasNfe> parcelasModel;
	
	@Setter
	private List<AgendaDTO> listaParcelas;
	
	@Getter
	@Setter
	private AgendaDTO agenda = new AgendaDTO();
	
	@Getter
	@Setter
	private boolean isDeleted = false;
	
	@Getter
	@Setter
	private LocalDate dataIni = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataFim = LocalDate.now();
	
	@Getter
	private List<ContaCorrente> listaContas = new ArrayList<ContaCorrente>();
	
	@Inject
	private ContaCorrenteRepository contaDao;
	
	@Getter
	@Setter
	private BigDecimal totalCredito = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalCreditoRec = new BigDecimal("0");
	
	
	@Getter
	@Setter
	private BigDecimal totalCreditoLiq = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalDebito = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalDebitoPag = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalDebitoLiq = new BigDecimal("0");
	
	@Getter
	@Setter
	private transient CartaoInf cartaoInf ;
	
	@Getter
	@Setter
	private transient ChequeInf chequeInf ;
	
	@Getter
	@Setter
	private boolean detailView = false;
	
	@Getter
	@Setter
	private boolean tipoBuscaPorTitulo = true;
	
	@Getter
	@Setter
	private List<ParcelasNfe> listaParcelasSelecionadas = new ArrayList<>();
	
	@Getter
	@Setter
	private BigDecimal totalTitulos =  new BigDecimal("0");
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPesquisa tipoPesquisa;
	
	@Getter
	@Setter
	private String clientePesquisa;
	
	@Getter
	@Setter
	private List<Cliente> listaCliente = new ArrayList<>();
	
	@Inject
	private ClienteRepository clienteDao;
	
	@Getter
	@Setter
	private Cliente cliente = new Cliente();
	
	@Getter
	@Setter
	private List<Colaborador> listaColaborador  = new ArrayList<>();
	
	@Inject
	private ColaboradorRepository colaboradorDao;
	
	@Getter
	@Setter
	private Colaborador colaborador =  new Colaborador();
	
	@Getter
	@Setter
	private List<Fornecedor> listaFornecedor = new ArrayList<>();
	
	@Inject
	private FornecedorRepository fornecedorDao;
	
	@Getter
	@Setter
	private Fornecedor fornecedor = new Fornecedor();
	
	@Getter
	@Setter
	private TipoLancamento tipoLancamento = TipoLancamento.tpAll;
	
	@Getter
	@Setter
	private ParcelaStatusSimplificada statusParcela = ParcelaStatusSimplificada.ALL;
	
	@Inject
	private CaixaUtil caixaUtil;
	
	@Getter
	@Setter
	private Caixa caixa;
	
	@Inject
	private CaixaRepository caixaDao;
	
	@Getter
	@Setter
	private AgTitulo agTitulo;
	
	@Inject
	private AgTituloRepository agTituloDao;
	
	@Getter
	@Setter
	private BigDecimal totalGT = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal totalBruto = new BigDecimal("0");

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
	private boolean caixaAberto = false;
	
	@Getter
	@Setter
	private TipoPagamento tipoPagamento;
	
	@Getter
	@Setter
	private List<RecebimentoParcial> listaRecebimentos = new ArrayList<RecebimentoParcial>();
	
	@Getter
	@Setter
	private HashMap<TipoPagamento, BigDecimal> hashRecebimentoParcialCaixa = new HashMap<TipoPagamento, BigDecimal>();
	
	@Getter
	@Setter
	private RecebimentoParcial recebimentoParcial;
	
	@Inject
	private RecebimentoParcialRepository recebimentoDao;
	
	@Getter
	@Setter
	private Configuration configUser;
	
	@Getter
	@Setter
	private EmpUser empresaUsuario;
	
	@Getter
	@Setter
	private boolean tipoDesconto = true;
	
	@Getter
	@Setter
	private boolean tipoAcrescimo = false;
	
	@Getter
	private List<AgTitulo> listaAgTitulos= new ArrayList<AgTitulo>();
	
	@Getter
	@Setter
	private transient List<ParcelasNfe> listaParcTemp = new ArrayList<>();
	
	@Getter
	@Setter
	private List<ParcelasNfe> listaParcelasRecorrentes = new ArrayList<>();
	
	@Inject
	private RelatorioVendas relatorios;
	
	@Getter
	@Setter
	private ContaCorrente contaCorrente;
	
	@Setter
	private List<RecebimentoParcial> listaRecParcialPorTitulos = new ArrayList<>();
	
	@Getter
	@Setter
	private String procuraOrigem;
	
	@Getter
	@Setter
	private BigDecimal valorTotalDosTitulosRecebidos = new BigDecimal("0");
	
	@Setter
	private List<ParcelasNfe> listaDeTitulosRecebidos;
	
	@Getter
	@Setter
	private List<AgTituloIDDTO> tempListaAgTitulo ;
	
	@Getter
	@Setter
	private boolean filtroDPEspecial = false;
	
	@Getter
	@Setter
	private AgPedido agrupado;
	
	@Inject
	private AgPedidoRepository agrupadoDao;
	
	@Getter
	@Setter
	private BigDecimal valorTotalAgrupado = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal valorRecebidoAgrupado = new BigDecimal("0");
	
	@Getter
	@Setter
	private String obsAgrupado;
	
	
	@PostConstruct
	public void init(){
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}

	
	public List<AgendaDTO> getListaParcelas(){
//		PageRequest pageRequest = new PageRequest();
//		pageRequest.setFirstResult(0).withPageSize(5)
//		.withDirection(SortOrder.ASCENDING.name());
		this.listaParcelas = parcelasDao.parcelasLazyComFiltro(isDeleted(),dataIni,dataFim, pegaIdEmpresa(), pegaIdFilial(), null,null, null,null, true,null,tipoLancamento,ParcelaStatus.valueOf(statusParcela.getCod()),procuraOrigem,filtroDPEspecial);
		return this.listaParcelas;
	}
//	
	/**
	 * 
	 * @param id - id da classe  a ser pesquisada
	 * @param idEmpresa -  id da Empresa
	 * @param idFilial - id da Filial
	 * @param tipo - Tipo de Cadastro
	 * @return os contatos em modo Lazy
	 */
	public AbstractLazyModel<ParcelasNfe> getLazyAgenda(){

		this.parcelasModel = new AbstractLazyModel<ParcelasNfe>() {
			
			/**
			 *
			 */
			private static final long serialVersionUID = -2274325506136912707L;
			@Override
			public List<ParcelasNfe> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();
				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "vencimento")
				.withDirection(sortOrder.name());
				Page<ParcelasNfe> page = new Page<ParcelasNfe>();
				page = parcelasDao.listaParcelasLazyComFiltro(isDeleted(),dataIni,dataFim, pegaIdEmpresa(), pegaIdFilial(), pageRequest, null, null, false,null,tipoLancamento);
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = parcelasDao.listaParcelasLazyComFiltro(isDeleted(), dataIni,dataFim,pegaIdEmpresa(), pegaIdFilial(),pageRequest,filterProperty, filterValue.toString(),false,null,tipoLancamento);
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				this.setRowCount(page.getTotalPagesInt());
				return page.getContent();
			}
		};
		return parcelasModel;
	}
	
	/**
	 * 
	 * @param id - id da classe  a ser pesquisada
	 * @param idEmpresa -  id da Empresa
	 * @param idFilial - id da Filial
	 * @param tipo - Tipo de Cadastro
	 * @return os contatos em modo Lazy
	 */
	public AbstractLazyModel<ParcelasNfe> getLazyGerir(){

		this.parcelasModel = new AbstractLazyModel<ParcelasNfe>() {
			/**
			 *
			 */
			private static final long serialVersionUID = -2274325506136912707L;

			@Override
			public List<ParcelasNfe> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "vencimento")
				.withDirection(sortOrder.name());
				Page<ParcelasNfe> page = new Page<ParcelasNfe>();
				page = parcelasDao.listaParcelasLazyComFiltroGerir(isDeleted(),dataIni,dataFim, pegaIdEmpresa(), pegaIdFilial(), null,  pageRequest, null, null, false,null,tipoLancamento);
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = parcelasDao.listaParcelasLazyComFiltroGerir(isDeleted(), dataIni,dataFim,pegaIdEmpresa(), pegaIdFilial(),null, pageRequest,filterProperty, filterValue.toString(),false,null,tipoLancamento);
//							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return parcelasModel;
	}

	@Override
	public Parcelas setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parcelas setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
//		this.parcelasModel = getLazyAgenda();
		this.agTitulo = new AgTitulo();
		this.agrupado = new AgPedido();
		this.listaParcelas = getListaParcelas();
		this.geraTotaisPeriodo();
	}
	 
	
	public void initializeGerir() {
		try {
			this.caixa = caixaUtil.retornaCaixa(CaixaFinalidade.nao);
			if (this.caixa != null) {
				this.caixaAberto = true;
				this.viewState = ViewState.LISTING;
				this.parcelasModel = getLazyGerir();
				this.cliente = new Cliente();
				this.colaborador = new Colaborador();
				this.fornecedor = new Fornecedor();
				this.parcela = new ParcelasNfe();
				this.totalTitulos = new BigDecimal("0");
				this.agTitulo = new AgTitulo();
				this.listaParcelasSelecionadas = new ArrayList<>();
			}
		}catch (CaixaException c) {
			this.addError(true, "caixa.error", c.getMessage());
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	public void initializeRecebeTitulo(Long idAgTitulo) {
		try {
			this.caixa = caixaUtil.retornaCaixa(CaixaFinalidade.rece);
			this.agTitulo = new AgTitulo();
			this.contaCorrente = new ContaCorrente();
			this.listaContas  = contaDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(),false ,false);
			if (this.caixa != null) {
				this.agTitulo = this.agTituloDao.encontraAgTituloPorId(idAgTitulo);
				if (this.agTitulo != null) {
					this.viewState = ViewState.EDITING;
					this.recebimentoParcial = new RecebimentoParcial();
					this.listaRecebimentos = new ArrayList<>();
					this.totalGT = this.agTitulo.getValorTotal().subtract(this.agTitulo.getValorRecebido());
					this.totalBruto = this.agTitulo.getValorBruto();
					this.resto = this.agTitulo.getValorTotal().subtract(this.agTitulo.getValorRecebido());
					
				}else {
					throw new FinanceiroException(translate("financeiro.group.notFound")+ idAgTitulo);
				}
			}
		}catch (FinanceiroException c) {
			this.addError(true, "caixa.error", c.getMessage());
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	public Long pegaIdCadastro() {
		if (this.tipoPesquisa == TipoPesquisa.CLI) {
			return this.cliente.getId();
		}else {
			if (this.tipoPesquisa == TipoPesquisa.COL) {
				return this.colaborador.getId();
			}else {
				return this.fornecedor.getId();
			}
		}
	}
	
	public AbstractLazyModel<ParcelasNfe> getListaParcelasPorCadastro(){

		this.parcelasModel = new AbstractLazyModel<ParcelasNfe>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 8205337253783791182L;

			@Override
			public List<ParcelasNfe> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no contatoModel");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "vencimento")
				.withDirection(sortOrder.name());
				Page<ParcelasNfe> page = new Page<ParcelasNfe>();
				page = parcelasDao.listaParcelasPorCliente(isDeleted(),dataIni,dataFim, pegaIdEmpresa(), pegaIdFilial(), pageRequest, tipoPesquisa, pegaIdCadastro(), tipoLancamento,true,true);
				//				if (filters != null){
				//					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
				//						try{
				//							String filterProperty = it.next();
				//							Object filterValue = filters.get(filterProperty);
				//							page = parcelasDao.listaParcelasLazyComFiltro(isDeleted(), dataIni,dataFim,pegaIdEmpresa(), pegaIdFilial(),null, pageRequest,filterProperty, filterValue.toString(),false,null);
				//						} catch(Exception e) {
				//							System.out.println(e.getMessage());
				//						}
				//					}
				//				}
				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return parcelasModel;
	}
	public void localizaTitulos() {
		try {
			System.out.println("cliente: " + this.cliente.getId() + "tipoPesquisa: " + this.tipoPesquisa);
			if (this.tipoBuscaPorTitulo == true) {
				initializeGerir();
			}else {
				if (this.tipoPesquisa != null) {
					if (this.cliente != null) {
						this.parcelasModel = getListaParcelasPorCadastro();
					}else {
						throw new CaixaException("Cliente nullo");
					}
				}else {
					if (this.cliente != null) {
						throw new CaixaException("Apenas tipoPesquisa nulo");
					}else {
						throw new CaixaException("ambos nulo");
					}
				}
			}
		}catch (CaixaException c){
			this.addError(true,"caixa.error",c.getMessage());
		}catch (Exception e) {
			this.addError(true,"caixa.error",e.getMessage());
		}
	}
	
	public void pesquisaDestinatario(){
		if (this.tipoPesquisa == TipoPesquisa.CLI){
			System.out.println("fiz cli");
			this.cliente = new Cliente();
			this.listaCliente = this.clienteDao.pesquisaTexto(this.clientePesquisa, getUsuarioAutenticado().getIdEmpresa(), null);
			telaResultadoDestinatario();
		}
		if (this.tipoPesquisa == TipoPesquisa.FOR){
			System.out.println("fiz for");
			this.fornecedor = new Fornecedor();
			this.listaFornecedor = this.fornecedorDao.pesquisaTexto(this.clientePesquisa, getUsuarioAutenticado().getIdEmpresa(), null);
			telaResultadoDestinatario();
		}
		if (this.tipoPesquisa == TipoPesquisa.COL){
			this.colaborador = new Colaborador();
			this.listaColaborador = this.colaboradorDao.pesquisaTexto(this.clientePesquisa, getUsuarioAutenticado().getIdEmpresa(), null);
			telaResultadoDestinatario();
		}
	}
	/**
	 * Método que agrupa todos os titulos selecionados em um (Grande Titulo), assim, permitir a baixa de varios titulos aos mesmos tempo.
	 * @return se agrupado com sucesso retorna pagina para prencher os titulos
	 * @throws CaixaException
	 */
	@Transactional
	public String agrupa() throws FinanceiroException {
		try {
			this.totalGT = new BigDecimal("0");
			BigDecimal desconto = new BigDecimal("0");
			BigDecimal acrescimo = new BigDecimal("0");
			
			if (this.listaParcelasSelecionadas.size() != 0) {
				if (this.listaParcelasSelecionadas.size() > 20) {
					throw new FinanceiroException(translate("financeiro.limit.titulo.max"));
				}else {
					for (ParcelasNfe titulo : this.listaParcelasSelecionadas) {
						this.agTitulo.getListaTitulosAgrupados().add(titulo);
						this.totalGT = this.totalGT.add(titulo.getValorParcela());
						if (titulo.getValorOriginal() != null) {
							if (titulo.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
								this.totalBruto = this.totalBruto.add(titulo.getValorOriginal().subtract(titulo.getValorRecebido()));
							}else {
								this.totalBruto = this.totalBruto.add(titulo.getValorParcela().subtract(titulo.getValorRecebido()));
							}
						}else {
							this.totalBruto = this.totalBruto.add(titulo.getValorParcela().subtract(titulo.getValorRecebido()));
						}
					}

					this.agTitulo.setValorTotal(this.totalGT);
					this.agTitulo.setValorBruto(this.totalBruto);
					this.agTitulo.setDesconto(desconto);
					this.agTitulo.setAcrescimo(acrescimo);
					this.agTitulo.setDataCriacao(LocalDate.now());
					this.agTitulo.setHoraCriacao(LocalTime.now());
					this.agTitulo.setStatus(PedidoStatus.AgR);
					this.agTitulo.setCaixa(this.caixa);
					this.agTitulo = this.agTituloDao.save(this.agTitulo);
				}
			}else {
				throw new FinanceiroException(this.translate("financeiro.list.isEmpty"));
			}
			for (ParcelasNfe parc : listaParcelasSelecionadas) {
//				parc.getListaAgTitulo().add(this.agTitulo);
				parc.setStatus(ParcelaStatus.AGR);
				parcelasDao.save(parc);

			}
			return toRecebeTitulo(this.agTitulo.getId());
		}catch (FinanceiroException f) {
			// TODO: handle exception
			this.addError(true, "financeiro.error", f.getMessage());
			return null;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Redireciona para tela Principal
	 * @return Dashboard
	 */
	public String toRecebeTitulo(Long idAgPedido) {
		return "/main/financial/financeiro/Baixa/formRecebeTitulo.xhtml?faces-redirect=true&idAgTitulo=" + idAgPedido;
	}		
	
	public void inserePagamento()   {
		try {
			if (this.tipoPagamento == null) {
				throw new CaixaException(this.translate("caixa.payment.notSelect"));
			}else {
				boolean pagamentoAceito = false;
				for (TipoPagamentoSimples tipo : TipoPagamentoSimples.values()) {
					if (this.tipoPagamento.equals(caixaUtil.converteTipoSimples(tipo))){
						pagamentoAceito = true;
					}
				}
//				if (this.tipoPagamento.equals(TipoPagamento.Crl)){
//					if (this.credito.getSaldoCreditoDevolucao().compareTo(new BigDecimal("0"))>0 && this.credito.getSaldoCreditoDevolucao().compareTo(this.valorRecebido)>=0) {
//						pagamentoAceito= true;
//					}else {
//						throw new CaixaException(this.translate("caixa.payment.credit.equal.zero"));
//					}
//				}
				if (pagamentoAceito == false) {
						throw new CaixaException(this.translate("caixa.payment.notAllowed") + this.tipoPagamento.toString());
				}else {
					addRecebimentoParcial(this.tipoPagamento, this.valorRecebido);
					this.valorRecebido = new BigDecimal("0");
					this.tipoPagamento = null;
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
	
	public void addRecebimentoParcial(TipoPagamento tipo,BigDecimal valor)throws CaixaException{
		if (tipo == null) {
			throw new CaixaException(translate("caixa.error.forma.notSelect"));
		}
		if (valor.compareTo(new BigDecimal("0"))<1) {
			throw new CaixaException(translate("caixa.error.valor.isEmpty"));
		}
		System.out.println("Resultado comparacao: " + valor.compareTo(this.resto) + " tipo Pagamento informado: " + tipo
				+ "Equals tipopagamento com ! : " + !tipo.equals(TipoPagamento.Din));
		if (this.resto.compareTo(new BigDecimal("0"))>0) {
			if (valor.compareTo(this.resto)>0 && !tipo.equals(TipoPagamento.Din)) {
				throw new CaixaException(this.translate("caixa.valorMaiorQuePermitido"));
			}else {
				if (tipo.equals(TipoPagamento.Din)) {
					if (valor.compareTo(this.resto)>0) {
						this.totalRecebido = this.totalRecebido.add(valor);
						this.troco = valor.subtract(this.resto);
						this.resto = this.totalGT.subtract(this.totalRecebido).add(this.troco);
					}else {
						this.totalRecebido = this.totalRecebido.add(valor);
						this.resto = this.totalGT.subtract(this.totalRecebido);
					}
				}else {
//					if (tipo.equals(TipoPagamento.Crl)) {
//						this.credito.setSaldoCreditoDevolucao(this.credito.getSaldoCreditoDevolucao().subtract(valor));
//					}
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
	
	public List<Map.Entry<TipoPagamento, BigDecimal>> getListaTipoPagamentoInserido(){
		Set<Map.Entry<TipoPagamento, BigDecimal>> TipoPagamentoSet = this.hashRecebimentoParcialCaixa.entrySet();
		return new ArrayList<Map.Entry<TipoPagamento,BigDecimal>> (TipoPagamentoSet);
	}
	
	public void removePagamento(TipoPagamento forma) {
		removeRecebimentoParcial(forma);
	}
	
	public void removeRecebimentoParcial(TipoPagamento forma) {
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
//			if (forma.getTipoPagamento().equals(TipoPagamento.Crl)) {
//				this.credito.setSaldoCreditoDevolucao(this.credito.getSaldoCreditoDevolucao().add(this.hashRecebimentoParcialCaixa.get(forma)));
//			}
			this.hashRecebimentoParcialCaixa.remove(forma);
			// removendo as parcelas que corresponde a forma de pagamento
//			List<ParcelasNfe> listaTempRemover = new ArrayList<ParcelasNfe>();
//			for(Iterator<ParcelasNfe> parc = this.listaParcelasPagamento.iterator(); parc.hasNext();) {
//				ParcelasNfe parcela = parc.next();
//				if (parcela.getFormaPag().equals(forma)) {
//					listaTempRemover.add(parcela);
//				}
//			}
//			this.listaParcelasPagamento.removeAll(listaTempRemover);
		}
	}
	
	/**
	 * Evento que controla o item da lista selecionado na tela de pesquisa
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectPesquisa(SelectEvent event)throws IOException{

		if (this.tipoPesquisa == TipoPesquisa.CLI){
			this.cliente = ((Cliente) event.getObject());
			this.clientePesquisa = this.cliente.getRazaoSocial();
		}
		if (this.tipoPesquisa == TipoPesquisa.FOR){
			this.fornecedor = (Fornecedor) event.getObject();
			this.clientePesquisa = this.fornecedor.getRazaoSocial();
		}
		if (this.tipoPesquisa == TipoPesquisa.COL){
			this.colaborador = (Colaborador) event.getObject();
			this.clientePesquisa = this.colaborador.getNome();
		}
	}
	
	/**
	 * Retorna form de pesquisa de destinatario 
	 */

	public void telaResultadoDestinatario(){
		this.updateAndOpenDialog("listaResultadoDialog","dialogListaResultado");
	}
	
	public void onRowAddParcela(SelectEvent event)throws IOException,CaixaException{
		try {
		boolean naoTemNaLista = true;
		boolean tipoLancamentoIgual = false;
		boolean tipoPagamentoIgual = false;
		this.parcela = (ParcelasNfe)event.getObject();
		if (this.listaParcelasSelecionadas.isEmpty()) { 
			this.listaParcelasSelecionadas.add(this.parcela);
			this.totalTitulos = this.totalTitulos.add(this.parcela.getValorParcela());
		}else {
			for (Iterator<ParcelasNfe> it = this.listaParcelasSelecionadas.iterator(); it.hasNext();){
				ParcelasNfe parc = it.next();
				if (parc.getTipoLancamento().equals(this.parcela.getTipoLancamento())) {
					tipoLancamentoIgual = true;
					for (ParcelasNfe temNaLista : this.listaParcelasSelecionadas) {
						if (temNaLista.equals(this.parcela)) {
							naoTemNaLista = false;
						}
						if (temNaLista.getTipoPagamento().equals(this.parcela.getTipoPagamento())) {
							tipoPagamentoIgual = true;
						}else {
							throw new FinanceiroException(this.translate("financeiro.info.error.tipoPagamento"));
						}
					}
				}
			}
			if (naoTemNaLista && tipoLancamentoIgual && tipoPagamentoIgual) {
				this.listaParcelasSelecionadas.add(this.parcela);
				this.totalTitulos = this.totalTitulos.add(this.parcela.getValorParcela());
			}
		}
		}catch (FinanceiroException f){
			this.addWarning(true, "financeiro.info.error", f.getLocalizedMessage());
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}
	
	public void excluiParcela(ParcelasNfe parcelaSelected){
		try{
			this.listaParcelasSelecionadas.remove(parcelaSelected);
			this.totalTitulos = this.totalTitulos.subtract(parcelaSelected.getValorParcela());
		}catch (Exception e){
			this.addError(true, "exception.error.fatal", e.getLocalizedMessage());
		}
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		try {
		this.listaContas  = contaDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(),false ,false);
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.parcela = new ParcelasNfe();
			this.chequeInf = new ChequeInf();
			this.cartaoInf = new CartaoInf();
		}else {
			this.parcela = this.parcelasDao.pegaParcela(pegaIdEmpresa(),pegaIdFilial(),id);
			if (this.parcela != null) {
				if (this.parcela.getStatus().equals(ParcelaStatus.REC)) {
					this.viewState = ViewState.DETAILING;
				}else {
					this.viewState = ViewState.EDITING;
				}
				if (this.parcela.getCheque() == null) {
					this.chequeInf = new ChequeInf();
				}else {
					this.chequeInf = this.parcela.getCheque();
				}
				if (this.parcela.getCartao() == null) {
					this.cartaoInf = new CartaoInf();
				}else {
					this.cartaoInf = this.parcela.getCartao();
				}
				viewDetailContions();
			}else {
				throw new FinanceiroException(this.translate("financeiro.titulo.isNull"));
			}
		}
		}catch (FinanceiroException f) {
			this.addError(true,"financial.error",f.getMessage());
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	/**
	 * redireciona para a pagina com o ID do Titulo.
	 * 
	 * @param recID
	 * 
	 * @returna pagina de edicao de titulo
	 */
	public String changeToEdit(Long titID) {
		return "formCadTitulo.xhtml?faces-redirect=true&recID=" + titID;
	}
	
	public String toGerirTitulo() {
		return "formGerirTitulos.xhtml?faces-redirect=true";
	}
	/**
	 *  método que define a exibição do botao info adicional da forma de pagamento
	 */
	public void viewDetailContions () {
		if (this.parcela != null) {
			if (this.parcela.getTipoPagamento().compareTo(TipoPagamento.Che)==0) {
				this.detailView = true;
			}else {
				if (this.parcela.getTipoPagamento().compareTo(TipoPagamento.Car) == 0 
						|| this.parcela.getTipoPagamento().compareTo(TipoPagamento.Cde)==0) {
					this.detailView = true;
				}else {
					this.detailView = false;
				}
			}
		}
	}
	
	/**
	 * redireciona para Cadastro de novos Titulos
	 * @return pagina de inclusao de Titiulo
	 */
	public String newTitulo() {
		return "formCadTitulo.xhtml?faces-redirect=true";
	}
	
	/**
	 * redireciona para Agenda Financeira
	 * @return pagina Agenda Financeira
	 */
	public String toAgenda() {
		return "formAgenda.xhtml?faces-redirect=true";
	}
	
	public void onRowSelect(SelectEvent event)throws IOException,CaixaException{
		this.agenda = (AgendaDTO) event.getObject();

		
		this.viewState = ViewState.EDITING;
	}
	
	public TipoPagamento[] listaPagamentos() {
		return TipoPagamento.values();
	}
	
	public TipoLancamento[] listaTiposLancamento() {
		return TipoLancamento.values();
	}
	
	public ParcelaStatusSimplificada[] listaParcelaStatus() {
		return ParcelaStatusSimplificada.values();
	}
	
	public boolean disableChange() {
		boolean retorno = false;
		if (this.parcela.getId() != null) {

			if (!this.parcela.getStatus().equals(ParcelaStatus.ABE)) {
				retorno = true;
			}
			if (this.viewState.equals(ViewState.DETAILING)) {
				retorno = true;
			}
		}
		return retorno;
	}
	
	public boolean readOnly() {
		boolean retorno = false;
		if (this.parcela.getId() != null) {
			if (! this.parcela.isOrigemAgenda()) {
				retorno = true;
			}
			if (!this.parcela.getStatus().equals(ParcelaStatus.ABE)) {
				retorno = true;
			}
			if (this.viewState.equals(ViewState.DETAILING)) {
				retorno = true;
			}
		}
		return retorno;
	}
	
	public void geraTitulos(){
		try {
			if (this.listaParcelasRecorrentes.size() >0 ) {
				this.listaParcelasRecorrentes = new ArrayList<>();
			}
			if (this.parcela.isRecorrente()) {
				if (this.parcela.getTipoPagamento() == null) {
					throw new FinanceiroException(this.translate("financial.error.recorrente.notTipoPagamento"));
				}
				if(this.parcela.getQRecorrencia() > 0) {
					for (int i = 1; i <  this.parcela.getQRecorrencia(); i++) {
						int dias = i*30;
						ParcelasNfe parc = new ParcelasNfe();
						parc.setConta(this.parcela.getConta());
						parc.setValorParcela(this.parcela.getValorParcela());
						parc.setVencimento(this.parcela.getVencimento().plusDays(dias));
						parc.setDeleted(false);
						parc.setDescricao(this.parcela.getDescricao());
						parc.setFormaPag(this.parcela.getFormaPag());
						parc.setFinanceiro(this.parcela.isFinanceiro());
						parc.setNumParcela(new BigDecimal(i+1).longValue());
						parc.setPessoal(this.parcela.isPessoal());
						parc.setRecorrente(this.parcela.isRecorrente());
						parc.setStatus(ParcelaStatus.ABE);
						parc.setTipoLancamento(this.parcela.getTipoLancamento());
						parc.setTipoPagamento(this.parcela.getTipoPagamento());
						parc.setQRecorrencia(this.parcela.getQRecorrencia());
						parc.setOrigemAgenda(true);
						parc.setCartao(this.cartaoInf);
						parc.setCheque(this.chequeInf);
						this.listaParcelasRecorrentes.add(parc);
					}
				}else {
					throw new FinanceiroException(this.translate("financial.error.recorrente.equalzero"));
				}
			}
		}catch (FinanceiroException fe) {
			this.addError(true,"financial.error",fe.getCause());
		}

	}
	
	@Transactional
	public void salvaTitulo() {
		try {
			if (this.parcela.getId() == null) {
				this.parcela.setOrigemAgenda(true);
				this.parcela.setStatus(ParcelaStatus.ABE);
				if (this.parcela.isRecorrente()) {
//			
					this.parcela.setNumParcela(new BigDecimal(1).longValue());
					for (ParcelasNfe parcelasNfe : this.listaParcelasRecorrentes) {
						this.parcelasDao.save(parcelasNfe);
					}
					this.parcela = this.parcelasDao.save(this.parcela);
				}else {
					this.parcela.setCartao(this.cartaoInf);
					this.parcela.setCheque(this.chequeInf);
					this.parcela = this.parcelasDao.save(this.parcela);
				}
			}else {
				this.parcela = this.parcelasDao.save(this.parcela);
			}
			this.addInfo(true,"save.sucess",this.parcela.getId());
		}catch (HibernateException c) {
			this.addWarning(true, "hibernate.persist.fail", c.getMessage());
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	public int totalParcelas(ParcelasNfe parc) {
		int i = 1;
		if (parc.getQRecorrencia() > 0) {
			i= parc.getQRecorrencia();
		}else {
			if (parc.getAgPedido() != null) {
				if (parc.getAgPedido().getListaParcelas().size() > 0) {
					i = parc.getAgPedido().getListaParcelas().size();
				}
			}else {
				if (parc.getNfe() != null) {
					i = parc.getNfe().getFormaPagamento().getParcelas();
				}else {
					i = 1;
				}
			}
		}
		return i;
	}
	
	public void exibeCompPagamento() {
		if (this.parcela.getTipoPagamento().compareTo(TipoPagamento.Che)==0) {
			this.openDialog("dlgFormaCheque");
		}
		if (this.parcela.getTipoPagamento().compareTo(TipoPagamento.Car) == 0 
					|| this.parcela.getTipoPagamento().compareTo(TipoPagamento.Cde)==0) {
				this.openDialog("dlgFormCartao");
		}
		if (this.parcela.isRecorrente() && this.viewState.equals(ViewState.ADDING)) {
			if (this.listaParcelasRecorrentes == null || this.listaParcelasRecorrentes.size() > 0 ) {
				this.listaParcelasRecorrentes = new ArrayList<>();
			}
		}
		viewDetailContions();
	}
	
	public void zeraListaRecorrente() {
		if (this.parcela.isRecorrente() && this.viewState.equals(ViewState.ADDING)) {
			if (this.listaParcelasRecorrentes == null || this.listaParcelasRecorrentes.size() > 0 ) {
				this.listaParcelasRecorrentes = new ArrayList<>();
			}
		}
	}
	
	public void geraTotaisPeriodo() {
		if (this.listaParcelas.size() >0) {
			this.totalCredito =this.listaParcelas.get(0).getTotalizador().getTotalCredito();
			this.totalCreditoRec = this.listaParcelas.get(0).getTotalizador().getTotalCreditoRec();
			this.totalDebito = this.listaParcelas.get(0).getTotalizador().getTotalDebito();
			this.totalDebitoPag = this.listaParcelas.get(0).getTotalizador().getTotalDebitoPag();
			this.totalCreditoLiq = this.listaParcelas.get(0).getTotalizador().getTotalCreditoLiq();
			this.totalDebitoLiq =this.listaParcelas.get(0).getTotalizador().getTotalDebitoLiq();
		}else {
			this.totalCredito = new BigDecimal("0");
			this.totalCreditoRec = new BigDecimal("0");
			this.totalDebito = new BigDecimal("0");
			this.totalDebitoPag = new BigDecimal("0");
			this.totalCreditoLiq = new BigDecimal("0");
			this.totalDebitoLiq = new BigDecimal("0");
		}
	}
	
	/**toTela
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaAgPedido(){
		novaListaAgTitulos();
		this.updateAndOpenDialog("PesquisaAgTituloDialog", "dialogPesquisaAgTitulo");
	}
	
	/**
	 * Metodo que remove o agrupamento, desvinculando os pedidos 
	 */
	@Transactional
	public String removeAgrupamento() {
		try {
			if (this.agTitulo != null) {
				List<ParcelasNfe> listaPedidos = new ArrayList<ParcelasNfe>();
				listaPedidos = this.agTitulo.getListaTitulosAgrupados();
				for (ParcelasNfe pedido : listaPedidos) {
//					pedido.setAgTitulo(null);
					if (pedido.getValorOriginal() != null || pedido.getValorOriginal().compareTo(new BigDecimal("0"))>0) {
						if(pedido.getValorParcela().compareTo(pedido.getValorOriginal())==0) {
							pedido.setStatus(ParcelaStatus.ABE);
						}else {
							if (pedido.getValorRecebido().compareTo(new BigDecimal("0"))>0 && pedido.getValorParcela().compareTo(new BigDecimal("0"))>0) {
								pedido.setStatus(ParcelaStatus.PAR);
							}else {
								pedido.setStatus(ParcelaStatus.ABE);
							}
						}
					}else {
						pedido.setStatus(ParcelaStatus.ABE);
					}
					parcelasDao.save(pedido);
				}
				agTituloDao.delete(this.agTitulo);
				return toGerirTitulo();
			}else {
				throw new CaixaException(translate("caixa.notSelect.AgPedido"));
			}
		}catch (CaixaException c) {
			// TODO: handle exception
			this.addErrorNew(true, "messagesDialog","caixa.error", c.getMessage());
			return null;
		}
	}
	
	public void novaListaAgTitulos() { 
		this.listaAgTitulos = this.agTituloDao.listaAgTituloPorStatus(dataIni, dataFim, pegaIdEmpresa(), pegaIdFilial(),PedidoStatus.AgR);
	}
	
	public void onRowSelectAgTitulo(SelectEvent event)throws IOException,CaixaException{
		this.agTitulo = (AgTitulo)event.getObject();
	}
	
	public String telaReceberAgrupado() {
		try {
			if (this.agTitulo != null) {
				return toRecebeTitulo(this.agTitulo.getId());
			}else {
				throw new CaixaException(translate("financeiro.notSelect.AgTitulo"));
			}
		}catch (CaixaException c) {
			// TODO: handle exception
			this.addErrorNew(true, "messagesDialog","caixa.error", c.getMessage());
			return null;
		}
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
					if (this.configUser.getPorcentagemDesconto().compareTo(caixaUtil.retornaPorcentagemDesconto(this.agTitulo.getValorBruto(), this.desconto)) == -1) {
						throw new RegraNegocioException(translate("regraNegocio.discount.greater.than.allowed"));
					}
				}
				if (this.totalGT.compareTo(this.resto) == 0) {
					if (this.agTitulo.getValorBruto().compareTo(this.agTitulo.getValorTotal()) != 0) {
						this.agTitulo.setValorTotal(this.agTitulo.getValorBruto());
						this.agTitulo.setDesconto(new BigDecimal("0"));
						this.agTitulo.setAcrescimo(new BigDecimal("0"));
						
					}
					this.totalGT = this.agTitulo.getValorTotal();
//					this.totalGT = this.totalBruto;
					this.resto = this.agTitulo.getValorTotal();
//					this.resto = this.totalBruto;
					this.agTitulo.setDesconto(new BigDecimal("0"));
					this.agTitulo.setAcrescimo(new BigDecimal("0"));
					System.out.println("estou dentro da lista formaPagamentos = 0");
					if (this.desconto.compareTo(new BigDecimal("0"))==0 && this.acrescimo.compareTo(new BigDecimal("0"))>0) { // acrescimo
						valorAcrescimo = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.acrescimo, false, this.tipoAcrescimo);
						this.totalGT = this.totalGT.add(valorAcrescimo);
						this.resto = this.resto.add(valorAcrescimo);
						this.agTitulo.setAcrescimo(valorAcrescimo);
					}else {
						if (this.desconto.compareTo(new BigDecimal("0"))>0 && this.acrescimo.compareTo(new BigDecimal("0"))==0) { // desconto
							valorDesconto = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.desconto, true, this.tipoDesconto);
							this.totalGT = this.totalGT.subtract(valorDesconto);
							this.resto = this.resto.subtract(valorDesconto);
							this.agTitulo.setDesconto(valorDesconto);
						}else { // desconto e acrï¿½scimo. Sendo que primeiro se aplica o desconto para depois se aplicar o acrï¿½scimo
							valorDesconto = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.desconto, true, this.tipoDesconto);
							valorAcrescimo = caixaUtil.calculaDescontoAcrescimo(this.totalGT, this.acrescimo, false, this.tipoAcrescimo);
							this.totalGT = this.totalGT.subtract(valorDesconto);
							this.resto = this.resto.subtract(valorDesconto);
							this.totalGT = this.totalGT.add(valorAcrescimo);
							this.resto = this.resto.add(valorAcrescimo);
							this.agTitulo.setDesconto(valorDesconto);
							this.agTitulo.setAcrescimo(valorAcrescimo);
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
	
	@Transactional
	public void baixaTitulo() {
		try {
			if (this.listaParcelasSelecionadas.isEmpty()) {
				throw new FinanceiroException(this.translate("financeiro.list.titulo.empty"));
			}
			for (ParcelasNfe parc : this.listaParcelasSelecionadas) {
				
				if (parc.getStatus().equals(ParcelaStatus.PAR)) {
					throw new FinanceiroException(this.translate("financial.baixaTitulo.automatic.notPermission") + "   Título: " + parc.getDescricao());
				}
				parc.setStatus(ParcelaStatus.REC);
				parc.setDataRecebimento(parc.getVencimento());
				parc.setValorRecebido(parc.getValorParcela());
				parc.setValorCobrado(parc.getValorParcela());
				parc.setValorOriginal(parc.getValorParcela());
				parc.setValorParcela(new BigDecimal("0"));
				parcelasDao.save(parc);
			}
			initializeGerir();
			this.addInfo(true,"financeiro.baixaTitulo.sucess");
		}catch(FinanceiroException f) {
			this.addWarning(true, "financeiro.info.error", f.getLocalizedMessage());
		}catch (HibernateException c) {
			this.addWarning(true, "hibernate.persist.fail", c.getMessage());
		}catch (Exception e ) {
			this.addError(true, "exception.error.fatal", e.getMessage());
		}
	}
	
	@Transactional
	public void baixaTituloParcial() throws HibernateException, CaixaException, Exception {
		this.agTitulo.setStatus(PedidoStatus.REC);
		this.agTitulo.setDataRec(LocalDate.now());
		this.agTitulo.setHoraRec(LocalTime.now());
		this.agTitulo.setCaixa(this.caixa);
		this.agTitulo.setValorRecebido(this.totalGT.subtract(this.resto));
		boolean isDebito = false;
		List<ParcelasNfe> listaParcelasTemp = new ArrayList<>();
		listaParcelasTemp = listaTitulosPreenchido(preencheListasTitulosERateiaAcrescimoDesconto(this.agTitulo.getListaTitulosAgrupados(),this.agTitulo),this.agTitulo);
		for (ParcelasNfe titulo : listaParcelasTemp) {
			if (titulo.getTipoLancamento().equals(TipoLancamento.tpDebito)) {
				isDebito = true;
			}
			parcelasDao.save(titulo);
		}
		List<RecebimentoParcial> listParcialTemp = new ArrayList<RecebimentoParcial>();
		listParcialTemp = geraRecebimentoParcialeTitulo(); 
		if (listParcialTemp.size() > 0 ) {
			for (RecebimentoParcial recebimentoParcial : listParcialTemp) {
				recebimentoParcial.setContaCorrente(contaCorrente);
				recebimentoParcial.setAgTitulo(this.agTitulo);
				recebimentoParcial= this.recebimentoDao.save(recebimentoParcial);
			}
		}
		if (isDebito == false) { // somente atualiza o saldo do caixa caso se trate de Credito caso DEBITO valores nao devem ser acrescidos ao saldo do caixa!
			this.caixa = caixaUtil.preencheSaldoCaixa(this.caixa, MovimentoEnum.Rec, listParcialTemp);
			this.caixaDao.save(this.caixa); // atualiza o saldo do caixa
		}
		this.agTitulo.setListRecebimentoParcial(listParcialTemp);
		this.agTitulo = this.agTituloDao.save(this.agTitulo);
	}
	
//	@Transactional
//	public void recebeTitulo() throws HibernateException, Exception {
//		this.agTitulo.setStatus(PedidoStatus.REC);
//		this.agTitulo.setDataRec(LocalDate.now());
//		this.agTitulo.setHoraRec(LocalTime.now());
//		this.agTitulo.setCaixa(this.caixa);
//		this.agTitulo.setValorRecebido(totalGT);
//		List<ParcelasNfe> listaParcelasTemp = new ArrayList<>();
//		listaParcelasTemp = preencheListasTitulosERateiaAcrescimoDesconto(this.agTitulo.getListaTitulosAgrupados(),this.agTitulo);
//		for (ParcelasNfe titulo : listaParcelasTemp) {
//			titulo.setValorRecebido(titulo.getValorCobrado());
//			titulo.setValorOriginal(titulo.getValorParcela());
//			titulo.setValorParcela(titulo.saldoEmAberto());
//			titulo.setStatus(ParcelaStatus.REC);
//			titulo.setDataRecebimento(LocalDate.now());
////			titulo.getListaAgTitulo().add(this.agTitulo);
//			parcelasDao.save(titulo);
//		}
//		// gerando lista de RecebimentoParcial e persistindo no banco!
//		List<RecebimentoParcial> listParcialTemp = new ArrayList<RecebimentoParcial>();
//			listParcialTemp = geraRecebimentoParcialeTitulo(); 
//		if (listParcialTemp.size() > 0 ) {
//			for (RecebimentoParcial recebimentoParcial : listParcialTemp) {
//					recebimentoParcial.setAgTitulo(agTitulo);
//					recebimentoParcial= this.recebimentoDao.save(recebimentoParcial);
//				}
//		}
//		this.caixa = caixaUtil.preencheSaldoCaixa(this.caixa, MovimentoEnum.Rec, listParcialTemp);
//		this.caixaDao.save(this.caixa); // atualiza o saldo do caixa
//		this.agTitulo.setListRecebimentoParcial(listParcialTemp);
//		this.agTitulo = this.agTituloDao.save(this.agTitulo);
//	}
	
	@Transactional
	public List<ParcelasNfe> listaTitulosPreenchido(List<ParcelasNfe> lista,AgTitulo gt){
		BigDecimal valorParcial = new BigDecimal("0");
		valorParcial = gt.getValorRecebido();
		List<ParcelasNfe> listaTemp = new ArrayList<>();
		for (ParcelasNfe titulo : lista) {
			if (valorParcial.compareTo(new BigDecimal("0"))>0 ) {
				if (valorParcial.compareTo(titulo.getValorParcela()) == -1) {
					if (valorParcial.compareTo(new BigDecimal("0"))> 0) {
						if (titulo.getValorRecebido().compareTo(new BigDecimal("0"))==0) {
							titulo.setValorRecebido(valorParcial);
						}else {
							titulo.setValorRecebido(titulo.getValorRecebido().add(valorParcial));
						}
						if(titulo.getValorOriginal() == null || titulo.getValorOriginal().compareTo(new BigDecimal("0"))==0) {
							titulo.setValorOriginal(titulo.getValorParcela());
						}
						titulo.setValorParcela(titulo.getValorParcela().subtract(valorParcial));
						titulo.setStatus(ParcelaStatus.PAR);
						titulo.setDataRecebimento(LocalDate.now());
						valorParcial = new BigDecimal("0");
					}
				}else {
					if (valorParcial.compareTo(titulo.getValorParcela())>=0 ) {
						titulo.setValorRecebido(titulo.getValorRecebido().add(titulo.getValorParcela()));
						if(titulo.getValorOriginal() == null || titulo.getValorOriginal().compareTo(new BigDecimal("0"))==0) {
							titulo.setValorOriginal(titulo.getValorParcela());
						}
						if (valorParcial.compareTo(titulo.getValorParcela()) >= 0) {
							titulo.setValorParcela(new BigDecimal("0"));
						}else {
							titulo.setValorParcela(titulo.getValorParcela().subtract(valorParcial));
						}
						titulo.setStatus(ParcelaStatus.REC);
						titulo.setDataRecebimento(LocalDate.now());
					} 
					valorParcial = new BigDecimal(valorParcial.subtract(titulo.getValorCobrado()).toString());
				}
			}else {
				titulo.setStatus(ParcelaStatus.ABE);
			}
			listaTemp.add(titulo);
		}
		return listaTemp;
	}
				

	@Transactional
	public String gravaRecebimentoTitulo() throws HibernateException, Exception {
		try {
			if(this.agTitulo != null) {
					baixaTituloParcial();
			}
			return toGerirTitulo();
		}catch (CaixaException c) {
			this.addError(true,"caixa.error", c.getMessage());
			return null;
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (Exception e) {
			this.addError(true, "exception.error.fatal", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Método que rateia os descontos/acrescimo na lista de titulos;
	 * @param Lista de Pedidos
	 * @param AgPedido
	 * @throws CaixaException
	 * @return lista com os titulos recalculados
	 */
	@Transactional
	public List<ParcelasNfe> preencheListasTitulosERateiaAcrescimoDesconto(List<ParcelasNfe> lista,AgTitulo gt) throws CaixaException,HibernateException,Exception {
		MathContext mc = new MathContext(20,RoundingMode.HALF_EVEN);
		BigDecimal descontoTitulo = new BigDecimal("0");
		BigDecimal acrescimoTitulo  = new BigDecimal("0");
		BigDecimal porcentagemDesc = new BigDecimal("0");
		BigDecimal porcentagemAcresc = new BigDecimal("0");
		BigDecimal acrescimoFracaoAgTitulo =new BigDecimal("0");
		List<ParcelasNfe> listaTemp = new ArrayList<>();
		int quantPedidos = lista.size();
		boolean desc = false;
		boolean acres =false;
		if (gt == null) {
			throw new CaixaException(translate("caixa.AgPedido.isNull"));
		}
		if (gt.getDesconto().compareTo(new BigDecimal("0"))>0 ) {
			porcentagemDesc = (gt.getValorBruto().subtract(gt.getDesconto())).divide(gt.getValorBruto(),mc).setScale(2,RoundingMode.HALF_EVEN);			
			desc = true;
			System.out.println("Estou dentro do gtDesconto  desc = " + desc + "porcentagem = " + porcentagemDesc);
		}
		if (gt.getAcrescimo().compareTo(new BigDecimal("0"))>0 ) {
			porcentagemAcresc = (gt.getValorBruto().add(gt.getAcrescimo())).divide(gt.getValorBruto(),mc).setScale(2,RoundingMode.HALF_EVEN);		
			acres= true;
			System.out.println("Estou dentro do gTacrescimo acres = " + acres);
		}
		if (quantPedidos > 0 && quantPedidos < 21) {
			for(Iterator<ParcelasNfe> pedIterator = lista.iterator(); pedIterator.hasNext();) {
				ParcelasNfe ped = pedIterator.next();		
				if (ped.getValorOriginal() == null || ped.getValorOriginal().compareTo(new BigDecimal("0"))==0) {
					ped.setValorOriginal(ped.getValorParcela());
				}
				if (acres && desc == false ) {
					System.out.println("Estou dentro do acrescimo");
					BigDecimal totAcrescimo = new BigDecimal("0");
							acrescimoTitulo = (ped.getValorParcela().multiply(porcentagemAcresc,mc)).subtract(ped.getValorParcela()).setScale(2,RoundingMode.HALF_EVEN);
							ped.setAcrescimo(acrescimoTitulo);
							if (ped.getValorCobrado() == null || ped.getValorCobrado().compareTo(new BigDecimal("0"))==0) {
								ped.setValorCobrado(ped.getValorParcela().add(acrescimoTitulo));
							}else {
								ped.setValorCobrado(ped.getValorCobrado().add(acrescimoTitulo));
							}
							ped.setValorParcela(ped.getValorParcela().add(acrescimoTitulo));
							totAcrescimo = totAcrescimo.add(acrescimoTitulo);
				}
				if (desc && acres == false) {
					BigDecimal totDescItem = new BigDecimal("0");
						descontoTitulo = ped.getValorParcela().subtract((ped.getValorParcela().multiply(porcentagemDesc,mc)).setScale(2,RoundingMode.HALF_EVEN));
						ped.setDesconto(descontoTitulo);
						if (ped.getValorCobrado() == null || ped.getValorCobrado().compareTo(new BigDecimal("0"))==0) {
							ped.setValorCobrado(ped.getValorParcela().subtract(descontoTitulo));		
						}else {
							ped.setValorCobrado(ped.getValorCobrado().subtract(descontoTitulo));	
						}
						ped.setValorParcela(ped.getValorParcela().subtract(descontoTitulo));
						totDescItem = totDescItem.add(descontoTitulo);
				}
				if (desc && acres) {
					
					BigDecimal totAcrescimo = new BigDecimal("0");
					BigDecimal totDescItem = new BigDecimal("0");
						acrescimoTitulo = (ped.getValorParcela().multiply(porcentagemAcresc,mc)).subtract(ped.getValorParcela()).setScale(2,RoundingMode.HALF_EVEN);
						descontoTitulo = ped.getValorParcela().subtract((ped.getValorParcela().multiply(porcentagemDesc,mc)).setScale(2,RoundingMode.HALF_EVEN));
						
						ped.setAcrescimo(acrescimoTitulo);
						if (ped.getValorCobrado() == null || ped.getValorCobrado().compareTo(new BigDecimal("0"))==0) {
							ped.setValorCobrado(ped.getValorParcela().add(acrescimoTitulo).subtract(descontoTitulo));		
						}else {
							ped.setValorCobrado(ped.getValorCobrado().add(acrescimoTitulo).subtract(descontoTitulo));
						}
						ped.setDesconto(descontoTitulo);
						ped.setValorParcela(ped.getValorParcela().add(acrescimoTitulo).subtract(descontoTitulo));
						totDescItem = totDescItem.add(descontoTitulo);
						totAcrescimo = totAcrescimo.add(acrescimoTitulo);
				}
				if (desc == false && acres == false) {
						
						if (ped.getValorCobrado() == null || ped.getValorCobrado().compareTo(new BigDecimal("0"))==0) {
							ped.setValorCobrado(ped.getValorParcela());	
						}
						ped.setAcrescimo(new BigDecimal("0"));
						ped.setDesconto(new BigDecimal("0"));
				}
				listaTemp.add(ped);
			}
			// calculo da diferenï¿½a no desconto e acrescimo
			BigDecimal fracao = new BigDecimal("0");
			BigDecimal totalDesconto = new BigDecimal("0");
			BigDecimal totalAcrescimo = new BigDecimal("0");
			for (ParcelasNfe pedTemp : listaTemp) {
				if (pedTemp.getDesconto() != null ) {
					totalDesconto = totalDesconto.add(pedTemp.getDesconto());
				}
				if (pedTemp.getAcrescimo() != null) {
					totalAcrescimo = totalAcrescimo.add(pedTemp.getAcrescimo());
				}
			}
			fracao = totalDesconto.subtract(gt.getDesconto());
			acrescimoFracaoAgTitulo = totalAcrescimo.subtract(gt.getAcrescimo());
			if (acrescimoFracaoAgTitulo.compareTo(new BigDecimal("0"))>0) {
				listaTemp.get(0).setAcrescimo((listaTemp.get(0).getAcrescimo().add(acrescimoFracaoAgTitulo,mc)).setScale(2,RoundingMode.HALF_EVEN));
			}
			if (fracao.compareTo(new BigDecimal("0"))>0) {
				listaTemp.get(0).setDesconto((listaTemp.get(0).getDesconto().add(fracao,mc)).setScale(2,RoundingMode.HALF_EVEN));
			}
			return listaTemp;
		}else {
			if (quantPedidos > 20 ) {
				throw new CaixaException(translate("caixa.limit.pedidos"));
			}
			throw new CaixaException(translate("caixa.caixa.list.isEmpty"));
		}
	}
	@Transactional
	public List<RecebimentoParcial> geraRecebimentoParcialeTitulo()  {
		List<RecebimentoParcial> listaTempRecParcial = new ArrayList<RecebimentoParcial>();
		System.out.println("Dentro do geraRecebimentoParcialParcelas");
		for(Entry<TipoPagamento, BigDecimal> pagamento : getListaTipoPagamentoInserido()) {
			if (this.hashRecebimentoParcialCaixa.containsKey(pagamento.getKey())) {
				RecebimentoParcial recParcial  = new RecebimentoParcial();
				recParcial.setValorRecebido(pagamento.getValue());
				recParcial.setLivroCaixa(MovimentoEnum.Rec);
				recParcial.setTipoPagamento(pagamento.getKey());
				if (pagamento.getKey().equals(TipoPagamento.Din) && this.troco.compareTo(new BigDecimal("0"))>0) {
					recParcial.setTroco(this.troco);
				}
				recParcial.setCaixa(this.caixa);
				System.out.println("geraRecebimentoParcialTitulo antes do save");
				recParcial = recebimentoDao.save(recParcial);
				System.out.println("geraRecebimentoParcialTitulo depois do save  ID : " + recParcial.getId() );
				listaTempRecParcial.add(recParcial);
			}
		}
		
		return listaTempRecParcial;
	}
	public void geraPDF() {
		try {
			List<AgendaDTOPdf> data = new ArrayList<>();
			
			for (AgendaDTO agenda : this.listaParcelas) {
				AgendaDTOPdf agTmp = new AgendaDTOPdf();
				agTmp.setDataRecebimento(agenda.getDataRecebimento());
				agTmp.setVencimento(agenda.getVencimento());
				agTmp.setDescricao(agenda.getDescricao());
				agTmp.setDiasDeAtraso(agenda.getDiasDeAtraso());
				agTmp.setIdParc(agenda.getIdParc());
				if (agenda.getNumeroNfe() == null) {
					agTmp.setNumeroNfe(agenda.getNumeroNFeRec());	
				}else {
					agTmp.setNumeroNfe(agenda.getNumeroNfe());
				}
				agTmp.setNumParcela(agenda.getNumParcela());
				agTmp.setQParcelas(agenda.getQParcelas());
				agTmp.setStatus(agenda.getStatus());
				agTmp.setTipoLancamento(agenda.getTipoLancamento());
				agTmp.setTipoPagamento(agenda.getTipoPagamento());
				agTmp.setValorOriginal(agenda.exibeValorCobrado());
				agTmp.setValorParcela(agenda.getValorParcela());
				data.add(agTmp);
			}
			
			
			
			Map<String, Object> parametros =  new HashMap<String,Object>();
			if (data.size() > 0) {
				parametros =  geraParametros();
				parametros.put("totalCredito",this.totalCredito);
				parametros.put("totalCreditoRec",this.totalCreditoRec);
				parametros.put("totalDebito",this.totalDebito);
				parametros.put("totalDebitoPag",this.totalDebitoPag);
				parametros.put("totalCreditoLiq",this.totalCreditoLiq);
				parametros.put("totalDebitoLiq",this.totalDebitoLiq);
				parametros.put("empresa",razaoEmpresaLogada());
				JRBeanCollectionDataSource jrBean = new JRBeanCollectionDataSource(data,false);
					String path = "/WEB-INF/Relatorios/Financeiro/RelAgendaFinanceira.jrxml";
					relatorios.visualizaPDF(path, parametros, "RelAgendaFinanceira" ,jrBean);
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
	
	public Map<String, Object> geraParametros(){
		Map<String, Object> parametros =  new HashMap<String,Object>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String dataIni = this.dataIni.format(formatter);
		String dataFim = this.dataFim.format(formatter);
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
	// retorna a descrição do status da parcela.
	public String descricaoParcela(String parcStatus) {
		return ParcelaStatus.valueOf(parcStatus).toString();
	
	}
	
	public List<RecebimentoParcial> getListaRecParcialPorTitulos() {
		try {
			List<RecebimentoParcial> tempLista = new ArrayList<>();
			this.tempListaAgTitulo = new ArrayList<>();
			if (this.parcela.getId() != null) {
				if (this.parcela.getStatus().equals(ParcelaStatus.PAR) || this.parcela.getStatus().equals(ParcelaStatus.REC)) {
					tempListaAgTitulo = agTituloDao.listaIDAgTituloPorTitulo(this.parcela.getId());
					if (tempListaAgTitulo.size() > 0) {// se lista de recebimento estiver com conteudo
						for (AgTituloIDDTO agTituloIDDTO : tempListaAgTitulo) {
							AgTitulo ag = new AgTitulo();
							ag = agTituloDao.encontraAgPedidoPorIdComRecParcial(agTituloIDDTO.getId().longValue());
							for (RecebimentoParcial rec : ag.getListRecebimentoParcial()) {
								tempLista.add(rec);
							}
							this.agTitulo =  ag;
						}
					}else { // se listaAgTituto vazia pege o recebimento direto da parcela (campo recebimentoParcial)
						RecebimentoParcial rec = new RecebimentoParcial();
						if (this.parcela.getRecebimentoParcial() != null) {
							rec = recebimentoDao.recebimentoPorid(this.parcela.getRecebimentoParcial().getId(),pegaIdEmpresa(),pegaIdFilial());
							this.agrupado = agrupadoDao.encontraAgPedidoPorId(rec.getAgrupado().getId());
							tempLista.add(rec);
						}else {
							throw new FinanceiroException(this.translate("financeiroException.recebimnetoParcial.null"));
						}
					}
				}else {
					throw new FinanceiroException(this.translate("financeiroException.statusNotRec"));
				}
			}
		return tempLista;
		}catch (FinanceiroException f) {
			this.addError(true, "caixa.error", f.getMessage());
			return null;
		}
	}
	
	private void inicializaDetalhePagamneto(Long id) throws FinanceiroException {
		this.parcela = this.parcelasDao.pegaParcela(pegaIdEmpresa(),pegaIdFilial(),id);
		this.agTitulo = new AgTitulo();
		this.agrupado = new AgPedido();
		if (this.parcela != null) {
			this.listaRecParcialPorTitulos = getListaRecParcialPorTitulos();
			preencheValoresAgrupados();
			if (this.parcela.getStatus().equals(ParcelaStatus.REC)) {
				this.viewState = ViewState.DETAILING;
			}else {
				this.viewState = ViewState.EDITING;
			}
			if (this.parcela.getCheque() == null) {
				this.chequeInf = new ChequeInf();
			}else {
				this.chequeInf = this.parcela.getCheque();
			}
			if (this.parcela.getCartao() == null) {
				this.cartaoInf = new CartaoInf();
			}else {
				this.cartaoInf = this.parcela.getCartao();
			}
			viewDetailContions();
		}else {
			throw new FinanceiroException(this.translate("financeiro.titulo.isNull"));
		}
	}
	
	public void preencheValoresAgrupados() throws FinanceiroException {
		
		if (this.agrupado.getId() != null) {
			this.valorTotalAgrupado = this.agrupado.getValorTotal();
			this.valorRecebidoAgrupado = this.agrupado.getValorRecebido();
		}else {
			if (this.agTitulo != null){
				this.valorTotalAgrupado = this.agTitulo.getValorTotal();
				this.valorRecebidoAgrupado = this.agTitulo.getValorRecebido();
				this.obsAgrupado = this.agTitulo.getObs();
			}else {
				throw new FinanceiroException(this.translate("financeiroException.Agrupado.Null"));
			}
		}
	}
	
	public List<ParcelasNfe> getListaDeTitulosRecebidos() {
		try {
			this.listaDeTitulosRecebidos = new ArrayList<>();
			if (this.agTitulo.getId() != null){
				this.listaDeTitulosRecebidos = agTituloDao.encontraAgTituloPorId(this.agTitulo.getId()).getListaTitulosAgrupados();
			}else {
				//			if (this.agrupado.getId() != null){
				if (this.agrupado.getId() != null) {
					this.listaDeTitulosRecebidos = this.agrupadoDao.encontraAgPedidoComListaDeParcelas(this.agrupado.getId()).getListaParcelas();
				}else {
					throw new FinanceiroException(this.translate("financeiroException.agTituloAgrupado.null"));
				}
				//			}
			}
			return this.listaDeTitulosRecebidos;
		}catch(FinanceiroException f) {
			this.addError(true, "caixa.error", f.getMessage());
			return null;
		}
	}
	
	
	
	public void detalhePagamento(AgendaDTO parcela){
		try {
			if (parcela.getStatus().contentEquals(ParcelaStatus.PAR.getCod()) || parcela.getStatus().contentEquals(ParcelaStatus.REC.getCod())) {
				inicializaDetalhePagamneto(parcela.getIdParc().longValue());
				this.updateAndOpenDialog("listaDetalhesPagamentoDialog", "dialogListaDetalhesPagamento");
			}else {
				throw new FinanceiroException(this.translate("financeiroException.notReceived"));
			}
		}catch(FinanceiroException f) {
			this.addError(true, "caixa.error", f.getMessage());
		}
	}
	
}
