package br.com.nsym.domain.model.repository.fiscal.sat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.fiscal.Cfe.CFe;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CFeRepository extends GenericRepositoryEmpDS<CFe, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Page<CFe> listaCFeEmitidoPorIntervaloData(LocalDate dataIni, LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CFe> criteria = builder.createQuery(CFe.class);

		Root<CFe> fromCFe = criteria.from(CFe.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate cfe = builder.between(fromCFe.get("dataEmissao"),dataIni,dataFim);


		conditions.add(cfe);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromCFe.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(fromCFe.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(fromCFe.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromCFe.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromCFe.get("idFilial"),pegaIdFilial));
			}
		}
		
		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(CFe.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();
		
		criteria.select(fromCFe.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		
		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromCFe.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromCFe.get(pageRequest.getSortField())));

		}
		
		criteria.distinct(true);
		TypedQuery<CFe> typedQuery = this.getEntityManager().createQuery(criteria);
		
		// paginamos
				typedQuery.setFirstResult(pageRequest.getFirstResult());
				typedQuery.setMaxResults(pageRequest.getPageSize());
		//		return (BarrasEstoque)typedQuery.getSingleResult();
		return new Page<>(typedQuery.getResultList(),totalRows);
		
	}
	
	public CFe pegaCfeLazy(Long id, Long idEmpresa, Long idFilial) {
	    CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<CFe> cq = cb.createQuery(CFe.class);

	    Root<CFe> root = cq.from(CFe.class);

	    // carrega listas (evita LazyInitialization + N+1)
	    root.fetch("listaItem", JoinType.LEFT);

	    List<Predicate> conditions = new ArrayList<>();
	    conditions.add(cb.equal(root.get("id"), id));

	    if (idEmpresa == null) {
	        conditions.add(cb.isNull(root.get("idEmpresa")));
	    } else {
	        conditions.add(cb.equal(root.get("idEmpresa"), idEmpresa));
	        if (idFilial == null) {
	            conditions.add(cb.isNull(root.get("idFilial")));
	        } else {
	            conditions.add(cb.equal(root.get("idFilial"), idFilial));
	        }
	    }

	    cq.select(root).where(conditions.toArray(new Predicate[0]));
	    cq.distinct(true);

	    CFe cfe = this.getEntityManager().createQuery(cq).getSingleResult();
		// garante inicialização das coleções LAZY ainda dentro do contexto/persistência
		if (cfe.getListaItem() != null) { cfe.getListaItem().size(); }
		if (cfe.getListaParcelas() != null) { cfe.getListaParcelas().size(); }
		return cfe;

	}


	
	public CFe pegaCFe(Long id, Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("id", id),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		criteria.setFetchMode("listaItem", FetchMode.JOIN);


		return (CFe) criteria.uniqueResult();
	}

	public BigDecimal totalCfePeriodo(LocalDate dataIni, LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial) {
	    CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();

	    CriteriaQuery<BigDecimal> cq = builder.createQuery(BigDecimal.class);
	    Root<CFe> root = cq.from(CFe.class);

	    List<Predicate> conditions = new ArrayList<>();

	    // dataEmissao é LocalDate
	    if (dataIni != null && dataFim != null) {
	        conditions.add(builder.between(root.get("dataEmissao"), dataIni, dataFim));
	    } else if (dataIni != null) {
	        conditions.add(builder.greaterThanOrEqualTo(root.get("dataEmissao"), dataIni));
	    } else if (dataFim != null) {
	        conditions.add(builder.lessThanOrEqualTo(root.get("dataEmissao"), dataFim));
	    }

	    // empresa/filial (mesma regra do list)
	    if (pegaIdEmpresa == null) {
	        conditions.add(builder.isNull(root.get("idEmpresa")));
	    } else {
	        conditions.add(builder.equal(root.get("idEmpresa"), pegaIdEmpresa));

	        if (pegaIdFilial == null) {
	            conditions.add(builder.isNull(root.get("idFilial")));
	        } else {
	            conditions.add(builder.equal(root.get("idFilial"), pegaIdFilial));
	        }
	    }

	    // SUM(vNf) no banco (coalesce para não voltar null)
	    cq.select(builder.coalesce(builder.sum(root.get("valorTotalNota")), BigDecimal.ZERO));

	    if (!conditions.isEmpty()) {
	        cq.where(conditions.toArray(new Predicate[0]));
	    }

	    BigDecimal total = this.getEntityManager().createQuery(cq).getSingleResult();
	    return total != null ? total : BigDecimal.ZERO;
	}
	
	
}
