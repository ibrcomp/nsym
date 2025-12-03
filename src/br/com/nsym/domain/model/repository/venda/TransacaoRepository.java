package br.com.nsym.domain.model.repository.venda;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.entity.venda.TipoTransacao;
import br.com.nsym.domain.model.entity.venda.Transacao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class TransacaoRepository extends GenericRepositoryEmpDS<Transacao,Long> {

	
	/**
	 *
	 */
	private static final long serialVersionUID = -3712051461378265251L;

	public Transacao pegaTransacaoPorId(Long id) {
		EntityGraph<Transacao> entityGraph = this.getEntityManager().createEntityGraph(Transacao.class);
		entityGraph.addAttributeNodes("tributoPadrao");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Transacao> criteria = builder.createQuery(Transacao.class);
		
		Root<Transacao> fromControle = criteria.from(Transacao.class);

		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(fromControle.get("id"), id));
		
		criteria.select(fromControle.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Transacao> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

		return typedQuery.getSingleResult();

		
	}
	
	public List<Transacao> listaTodasAsTransacoesPorFilial(boolean isDeleted,TipoTransacao tipo,Long idEmpresa, Long idFilial) {
		EntityGraph<Transacao> entityGraph = this.getEntityManager().createEntityGraph(Transacao.class);
		entityGraph.addAttributeNodes("tributoPadrao");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Transacao> criteria = builder.createQuery(Transacao.class);
		
		Root<Transacao> fromControle = criteria.from(Transacao.class);

		List<Predicate> conditions = new ArrayList<>();
		
//		Predicate filial = builder.equal(fromControle.get("idFilial"), idFilial);
//		Predicate empresa = builder.equal(fromControle.get("idEmpresa"), idEmpresa);
//		Predicate empresaNull = builder.isNull(fromControle.get("idEmpresa"));

		conditions.add(builder.equal(fromControle.get("isDeleted"), isDeleted));
		if (tipo != null) {
			conditions.add(builder.equal(fromControle.get("tipoTransacao"), tipo));
		}

		if (idEmpresa == null){
			conditions.add(builder.isNull(fromControle.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromControle.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(fromControle.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromControle.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(fromControle.get("idFilial"),idFilial));
			}
		}
		
		criteria.select(fromControle.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Transacao> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

		return typedQuery.getResultList();

		
	}

}
