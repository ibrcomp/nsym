package br.com.nsym.domain.model.repository.financeiro;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.financeiro.Banco;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class BancoRepository extends GenericRepositoryEmpDS<Banco, Long>{
	/**
	 *
	 */
	private static final long serialVersionUID = 5171701802856949426L;
	
	
public Banco pegaBanco(Long id,Long pegaIdEmpresa,Long  pegaIdFilial) {
		
//		EntityGraph<Banco> entityGraph = this.getEntityManager().createEntityGraph(Banco.class);
//		entityGraph.addAttributeNodes("listaRecebimentoCaixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Banco> criteria = builder.createQuery(Banco.class);

		Root<Banco> formPedido = criteria.from(Banco.class);
//		formPedido.fetch("listaRecebimentoCaixa",JoinType.LEFT);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("id"),id));
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formPedido.get("idFilial")));
			}else{
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
			}
		}
		
//		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		
		TypedQuery<Banco> typedQuery = this.getEntityManager().createQuery(criteria);
//		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		if (typedQuery.getResultList().isEmpty() || typedQuery.getResultList().size() >=2) {
			return null;
		}else {
			return typedQuery.getSingleResult();
		}
	}
	
	

}
