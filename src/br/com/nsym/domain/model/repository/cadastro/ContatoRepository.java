package br.com.nsym.domain.model.repository.cadastro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.TipoCadastro;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ContatoRepository extends GenericRepositoryEmpDS<Contato, Long> {


	/**
	 *
	 */
	private static final long serialVersionUID = 858635523689318287L;


	public Contato update (Contato emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}

	
	public Page<Contato> listaContatoLazyComFiltro(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial, Long id,TipoCadastro tipo, PageRequest pageRequest,String filtro , String pesquisa,Boolean porFilial ){
		EntityGraph<Contato> entityGraph = this.getEntityManager().createEntityGraph(Contato.class);
		entityGraph.addAttributeNodes("fone");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Contato> criteria = builder.createQuery(getPersistentClass());

		Root<Contato> fromProdutos = criteria.from(getPersistentClass());


		List<Predicate> conditions = new ArrayList<>();
		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate filialNull = builder.isNull(fromProdutos.get("idFilial"));
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		
		if (tipo.equals(TipoCadastro.COLAB)) {
			conditions.add(builder.equal(fromProdutos.get("colaborador"), id));
		}else if (tipo.equals(TipoCadastro.FOR)) {
			conditions.add(builder.equal(fromProdutos.get("fornecedor"), id));
		}else if (tipo.equals(TipoCadastro.CLI)) {
			conditions.add(builder.equal(fromProdutos.get("cliente"), id));
		}else if (tipo.equals(TipoCadastro.TRANSP)) {
			conditions.add(builder.equal(fromProdutos.get("transportadora"), id));
		}else if (tipo.equals(TipoCadastro.EMP)) {
			conditions.add(builder.equal(fromProdutos.get("empresa"), id));
		}else if (tipo.equals(TipoCadastro.FIL)) {
			conditions.add(builder.equal(fromProdutos.get("filial"), id));
		}
			
		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				if (porFilial) {
					conditions.add(empresa);
					conditions.add(filialNull);
				}else {
					conditions.add(empresa);
				}
			}
		}else{
			if (porFilial) {
				conditions.add(empresa);
				conditions.add(filial);
			}else {
				conditions.add(empresa);
				}
		}
		
		if (filtro != null){
			if (!filtro.isEmpty()){
				conditions.add(builder.like(builder.lower(fromProdutos.<String>get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(getPersistentClass())));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[0]));


		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromProdutos.<String>get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromProdutos.<String>get(pageRequest.getSortField())));

		}
		criteria.distinct(true);
		TypedQuery<Contato> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());

		// montamos o resultado paginado
		return new Page<>(typedQuery.getResultList(), totalRows);
	}
	
	public List<Contato> procuraContatoEmitente(Empresa empresaID,Filial filialID){
		EntityGraph<Contato> entityGraph = this.getEntityManager().createEntityGraph(Contato.class);
		entityGraph.addAttributeNodes("fone");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Contato> criteria = builder.createQuery(getPersistentClass());

		Root<Contato> fromProdutos = criteria.from(getPersistentClass());


		List<Predicate> conditions = new ArrayList<>();
		Predicate filialNull = builder.isNull(fromProdutos.get("idFilial"));
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));
		
		if  (filialID == null) {
			if (empresaID == null) {
				conditions.add(empresaNull);
			}else {
				conditions.add(builder.equal(fromProdutos.get("empresa"), empresaID));
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"), empresaID.getId()));
			}
			conditions.add(filialNull);
		}else {
			if (empresaID != null) {
				conditions.add(builder.equal(fromProdutos.get("empresa"), empresaID));
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"), empresaID.getId()));
			}
			conditions.add(builder.equal(fromProdutos.get("filial"), filialID));
			conditions.add(builder.equal(fromProdutos.get("idFilial"), filialID.getId()));
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Contato> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		System.out.println("lista de contatos possui: " + typedQuery.getResultList().size() + " registro(s)!");
		return typedQuery.getResultList();
	}
	
	public List<Contato> procuraContatoDestino(Cliente cliente,Fornecedor fornecedor, Colaborador colaborador , Empresa empresaID , Filial filialID){
			EntityGraph<Contato> entityGraph = this.getEntityManager().createEntityGraph(Contato.class);
			entityGraph.addAttributeNodes("fone");
			CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Contato> criteria = builder.createQuery(getPersistentClass());

			Root<Contato> fromProdutos = criteria.from(getPersistentClass());


			List<Predicate> conditions = new ArrayList<>();
			Predicate filialNull = builder.isNull(fromProdutos.get("idFilial"));
			Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

			if (cliente != null) {
				conditions.add(builder.equal(fromProdutos.get("cliente"), cliente));
			}
			if (fornecedor != null) {
				conditions.add(builder.equal(fromProdutos.get("fornecedor"), fornecedor));
			}
			if (colaborador != null) {
				conditions.add(builder.equal(fromProdutos.get("colaborador"), colaborador));
			}
				
//			if (empresaID == null){
//				conditions.add(empresaNull);
//			}else {
//				if (filialID != null) {
//					conditions.add(builder.equal(fromProdutos.get("idEmpresa"), idEmpresa));
//					conditions.add(builder.equal(fromProdutos.get("idFilial"), idFilial));
////					conditions.add(builder.equal(fromProdutos.get("filial"), filialID));
//				}else {
//					conditions.add(builder.equal(fromProdutos.get("idEmpresa"), idEmpresa));
////					conditions.add(builder.equal(fromProdutos.get("empresa"), empresaID));
//					conditions.add(filialNull);
//				}
//			}	
			criteria.select(fromProdutos.alias("p"));
			criteria.where(conditions.toArray(new Predicate[]{}));
			criteria.distinct(true);
			TypedQuery<Contato> typedQuery = this.getEntityManager().createQuery(criteria);
			typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
			
			return typedQuery.getResultList();
	}
}
