package br.com.nsym.domain.model.repository.fiscal.reforma;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fiscal.reforma.CClassTrib;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CClassTribRepository extends GenericRepositoryEmpDS<CClassTrib, Long> {


		private static final long serialVersionUID = 6274620376805398740L;

	public CClassTrib findByCstAndCClassTrib(String cst, String cclass) {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CClassTrib> cq = cb.createQuery(CClassTrib.class);
        Root<CClassTrib> root = cq.from(CClassTrib.class);

        Predicate p = cb.and(
                cb.equal(root.get("cstIbsCbs"), cst),
                cb.equal(root.get("cClassTrib"), cclass)
        );

        cq.select(root).where(p);

        List<CClassTrib> list = this.getEntityManager().createQuery(cq)
                .setMaxResults(1)
                .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }

}
