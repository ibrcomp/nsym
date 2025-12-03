package br.com.nsym.domain.model.repository.fabrica;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;

import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.entity.fabrica.MaterialModelo;
import br.com.nsym.domain.model.entity.fabrica.Modelo;
import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ModeloRepository extends GenericRepositoryEmpDS<Modelo,Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = -5841362621809033328L;
	
	public Modelo pegaModeloPorID(boolean isDeleted, Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Modelo> entityGraph = this.getEntityManager().createEntityGraph(Modelo.class);
		entityGraph.addAttributeNodes("listaDeMaterias");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Modelo> criteria = builder.createQuery(Modelo.class);

		Root<Modelo> fromModelo = criteria.from(Modelo.class);
		
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
		TypedQuery<Modelo> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		try {
			return typedQuery.getSingleResult();
		}catch(NoResultException nr) {
			return null;
		}
	}
	
	public List<Tamanho> pegaTamanhosDisponiveisPorModelo(boolean isDeleted, Long id, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Modelo> entityGraph = this.getEntityManager().createEntityGraph(Modelo.class);
		entityGraph.addAttributeNodes("tamanhosDisponiveis");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Modelo> criteria = builder.createQuery(Modelo.class);

		Root<Modelo> fromModelo = criteria.from(Modelo.class);
		
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
		TypedQuery<Modelo> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);

		
//		final Long totalRows = new BigDecimal(typedQuery.getResultList().size()).longValue();
		// montamos o resultado paginado
		List<Tamanho> listaTamanhos = new ArrayList<>();
		try {
			Modelo modeloTemp = typedQuery.getSingleResult();
			if (modeloTemp.getTamanhosDisponiveis() != null) {
				listaTamanhos = modeloTemp.getTamanhosDisponiveis();
			}
		}catch(NoResultException nr) {
			return null;
		}
		return listaTamanhos;
	}
	
	public List<Modelo> pegaListaDeModelosComMateriais(boolean isDeleted, Long idEmpresa, Long idFilial,boolean porFilial){
		EntityGraph<Modelo> entityGraph = this.getEntityManager().createEntityGraph(Modelo.class);
		entityGraph.addAttributeNodes("listaDeMaterias");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Modelo> criteria = builder.createQuery(Modelo.class);

		Root<Modelo> fromModelo = criteria.from(Modelo.class);
		
		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromModelo.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromModelo.get("idEmpresa"));

		conditions.add(builder.equal(fromModelo.get("isDeleted"), isDeleted));
		
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
		TypedQuery<Modelo> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
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
