package br.com.nsym.domain.model.repository.venda;

import java.time.LocalDate;
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
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.entity.tools.PedidoTipo;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class AgPedidoRepository extends GenericRepositoryEmpDS<AgPedido, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5828889470351740091L;

	public List<ParcelasNfe> listaDeParcelasAgPedidoPorId(Long id){
		EntityGraph<AgPedido> entityGraph = this.getEntityManager().createEntityGraph(AgPedido.class);
		entityGraph.addAttributeNodes("caixa");
//		entityGraph.addSubgraph("agPedido").addAttributeNodes("listaParcelas");
		entityGraph.addAttributeNodes("listaParcelas");
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgPedido> criteria =  builder.createQuery(getPersistentClass());

		Root<AgPedido> formPedido = criteria.from(AgPedido.class);
		
		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("id"),id));
		
		
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgPedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("jakarta.persistence.loadgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult().getListaParcelas();
	}
		
	public List<AgPedido> listaAgPedidoPorStatus(LocalDate dataIni,LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial, PedidoStatus status){
		EntityGraph<AgPedido> entityGraph = this.getEntityManager().createEntityGraph(AgPedido.class);
		entityGraph.addAttributeNodes("caixa");
		entityGraph.addSubgraph("listaPedidosRecebidos")
		.addAttributeNodes("controle","emitente","transacao","destino","pagamento");
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgPedido> criteria =  builder.createQuery(getPersistentClass());

		Root<AgPedido> formPedido = criteria.from(AgPedido.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.between(formPedido.get("dataCriacao"),dataIni,dataFim);
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(itemPedido);
		
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("status"),status));
		}
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgPedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	public List<AgPedido> listaAgPedidoPorTipoEStatus(LocalDate dataIni,LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial, PedidoTipo tipo,PedidoStatus status){
		EntityGraph<AgPedido> entityGraph = this.getEntityManager().createEntityGraph(AgPedido.class);
		entityGraph.addAttributeNodes("caixa");
		entityGraph.addSubgraph("listaPedidosRecebidos")
		.addAttributeNodes("controle","emitente","transacao","destino","pagamento");
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgPedido> criteria =  builder.createQuery(getPersistentClass());

		Root<AgPedido> formPedido = criteria.from(AgPedido.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.between(formPedido.get("dataCriacao"),dataIni,dataFim);
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(itemPedido);
		
		if (tipo != null) {
			conditions.add(builder.equal(formPedido.get("pedidoTipo"),tipo));
		}
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("pedidoStatus"),status));
		}
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgPedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	public AgPedido encontraAgPedidoPorId(Long id) {
		EntityGraph<AgPedido> entityGraph = this.getEntityManager().createEntityGraph(AgPedido.class);
		entityGraph.addAttributeNodes("caixa");
		entityGraph.addSubgraph("listaPedidosRecebidos")
		.addAttributeNodes("controle");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgPedido> criteria =  builder.createQuery(getPersistentClass());

		Root<AgPedido> formPedido = criteria.from(getPersistentClass());
//		Join<AgPedido,Pedido> listaPedido = formPedido.join("listaPedidosRecebidos");
//		Join<Pedido,ControlePedido> controle = listaPedido.join("controle");
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("id"), id));
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgPedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult();
	}
	
	public List<AgPedido> pegaAgPedidoPorCaixa(Caixa caixa, Long pegaIdEmpresa, Long pegaIdFilial){
		EntityGraph<AgPedido> entityGraph = this.getEntityManager().createEntityGraph(AgPedido.class);
		entityGraph.addAttributeNodes("caixa");
		entityGraph.addSubgraph("listaPedidosRecebidos")
		.addAttributeNodes("controle","destino");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgPedido> criteria =  builder.createQuery(getPersistentClass());

		Root<AgPedido> formPedido = criteria.from(getPersistentClass());
//		Join<AgPedido,Pedido> listaPedido = formPedido.join("listaPedidosRecebidos").join("controle",JoinType.INNER);
//		listaPedido.join("destino",JoinType.INNER);
//		Join<Pedido,ControlePedido> controle = listaPedido.join("controle");
//		Join<Pedido,DestinatarioPedido> destino = listaPedido.join("destino");
		
		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("caixa"),caixa ));
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgPedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	public AgPedido preencheAgPedidoComItensPedido(Long id) {
		EntityGraph<AgPedido> entityGraph = this.getEntityManager().createEntityGraph(AgPedido.class);
		entityGraph.addAttributeNodes("listRecebimentoParcial");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgPedido> criteria =  builder.createQuery(getPersistentClass());

		Root<AgPedido> formPedido = criteria.from(getPersistentClass());
//		Join<AgPedido,Pedido> listaPedido = formPedido.join("listaPedidosRecebidos");
//		Join<Pedido,ControlePedido> controle = listaPedido.join("controle");
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("id"), id));
		
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgPedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult();
	}
	
	public AgPedido encontraAgPedidoComListaDeParcelas(Long id) {
		EntityGraph<AgPedido> entityGraph = this.getEntityManager().createEntityGraph(AgPedido.class);
		entityGraph.addAttributeNodes("listaParcelas");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgPedido> criteria =  builder.createQuery(getPersistentClass());

		Root<AgPedido> formPedido = criteria.from(getPersistentClass());
//		Join<AgPedido,Pedido> listaPedido = formPedido.join("listaPedidosRecebidos");
//		Join<Pedido,ControlePedido> controle = listaPedido.join("controle");
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("id"), id));
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgPedido> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult();
	}
}
