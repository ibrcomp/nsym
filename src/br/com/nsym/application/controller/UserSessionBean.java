package br.com.nsym.application.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.omnifaces.util.Faces;
import org.picketlink.Identity;
import org.picketlink.authentication.event.LoggedInEvent;
import org.picketlink.authentication.event.PostLoggedOutEvent;

import br.com.nsym.application.producer.qualifier.AuthenticatedUser;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.repository.cadastro.ControleLogonRepository;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FilialRepository;
import br.com.nsym.domain.model.repository.tools.ConfigurationRepository;
import br.com.nsym.domain.model.security.Authorization;
import br.com.nsym.domain.model.security.Group;
import br.com.nsym.domain.model.security.User;
import br.com.nsym.domain.model.service.AccountService;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean utlizado pelo sistema para requisitar as authorities disponiveis no
 * sistemas
 *
 * @author Ibrahim Yousef
 *
 * @version 1.1.0
 * @since 2.0.0, 27/06/2015
 */
@Named
@SessionScoped
public class UserSessionBean implements Serializable {

	private List<Group> userGroups;

	@Getter
	@Setter
	private Empresa empresaADM = new Empresa();
	
	@Getter
	@Setter
	private Filial filial = new Filial();
	
	@Inject
	private FilialRepository filialDao;

	@Inject
	private EmpresaRepository empresaDao;
	
	@Inject
	private ControleLogonRepository controleLogonDao;

	@Getter
	private List<Empresa> empresasADM = new ArrayList<>();
	
	@Inject
	private ConfigurationRepository configDao;

	@Getter
	@Inject
	private transient Authorization authorization;

	@Inject
	private  transient Identity identity;

	@Getter
	@Setter
	private int numeroUsuariosLogado = 1;

	@Inject
	private transient AccountService accountService;


	/**
	 * Inicializamos a sessao do usuario carregando os grupos dele e suas roles
	 *
	 * @param event o evento de login
	 */
	protected void initialize(@Observes LoggedInEvent event) {
		this.userGroups = this.accountService
				.listUserGroupsAndGrants(this.getAuthenticatedUser());
	}

	/**
	 * Destruimos a sessao, forcando que em um proximo login os grupos sejam
	 * carregados novamente
	 *
	 * @param event o evento de logout
	 */
	@Transactional
	protected void destroy(@Observes PostLoggedOutEvent event) {
		
		this.userGroups = null;
		System.out.println("estou o evento de logout");
	}

	/**
	 * Checa pela role de um respectivo usuario
	 *
	 * @param roleName a role que espera-se que este usuario tenha
	 * @return se existe ou nao uma instancia desta role atribuida a ele
	 */
	public boolean hasRole(String roleName) {

		boolean hasRole = false;

		for (Group group : this.userGroups) {
			hasRole = this.hasGrantTo(roleName, group);
		}

		return hasRole;
	}

	public List<Empresa> setEmpresasADM(){
		this.empresasADM = this.empresaDao.listEmpresaAtiva();
		return this.empresasADM;
	}

	/**
	 * Buscamos nos grupos do usuario que fez login se em algum deles existe um
	 * grant para a role desejada
	 *
	 * @param role a role que esperamos que o usuario tenha
	 * @param group o grupo que pretendemos checar pela role
	 * @return se ha ou nao a o grant para aquela role em algum dos grupos
	 */
	private boolean hasGrantTo(String role, Group group) {

		// se for um grupo parente, os grants vem vazio, entao preenchemos
		if (group.getGrants() == null) {
			group.setGrants(this.accountService.listGrantsByGroup(group));
		}

		// agora iteramos nos grupos
		if (!group.getGrants().isEmpty()) {

			if (group.getGrants().stream().anyMatch((grant)
					-> (grant.getRole().getAuthorization().equals(role)))) {
				return true;
			}
		}

		// se nao tem acesso em primeira instancia, checamos pelos outros grupos
		// aninhados dentro daquele grupo
		if (group.getParent() != null) {
			return this.hasGrantTo(role, group.getParent());
		} else {
			return false;
		}
	}

	/**
	 * @return o modelo UI que nosso usuario vai usar
	 */
	public String getAuthenticatedUserUI() {
		final User user = this.getAuthenticatedUser();
		return user.getTheme() + " " + user.getMenuLayout();
	}

	/**
	 * @return o nome do usuario logado atualmente no sistema
	 */
	public String getAuthenticatedUserName() {
		return this.getAuthenticatedUser().getName();
	}

	public void ativaEmpresa(){
		if (this.empresaADM != null){
			this.getAuthenticatedUser().setIdEmpresa(this.empresaADM.getId());
		}else{
			System.out.println("erro empresaADM nulo");
		}
	}

	/**
	 * 
	 * @return o ID da Empresa ao qual o usuário logado pertence.
	 */
	public Long getAuthenticatedUserIDEmpresa(){
		return this.getAuthenticatedUser().getIdEmpresa();
	}

	/**
	 * 
	 * @return o ID da Filial ao qual o usuário logado pertence
	 */
	public Long getAuthenticatedUserIDFilial(){
			return this.getAuthenticatedUser().getIdFilial();
	}
	
	/**
	 * 
	 * @return a Razao Social da empresa logada
	 */
	public String getEmpAuthenticated() {
		if (this.getAuthenticatedUserIDFilial() != null) {
			return this.filialDao.findById(getAuthenticatedUserIDFilial(), false).getRazaoSocial();
		}else {
			if (this.getAuthenticatedUserIDEmpresa()!= null) {
				return this.empresaDao.findById(getAuthenticatedUserIDEmpresa(), false).getRazaoSocial();
			}else {
				return "Administrador";
			}
		}
	}

	/**
	 * @return o email do usuario logado
	 */
	public String getAuthenticatedUserEmail() {
		return this.getAuthenticatedUser().getEmail();
	}

	/**
	 * @return o grupo ao qual este usuario esta vinculado
	 */
	public String getAuthenticatedUserGroup() {
		return this.userGroups.stream().findAny().get().getName();
	}

	/**
	 * Armazena um valor na sessao para o usuario logado
	 *
	 * @param key a chave para o valor
	 * @param value o valor
	 */
	public void setOnUserSession(String key, Object value) {
		Faces.setSessionAttribute(this.generateKeyForUser(key), value);
	}

	/**
	 * Captura um valor da sessao do usuario logado
	 *
	 * @param <T> o tipo do objeto
	 * @param key a chave para busca
	 * @return o valor previamente setado para este usuario
	 */
	public <T> T getFromUserSession(String key) {
		return Faces.getSessionAttribute(this.generateKeyForUser(key));
	}

	/**
	 * Remove um valor da sessao do usuario logado
	 *
	 * @param key a chave do valor
	 */
	public void removeFromUserSession(String key) {
		Faces.removeSessionAttribute(this.generateKeyForUser(key));
	}

	/**
	 * Gera uma chave para este atributo na sessao
	 *
	 * @param valueKey a chave para apendar e formar uma chave unica
	 * @return a chave para este usuario
	 */
	private String generateKeyForUser(String valueKey) {
		return this.getAuthenticatedUserName() + ":" + valueKey;
	}

	/**
	 * @return o usuario autenticado
	 */
	@Produces
	@RequestScoped
	@AuthenticatedUser
	protected User getAuthenticatedUser() {
		return  (User) this.identity.getAccount();
	}


}
