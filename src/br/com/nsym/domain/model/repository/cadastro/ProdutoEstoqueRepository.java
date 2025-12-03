package br.com.nsym.domain.model.repository.cadastro;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Produto;
import br.com.nsym.domain.model.entity.cadastro.produto.ProdutoEstoque;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ProdutoEstoqueRepository extends GenericRepositoryEmpDS<ProdutoEstoque, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Metodo que retorna um resultado unico com o ProdutoEstoque, tanto por empresa como por filial ou ambos 
	 * @param produto
	 * @param emp
	 * @param filial
	 * @return ProdutoEstoque se a buscar trouxer mais de 1 resultado o retorno é null 
	 */
	public ProdutoEstoque pegaEstoque(Produto produto , Long emp , Long filial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("produto", produto),
				Restrictions.eq("isDeleted", false)));
		if (emp == null){
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
		}else{
			if (filial != null){
				criteria.add(Restrictions.and(Restrictions.eqOrIsNull("idEmpresa", emp),
						Restrictions.eqOrIsNull("idFilial", filial)));
			}else{
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
			}
		}
		criteria.setFetchMode("produto", FetchMode.JOIN);
		if (criteria.list().size() > 1 ){
			return null;
		}else{
			return (ProdutoEstoque) criteria.uniqueResult();
		}
	}
	
	/**
	 * Metodo que retorna um resultado unico com o ProdutoEstoque, tanto por empresa como por filial ou ambos 
	 * @param produto
	 * @param emp
	 * @param filial
	 * @return lista de ProdutoEstoque
	 */
	public List<ProdutoEstoque> ListaProdutoEstoque(Produto produto , Long emp , Long filial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("produto", produto),
				Restrictions.eq("isDeleted", false)));
		if (emp == null){
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
		}else{
			if (filial != null){
				criteria.add(Restrictions.and(Restrictions.eqOrIsNull("idEmpresa", emp),
						Restrictions.eqOrIsNull("idFilial", filial)));
			}else{
				criteria.add(Restrictions.eqOrIsNull("idEmpresa", emp));
			}
		}
		criteria.setFetchMode("produto", FetchMode.JOIN);
			return  criteria.list();
	}
}
