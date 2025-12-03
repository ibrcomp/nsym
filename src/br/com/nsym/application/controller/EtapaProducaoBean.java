package br.com.nsym.application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.SortOrder;

import br.com.nsym.application.component.table.AbstractLazyModel;
import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Secao;
import br.com.nsym.domain.model.entity.fabrica.util.EtapaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.SequenciaLinhaProducao;
import br.com.nsym.domain.model.repository.fabrica.EtapaProducaoRepository;
import br.com.nsym.domain.model.repository.fabrica.LinhaProducaoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class EtapaProducaoBean extends AbstractBeanEmpDS<Secao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private EtapaProducao etapa = new EtapaProducao();

	@Inject
	private EtapaProducaoRepository etapaDao;
	
	@Getter
	@Setter
	private LinhaProducao linha = new LinhaProducao();
	
	@Inject
	private LinhaProducaoRepository linhaDao ;
	
	@Getter
	private AbstractLazyModel<LinhaProducao> linhaModel;

	@Getter
	private AbstractLazyModel<EtapaProducao> etapaModel;

	@Getter
	@Setter
	private boolean isDeleted = false;

	@Getter
	@Setter
	private boolean visivelPorIdEtapa = false;
	
	@Getter
	@Setter
	private List<EtapaProducao> etapaSource = new ArrayList<>();
	
	@Getter
	@Setter
	private List<EtapaProducao> etapaTarget = new ArrayList<>();
	
	@Getter
	@Setter
	private List<EtapaProducao> etapaTarguetEdit = new ArrayList<>();
	
	@Getter
	@Setter
	private List<SequenciaLinhaProducao> sequenciaTarget = new ArrayList<>();
	
	@Getter
	@Setter
	private DualListModel<EtapaProducao> sequenciaProducaoDL ;


	public AbstractLazyModel<EtapaProducao> getLazyEtapa() {
		this.etapaModel = new AbstractLazyModel<EtapaProducao>() {

			/**
			 *
			 */
			private static final long serialVersionUID = -7752186664553888533L;

			@Override
			public List<EtapaProducao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
						.withDirection(sortOrder.name());

				final Page<EtapaProducao> page = etapaDao.listByStatus(isDeleted, null, pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return etapaModel;
	}

	/**
	 * Inicializa o Form de visualizaï¿½ï¿½o para modo de adicionar ou editar e faz
	 * o carregamento dos dados
	 * 
	 * @param idSecao
	 */
	public void initializeForm(Long idEtapa) {
		if (idEtapa == null) {
			this.viewState = ViewState.ADDING;
			this.etapa = new EtapaProducao();
		} else {
			this.viewState = ViewState.EDITING;
			setVisivelPorIdEtapa(true);
			this.etapa = this.etapaDao.findById(idEtapa, false);
		}
	}

	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListing() {
		this.viewState = ViewState.LISTING;
		this.etapaModel = getLazyEtapa();
	}

	public String newEtapa() {
		return "formCadEtapaProd.xhtml?faces-redirect=true";
	}

	public String toListEtapa() {
		return "formListEtapaProd.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do secao a ser editado
	 * 
	 * @param secaoID
	 * @return a pï¿½gina
	 */
	public String changeToEdit(Long etapaID) {
		return "formCadEtapaProd.xhtml?faces-redirect=true&etapaID=" + etapaID;
	}

	/**
	 * Metodo que salva o Secao
	 */
	@Transactional
	public void doSalvar() {
		try {
			if (this.viewState == ViewState.ADDING) {
				System.out.println("entou dentro do adding");
				if (!etapaDao.existeEtapa(pegaIdEmpresa(),null,this.etapa.getDescricao())) {
					this.etapa.setDeleted(false);
				} else {
					this.addError(true, "fabrica.etapaProducao.exist");
				}
			}
			this.etapa = this.etapaDao.save(this.etapa);
			this.addInfo(true, "save.sucess", this.etapa.getDescricao());
			this.etapa = new EtapaProducao();
		} catch (NonUniqueResultException nu) {
			this.addError(true,nu.getMessage());
		} catch (Exception e) {
			this.addError(true, "save.error",e.getMessage());
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
			this.etapa.setDeleted(true);
			this.etapa = etapaDao.save(this.etapa);
			this.addInfo(true, "delete.sucess");
			return toListEtapa();
		} catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}

	public void onRowSelect(SelectEvent event) throws IOException {
		this.etapa = (EtapaProducao) event.getObject();
		setVisivelPorIdEtapa(true);
		this.viewState = ViewState.EDITING;
	}

	/**
	 * Metodo que possibilita exportar para excel a lista de secao
	 */
	public List<EtapaProducao> pegaListaEtapa() {
		return etapaDao.listaCriteriaPorFilial(pegaIdEmpresa(), null,true, false);
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
	
	//--------------------------------
	// Modulo LINHA DE PRODUCAO
	//--------------------------------
	
	public void initializeFormLinhaProd(Long idLinha) {
		this.etapaSource = this.etapaDao.listaCriteriaPorFilial(pegaIdEmpresa(), null, true,false);
		if (idLinha == null) {
			this.viewState = ViewState.ADDING;
			this.linha = new LinhaProducao(); 
			this.etapaTarget = new ArrayList<>();
			this.sequenciaProducaoDL = new DualListModel<EtapaProducao>(this.etapaSource,new ArrayList<>());
		} else {
			this.viewState = ViewState.EDITING;
			this.linha = this.linhaDao.pegaLinhaProducaoComSequencia(false, idLinha, pegaIdEmpresa(), null, false);
			this.etapaTarguetEdit = this.linhaDao.pegaSequenciaProducaoEmOrdem(false, idLinha, pegaIdEmpresa(), null, false);
			for (EtapaProducao etapaProducao : this.etapaTarguetEdit) {
				this.etapaSource.remove(etapaProducao);
			}
			this.sequenciaProducaoDL = new DualListModel<EtapaProducao>(this.etapaSource,this.etapaTarguetEdit);
		}
	}

	/**
	 * Define o status da view para modo de Listagem
	 */
	public void initializeListingLinhaProd() {
		this.viewState = ViewState.LISTING;
		this.linhaModel = getLazyLinha();
	}
	
	
	/**
	 * Lista lazy de linhas de Producao
	 * @return lista lazy
	 */
	public AbstractLazyModel<LinhaProducao> getLazyLinha() {
		this.linhaModel = new AbstractLazyModel<LinhaProducao>() {
			private static final long serialVersionUID = -2625160439428043275L;

			@Override
			public List<LinhaProducao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				System.out.println("Estou no Produto");
				final PageRequest pageRequest = new PageRequest();

				pageRequest.setFirstResult(first).withPageSize(pageSize).sortingBy(sortField, "inclusion")
						.withDirection(sortOrder.name());

				final Page<LinhaProducao> page = linhaDao.listByStatus(isDeleted, null, pegaIdEmpresa(), pageRequest);

				this.setRowCount(page.getTotalPagesInt());

				return page.getContent();
			}
		};
		return linhaModel;
	}
	
	public String newLinha() {
		return "formCadLinhaProd.xhtml?faces-redirect=true";
	}

	public String toListLinha() {
		return "formListLinhaProd.xhtml?faces-redirect=true";
	}

	/**
	 * redireciona para a pagina com o ID do secao a ser editado
	 * 
	 * @param secaoID
	 * @return a pï¿½gina
	 */
	public String changeToEditLinha(Long linhaID) {
		return "formCadLinhaProd.xhtml?faces-redirect=true&linhaID=" + linhaID;
	}
	
	public void onRowSelectLinha(SelectEvent event) throws IOException {
		this.linha = (LinhaProducao) event.getObject();
		this.viewState = ViewState.EDITING;
	}
	
	/**
	 * Metodo que salva a Linha de producao
	 */
	@Transactional
	public void doSalvarLinha() {
		try {
			if (this.linha.getId() == null) {
				if (this.viewState == ViewState.ADDING) {
					System.out.println("entou dentro do adding");
					if (!linhaDao.existeLinha(pegaIdEmpresa(),null,this.etapa.getDescricao())) {
						this.linha.setDeleted(false);
					} else {
						this.addError(true, "fabrica.etapaProducao.exist");
					}
				}
				Long i = 0l ;
				for (EtapaProducao linhaProd : sequenciaProducaoDL.getTarget()) {
					SequenciaLinhaProducao tempSeq = new SequenciaLinhaProducao();
					i++;
					tempSeq.setEtapa(linhaProd);
					tempSeq.setIndice(i);
					tempSeq.setLinha(this.linha);
					tempSeq.setDeleted(false);
					this.sequenciaTarget.add(tempSeq);
				}
				this.linha.setSequenciaProducao(this.sequenciaTarget);
				this.linha = this.linhaDao.save(this.linha);
				this.addInfo(true, "save.sucess", this.linha.getDescricao());
			}else {
				this.addWarning(true,"fabrica.update.notAllowed");
			}
		} catch (NonUniqueResultException nu) {
			this.addError(true,nu.getMessage());
		} catch (Exception e) {
			this.addError(true, "save.error",e.getMessage());
		}
	}

	/**
	 * Metodo que exclui a linha de producao
	 * 
	 * @return redireciona para a lista
	 */
	@Transactional
	public String doExcluirLinha() {
		try {
			this.linha.setDeleted(true);
			this.linha = linhaDao.save(this.linha);
			this.addInfo(true, "delete.sucess");
			return toListLinha();
		} catch (Exception e) {
			this.addError(false, "error.delete");
			return null;
		}
	}
	
	@Transactional
	public void excluiLinhaProducao(LinhaProducao linhaSelect){
		// fazer validação para verificar se a linha nao foi utilizada 
		this.linhaDao.delete(linhaSelect);
	}

}
