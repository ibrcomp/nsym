package br.com.nsym.application.controller.tools;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import br.com.nsym.application.channels.DadosDeConexaoSocket;
import br.com.nsym.application.controller.AbstractBean;
import br.com.nsym.domain.misc.ImpressoraACBr;
import br.com.nsym.domain.misc.ModeloImpressoraAcbr;
import br.com.nsym.domain.misc.ex.InternalServiceError;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.entity.venda.Transacao;
import br.com.nsym.domain.model.repository.tools.ConfigurationRepository;
import br.com.nsym.domain.model.repository.venda.TransacaoRepository;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.domain.model.service.AccountService;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class RegraNegocioBean extends AbstractBean  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5920100803249983646L;
	
	@Getter
	@Setter
	private Configuration config;
	
	@Inject
	private ConfigurationRepository configDao;
	
	@Getter
	@Setter
	@Inject
	private User user;
	
	@Inject
	private AccountService accountService;
	
	@Inject
	private ImpressoraACBr impressora;
	
	@Getter
	@Setter
	private List<User> listaUsarioEmp = new ArrayList<>();
	
	@Getter
	@Setter
	private List<String> listaimpressoraSpooler = new ArrayList<>();
	
	@Getter
	@Setter
	private List<Transacao> listaDeTransacao = new ArrayList<Transacao>();
	
	@Inject
	private TransacaoRepository transacaoDao;
	
	@Getter
	@Setter
	private Transacao transacao;
	
	
	@PostConstruct
	public void init(){
		this.listaUsarioEmp = accountService.listUsersByEmp(getUsuarioAutenticado().getIdEmpresa(), null);
//		this.listaimpressoraSpooler = impressora.listaImpressorasSpooler(pegaConexao());
	}

	public void initializeListing() {
		// TODO Auto-generated method stub
		this.viewState = ViewState.LISTING;
	}

	public void initializeForm(String configId) throws IOException {
		// TODO Auto-generated method stub
		this.listaDeTransacao = transacaoDao.listaTodasAsTransacoesPorFilial(false,null,getUsuarioAutenticado().getIdEmpresa(), getUsuarioAutenticado().getIdFilial());
		if (configId == null) {
			this.viewState = ViewState.ADDING;
			this.transacao =  new Transacao();
			this.config = new Configuration();
		}else {
			this.viewState = ViewState.EDITING;
			this.user = this.accountService.findUserById(configId);
			if (this.user.getConfig() != null) {
				this.config = this.configDao.findById(this.user.getConfig().getId(), false);
				if (this.config.getTransacaoPadrao() != null) {
					this.setTransacao( this.transacaoDao.pegaTransacaoPorId(this.user.getConfig().getTransacaoPadrao()));
				}
			
				if (this.config.getPortaACBR() != null && this.config.getIpACBR() != null) {
					this.listaimpressoraSpooler = impressora.listaImpressorasSpooler(pegaConexao());
				}
			}else {
				this.config = new Configuration();
				this.transacao = new Transacao();
			}
		}
		
	}
	
	/**
	 * Pega as informa��es da empresa / filial para conexao com acbr
	 * @return a conexao preenchida
	 */
	public  DadosDeConexaoSocket pegaConexao(){
		DadosDeConexaoSocket conexao; 
		conexao = new DadosDeConexaoSocket(this.meuIP().trim(),new BigDecimal(this.config.getPortaACBR()).intValue());
		return conexao;
	}
	
	 /**
	  * @param userId
	  * @return
	  */
	 public String changeToEdit(String configId) {
		 return "formCadRegraNegocio.xhtml?faces-redirect=true&configId=" + configId;
	 }
	 
	 public String toLisUsersConfig() {
		 return "listUsersConfig.xhtml?faces-redirect=true";
	 }
	 
	 @Transactional
	 public void doSave() {
		 try {
			 if (this.config.getId() == null) {
				 this.config.setTransacaoPadrao(this.transacao.getId());
				 this.user.setConfig(this.config);
				 this.accountService.update(this.user);
				 this.addInfo(true, "adm.user.save",user.getName());
			 }else {
				 this.config.setTransacaoPadrao(this.transacao.getId());
				 this.configDao.save(config);
				 this.addInfo(true, "adm.user.save",user.getName());
			 }
		 } catch (InternalServiceError ex) {
			 this.addError(true, ex.getMessage(), ex.getParameters());
		 } catch (Exception ex) {
			 this.addError(true, "error.undefined-error", ex.getMessage());
		 }
	 }
	 
	 public ModeloImpressoraAcbr[] getListaImpressoras(){
			return ModeloImpressoraAcbr.values();
		}

}
