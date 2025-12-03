package br.com.nsym.infraestrutura.picketlink;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.slf4j.Logger;

/**
 * O autenticador do sistema, por ele realizamos o processo de autenticacao de 
 * um usuario atraves de suas credenciais informadas na tela de login
 *
 * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
@Named
@PicketLink
@ApplicationScoped
public class Authenticator extends BaseAuthenticator {

	@Inject
	private Logger logger;

	@Inject
	private CustomCredentials wbCredentials;

	@Inject
	private IdentityManager identityManager;

//	@Inject
//	private ControleLogonRepository logonDao;
	
//	@Getter
//	@Setter
//	private User acesso;

//	@Getter
//	@Setter
//	private ControleLogon usuarioLogado;


	/**
	 * Autentica o usuario no banco de dados pelo modelo de seguranca
	 */
	@Override
	public void authenticate() {

		UsernamePasswordCredentials userCredentials = 
				new UsernamePasswordCredentials(this.wbCredentials.getUsername(),
						new Password(this.wbCredentials.getPassword()));


		try {
			this.identityManager.validateCredentials(userCredentials);
			this.defineStatus(userCredentials.getStatus());

			if (this.getStatus() == AuthenticationStatus.SUCCESS){
				this.setAccount(userCredentials.getValidatedAccount());
			}

		} catch (Exception ex) {
			this.setStatus(AuthenticationStatus.FAILURE);
			logger.error("Error in an attempt to authenticate {}", 
					this.wbCredentials.getUsername(), ex);
		}
	}
//	public void addUsuarioLogado(String id){
//		
//		this.insereLogado(id);
//
//	}
//	
//	@Transactional
//	public void insereLogado(String id){
//		this.usuarioLogado = new ControleLogon();
//		this.acesso = this.recuperaUsuario(id);
//		if (this.logonDao.logon(id) == null){
//			this.usuarioLogado.setIdReferencia(this.acesso.getId());
//			this.usuarioLogado.setNome(this.acesso.getName());
//			this.usuarioLogado.setDeleted(false);
//			this.usuarioLogado.setDataLogin(new Date());
//			this.usuarioLogado.setLogado(true);
//			this.logonDao.save(this.usuarioLogado);
//		}else{
//			this.usuarioLogado.setLogado(true);
//			this.logonDao.save(this.usuarioLogado);
//		}
//	}
	
//	public User recuperaUsuario(String id){
//		IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
//		IdentityQuery<User> query = queryBuilder.createIdentityQuery(User.class);
//		query.where(queryBuilder.equal(User.ID, id));
//
//		if (query.getResultList().size() == 1){
//			System.out.println("localizado usuario " + query.getResultList().get(0).getId());
//			return query.getResultList().get(0);
//		}else{
//			return null;
//		}
//
//	}


	/**
	 * Define no contexto de segurancao pelo autenticador qual o status da 
	 * autenticacao do usuario
	 * 
	 * @param status o status a ser checado para a autenticacao
	 */
	private void defineStatus(Status status) {

		switch (status) {
		case ACCOUNT_DISABLED:
			this.setStatus(AuthenticationStatus.DEFERRED);
			break;
		case EXPIRED:
			this.setStatus(AuthenticationStatus.DEFERRED);
			break;
		case VALID:
			this.setStatus(AuthenticationStatus.SUCCESS);
			break;
		default:
			this.setStatus(AuthenticationStatus.FAILURE);
			break;
		}
	}

	@Override
	public void postAuthenticate() {
//		this.addUsuarioLogado(this.getAccount().getId());
		
	}
}


