package br.com.nsym.application.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.HibernateException;
import org.primefaces.event.SelectEvent;

import br.com.nsym.application.component.table.AbstractDataModel;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.fabrica.OrdemServico;
import br.com.nsym.domain.model.entity.fabrica.Producao;
import br.com.nsym.domain.model.entity.fabrica.Servico;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.repository.cadastro.FornecedorRepository;
import br.com.nsym.domain.model.repository.fabrica.OrdemServicoRepository;
import br.com.nsym.domain.model.repository.fabrica.ProducaoRepository;
import br.com.nsym.domain.model.repository.fabrica.ServicoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class OSBean extends AbstractBeanEmpDS<OrdemServico>{
	
	
	@Getter
	@Setter
	private Configuration configUser;
	
	@Getter
	@Setter
	private EmpUser empresaUsuario;
	
	@Setter
	private AbstractDataModel<OrdemServico> listaOSModel; 
	
	@Getter
	@Setter
	private List<OrdemServico> listaOS;
	
	@Inject
	private OrdemServicoRepository osDao;
	
	@Getter
	@Setter
	private OrdemServico os;
	
	@Getter
	@Setter
	private Producao producao;
	
	@Getter
	@Setter
	private List<Producao> listaOP = new ArrayList<>();
	
	@Getter
	@Setter
	private Fornecedor prestador;
	
	@Getter
	@Setter
	private List<Fornecedor> listaPrestador = new ArrayList<>();
	
	@Inject
	private FornecedorRepository fornecedorDao;
	
	@Inject
	private ProducaoRepository producaoDao;
	
	@Getter
	@Setter
	private LinhaProducao linhaProducao;
	
	@Getter
	@Setter
	private List<LinhaProducao> listaLinhaProducao = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean retorno = false;
	
	@Getter
	@Setter
	private Servico servico;
	
	@Inject
	private ServicoRepository servicoDao;
	
	@Getter
	@Setter
	private List<Servico> listaDeServicos = new ArrayList<>();
	
	@Getter
	@Setter
	private List<Servico> listaServicosSelecionados = new ArrayList<>();
	
	@Getter
	@Setter
	private String refService;
	
	@Getter
	@Setter
	private BigDecimal quantidade = new BigDecimal("0");
	
	@Getter
	@Setter
	private BigDecimal valorServico = new BigDecimal("0");
	
	/**
	 *
	 */
	private static final long serialVersionUID = 7619246316824237685L;
	
	@PostConstruct
	public void init(){
		this.empresaUsuario  = this.configEmpUser();
		this.configUser = this.getUsuarioAutenticado().getConfig();
	}

	@Override
	public OrdemServico setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrdemServico setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		
	}
	
	public void initializeCadOs(Long osID) {
		if (osID == null) {
			this.viewState = ViewState.ADDING;
			this.os = new OrdemServico();
			this.listaOP = geraListaOP();
			this.listaPrestador = geraListaPrestador();
		}else {
			this.viewState = ViewState.EDITING;
		}
	}
	
	public void initializeListing(Long op) {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
		this.listaOS = this.osDao.listaOSporOP(op, pegaIdEmpresa(), pegaIdFilial(), false, false);
		this.listaOSModel =  getListaOSModel();
	}
	
	/**
	 * redireciona para a pagina com o ID da OS.
	 * 
	 * @param osID
	 * 
	 * @returna pagina de edicao da OS
	 */
	public String changeToEditOS(Long osID) {
		return "formCadOS.xhtml?faces-redirect=true&osID=" + osID;
	}
	
	public String toListOS() {
		return "formListOS.xhtml?faces-redirect=true";
	}
	
	public String newOS() {
		return "formCadOS.xhtml?faces-redirect=true";
	}

	public AbstractDataModel<OrdemServico> getListaOSModel(){
		return new AbstractDataModel<OrdemServico>(this.listaOS);
	}
	
	/**
	 * Retorna uma lista com as OP disponíveis para envio para prestadores de serviço
	 * @return lista preenchida com as OPs.
	 */
	public List<Producao> geraListaOP(){
		// gerar lista de producao com status = aguardando inicio para proximo estagio da produçao.
		// não exibir OP que esta em produçao ou que ja foi concluida!
		return this.producaoDao.pegaProducaoAguardandoInicio(false, pegaIdEmpresa(), pegaIdFilial(), true); 
	}
	
	/*
	 * Retorna lista de Prestadores de serviço
	 * @ return 
	 */
	public List<Fornecedor> geraListaPrestador(){
		return this.fornecedorDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(), true, false);
	}
	
	/**toTela
	 * Exibe o dialog com a lista de produtos
	 */
	public void telaListaServico(){
		this.listaDeServicos = this.servicoDao.listaCriteriaPorFilial(pegaIdEmpresa(), pegaIdFilial(), false, false);
		this.updateAndOpenDialog("PesquisaServicoDialog", "dialogPesquisaServico");
	}
	
	/**
	 * Método que seta o Serviço quando selecionado em uma lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectServico(SelectEvent event)throws IOException{
		this.servico = (Servico) event.getObject();
		this.refService = this.servico.getId().toString();
	}
	
	/**
	 * Método que seta o Serviço quando selecionado em uma lista
	 * @param event
	 * @throws IOException
	 */
	public void onRowSelectOS(SelectEvent event)throws IOException{
		this.os = (OrdemServico) event.getObject();
//		this.refService = this.os.getId().toString();
	}
	
	public void localizaServico() {		
		try {
			// localiza o serviço pela ID
			this.servico = this.servicoDao.pegaServicoPorID(new BigDecimal(this.refService).longValue(), pegaIdEmpresa(), pegaIdFilial());
			// definindo o valor do serviço, caso valor informado diferente do valor base, define o valor informado como o correto.
			if (this.valorServico.compareTo(new BigDecimal("0"))==0) {
				this.os.setValorCobrado(this.servico.getValorSugerido()); 
			}else {
				this.os.setValorCobrado(this.valorServico);
			}
			// seta o serviço na OS
			if (this.servico.getId() != null) {
				this.os.setServico(this.servico);
			}else {
				throw new HibernateException(this.translate("os.service.notFound") + this.refService);
			}
		}catch (HibernateException h) {
			this.addError(true,"hibernet.serch.error", h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal", e.getMessage());
		}
	}
	
	public void preencheProducao() {
		this.producao = this.producaoDao.pegaProducaoComGradePreenchida(false, this.producao.getId(), pegaIdEmpresa(), pegaIdFilial(), true);
		}
	
	public void doSalvarOS() {
		try {
			if (this.os.getId() != null) {
				
				Optional.ofNullable(this.prestador)
			    .ifPresentOrElse(
			        p -> this.os.setPrestador(p),
			        () -> this.addWarning(true,"os.prestador.notFound")
			    );


				this.os = this.osDao.save(this.os);
			}			
		}catch (HibernateException h) {
			this.addError(true,"hibernet.serch.error", h.getMessage());
		}catch(Exception e ) {
			this.addError(true,"exception.error.fatal", e.getMessage());
		}
		
	}
}
