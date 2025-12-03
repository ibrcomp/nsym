package br.com.nsym.domain.model.repository.financeiro;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.financeiro.FormaDePagamento;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class FormaDePagementoRepository extends GenericRepositoryEmpDS<FormaDePagamento, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<FormaDePagamento> listaFormaDePagamento(Long empresa,Long filial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FormaDePagamento> criteria = builder.createQuery(FormaDePagamento.class);

		Root<FormaDePagamento> formPedido = criteria.from(FormaDePagamento.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("isDeleted"),false));
		if (empresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (filial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),empresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),empresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),filial));
			}
		}

		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<FormaDePagamento> typedQuery = this.getEntityManager().createQuery(criteria);
		return typedQuery.getResultList();
		
		
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(
//				Restrictions.eq("isDeleted", false),
//				Restrictions.eqOrIsNull("idEmpresa", empresa),
//				Restrictions.eqOrIsNull("idFilial", filial)));
//		
//			return  criteria.list();
	}
	
	public boolean isExist(int codigo,Long empresa,Long filial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("codigo", codigo),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", empresa),
				Restrictions.eqOrIsNull("idFilial", filial)));
		
			if (criteria.list().isEmpty()){
				return false;
			}else {
				return true;
			}
	}
	
	
}
