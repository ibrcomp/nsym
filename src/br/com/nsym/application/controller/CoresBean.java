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
import br.com.nsym.domain.model.entity.cadastro.Cor;
import br.com.nsym.domain.model.repository.cadastro.CoresRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class CoresBean extends AbstractBeanEmpDS<Cor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Cor cores = new Cor();

	@Inject
	private CoresRepository coresDao;

	@Getter
	private AbstractLazyModel<Cor> coresModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdCores = false;

	@PostConstruct
	public void init() {
		coresModel = getLazyCor();
	}

	public AbstractLazyModel<Cor> getLazyCor() {
		this.coresModel = new AbstractLazyModel<Cor>() {

			@Override
			public List<Cor> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
						.withDirection(sortOrder.name());

				final Page<Cor> page = coresDao.listByStatus(isDeleted, null, pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return coresModel;
	}

	/**
	 * Inicializa o Form de visualiza��o para modo de adicionar ou editar e faz
	 * o carregamento dos dados
	 * 
	 * @param idCor
	 */
	public void initializeForm(Long idCor) {
		if (idCor == null) {
			this.viewState = ViewState.ADDING;
			this.cores = new Cor();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdCores(true);
			this.cores = this.coresDao.findById(idCor, false);
		}
	}

	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public String newCores() {
		return "formCadCores.xhtml?faces-redirect=true";
	}

	public String toListCores() {
		return "formListCores.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do cores a ser editado
	 * 
	 * @param coresID
	 * @return a p�gina
	 */
	public String changeToEdit(Long coresID) {
		return "formCadCores.xhtml?faces-redirect=true&coresID=" + coresID;
	}

	/**
	 * Metodo que salva o Cores
	 */
	@Transactional
	public void doSalvar() {
		try {
			if (this.viewState == ViewState.ADDING) {
				System.out.println("entou dentro do adding");
				if (!coresDao.jaExiste(this.cores.getNome(),getUsuarioAutenticado().getIdEmpresa())) {
					System.out.println("estou dentro do n�o existe cores");
					this.cores.setDeleted(false);
					this.cores.setNome(this.cores.getNome().toUpperCase());
					this.cores = this.coresDao.save(this.cores);
					this.addInfo(true, "save.sucess",this.cores.getNome());
					this.cores = new Cor();
				} else {
					this.addError(true, "cores.exist");
				}
			} else {
				this.cores = this.coresDao.save(this.cores);
				this.addInfo(true, "save.sucess");
			}
		} catch (Exception e) {
			this.addError(true, "save.error");
		}
	}

	/**
	 * Metodo que exclui o Cor
	 * 
	 * @return redireciona para a lista
	 */
	@Transactional
	public String doExcluir() {
		try {
			this.cores.setDeleted(true);
			this.cores = coresDao.save(this.cores);
			this.addInfo(true, "delete.sucess");
			return toListCores();
		} catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}

	public void onRowSelect(SelectEvent event) throws IOException {
		this.addInfo(true, "cor.selection",((Cor) event.getObject()).getNome());
		this.cores = (Cor) event.getObject();
		setVisivelPorIdCores(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}

	/**
	 * Metodo que possibilita exportar para excel a lista de cores
	 */
	public List<Cor> pegaListaCor() {
		return coresDao.listCoresAtivo();
	}

	public void criaExcel() {
		postProcessXLS(pegaListaCor());
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
	public Cor setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cor setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
