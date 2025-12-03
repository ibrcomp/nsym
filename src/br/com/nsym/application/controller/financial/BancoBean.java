package br.com.nsym.application.controller.financial;

import java.io.IOException;
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

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.financeiro.Banco;
import br.com.nsym.domain.model.repository.financeiro.BancoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class BancoBean  extends AbstractBeanEmpDS<Banco>{

	/**
	 *
	 */
	private static final long serialVersionUID = -6020234715639779532L;
	
	@Getter
	@Setter
	private Banco banco;
	
	@Inject
	private BancoRepository bancoDao; 
	
	@Getter
	private AbstractLazyModel<Banco> bancoModel  ;
	
	@Getter
	@Setter
	private boolean Deleted = false;
	
	
	@Override
	public Banco setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Banco setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.bancoModel = getBancoLazyModel();
	}
	
	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.banco = new Banco();
		}else {
			this.viewState = ViewState.EDITING;
			this.banco = this.bancoDao.findById(id, false);
		}
	}
	
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Banco> getBancoLazyModel(){
		this.bancoModel = new AbstractLazyModel<Banco>() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2972770799144484522L;


			@Override
			public List<Banco> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no lista Banco");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "nomeBanco")
				.withDirection(sortOrder.name());

				Page<Banco> page = bancoDao.listaLazyComFiltro(isDeleted(),null,pegaIdEmpresa(), null, pageRequest,null, null,false);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = bancoDao.listaLazyComFiltro(isDeleted(),null,pegaIdEmpresa(), null, pageRequest, filterProperty, filterValue.toString().toUpperCase(),false);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return this.bancoModel;
	}
	
	public void onRowSelect(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.banco = (Banco) event.getObject();
	}

	
	public String newBanco() {
		return "formCadBanco.xhtml?faces-redirect=true";
	}
	
	public String changeToEdit(Long id) {
		return "formCadBanco.xhtml?faces-redirect=true&bancoID=" + id;
	}
	
	public String toListBanco() {
		return "formListBanco.xhtml?faces-redirect=true";
	}
	
	@Transactional
	public void doSalvar() {
		try {
			this.banco = this.bancoDao.save(this.banco);
			this.addInfo(true,"save.sucess",this.banco.getNomeBanco());
		}catch (HibernateException e) {
			this.addError(true,"error.save",e.getMessage());
		}catch (Exception f) {
			// TODO: handle exception
			this.addError(true,"exception.error.fatal",f.getMessage());
		}
	}
}
