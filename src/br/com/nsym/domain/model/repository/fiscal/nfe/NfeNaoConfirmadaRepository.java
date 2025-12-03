package br.com.nsym.domain.model.repository.fiscal.nfe;

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

import br.com.nsym.domain.model.entity.fiscal.nfe.NfeNaoConfirmada;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NfeNaoConfirmadaRepository extends GenericRepositoryEmpDS<NfeNaoConfirmada, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  retorna uma Nfe atraves de sua chave de acesso 
	 * @param chave
	 * @param idEmpresa
	 * @param idFilial
	 * @return NfeNaoConfirmada
	 */
	public NfeNaoConfirmada pegaNfePorChaveDeAcesso(String chave, Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();
		
			criteria.add(Restrictions.and(
				Restrictions.eq("chNfe", chave),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (idFilial != null){
			criteria.add(Restrictions.eqOrIsNull("idFilial", idFilial));
		}


		return (NfeNaoConfirmada) criteria.uniqueResult();
	}
	
	
	/**
	 * retorna Verdadeiro caso Nfe NÃO tenha sido confirmada caso contrario retorna falso
	 * @param chave
	 * @param idEmpresa
	 * @param idFilial
	 * @return boolean
	 */
	public boolean nfeNaoConfirmadaEncontrada(String chave, Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();
		
			criteria.add(Restrictions.and(
				Restrictions.eq("chNfe", chave),
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("idEmpresa", idEmpresa)));
		if (idFilial != null){
			criteria.add(Restrictions.eq("idFilial", idFilial));
		}
		
		if (criteria.uniqueResult() == null){
			return false;
		}else{
			NfeNaoConfirmada nfe = (NfeNaoConfirmada) criteria.uniqueResult();
			if (nfe.isConfirmada()) {
				return true;
			}else {
				return false;
			}
		}
	}
	
	/**
	 * retorna Verdadeiro caso Nfe NÃO tenha sido salva na base de dados, caso contrario retorna falso
	 * @param chave
	 * @param idEmpresa
	 * @param idFilial
	 * @return boolean
	 */
	public boolean nfeNaoConfirmadaSalva(String chave, Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();
		
			criteria.add(Restrictions.and(
				Restrictions.eq("chNfe", chave),
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("idEmpresa", idEmpresa)));
		if (idFilial != null){
			criteria.add(Restrictions.eq("idFilial", idFilial));
		}
		
		if (criteria.uniqueResult() == null){
			return false;
		}else{
			NfeNaoConfirmada nfe = (NfeNaoConfirmada) criteria.uniqueResult();
			if (nfe != null) {
				return true;
			}else {
				return false;
			}
		}
	}
	
	public List<NfeNaoConfirmada> listaDeNaoConfirmadas(Long idEmpresa,Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<NfeNaoConfirmada> criteria = builder.createQuery(NfeNaoConfirmada.class);
		
		Root<NfeNaoConfirmada> fromNota = criteria.from(NfeNaoConfirmada.class);
		
		List<Predicate> conditions = new ArrayList<>();
		Predicate nfe =  builder.equal(fromNota.get("confirmada"),false);
		
		conditions.add(nfe);
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromNota.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromNota.get("idEmpresa"),idEmpresa));
			}else{
				conditions.add(builder.equal(fromNota.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(fromNota.get("idFilial"),idFilial));
			}
		}
		criteria.select(fromNota.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		
		TypedQuery<NfeNaoConfirmada> typedQuery = this.getEntityManager().createQuery(criteria);
		
		
		return typedQuery.getResultList();
	}
}
