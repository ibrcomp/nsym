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
import br.com.nsym.domain.model.entity.cadastro.Secao;
import br.com.nsym.domain.model.repository.cadastro.SecaoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class SecaoBean extends AbstractBeanEmpDS<Secao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Secao secao = new Secao();

	@Inject
	private SecaoRepository secaoDao;

	@Getter
	private AbstractLazyModel<Secao> secaoModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdSecao = false;

	@PostConstruct
	public void init() {
		secaoModel = getLazySecao();
	}

	public AbstractLazyModel<Secao> getLazySecao() {
		this.secaoModel = new AbstractLazyModel<Secao>() {

			@Override
			public List<Secao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
						.withDirection(sortOrder.name());

				final Page<Secao> page = secaoDao.listByStatus(isDeleted, null, pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return secaoModel;
	}

	/**
	 * Inicializa o Form de visualiza��o para modo de adicionar ou editar e faz
	 * o carregamento dos dados
	 * 
	 * @param idSecao
	 */
	public void initializeForm(Long idSecao) {
		if (idSecao == null) {
			this.viewState = ViewState.ADDING;
			this.secao = new Secao();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdSecao(true);
			this.secao = this.secaoDao.findById(idSecao, false);
		}
	}

	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public String newSecao() {
		return "formCadSecao.xhtml?faces-redirect=true";
	}

	public String toListSecao() {
		return "formListSecao.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do secao a ser editado
	 * 
	 * @param secaoID
	 * @return a p�gina
	 */
	public String changeToEdit(Long secaoID) {
		return "formCadSecao.xhtml?faces-redirect=true&secaoID=" + secaoID;
	}

	/**
	 * Metodo que salva o Secao
	 */
	@Transactional
	public void doSalvar() {
		try {
			if (this.viewState == ViewState.ADDING) {
				System.out.println("entou dentro do adding");
				if (!secaoDao.jaExiste(this.secao.getSecao(),getUsuarioAutenticado().getIdEmpresa())) {
					System.out.println("estou dentro do n�o existe secao");
					this.secao.setDeleted(false);
					this.secao = this.secaoDao.save(this.secao);
					this.secao = new Secao();
					this.addInfo(true, "save.sucess");
				} else {
					this.addError(true, "secao.exist");
				}
			} else {
				this.secao = this.secaoDao.save(this.secao);
				this.secao = new Secao();
				this.addInfo(true, "save.sucess");
			}
		} catch (Exception e) {
			this.addError(true, "save.error");
		}
	}

	/**
	 * Metodo que exclui o Secao
	 * 
	 * @return redireciona para a lista
	 */
	@Transactional
	public String doExcluir() {
		try {
			this.secao.setDeleted(true);
			this.secao = secaoDao.save(this.secao);
			this.addInfo(true, "delete.sucess");
			return toListSecao();
		} catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}

	public void onRowSelect(SelectEvent event) throws IOException {
		this.addInfo(true, "Se��o "+((Secao) event.getObject()).getSecao() + " selecionada");
		this.secao = (Secao) event.getObject();
		setVisivelPorIdSecao(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}

	/**
	 * Metodo que possibilita exportar para excel a lista de secao
	 */
	public List<Secao> pegaListaSecao() {
		return secaoDao.listSecaoAtivo();
	}

	public void criaExcel() {
		postProcessXLS(pegaListaSecao());
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
	public Secao setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Secao setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
