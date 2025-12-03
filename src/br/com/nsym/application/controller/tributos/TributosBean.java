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
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.fiscal.CFOP;
import br.com.nsym.domain.model.entity.fiscal.COFINS;
import br.com.nsym.domain.model.entity.fiscal.COFINSST;
import br.com.nsym.domain.model.entity.fiscal.ICMS;
import br.com.nsym.domain.model.entity.fiscal.ICMSST;
import br.com.nsym.domain.model.entity.fiscal.IPI;
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.PIS;
import br.com.nsym.domain.model.entity.fiscal.PISST;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTCOFINS;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTIPI;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTNormal;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTPIS;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTSimples;
import br.com.nsym.domain.model.entity.fiscal.tools.ModalidadeICMS;
import br.com.nsym.domain.model.entity.fiscal.tools.ModalidadeICMSST;
import br.com.nsym.domain.model.entity.fiscal.tools.Origem;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoCalculo;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoMovimento;
import br.com.nsym.domain.model.entity.tools.Enquadramento;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.fiscal.CFOPRepository;
import br.com.nsym.domain.model.repository.fiscal.COFINSRepository;
import br.com.nsym.domain.model.repository.fiscal.COFINSSTRepository;
import br.com.nsym.domain.model.repository.fiscal.ICMSRepository;
import br.com.nsym.domain.model.repository.fiscal.ICMSSTRepository;
import br.com.nsym.domain.model.repository.fiscal.IPIRepository;
import br.com.nsym.domain.model.repository.fiscal.NCMRepository;
import br.com.nsym.domain.model.repository.fiscal.PISRepository;
import br.com.nsym.domain.model.repository.fiscal.PISSTRepository;
import br.com.nsym.domain.model.repository.fiscal.TributosRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class TributosBean extends AbstractBeanEmpDS<Tributos>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Tributos tributos = new Tributos();	

	@Getter
	@Setter
	private Empresa empresa = new Empresa();

	@Getter
	@Setter
	private Filial filial = new Filial();

	@Inject
	private EmpresaRepository empDao;

	@Inject
	private FilialRepository filialDao;

	@Inject
	private TributosRepository tributosDao;

	@Inject
	private ICMSRepository icmsDao;

	@Getter
	@Setter
	private ICMS icms = new ICMS();

	@Inject
	private ICMSSTRepository icmsStDao;

	@Getter
	@Setter
	private ICMSST icmsSt = new ICMSST();

	@Inject
	private IPIRepository ipiDao;

	@Getter
	@Setter
	private Uf estado;

	@Getter
	@Setter
	private IPI ipi = new IPI();

	@Inject
	private PISRepository pisDao;

	@Getter
	@Setter
	private PIS pis = new PIS();

	@Inject
	private PISSTRepository pisStDao;

	@Getter
	@Setter
	private PISST pisSt = new PISST();

	@Inject
	private COFINSRepository cofinsDao;

	@Getter
	@Setter
	private COFINS cofins = new COFINS();

	@Inject
	private COFINSSTRepository cofinsStDao;

	@Getter
	@Setter
	private COFINSST cofinsSt = new COFINSST();

	@Getter
	@Setter
	private Ncm ncm;

	@Inject
	private NCMRepository ncmDao;

	@Getter
	@Setter
	private CFOP cfopDentro;

	@Getter
	@Setter
	private CFOP cfopFora;

	@Getter
	@Setter
	private CFOP cfopExterior;
	
	@Getter
	@Setter
	private CFOP cfopConsumidor;
	
	@Getter
	@Setter
	private CFOP cfopProdConsumidor;
	
	@Getter
	@Setter
	private CFOP cfopProdExterior;
	
	@Getter
	@Setter
	private CFOP cfopProdDentro;

	@Getter
	@Setter
	private CFOP cfopProdFora;

	@Inject
	private CFOPRepository cfopDao;

	@Getter
	private AbstractLazyModel<Tributos> tributosModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Setter
	private Enquadramento regime ;

	@Getter
	@Setter
	private boolean visivelPorIdTributos = false;

	@PostConstruct
	public void init(){
		this.tributosModel = getLazyTributos();
	}
	/**
	 * Gera a lista de tributos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<Tributos> getLazyTributos(){
		this.tributosModel = new AbstractLazyModel<Tributos>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Tributos> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<Tributos> page = tributosDao.listByStatusFilial(isDeleted , null,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(),null, pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return tributosModel;
	}

	/**
	 * Inicialização da pagina em modo de Adição ou Edição
	 * @param idTributos
	 */
	public void initializeForm(Long idTributos) {
		
		if (idTributos == null) {
			this.viewState = ViewState.ADDING;
			this.tributos  = new Tributos();
			this.ncm = new Ncm();
			this.cfopDentro = new CFOP();
			this.cfopProdDentro = new CFOP();
			this.cfopFora = new CFOP();
			this.cfopProdFora = new CFOP();
			this.cfopExterior = new CFOP();
			this.cfopProdExterior = new CFOP();
			this.cfopConsumidor = new CFOP();
			this.cfopProdConsumidor = new CFOP();
			this.icms =  new ICMS();
			this.icmsSt = new ICMSST();
			this.ipi = new IPI();
			this.pis = new PIS();
			this.pisSt = new PISST();
			this.cofins = new COFINS();
			this.cofinsSt = new COFINSST();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdTributos(true);
			this.tributos = this.tributosDao.findById(idTributos, false);
			if (this.tributos.getIpi() != null){
				this.ipi = this.ipiDao.findById(this.tributos.getIpi().getId(), false);
			}
			//			if (this.tributos.getNcm() != null){
			//				this.ncm = this.ncmDao.findById(this.tributos.getNcm().getId(), false);
			//			}
			if (this.tributos.getCfopDentro() != null){
				this.cfopDentro = this.cfopDao.findById(this.tributos.getCfopDentro().getId(), false);
			}
			if (this.tributos.getCfopDentroFabricado() != null){
				this.cfopProdDentro = this.cfopDao.findById(this.tributos.getCfopDentroFabricado().getId(), false);
			}
			if (this.tributos.getCfopFora() != null){
				this.cfopFora = this.cfopDao.findById(this.tributos.getCfopFora().getId(), false);
			}
			if (this.tributos.getCfopForaFabricado() != null){
				this.cfopProdFora = this.cfopDao.findById(this.tributos.getCfopForaFabricado().getId(), false);
			}
			if (this.tributos.getCfopExterior() != null){
				this.cfopExterior = this.cfopDao.findById(this.tributos.getCfopExterior().getId(), false);
			}
			if (this.tributos.getCfopExteriorFabricado() != null){
				this.cfopProdExterior = this.cfopDao.findById(this.tributos.getCfopExteriorFabricado().getId(), false);
			}
			if (this.tributos.getCfopConsumidor() != null){
				this.cfopConsumidor = this.cfopDao.findById(this.tributos.getCfopConsumidor().getId(), false);
			}
			if (this.tributos.getCfopConsumidorFabricado() != null){
				this.cfopProdConsumidor = this.cfopDao.findById(this.tributos.getCfopConsumidorFabricado().getId(), false);
			}
			
			if (this.tributos.getEstado() != null){
				this.setEstado(this.tributos.getEstado());
			}
			if (this.tributos.isSt()){
				if (this.tributos.getIcmsSt() != null){
					this.icmsSt = this.icmsStDao.findById(this.tributos.getIcmsSt().getId(), false);
				}
				if (this.tributos.getPisSt()!= null){
					this.pisSt = this.pisStDao.findById(this.tributos.getPisSt().getId(), false);
				}
				if(this.tributos.getCofinsSt() != null){
					this.cofinsSt = this.cofinsStDao.findById(this.tributos.getCofinsSt().getId(), false);
				}
			}else{
				this.icms = this.icmsDao.findById(this.tributos.getIcms().getId(), false);
				this.pis = this.pisDao.findById(this.tributos.getPis().getId(), false);
				this.cofins = this.cofinsDao.findById(this.tributos.getCofins().getId(), false);
			}
		}
	}

	/**
	 * Inicialização da pagina em modo listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	/**
	 * redireciona para a pagina com o ID do tributo a ser editado
	 * 
	 * @param tributoID
	 * 
	 * @return
	 */
	public String changeToEdit(Long tributoID) {
		return "formCadTributos.xhtml?faces-redirect=true&idTributo=" + tributoID;
	}

	/**
	 * redireciona para Cadastramento de novo tributo / edição de tributo já cadastrado
	 * @return pagina de edição/inclusao de tributo
	 */
	public String newTributos() {
		return "formCadTributos.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListTributos() {
		return "formListTributos.xhtml?faces-redirect=true";
	}

	/**
	 * chama dialog CFOP
	 */
	public void telaCfop() {
		this.updateAndOpenDialog("cfopDialog", "dialogCFOP");
	}

	/**
	 * chama dialog NCM
	 */
	public void telaNcm() {
		this.openDialog("dialogNCM");
	}

	/**
	 * Evento que controla o item da lista selecionado enviando o id do tributo pela url 
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "O Tributo para o NCM " + ((Tributos)event.getObject()).getDescricao()+ " foi selecionado");
		this.tributos = (Tributos) event.getObject();
		setVisivelPorIdTributos(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		changeToEdit(tributos.getId());

	}

	/**
	 *  Lista do autocompletar NCM  
	 */
	public List<Ncm> completaNcm(String query) { // Testar!!!!!!!

		List<Ncm> fontePesquisa = this.ncmDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(),null);

		return fontePesquisa;
	}

	public List<Ncm> listaNcm(){
		return this.ncmDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(), null);
	}

	/**
	 * Lista do autocompletar CFOP
	 */
	public List<CFOP> completaCfop(String query){
		List<CFOP> fontePesquisa = this.cfopDao.pesquisaTexto(query, getUsuarioAutenticado().getIdEmpresa(),null);
		return fontePesquisa;
	}

	public List<CFOP> listaCfop(){
		return this.cfopDao.listCFOPAtivoEmpresa(getUsuarioAutenticado().getIdEmpresa());
	}


	public Uf[] getUf(){
		return Uf.values();
	}

	public ModalidadeICMS[] getModIcms(){
		return ModalidadeICMS.values();
	}

	public ModalidadeICMSST[] getModIcmsSt(){
		return ModalidadeICMSST.values();
	}

	public CSTNormal[] getCSTNormal(){
		return CSTNormal.values();
	}

	public CSTSimples[] getCSTSimples(){
		return CSTSimples.values();
	}

	public CSTPIS[] getCSTpis(){
		return CSTPIS.values();
	}

	public CSTIPI[] getCSTIPI(){
		return CSTIPI.values();
	}

	public CSTCOFINS[] getCSTCOFINS(){
		return CSTCOFINS.values();
	}

	public Object[] getCST(){
		Empresa emp =new Empresa();
		Filial filial = new Filial();
		System.out.println(getUsuarioAutenticado().getIdEmpresa() );
		if (getUsuarioAutenticado().getIdEmpresa() != null && getUsuarioAutenticado().getIdFilial() != null  ){
			filial = this.filialDao.findById(getUsuarioAutenticado().getIdFilial(), false);
			if (filial.getEnquadramento().equals(Enquadramento.SimplesNacional) || filial.getEnquadramento().equals(Enquadramento.SimplesNacionalMei)|| filial.getEnquadramento().equals(Enquadramento.SimplesNacionalExcecao)){
				return getCSTSimples();
			}else{
				return getCSTNormal();
			}
		}else{
			emp =  this.empDao.findById(getUsuarioAutenticado().getIdEmpresa(), false);
			if (emp.getEnquadramento().equals(Enquadramento.SimplesNacional)|| emp.getEnquadramento().equals(Enquadramento.SimplesNacionalMei)|| emp.getEnquadramento().equals(Enquadramento.SimplesNacionalExcecao)){
				return getCSTSimples();
			}else{
				return getCSTNormal();
			}
		}
	}

	public Origem[] getOrigem(){
		return Origem.values();
	}
	
	public TipoMovimento[] getTipoNota(){
		return TipoMovimento.values();
	}

	@Transactional
	public void doSalvar(){
//		try {
			System.out.println("estou no método salvar");
			if (this.tributos.getId() == null){ // caso Tributo novo
				if (this.tributos.isSt()){
					this.icmsSt = this.icmsStDao.save(this.icmsSt);
					this.tributos.setIcmsSt(this.icmsSt);
					this.ipi = this.ipiDao.save(this.ipi);
					this.tributos.setIpi(this.ipi);
					this.pisSt = this.pisStDao.save(this.pisSt);
					this.tributos.setPisSt(this.pisSt);
					this.cofinsSt = this.cofinsStDao.save(this.cofinsSt);
					this.tributos.setCofinsSt(this.cofinsSt);
				}else{
					this.icms = this.icmsDao.save(this.icms);
					System.out.println("Passei pelo save ICMS");
					this.tributos.setIcms(this.icms);
					this.ipi = this.ipiDao.save(this.ipi);
					System.out.println("Passei pelo save ipi");
					this.tributos.setIpi(this.ipi);
					this.pis = this.pisDao.save(this.pis);
					System.out.println("Passei pelo save pis");
					this.tributos.setPis(this.pis);
					this.cofins = this.cofinsDao.save(this.cofins);
					System.out.println("Passei pelo save cofins");
					this.tributos.setCofins(this.cofins);
				}
				//				this.tributos.setNcm(this.ncm);
				this.tributos.setCfopDentro(this.cfopDentro);
				this.tributos.setCfopDentroFabricado(this.cfopProdDentro);
				this.tributos.setCfopFora(this.cfopFora);
				this.tributos.setCfopForaFabricado(this.cfopProdFora);
				this.tributos.setCfopExterior(this.cfopExterior);
				this.tributos.setCfopExteriorFabricado(this.cfopProdExterior);
				this.tributos.setCfopConsumidor(this.cfopConsumidor);
				this.tributos.setCfopConsumidorFabricado(this.cfopProdConsumidor);
				this.tributos = this.tributosDao.save(this.tributos);
				this.addInfo(true, "save.sucess", tributos.getDescricao());
			}else{ // caso alterando tributo
				if (this.tributos.isSt()){
					this.icmsSt = this.icmsStDao.save(this.icmsSt);
					this.tributos.setIcmsSt(this.icmsSt);
					this.ipi = this.ipiDao.save(this.ipi);
					this.tributos.setIpi(this.ipi);
					this.pisSt = this.pisStDao.save(this.pisSt);
					this.tributos.setPisSt(this.pisSt);
					this.cofinsSt = this.cofinsStDao.save(this.cofinsSt);
					this.tributos.setCofinsSt(this.cofinsSt);
				}else{
					this.icms = this.icmsDao.save(this.icms);
					this.tributos.setIcms(this.icms);
					this.ipi = this.ipiDao.save(this.ipi);
					this.tributos.setIpi(this.ipi);
					this.pis = this.pisDao.save(this.pis);
					this.tributos.setPis(this.pis);
					this.cofins = this.cofinsDao.save(this.cofins);
					this.tributos.setCofins(this.cofins);
				}
				//				this.tributos.setNcm(this.ncm);
				this.tributos.setCfopDentro(this.cfopDentro);
				this.tributos.setCfopDentroFabricado(this.cfopProdDentro);
				this.tributos.setCfopFora(this.cfopFora);
				this.tributos.setCfopForaFabricado(this.cfopProdFora);
				this.tributos.setCfopExterior(this.cfopExterior);
				this.tributos.setCfopExteriorFabricado(this.cfopProdExterior);
				this.tributos.setCfopConsumidor(this.cfopConsumidor);
				this.tributos.setCfopConsumidorFabricado(this.cfopProdConsumidor);
				this.tributos = this.tributosDao.save(this.tributos);
				this.addInfo(true, "save.update", tributos.getDescricao());
			}
//		} catch (Exception e) {
//			this.addError(true, "error.save" + e.getCause(), tributos.getDescricao());
//		}

	}

	@Transactional
	public void doExcluir(){
		try{
			this.tributos.setDeleted(true);
			this.tributosDao.save(this.tributos);
			toListTributos();
			this.addInfo(true, "delete.sucess", this.tributos.getDescricao());
		}catch (Exception e){
			this.addError(true, "error.delete", this.tributos.getDescricao());
		}
	}

	public Enquadramento getRegimeUsuario(){
		if (this.getUsuarioAutenticado().getIdFilial() != null){
			this.filial = this.filialDao.findById(this.getUsuarioAutenticado().getIdFilial(), false);
			this.setRegime(this.filial.getEnquadramento());
		}else if (this.getUsuarioAutenticado().getIdEmpresa() != null){ 
			this.empresa = this.empDao.findById(this.getUsuarioAutenticado().getIdEmpresa(), false);
			this.setRegime(this.empresa.getEnquadramento());
		}
		return this.regime;
	}

	public TipoCalculo[] getTipoCalculos(){
		return TipoCalculo.values();
	}
	
	@Override
	public Tributos setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tributos setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
