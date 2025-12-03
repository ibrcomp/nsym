package br.com.nsym.application.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Fabricante;
import br.com.nsym.domain.model.repository.cadastro.FabricanteRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FabricanteBean extends AbstractBeanEmpDS<Fabricante> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Fabricante fabricante = new Fabricante();

	@Inject
	private FabricanteRepository fabricanteDao;

	@Getter
	private AbstractLazyModel<Fabricante> fabricanteModel;
	

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdFabricante = false;


	@PostConstruct
	public void init(){
		fabricanteModel = getLazyFabricante();
	}

	public AbstractLazyModel<Fabricante> getLazyFabricante(){
		this.fabricanteModel = new AbstractLazyModel<Fabricante>() {

			@Override
			public List<Fabricante> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<Fabricante> page = fabricanteDao.listByStatus(isDeleted , null,pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return fabricanteModel;
	}

	/**
	 *  Inicializa o Form de visualiza��o para modo de adicionar ou editar e faz o carregamento dos dados
	 * @param idFabricante
	 */
	public void initializeForm(Long idFabricante) {
		if (idFabricante == null) {
			this.viewState = ViewState.ADDING;
			this.fabricante  = new Fabricante();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdFabricante(true);
			this.fabricante = this.fabricanteDao.findById(idFabricante, false);
		}
	}
	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public String newFabricante() {
		return "formCadFabricante.xhtml?faces-redirect=true";
	}

	public String toListFabricante() {
		return "formListFabricante.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do Fabricante a ser editado
	 * @param FabricanteID
	 * @return a p�gina
	 */
	public String changeToEdit(Long fabricanteID) {
		return "formCadFabricante.xhtml?faces-redirect=true&fabricanteID=" + fabricanteID;
	}

	/**
	 * Metodo que salva o Fabricante
	 */
	@Transactional
	public void doSalvar(){
		try{
			if (this.viewState == ViewState.ADDING){
				System.out.println("entou dentro do adding");
				if (!fabricanteDao.jaExiste(this.fabricante.getMarca(),getUsuarioAutenticado().getIdEmpresa())){
					System.out.println("estou dentro do n�o existe Fabricante");
					this.fabricante.setDeleted(false);
					this.fabricante = this.fabricanteDao.save(this.fabricante);
					this.fabricante = new Fabricante();
					this.addInfo(true, "save.sucess");
				}else{
					this.addError(true, "Fabricante.exist");
				}
			}else{
				this.fabricante = this.fabricanteDao.save(this.fabricante);
				this.fabricante = new Fabricante();
				this.addInfo(true, "save.sucess");
			}
		}catch (Exception e) {
			this.addError(true, "save.error");
		}
	}

	/**
	 * Metodo que exclui o Fabricante 
	 * @return redireciona para a lista
	 */ 
	@Transactional
	public String doExcluir(){
		try{
			this.fabricante.setDeleted(true);
			this.fabricante = fabricanteDao.save(this.fabricante);
			this.addInfo(true, "delete.sucess");
			return toListFabricante();
		}catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}
	
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "Fabricante "+((Fabricante) event.getObject()).getMarca()+" selecionada");  
		this.fabricante = (Fabricante) event.getObject();
		setVisivelPorIdFabricante(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Metodo que possibilita exportar para excel a lista de Fabricante
	 */
	public List<Fabricante> pegaListaFabricante(){
		return fabricanteDao.listFabricanteAtivo();
	}

	public void criaExcel(){
		postProcessXLS(pegaListaFabricante());
	}

	/**
	 * Criar um documento em excel personalizado.
	 *  @param document
	 */
	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColorPredefined.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
		}
	}

	@Override
	public Fabricante setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fabricante setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
