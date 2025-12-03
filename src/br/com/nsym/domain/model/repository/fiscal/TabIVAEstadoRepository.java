package br.com.nsym.domain.model.repository.fiscal;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.TabIVAEstado;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class TabIVAEstadoRepository extends GenericRepositoryEmpDS<TabIVAEstado, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<TabIVAEstado> listaIvaPorNcm(Ncm idNcm,Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("ncm", idNcm),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		return criteria.list();	
	}

	public TabIVAEstado pegarIvaPorNcmEstado(Ncm idNcm, Uf uf,Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("ncm", idNcm),
				Restrictions.eq("uf", uf),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.uniqueResult() == null){
			return new TabIVAEstado();
		}else{
			return (TabIVAEstado) criteria.uniqueResult();
		}
	}

	public boolean jaExiste(Ncm idNcm,Uf uf, Long idEmpresa){
		final Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("ncm", idNcm),
				Restrictions.eq("uf", uf),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.list().isEmpty()){
			System.out.println("retornou false");
			return  false;
		}else{
			System.out.println("retornou true");
			return true;
		}
	}
}
