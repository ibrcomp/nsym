package br.com.nsym.domain.model.repository.financeiro;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.financeiro.AgPedido;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.financeiro.tools.MovimentoEnum;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class RecebimentoParcialRepository  extends GenericRepositoryEmpDS<RecebimentoParcial, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5545765195627106063L;
	
	public List<RecebimentoParcial> listaRecebimentosPorLivroCaixa(Caixa caixa,MovimentoEnum livroCaixa,Long idEmpresa,Long idFilial){
		EntityGraph<RecebimentoParcial> entityGraph = this.getEntityManager().createEntityGraph(RecebimentoParcial.class);
		entityGraph.addAttributeNodes("caixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RecebimentoParcial> criteria = builder.createQuery(RecebimentoParcial.class);

		Root<RecebimentoParcial> formRecebimento = criteria.from(RecebimentoParcial.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formRecebimento.get("caixa"),caixa));
		conditions.add(builder.equal(formRecebimento.get("livroCaixa"),livroCaixa));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formRecebimento.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formRecebimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formRecebimento.get("idFilial"),idFilial));
			}
		}
		
//		criteria.select(formRecebimento.alias("r"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<RecebimentoParcial> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	public List<RecebimentoParcial> listaRecebimentosPorMovimentacaoSangria(Caixa caixa,Long idEmpresa,Long idFilial){
		EntityGraph<RecebimentoParcial> entityGraph = this.getEntityManager().createEntityGraph(RecebimentoParcial.class);
		entityGraph.addAttributeNodes("caixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RecebimentoParcial> criteria = builder.createQuery(RecebimentoParcial.class);

		Root<RecebimentoParcial> formRecebimento = criteria.from(RecebimentoParcial.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formRecebimento.get("caixa"),caixa));
		conditions.add
		(builder.or(builder.equal(formRecebimento.get("livroCaixa"),MovimentoEnum.Ent),
				builder.equal(formRecebimento.get("livroCaixa"),MovimentoEnum.Ret)));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formRecebimento.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formRecebimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formRecebimento.get("idFilial"),idFilial));
			}
		}
		
//		criteria.select(formRecebimento.alias("r"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<RecebimentoParcial> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	public List<RecebimentoParcial> listaRecebimentosPorCaixa(Caixa caixa,Long idEmpresa,Long idFilial){
		EntityGraph<RecebimentoParcial> entityGraph = this.getEntityManager().createEntityGraph(RecebimentoParcial.class);
		entityGraph.addAttributeNodes("formaPagamento","caixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RecebimentoParcial> criteria = builder.createQuery(RecebimentoParcial.class);

		Root<RecebimentoParcial> formRecebimento = criteria.from(RecebimentoParcial.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formRecebimento.get("caixa"),caixa));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formRecebimento.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formRecebimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formRecebimento.get("idFilial"),idFilial));
			}
		}
		
//		criteria.select(formRecebimento.alias("r"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<RecebimentoParcial> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	// criado para ser usado no SAT CAIXA preenchendo a lista de recebimentos do pedido
	public List<RecebimentoParcial> listaRecebimentosPorAgrupados(AgPedido agPedido,Long idEmpresa,Long idFilial){
		EntityGraph<RecebimentoParcial> entityGraph = this.getEntityManager().createEntityGraph(RecebimentoParcial.class);
		entityGraph.addAttributeNodes("formaPagamento","agrupado");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RecebimentoParcial> criteria = builder.createQuery(RecebimentoParcial.class);

		Root<RecebimentoParcial> formRecebimento = criteria.from(RecebimentoParcial.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formRecebimento.get("agrupado"),agPedido));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formRecebimento.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formRecebimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formRecebimento.get("idFilial"),idFilial));
			}
		}
		
//		criteria.select(formRecebimento.alias("r"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<RecebimentoParcial> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	public RecebimentoParcial recebimentoPorid(Long rec,Long idEmpresa,Long idFilial){
		EntityGraph<RecebimentoParcial> entityGraph = this.getEntityManager().createEntityGraph(RecebimentoParcial.class);
		entityGraph.addAttributeNodes("formaPagamento");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RecebimentoParcial> criteria = builder.createQuery(RecebimentoParcial.class);

		Root<RecebimentoParcial> formRecebimento = criteria.from(RecebimentoParcial.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formRecebimento.get("id"),rec));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formRecebimento.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formRecebimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formRecebimento.get("idFilial"),idFilial));
			}
		}
		
//		criteria.select(formRecebimento.alias("r"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<RecebimentoParcial> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult();
	}

}
