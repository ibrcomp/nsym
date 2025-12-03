package br.com.nsym.domain.model.repository.cadastro;

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

import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.cadastro.Cor;
import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.Tamanho;
import br.com.nsym.domain.model.entity.venda.Transacao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class BarrasEstoqueRepository extends GenericRepositoryEmpDS<BarrasEstoque, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<BarrasEstoque> listBarrasAtivo() {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null) {
			return null;
		} else {
			return criteria.list();
		}
	}

	public boolean jaExisteParaEmpresa(String barras, Long idEmpresa) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.ilike("barras", barras),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.list().isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}
	public boolean jaExisteParaEmpresaEFilial(String barras, Long idEmpresa,Long idFilial) {
		final Criteria criteria = this.createCriteria();

		if (idFilial == null){ 
			criteria.add(Restrictions.and(
					Restrictions.ilike("barras", barras),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		}else{
			criteria.add(Restrictions.and(
					Restrictions.ilike("barras", barras),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
					Restrictions.eqOrIsNull("idFilial", idFilial)));
			
		}
		if (criteria.list().isEmpty() == true) {
			return false;
		} else {
			return true;
		}
	}

	public BarrasEstoque encontraBarrasPorEmpresa(String nome, Long idEmpresa, Long idFilial) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);

		Root<BarrasEstoque> fromBarras = criteria.from(BarrasEstoque.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate prod = builder.equal(builder.lower(fromBarras.get("barras")),nome.toLowerCase());
		
		conditions.add(prod);
			
			if (idEmpresa == null){
				conditions.add(builder.isNull(fromBarras.get("idEmpresa")));
			}else{
				if (idFilial == null){
					conditions.add(builder.equal(fromBarras.get("idEmpresa"),idEmpresa));
					conditions.add(builder.isNull(fromBarras.get("idFilial")));
				}else{
					conditions.add(builder.equal(fromBarras.get("idEmpresa"),idEmpresa));
					conditions.add(builder.equal(fromBarras.get("idFilial"),idFilial));
				}
			}
		criteria.select(fromBarras.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria);
		
//		return (BarrasEstoque)typedQuery.getSingleResult();
		List<BarrasEstoque> listaBarras = new ArrayList<BarrasEstoque>();
		System.out.println("tamanho da lista: " + typedQuery.getResultList().size());
		for (BarrasEstoque barrasEstoque : typedQuery.getResultList()) {
			if (idFilial == null) {
				if (barrasEstoque.getIdFilial() == null) {
					listaBarras.add(barrasEstoque);
				}
			}else {
				if (barrasEstoque.getIdFilial() == idFilial) {
					listaBarras.add(barrasEstoque);
				}
				
			}
		}
		if (listaBarras.size() == 1){
			if (listaBarras.get(0) == null){
				return null;
			}else{
				return listaBarras.get(0);
			}
		}else{
			return null;
		}
		
		
		
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(
//				Restrictions.ilike("barras", nome),
//				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
//				Restrictions.eqOrIsNull("idFilial", idFilial)));
//		if (criteria.list().isEmpty() == true) {
//			System.out.println("barras repository retorno barras vazio");
//			return null;
//		} else {
//			System.out.println("barras repository barras ja existe!");
//			return (BarrasEstoque) criteria.uniqueResult();
//		}
	}

	public BarrasEstoque encontraBarrasPorEmpresaEFilialEProduto(String nome,Produto produto ,Long idEmpresa,Long idFilial) {
		final Criteria criteria = this.createCriteria();

			criteria.add(Restrictions.and(
					Restrictions.ilike("barras", nome),
					Restrictions.eq("produtoBase",produto),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
					Restrictions.eqOrIsNull("idFilial", idFilial)));
			
		if (criteria.list().isEmpty() == true) {
			System.out.println("barras repository retorno barras vazio");
			return null;
		} else {
			System.out.println("barras repository barras ja existe!");
			return (BarrasEstoque) criteria.uniqueResult();
		}
	}
	/**
	 * 
	 * @param codigo - envie a classe produto
	 * @param idEmpresa - envie o id da empresa TIPO Long
	 * @return lista de codigo de barras por produto
	 */
	public List<BarrasEstoque> listaBarrasPorProduto(Produto codigo, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);
		
		Root<BarrasEstoque> fromControle = criteria.from(BarrasEstoque.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		Predicate prod = builder.equal(fromControle.get("produtoBase"),codigo.getId());
		
//		conditions.add(builder.equal(fromControle.get("isDeleted"), isDeleted));
		conditions.add(prod);
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
		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		
		
		if (typedQuery.getResultList().isEmpty() == true) {
			return null;
		} else {
			return typedQuery.getResultList();
		}
	}
	
	/**
	 * 
	 * @param codigo - envie a classe produto
	 * @param idEmpresa - envie o id da empresa TIPO Long
	 * @return lista de codigo de barras por produto
	 */
	public BarrasEstoque encontraBarrasPorProdutoTamanhoCor(Produto codigo,Tamanho tam,Cor cor, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);
		
		Root<BarrasEstoque> fromControle = criteria.from(BarrasEstoque.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		Predicate prod = builder.equal(fromControle.get("produtoBase"),codigo);
		if (tam == null) {
			conditions.add(builder.isNull(fromControle.get("tamanho")));
		}else {
			conditions.add(builder.equal(fromControle.get("tamanho"), tam));
		}
		if (cor == null) {
			conditions.add(builder.isNull(fromControle.get("cor")));
		}else {
			conditions.add(builder.equal(fromControle.get("cor"), cor));
		}
//		conditions.add(builder.equal(fromControle.get("isDeleted"), isDeleted));
		conditions.add(prod);
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
		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		
		
		if (typedQuery.getResultList().isEmpty() == true) {
			return null;
		} else {
			return typedQuery.getSingleResult();
		}
	}
		
//		final Criteria criteria = this.createCriteria();
//
//			criteria.add(Restrictions.and(
//					Restrictions.eq("produtoBase", codigo),
//					Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
//					Restrictions.eqOrIsNull("idFilial", idFilial)));
//			
//		if (criteria.list().isEmpty() == true) {
//			return null;
//		} else {
//			return criteria.list();
//		}

	public BarrasEstoque pegaEstoque(Long produto, Long pegaIdEmpresa, Long pegaIdFilial) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);

		Root<BarrasEstoque> fromBarras = criteria.from(BarrasEstoque.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate prod = builder.equal(fromBarras.get("produtoBase"),produto);


		conditions.add(prod);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromBarras.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(fromBarras.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(fromBarras.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromBarras.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromBarras.get("idFilial"),pegaIdFilial));
			}
		}

		criteria.select(fromBarras.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria);
		//		return (BarrasEstoque)typedQuery.getSingleResult();
		List<BarrasEstoque> listaBarra = typedQuery.getResultList();
		if (listaBarra.size() == 1){
			if (listaBarra.get(0) == null){
				return null;
			}else{
				return listaBarra.get(0);
			}
		}else{
			return null;
		}
	}

	public List<BarrasEstoque> pegaListaEstoque(Long produto, Long pegaIdEmpresa, Long pegaIdFilial) {

		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);

		Root<BarrasEstoque> fromBarras = criteria.from(BarrasEstoque.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate prod = builder.equal(fromBarras.get("produtoBase"),produto);


		conditions.add(prod);
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromBarras.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(fromBarras.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(fromBarras.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromBarras.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromBarras.get("idFilial"),pegaIdFilial));
			}
		}

		criteria.select(fromBarras.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria);

		return typedQuery.getResultList();
	}
	
	public BarrasEstoque encontraEstoque(String nome,Produto produto ,Long idEmpresa,Long idFilial) {
		
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);

		Root<BarrasEstoque> fromBarras = criteria.from(BarrasEstoque.class);

		List<Predicate> conditions = new ArrayList<>();

		Predicate prod = builder.equal(fromBarras.get("produtoBase"),produto);
		
		conditions.add(prod);
			builder.equal(builder.lower(fromBarras.get("barras")),nome.toLowerCase());
			
			if (idEmpresa == null){
				conditions.add(builder.isNull(fromBarras.get("idEmpresa")));
			}else{
				if (idFilial == null){
					conditions.add(builder.equal(fromBarras.get("idEmpresa"),idEmpresa));
					conditions.add(builder.isNull(fromBarras.get("idFilial")));
				}else{
					conditions.add(builder.equal(fromBarras.get("idEmpresa"),idEmpresa));
					conditions.add(builder.equal(fromBarras.get("idFilial"),idFilial));
				}
			}
		criteria.select(fromBarras.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return (BarrasEstoque)typedQuery.getSingleResult();
		
	}
	
	public List<BarrasEstoque> listaEstoqueExistentePorEmpresaEFilial(Long pegaIdEmpresa, Long pegaIdFilial) {

		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<BarrasEstoque> criteria = builder.createQuery(BarrasEstoque.class);

		Root<BarrasEstoque> fromBarras = criteria.from(BarrasEstoque.class);

		List<Predicate> conditions = new ArrayList<>();
		
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(fromBarras.get("idEmpresa")));
		}else{
			if (pegaIdFilial == null){
				conditions.add(builder.equal(fromBarras.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.isNull(fromBarras.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromBarras.get("idEmpresa"),pegaIdEmpresa));
				conditions.add(builder.equal(fromBarras.get("idFilial"),pegaIdFilial));
			}
		}

		criteria.select(fromBarras.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<BarrasEstoque> typedQuery = this.getEntityManager().createQuery(criteria);

		return typedQuery.getResultList();
	}
	
}

