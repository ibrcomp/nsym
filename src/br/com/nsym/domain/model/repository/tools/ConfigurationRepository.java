package br.com.nsym.domain.model.repository.tools;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.tools.Configuration;
import br.com.nsym.domain.model.repository.GenericRepository;

@Dependent
public class ConfigurationRepository extends GenericRepository<Configuration, Long> implements IConfigurationRepository {


	/**
	 * 
	 */
	private static final long serialVersionUID = -685305580407465761L;

	@Override
	public Configuration findDefault(Long id) {
		
		final CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Configuration> criteria = builder.createQuery(this.getPersistentClass());
		
		Root<Configuration> fromProdutos = criteria.from(Configuration.class);
		fromProdutos.fetch("user",JoinType.LEFT);
		
		List<Predicate> conditions = new ArrayList<>();
			conditions.add(builder.equal(fromProdutos.get("id"), id));
		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Configuration> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		
		return (Configuration) typedQuery.getSingleResult();
		
	}


}
