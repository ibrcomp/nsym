package br.com.nsym.application.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Cargo;
import br.com.nsym.domain.model.repository.cadastro.CargoRepository;
import lombok.Getter;
import lombok.Setter;


/**
 * 	Bean que gerencia os Cargos 
 * 
 * Regra: Este cadastro devera ser restrito ao grupo da mesma EMPRESA 
 * 
 * @author Ibrahim
 *
 */

@Named
@ViewScoped
public class CargoBean extends AbstractBeanEmpDS<Cargo>{


	@Getter
	@Setter
	private Cargo cargo = new Cargo();

	@Inject
	private CargoRepository cargoDao;

	@Getter
	private AbstractLazyModel<Cargo> cargoModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private Long idEmpLogadao ;




	@PostConstruct
	public void init(){
		cargoModel = getLazyCargo();
	}

	public AbstractLazyModel<Cargo> getLazyCargo(){
		this.cargoModel = new AbstractLazyModel<Cargo>() {
			@Override
			public List<Cargo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no empresaModel");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());
				Page<Cargo> page = new Page<>(); 
				//				if (getIdEmpresaLogado() != null) {

				page = cargoDao.listByStatus(isDeleted, null, getUsuarioAutenticado().getIdEmpresa(), pageRequest);
				//				} else {
				//				 page = cargoDao.listByStatus(isDeleted, null, null, pageRequest);	
				//				}

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return cargoModel;
	}

	public Long getIdEmpresaLogado(){
		try{
			idEmpLogadao = getUsuarioAutenticado().getIdEmpresa();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return idEmpLogadao;
	}

	@Override
	public Cargo setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cargo setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 *  Informa a página se estamos em modo de adição de novo Cargo ou editando um cargo já existente
	 * 
	 * @param idCargo
	 */
	public void initializeForm(Long idCargo) {
		if ((idCargo == null) && (idCargo == null)) {
			this.viewState = ViewState.ADDING;
			this.cargo = new Cargo();
		} else {
			this.viewState = ViewState.EDITING;
			this.cargo = this.cargoDao.findById(idCargo, false);
		}
	}

	/**
	 * 
	 * Informa a página que estamos no modo Lista
	 * 
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public Long pegaIdEmpresa() {
		final Long id = getUsuarioAutenticado().getIdEmpresa();
		if (id == null) {
			FacesMessage msg = new FacesMessage("O Usuário " + getUsuarioAutenticado().getName()
					+ " não pertence a uma Empresa, vincule uma empresa primeiro!");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		return id;
	}

	/**
	 * 
	 * Direciona as páginas
	 * 
	 * @return
	 */

	public String newCargo() {
		return "formCadCargo.xhtml?faces-redirect=true";
	}

	public String toListCargo() {
		return "formListCargo.xhtml?faces-redirect=true";
	}

	/*
	 * redireciona para a pagina com o ID da empresa a ser editada
	 * 
	 * @param empresaID
	 * 
	 * @return
	 */

	public String changeToEdit(Long cargoID) {
		System.out.println();
		return "formCadCargo.xhtml?faces-redirect=true&cargoID=" + cargoID;
	}

	/**
	 * 
	 * Ao clicar em um registro da tabela seleciona e exibe as opções disponíveis
	 * 
	 * @param event
	 * @throws IOException
	 */

	public void onRowSelect(SelectEvent event)throws IOException{
		FacesMessage msg = new FacesMessage("Foi selecionado o cargo: "+((Cargo) event.getObject()).getCargo());  
		FacesContext.getCurrentInstance().addMessage(null, msg);
		this.cargo = (Cargo) event.getObject();
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		//	insereEmpresaNaSessao();
	}


	@Transactional
	public void doSalvar() {
		try {
			if (this.cargo.getId() == null){ // laço NEW
				if (getIdEmpresaLogado()!= null){
					this.cargo.setIdEmpresa(getIdEmpresaLogado());
				}
				this.cargo.setDeleted(false);
				this.cargo = this.cargoDao.save(cargo);
				String resposta = this.cargo.getCargo();
				this.addInfo(true, "save.sucess",resposta);
			}else  { // laço atualiza (Update)

//				this.cargoDao.update(this.cargo, usuarioAutenticado.getName(), new Date());
				this.cargoDao.save(cargo);
				this.addInfo(true, "save.update");

			}

		} catch (Exception e) {
			this.addError(true, "save.error", e.getCause());
		}
	}

	@Transactional
	public String doExcluir() {
		try {
			this.cargo.setDeleted(true);
			this.cargoDao.save(this.cargo);
			return toListCargo();
		} catch (IllegalArgumentException e) {
			System.out.println("Não foi possivel excluir o registro " + e);
			return null;
		}
	}
	
	public List<Cargo> getListaCargoPorFilial(){
		return this.cargoDao.listaPorFilial(pegaIdEmpresa(), pegaIdFilial());
	}
}
