package br.com.nsym.domain.model.repository.fiscal.sat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
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
	
	public CFe pegaCfeLazy (Long id, Long idEmpresa, Long idFilial) {
	CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
	CriteriaQuery<CFe> criteria = builder.createQuery(CFe.class);

	Root<CFe> fromCFe = criteria.from(CFe.class);	
	From<?,?> parcelasJoin = (From<?, ?>) fromCFe.join("listaParcelas",JoinType.INNER);
	fromCFe.fetch("listaItem",JoinType.INNER);
	

	List<Predicate> conditions = new ArrayList<>();


	conditions.add(builder.equal(fromCFe.get("id"), id));
	conditions.add(builder.equal(parcelasJoin.get("cfe"), id));
	if (idEmpresa == null){
		conditions.add(builder.isNull(fromCFe.get("idEmpresa")));
	}else{ 
		if (idFilial == null) {
			conditions.add(builder.equal(fromCFe.get("idEmpresa"),idEmpresa));
			conditions.add(builder.isNull(fromCFe.get("idFilial")));
		}else {
			conditions.add(builder.equal(fromCFe.get("idEmpresa"),idEmpresa));
			conditions.add(builder.equal(fromCFe.get("idFilial"),idFilial));
		}
	}

	criteria.select(fromCFe.alias("p"));
	criteria.where(conditions.toArray(new Predicate[]{}));
	criteria.distinct(true);
	TypedQuery<CFe> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
	
	
	
	// montamos o resultado paginado
	return  (CFe) typedQuery.getSingleResult();


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
			}else{
				conditions.add(builder.equal(fromCFe.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromCFe.get("idFilial"),pegaIdFilial));
			}
		}
		
//		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
//		cq.select(builder.count(cq.from(CFe.class)));
//		this.getEntityManager().createQuery(cq);
//		cq.where(conditions.toArray(new Predicate[0]));

		criteria.select(fromCFe.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		
		criteria.distinct(true);
		TypedQuery<CFe> typedQuery = this.getEntityManager().createQuery(criteria);
		BigDecimal total = new BigDecimal("0");
		Iterator<CFe> it =  typedQuery.getResultList().iterator();
		while (it.hasNext()) {
			total = total.add(it.next().getValorTotalNota());
		}
		return  total;
	} 
	
	
}
