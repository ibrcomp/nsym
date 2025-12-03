package br.com.nsym.domain.model.repository.estoque;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.transform.ResultTransformer;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.estoque.EntradaEstoque;
import br.com.nsym.domain.model.entity.estoque.dto.RelEstoqueGeralDTO;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.fiscal.dto.RelNatOperacaoDTO;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;


@Dependent
public class EntradaEstoqueRepository extends GenericRepositoryEmpDS<EntradaEstoque, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -40214378143108689L;
	
	public Page<EntradaEstoque> listaEntradaEstoquePorIntervalo(LocalDate inicio, LocalDate fim, Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest,String filtro , String pesquisa,boolean recebimentoProduto) {
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<EntradaEstoque> criteria = builder.createQuery(EntradaEstoque.class);

		Root<EntradaEstoque> fromEntrada = criteria.from(EntradaEstoque.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.between(fromEntrada.get("dataCriacao"),inicio,fim));
		conditions.add(builder.equal(fromEntrada.get("recebimentoProduto"),recebimentoProduto));
		
		if (filtro != null){
			if (!filtro.isEmpty() ) {
				conditions.add(builder.like(builder.upper(fromEntrada.get(filtro)),"%"+pesquisa.toUpperCase()+"%")); // garantindo que tudo estja em maiuscula para localizar o termo
			}
		}
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromEntrada.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(fromEntrada.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(fromEntrada.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromEntrada.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromEntrada.get("idFilial"),pegaIdFilial));
			}
		}
		
		
		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(EntradaEstoque.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();
		
		criteria.select(fromEntrada.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		
		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromEntrada.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromEntrada.get(pageRequest.getSortField())));

		}
		
		criteria.distinct(true);
		TypedQuery<EntradaEstoque> typedQuery = this.getEntityManager().createQuery(criteria);
		// paginamos
				typedQuery.setFirstResult(pageRequest.getFirstResult());
				typedQuery.setMaxResults(pageRequest.getPageSize());
		return new Page<>(typedQuery.getResultList(),totalRows);
		
	}
	
	public Page<EntradaEstoque> listaRecProdPorIntervalo(LocalDate inicio, LocalDate fim, Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest,String filtro , String pesquisa,boolean recebimentoProduto,boolean fab) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<EntradaEstoque> criteria = builder.createQuery(EntradaEstoque.class);

		Root<EntradaEstoque> fromEntrada = criteria.from(EntradaEstoque.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.between(fromEntrada.get("dataCriacao"),inicio,fim));
		conditions.add(builder.equal(fromEntrada.get("recebimentoProduto"),recebimentoProduto));
		conditions.add(builder.equal(fromEntrada.get("fabrica"),fab));
		if (filtro != null){
			if (!filtro.isEmpty() ) {
				conditions.add(builder.like(builder.upper(fromEntrada.get(filtro)),"%"+pesquisa.toUpperCase()+"%")); // garantindo que tudo estja em maiuscula para localizar o termo
			}
		}
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromEntrada.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(fromEntrada.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(fromEntrada.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromEntrada.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromEntrada.get("idFilial"),pegaIdFilial));
			}
		}
		
		
		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(EntradaEstoque.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();
		
		criteria.select(fromEntrada.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		
		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromEntrada.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromEntrada.get(pageRequest.getSortField())));

		}
		
		criteria.distinct(true);
		TypedQuery<EntradaEstoque> typedQuery = this.getEntityManager().createQuery(criteria);
		
		// paginamos
				typedQuery.setFirstResult(pageRequest.getFirstResult());
				typedQuery.setMaxResults(pageRequest.getPageSize());
		return new Page<>(typedQuery.getResultList(),totalRows);
		
	}
	
	public EntradaEstoque pegaEntradaEstoquePorID(Long id) {
		EntityGraph<EntradaEstoque> entityGraph = this.getEntityManager().createEntityGraph(EntradaEstoque.class);
		entityGraph.addAttributeNodes("listaDeItensEntrada");
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<EntradaEstoque> criteria = builder.createQuery(EntradaEstoque.class);

		Root<EntradaEstoque> formEntrada = criteria.from(EntradaEstoque.class);
//		formEntrada.fetch("listaDeItensEntrada",JoinType.INNER);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formEntrada.get("id"),id));

		criteria.select(formEntrada.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<EntradaEstoque> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		return typedQuery.getSingleResult();

	}
	
	public List<RelEstoqueGeralDTO> relEstoqueGeral(Long pegaIdEmpresa,Long pegaIdFilial,boolean estoqueDifereZero ) {
		String sql = 
				"select prod.referencia as ref, prod.descricao as descricao, sum(barras.totalEstoque) as estoqueTotal,"
				+" sum(barras.totalComprado) as totalRecebido "
				+" from produto as prod "
				+ " inner join barras on barras.Produto_Id = prod.id "
				+ " where barras.id_empresa = '"+pegaIdEmpresa+"' and prod.isDeleted = FALSE and ";
		if (estoqueDifereZero) {
			sql = sql + " barras.totalEstoque <> '0' and";
			}
		if (pegaIdFilial == null) {
			sql = sql + " barras.id_filial is null group by prod.id,barras.Produto_id order by prod.referencia asc";
		}else {
			sql = sql + " barras.id_filial = '"+pegaIdFilial+"' group by prod.id,barras.Produto_id order by prod.referencia asc";
		}
		
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
					RelEstoqueGeralDTO relDto = new RelEstoqueGeralDTO(
									(String)tuple[0],
									(String)tuple[1], 
									(BigDecimal)tuple[2],
									(BigDecimal)tuple[3]) 
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
		@SuppressWarnings("unchecked")
		List<RelEstoqueGeralDTO> dto =  resultado.getResultList();
		
		
		return dto;
	}	


}
