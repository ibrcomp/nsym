package br.com.nsym.application.controller.reforma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.primefaces.event.SelectEvent;

import br.com.nsym.application.controller.AbstractBeanEmpDS;
import br.com.nsym.domain.model.entity.fiscal.reforma.CClassTrib;
import br.com.nsym.domain.model.entity.fiscal.reforma.CstIbsCbs;
import br.com.nsym.domain.model.entity.fiscal.tools.TipoTributo;
import br.com.nsym.domain.model.repository.fiscal.reforma.CClassTribRepository;
import br.com.nsym.domain.model.repository.fiscal.reforma.CstIbsCbsRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class CstIbsCbsBean extends AbstractBeanEmpDS<CstIbsCbs> {

    private static final long serialVersionUID = 1L;

    @Inject
    private CstIbsCbsRepository cstIbsCbsRepository;

    @Inject
    private CClassTribRepository cClassTribRepository;

    /** Lista de CST IBS/CBS para o DataTable */
    @Getter
    @Setter
    private List<CstIbsCbs> lista;

    /** Registro em edição (no diálogo) */
    @Getter
    @Setter
    private CstIbsCbs entidade;
    

    /** Lista de classificações tributárias para o combo */
    @Getter
    @Setter
    private List<CClassTrib> listaClassTrib;

    @PostConstruct
    public void init() {
        initializeListing();
    }

    /**
     * Chamado pelo &lt;f:viewAction&gt; na tela.
     */
    public void initializeListing() {
    	this.viewState = ViewState.LISTING;
        carregarListaCst();
        carregarListaClassTrib();
    }

    private void carregarListaCst() {
        List<CstIbsCbs> result = cstIbsCbsRepository.listaCriteriaPorFilial(pegaIdEmpresa(),pegaIdFilial(),false,false);
        if (result == null) {
            result = new ArrayList<CstIbsCbs>();
        }
        this.lista = result;
    }

    private void carregarListaClassTrib() {
        List<CClassTrib> result = cClassTribRepository.listaCriteriaPorFilial(pegaIdEmpresa(),pegaIdFilial(),false,false);
        if (result == null) {
            result = new ArrayList<CClassTrib>();
        }
        this.listaClassTrib = result;
    }

    /**
     * Prepara um novo CST IBS/CBS para cadastro.
     */
    public void novo() {
    	this.viewState = ViewState.EDITING;
        CstIbsCbs c = new CstIbsCbs();
        c.setDeleted(false);;
        this.entidade = c;
    }

    /**
     * Edita um registro existente (chamado pelo botão de Visualizar).
     */
    @Transactional
    public void editar(CstIbsCbs c) {
    	if (c == null) {
    		this.addError(true,"Edição esta nula!");
    		return;
    	}
    	this.entidade = c;
    	this.updateAndOpenDialog("dialogEdicao", "dlgCstIbsCbs");
    }
    
    /**
     * Cancela a edição e volta apenas para a listagem.
     */
    public void cancelarEdicao() {
    	this.viewState= ViewState.LISTING;
        this.entidade = null;
        carregarListaCst();
    }

    /**
     * Salva (insert/update) o CST IBS/CBS.
     */
    @Transactional
    public void salvar() {
        if (this.entidade == null) {
            return;
        }

        // Ajuste o método conforme seu GenericRepositoryEmpDS
        this.entidade = cstIbsCbsRepository.save(this.entidade);

        // Seta pagina em modo listagem
        this.viewState = ViewState.LISTING;
        // atualiza lista
        carregarListaCst();
    }

    /**
     * Exclui o registro selecionado.
     */
    @Transactional
    public void excluir(CstIbsCbs c) {
        if (c == null) {
            return;
        }

        CstIbsCbs gerenciado = c;
        if (c.getId() != null) {
            gerenciado = cstIbsCbsRepository.findById(c.getId(),false);
        }

        if (gerenciado != null) {
            cstIbsCbsRepository.delete(gerenciado);
        }

        carregarListaCst();
    }
    
    public TipoTributo[] getTiposTributo() {
        return TipoTributo.values();
    }
    
	@Override
	public CstIbsCbs setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CstIbsCbs setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Redireciona para tela Principal
	 * @return Dashboard
	 */
	public String toTelaPrincipal() {
		return "/main/dashboard.xhtml?faces-redirect=true";
	}
}
