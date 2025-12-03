package br.com.nsym.domain.model.repository.cadastro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class FoneRepository extends GenericRepositoryEmpDS<Fone, Long> {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 386430166550015399L;
	/**
	 * 
	 * @param isBlocked
	 * @param pageRequest
	 * @return 
	 */
	public Page<Fone> odlistByStatus(Boolean isBlocked, PageRequest pageRequest) {

		final Criteria criteria = this.createCriteria();

		if (isBlocked != null) {
			criteria.add(Restrictions.eq("blocked", isBlocked));
		}

		// projetamos para pegar o total de paginas possiveis
		criteria.setProjection(Projections.count("id"));

		final Long totalRows = (Long) criteria.uniqueResult();

		// limpamos a projection para que a criteria seja reusada
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// paginamos
		criteria.setFirstResult(pageRequest.getFirstResult());
		criteria.setMaxResults(pageRequest.getPageSize());

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.addOrder(Order.asc(pageRequest.getSortField()));
		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.addOrder(Order.desc(pageRequest.getSortField()));
		}

		// montamos o resultado paginado
		return new Page<>(criteria.list(), totalRows);
	}
	
	public List<Fone> listaFonePorContato(Contato contato){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Fone> criteria = builder.createQuery(getPersistentClass());

		Root<Fone> fromProdutos = criteria.from(getPersistentClass());

		
		List<Predicate> conditions = new ArrayList<>();
			
		conditions.add(builder.equal(fromProdutos.get("contato"), contato));
		
		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Fone> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
}
	
//	public List<Fone> odllistaFonePorContato(Contato contato){
//		
//		final Criteria criteria = this.createCriteria();
//
//		
//		criteria.add(Restrictions.eq("contato", contato));
//		
//		return criteria.list();
//		
//	}
	public Fone update (Fone emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}
}
