package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.nfe.Nfe;
import br.com.nsym.domain.model.entity.fiscal.nfe.NfeRecebida;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class NfeRecebidaRepository extends GenericRepositoryEmpDS<NfeRecebida, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<NfeRecebida> listNfeAtivo(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}

	public boolean jaExiste(String nome){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.ilike("cfop", nome));
		if (criteria.list().isEmpty() == true){
			return false;
		}else {
			return true;
		}
	}

	public List<NfeRecebida> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.ilike("nfe", "%"+dep+"%"),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();
	}
/**
 *  Metodo que retorna a nfeRecebida com a lista de itens preenchida
 * @param chave
 * @param idEmpresa
 * @return NfeRecebida
 */
	public NfeRecebida pegaNfe(String chave, Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("chaveAcesso", chave),
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		criteria.setFetchMode("listaItemNfe", FetchMode.JOIN);


		return (NfeRecebida) criteria.uniqueResult();
	}
	
	/**
	 *  Método que retorna a NFE com a lista de parcelas preenchida
	 * @param id da nfe
	 * @param idEmpresa 
	 * @param idFilial
	 * @return Nfe
	 */
	public NfeRecebida pegaNfeRecebidaComParcelas(Long id, Long idEmpresa,Long idFilial) {
		EntityGraph<NfeRecebida> entityGraph = this.getEntityManager().createEntityGraph(NfeRecebida.class);
		entityGraph.addAttributeNodes("listaParcelas");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<NfeRecebida> criteria = builder.createQuery(NfeRecebida.class);
		

		Root<NfeRecebida> formPedido = criteria.from(NfeRecebida.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formPedido.get("id"),id);

		conditions.add(itemPedido);
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


	//	criteria.select(builder.sum(formPedido.get("valorTotalPedido").as(BigDecimal.class)));
		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<NfeRecebida> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.loadgraph", entityGraph);
		return typedQuery.getSingleResult();
	}

}


