package br.com.nsym.domain.model.repository.cadastro;

import java.util.Date;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class EndComplementoRepository extends GenericRepositoryEmpDS<EndComplemento, Long>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Page<EndComplemento> listaEndereco( Long id, Long idEmpresa, Long idFilial, PageRequest pageRequest ){
		final Criteria criteria = this.createCriteria();
	
		if (idFilial != null){
        criteria.add(Restrictions.and(
                Restrictions.eq("id", id),
                Restrictions.eq("idEmpresa", idEmpresa),
                Restrictions.eq("logradouro", idFilial)
        ));
		}else{
			criteria.add(Restrictions.and(
	                Restrictions.eq("id", id),
	                Restrictions.eq("idEmpresa", idEmpresa)
	        ));
		}
				
    criteria.add(Restrictions.eq("deleted", false));
    
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
	public EndComplemento pegaEndComplementoPorEmpresa(Empresa id){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("empresa",id)));
		if (criteria.uniqueResult() == null) {
			System.out.println("Pesquisei mas nao encontrei o Id correspondente");
			return null;
		}else{
			final EndComplemento end = (EndComplemento)criteria.uniqueResult();
			System.out.println("encontrei " + end.getId());
			return end;
		}
	}
	public EndComplemento pegaEndComplementoPorFilial(Filial id){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("filial",id)));
		if (criteria.uniqueResult() == null) {
			System.out.println("Pesquisei mas nao encontrei o Id correspondente");
			return null;
		}else{
			final EndComplemento end = (EndComplemento)criteria.uniqueResult();
			System.out.println("encontrei " + end.getId());
			return end;
		}
	}
	public EndComplemento pegaEndComplementoPorCliente(Cliente id){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("cliente",id)));
		if (criteria.uniqueResult() == null) {
			System.out.println("Pesquisei mas nao encontrei o Id correspondente");
			return null;
		}else{
			final EndComplemento end = (EndComplemento)criteria.uniqueResult();
			System.out.println("encontrei " + end.getId());
			return end;
		}
	}
	public EndComplemento update (EndComplemento emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}
	public EndComplemento pegaEndComplementoPorColaborador(Colaborador id) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("colaborador",id)));
		if (criteria.uniqueResult() == null) {
			System.out.println("Pesquisei mas nao encontrei o Id correspondente");
			return null;
		}else{
			final EndComplemento end = (EndComplemento)criteria.uniqueResult();
			System.out.println("encontrei " + end.getId());
			return end;
		}
	}
	public EndComplemento pegaEndComplementoPorTransportadora(Transportadora transportadora) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("transportadora",transportadora)));
		if (criteria.uniqueResult() == null) {
			System.out.println("Pesquisei mas nao encontrei o Id correspondente");
			return null;
		}else{
			final EndComplemento end = (EndComplemento)criteria.uniqueResult();
			System.out.println("encontrei " + end.getId());
			return end;
		}
	}
	public EndComplemento pegaEndComplementoPorFornecedor(Fornecedor fornecedor) {
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("fornecedor",fornecedor)));
		if (criteria.uniqueResult() == null) {
			System.out.println("Pesquisei mas nao encontrei o Id correspondente");
			return null;
		}else{
			final EndComplemento end = (EndComplemento)criteria.uniqueResult();
			System.out.println("encontrei " + end.getId());
			return end;
		}
	}
}
