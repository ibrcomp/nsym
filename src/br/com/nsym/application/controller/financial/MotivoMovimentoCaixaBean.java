package br.com.nsym.application.controller.financial;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.ibrcomp.exception.CaixaException;
import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.MovimentoCaixa;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.tools.MotivoMovimentoCaixa;
import br.com.nsym.domain.model.entity.financeiro.tools.CaixaUtil;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoMovimento;
import br.com.nsym.domain.model.entity.tools.CaixaFinalidade;
import br.com.nsym.domain.model.repository.financeiro.CaixaRepository;
import br.com.nsym.domain.model.repository.financeiro.FormaDePagementoRepository;
import br.com.nsym.domain.model.repository.financeiro.MotivoMovimentoCaixaRepository;
import br.com.nsym.domain.model.repository.financeiro.MovimentoCaixaRepository;
import br.com.nsym.domain.model.repository.financeiro.RecebimentoParcialRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class MotivoMovimentoCaixaBean  extends AbstractBeanEmpDS<MotivoMovimentoCaixa> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3957140676740845374L;
	
	@Getter
	@Setter
	private MotivoMovimentoCaixa motivo;

	@Getter
	@Setter
	private MovimentoCaixa movimento;
	
	@Inject
	private MovimentoCaixaRepository movimentoDao;
	
	@Getter
	@Setter
	private BigDecimal valor = new BigDecimal("0");
	
	@Getter
	@Setter
	private FormaDePagamento pagamento;
	
	@Inject
	private FormaDePagementoRepository pagamentoDao;
	
	@Getter
	@Setter
	private List<FormaDePagamento> listaPagamentos = new ArrayList<FormaDePagamento>();
	
	@Getter
	private AbstractLazyModel<MotivoMovimentoCaixa> motivoModel;
	
	@Getter
	private AbstractLazyModel<MovimentoCaixa> movimentoModel;
	
	@Inject
	private MotivoMovimentoCaixaRepository motivoDao;
	
	@Getter
	@Setter
	private List<MotivoMovimentoCaixa> listaDeMotivos;
	
	@Inject
	private CaixaRepository caixaDao;
	
	@Getter
	@Setter
	private Caixa caixa;
	
	@Inject
	private CaixaUtil caixaUtil;
	
	@Getter
	@Setter
	private RecebimentoParcial recebimento;
	
	@Inject
	private RecebimentoParcialRepository recebimentoDao;
	
	@Getter
	@Setter
	private LocalDate dataInicial = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now();

	@Override
	public MotivoMovimentoCaixa setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MotivoMovimentoCaixa setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		this.motivoModel = getLazyMotivoMovimento();
		
	}
	
	public void initializeListingMovimetno() {
		// TODO Auto-generated method stub
		this.movimentoModel = getLazyExtratoMovimento();
		
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.motivo = new MotivoMovimentoCaixa();
		}else {
			this.viewState = ViewState.EDITING;
			this.motivo = motivoDao.findById(id, false);
		}
		
	}
	
	public void initializeFormLanc(Long id) {
		// TODO Auto-generated method stub
		try {
			this.caixa = caixaUtil.retornaCaixa(CaixaFinalidade.rece);
			this.caixa.setListaRecebimentoCaixa(this.recebimentoDao.listaRecebimentosPorCaixa(this.caixa, pegaIdEmpresa(), pegaIdFilial()));
			this.viewState = ViewState.LISTING;
			if (this.caixa != null) {
				this.listaDeMotivos = this.motivoDao.listaCriteriaPorFilial(pegaIdEmpresa(), null, true,false);
				this.listaPagamentos = this.pagamentoDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(),false ,false);
				if (id == null) {
					this.viewState = ViewState.ADDING;
					this.movimento = new MovimentoCaixa();
					this.recebimento = new RecebimentoParcial();
					this.valor = new BigDecimal("0");
				}else {
					this.viewState = ViewState.EDITING;
					this.movimento =  movimentoDao.findById(id, false);
					this.recebimento = this.recebimentoDao.findById(this.movimento.getRecebimento().getId(), false);
					this.valor = this.movimento.getValor();
				}
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
	
	
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<MotivoMovimentoCaixa> getLazyMotivoMovimento(){
		this.motivoModel = new AbstractLazyModel<MotivoMovimentoCaixa>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3097078596870201247L;

			@Override
			public List<MotivoMovimentoCaixa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<MotivoMovimentoCaixa> page = motivoDao.listaDeMotivosParaMovimentoFiananceiro(false,pegaIdEmpresa(),pegaIdFilial(), pageRequest, null, null);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = motivoDao.listaDeMotivosParaMovimentoFiananceiro(false,pegaIdEmpresa(),pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return motivoModel;
	}
	
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<MovimentoCaixa> getLazyExtratoMovimento(){
		this.movimentoModel = new AbstractLazyModel<MovimentoCaixa>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3097078596870201247L;

			@Override
			public List<MovimentoCaixa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<MovimentoCaixa> page = movimentoDao.extratoMovimentoCaixaPorData(dataInicial,dataFinal,pegaIdEmpresa(),pegaIdFilial(), pageRequest, null, null);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = movimentoDao.extratoMovimentoCaixaPorData(dataInicial,dataFinal,pegaIdEmpresa(),pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return movimentoModel;
	}
	
	public void onRowSelect(SelectEvent event) throws IOException {
		this.motivo = (MotivoMovimentoCaixa) event.getObject();
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Redireciona para o form de lista de motivos
	 * @return a pagina de lista de motivos
	 */
	public String toListMotivo() {
		return "formListMotivoMovimento.xhtml?faces-redirect=true";
	}
	
	/**
	 * Redireciona para o extrato de movimentos
	 * @return a pagina com o extrato de movimentos
	 */
	public String toExtratoMovimento() {
		return "formExtratoMovimento.xhtml?faces-redirect=true";
	}
	
	/**
	 * redireciona para a pagina com o ID do Motivo a ser editado
	 * 
	 * @param motivoID
	 * 
	 * @returna pagina de edição do motivo
	 */
	public String changeToEdit(Long motivoID) {
		return "formCadMotivoMovimento.xhtml?faces-redirect=true&motivoID=" + motivoID;
	}
	
	/**
	 * redireciona para a pagina com o ID do lançamento do movimento a ser editado
	 * 
	 * @param lancaMoviID
	 * 
	 * @returna pagina de edição do lançamento de movimento
	 */
	public String changeToEditLanca(Long lancaMoviID) {
		return "formLancaMovimento.xhtml?faces-redirect=true&lancaMoviID=" + lancaMoviID;
	}

	/**
	 * redireciona para Cadastramento de novo Motivo
	 * @return pagina de inclusao de Motivos
	 */
	public String newMotivo() {
		return "formCadMotivoMovimento.xhtml?faces-redirect=true";
	}
	
	/**
	 * redireciona para Cadastramento de novo Lançamento de Movimento
	 * @return pagina de inclusao de um novo Lançamento de movimento
	 */
	public String newLancaMovimento() {
		return "formLancaMovimento.xhtml?faces-redirect=true";
	}
	
	public TipoMovimento[] pegaTiposMovimento() {
		return TipoMovimento.values();
	}
	 // método que persiste o motivo no banco de dados
	@Transactional
	public String doSalvar() {
		try {
			
			this.motivo.setDeleted(false);
			motivoDao.save(this.motivo);
			return toListMotivo();
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail",this.translate(h.getMessage()));
			return null;
		}catch (NullPointerException n) {
			this.addError(true, "campo nulo",n.getMessage());
			return null;
		}catch (Exception e) {
			// TODO: handle exception			
			this.addError(true, "exception.error.fatal", this.translate(e.getMessage()));
			return null;
		}
	}
	
	@Transactional
	public String doSalvarLancamento(){
		try {
			if (this.movimento.getId() == null) {
				this.movimento.setCaixa(this.caixa);
			}
			this.movimento.setDeleted(false);
			this.movimento.setValor(this.valor.setScale(3,RoundingMode.HALF_EVEN));
			this.recebimento = caixaUtil.movimentacaoEntradaSaidaDeValor(this.movimento, this.valor, this.caixa);
			List<RecebimentoParcial> listaRecTemp = new ArrayList<RecebimentoParcial>();
			listaRecTemp.add(this.recebimento);
			this.caixa.getListaRecebimentoCaixa().add(this.recebimento);			
			this.caixa = caixaUtil.preencheSaldoCaixa(this.caixa, this.recebimento.getLivroCaixa(),listaRecTemp);
			System.out.println("preenchi o saldo e retornei o caixa " );
			this.caixaDao.save(this.caixa);
			System.out.println("salvei o caixa! Iniciando salvar Recebimento");
			return toExtratoMovimento();
		}catch (HibernateException h) {
			this.addError(true, "hibernate.persist.fail", h.getMessage());
			return null;
		}catch (CaixaException c) {
			this.addError(true, "caixa.error",c.getMessage());
			return null;
		}catch (NullPointerException n) {			
			this.addError(true, "campo nulo",n.getMessage());
			return null;
		}catch (Exception e) {
			// TODO: handle exception			
			this.addError(true, "exception.error.fatal",e.getMessage());
			return null;
		}
	}
}
