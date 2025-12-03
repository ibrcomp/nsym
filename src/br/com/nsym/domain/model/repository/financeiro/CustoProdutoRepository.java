package br.com.nsym.domain.model.repository.financeiro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.financeiro.produto.ProdutoCusto;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CustoProdutoRepository extends GenericRepositoryEmpDS<ProdutoCusto, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	public ProdutoCusto custoProduto(){
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.eq("isDeleted", false));
//		if (criteria.list() == null){
//			return null;
//		}else {
//			return (ProdutoCusto) criteria.uniqueResult();
//		}
//	}
	
	/**
	 * Metodo que retorna um resultado unico com o CustoProduto, tanto por empresa como por filial ou ambos 
	 * @param produto
	 * @param emp
	 * @param filial
	 * @return lista de ProdutoEstoque
	 */
//	public ProdutoCusto pegaProdutoCustos(Produto produto , Long emp , Long filial){
//		final Criteria criteria = this.createCriteria();
//
//		criteria.add(Restrictions.and(
//				Restrictions.eq("produto", produto),
//				Restrictions.eq("isDeleted", false)));
//		if (emp == null){
//				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
//		}else{
//			if (filial != null){
//				criteria.add(Restrictions.and(Restrictions.eqOrIsNull("idEmpresa", emp),
//						Restrictions.eqOrIsNull("idFilial", filial)));
//			}else{
//				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
//			}
//		}
//		criteria.setFetchMode("produto", FetchMode.JOIN);
//			return  (ProdutoCusto) criteria.uniqueResult();
//	}
	
	
	public ProdutoCusto pegaCustoProdutoPorIdTransferencia(Produto produto, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ProdutoCusto> criteria = builder.createQuery(ProdutoCusto.class);

		Root<ProdutoCusto> fromProdutos = criteria.from(ProdutoCusto.class);


		List<Predicate> conditions = new ArrayList<>();
		
		Predicate referencia = builder.equal(fromProdutos.get("produto"),produto);


		conditions.add(referencia);
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromProdutos.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(fromProdutos.get("idFilial"))); // Produto � Global registrado somente por IDEMPRESA
			}else{
				conditions.add(builder.equal(fromProdutos.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(fromProdutos.get("idFilial"),idFilial));// Produto � Global registrado somente por IDEMPRESA
			}
		}

		criteria.select(fromProdutos.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ProdutoCusto> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		ProdutoCusto custo = new ProdutoCusto();
		try {
			custo =  typedQuery.getSingleResult();
		}catch (NoResultException n) {
			custo = null;
		}
		// montamos o resultado paginado
		return  custo;
//		Produto prodTemp = (Produto) typedQuery.getSingleResult();
//		List<ProdutoCusto> listaTempCusto = new ArrayList<>();
//		
//		
//		// gerando lista de custo apenas da filial/empresa
//		for (Iterator<ProdutoCusto> it = prodTemp.getListaCustoProduto().iterator(); it.hasNext();){
//			ProdutoCusto custo = it.next();
//			if (custo.getIdFilial() == idFilial && custo.getIdEmpresa() == idEmpresa) {
//				listaTempCusto.add(custo);
//			}
//		}
//		prodTemp.setListaCustoProduto(listaTempCusto);


		// montamos o resultado paginado
//		if (prodTemp.getListaCustoProduto().size() == 1) {
//			return  prodTemp.getListaCustoProduto().get(0);
//		}else {
//			return new ProdutoCusto();
//		}

		
	}

}
