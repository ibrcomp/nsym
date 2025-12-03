package br.com.nsym.domain.model.repository.fiscal.sat;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.entity.fiscal.Cfe.ItemCFe;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ItemCFeRepository extends GenericRepositoryEmpDS<ItemCFe, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<ItemCFe> listaDeItensPorCFe(CFe cfe,Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("cfe", cfe),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();	
	}

}
