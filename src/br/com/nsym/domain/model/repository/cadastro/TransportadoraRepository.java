package br.com.nsym.domain.model.repository.cadastro;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class TransportadoraRepository extends GenericRepositoryEmpDS<Transportadora, Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Transportadora> listTransportadoraAtiva(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}
	public List<Transportadora> pesquisaTexto(String dep,Long idEmpresa){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
						Restrictions.ilike("razaoSocial", "%"+dep+"%"),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		return criteria.list();
	}
	
}
