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
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.repository.cadastro.TamanhoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class TamanhoBean extends AbstractBeanEmpDS<Tamanho> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Tamanho tamanho = new Tamanho();

	@Inject
	private TamanhoRepository tamanhoDao;

	@Getter
	private AbstractLazyModel<Tamanho> tamanhoModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdTamanho = false;

	@PostConstruct
	public void init() {
		tamanhoModel = getLazyTamanho();
	}

	public AbstractLazyModel<Tamanho> getLazyTamanho() {
		this.tamanhoModel = new AbstractLazyModel<Tamanho>() {

			@Override
			public List<Tamanho> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
						.withDirection(sortOrder.name());

				final Page<Tamanho> page = tamanhoDao.listByStatus(isDeleted, null, pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return tamanhoModel;
	}

	/**
	 * Inicializa o Form de visualiza��o para modo de adicionar ou editar e faz
	 * o carregamento dos dados
	 * 
	 * @param idTamanho
	 */
	public void initializeForm(Long idTamanho) {
		if (idTamanho == null) {
			this.viewState = ViewState.ADDING;
			this.tamanho = new Tamanho();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdTamanho(true);
			this.tamanho = this.tamanhoDao.findById(idTamanho, false);
		}
	}

	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public String newTamanho() {
		return "formCadTamanho.xhtml?faces-redirect=true";
	}

	public String toListTamanho() {
		return "formListTamanho.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do tamanho a ser editado
	 * 
	 * @param tamanhoID
	 * @return a p�gina
	 */
	public String changeToEdit(Long tamanhoID) {
		return "formCadTamanho.xhtml?faces-redirect=true&tamanhoID=" + tamanhoID;
	}

	/**
	 * Metodo que salva o Tamanho
	 */
	@Transactional
	public void doSalvar() {
		try {
			if (this.viewState == ViewState.ADDING) {
				System.out.println("entou dentro do adding");
				if (!tamanhoDao.jaExiste(this.tamanho.getTamanho(),getUsuarioAutenticado().getIdEmpresa())) {
					System.out.println("estou dentro do n�o existe tamanho");
					this.tamanho.setDeleted(false);
					this.tamanho.setTamanho(this.tamanho.getTamanho().toUpperCase());
					this.tamanho = this.tamanhoDao.save(this.tamanho);
					this.addInfo(true, "save.sucess",this.tamanho.getTamanho());
					this.tamanho = new Tamanho();
				} else {
					this.addError(true, "tamanho.exist");
				}
			} else {
				this.tamanho = this.tamanhoDao.save(this.tamanho);
				this.addInfo(true, "save.sucess");
			}
		} catch (Exception e) {
			this.addError(true, "save.error");
		}
	}

	/**
	 * Metodo que exclui o Tamanho
	 * 
	 * @return redireciona para a lista
	 */
	@Transactional
	public String doExcluir() {
		try {
			this.tamanho.setDeleted(true);
			this.tamanho = tamanhoDao.save(this.tamanho);
			this.addInfo(true, "delete.sucess");
			return toListTamanho();
		} catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}

	public void onRowSelect(SelectEvent event) throws IOException {
		this.addInfo(true, "tamanho.selection",((Tamanho) event.getObject()).getTamanho());
		this.tamanho = (Tamanho) event.getObject();
		setVisivelPorIdTamanho(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}

	/**
	 * Metodo que possibilita exportar para excel a lista de tamanho
	 */
	public List<Tamanho> pegaListaTamanho() {
		return tamanhoDao.listTamanhoAtivo();
	}

	public void criaExcel() {
		postProcessXLS(pegaListaTamanho());
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
	public Tamanho setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tamanho setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
