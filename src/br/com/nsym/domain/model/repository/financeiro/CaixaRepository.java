package br.com.nsym.domain.model.repository.financeiro;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.tools.StatusCaixa;
import br.com.nsym.domain.model.entity.tools.CaixaFinalidade;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CaixaRepository extends GenericRepositoryEmpDS<Caixa, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5360340577753517740L;
	
	public List<Caixa> pegaCaixasEmAbertoUsuario(String name,LocalDate perido, StatusCaixa abe,Long pegaIdEmpresa, Long pegaIdFilial) {
		EntityGraph<Caixa> entityGraph = this.getEntityManager().createEntityGraph(Caixa.class);
		entityGraph.addAttributeNodes("saldoCaixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Caixa> criteria = builder.createQuery(Caixa.class);

		Root<Caixa> formPedido = criteria.from(Caixa.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
//		conditions.add(builder.between(formPedido.get("dataAbertura"),perido,LocalDate.now()));
		conditions.add(builder.equal(formPedido.get("usuario"),name));
		conditions.add(builder.equal(formPedido.get("statusCaixa"),abe));
		
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
		TypedQuery<Caixa> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}

	public List<Caixa> pegaCaixasEmAberto(String name,LocalDate perido, StatusCaixa abe,Long pegaIdEmpresa, Long pegaIdFilial) {
		EntityGraph<Caixa> entityGraph = this.getEntityManager().createEntityGraph(Caixa.class);
		entityGraph.addAttributeNodes("saldoCaixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Caixa> criteria = builder.createQuery(Caixa.class);

		Root<Caixa> formPedido = criteria.from(Caixa.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		if (abe != StatusCaixa.Abe) {
			conditions.add(builder.between(formPedido.get("dataAbertura"),perido,LocalDate.now()));
		}
		conditions.add(builder.equal(formPedido.get("usuario"),name));
		conditions.add(builder.equal(formPedido.get("statusCaixa"),abe));
		
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
		TypedQuery<Caixa> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		return typedQuery.getResultList();
	}
	/**
	 *  consulta a base de dados retornando o caixa com saldo de caixa em modo lazy ou a lista de recebiveis em modo lazy
	 * @param name = nome do usuario
	 * @param now = LocalDate data abertura
	 * @param status = StatusCaixa status do caixa
	 * @param pegaIdEmpresa = id da empresa
	 * @param pegaIdFilial = id da filial
	 * @param id = sem utilidade
	 * @param tipo 
	 * 	( rece = tras o saldo de caixa em modo lazy
	 * 	 fech = tras a lista de recebimento em modo lazy )
	 * @return
	 */
	public Caixa pegaCaixaAbertoUsuario(String name, LocalDate now,StatusCaixa status, Long pegaIdEmpresa, Long pegaIdFilial,Long id,CaixaFinalidade tipo) {
		// TODO Auto-generated method stub
		EntityGraph<Caixa> entityGraph = this.getEntityManager().createEntityGraph(Caixa.class);
		if (tipo == CaixaFinalidade.rece) {
			entityGraph.addAttributeNodes("saldoCaixa");
		}else {
			if (tipo == CaixaFinalidade.fech) {
				entityGraph.addAttributeNodes("listaRecebimentoCaixa");
			}
		
		}
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Caixa> criteria = builder.createQuery(Caixa.class);

		Root<Caixa> formPedido = criteria.from(Caixa.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("usuario"),name));
		conditions.add(builder.equal(formPedido.get("statusCaixa"),status));
		
		if (status != StatusCaixa.Abe) {
			conditions.add(builder.equal(formPedido.get("dataAbertura"),now));
		}
		
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
		TypedQuery<Caixa> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		if (typedQuery.getResultList().isEmpty() || typedQuery.getResultList().size() >=2) {
			return null;
		}else {
			return typedQuery.getSingleResult();
		}
	}
	
	
	public List<Caixa> pegaListaCaixaAbertoUsuario(String name, LocalDate now,StatusCaixa status, Long pegaIdEmpresa, Long pegaIdFilial, Long id) {
		// TODO Auto-generated method stub
		EntityGraph<Caixa> entityGraph = this.getEntityManager().createEntityGraph(Caixa.class);
		entityGraph.addAttributeNodes("listaRecebimentoCaixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Caixa> criteria = builder.createQuery(Caixa.class);

		Root<Caixa> formPedido = criteria.from(Caixa.class);
//		if (status == StatusCaixa.Abe) {
			formPedido.fetch("listaRecebimentoCaixa",JoinType.LEFT);
//		}else {
//			formPedido.fetch("listaFechamentoCaixa",JoinType.LEFT);
//		}
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("usuario"),name));
		conditions.add(builder.equal(formPedido.get("statusCaixa"),status));
		conditions.add(builder.equal(formPedido.get("dataAbertura"),now));
		if (id != null) {
			conditions.add(builder.equal(formPedido.get("id"),id));
		}
		
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
		TypedQuery<Caixa> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		
		return typedQuery.getResultList();
		
	}
	
	public Caixa pegaCaixaAbertoUsuarioParaFechamento(String name, LocalDate now,StatusCaixa status, Long pegaIdEmpresa, Long pegaIdFilial) {
		// TODO Auto-generated method stub
		EntityGraph<Caixa> entityGraph = this.getEntityManager().createEntityGraph(Caixa.class);
		entityGraph.addAttributeNodes("listaRecebimentoCaixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Caixa> criteria = builder.createQuery(Caixa.class);

		Root<Caixa> formPedido = criteria.from(Caixa.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formPedido.get("usuario"),name));
		conditions.add(builder.equal(formPedido.get("statusCaixa"),status));
		conditions.add(builder.equal(formPedido.get("dataAbertura"),now));
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
		TypedQuery<Caixa> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		if (typedQuery.getResultList().isEmpty() || typedQuery.getResultList().size() >=2) {
			return null;
		}else {
			return typedQuery.getSingleResult();
		}
	}
	
	public Page<Caixa> pegaListaCaixaLazy(LocalDate inicio, LocalDate fim, StatusCaixa status, Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest,String filtro , String pesquisa) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Caixa> criteria = builder.createQuery(Caixa.class);

		Root<Caixa> formPedido = criteria.from(Caixa.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.between(formPedido.get("dataFechamento"),inicio,fim));
		
		if (filtro != null){
			if (!filtro.isEmpty() ) {
				conditions.add(builder.like(builder.upper(formPedido.get(filtro)),"%"+pesquisa.toUpperCase()+"%")); // garantindo que tudo estja em maiuscula para localizar o termo
			}
		}
		if (status != null) {
			conditions.add(builder.equal(formPedido.get("statusCaixa"),status));
		}
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
		
		
		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Caixa.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();
		
		criteria.select(formPedido.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		
		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(formPedido.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(formPedido.get(pageRequest.getSortField())));

		}
		
		criteria.distinct(true);
		TypedQuery<Caixa> typedQuery = this.getEntityManager().createQuery(criteria);
		
		// paginamos
				typedQuery.setFirstResult(pageRequest.getFirstResult());
				typedQuery.setMaxResults(pageRequest.getPageSize());
		return new Page<>(typedQuery.getResultList(),totalRows);
		
	}
	
	public Caixa pegaCaixa(Long id,Long pegaIdEmpresa,Long  pegaIdFilial) {
		
		EntityGraph<Caixa> entityGraph = this.getEntityManager().createEntityGraph(Caixa.class);
		entityGraph.addAttributeNodes("listaRecebimentoCaixa");
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Caixa> criteria = builder.createQuery(Caixa.class);

		Root<Caixa> formPedido = criteria.from(Caixa.class);
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
		
		TypedQuery<Caixa> typedQuery = this.getEntityManager().createQuery(criteria);
		typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
		if (typedQuery.getResultList().isEmpty() || typedQuery.getResultList().size() >=2) {
			return null;
		}else {
			return typedQuery.getSingleResult();
		}
	}
	
}
