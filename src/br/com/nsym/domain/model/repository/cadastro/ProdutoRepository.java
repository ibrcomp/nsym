package br.com.nsym.domain.model.repository.cadastro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.entity.tools.Finalidade;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ProdutoRepository extends GenericRepositoryEmpDS<Produto, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean jaExiste(String nome, Long idEmpresa){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);
		
		Root<Produto> fromProdutos = criteria.from(Produto.class);
		List<Predicate> conditions = new ArrayList<>();
		conditions.add(builder.equal(builder.lower(fromProdutos.get("referencia")),nome.toLowerCase()));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
		}else{
			conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria); 
		Produto prodTemp = new Produto();
		try {
			prodTemp = (Produto) typedQuery.getSingleResult();
			if (prodTemp != null) {
				return true;
			}else{
				return false;
			}
		}catch (NoResultException n) {
			return false;
		}
		
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(
//				Restrictions.eq("referencia", nome),
//				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
//		if (criteria.list().isEmpty() == true){
//			return false;
//		}else {
//			return true;
//		}
	}
	/*
	 * Inutil!!!!
	 */
//	public boolean barrasExiste(String barras, Long idEmpresa){
//		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
//		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);
//		
//		Root<Produto> fromProdutos = criteria.from(Produto.class);
//		List<Predicate> conditions = new ArrayList<>();
//		conditions.add(builder.equal(builder.lower(fromProdutos.get("barras")),barras.toLowerCase()));
//		if (idEmpresa == null){
//			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
//		}else{
//			conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//		}
//
//		criteria.select(fromProdutos.alias("p"));
//		criteria.where(conditions.toArray(new Predicate[]{}));
//		criteria.distinct(true);
//		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria); 
//		Produto prodTemp = new Produto();
//		try {
//			prodTemp = (Produto) typedQuery.getSingleResult();
//			if (prodTemp != null) {
//				return true;
//			}else{
//				return false;
//			}
//		}catch (NoResultException n) {
//			return false;
//		}
		
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(
//				Restrictions.eq("barras", barras),
//				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
//		if (criteria.list().isEmpty() == true){
//			return false;
//		}else {
//			return true;
//		}
//	}

	public Produto pegaProdutoComFornecedores(Long id, Long idEmpresa, Long idPrecoFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);
		fromProdutos.fetch("fornecedores",JoinType.LEFT);
		fromProdutos.fetch("departamento",JoinType.LEFT);
		fromProdutos.fetch("secao",JoinType.LEFT);
		fromProdutos.fetch("subSecao",JoinType.LEFT);
		fromProdutos.fetch("fabricante",JoinType.LEFT);
		fromProdutos.fetch("grade",JoinType.LEFT);
		fromProdutos.fetch("tributoEspecial",JoinType.LEFT);
		fromProdutos.fetch("tributoEspecialDevolucao",JoinType.LEFT);
//		fromProdutos.fetch("listaBarras",JoinType.LEFT);

		List<Predicate> conditions = new ArrayList<>();


		conditions.add(builder.equal(fromProdutos.get("id"), id));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
		}else{
			conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		
		Produto prodTemp = (Produto) typedQuery.getSingleResult();
		List<ProdutoCusto> listaTempCusto = new ArrayList<>();
		
		// gerando lista de custo apenas da filial/empresa
		for (Iterator<ProdutoCusto> it = prodTemp.getListaCustoProduto().iterator(); it.hasNext();){
			ProdutoCusto custo = it.next();
			if (custo.getIdFilial() == idPrecoFilial && custo.getIdEmpresa() == idEmpresa) {
				listaTempCusto.add(custo);
			}
		}
		prodTemp.setListaCustoProduto(listaTempCusto);


		// montamos o resultado paginado
		return  prodTemp;


	}

	public Page<Produto> pegaPageProdutoComCustoComEstoque(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial,Long precoFilialId, PageRequest pageRequest ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);

//				Join<Produto,BarrasEstoque> joinEstoques = (Join)fromProdutos.fetch("listaBarras",JoinType.LEFT);

		List<Predicate> conditions = new ArrayList<>();

		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		conditions.add(builder.equal(fromProdutos.get("finalidade"), Finalidade.Rev));

		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				conditions.add(empresa);
			}
		}else{
			conditions.add(empresa);
		}

		List<Predicate> cond = new ArrayList<>();


		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Produto.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[0]));


		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromProdutos.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromProdutos.get(pageRequest.getSortField())));

		}
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria);


		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		
		List<Produto> listaTempProduto = new ArrayList<Produto>();
		List<ProdutoCusto> listaTempCustoAd = new ArrayList<>();
		
		listaTempProduto = typedQuery.getResultList();
			for (Iterator<Produto> iterator = listaTempProduto.iterator(); iterator.hasNext();) {
				Produto prod = iterator.next();
				listaTempCustoAd = new ArrayList<ProdutoCusto>();
				for(Iterator<ProdutoCusto> produtoCustoIterator = prod.getListaCustoProduto().iterator(); produtoCustoIterator.hasNext();) {
					ProdutoCusto custo = produtoCustoIterator.next();
					if (custo.getIdFilial() == precoFilialId && custo.getIdEmpresa() == idEmpresa) {
						listaTempCustoAd.add(custo);
					}
				}
				prod.setListaCustoProduto(listaTempCustoAd);
			}

		// montamos o resultado paginado
		return new Page<>(listaTempProduto, totalRows);
	}
	
	public Page<Produto> pegaPageProdutoComCustoComEstoqueFabrica(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial,Long precoFilialId, PageRequest pageRequest ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);

//				Join<Produto,BarrasEstoque> joinEstoques = (Join)fromProdutos.fetch("listaBarras",JoinType.LEFT);

		List<Predicate> conditions = new ArrayList<>();

		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		conditions.add(builder.notEqual(fromProdutos.get("finalidade"), Finalidade.Rev));

		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				conditions.add(empresa);
			}
		}else{
			conditions.add(empresa);
		}

		List<Predicate> cond = new ArrayList<>();


		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Produto.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[0]));


		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(fromProdutos.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(fromProdutos.get(pageRequest.getSortField())));

		}
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria);


		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		
		List<Produto> listaTempProduto = new ArrayList<Produto>();
		List<ProdutoCusto> listaTempCustoAd = new ArrayList<>();
		
		listaTempProduto = typedQuery.getResultList();
			for (Iterator<Produto> iterator = listaTempProduto.iterator(); iterator.hasNext();) {
				Produto prod = iterator.next();
				listaTempCustoAd = new ArrayList<ProdutoCusto>();
				for(Iterator<ProdutoCusto> produtoCustoIterator = prod.getListaCustoProduto().iterator(); produtoCustoIterator.hasNext();) {
					ProdutoCusto custo = produtoCustoIterator.next();
					if (custo.getIdFilial() == precoFilialId && custo.getIdEmpresa() == idEmpresa) {
						listaTempCustoAd.add(custo);
					}
				}
				prod.setListaCustoProduto(listaTempCustoAd);
			}

		// montamos o resultado paginado
		return new Page<>(listaTempProduto, totalRows);
	}
	
	public Page<Produto> pegaPageMaterialComEstoqueComCustoComFiltro(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial,Long precoFilialId, PageRequest pageRequest,String filtro , String pesquisa ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);


		List<Predicate> conditions = new ArrayList<>();
		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		conditions.add(builder.notEqual(fromProdutos.get("finalidade"), Finalidade.Rev));

		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				conditions.add(empresa);
			}
		}else{
			conditions.add(empresa);
		}
		if (filtro != null){
			if (!filtro.isEmpty()){
				conditions.add(builder.like(builder.lower(fromProdutos.<String>get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Produto.class)));
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
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		
		List<Produto> listaTempProduto = new ArrayList<>();
		List<ProdutoCusto> listaTempCustoAd = new ArrayList<>();
		
		listaTempProduto = typedQuery.getResultList();
			for (Iterator<Produto> iterator = listaTempProduto.iterator(); iterator.hasNext();) {
				Produto prod = iterator.next();
				listaTempCustoAd = new ArrayList<ProdutoCusto>();
				for(Iterator<ProdutoCusto> produtoCustoIterator = prod.getListaCustoProduto().iterator(); produtoCustoIterator.hasNext();) {
					ProdutoCusto custo = produtoCustoIterator.next();
					if (custo.getIdFilial() == precoFilialId && custo.getIdEmpresa() == idEmpresa) {
						listaTempCustoAd.add(custo);
					}
				}
				prod.setListaCustoProduto(listaTempCustoAd);
			}

		// montamos o resultado paginado
		return new Page<>(listaTempProduto, totalRows);
	}

	public Page<Produto> pegaPageProdutoComEstoqueComCustoComFiltro(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial,Long precoFilialId, PageRequest pageRequest,String filtro , String pesquisa ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);

				Join<BarrasEstoque,Produto> joinEstoques = fromProdutos.join("listaBarras",JoinType.LEFT);

		List<Predicate> conditions = new ArrayList<>();

		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		conditions.add(builder.equal(fromProdutos.get("finalidade"), Finalidade.Rev));

		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				conditions.add(empresa);
			}
		}else{
			conditions.add(empresa);
		}

		if (!filtro.isEmpty()){
			if (filtro != null){
				conditions.add(builder.like(builder.lower(fromProdutos.<String>get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Produto.class)));
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
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		
		List<Produto> listaTempProduto = new ArrayList<>();
		List<ProdutoCusto> listaTempCustoAd = new ArrayList<>();
		
		listaTempProduto = typedQuery.getResultList();
			for (Iterator<Produto> iterator = listaTempProduto.iterator(); iterator.hasNext();) {
				Produto prod = iterator.next();
				listaTempCustoAd = new ArrayList<ProdutoCusto>();
				for(Iterator<ProdutoCusto> produtoCustoIterator = prod.getListaCustoProduto().iterator(); produtoCustoIterator.hasNext();) {
					ProdutoCusto custo = produtoCustoIterator.next();
					if (custo.getIdFilial() == precoFilialId && custo.getIdEmpresa() == idEmpresa) {
						listaTempCustoAd.add(custo);
					}
				}
				prod.setListaCustoProduto(listaTempCustoAd);
			}

		// montamos o resultado paginado
		return new Page<>(listaTempProduto, totalRows);
	}
	
	public Page<Produto> pegaPageProdutoComEstoqueComCustoComFiltroFabrica(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial,Long precoFilialId, PageRequest pageRequest,String filtro , String pesquisa ){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);

				Join<BarrasEstoque,Produto> joinEstoques = fromProdutos.join("listaBarras",JoinType.LEFT);

		List<Predicate> conditions = new ArrayList<>();

		Predicate filial = builder.equal(fromProdutos.get("idFilial"), idFilial);
		Predicate empresa = builder.equal(fromProdutos.get("idEmpresa"), idEmpresa);
		Predicate empresaNull = builder.isNull(fromProdutos.get("idEmpresa"));

		conditions.add(builder.equal(fromProdutos.get("isDeleted"), isDeleted));
		conditions.add(builder.notEqual(fromProdutos.get("finalidade"), Finalidade.Rev));

		if (idFilial == null){
			if (idEmpresa == null){
				conditions.add(empresaNull);
			}else{
				conditions.add(empresa);
			}
		}else{
			conditions.add(empresa);
		}

		if (!filtro.isEmpty()){
			if (filtro != null){
				conditions.add(builder.like(builder.lower(fromProdutos.<String>get(filtro)),"%"+pesquisa.toLowerCase()+"%"));
			}
		}

		// projetamos para pegar o total de paginas possiveis

		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(Produto.class)));
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
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria);

		// paginamos
		typedQuery.setFirstResult(pageRequest.getFirstResult());
		typedQuery.setMaxResults(pageRequest.getPageSize());
		
		List<Produto> listaTempProduto = new ArrayList<>();
		List<ProdutoCusto> listaTempCustoAd = new ArrayList<>();
		
		listaTempProduto = typedQuery.getResultList();
			for (Iterator<Produto> iterator = listaTempProduto.iterator(); iterator.hasNext();) {
				Produto prod = iterator.next();
				listaTempCustoAd = new ArrayList<ProdutoCusto>();
				for(Iterator<ProdutoCusto> produtoCustoIterator = prod.getListaCustoProduto().iterator(); produtoCustoIterator.hasNext();) {
					ProdutoCusto custo = produtoCustoIterator.next();
					if (custo.getIdFilial() == precoFilialId && custo.getIdEmpresa() == idEmpresa) {
						listaTempCustoAd.add(custo);
					}
				}
				prod.setListaCustoProduto(listaTempCustoAd);
			}

		// montamos o resultado paginado
		return new Page<>(listaTempProduto, totalRows);
	}

	
	public Page<Produto> pegaPageProdutoComCustoComFiltro(Boolean isDeleted, Boolean isBloked, Long idEmpresa, Long idFilial, PageRequest pageRequest,String filtro , String pesquisa ){
		final Criteria criteria = this.createCriteria();

		criteria.add(
				Restrictions.eq("isDeleted", isDeleted));
		if (idEmpresa == null){
			criteria.add(Restrictions.eqOrIsNull("idEmpresa", idEmpresa));
		}else{
			if (idFilial != null){
				criteria.add(Restrictions.and(Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
						Restrictions.eqOrIsNull("idFilial", idFilial)));
			}else{
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", idEmpresa));
			}
		}
		if (!filtro.isEmpty()){
			criteria.add(Restrictions.ilike(filtro, pesquisa, MatchMode.ANYWHERE));
		}
		criteria.setFetchMode("listaCustoProduto", FetchMode.JOIN);

		// projetamos para pegar o total de paginas possiveis
		criteria.setProjection(Projections.count("id"));

		final Long totalRows = (Long) criteria.uniqueResult();

		// limpamos a projection para que a criteria seja reusada
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// paginamos
		criteria.setFirstResult(pageRequest.getFirstResult());
		criteria.setMaxResults(pageRequest.getPageSize());

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.addOrder(Order.asc(pageRequest.getSortField()));
		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.addOrder(Order.desc(pageRequest.getSortField()));
		}

		// montamos o resultado paginado
		return new Page<>(criteria.list(), totalRows);
	}
	
	public Produto pegaMaterialRef(String ref, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);
		fromProdutos.fetch("tributoEspecial",JoinType.LEFT);
		fromProdutos.fetch("tributoEspecialDevolucao",JoinType.LEFT);



		List<Predicate> conditions = new ArrayList<>();
		
		Predicate referencia = builder.equal(builder.lower(fromProdutos.get("referencia")),ref.toLowerCase());

		conditions.add(referencia);
		conditions.add(builder.notEqual(fromProdutos.get("finalidade"),Finalidade.Rev));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.isNull(fromProdutos.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.equal(fromProdutos.get("idFilial"),idFilial));
			}
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		Produto prodTemp = new Produto();
		try {
			prodTemp = (Produto) typedQuery.getSingleResult();
			List<ProdutoCusto> listaTempCusto = new ArrayList<>();

			// gerando lista de custo apenas da filial/empresa
			for (Iterator<ProdutoCusto> it = prodTemp.getListaCustoProduto().iterator(); it.hasNext();){
				ProdutoCusto custo = it.next();
				if (custo.getIdFilial() == idFilial && custo.getIdEmpresa() == idEmpresa) {
					listaTempCusto.add(custo);
				}
			}
			prodTemp.setListaCustoProduto(listaTempCusto);
		}catch (NoResultException n) {
			prodTemp = null;
		}
		// montamos o resultado paginado
		return  prodTemp;

		
	}


	public Produto pegaProdutoRef(String ref, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);
		fromProdutos.fetch("tributoEspecial",JoinType.LEFT);
		fromProdutos.fetch("tributoEspecialDevolucao",JoinType.LEFT);



		List<Predicate> conditions = new ArrayList<>();
		
		Predicate referencia = builder.equal(builder.lower(fromProdutos.get("referencia")),ref.toLowerCase());


		conditions.add(referencia);
		conditions.add(builder.equal(fromProdutos.get("finalidade"),Finalidade.Rev));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.isNull(fromProdutos.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.equal(fromProdutos.get("idFilial"),idFilial));
			}
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		Produto prodTemp = new Produto();
		try {
			prodTemp = (Produto) typedQuery.getSingleResult();
			List<ProdutoCusto> listaTempCusto = new ArrayList<>();

			// gerando lista de custo apenas da filial/empresa
			for (Iterator<ProdutoCusto> it = prodTemp.getListaCustoProduto().iterator(); it.hasNext();){
				ProdutoCusto custo = it.next();
				if (custo.getIdFilial() == idFilial && custo.getIdEmpresa() == idEmpresa) {
					listaTempCusto.add(custo);
				}
			}
			prodTemp.setListaCustoProduto(listaTempCusto);
		}catch (NoResultException n) {
			prodTemp = null;
		}
		// montamos o resultado paginado
		return  prodTemp;
		
	}
	
	
	public Produto pegaProdutoRefFab(String ref, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);
		fromProdutos.fetch("tributoEspecial",JoinType.LEFT);
		fromProdutos.fetch("tributoEspecialDevolucao",JoinType.LEFT);



		List<Predicate> conditions = new ArrayList<>();
		
		Predicate referencia = builder.equal(builder.lower(fromProdutos.get("referencia")),ref.toLowerCase());


		conditions.add(referencia);
		conditions.add(builder.notEqual(fromProdutos.get("finalidade"),Finalidade.Rev));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.isNull(fromProdutos.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.equal(fromProdutos.get("idFilial"),idFilial));
			}
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		Produto prodTemp = new Produto();
		try {
			prodTemp = (Produto) typedQuery.getSingleResult();
			List<ProdutoCusto> listaTempCusto = new ArrayList<>();

			// gerando lista de custo apenas da filial/empresa
			for (Iterator<ProdutoCusto> it = prodTemp.getListaCustoProduto().iterator(); it.hasNext();){
				ProdutoCusto custo = it.next();
				if (custo.getIdFilial() == idFilial && custo.getIdEmpresa() == idEmpresa) {
					listaTempCusto.add(custo);
				}
			}
			prodTemp.setListaCustoProduto(listaTempCusto);
		}catch (NoResultException n) {
			prodTemp = null;
		}
		// montamos o resultado paginado
		return  prodTemp;
		
	}
	
//	public BarrasEstoque pegaProdutoPorBarras(String ref, Long idEmpresa, Long idPrecoFilial){
//		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
//		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);
//
//		Root<BarrasEstoque> fromBarras = criteria.from(BarrasEstoque.class);
////		fromBarras.fetch("tributoEspecial",JoinType.LEFT);
////		fromBarras.fetch("tributoEspecialDevolucao",JoinType.LEFT);
//
//
//
//		List<Predicate> conditions = new ArrayList<>();
//		
//		Predicate referencia = builder.equal(builder.lower(fromBarras.get("barras")),ref.toLowerCase());
//
//
//		conditions.add(referencia);
//		if (idEmpresa == null){
//			conditions.add(builder.isNull(fromBarras.get("idEmpresa")));
//		}else{
//			conditions.add(builder.equal(fromBarras.get("idEmpresa"),idEmpresa));
//		}
//
//		criteria.select(fromBarras.alias("p"));
//		criteria.where(conditions.toArray(new Predicate[]{}));
//		criteria.distinct(true);
//		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
//
//		BarrasEstoque prodTemp = (BarrasEstoque) typedQuery.getSingleResult();
//		List<ProdutoCusto> listaTempCusto = new ArrayList<>();
//		
//
//		listaTempCusto = prodTemp.getListaCustoProduto();
//		for (int i = 0; listaTempCusto.size() > i ; i++){
//			if (listaTempCusto.get(i).getIdFilial() != idFilial ){
//				listaTempCusto.remove(i);
//			}
//		};
//		prodTemp.setListaCustoProduto(listaTempCusto);
//		
//
//		// montamos o resultado paginado
//		return  prodTemp;
//
//		
//	}
	
	public Produto pegaProdutoID(Long id, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);

		Root<Produto> fromProdutos = criteria.from(Produto.class);
		fromProdutos.fetch("tributoEspecial",JoinType.LEFT);
		fromProdutos.fetch("tributoEspecialDevolucao",JoinType.LEFT);



		List<Predicate> conditions = new ArrayList<>();
		
		Predicate referencia = builder.equal(fromProdutos.get("id"),id);


		conditions.add(referencia);
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.isNull(fromProdutos.get("idFilial"))); // Produto � Global registrado somente por IDEMPRESA
			}else{
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
//				conditions.add(builder.equal(fromProdutos.get("idFilial"),idFilial));// Produto � Global registrado somente por IDEMPRESA
			}
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Produto> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		Produto prodTemp = (Produto) typedQuery.getSingleResult();
		List<ProdutoCusto> listaTempCusto = new ArrayList<>();
		
		
		// gerando lista de custo apenas da filial/empresa
		for (Iterator<ProdutoCusto> it = prodTemp.getListaCustoProduto().iterator(); it.hasNext();){
			ProdutoCusto custo = it.next();
			if (custo.getIdFilial() == idFilial && custo.getIdEmpresa() == idEmpresa) {
				listaTempCusto.add(custo);
			}
		}
		prodTemp.setListaCustoProduto(listaTempCusto);


		// montamos o resultado paginado
		return  prodTemp;

		
	}
	

}
