package br.com.nsym.domain.model.repository.cadastro;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Secao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class SecaoRepository extends GenericRepositoryEmpDS<Secao, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Secao> listSecaoAtivo() {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null) {
			return null;
		} else {
			return criteria.list();
		}
	}

	public boolean jaExiste(String nome,Long idEmpresa) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.ilike("secao", nome),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.list().isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}

	public List<Secao> pesquisaTexto(String dep,Long idEmpresa){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
						Restrictions.ilike("secao", "%"+dep+"%"),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		return criteria.list();
	}
}
