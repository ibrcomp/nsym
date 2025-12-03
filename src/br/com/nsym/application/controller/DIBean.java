package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
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
import br.com.nsym.domain.model.entity.fiscal.nfe.Adicao;
import br.com.nsym.domain.model.entity.fiscal.nfe.DI;
import br.com.nsym.domain.model.entity.fiscal.nfe.TipoIntermedio;
import br.com.nsym.domain.model.entity.fiscal.nfe.TipoViaTransporte;
import br.com.nsym.domain.model.repository.fiscal.AdicaoRepository;
import br.com.nsym.domain.model.repository.fiscal.DIRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DIBean extends AbstractBeanEmpDS<DI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private DI di = new DI();

	@Getter
	@Setter
	private boolean visivelPorIdProduto = false;

	@Inject
	private DIRepository diDao;

	@Getter
	@Setter
	private Adicao adicao = new Adicao();

	@Getter
	@Setter
	private Adicao adiTemp = new Adicao();

	@Getter
	@Setter
	private boolean deleted;

	//	@Getter
	//	@Setter
	//	private List<Adicao> listaAdicao = new ArrayList<>();

	@Setter
	@Getter
	private Iterator<Adicao> listaIteAdicaoTemp = new ArrayList<Adicao>().iterator();

	@Getter
	@Setter
	private List<Adicao> listaAdicao = new ArrayList<>(); 

	@Getter
	private AbstractLazyModel<DI> listaDIModel;

	@Inject
	private AdicaoRepository adicaoDao;

	@Getter
	@Setter
	private boolean tipoIntermedio = true;

	@Override
	public DI setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DI setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@PostConstruct
	public void init(){
		this.listaDIModel = getListaDI();
	}

	/**
	 * Gera a lista de tributos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<DI> getListaDI(){
		this.listaDIModel = new AbstractLazyModel<DI>() {

			/**
			 *
			 */
			private static final long serialVersionUID = -6506861437398527277L;

			@Override
			public List<DI> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				Page<DI> page = diDao.listByStatusFilial(isDeleted(), null, getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial(),null, pageRequest);

				this.setRowCount(page.getTotalPagesInt());
				if (filters != null){
					for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();){
						try{
							String filterProperty = it.next();
							Object filterValue = filters.get(filterProperty);
							page = diDao.listByFilterFilial(isDeleted() , null,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(), pageRequest, filterProperty, filterValue.toString().toUpperCase());
							this.setRowCount(page.getTotalPagesInt());
						} catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				return page.getContent();
			}

		};
		return listaDIModel;
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		if (id== null) {
			this.viewState = ViewState.ADDING;
			this.di  = new DI();
			this.adicao = new Adicao();
			this.listaAdicao = new ArrayList<>();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdProduto(true);
			this.di = this.diDao.pegaDI(id, getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial());
			this.listaAdicao = this.di.getListaAdicao();
		}
	}

	/**
	 * redireciona para a pagina com o ID do DI a ser editado
	 * 
	 * @param tributoID
	 * 
	 * @return
	 */
	public String changeToEdit(Long id) {
		return "formCadDI.xhtml?faces-redirect=true&diID=" + id;
	}

	/**
	 * redireciona para Cadastramento de novo DI
	 * @return pagina de inclusao de DI
	 */
	public String newDI() {
		return "formCadDI.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de DI
	 * @return a lista de DI
	 */
	public String toListDI() {
		return "formListDI.xhtml?faces-redirect=true";
	}
	/**
	 * gera um array de Tipo de via de transporte
	 * @return array TipoViaTransportes
	 */
	public TipoViaTransporte[] getTipoViaTransportes(){
		return TipoViaTransporte.values();
	}

	/**
	 * Gera um array de TipoIntermedio
	 * @return array de TipoIntermedio
	 */
	public TipoIntermedio[] getTPIntermedio(){
		return TipoIntermedio.values();
	}

	public void visivelTipoIntermedio(){
		if (this.di.getTpIntermedio().getCodigo() == 1){
			this.setTipoIntermedio(true);
		}else{
			this.setTipoIntermedio(false);
		}
	}

	@Transactional
	public String doSalvar(){
		try{
			System.out.println("inicio do salvar antes do di.save");
			if (listaAdicao.size() != 0){
				System.out.println("dentro da lista adicao");
				this.di.setListaAdicao(listaAdicao);
				int i = 1;
				for (Adicao adicao : this.di.getListaAdicao()) {
					adicao.setDi(this.di);
					adicao.setNSeqAdic(new BigDecimal(i).toString());
					i++;
				}
			}
			this.di = this.diDao.save(this.di);
			this.addInfo(true, "save.sucess",this.di.getNnDi());

		}catch (Exception e) {
			this.addError(true,"erro.save",e.getMessage());
		}
		return toListDI();
	}

	public void inserteAdicaoNaLista(){
		System.out.println("inserido na lista");
		this.listaAdicao.listIterator().add(this.adiTemp);
		this.adiTemp = new Adicao();
		for (Adicao adicao : listaAdicao) {
			System.out.println(adicao.getNAdicao() + " numero da adicao");
		}
	}

	public void removeAdicaoDaLista(Adicao ad){
		System.out.println("Estou com adicao numero: "+ad.getNAdicao());
		for (int i= 0; i < listaAdicao.size(); i++){
			Adicao adTemp = listaAdicao.get(i);
			System.out.println("a Adicao antes do if numero da adicao: " + adTemp.getNAdicao() + " foi encontrada");
			if(ad.equals(adTemp)){
				System.out.println("a Adicao - dentro do if - de numero da adicao: " + adTemp.getNAdicao() + " foi encontrada");
				this.listaAdicao.remove(i);
			}
		}
	}

	public void onRowSelect(SelectEvent event)throws IOException{
		this.di = (DI) event.getObject();
		System.out.println(this.di.getNnDi()+ " ndi select + id: " + this.di.getId());
		this.viewState = ViewState.EDITING;
		//		setVisivelPorIdTributos(true);
	}
}
