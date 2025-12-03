package br.com.nsym.domain.model.repository.fabrica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fabrica.FichaTecnica;
import br.com.nsym.domain.model.entity.fabrica.ItemFichaTecnica;
import br.com.nsym.domain.model.entity.fabrica.util.EtapaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.SequenciaLinhaProducao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class FichaTecnicaRepository extends GenericRepositoryEmpDS<FichaTecnica,Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = 824596947263995877L;
	
	public FichaTecnica pegaFichaTecnica(Long idEmpresa,Long idFilial, Long idFicha){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FichaTecnica> criteria = builder.createQuery(FichaTecnica.class);
		

		Root<FichaTecnica> formPedido = criteria.from(FichaTecnica.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("id"),idFicha);

		conditions.add(itemPedido);
		conditions.add(builder.equal(formPedido.get("isDeleted"),false));
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
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<FichaTecnica> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getSingleResult();
		
	}
	
	public List<ItemFichaTecnica> pegaItensFicha(boolean isDeleted,Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<FichaTecnica> entityGraph = this.getEntityManager().createEntityGraph(FichaTecnica.class);
		entityGraph.addAttributeNodes("itens");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FichaTecnica> criteria = builder.createQuery(FichaTecnica.class);

		Root<FichaTecnica> fromModelo = criteria.from(FichaTecnica.class);
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
		TypedQuery<FichaTecnica> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return typedQuery.getSingleResult().getItens() ;
		}catch(NoResultException nr) {
			return null;
		}
	}

}
