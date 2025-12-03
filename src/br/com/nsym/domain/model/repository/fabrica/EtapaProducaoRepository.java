package br.com.nsym.domain.model.repository.fabrica;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fabrica.util.EtapaProducao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class EtapaProducaoRepository extends GenericRepositoryEmpDS<EtapaProducao, Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = -951137017166267332L;
	
	public boolean existeEtapa(Long idEmpresa,Long idFilial, String descricao){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<EtapaProducao> criteria = builder.createQuery(EtapaProducao.class);
		

		Root<EtapaProducao> formEtapa = criteria.from(EtapaProducao.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate etapaDescricao = builder.equal(formEtapa.get("descricao"),descricao);

		conditions.add(etapaDescricao);
		conditions.add(builder.equal(formEtapa.get("isDeleted"),false));
		if (idEmpresa == null){
			conditions.add(builder.isNull(formEtapa.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formEtapa.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formEtapa.get("idFilial")));
			}else{
				conditions.add(builder.equal(formEtapa.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formEtapa.get("idFilial"),idFilial));
			}
		}
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<EtapaProducao> typedQuery = this.getEntityManager().createQuery(criteria);
		try {
			if (typedQuery.getSingleResult() != null ) {
				return true;
			}else {
				return false;
			}
		}catch (NoResultException n){
			return false;
		}catch (NonUniqueResultException nu) {
			throw new NonUniqueResultException("hibernate.multipleResults");			
		}
		
	}

}
