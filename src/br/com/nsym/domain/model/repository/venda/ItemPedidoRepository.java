package br.com.nsym.domain.model.repository.venda;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.venda.ItemPedido;
import br.com.nsym.domain.model.entity.venda.Pedido;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ItemPedidoRepository extends GenericRepositoryEmpDS<ItemPedido, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<ItemPedido> listaDeItensPorCFe(Pedido pedido,Long idEmpresa,Long idFilial){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("pedido", pedido),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
				Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();	
	}

}
