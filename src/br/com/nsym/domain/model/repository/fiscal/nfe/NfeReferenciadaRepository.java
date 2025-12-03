package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeReferenciada;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NfeReferenciadaRepository extends GenericRepositoryEmpDS<NfeReferenciada, Long> {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 2775880715470087474L;

	public List<NfeReferenciada> listaNotasReferenciadas(Nfe nota, Long idEmpresa , Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<NfeReferenciada> criteria = builder.createQuery(NfeReferenciada.class);
		

		Root<NfeReferenciada> formPedido = criteria.from(NfeReferenciada.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("nfe"),nota.getId());

		conditions.add(itemPedido);
		if (idEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),idFilial));
			}
		}


		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<NfeReferenciada> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getResultList();
		
		
	}

}
