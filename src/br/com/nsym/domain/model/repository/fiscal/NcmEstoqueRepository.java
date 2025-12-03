package br.com.nsym.domain.model.repository.fiscal;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.NcmEstoque;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NcmEstoqueRepository extends GenericRepositoryEmpDS<NcmEstoque, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NcmEstoque pegaNcmComEstoque(Ncm ncm, Long emp , Long fil){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("ncm", ncm),
				Restrictions.eq("isDeleted", false)));
		if (emp == null){
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
		}else{
			if (fil != null){
				criteria.add(Restrictions.and(Restrictions.eqOrIsNull("idEmpresa", emp),
						Restrictions.eqOrIsNull("idFilial", fil)));
			}else{
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
			}
		}
		criteria.setFetchMode("ncm", FetchMode.JOIN);
		if (criteria.list().size() > 1 ){
			return null;
		}else{
			return (NcmEstoque) criteria.uniqueResult();
		}
	}

}
