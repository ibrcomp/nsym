package br.com.nsym.application.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;

import br.com.nsym.domain.model.entity.cadastro.ControleLogon;
import br.com.nsym.domain.model.repository.cadastro.ControleLogonRepository;
import br.com.nsym.domain.model.security.User;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ControleAcessoBean extends AbstractBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2742663867959215001L;


	@Inject
	private IdentityManager identityManager;

	@Inject
	private Identity identity;



	@Getter
	@Setter
	private User acesso = new User();


	@Getter
	@Setter
	private ControleLogon usuarioLogado = new ControleLogon();

	@Inject
	private ControleLogonRepository controleLogonDao;

	@Getter
	@Setter
	private List<ControleLogon> listaAcesso = new ArrayList<>();	

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
	public void liberaAcesso(){
		this.usuarioLogado = this.controleLogonDao.logon(this.identity.getAccount().getId());
		this.usuarioLogado.setLogado(false);
		this.controleLogonDao.save(this.usuarioLogado);
	}

	@Transactional
	public void insereLogado(String id){
		this.usuarioLogado = new ControleLogon();
		this.acesso = this.recuperaUsuario(id);
		if (this.controleLogonDao.logon(id) == null){
			this.usuarioLogado.setIdReferencia(this.acesso.getId());
			this.usuarioLogado.setNome(this.acesso.getName());
			this.usuarioLogado.setDeleted(false);
			this.usuarioLogado.setDataLogin(new Date());
			this.usuarioLogado.setLogado(true);
			this.controleLogonDao.save(this.usuarioLogado);
		}else{
			this.usuarioLogado.setLogado(true);
			this.controleLogonDao.save(this.usuarioLogado);
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



