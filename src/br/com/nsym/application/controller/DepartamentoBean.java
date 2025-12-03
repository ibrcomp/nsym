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
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Departamento;
import br.com.nsym.domain.model.repository.cadastro.DepartamentoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DepartamentoBean extends AbstractBeanEmpDS<Departamento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Departamento departamento = new Departamento();

	@Inject
	private DepartamentoRepository departamentoDao;

	@Getter
	private AbstractLazyModel<Departamento> departamentoModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdDepartamento = false;


	@PostConstruct
	public void init(){
		departamentoModel = getLazyDepartamento();
	}

	public AbstractLazyModel<Departamento> getLazyDepartamento(){
		this.departamentoModel = new AbstractLazyModel<Departamento>() {

			@Override
			public List<Departamento> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<Departamento> page = departamentoDao.listByStatus(isDeleted , null,pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return departamentoModel;
	}

	/**
	 *  Inicializa o Form de visualiza��o para modo de adicionar ou editar e faz o carregamento dos dados
	 * @param idDepartamento
	 */
	public void initializeForm(Long idDepartamento) {
		if (idDepartamento == null) {
			this.viewState = ViewState.ADDING;
			this.departamento  = new Departamento();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdDepartamento(true);
			this.departamento = this.departamentoDao.findById(idDepartamento, false);
		}
	}
	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public String newDepartamento() {
		return "formCadDepartamento.xhtml?faces-redirect=true";
	}

	public String toListDepartamento() {
		return "formListDepartamento.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do departamento a ser editado
	 * @param departamentoID
	 * @return a p�gina
	 */
	public String changeToEdit(Long departamentoID) {
		return "formCadDepartamento.xhtml?faces-redirect=true&departamentoID=" + departamentoID;
	}

	/**
	 * Metodo que salva o Departamento
	 */
	@Transactional
	public void doSalvar(){
		try{
			if (this.viewState == ViewState.ADDING){
				System.out.println("entou dentro do adding");
				if (!departamentoDao.jaExiste(this.departamento.getDepartamento(),getUsuarioAutenticado().getIdEmpresa())){
					System.out.println("estou dentro do n�o existe departamento");
					this.departamento.setDeleted(false);
					this.departamento = this.departamentoDao.save(this.departamento);
					this.departamento = new Departamento();
					this.addInfo(true, "save.sucess");
				}else{
					this.addError(true, "departamento.exist");
				}
			}else{
				this.departamento = this.departamentoDao.save(this.departamento);
				this.departamento = new Departamento();
				this.addInfo(true, "save.sucess");
			}
		}catch (Exception e) {
			this.addError(true, "save.error");
		}
	}

	/**
	 * Metodo que exclui o Departamento 
	 * @return redireciona para a lista
	 */ 
	@Transactional
	public String doExcluir(){
		try{
			this.departamento.setDeleted(true);
			this.departamento = departamentoDao.save(this.departamento);
			this.addInfo(true, "delete.sucess");
			return toListDepartamento();
		}catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}
	
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "Departamento "+((Departamento) event.getObject()).getDepartamento()+" selecionada");  
		this.departamento = (Departamento) event.getObject();
		setVisivelPorIdDepartamento(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Metodo que possibilita exportar para excel a lista de departamento
	 */
	public List<Departamento> pegaListaDepartamento(){
		return departamentoDao.listDepartamentoAtivo();
	}

	public void criaExcel(){
		postProcessXLS(pegaListaDepartamento());
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
	public Departamento setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Departamento setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
