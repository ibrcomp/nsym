package br.com.nsym.domain.model.repository.fiscal;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.nfe.Adicao;
import br.com.nsym.domain.model.entity.fiscal.nfe.DI;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class AdicaoRepository extends GenericRepositoryEmpDS<Adicao, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Adicao> pegaListaPorDI(DI di) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
					Restrictions.eq("isDeleted", false),
					Restrictions.eq("di", di)));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}

}
