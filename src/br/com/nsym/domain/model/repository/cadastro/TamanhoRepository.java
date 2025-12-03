package br.com.nsym.domain.model.repository.cadastro;

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
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Cor;
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class TamanhoRepository extends GenericRepositoryEmpDS<Tamanho, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Tamanho> listTamanhoAtivo() {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null) {
			return null;
		} else {
			return criteria.list();
		}
	}

	public boolean jaExiste(String nome, Long idEmpresa) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.ilike("tamanho", nome)));
		if (criteria.list().isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}
	
	public Tamanho localizaPorNome(String nome,Long pegaIdEmpresa){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Tamanho> criteria = builder.createQuery(Tamanho.class);

		Root<Tamanho> fromTamanho = criteria.from(Tamanho.class);
		fromTamanho.fetch("listaBarras",JoinType.LEFT);
		
		List<Predicate> conditions = new ArrayList<>();

		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromTamanho.get("idEmpresa")));
			conditions.add(builder.equal(fromTamanho.get("tamanho"),nome));
		}else{
				conditions.add(builder.equal(fromTamanho.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromTamanho.get("tamanho"),nome));
		}

		criteria.select(fromTamanho.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Tamanho> typedQuery = this.getEntityManager().createQuery(criteria);

		return  typedQuery.getSingleResult();
	}
	
	public List<Tamanho> listaTamanhoLazy(Long pegaIdEmpresa,Long pegaIdFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Tamanho> criteria = builder.createQuery(Tamanho.class);

		Root<Tamanho> fromCor = criteria.from(Tamanho.class);
		fromCor.fetch("listaBarras",JoinType.LEFT);
//		fromCor.fetch("produtos",JoinType.LEFT);

		List<Predicate> conditions = new ArrayList<>();

		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromCor.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(fromCor.get("idEmpresa"),pegaIdEmpresa));
			}else{
				conditions.add(builder.equal(fromCor.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromCor.get("idFilial"),pegaIdFilial));
			}
		}

		criteria.select(fromCor.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Tamanho> typedQuery = this.getEntityManager().createQuery(criteria);

		return typedQuery.getResultList();
	}
}
