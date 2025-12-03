package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fiscal.nfe.Lacre;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class LacreRepository extends GenericRepositoryEmpDS<Lacre, Long>{


	/**
	 *
	 */
	private static final long serialVersionUID = -9094511079857874569L;

	public List<Lacre> findLacreForNfe(Nfe nfe,Long empresa,Long filial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Lacre> criteria = builder.createQuery(Lacre.class);
		

		Root<Lacre> formPedido = criteria.from(Lacre.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("nfe"),nfe.getId());

		conditions.add(itemPedido);
		if (empresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (filial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),empresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),empresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),filial));
			}
		}


	//	criteria.select(builder.sum(formPedido.get("valorTotalPedido").as(BigDecimal.class)));
		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Lacre> typedQuery = this.getEntityManager().createQuery(criteria);
		
			return typedQuery.getResultList();
		
		
	}

}
