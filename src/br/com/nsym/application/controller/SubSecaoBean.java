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
import br.com.nsym.domain.model.entity.cadastro.SubSecao;
import br.com.nsym.domain.model.repository.cadastro.SubSecaoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class SubSecaoBean extends AbstractBeanEmpDS<SubSecao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private SubSecao subSecao = new SubSecao();

	@Inject
	private SubSecaoRepository subSecaoDao;

	@Getter
	private AbstractLazyModel<SubSecao> subSecaoModel;
	

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdSubSecao = false;


	@PostConstruct
	public void init(){
		subSecaoModel = getLazySubSecao();
	}

	public AbstractLazyModel<SubSecao> getLazySubSecao(){
		this.subSecaoModel = new AbstractLazyModel<SubSecao>() {

			@Override
			public List<SubSecao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<SubSecao> page = subSecaoDao.listByStatus(isDeleted , null,pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return subSecaoModel;
	}

	/**
	 *  Inicializa o Form de visualiza��o para modo de adicionar ou editar e faz o carregamento dos dados
	 * @param idSubSecao
	 */
	public void initializeForm(Long idSubSecao) {
		if (idSubSecao == null) {
			this.viewState = ViewState.ADDING;
			this.subSecao  = new SubSecao();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdSubSecao(true);
			this.subSecao = this.subSecaoDao.findById(idSubSecao, false);
		}
	}
	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	public String newSubSecao() {
		return "formCadSubSecao.xhtml?faces-redirect=true";
	}

	public String toListSubSecao() {
		return "formListSubSecao.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do subsecao a ser editado
	 * @param subsecaoID
	 * @return a p�gina
	 */
	public String changeToEdit(Long subsecaoID) {
		return "formCadSubSecao.xhtml?faces-redirect=true&subsecaoID=" + subsecaoID;
	}

	/**
	 * Metodo que salva o SubSecao
	 */
	@Transactional
	public void doSalvar(){
		try{
			if (this.viewState == ViewState.ADDING){
				System.out.println("entou dentro do adding");
				if (!subSecaoDao.jaExiste(this.subSecao.getSubSecao(),getUsuarioAutenticado().getIdEmpresa())){
					System.out.println("estou dentro do n�o existe subsecao");
					this.subSecao.setDeleted(false);
					this.subSecao = this.subSecaoDao.save(this.subSecao);
					this.subSecao = new SubSecao();
					this.addInfo(true, "save.sucess");
				}else{
					this.addError(true, "subsecao.exist");
				}
			}else{
				this.subSecao = this.subSecaoDao.save(this.subSecao);
				this.subSecao = new SubSecao();
				this.addInfo(true, "save.sucess");
			}
		}catch (Exception e) {
			this.addError(true, "save.error");
		}
	}

	/**
	 * Metodo que exclui o SubSecao 
	 * @return redireciona para a lista
	 */ 
	@Transactional
	public String doExcluir(){
		try{
			this.subSecao.setDeleted(true);
			this.subSecao = subSecaoDao.save(this.subSecao);
			this.addInfo(true, "delete.sucess");
			return toListSubSecao();
		}catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}
	
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "SubSecao "+((SubSecao) event.getObject()).getSubSecao()+" selecionada");  
		this.subSecao = (SubSecao) event.getObject();
		setVisivelPorIdSubSecao(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Metodo que possibilita exportar para excel a lista de subsecao
	 */
	public List<SubSecao> pegaListaSubSecao(){
		return subSecaoDao.listSubSecaoAtivo();
	}

	public void criaExcel(){
		postProcessXLS(pegaListaSubSecao());
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
	public SubSecao setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubSecao setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
