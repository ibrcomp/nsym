package br.com.nsym.domain.model.repository.financeiro;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.transform.ResultTransformer;

import br.com.nsym.domain.model.entity.financeiro.AgTitulo;
import br.com.nsym.domain.model.entity.financeiro.AgTituloIDDTO;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import br.com.nsym.domain.model.entity.tools.PedidoStatus;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;


@Dependent
public class AgTituloRepository extends GenericRepositoryEmpDS<AgTitulo, Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1175919597607992827L;
	
	public List<ParcelasNfe> listaDeTitulosPorIdAgTitulo(Long id){
		EntityGraph<AgTitulo> entityGraph = this.getEntityManager().createEntityGraph(AgTitulo.class);
		entityGraph.addAttributeNodes("caixa");
		entityGraph.addAttributeNodes("listaParcelas");
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgTitulo> criteria =  builder.createQuery(getPersistentClass());

		Root<AgTitulo> formPedido = criteria.from(AgTitulo.class);
		
		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("id"),id));
		
		
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgTitulo> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("jakarta.persistence.loadgraph", entityGraph);
//		typedQuery.setHint("jakarta.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult().getListaTitulosAgrupados();
	}
	
	public AgTitulo encontraAgTituloPorId(Long id) {
		EntityGraph<AgTitulo> entityGraph = this.getEntityManager().createEntityGraph(AgTitulo.class);
		entityGraph.addAttributeNodes("listaTitulosAgrupados");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgTitulo> criteria =  builder.createQuery(getPersistentClass());

		Root<AgTitulo> formPedido = criteria.from(getPersistentClass());
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("id"), id));
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgTitulo> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult();
	}
	
	public AgTitulo encontraAgPedidoPorIdComRecParcial(Long id) {
		EntityGraph<AgTitulo> entityGraph = this.getEntityManager().createEntityGraph(AgTitulo.class);
		entityGraph.addAttributeNodes("listRecebimentoParcial");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgTitulo> criteria =  builder.createQuery(getPersistentClass());

		Root<AgTitulo> formPedido = criteria.from(getPersistentClass());
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("isDeleted"), false));
		conditions.add(builder.equal(formPedido.get("id"), id));
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<AgTitulo> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getSingleResult();
	}
	
	public List<AgTitulo> listaAgTituloPorStatus(LocalDate dataIni,LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial, PedidoStatus status){
		EntityGraph<AgTitulo> entityGraph = this.getEntityManager().createEntityGraph(AgTitulo.class);
		entityGraph.addAttributeNodes("listaTitulosAgrupados");
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AgTitulo> criteria =  builder.createQuery(getPersistentClass());

		Root<AgTitulo> formPedido = criteria.from(AgTitulo.class);
		
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
		TypedQuery<AgTitulo> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<AgTituloIDDTO> listaIDAgTituloPorTitulo(Long idTitulo){
		String sql = "select AgTitulo_ID as id from TabelaListaTitulosAgrupados where Titulo_ID = "+ idTitulo+""; 
				
		@SuppressWarnings("deprecation")
		Query resultado  =  this.getEntityManager()
		.createNativeQuery(sql)
			.unwrap(org.hibernate.query.Query.class).setResultTransformer(new ResultTransformer(){

				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Object transformTuple(Object[] tuple, String[] aliases) {
				
					// TODO Auto-generated method stub
					AgTituloIDDTO relDto = new AgTituloIDDTO(
								(BigInteger)tuple[0])
									;
						return relDto;
				}

				@SuppressWarnings("rawtypes")
				@Override
				public List transformList(List collection) {
					// TODO Auto-generated method stub
					return collection;
				}
				
			});
		
		return (List<AgTituloIDDTO>)resultado.getResultList();
		
		
	}
	
	@SuppressWarnings("unchecked")
	public List<AgTituloIDDTO> listaTitulosPorAgTitulo(Long ag){
		String sql = "select Titulo_ID as id from TabelaListaTitulosAgrupados where AgTitulo_ID = "+ ag+""; 
				
		@SuppressWarnings("deprecation")
		Query resultado  =  this.getEntityManager()
		.createNativeQuery(sql)
			.unwrap(org.hibernate.query.Query.class).setResultTransformer(new ResultTransformer(){

				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Object transformTuple(Object[] tuple, String[] aliases) {
				
					// TODO Auto-generated method stub
					AgTituloIDDTO relDto = new AgTituloIDDTO(
								(BigInteger)tuple[0])
									;
						return relDto;
				}

				@SuppressWarnings("rawtypes")
				@Override
				public List transformList(List collection) {
					// TODO Auto-generated method stub
					return collection;
				}
				
			});
		
		return (List<AgTituloIDDTO>)resultado.getResultList();
		
		
	}

}
