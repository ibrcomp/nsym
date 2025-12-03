package br.com.nsym.application.controller.reforma;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.fiscal.ParamReforma2026;
import br.com.nsym.domain.model.entity.tools.TipoCliente;
import br.com.nsym.domain.model.repository.fiscal.ParamReforma2026Repository;
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
        this.viewState = ViewState.EDITING;
    }

    /** Voltar para listagem */
    public void cancelarEdicao() {
        this.entidade = null;
        this.viewState = ViewState.LISTING;
        carregarLista();
    }

    /** Salvar (insert/update) */
    @Transactional
    public void salvar() {
        if (this.entidade == null) {
            return;
        }
        // Ajuste estes métodos conforme seu GenericRepositoryEmpDS
        // Ex.: repository.salvar(entidade) ou repository.merge(entidade)
        this.entidade = repository.save(this.entidade);

        this.viewState = ViewState.LISTING;
        carregarLista();
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
