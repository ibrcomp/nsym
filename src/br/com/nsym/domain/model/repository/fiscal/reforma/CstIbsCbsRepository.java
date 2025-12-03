package br.com.nsym.domain.model.repository.fiscal.reforma;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fiscal.reforma.CstIbsCbs;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CstIbsCbsRepository extends GenericRepositoryEmpDS<CstIbsCbs, Long> {

	private static final long serialVersionUID = 3898873844202732147L;
	

    public CstIbsCbs findByCst(String cst) {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CstIbsCbs> cq = cb.createQuery(CstIbsCbs.class);
        Root<CstIbsCbs> root = cq.from(CstIbsCbs.class);

        Predicate p = cb.equal(root.get("cstIbsCbs"), cst);
        cq.select(root).where(p);

        List<CstIbsCbs> list = this.getEntityManager().createQuery(cq)
                .setMaxResults(1)
                .getResultList();

        return list.isEmpty() ? null : list.get(0);
    }

}
