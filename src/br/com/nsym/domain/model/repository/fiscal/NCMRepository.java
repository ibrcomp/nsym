package br.com.nsym.domain.model.repository.fiscal;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NCMRepository extends GenericRepositoryEmpDS<Ncm, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Ncm> listNcmAtivo(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}
	
	public boolean jaExiste(String nome,Long idEmpresa){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
				Restrictions.ilike("ncm", nome),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.list().isEmpty() == true){
			return false;
		}else {
			return true;
		}
	}
	
	public List<Ncm> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
						Restrictions.ilike("ncm", "%"+dep+"%"),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa)
//						Restrictions.eqOrIsNull("idFilial", idFilial)
						));
		return criteria.list();
	}
	
	public Ncm pegaNcm(Long id,Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("id", id),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		criteria.setFetchMode("listaIVAEstado", FetchMode.JOIN);
		
		return (Ncm) criteria.uniqueResult();
	}
	
	public Ncm pegaNcmComEstoque(Boolean isDeleted, Long id,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.and(
				Restrictions.eq("id", id),
				Restrictions.eq("isDeleted", isDeleted)));
		if (idEmpresa == null){
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", idEmpresa));
		}else{
			if (idFilial != null){
				criteria.add(Restrictions.and(Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
						Restrictions.eqOrIsNull("idFilial", idFilial)));
			}else{
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", idEmpresa));
			}
		}
		criteria.setFetchMode("listaEstoque", FetchMode.JOIN);
		
		return (Ncm) criteria.uniqueResult();
	}
	
	public Ncm localizaNCM(String nome,Long idEmpresa){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
				Restrictions.ilike("ncm", nome),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		return (Ncm) criteria.uniqueResult();
	}
	
	public Page<Ncm> listaLazyDeNCMPorFilial(boolean isDelete , Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Ncm> criteria = builder.createQuery(Ncm.class);

		Root<Ncm> formPedido = criteria.from(Ncm.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("isDeleted"),isDelete);
		
		conditions.add(itemPedido);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
//				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Ncm.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(formPedido.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(formPedido.get(pageRequest.getSortField())));

		}

		criteria.distinct(true);
		TypedQuery<Ncm> typedQuery = this.getEntityManager().createQuery(criteria);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		return new Page<>(typedQuery.getResultList(),totalRows);

	}
	
	
}
