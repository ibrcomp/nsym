package br.com.nsym.domain.model.repository.cadastro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ClienteRepository extends GenericRepositoryEmpDS<Cliente, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1452433858181582337L;

	public List<Cliente> listClienteAtiva(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return criteria.list();
		}
	}

	public Cliente update (Cliente emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}
	
	public List<Cliente> localizaClientePorRazaoSocial(String razao,Long idEmpresa){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Cliente> criteria =  builder.createQuery(getPersistentClass());

		Root<Cliente> formCliente = criteria.from(Cliente.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formCliente.get("isDeleted"), false);
		conditions.add(itemPedido);
		
		if (razao != null){
			if (!razao.isEmpty()){
				conditions.add(builder.like(builder.lower(formCliente.<String>get("razaoSocial")),"%"+razao.toLowerCase()+"%"));
			}
		}
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formCliente.get("idEmpresa")));
		}else{
				conditions.add(builder.equal(formCliente.get("idEmpresa"),idEmpresa));
		}
		
		criteria.select(formCliente.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Cliente> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
	}
	
	public List<Cliente> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
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
	
	public boolean passaporteCadastrado(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		
		if (idFilial == null){
			criteria.add(Restrictions.and(
					Restrictions.ilike("idEstrangeiro", dep),
					Restrictions.eq("isDeleted", false),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		}else{
		criteria.add(Restrictions.and(
						Restrictions.ilike("idEstrangeiro", dep),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
						Restrictions.eqOrIsNull("idFilial", idFilial)));
		}
			if (criteria.list().isEmpty()){
				return false;
			}else{
				return true;
			}
		
	}
	/**
	 *  Retorna se cliente ja esta cadastrado
	 * @param doc =  CPF ou CNPJ
	 * @param idEmpresa
	 * @return True = Cadastrado False = Não Cadastrado
	 */
	public boolean  clienteCadastrado(String doc,Long idEmpresa){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Cliente> criteria =  builder.createQuery(getPersistentClass());

		Root<Cliente> formCliente = criteria.from(Cliente.class);
		
		List<Predicate> conditions = new ArrayList<>();

		Predicate itemPedido = builder.equal(formCliente.get("isDeleted"), false);
		conditions.add(itemPedido);
		
		if (doc != null){
			if (!doc.isEmpty()){
				conditions.add(builder.or(builder.like(formCliente.get("cnpj"),"%"+doc+"%"),builder.like(formCliente.get("cpf"),"%"+doc+"%")));
			}
		}
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formCliente.get("idEmpresa")));
		}else{
			conditions.add(builder.equal(formCliente.get("idEmpresa"),idEmpresa));
		}

		
		criteria.select(formCliente.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Cliente> typedQuery = this.getEntityManager().createQuery(criteria);
		
		if (typedQuery.getResultList().isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
//	public boolean clienteCadastrado(String doc,Long idEmpresa, Long idFilial){
//		final Criteria criteria = this.createCriteria();
//		
//		criteria.add(Restrictions.or(
//						Restrictions.eqOrIsNull("cnpj", doc),
//						Restrictions.eqOrIsNull("cpf", doc))); 
//		if (idFilial == null){
//			criteria.add(Restrictions.and(
//					Restrictions.eq("isDeleted", false),
//					Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
//		}else{
//		criteria.add(Restrictions.and(
//						Restrictions.eq("isDeleted", false),
//						Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
//						Restrictions.eqOrIsNull("idFilial", idFilial)));
//		}
//			if (criteria.list().isEmpty()){
//				return false;
//			}else{
//				return true;
//			}
//		
//	}
	
	public Page<Cliente> pageListaClienteLazy(Boolean isDeleted,Long idEmpresa, Long idFilial, PageRequest pageRequest,String filtro , String pesquisa ){
//		EntityGraph<Cliente> entityGraph = this.getEntityManager().createEntityGraph(Cliente.class);
//		entityGraph.addAttributeNodes("caixa");
		
	
	CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
	CriteriaQuery<Cliente> criteria =  builder.createQuery(getPersistentClass());

	Root<Cliente> formCliente = criteria.from(Cliente.class);
	
	List<Predicate> conditions = new ArrayList<>();

	Predicate itemPedido = builder.equal(formCliente.get("isDeleted"), false);
	conditions.add(itemPedido);
	
	if (filtro != null){
		if (!filtro.isEmpty()){
			conditions.add(builder.like(builder.lower(formCliente.<String>get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
		}
	}
	
	if (idEmpresa == null){
		conditions.add(builder.isNull(formCliente.get("idEmpresa")));
	}else{
			conditions.add(builder.equal(formCliente.get("idEmpresa"),idEmpresa));
	}
	
	
	
	// projetamos para pegar o total de paginas possiveis

			CriteriaQuery<Long> cq = builder.createQuery(Long.class);
			cq.select(builder.count(cq.from(Cliente.class)));
			this.getEntityManager().createQuery(cq);
			cq.where(conditions.toArray(new Predicate[0]));

			final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();
			
			criteria.select(formCliente.alias("p"));
			criteria.where(conditions.toArray(new Predicate[]{}));
			criteria.distinct(true);


			if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
				criteria.orderBy(builder.asc(formCliente.get(pageRequest.getSortField())));

			} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
				criteria.orderBy(builder.desc(formCliente.get(pageRequest.getSortField())));

			}
			
			TypedQuery<Cliente> typedQuery = this.getEntityManager().createQuery(criteria);


			// paginamos
			typedQuery.setFirstResult(pageRequest.getFirstResult());
			typedQuery.setMaxResults(pageRequest.getPageSize());
	
	
//	typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
	return new Page<>(typedQuery.getResultList(), totalRows);
}
}
