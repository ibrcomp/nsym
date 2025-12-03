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

import br.com.nsym.domain.model.entity.fabrica.FichaTecnica;
import br.com.nsym.domain.model.entity.fabrica.MaterialModelo;
import br.com.nsym.domain.model.entity.fabrica.Producao;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.SequenciaLinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.StatusAndamento;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ProducaoRepository extends GenericRepositoryEmpDS<Producao, Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = -6261492202217128682L;
	
	public Producao pegaProducaoComGradePreenchida(boolean isDeleted,Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Producao> entityGraph = this.getEntityManager().createEntityGraph(Producao.class);
		entityGraph.addAttributeNodes("listaGrade","produto");
//		entityGraph.addSubgraph("modelo",Modelo.class).addAttributeNodes("listaDeMaterias");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Producao> criteria = builder.createQuery(Producao.class);

		Root<Producao> fromModelo = criteria.from(Producao.class);
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
		TypedQuery<Producao> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return  typedQuery.getSingleResult();
		}catch(NoResultException nr) {
			return null;
		}
	}
	
	public Producao pegaProducaoComListaDeFichasTecnicas(boolean isDeleted,Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Producao> entityGraph = this.getEntityManager().createEntityGraph(Producao.class);
		entityGraph.addAttributeNodes("listaDeFichasTecnicas");
//		entityGraph.addSubgraph("modelo",Modelo.class).addAttributeNodes("listaDeMaterias");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Producao> criteria = builder.createQuery(Producao.class);

		Root<Producao> fromModelo = criteria.from(Producao.class);
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
		TypedQuery<Producao> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return  typedQuery.getSingleResult();
		}catch(NoResultException nr) {
			return null;
		}
	}
	
	public List<FichaTecnica> pegaListaDeFichasTecnicas(boolean isDeleted,Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Producao> entityGraph = this.getEntityManager().createEntityGraph(Producao.class);
		entityGraph.addAttributeNodes("listaDeFichasTecnicas");
//		entityGraph.addSubgraph("modelo",Modelo.class).addAttributeNodes("listaDeMaterias");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Producao> criteria = builder.createQuery(Producao.class);

		Root<Producao> fromModelo = criteria.from(Producao.class);
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
		TypedQuery<Producao> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return  typedQuery.getSingleResult().getListaDeFichasTecnicas();
		}catch(NoResultException nr) {
			return null;
		}
	}
	
	public List<MaterialModelo> pegaListaDeMateriaisSemFichasTecnicas(boolean isDeleted,Long id ,Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Producao> entityGraph = this.getEntityManager().createEntityGraph(Producao.class);
		entityGraph.addAttributeNodes("listaDeMateriais");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Producao> criteria = builder.createQuery(Producao.class);

		Root<Producao> fromModelo = criteria.from(Producao.class);
		
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
		TypedQuery<Producao> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			List<MaterialModelo> listaMaterialSemFicha = new ArrayList<>();
			for (MaterialModelo materialModelo : typedQuery.getSingleResult().getListaDeMateriais()) {
				if (materialModelo.isPossuiFichaTecnicas() == false) {
					listaMaterialSemFicha.add(materialModelo);
				}
			}
			return  listaMaterialSemFicha;
		}catch(NoResultException nr) {
			return null;
		}
	}
	
	public List<SequenciaLinhaProducao> pegaSequenciaProducao(boolean isDeleted,Long id ,Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Producao> entityGraph = this.getEntityManager().createEntityGraph(Producao.class);
		entityGraph.addSubgraph("sequenciaProducao",LinhaProducao.class).addAttributeNodes("sequenciaProducao");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Producao> criteria = builder.createQuery(Producao.class);

		Root<Producao> fromModelo = criteria.from(Producao.class);
		
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
		TypedQuery<Producao> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return  typedQuery.getSingleResult().getSequenciaProducao().getSequenciaProducao();
		}catch(NoResultException nr) {
			return null;
		}
	}
	
	public List<Producao> pegaProducaoAguardandoInicio(boolean isDeleted,Long idEmpresa, Long idFilial,boolean porFilial){
//		EntityGraph<Producao> entityGraph = this.getEntityManager().createEntityGraph(Producao.class);
//		entityGraph.addSubgraph("sequenciaProducao",LinhaProducao.class).addAttributeNodes("sequenciaProducao");
//		entityGraph.addSubgraph("modelo",Modelo.class).addAttributeNodes("listaDeMaterias");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Producao> criteria = builder.createQuery(Producao.class);

		Root<Producao> fromModelo = criteria.from(Producao.class);
		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromModelo.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromModelo.get("idEmpresa"));

		conditions.add(builder.equal(fromModelo.get("isDeleted"), isDeleted));
		conditions.add(builder.or(builder.equal(fromModelo.get("andamento"),StatusAndamento.AND),builder.equal(fromModelo.get("andamento"),StatusAndamento.AGU)));
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
		TypedQuery<Producao> typedQuery = this.getEntityManager().createQuery(criteria);
//		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return  typedQuery.getResultList();
		}catch(NoResultException nr) {
			return null;
		}
	}

}
