package br.com.nsym.domain.model.repository.cadastro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class FilialRepository extends GenericRepositoryEmpDS<Filial, Long>{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 830657041353154691L;

	public List<Filial> listFilialAtiva(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return criteria.list();
		}
	}
	public Filial update (Filial emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}
	
	public List<Filial> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
						Restrictions.ilike("razaoSocial", "%"+dep+"%"),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		return criteria.list();
	}
	
	 /**
     *  	Método que retorna uma lista de filiais por MATRIZ, 
     * @param idEmpresa
     * @param idFilial
     * @param deleted (TRUE- exibe somente Excluidos  FALSE - Exibe os não excluidos)
     * @return Lista<T>
     */
    public List<Filial> listaDeFiliaisPorEmpresa (Long idEmpresa, boolean deleted){
    	CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Filial> criteria =  builder.createQuery(Filial.class);
		
		Root<Filial> formPedido = criteria.from(Filial.class);
		List<Predicate> conditions = new ArrayList<>();
		
		
		conditions.add(builder.equal(formPedido.get("isDeleted"), deleted));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
				conditions.add(builder.equal(formPedido.get("empresa"),idEmpresa));
		}
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Filial> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
    }
    
    public Filial lockById(Long id) {
    	return this.getEntityManager().find(Filial.class, id, LockModeType.PESSIMISTIC_WRITE);
    }
}
