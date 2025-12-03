package br.com.nsym.domain.model.repository.fabrica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fabrica.util.EtapaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.SequenciaLinhaProducao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class LinhaProducaoRepository extends GenericRepositoryEmpDS<LinhaProducao,Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = -4233453591914495932L;
	
	public boolean existeLinha(Long idEmpresa,Long idFilial, String descricao){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<LinhaProducao> criteria = builder.createQuery(LinhaProducao.class);
		

		Root<LinhaProducao> formEtapa = criteria.from(LinhaProducao.class);

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
		TypedQuery<LinhaProducao> typedQuery = this.getEntityManager().createQuery(criteria);
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
	
	public List<EtapaProducao> pegaSequenciaProducaoEmOrdem(boolean isDeleted,Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<LinhaProducao> entityGraph = this.getEntityManager().createEntityGraph(LinhaProducao.class);
		entityGraph.addAttributeNodes("sequenciaProducao");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<LinhaProducao> criteria = builder.createQuery(LinhaProducao.class);

		Root<LinhaProducao> fromModelo = criteria.from(LinhaProducao.class);
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
		TypedQuery<LinhaProducao> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			List<EtapaProducao> sequenciaProducaoEmOrdem = new ArrayList<>();
			HashMap<Long, EtapaProducao> hasSequenciaProducao = new HashMap<Long, EtapaProducao>();
			for (SequenciaLinhaProducao sequenciaProd : typedQuery.getSingleResult().getSequenciaProducao()) {
				hasSequenciaProducao.put(sequenciaProd.getIndice(), sequenciaProd.getEtapa());
			}
			for (Long i = 1l;typedQuery.getSingleResult().getSequenciaProducao().size()>= i; i++ ) {
				sequenciaProducaoEmOrdem.add(hasSequenciaProducao.get(i));
			}
			return sequenciaProducaoEmOrdem ;
		}catch(NoResultException nr) {
			return null;
		}
	}

	public LinhaProducao pegaLinhaProducaoComSequencia(boolean isDeleted,Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<LinhaProducao> entityGraph = this.getEntityManager().createEntityGraph(LinhaProducao.class);
		entityGraph.addAttributeNodes("sequenciaProducao");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<LinhaProducao> criteria = builder.createQuery(LinhaProducao.class);

		Root<LinhaProducao> fromModelo = criteria.from(LinhaProducao.class);
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
		TypedQuery<LinhaProducao> typedQuery = this.getEntityManager().createQuery(criteria);
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
