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

import org.hibernate.HibernateError;

import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.financeiro.Credito;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CreditoRepository extends GenericRepositoryEmpDS<Credito, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8515025566147108013L;
	
	public Credito retornaCredito(Cliente cliente,Fornecedor fornecedor,Colaborador colaborador,Long pegaIdEmpresa,Long pegaIdFilial){
		EntityGraph<Credito> entityGraph = this.getEntityManager().createEntityGraph(Credito.class);
		entityGraph.addAttributeNodes("cliente","fornecedor","colaborador");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Credito> criteria = builder.createQuery(Credito.class);

		Root<Credito> formRecebimento = criteria.from(Credito.class);
		
		List<Predicate> conditions = new ArrayList<>();
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formRecebimento.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formRecebimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formRecebimento.get("idFilial"),pegaIdFilial));
			}
		}
		
		if (cliente != null) {
			conditions.add(builder.equal(formRecebimento.get("cliente"),cliente));
		}else {
			conditions.add(builder.isNull(formRecebimento.get("cliente")));
		}
		if (fornecedor != null) {
			conditions.add(builder.equal(formRecebimento.get("fornecedor"),fornecedor));
		}else {
			conditions.add(builder.isNull(formRecebimento.get("fornecedor")));
		}
		if (colaborador != null) {				
			conditions.add(builder.equal(formRecebimento.get("colaborador"),colaborador));
		}else {
			conditions.add(builder.isNull(formRecebimento.get("colaborador")));
		}
		
		
		criteria.select(formRecebimento.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Credito> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		if (typedQuery.getResultList().size() == 1) {
			return typedQuery.getSingleResult();
		}else {
			if (typedQuery.getResultList().size() == 0) {
				return new Credito();
			}else {
				throw new HibernateError("resultado possui mais de 1 registro!");
			}
		}
	}
	
	public List<Credito> retornaListaCredito(Cliente cliente,Fornecedor fornecedor,Colaborador colaborador,Long pegaIdEmpresa,Long pegaIdFilial){
		EntityGraph<Credito> entityGraph = this.getEntityManager().createEntityGraph(Credito.class);
		entityGraph.addAttributeNodes("cliente","fornecedor","colaborador");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Credito> criteria = builder.createQuery(Credito.class);

		Root<Credito> formRecebimento = criteria.from(Credito.class);
		
		List<Predicate> conditions = new ArrayList<>();
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formRecebimento.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(formRecebimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formRecebimento.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(formRecebimento.get("idFilial"),pegaIdFilial));
			}
		}
		
		if (cliente != null) {
			conditions.add(builder.equal(formRecebimento.get("cliente"),cliente));
		}else {
			if (fornecedor != null) {
				conditions.add(builder.equal(formRecebimento.get("fornecedor"),fornecedor));
			}else {
				if (colaborador != null) {
					conditions.add(builder.equal(formRecebimento.get("colaborador"),colaborador));
				}else {
					conditions.add(builder.isNull(formRecebimento.get("cliente")));
					conditions.add(builder.isNull(formRecebimento.get("fornecedor")));
					conditions.add(builder.isNull(formRecebimento.get("colaborador")));
				}
			}
		}
		
		
		criteria.select(formRecebimento.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Credito> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
}
