package br.com.nsym.application.controller.reforma;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.transaction.Transactional;

import br.com.ibrcomp.exception.EstoqueException;
import br.com.ibrcomp.exception.ParametroException;
import br.com.ibrcomp.interceptor.RollbackOn;
import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.fiscal.ParamReforma2026;
import br.com.nsym.domain.model.entity.fiscal.reforma.CClassTrib;
import br.com.nsym.domain.model.entity.fiscal.reforma.CstIbsCbs;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTNormal;
import br.com.nsym.domain.model.entity.fiscal.tools.CSTSimples;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.repository.fiscal.ParamReforma2026Repository;
import br.com.nsym.domain.model.repository.fiscal.reforma.CClassTribRepository;
import br.com.nsym.domain.model.repository.fiscal.reforma.CstIbsCbsRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ParamReforma2026Bean extends AbstractBeanEmpDS<ParamReforma2026> {


	private static final long serialVersionUID = -3468470794168423726L;

	@Inject
    private ParamReforma2026Repository repository;

    /** Lista para o DataTable */
	@Getter
	@Setter
    private List<ParamReforma2026> lista;

    /** Registro em edição */
	@Getter
	@Setter
    private ParamReforma2026 entidade;
	
	@Inject
	private CstIbsCbsRepository ibsCbsDao;
	
	@Inject
	private CClassTribRepository cClassTribDao;
	
	@Getter
	@Setter
	private CClassTrib cClassTrib;

    /** Filtros simples para pesquisa */
	@Getter
	@Setter
    private String filtroNcm;
	@Getter
	@Setter
    private String filtroCfop;
	@Getter
	@Setter
    private String filtroUfOrig;
	@Getter
	@Setter
    private String filtroUfDest;
	@Getter
	@Setter
    private Boolean filtroAtivo;
	
	@Getter
	@Setter
	private CSTSimples cstVendaSimples;
	
	@Getter
	@Setter
	private CSTNormal cstVendaNormal;


    @PostConstruct
    public void init() {
        initializeListing();
    }

    /** Chamado pelo <f:viewAction> na tela */
    public void initializeListing() {
        carregarLista();
        this.viewState = ViewState.LISTING;
    }

    private void carregarLista() {
        // Se você já tiver um método de filtro no repository, use aqui.
        // Por enquanto, usamos uma listagem geral ordenada.
        List<ParamReforma2026> result = repository.listarAtivosOrdenados(pegaIdEmpresa(),pegaIdFilial());
        if (result == null) {
            result = new ArrayList<ParamReforma2026>();
        }

        // Filtro em memória simples (pode ser trocado para Criteria no repository depois)
        final String ncmFilter = filtroNcm != null ? filtroNcm.trim() : null;
        final String cfopFilter = filtroCfop != null ? filtroCfop.trim() : null;
        final String ufOrigFilter = filtroUfOrig != null ? filtroUfOrig.trim() : null;
        final String ufDestFilter = filtroUfDest != null ? filtroUfDest.trim() : null;
        final Boolean ativoFilter = filtroAtivo;

        List<ParamReforma2026> filtrados = new ArrayList<ParamReforma2026>();
        for (ParamReforma2026 p : result) {
            if (ncmFilter != null && !ncmFilter.isEmpty()) {
                String ncm = p.getNcmPrefix();
                if (ncm == null || !ncm.startsWith(ncmFilter)) {
                    continue;
                }
            }
            if (cfopFilter != null && !cfopFilter.isEmpty()) {
                String cf = p.getCfop();
                if (cf == null || !cf.startsWith(cfopFilter)) {
                    continue;
                }
            }
            if (ufOrigFilter != null && !ufOrigFilter.isEmpty()) {
                String uo = p.getUfOrig();
                if (uo == null || !uo.equalsIgnoreCase(ufOrigFilter)) {
                    continue;
                }
            }
            if (ufDestFilter != null && !ufDestFilter.isEmpty()) {
                String ud = p.getUfDest();
                if (ud == null || !ud.equalsIgnoreCase(ufDestFilter)) {
                    continue;
                }
            }
            if (ativoFilter != null) {
                if (p.getAtivo() == null || !p.getAtivo().equals(ativoFilter)) {
                    continue;
                }
            }
            filtrados.add(p);
        }

        this.lista = filtrados;
    }

    /** Acionado pelo botão "Pesquisar" */
    public void pesquisar() {
        carregarLista();
    }

    /** Prepara novo registro */
    public void novo() {
        ParamReforma2026 p = new ParamReforma2026();
        p.setAtivo(Boolean.TRUE);
        p.setVigenciaIni(LocalDate.of(2026, 1, 1)); // default mínimo
        this.entidade = p;
        this.viewState = ViewState.EDITING;
    }

    /** Editar registro existente */
    public void editar(ParamReforma2026 p) {
        this.entidade = p;
        if (this.entidade != null) {
        	if (this.entidade.getCClassTrib() != null) {
        		this.cClassTrib = this.entidade.getCClassTrib();
        	}
        	if (this.entidade.getCsosn() != null ) {
        		this.cstVendaSimples = CSTSimples.fromCodigo(this.entidade.getCsosn());
        	}
        	if (this.entidade.getCst() != null ) {
        		this.cstVendaNormal = CSTNormal.fromCodigo(this.entidade.getCst());
        	}
        }
        this.viewState = ViewState.EDITING;
    }

    /** Voltar para listagem */
    public void cancelarEdicao() {
        this.entidade = null;
        this.viewState = ViewState.LISTING;
        carregarLista();
    }

    /** Salvar (insert/update) */
    @RollbackOn({ParametroException.class})
    @Transactional
    public void salvar() {
    	try {
    		if (this.entidade == null) {
    			return;
    		}
    		// Ajuste estes métodos conforme seu GenericRepositoryEmpDS
    		// Ex.: repository.salvar(entidade) ou repository.merge(entidade)

    		if (this.cstVendaSimples != null ) {
    			this.entidade.setCsosn(this.cstVendaSimples.getCst());
    		}
    		if (this.cstVendaNormal != null ) {
    			this.entidade.setCst(this.cstVendaNormal.getCst());
    		}
    		if (this.cClassTrib != null) {
    			this.entidade.setCClassTrib(this.cClassTrib);
    		}else {
    			throw new ParametroException(this.translate("reforma.cClassTrib.required" ));
    		}

    		this.entidade = repository.save(this.entidade);

    		this.viewState = ViewState.LISTING;
    		carregarLista();
    	}catch (ParametroException p) {
    		this.addError(true,p.getMessage());
    	}catch (Exception e) {
    		this.addError(true,e.getMessage());
    	}
    }

    /** Excluir registro */
    @Transactional
    public void excluir(ParamReforma2026 p) {
        if (p == null) {
            return;
        }
        ParamReforma2026 gerenciado = p;
        if (p.getId() != null) {
            // Carrega se precisar garantir que está gerenciado
            gerenciado = repository.findById(p.getId(),false);
        }
        if (gerenciado != null) {
            repository.delete(gerenciado);
        }
        carregarLista();
    }
    
    public TipoCliente[] getTiposCliente() {
        return TipoCliente.values();
    }
    
	public CSTSimples[] getListaVendaSimples() {
		return CSTSimples.values();
	}
	
	public CSTNormal[] getListaVendaNormal() {
		return CSTNormal.values();
	}
	
    public List<CstIbsCbs> listaCstReforma(){
    	return ibsCbsDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(), false, false);
    }
    
    public List<CClassTrib> listaCClassTrib(){
    	return cClassTribDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(), false, false);
    }


	@Override
	public ParamReforma2026 setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParamReforma2026 setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		
	}
}
