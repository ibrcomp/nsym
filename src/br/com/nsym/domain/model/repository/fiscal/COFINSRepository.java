package br.com.nsym.domain.model.repository.fiscal;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.COFINS;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class COFINSRepository extends GenericRepositoryEmpDS<COFINS, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<COFINS> listTributosAtivo() {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null) {
			return null;
		} else {
			return criteria.list();
		}
	}


}
