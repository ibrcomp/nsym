package br.com.nsym.domain.model.repository.fabrica;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fabrica.Risco;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class RiscoRepository extends GenericRepositoryEmpDS<Risco,Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = 6070488696919618844L;
	
	public Risco pegaRiscoComGradePreenchida(boolean isDeleted,Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Risco> entityGraph = this.getEntityManager().createEntityGraph(Risco.class);
		entityGraph.addAttributeNodes("grade");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Risco> criteria = builder.createQuery(Risco.class);

		Root<Risco> fromModelo = criteria.from(Risco.class);
		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromModelo.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromModelo.get("idEmpresa"));

		conditions.add(builder.equal(fromModelo.get("isDeleted"), isDeleted));
		conditions.add(builder.equal(fromModelo.get("id"),id));	
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
		TypedQuery<Risco> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		try {
			return  typedQuery.getSingleResult();
		}catch(NoResultException nr) {
			return null;
		}
	}

}
