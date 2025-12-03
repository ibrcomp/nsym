package br.com.nsym.application.controller.tributos;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.TabFcpEstado;
import br.com.nsym.domain.model.entity.fiscal.TabIVAEstado;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.repository.fiscal.NCMRepository;
import br.com.nsym.domain.model.repository.fiscal.TabFcpEstadoRepository;
import br.com.nsym.domain.model.repository.fiscal.TabIVAEstadoRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class NcmBean extends AbstractBeanEmpDS<Ncm>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Ncm ncm = new Ncm();

	@Inject
	private NCMRepository ncmDao;

	@Getter
	@Setter
	private Tributos tributo;

	@Inject
	private TributosRepository tributoDao;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private TabIVAEstado ivaEstado = new TabIVAEstado();

	@Getter
	@Setter
	private TabFcpEstado fcpEstado = new TabFcpEstado();

	@Getter
	@Setter
	private Uf Uf ;

	@Getter
	@Setter
	private Uf ufFCP;

	@Inject
	private TabIVAEstadoRepository ivaEstadoDao;

	@Inject
	private TabFcpEstadoRepository fcpEstadoDao;

	@Getter
	private List<TabIVAEstado> listaIVAEstadoNcm = new ArrayList<>();

	@Getter
	private List<TabFcpEstado> listaFcpEstadoNcm = new ArrayList<>();

	@Getter
	@Setter
	private boolean visivelPorIdTributos = false;

	@Getter
	private AbstractLazyModel<Ncm> ncmModel;

	@Getter
	@Setter
	private BigDecimal valorIVa = new BigDecimal("0");

	@Getter
	@Setter
	private String valorFCP;

	@Getter
	private List<Tributos> listaTributosAtivos = new ArrayList<>();

	@PostConstruct
	public void init(){
		//		this.ListaIVAEstadoNcm = this.setListaIVAEstadoNcm();
	}
	/**
	 * Gera a lista de Ncm em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Ncm> getLazyNcm(){
		this.ncmModel = new AbstractLazyModel<Ncm>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Ncm> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<Ncm> page = ncmDao.listaLazyDeNCMPorFilial(isDeleted , pegaIdEmpresa(), null, pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return ncmModel;
	}
	/**
	 * Inicialização da pagina em modo de Adição ou Edição
	 * @param idTributos
	 */
	public void initializeForm(Long id) {
		this.listaTributosAtivos = this.tributoDao.listaTodosTributosAtivos(getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		pegaIdFilial();
		if (id == null) {
			this.viewState = ViewState.ADDING;
			this.ncm = new Ncm();
			this.ivaEstado = new TabIVAEstado();
			this.fcpEstado = new TabFcpEstado();
			this.valorIVa = new BigDecimal("0");

		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdTributos(true);
			this.ivaEstado = new TabIVAEstado();
			this.fcpEstado = new TabFcpEstado();
			this.valorIVa=new BigDecimal("0");
			this.ncm = this.ncmDao.pegaNcm(id, getUsuarioAutenticado().getIdEmpresa());
			this.listaIVAEstadoNcm = this.ivaEstadoDao.listaIvaPorNcm(this.ncm, getUsuarioAutenticado().getIdEmpresa());
			this.listaFcpEstadoNcm = this.fcpEstadoDao.listaFcpPorNcm(this.ncm, getUsuarioAutenticado().getIdEmpresa());
		}
	}

	/**
	 * Inicialização da pagina em modo listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
		this.ncmModel = getLazyNcm();
	}

	/**
	 * redireciona para a pagina com o ID do ncm a ser editado
	 * 
	 * @param ncmID
	 * 
	 * @return
	 */
	public String changeToEdit(Long ncmID) {
		return "formCadNcm.xhtml?faces-redirect=true&ncmID=" + ncmID;
	}

	/**
	 * redireciona para Cadastramento de novo ncm / edição de ncm já cadastrado
	 * @return pagina de edição/inclusao de ncm
	 */
	public String newNcm() {
		return "formCadNcm.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de Ncm
	 * @return a lista de Ncm
	 */
	public String toListNcm() {
		return "formListNcm.xhtml?faces-redirect=true";
	}

	/**
	 * Evento que controla o item da lista selecionado enviando o id do ncm pela url 
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "O NCM " + ((Ncm)event.getObject()).getDescricao()+ " foi selecionado");
		this.ncm = (Ncm) event.getObject();
		setVisivelPorIdTributos(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		changeToEdit(this.ncm.getId());
	}

	/**
	 * Evento que controla o item da lista IVA/Estado
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectIVAEstado(SelectEvent event)throws IOException{
		this.addInfo(true, "O Estado" + ((TabIVAEstado)event.getObject()).getUf().name()+ " foi selecionado");
		this.ivaEstado = (TabIVAEstado) event.getObject();
		this.valorIVa = this.ivaEstado.getPIVA();
		this.Uf = this.ivaEstado.getUf();

	}

	/**
	 * Evento que controla o item da lista FCP/Estado
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectFcpEstado(SelectEvent event)throws IOException{
		this.addInfo(true, "O Estado" + ((TabFcpEstado)event.getObject()).getUf().name()+ " foi selecionado");
		this.fcpEstado = (TabFcpEstado) event.getObject();
		this.valorFCP = this.fcpEstado.getPFcp().toString();
		this.Uf = this.fcpEstado.getUf();

	}

	@Transactional
	public void doSalvar(){
		try {
			if (this.ncm.getId() == null){
				if (!this.ncmDao.jaExiste(this.ncm.getNcm(),this.getUsuarioAutenticado().getIdEmpresa())){
					this.ncm.setDeleted(false);
					if (this.ncm.getTributo() == null){
						this.ncm.setTributo(tributoDao.findById((long) 1, false));
					}
					this.ncm = this.ncmDao.save(this.ncm);
					this.addInfo(true, "save.sucess", this.ncm.getNcm());
				}else{
					this.addError(true, "error.exist", this.ncm.getNcm());
				}
			}else{
				if (this.ncm.getTributo() == null){
					this.ncm.setTributo(tributoDao.findById((long) 1, false));
				}
				this.ncm = this.ncmDao.save(this.ncm);
				this.addInfo(true, "save.update", this.ncm.getNcm());
			}
		} catch (Exception e) {
			this.addError(true, "save.error", this.ncm.getNcm());
		}
	}

	@Transactional
	public void doExcluir(){
		try {
			this.ncm.setDeleted(true);
			this.ncm = this.ncmDao.save(this.ncm);
			this.addInfo(true, "delete.sucess", this.ncm.getDescricao());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * lista do autocompletar Tributos
	 */
	public List<Tributos> completaTributos(String query){
		List<Tributos> fontePesquisa = this.tributoDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		return fontePesquisa;
	}

	/**
	 * gera lista de estados
	 */
	public Uf[] estadoType(){
		return br.com.nsym.domain.model.entity.tools.Uf.values();
	}


	/**
	 * insere iva com o estado no NCM
	 */
	@Transactional
	public void salvaIVAEstado(){
		try{
			if (this.ncm.getId() == null){
				this.addError(true, "error.save.before","NCM");

			}else{
				System.out.println(this.Uf.toString());
				System.out.println(this.valorIVa);
				this.ivaEstado.setUf(this.Uf);
				this.ivaEstado.setPIVA(this.valorIVa);
				System.out.println("IVAESTADO UF: " + this.ivaEstado.getUf());
				System.out.println("IVAEstado pIVA: " + this.ivaEstado.getPIVA());
				this.ivaEstado.setNcm(this.ncm);
				this.ivaEstado = this.ivaEstadoDao.save(this.ivaEstado);
				this.addInfo(true, "save.sucess", this.ivaEstado.getUf());
				this.listaIVAEstadoNcm = this.ivaEstadoDao.listaIvaPorNcm(this.ncm, getUsuarioAutenticado().getIdEmpresa());
				limpaIVA();
			}
		}catch (Exception e){
			this.addError(true, "error.save.iva", this.ivaEstado.getUf());
		}
	}
	@Transactional
	public void excluiIVAUf(TabIVAEstado itemSelect){
		this.ivaEstadoDao.delete(itemSelect);
		this.listaIVAEstadoNcm.remove(itemSelect);
	}
	/**
	 * insere FCP com o estado no NCM
	 */

	@Transactional
	public void salvaFcpEstado(){
		try{
			if (this.ncm.getId() == null){
				this.addError(true, "error.save.before","NCM");

			}else{
				System.out.println(this.ufFCP.toString());
				System.out.println(this.valorFCP);
				this.fcpEstado.setUf(this.ufFCP);
				this.fcpEstado.setPFcp(new BigDecimal(this.valorFCP));
				System.out.println("fcpEstado UF: " + this.fcpEstado.getUf());
				System.out.println("fcpEstado pFCP: " + this.fcpEstado.getPFcp());
				this.fcpEstado.setNcm(this.ncm);
				this.fcpEstado = this.fcpEstadoDao.save(this.fcpEstado);
				this.addInfo(true, "save.sucess", this.fcpEstado.getUf());
				this.listaFcpEstadoNcm = this.fcpEstadoDao.listaFcpPorNcm(this.ncm, getUsuarioAutenticado().getIdEmpresa());
				limpaIVA();
			}
		}catch (Exception e){
			this.addError(true, "error.save.fcp", this.ivaEstado.getUf());
		}
	}
	/**
	 * Altera o IVA do Estado pra o NCM
	 */
	public void alteraIVAEstado(){
		if (this.ivaEstado != null){
			if (this.ivaEstado.getId() != null){
				this.ivaEstado.setPIVA(this.valorIVa);
				this.ivaEstado.setUf(this.getUf());
				this.ivaEstado.setNcm(this.ncm);
				this.ivaEstado = this.ivaEstadoDao.save(this.ivaEstado);
				limpaIVA();
			}
		}
	}
	/**
	 * Exclui o IVA do Estado para o NCM
	 */
	public void excluiIVAEstado(){
		if (this.ivaEstado.getId() != null){
			this.ivaEstadoDao.delete(this.ivaEstado);
		}
	}
	/**
	 * limpa TabIVAEstado para permitir um novo estado
	 */
	public void limpaIVA(){
		this.ivaEstado = new TabIVAEstado();
		this.valorIVa=new BigDecimal("0");
		this.Uf = null;
		this.fcpEstado = new TabFcpEstado();
		this.valorFCP = "";
		this.ufFCP = null;
	}

	/**
	 * gera lista de IVA por Estado
	 */
	public void setListaIVAEstadoNcm(){
		this.listaIVAEstadoNcm = this.ivaEstadoDao.listaIvaPorNcm(this.ncm, getUsuarioAutenticado().getIdEmpresa());
	}


	@Override
	public Ncm setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ncm setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}
}
