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

import br.com.nsym.domain.model.entity.fiscal.CFOP;
import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.Tributos;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class TributosRepository extends GenericRepositoryEmpDS<Tributos, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Tributos> listTributosAtivo() {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null) {
			return null;
		} else {
			return criteria.list();
		}
	}

	public boolean isEspecial(CFOP cfop,Long filial,Long empresa){
		final Criteria criteria = this.createCriteria();
		if (cfop !=null ){
			criteria.add(Restrictions.and(
					Restrictions.eq("isDeleted", false),
					Restrictions.eq("cfop", cfop),
					Restrictions.eqOrIsNull("idEmpresa", empresa),
					Restrictions.eqOrIsNull("idFilial", filial)));
		}
		if (criteria.list().isEmpty()){
			return false;
		}else{
			return true;
		}
	}

	public Tributos localizaTributos(CFOP cfop,Long empresa,Long filial){
		final Criteria criteria = this.createCriteria();
		if ( cfop != null){
			criteria.add(Restrictions.and(
					Restrictions.eq("isDeleted", false),
					Restrictions.eq("cfop", cfop),
					Restrictions.eqOrIsNull("idEmpresa", empresa),
					Restrictions.eqOrIsNull("idFilial", filial)));
			if (criteria.list().size() > 1 || criteria.list().isEmpty()){
				return null;
			}else{
				return (Tributos) criteria.uniqueResult();
			}
		}else{
			return null;
		}
	}

	public List<Tributos> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.ilike("descricao", "%"+dep+"%"),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();
	}

	public List<Tributos> listaTodosTributosAtivos(Long idEmpresa,Long idFilial){
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Tributos> criteria = builder.createQuery(Tributos.class);

		Root<Tributos> formPedido = criteria.from(Tributos.class);
//		formPedido.fetch("controle",JoinType.LEFT);

		List<Predicate> conditions = new ArrayList<>();

//		Predicate itemPedido = builder.between(formPedido.get("dataEmissao"),dataIni,dataFim);
//
//		conditions.add(itemPedido);
		if (idEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),idFilial));
			}
		}

		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));


		criteria.distinct(true);
		TypedQuery<Tributos> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(
//				Restrictions.eq("isDeleted", false),
//				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
//				Restrictions.eqOrIsNull("idFilial", idFilial)));
//		return criteria.list();
	}
}
