package br.com.nsym.domain.model.repository.cadastro;

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

import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class FornecedorRepository extends GenericRepositoryEmpDS<Fornecedor, Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8560312445330812020L;

	public List<Fornecedor> listFornecedorAtiva(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}

//	public List<Fornecedor> pesquisaTexto(String dep, Long idEmpresa) {
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(Restrictions.ilike("razaoSocial", "%" + dep + "%"),
//				Restrictions.eq("isDeleted", false), 
//				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
//		return criteria.list();
//	}
	
	public List<Fornecedor> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		
		if (idFilial == null){
			criteria.add(Restrictions.and(
					Restrictions.ilike("razaoSocial", "%"+dep+"%"),
					Restrictions.eq("isDeleted", false),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		}else{
			criteria.add(Restrictions.and(
						Restrictions.ilike("razaoSocial", "%"+dep+"%"),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
						Restrictions.eqOrIsNull("idFilial", idFilial)));
		}
		return criteria.list();
	}
	
	public List<Fornecedor> localizaFornecedorPorRazaoSocial(String razao,Long idEmpresa){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Fornecedor> criteria =  builder.createQuery(getPersistentClass());

		Root<Fornecedor> formFornecedor = criteria.from(Fornecedor.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate item = builder.equal(formFornecedor.get("isDeleted"), false);
		conditions.add(item);
		
		if (razao != null){
			if (!razao.isEmpty()){
				conditions.add(builder.like(builder.lower(formFornecedor.<String>get("razaoSocial")),"%"+razao.toLowerCase()+"%"));
			}
		}
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formFornecedor.get("idEmpresa")));
		}else{
				conditions.add(builder.equal(formFornecedor.get("idEmpresa"),idEmpresa));
		}
		
		criteria.select(formFornecedor.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Fornecedor> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
	}
}
