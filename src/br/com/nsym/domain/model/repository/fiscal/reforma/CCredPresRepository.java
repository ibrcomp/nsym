package br.com.nsym.domain.model.repository.fiscal.reforma;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fiscal.reforma.CCredPres;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@RequestScoped
public class CCredPresRepository extends GenericRepositoryEmpDS<CCredPres,Long> {

    /**
	 *
	 */
	private static final long serialVersionUID = 6581566246359921770L;

	 public CCredPres findByCodigo(String codigo, Long idEmpresa, Long idFilial) {
	        if (codigo == null || codigo.trim().isEmpty()) {
	            return null;
	        }

	        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
	        CriteriaQuery<CCredPres> cq = cb.createQuery(CCredPres.class);
	        Root<CCredPres> root = cq.from(CCredPres.class);

	        List<Predicate> predicates = new ArrayList<>();

	        // c.codigo = :codigo
	        predicates.add(cb.equal(root.get("codigo"), codigo));

	        // Controle de proprietário: mesma Empresa
	        if (idEmpresa != null) {
	            predicates.add(cb.equal(root.get("idEmpresa"), idEmpresa));
	        }

	        // Controle de proprietário: mesma Filial
	        if (idFilial != null) {
	            predicates.add(cb.equal(root.get("idFilial"), idFilial));
	        }

	        cq.select(root)
	          .where(predicates.toArray(new Predicate[0]));

	        return this.getEntityManager()
	                .createQuery(cq)
	                .setMaxResults(1)
	                .getResultStream()
	                .findFirst()
	                .orElse(null);
	    }
	
	 public CCredPres findVigente(String codigo, LocalDate data, Long idEmpresa, Long idFilial) {
	        if (codigo == null || codigo.trim().isEmpty()) {
	            return null;
	        }
	        if (data == null) {
	            data = LocalDate.now();
	        }

	        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
	        CriteriaQuery<CCredPres> cq = cb.createQuery(CCredPres.class);
	        Root<CCredPres> root = cq.from(CCredPres.class);

	        List<Predicate> predicates = new ArrayList<>();

	        // c.codigo = :codigo
	        predicates.add(cb.equal(root.get("codigo"), codigo));

	        // (c.dataInicioVigencia is null or c.dataInicioVigencia <= :data)
	        predicates.add(
	            cb.or(
	                cb.isNull(root.get("dataInicioVigencia")),
	                cb.lessThanOrEqualTo(root.get("dataInicioVigencia"), data)
	            )
	        );

	        // (c.dataFimVigencia is null or c.dataFimVigencia >= :data)
	        predicates.add(
	            cb.or(
	                cb.isNull(root.get("dataFimVigencia")),
	                cb.greaterThanOrEqualTo(root.get("dataFimVigencia"), data)
	            )
	        );

	        // Controle de proprietário: mesma Empresa
	        if (idEmpresa != null) {
	            predicates.add(cb.equal(root.get("idEmpresa"),idEmpresa));
	        }

	        // Controle de proprietário: mesma Filial
	        if (idFilial != null) {
	            predicates.add(cb.equal(root.get("idFilial"), idFilial));
	        }

	        // Emula "order by dataInicioVigencia desc nulls last"
	        // nulls por último
	        Expression<Integer> nullsLast = cb.<Integer>selectCase()
	            .when(cb.isNull(root.get("dataInicioVigencia")), 1)
	            .otherwise(0);

	        cq.select(root)
	          .where(predicates.toArray(new Predicate[0]))
	          .orderBy(
	              cb.asc(nullsLast),                           // não nulos primeiro, nulos por último
	              cb.desc(root.get("dataInicioVigencia"))
	          );

	        return this.getEntityManager()
	                .createQuery(cq)
	                .setMaxResults(1)
	                .getResultStream()
	                .findFirst()
	                .orElse(null);
	    }
}
