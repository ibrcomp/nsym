package br.com.nsym.application.controller.tributos;

import java.io.IOException;
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
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.fiscal.CFOP;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoMovimento;
import br.com.nsym.domain.model.repository.fiscal.CFOPRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class CfopBean extends AbstractBeanEmpDS<CFOP> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private CFOP cfop = new CFOP();

	@Inject
	private CFOPRepository cfopDao;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdTributos = false;

	@Getter
	private AbstractLazyModel<CFOP> cfopModel;

	@PostConstruct
	public void init(){
		this.cfopModel = getLazCfop();
	}
	/**
	 * Gera a lista de Cfops em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<CFOP> getLazCfop(){
		this.cfopModel = new AbstractLazyModel<CFOP>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<CFOP> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<CFOP> page = cfopDao.listByStatus(isDeleted , null,pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return cfopModel;
	}

	/**
	 * Inicialização da pagina em modo de Adição ou Edição
	 * @param idTributos
	 */
	public void initializeForm(Long idCfop) {
		if (idCfop == null) {
			this.viewState = ViewState.ADDING;
			this.cfop = new CFOP();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdTributos(true);
			this.cfop = this.cfopDao.findById(idCfop, false);
		}
	}

	/**
	 * Inicialização da pagina em modo listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	/**
	 * redireciona para a pagina com o ID do cfop a ser editado
	 * 
	 * @param tributoID
	 * 
	 * @return
	 */
	public String changeToEdit(Long cfopID) {
		return "formCadCfop.xhtml?faces-redirect=true&cfopID=" + cfopID;
	}

	/**
	 * redireciona para Cadastramento de novo cfop / edição de cfop já cadastrado
	 * @return pagina de edição/inclusao de cfop
	 */
	public String newCfop() {
		return "formCadCfop.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de Cfop
	 * @return a lista de Cfop
	 */
	public String toListCfop() {
		return "formListCfop.xhtml?faces-redirect=true";
	}

	/**
	 * Evento que controla o item da lista selecionado enviando o id do cfop pela url 
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "O CFOP " + ((CFOP)event.getObject()).getDescricao()+ " foi selecionado");
		this.cfop = (CFOP) event.getObject();
		setVisivelPorIdTributos(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		changeToEdit(this.cfop.getId());
	}
	
	public TipoMovimento[] getTipoNota(){
		return TipoMovimento.values();
	}

	@Transactional
	public void doSalvar(){
		try {
			if (this.cfop.getId() == null){
				if (!this.cfopDao.jaExiste(this.cfop.getCfop(),getUsuarioAutenticado().getIdEmpresa())){
					this.cfop.setDeleted(false);
					this.cfop = this.cfopDao.save(this.cfop);
					this.cfop = new CFOP();
					this.addInfo(true, "save.sucess", this.cfop.getCfop());
				}else{
					this.addError(true, "error.exist", this.cfop.getCfop());
				}
			}else{
				this.cfop = this.cfopDao.save(this.cfop);
				this.cfop = new CFOP();
				this.addInfo(true, "save.update", this.cfop.getCfop());
			}
		} catch (Exception e) {
			this.addError(true, "error.save", this.cfop.getCfop());
		}
	}


	public void doExcluir(){
		try {
			this.cfop.setDeleted(true);
			this.cfop = this.cfopDao.save(this.cfop);
			this.addInfo(true, "error.delete", this.cfop.getDescricao());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}



	@Override
	public CFOP setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CFOP setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
