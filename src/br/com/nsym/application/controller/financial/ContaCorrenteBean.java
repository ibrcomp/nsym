package br.com.nsym.application.controller.financial;

import java.io.IOException;
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

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.financeiro.Banco;
import br.com.nsym.domain.model.entity.financeiro.ContaCorrente;
import br.com.nsym.domain.model.entity.financeiro.InfBoleto;
import br.com.nsym.domain.model.entity.financeiro.Enum.CarteiraEnvio;
import br.com.nsym.domain.model.entity.financeiro.Enum.CodigoMora;
import br.com.nsym.domain.model.entity.financeiro.Enum.CodigoNegativacao;
import br.com.nsym.domain.model.entity.financeiro.Enum.Protesto;
import br.com.nsym.domain.model.entity.financeiro.Enum.Sacado;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoDesconto;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoImpressao;
import br.com.nsym.domain.model.entity.financeiro.tools.WsBanco;
import br.com.nsym.domain.model.repository.financeiro.BancoRepository;
import br.com.nsym.domain.model.repository.financeiro.ContaCorrenteRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ContaCorrenteBean  extends AbstractBeanEmpDS<ContaCorrente>{
	
	
	/**
	 *
	 */
	private static final long serialVersionUID = -6160517228212176973L;

	@Getter
	@Setter
	private ContaCorrente conta = new ContaCorrente();
	
	@Getter
	@Setter
	private InfBoleto infBoleto;
	
	@Getter
	@Setter
	private Banco banco;
	
	@Getter
	private List<Banco> listaBancos = new ArrayList<>();
	
	@Inject
	private BancoRepository bancoDao;
	
	@Getter
	@Setter
	private WsBanco wsBanco = new WsBanco();
	
	@Inject
	private ContaCorrenteRepository contaDao;
	
	@Getter
	private AbstractLazyModel<ContaCorrente> contaModel;
	
	@Getter
	@Setter
	private boolean Deleted = false;

	@Override
	public ContaCorrente setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContaCorrente setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.contaModel = getContaLazyModel();
		
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		this.listaBancos = this.bancoDao.listaCriteriaPorFilial(pegaIdEmpresa(),null,true,false);
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.conta = new ContaCorrente();
			this.banco = new Banco();
			this.wsBanco = new WsBanco();
			this.infBoleto = new InfBoleto();
		}else {
			this.viewState = ViewState.EDITING;
			this.conta = this.contaDao.pegaConta(id, pegaIdEmpresa(), pegaIdFilial());
			if (this.conta != null) {
				this.banco = this.bancoDao.pegaBanco(this.conta.getBanco().getId(), pegaIdEmpresa(),pegaIdFilial());
				this.wsBanco = this.conta.getWsBanco();
				this.infBoleto = this.conta.getInfBoleto();
			}
		}
		
	}
	
	/**
	 * Gera a lista de produtos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<ContaCorrente> getContaLazyModel(){
		this.contaModel = new AbstractLazyModel<ContaCorrente>() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2972770799144484522L;


			@Override
			public List<ContaCorrente> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no lista Conta Corrente");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "nomeBanco")
				.withDirection(sortOrder.name());

				Page<ContaCorrente> page = contaDao.listaLazyComFiltro(isDeleted(),null,pegaIdEmpresa(), null, pageRequest,null, null,false);

				this.setRowCount(page.getTotalPagesInt());

				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = contaDao.listaLazyComFiltro(isDeleted(),null,pegaIdEmpresa(), null, pageRequest, filterProperty, filterValue.toString().toUpperCase(),false);
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				return page.getContent();
			}
		};
		return this.contaModel;
	}
	
	public void onRowSelect(SelectEvent event)throws IOException{
		this.viewState = ViewState.EDITING;
		this.conta = (ContaCorrente)event.getObject();
		System.out.println("id Conta: " + this.conta.getId());
	}

	
	public String newContaCorrente() {
		return "formCadContaCorrente.xhtml?faces-redirect=true";
	}
	
	public String changeToEdit(Long id) {
		return "formCadContaCorrente.xhtml?faces-redirect=true&contaID=" + id;
	}
	
	public String toListContaCorrente() {
		return "formListContaCorrente.xhtml?faces-redirect=true";
	}
	
	@Transactional
	public String doSalvar() {
		try {
			this.infBoleto.setConta(this.conta);
			this.conta.setInfBoleto(this.infBoleto);
			this.conta.setBanco(this.banco);
			this.wsBanco.setConta(this.conta);
			this.conta.setWsBanco(this.wsBanco);
			this.conta = this.contaDao.save(this.conta);
			this.viewState = ViewState.EDITING;
			this.addInfo(true,"save.sucess",this.conta.getContaCorrente());
			return toListContaCorrente();
		}catch (HibernateException e) {
			this.addError(true,"error.save",e.getMessage());
			return null;
		}catch (Exception f) {
			// TODO: handle exception
			this.addError(true,"exception.error.fatal",f.getMessage());
			return null;
		}
	}
	
	public Sacado[] listaSacado() {
		return Sacado.values();
	}
	public Protesto[] listaProtesto() {
		return Protesto.values();
	}
	public TipoImpressao[] listaImpressao() {
		return TipoImpressao.values();
	}
	public TipoDesconto[] listaDesconto() {
		return TipoDesconto.values();
	}
	public CodigoMora[] listaCodigoMora() {
		return CodigoMora.values();
	}
	public CarteiraEnvio[] listaCarteiraEnvio() {
		return CarteiraEnvio.values();
	}
	public CodigoNegativacao[] listaCodigoNegativacao() {
		return CodigoNegativacao.values();
	}
}
