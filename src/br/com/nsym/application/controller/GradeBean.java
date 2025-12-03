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
import br.com.nsym.domain.model.entity.cadastro.Grade;
import br.com.nsym.domain.model.repository.cadastro.GradeRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class GradeBean extends AbstractBeanEmpDS<Grade> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Grade grade = new Grade();

	@Inject
	private GradeRepository gradeDao;

	@Getter
	private AbstractLazyModel<Grade> gradeModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdGrade = false;

	@PostConstruct
	public void init() {
		gradeModel = getLazyGrade();
	}

	public AbstractLazyModel<Grade> getLazyGrade() {
		this.gradeModel = new AbstractLazyModel<Grade>() {

			@Override
			public List<Grade> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
						.withDirection(sortOrder.name());

				final Page<Grade> page = gradeDao.listByStatus(isDeleted, null, pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return gradeModel;
	}

	/**
	 * Inicializa o Form de visualiza��o para modo de adicionar ou editar e faz
	 * o carregamento dos dados
	 * 
	 * @param idGrade
	 */
	public void initializeForm(Long idGrade) {
		if (idGrade == null) {
			this.viewState = ViewState.ADDING;
			this.grade = new Grade();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdGrade(true);
			this.grade = this.gradeDao.findById(idGrade, false);
		}
	}

	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public String newGrade() {
		return "formCadGrade.xhtml?faces-redirect=true";
	}

	public String toListGrade() {
		return "formListGrade.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do grade a ser editado
	 * 
	 * @param gradeID
	 * @return a p�gina
	 */
	public String changeToEdit(Long gradeID) {
		return "formCadGrade.xhtml?faces-redirect=true&gradeID=" + gradeID;
	}

	/**
	 * Metodo que salva o Grade
	 */
	@Transactional
	public void doSalvar() {
		try {
			if (this.viewState == ViewState.ADDING) {
				System.out.println("entou dentro do adding");
				if (!gradeDao.jaExiste(this.grade.getGrade())) {
					System.out.println("estou dentro do n�o existe grade");
					this.grade.setDeleted(false);
					this.grade = this.gradeDao.save(this.grade);
					this.addInfo(true, "save.sucess");
				} else {
					this.addError(true, "grade.exist");
				}
			} else {
				this.grade = this.gradeDao.save(this.grade);
				this.addInfo(true, "save.sucess");
			}
		} catch (Exception e) {
			this.addError(true, "save.error");
		}
	}

	/**
	 * Metodo que exclui o Grade
	 * 
	 * @return redireciona para a lista
	 */
	@Transactional
	public String doExcluir() {
		try {
			this.grade.setDeleted(true);
			this.grade = gradeDao.save(this.grade);
			this.addInfo(true, "delete.sucess");
			return toListGrade();
		} catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}

	public void onRowSelect(SelectEvent event) throws IOException {
		this.addInfo(true, "Se��o "+((Grade) event.getObject()).getGrade() + " selecionada");
		this.grade = (Grade) event.getObject();
		setVisivelPorIdGrade(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}

	/**
	 * Metodo que possibilita exportar para excel a lista de grade
	 */
	public List<Grade> pegaListaGrade() {
		return gradeDao.listGradeAtivo();
	}

	public void criaExcel() {
		postProcessXLS(pegaListaGrade());
	}

	/**
	 * Criar um documento em excel personalizado.
	 * 
	 * @param document
	 */
	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColorPredefined.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
		}
	}

	@Override
	public Grade setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Grade setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
