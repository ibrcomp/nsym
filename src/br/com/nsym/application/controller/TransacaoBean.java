package br.com.nsym.application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.venda.TipoTransacao;
import br.com.nsym.domain.model.entity.venda.Transacao;
import br.com.nsym.domain.model.repository.financeiro.FormaDePagementoRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import br.com.nsym.domain.model.repository.venda.TransacaoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped

public class TransacaoBean extends AbstractBeanEmpDS<Transacao>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2637172610792404457L;
	
	@Getter
	@Setter
	private Transacao transacao = new Transacao();
	
	@Inject
	private TransacaoRepository transacaoDao;
	
	@Getter
	private AbstractLazyModel<Transacao> transacaoModel;
	
	@Getter
	@Setter
	private boolean deleted = false;
	
	@Getter
	@Setter
	private List<FormaDePagamento> listaFormasDePagamento = new ArrayList<FormaDePagamento>();
	
	@Getter
	private List<Tributos> listaTributosAtivos = new ArrayList<>();
	
	@Inject
	private FormaDePagementoRepository formaPagDao;
	
	@Inject
	private TributosRepository tributoDao;

	
	@PostConstruct
	public void init(){
		this.transacaoModel = getLazyTransacao();
	}
	
	@Override
	public Transacao setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transacao setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		this.transacao = new Transacao();
		
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
			this.listaFormasDePagamento = this.formaPagDao.listaFormaDePagamento(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
			this.listaTributosAtivos = this.tributoDao.listaTodosTributosAtivos(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.transacao = new Transacao();
		}else {
			this.viewState = ViewState.EDITING;
			this.transacao = this.transacaoDao.pegaTransacaoPorId(id);
		}
	}
	
	/**
	 * Gera a lista de tributos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Transacao> getLazyTransacao(){
		this.transacaoModel = new AbstractLazyModel<Transacao>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8240850644683518994L;

			@Override
			public List<Transacao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				 Page<Transacao> page = transacaoDao.listByStatusFilial(isDeleted() , null,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(),null, pageRequest);

				this.setRowCount(page.getTotalPagesInt());
				
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = transacaoDao.listByFilterFilial(false, null, pegaIdEmpresa(), pegaIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
	                    	System.out.println(e.getMessage());
	                    }
					}
				}

				return page.getContent();
			}
		};
		return transacaoModel;
	}
	
	/**
	 * redireciona para a pagina com o ID da NFE a ser editado
	 * 
	 * @param nfeID
	 * 
	 * @returna pagina de edição de NFE
	 */
	public String changeToEdit(Long idTransacao) {
		return "formCadTipoVenda.xhtml?faces-redirect=true&idTransacao=" + idTransacao;
	}

	/**
	 * redireciona para Cadastramento de nova NFE / edição de NFE já cadastrado
	 * @return pagina de inclusao de NFE
	 */
	public String newTransaction() {
		return "formCadTipoVenda.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListTransaction() {
		return "formListTipoVenda.xhtml?faces-redirect=true";
	}
	
	/**
	 * lista do autocompletar Tributos
	 */
	public List<Tributos> completaTributos(String query){
		List<Tributos> fontePesquisa = this.tributoDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		return fontePesquisa;
	}
	
	public void onRowSelect(SelectEvent event)throws IOException{
		this.transacao = (Transacao) event.getObject();
		this.viewState =  ViewState.EDITING;
	}
	
	@Transactional
	public String doSave() {
		try {
			this.transacaoDao.save(this.transacao);
		
			return "formListTipoVenda.xhtml?faces-redirect=true";
		}catch (Exception e ) {
			this.addError(true, e.getMessage());
			return null;
		}
	}
	
	public TipoTransacao[] getlistaTipoTransacao() {
		return TipoTransacao.values();
	}
}
