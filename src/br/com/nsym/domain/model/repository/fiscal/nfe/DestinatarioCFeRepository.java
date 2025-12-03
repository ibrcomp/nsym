package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.Cfe.DestinatarioCFe;
import br.com.nsym.domain.model.entity.fiscal.nfe.Destinatario;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class DestinatarioCFeRepository extends GenericRepositoryEmpDS<DestinatarioCFe, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Destinatario> listDestinatarioAtivo(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}
	
	
	
	
}
