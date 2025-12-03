package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.nfe.Inutilizacao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class InutilizacaoRepository extends GenericRepositoryEmpDS<Inutilizacao, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<Inutilizacao> listNfeInutilizada(Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
			return  criteria.list();
	}
}
