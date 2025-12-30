package br.com.nsym.domain.model.repository.fiscal.nfce;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.NoResultException;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.financeiro.RecebimentoParcial;
import br.com.nsym.domain.model.entity.fiscal.Cfe.Nfce;
import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NfceRepository extends GenericRepositoryEmpDS<Nfce, Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3305623321105756061L;
	
	public Page<Nfce> listaNfceEmitidoPorIntervaloData(
	        LocalDate dataIni, LocalDate dataFim,
	        Long pegaIdEmpresa, Long pegaIdFilial,
	        PageRequest pageRequest) {

	    CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();

	    // =========================
	    // Query principal (lista)
	    // =========================
	    CriteriaQuery<Nfce> criteria = builder.createQuery(Nfce.class);
	    Root<Nfce> root = criteria.from(Nfce.class);

	    List<Predicate> conditions = buildConditionsNfce(
	            builder, root, dataIni, dataFim, pegaIdEmpresa, pegaIdFilial
	    );

	    criteria.select(root);
	    if (!conditions.isEmpty()) {
	        criteria.where(conditions.toArray(new Predicate[0]));
	    }

	    // Ordenação
	    String sortField = pageRequest.getSortField();
	    if (sortField == null || sortField.trim().isEmpty()) {
	        sortField = "dataEmissao"; // fallback
	    }

	    if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
	        criteria.orderBy(builder.asc(root.get(sortField)));
	    } else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
	        criteria.orderBy(builder.desc(root.get(sortField)));
	    }

	    criteria.distinct(true);

	    TypedQuery<Nfce> typedQuery = this.getEntityManager().createQuery(criteria);
	    typedQuery.setFirstResult(pageRequest.getFirstResult());
	    typedQuery.setMaxResults(pageRequest.getPageSize());

	    List<Nfce> content = typedQuery.getResultList();

	    // =========================
	    // Query de count (total)
	    // =========================
	    CriteriaQuery<Long> cqCount = builder.createQuery(Long.class);
	    Root<Nfce> countRoot = cqCount.from(Nfce.class);
	    cqCount.select(builder.countDistinct(countRoot));

	    List<Predicate> countConditions = buildConditionsNfce(
	            builder, countRoot, dataIni, dataFim, pegaIdEmpresa, pegaIdFilial
	    );

	    if (!countConditions.isEmpty()) {
	        cqCount.where(countConditions.toArray(new Predicate[0]));
	    }

	    Long totalRows = this.getEntityManager().createQuery(cqCount).getSingleResult();

	    return new Page<>(content, totalRows);
	}

	
	public BigDecimal totalNfcePeriodo(LocalDate dataIni, LocalDate dataFim, Long pegaIdEmpresa, Long pegaIdFilial) {
	    CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();

	    CriteriaQuery<BigDecimal> cq = builder.createQuery(BigDecimal.class);
	    Root<Nfce> root = cq.from(Nfce.class);

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
	    cq.select(builder.coalesce(builder.sum(root.get("vNf")), BigDecimal.ZERO));

	    if (!conditions.isEmpty()) {
	        cq.where(conditions.toArray(new Predicate[0]));
	    }

	    BigDecimal total = this.getEntityManager().createQuery(cq).getSingleResult();
	    return total != null ? total : BigDecimal.ZERO;
	}
	
	private List<Predicate> buildConditionsNfce(
	        CriteriaBuilder builder,
	        Root<Nfce> root,
	        LocalDate dataIni,
	        LocalDate dataFim,
	        Long idEmpresa,
	        Long idFilial) {

	    List<Predicate> conditions = new ArrayList<>();

	    // Filtro por dataEmissao (LocalDate)
	    if (dataIni != null && dataFim != null) {
	        conditions.add(builder.between(root.get("dataEmissao"), dataIni, dataFim));
	    } else if (dataIni != null) {
	        conditions.add(builder.greaterThanOrEqualTo(root.get("dataEmissao"), dataIni));
	    } else if (dataFim != null) {
	        conditions.add(builder.lessThanOrEqualTo(root.get("dataEmissao"), dataFim));
	    }

	    // Filtro empresa/filial (mesma regra que você já usa)
	    if (idEmpresa == null) {
	        conditions.add(builder.isNull(root.get("idEmpresa")));
	    } else {
	        conditions.add(builder.equal(root.get("idEmpresa"), idEmpresa));
	        if (idFilial == null) {
	            conditions.add(builder.isNull(root.get("idFilial")));
	        } else {
	            conditions.add(builder.equal(root.get("idFilial"), idFilial));
	        }
	    }

	    return conditions;
	}
	
	public Nfce pegaNfceLazy(Long id, Long idEmpresa, Long idFilial) {
	    CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

	    // =========================
	    // Query 1: fetch SOMENTE itens
	    // =========================
	    CriteriaQuery<Nfce> cq1 = cb.createQuery(Nfce.class);
	    Root<Nfce> root1 = cq1.from(Nfce.class);

	    // fetch de UMA bag (itens)
	    root1.fetch("itens", JoinType.LEFT);

	    List<Predicate> p1 = buildPredicatesNfce(cb, root1, id, idEmpresa, idFilial);

	    cq1.select(root1)
	       .where(p1.toArray(new Predicate[0]))
	       .distinct(true);

	    Nfce nfce;
	    try {
	        nfce =  this.getEntityManager().createQuery(cq1).getSingleResult();
	    } catch (NoResultException e) {
	        return null; // ou lance sua exceção de negócio
	    }

	    // =========================
	    // Query 2: fetch SOMENTE recebimentos + formaPagamento
	    // (mesma NFC-e; vai popular no MESMO contexto)
	    // =========================
	    CriteriaQuery<Nfce> cq2 = cb.createQuery(Nfce.class);
	    Root<Nfce> root2 = cq2.from(Nfce.class);

	    // fetch de UMA bag (listaRecebimentosAgrupados)
	    Fetch<Object, Object> recFetch = root2.fetch("listaRecebimentosAgrupados", JoinType.LEFT);

	    // fetch do ManyToOne dentro dela (formaPagamento)
	    // (Fetch tem fetch também; funciona no Hibernate/JPA)
	    recFetch.fetch("formaPagamento", JoinType.LEFT);

	    List<Predicate> p2 = buildPredicatesNfce(cb, root2, id, idEmpresa, idFilial);

	    cq2.select(root2)
	       .where(p2.toArray(new Predicate[0]))
	       .distinct(true);

	    // executa só para carregar os relacionamentos no contexto
	    this.getEntityManager().createQuery(cq2).getResultList();

	    return nfce;
	}
	
	public List<RecebimentoParcial> pegaListaRecebimentoParcial(Long id, Long idEmpresa, Long idFilial) {
		
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Nfce> cq1 = cb.createQuery(Nfce.class);
	    Root<Nfce> root1 = cq1.from(Nfce.class);

	    // fetch de UMA bag (itens)
	    root1.fetch("listaRecebimentosAgrupados", JoinType.LEFT);

	    List<Predicate> p1 = buildPredicatesNfce(cb, root1, id, idEmpresa, idFilial);

	    cq1.select(root1)
	       .where(p1.toArray(new Predicate[0]))
	       .distinct(true);

	    Nfce nfce;
	    try {
	        nfce =  this.getEntityManager().createQuery(cq1).getSingleResult();
	        
	        // ✅ inicializa a outra lista dentro da sessão
	        nfce.getListaRecebimentosAgrupados().size();
	        // se você precisa da formaPagamento também:
	        nfce.getListaRecebimentosAgrupados().forEach(r -> {
	            if (r != null && r.getFormaPagamento() != null) {
	                r.getFormaPagamento().getId(); // “toca” para inicializar
	            }
	        });
	    } catch (NoResultException e) {
	        return null; // ou lance sua exceção de negócio
	    }
		return nfce.getListaRecebimentosAgrupados();
	}

	private List<Predicate> buildPredicatesNfce(CriteriaBuilder cb, Root<Nfce> root,
	                                           Long id, Long idEmpresa, Long idFilial) {
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
	    return conditions;
	}
	
	
	public Nfce pegaNfceLazyComRecebimento(Long id, Long idEmpresa, Long idFilial) {
		EntityGraph<Nfce> entityGraph = this.getEntityManager().createEntityGraph(Nfce.class);
		entityGraph.addAttributeNodes("listaRecebimentosAgrupados");
		Subgraph<?> recebimentosSubgraph =
	    		entityGraph.addSubgraph("listaRecebimentosAgrupados");
	    recebimentosSubgraph.addAttributeNodes("formaPagamento");
	    CriteriaBuilder cb =  this.getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<Nfce> cq = cb.createQuery(Nfce.class);
	    Root<Nfce> root = cq.from(Nfce.class);

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

	    cq.select(root)
	      .where(conditions.toArray(new Predicate[0]))
	      .distinct(true);

	    TypedQuery<Nfce> query = this.getEntityManager().createQuery(cq);
	    query.setHint("javax.persistence.loadgraph", entityGraph);
	    return query.getSingleResult();
	}


}