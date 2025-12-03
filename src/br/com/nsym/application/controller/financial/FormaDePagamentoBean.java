package br.com.nsym.application.controller.financial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.hibernate.HibernateError;
import org.hibernate.HibernateException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.financeiro.ContaCorrente;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.financeiro.OperadoraCartao;
import br.com.nsym.domain.model.entity.financeiro.TipoPagamento;
import br.com.nsym.domain.model.repository.financeiro.ContaCorrenteRepository;
import br.com.nsym.domain.model.repository.financeiro.FormaDePagementoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FormaDePagamentoBean extends AbstractBeanEmpDS<FormaDePagamento>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private boolean isDeleted = false;
	
	@Getter
	@Setter
	private FormaDePagamento formaPagamento = new FormaDePagamento();
	
	@Inject
	private FormaDePagementoRepository formaDao;
	
	@Getter
	private AbstractLazyModel<FormaDePagamento> formaModel;
	
	@Getter
	private List<ContaCorrente> listaContas	= new ArrayList<>();
	
	@Inject
	private ContaCorrenteRepository contaCorrenteDao;
	
	
	
	@Override
	public FormaDePagamento setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormaDePagamento setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	@Override
	public void initializeForm(Long id) {
		this.listaContas = contaCorrenteDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(),true,false);
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.formaPagamento = new FormaDePagamento();

		} else {
			this.viewState = ViewState.EDITING;
			this.formaPagamento = this.formaDao.findById(id, false);
		}
	}
	
	@PostConstruct
	public void init(){
		this.formaModel = getLazyForma();
	}
	
	public AbstractLazyModel<FormaDePagamento> getLazyForma(){
		this.formaModel = new AbstractLazyModel<FormaDePagamento>(){
		/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		@Override
		public List<FormaDePagamento> load(int first, int pageSize, String sortField, SortOrder sortOrder,
				Map<String, Object> filters) {
			System.out.println("Estou no Produto");
			final PageRequest pageRequest = new PageRequest();

			pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
			.withDirection(sortOrder.name());

			final Page<FormaDePagamento> page = formaDao.listByStatusFilial(isDeleted , null,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(),null, pageRequest);

			this.setRowCount(page.getTotalPagesInt());

			return page.getContent();
		}
	};
	return formaModel;
	}
	/**
	 * redireciona para a pagina com o ID do ncm a ser editado
	 * 
	 * @param ncmID
	 * 
	 * @return
	 */
	public String changeToEdit(Long id) {
		return "formCadFormaDePagamento.xhtml?faces-redirect=true&ID=" + id;
	}

	/**
	 * redireciona para Cadastramento de novo ncm / edição de ncm já cadastrado
	 * @return pagina de edição/inclusao de Forma de Pagamento
	 */
	public String newFormaDePagamento() {
		return "formCadFormaDePagamento.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de Forma de Pagamento
	 * @return a lista de Forma de Pagamento
	 */
	public String toListFormaDePagamento() {
		return "formListFormaDePagamento.xhtml?faces-redirect=true";
	}
	/**
	 * Método que controla o seleção do item na lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "A forma de pagamento " + ((FormaDePagamento)event.getObject()).getDescricao()+ " foi selecionado");
		this.formaPagamento = (FormaDePagamento) event.getObject();
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Método que persiste no banco de dados
	 */
	
	@Transactional
	public void doSalvar(){
		try{
			if (this.formaPagamento.getId() == null){
				System.out.println(getUsuarioAutenticado().getIdEmpresa()+ "filial :" + getUsuarioAutenticado().getIdFilial());
				if (!this.formaDao.isExist(this.formaPagamento.getCodigo(), getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial())){
					this.formaPagamento.setDeleted(false);
					this.formaPagamento = this.formaDao.save(this.formaPagamento);
					this.addInfo(true, "save.sucess", this.formaPagamento.getDescricao());
				}else{
					this.addError(true, "error.exist.forma", this.formaPagamento.getCodigo());
				}
			}else{
				this.formaPagamento = this.formaDao.save(this.formaPagamento);
				this.addInfo(true, "save.update", this.formaPagamento.getDescricao());
			}
		}catch (HibernateError h) {
			this.addError(true, "error.save" + "Hibernate :", h.getCause());
		}catch (Exception e) {
			// TODO: handle exception
			this.addError(true, "save.error", e.getCause());
		}
	}
	
	/**
	 * Método que define como deletado, removendo a listagem.
	 * obs: a forma de pagamento continua no banco de dados, apenas não será exibida para o usuário
	 */
	
	@Transactional
	public void doExcluir(){
		try{
			this.formaPagamento.setDeleted(true);
			this.formaDao.save(this.formaPagamento);
			this.addInfo(true, "delete.sucess", this.formaPagamento.getDescricao());
		}catch (HibernateException e){
			this.addError(true, "save.error", e.getClass(), e.getCause());
		}
	}
	
	/**
	 * Lista de Tipos de Pagamentos Aceitos
	 * @return lista de tipos de pagamento.
	 */
	
	public TipoPagamento[] getTiposDePagamento(){
		return TipoPagamento.values();
	}
	
	/**
	 * Lista as Operadoras de cartoes
	 * @return lista de tipos de pagamento.
	 */
	
	public OperadoraCartao[] getOperadoraCartao(){
		return OperadoraCartao.values();
	}

}
