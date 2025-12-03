package br.com.nsym.application.controller.nfe.tools;

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
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.fiscal.nfe.NaturezaOperacao;
import br.com.nsym.domain.model.repository.fiscal.nfe.NaturezaOperacaoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped

public class NaturezaOperacaoBean extends AbstractBeanEmpDS<NaturezaOperacao>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private NaturezaOperacao natOperacao;

	@Inject
	private NaturezaOperacaoRepository natOperacaoDao;

	@Getter
	private AbstractLazyModel<NaturezaOperacao> natOperacaoModel;

	@Getter
	@Setter
	private boolean Deleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdTributos = false;

	@PostConstruct
	public void init(){
		this.natOperacaoModel = getLazyNatOperacao();
	}


	/**
	 * Gera a lista de tributos em modo lazy
	 * @return a lista
	 */
	public AbstractLazyModel<NaturezaOperacao> getLazyNatOperacao(){
		this.natOperacaoModel = new AbstractLazyModel<NaturezaOperacao>() {

			@Override
			public List<NaturezaOperacao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
				.withDirection(sortOrder.name());

				final Page<NaturezaOperacao> page = natOperacaoDao.listByStatusFilial(isDeleted() , null,getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial(),null, pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return natOperacaoModel;
	}

	/**
	 * Inicializa��o da pagina em modo de Adi��o ou Edi��o
	 * @param idNfe
	 */
	public void initializeForm(Long idNfe) {
		if (idNfe == null) {
			this.viewState = ViewState.ADDING;
			this.natOperacao = new NaturezaOperacao();

		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdTributos(true);
			this.natOperacao = this.natOperacaoDao.findById(idNfe, false);
		}
	}

	/**
	 * Inicializa��o da pagina em modo listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
	}

	/**
	 * redireciona para a pagina com o ID da NFE a ser editado
	 * 
	 * @param natOperacaoID
	 * 
	 * @return
	 */
	public String changeToEdit(Long natOperacaoID) {
		return "formCadNaturezaOperacao.xhtml?faces-redirect=true&natOperacaoID=" + natOperacaoID;
	}

	/**
	 * redireciona para Cadastramento de nova NFE / edi��o de NFE j� cadastrado
	 * @return pagina de edi��o/inclusao de NFE
	 */
	public String newNaturezaOperacao() {
		return "formCadNaturezaOperacao.xhtml?faces-redirect=true";
	}
	/**
	 * Redireciona para Lista de produtos
	 * @return a lista de produto
	 */
	public String toListNaturezaOperacao() {
		return "formListNaturezaOperacao.xhtml?faces-redirect=true";
	}

	/**
	 * Evento que controla o item da lista selecionado 
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelect(SelectEvent event)throws IOException{
		this.addInfo(true, "A Natureza de Opera��o " + ((NaturezaOperacao)event.getObject()).getDescricao()+ " foi selecionado");
		this.natOperacao = (NaturezaOperacao) event.getObject();
		setVisivelPorIdTributos(true);
		System.out.println("Estou o RowSelect");
		this.viewState = ViewState.EDITING;
		changeToEdit(this.natOperacao.getId());

	}

	/**
	 * Metodo que possibilita exportar para excel a lista de Nfe
	 */
	public List<NaturezaOperacao> pegaListaNaturezaOperacao() {
		return natOperacaoDao.listaPorFilial(getUsuarioAutenticado().getIdEmpresa(),getUsuarioAutenticado().getIdFilial());
	}

	public void criaExcel() {
		postProcessXLS(pegaListaNaturezaOperacao());
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

	@Transactional
	public void doSalvar(){
		try{
			if (this.natOperacao.getId() == null){
				this.natOperacao.setDeleted(false);
				this.natOperacao = this.natOperacaoDao.save(this.natOperacao);
				this.addInfo(true, "save.sucess", this.natOperacao.getDescricao());
			}else{
				this.natOperacao = this.natOperacaoDao.save(this.natOperacao);
				this.addInfo(true, "save.update", this.natOperacao.getDescricao());
			}
		}catch (Exception e) {
			this.addError(true, "error.save", this.natOperacao.getDescricao() + e.getStackTrace());
		}
	}
	
	@Transactional
	public void doExcluir(){
		try {
			this.natOperacao.setDeleted(true);
			this.natOperacaoDao.save(this.natOperacao);
			this.addInfo(true, "delete.sucess", this.natOperacao.getDescricao());
		} catch (Exception e) {
			this.addError(true, "error.delete", this.natOperacao.getDescricao() + e.getStackTrace());
		}
	}


	@Override
	public NaturezaOperacao setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NaturezaOperacao setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

}
