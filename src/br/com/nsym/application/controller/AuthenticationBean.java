package br.com.nsym.application.controller;

import java.util.Date;

import javax.enterprise.event.Observes;
import javax.faces.application.ProjectStage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.picketlink.Identity;
import org.picketlink.Identity.AuthenticationResult;
import org.picketlink.authentication.event.LoginFailedEvent;
import org.picketlink.authentication.event.PreLoggedOutEvent;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;

import br.com.nsym.domain.model.entity.cadastro.ControleLogon;
import br.com.nsym.domain.model.repository.cadastro.ControleLogonRepository;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean que controla a autenticacao no sistema, por ele invocamos o gerenciador
 * de autenticacao para que o usuario possa realizar acesso ao sistema
 *
 * @author Ibrahim Yousef quatani
 *
 * @version 2.0.0
 * @since 1.0.0, 25/10/2016
 */
@Named
@ViewScoped
public class AuthenticationBean extends AbstractBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Identity identity;

	@Inject
	private IdentityManager identityManager;


	@Getter
	private boolean authenticationError;


	@Inject
	private ControleLogonRepository controleLogonDao;

	@Getter
	@Setter
	private ControleLogon usuarioLogado = new ControleLogon();

	@Getter
	@Setter
	private User acesso = new User();




	/**
	 * Iniciliazacao da pagina da login onde checamos pela existencia de uma 
	 * sessao valida
	 * 
	 * @return a dashboard do sistema
	 */
	public String initialize() {

		// validamos se nao existe uma sessao ativa
		if (this.identity.isLoggedIn()) {
			this.identity.logout();
			return "/index.xhtml?faces-redirect=true";

		}
		// permanecemos na pagina
		return null;

	}

	/**
	 * Realiza o login, se houver erro redireciona para a home novamente e 
	 * impede que prossiga
	 *
	 * @return a home autenticada ou a home de login caso acesso negado
	 */
	@Transactional
	public String doLogin() {

		final AuthenticationResult result = this.identity.login();

		if ((result == AuthenticationResult.SUCCESS) && (!this.isLogado(this.identity.getAccount().getId()))) {
			System.out.println(this.identity.getAccount().getId());
			this.insereLogado();
			return "/main/dashboard.xhtml?faces-redirect=true";
		}else{
			this.identity.logout();
			return null;
		}
	}

	/**
	 * Realiza logout do sistema
	 * 
	 * @return a home para login
	 */
	public String doLogout() {
		this.identity.logout();
		return "/index.xhtml?faces-redirect=true";
	}
	/**
	 * Evento disparado antes que um usuário deslogue
	 */
	@Transactional
	protected void preSaida(@Observes PreLoggedOutEvent event){
		System.out.println("estou liberando o usuario antes de concluir o logout!");
		ControleLogon logado = new ControleLogon();
		if (this.controleLogonDao.logon(event.getAccount().getId()) != null){
			logado = this.controleLogonDao.logon(event.getAccount().getId());
			System.out.println(logado.getNome() + " foi liberado");
			logado.setLogado(false);
			this.controleLogonDao.save(logado);
			
		}
	}


	/**
	 * Trata erros de autenticacao
	 * 
	 * @param event o evento de autenticacao
	 */
	protected void handleUnsuccesfulLogin(@Observes LoginFailedEvent event) {

		this.addError(true, "error.invalid-credentials");
	}

	/**
	 * @return verifica se a aplicacao esta em teste ou se estamos em outro 
	 * ambiente
	 */
	public boolean isSystemTest() {
		return ApplicationUtils.isStageRunning(ProjectStage.SystemTest);
	}

	/**
	 * 
	 * Inicio do modulo controle Logon
	 * 
	 */

	public boolean isLogado(String id){
		ControleLogon localiza = new ControleLogon();
		localiza = this.controleLogonDao.logon(id);
		if (localiza == null){
			return false;
		}else{
			return localiza.isLogado();
		}
	}


	@Transactional
	public void liberaAcesso() { 



			if (this.controleLogonDao.logon(this.identity.getAccount().getId()) != null){
				this.usuarioLogado = this.controleLogonDao.logon(this.identity.getAccount().getId());
				this.usuarioLogado.setLogado(false);
				this.controleLogonDao.save(this.usuarioLogado);
				System.out.println("estou no libera Acesso");
			}
			System.out.println("estou no libera Acesso");
	}
	@Transactional
	public void bloqueiaAcesso(){
		if (this.controleLogonDao.logon(this.identity.getAccount().getId()) != null){
			this.usuarioLogado = this.controleLogonDao.logon(this.identity.getAccount().getId());
			this.usuarioLogado.setLogado(true);
			this.controleLogonDao.save(this.usuarioLogado);
		}
		System.out.println("estou no bloqueio de usuario logado!");
	}

	@Transactional
	public void liberaInativo(){
		if (this.controleLogonDao.logon(this.identity.getAccount().getId()) != null){
			this.usuarioLogado = this.controleLogonDao.logon(this.identity.getAccount().getId());
			this.usuarioLogado.setLogado(false);
			System.out.println(this.usuarioLogado.getNome() + " acaba de ser liberado");
			this.controleLogonDao.save(this.usuarioLogado);
		}
	}

	@Transactional
	public void insereLogado(){
		this.acesso = this.recuperaUsuario(this.identity.getAccount().getId());
		if (this.controleLogonDao.logon(this.identity.getAccount().getId()) == null){
			this.usuarioLogado.setIdReferencia(this.acesso.getId());
			System.out.println(this.usuarioLogado.getIdReferencia());
			this.usuarioLogado.setNome(this.acesso.getUsername());
			System.out.println(this.usuarioLogado.getNome());
			this.usuarioLogado.setDeleted(false);
			System.out.println(this.usuarioLogado.isDeleted());
			this.usuarioLogado.setInclusion(new Date());
			System.out.println(this.usuarioLogado.getInclusionDateAsString());
			this.usuarioLogado.setLogado(true);
			System.out.println(this.usuarioLogado.isLogado());
			this.controleLogonDao.save(usuarioLogado);
			System.out.println("deu erro?");
		}else{
			this.usuarioLogado = this.controleLogonDao.logon(this.identity.getAccount().getId());
			this.usuarioLogado.setLogado(true);
			this.controleLogonDao.save(usuarioLogado);
		}
	}

	public User recuperaUsuario(String id){
		IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
		IdentityQuery<User> query = queryBuilder.createIdentityQuery(User.class);
		query.where(queryBuilder.equal(User.ID, id));

		if (query.getResultList().size() == 1){
			System.out.println("localizado usuario " + query.getResultList().get(0).getId());
			return query.getResultList().get(0);
		}else{
			return null;
		}

	}
}
