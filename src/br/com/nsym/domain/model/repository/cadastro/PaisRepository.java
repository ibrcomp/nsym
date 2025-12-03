package br.com.nsym.domain.model.repository.cadastro;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Pais;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class PaisRepository extends GenericRepositoryEmpDS<Pais, Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Pais listaPaises(String dep) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.ilike("nome", dep),
				Restrictions.eq("isDeleted", false)));
		return (Pais)criteria.uniqueResult();		
		
	}
	
	public List<Pais> listaPaisAtivo(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false)));
		return criteria.list();	
	}


}
