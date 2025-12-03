package br.com.nsym.domain.model.repository.fiscal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fiscal.nfe.DI;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

public class DIRepository extends GenericRepositoryEmpDS<DI, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DI pegaDI(Long id, Long idEmpresa, Long idFilial) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("id", id),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		criteria.setFetchMode("listaAdicao", FetchMode.JOIN);


		return (DI) criteria.uniqueResult();
	}
	
	public DI pegaDICriteria(boolean isDeleted,String id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<DI> entityGraph = this.getEntityManager().createEntityGraph(DI.class);
		entityGraph.addAttributeNodes("listaAdicao");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<DI> criteria = builder.createQuery(DI.class);

		Root<DI> fromModelo = criteria.from(DI.class);
		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromModelo.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromModelo.get("idEmpresa"));

		conditions.add(builder.equal(fromModelo.get("isDeleted"), isDeleted));
		conditions.add(builder.equal(fromModelo.get("nnDi"),id));	
		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(builder.equal(fromModelo.get("idEmpresa"), idEmpresa));
					conditions.add(filialNull);
				}else {
					conditions.add(builder.equal(fromModelo.get("idEmpresa"), idEmpresa));
				}
			}
		}else{
			if (porFilial) {
				conditions.add(builder.equal(fromModelo.get("idEmpresa"), idEmpresa));
				conditions.add(builder.equal(fromModelo.get("idFilial"), idFilial));
			}else {
				conditions.add(builder.equal(fromModelo.get("idEmpresa"), idEmpresa));
			}
		}
		
		criteria.select(fromModelo.alias("parc"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<DI> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return typedQuery.getSingleResult() ;
		}catch(NoResultException nr) {
			return null;
		}
	}

}
