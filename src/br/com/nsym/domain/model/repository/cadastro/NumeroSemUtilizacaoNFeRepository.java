package br.com.nsym.domain.model.repository.cadastro;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.controller.nfe.tools.NumeroSemUtilizacaoNFe;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NumeroSemUtilizacaoNFeRepository extends GenericRepositoryEmpDS<NumeroSemUtilizacaoNFe, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<NumeroSemUtilizacaoNFe> listaNumeroSemUtilizacao(Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();
	}
	
	public List<NumeroSemUtilizacaoNFe> retornaListaDeNumeroNFeDisponivel(Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("bloqueado", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();
	}
	
	public NumeroSemUtilizacaoNFe retornaNumero(Long numero , Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("numeroLivre", numero),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		return (NumeroSemUtilizacaoNFe) criteria.uniqueResult();
		
	}
}

