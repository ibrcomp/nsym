package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfeRecebida;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeRecebida;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ItemNfeRecebidaRepository extends GenericRepositoryEmpDS<ItemNfeRecebida, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<ItemNfeRecebida> listItensNfeAtivo(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}
	
	public List<ItemNfeRecebida> listaItensPorNfeRecebida(NfeRecebida nfe){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
					Restrictions.eq("isDeleted", false),
					Restrictions.eq("nfeRecebida", nfe)));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}
}
