package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fiscal.nfe.ItemNfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeRecebida;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ItemNfeRepository extends GenericRepositoryEmpDS<ItemNfe, Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 640762121490991075L;
	
	
	public List<ItemNfe> listaItensPorNfe(Nfe nfe){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ItemNfe> criteria = builder.createQuery(ItemNfe.class);

		Root<ItemNfe> formPedido = criteria.from(ItemNfe.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("nfe"),nfe.getId()));

		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ItemNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		
		
		if (typedQuery.getResultList().size() > 0){
			return typedQuery.getResultList();
		}else {
			return null;
		}
		
	}
	public List<ItemNfe> listaItensPorNfeRecebida(NfeRecebida nfe){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ItemNfe> criteria = builder.createQuery(ItemNfe.class);

		Root<ItemNfe> formPedido = criteria.from(ItemNfe.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("nfeRecebida"),nfe.getId()));
		conditions.add(builder.equal(formPedido.get("isDeleted"),false ));

		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ItemNfe> typedQuery = this.getEntityManager().createQuery(criteria);
		
		
		if (typedQuery.getResultList().size() > 0){
			return typedQuery.getResultList();
		}else {
			return null;
		}
		
	}
}
