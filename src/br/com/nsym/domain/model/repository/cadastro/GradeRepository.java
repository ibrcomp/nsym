package br.com.nsym.domain.model.repository.cadastro;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Grade;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class GradeRepository extends GenericRepositoryEmpDS<Grade, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Grade> listGradeAtivo() {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null) {
			return null;
		} else {
			return criteria.list();
		}
	}

	public boolean jaExiste(String nome) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.ilike("grade", nome));
		if (criteria.list().isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}
}
