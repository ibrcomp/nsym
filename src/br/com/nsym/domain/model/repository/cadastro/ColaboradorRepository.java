package br.com.nsym.domain.model.repository.cadastro;

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
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ColaboradorRepository extends GenericRepositoryEmpDS<Colaborador, Long>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7622372654125828320L;

	public List<Colaborador> listColaboradorAtiva(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return criteria.list();
		}
	}
	
	public List<Colaborador> pesquisaTexto(String procure,Long idEmpresa, Long idFilial) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Colaborador> criteria = builder.createQuery(Colaborador.class);
		
		Root<Colaborador> fromColaborador = criteria.from(Colaborador.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.like(builder.lower(fromColaborador.<String>get("nome")),"%"+procure.toLowerCase()+"%"));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromColaborador.get("idEmpresa")));
		}else{
			conditions.add(builder.equal(fromColaborador.get("idEmpresa"),idEmpresa));			
		}
		conditions.add(builder.equal(fromColaborador.get("isDeleted"), false));
		
		criteria.select(fromColaborador.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Colaborador> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return  typedQuery.getResultList();
	}
	
	public List<Colaborador> listaColaboradorPorFilial(boolean isDeleted,Long idEmpresa, Long idFilial) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Colaborador> criteria = builder.createQuery(Colaborador.class);
		
		Root<Colaborador> fromControle = criteria.from(Colaborador.class);

		List<Predicate> conditions = new ArrayList<>();
		

		conditions.add(builder.equal(fromControle.get("isDeleted"), isDeleted));

		if (idEmpresa == null){
			conditions.add(builder.isNull(fromControle.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromControle.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(fromControle.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromControle.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(fromControle.get("idFilial"),idFilial));
			}
		}
		
		criteria.select(fromControle.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Colaborador> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		

		return typedQuery.getResultList();

		
	}
	
	public Colaborador pegaColaboradorPorID(Long id) {
//		EntityGraph<Colaborador> entityGraph = this.getEntityManager().createEntityGraph(Colaborador.class);
//		entityGraph.addAttributeNodes("controle","emitente","transacao","destino","listaItensPedido","pagamento");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Colaborador> criteria = builder.createQuery(Colaborador.class);

		Root<Colaborador> formPedido = criteria.from(Colaborador.class);

		List<Predicate> conditions = new ArrayList<>();

		conditions.add(builder.equal(formPedido.get("id"),id));

		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Colaborador> typedQuery = this.getEntityManager().createQuery(criteria);
//		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		return typedQuery.getSingleResult();

	}
	
	public Page<Colaborador> pageListaColaboradorLazy(Boolean isDeleted,Long idEmpresa, Long idFilial, PageRequest pageRequest,String filtro , String pesquisa ){
//		EntityGraph<Cliente> entityGraph = this.getEntityManager().createEntityGraph(Cliente.class);
//		entityGraph.addAttributeNodes("caixa");
		
	
	CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
	CriteriaQuery<Colaborador> criteria =  builder.createQuery(getPersistentClass());

	Root<Colaborador> formCliente = criteria.from(Colaborador.class);
	
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
		if (idFilial == null){
			conditions.add(builder.equal(formCliente.get("idEmpresa"),idEmpresa));
			conditions.add(builder.isNull(formCliente.get("idFilial")));
		}else{
			conditions.add(builder.equal(formCliente.get("idEmpresa"),idEmpresa));
			conditions.add(builder.equal(formCliente.get("idFilial"),idFilial));
		}
	}

	
	// projetamos para pegar o total de paginas possiveis

			CriteriaQuery<Long> cq = builder.createQuery(Long.class);
			cq.select(builder.count(cq.from(Colaborador.class)));
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
			
			TypedQuery<Colaborador> typedQuery = this.getEntityManager().createQuery(criteria);


			// paginamos
			typedQuery.setFirstResult(pageRequest.getFirstResult());
			typedQuery.setMaxResults(pageRequest.getPageSize());
	
	
//	typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
	return new Page<>(typedQuery.getResultList(), totalRows);
}

	
}
