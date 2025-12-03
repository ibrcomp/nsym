package br.com.nsym.domain.model.repository.cadastro;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.ControleLogon;
import br.com.nsym.domain.model.repository.GenericRepository;

@Dependent
public class ControleLogonRepository extends GenericRepository<ControleLogon, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * localiza o usuario que fez o login no controle de Logons
	 * @param usuarioLogin
	 * @return o Logon atual do usuario que fez login possibilitando verificar se está ou não logado no momento.
	 */
	public ControleLogon logon(String id){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("idReferencia", id));
		if (criteria.list() == null){
			return   null;
		}else {
			return  (ControleLogon) criteria.uniqueResult();
		}
	}
	
	public List<ControleLogon> listaControleLogon(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted",false));
		if (criteria.list() == null){
			return   null;
		}else {
			return   criteria.list();
		}
	}
	/**
	 * verifica o estado de login do usuario que fez o login
	 * @param usuarioLogin
	 * @return isLogado (true/false)
	 */
	
	public boolean logonAtivo(ControleLogon usuarioLogin){
		if (this.logon(usuarioLogin.getIdReferencia()) != null){
			return this.logon(usuarioLogin.getIdReferencia()).isLogado();
		}else{
			return false;
		}
	}

}
