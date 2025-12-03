package br.com.nsym.application.controller.tools;

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
import br.com.nsym.domain.model.entity.tools.MensagensInternas;
import br.com.nsym.domain.model.repository.tools.MensagensInternaRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class MensagensInternasBean extends AbstractBeanEmpDS<MensagensInternas> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private MensagensInternas mensagem;
	
	@Inject
	private MensagensInternaRepository mensagemDao;
	
	@Getter
	private AbstractLazyModel<MensagensInternas> mensagemModel;
	
	@Getter
	@Setter
	private boolean isDeleted = false;
	
	@PostConstruct
	public void init(){
		mensagemModel = getLazyMensagem();
	}

	public AbstractLazyModel<MensagensInternas> getLazyMensagem(){
		this.mensagemModel = new AbstractLazyModel<MensagensInternas>() {
			@Override
			public List<MensagensInternas> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no empresaModel");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());
				Page<MensagensInternas> page = new Page<>(); 
				//				if (getIdEmpresaLogado() != null) {

				page = mensagemDao.listByStatus(isDeleted, null, getUsuarioAutenticado().getIdEmpresa(), pageRequest);
				//				} else {
				//				 page = cargoDao.listByStatus(isDeleted, null, null, pageRequest);	
				//				}

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return mensagemModel;
	}
	
	/**
	 * 
	 *  Informa a página se estamos em modo de adição de nova Mensagem ou editando uma Mensagem já existente
	 * 
	 * @param idMensagem
	 */
	public void initializeForm(Long idMensagem) {
		if ((idMensagem == null) && (idMensagem == null)) {
			this.viewState = ViewState.ADDING;
			this.mensagem = new MensagensInternas();
		} else {
			this.viewState = ViewState.EDITING;
			this.mensagem = this.mensagemDao.findById(idMensagem, false);
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
	
	/**
	 * 
	 * Direciona as páginas
	 * 
	 * @return
	 */

	public String newMensagem() {
		return "formCadMensagensSistema.xhtml?faces-redirect=true";
	}

	public String toListMensagem() {
		return "formListMensagensSistema.xhtml?faces-redirect=true";
	}
	
	/*
	 * redireciona para a pagina com o ID da Mensagem a ser editada
	 * 
	 * @param mensagemID
	 * 
	 * @return
	 */

	public String changeToEdit(Long mensagemID) {
		System.out.println();
		return "formCadMensagensSistema.xhtml?faces-redirect=true&cargoID=" + mensagemID;
	}
	
	/**
	 * 
	 * Ao clicar em um registro da tabela seleciona e exibe as opções disponíveis
	 * 
	 * @param event
	 * @throws IOException
	 */

	public void onRowSelect(SelectEvent event)throws IOException{
		this.mensagem = (MensagensInternas) event.getObject();
		this.addInfo(true, "Mensagem para {0} foi selecionada", this.mensagem.getTipoMensagem());
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Metodo que persiste no banco de dados
	 */
	@Transactional
	public void doSalvar(){
		try {
			if (this.mensagem.getId() == null){
				this.mensagem.setDeleted(false);
				this.mensagem = this.mensagemDao.save(this.mensagem);
				this.addInfo(true, "save.sucess", this.mensagem.getTipoMensagem());
			}else{
				this.mensagem = this.mensagemDao.save(this.mensagem);
				this.addInfo(true, "save.update");
			}
		} catch (Exception e) {
			this.addError(true, "save.error", e.getCause());
		}
	}
	 /**
	  * método que exclui do banco de dados
	  */
	public void doExcluir(){
		try {
			this.mensagemDao.delete(this.mensagem);
			this.addInfo(true, "delete.sucess", this.mensagem.getTipoMensagem());
		} catch (Exception e) {
			this.addError(true, "error.delete", e.getCause());
		}
	}
	
	@Override
	public MensagensInternas setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MensagensInternas setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
