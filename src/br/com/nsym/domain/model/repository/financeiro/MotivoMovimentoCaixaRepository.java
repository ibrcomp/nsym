package br.com.nsym.domain.model.repository.financeiro;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.model.entity.financeiro.tools.MotivoMovimentoCaixa;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class MotivoMovimentoCaixaRepository extends GenericRepositoryEmpDS<MotivoMovimentoCaixa, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9173498683080814229L;
	
	
	public Page<MotivoMovimentoCaixa> listaDeMotivosParaMovimentoFiananceiro(boolean deleted ,Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest,String filtro , String pesquisa) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<MotivoMovimentoCaixa> criteria = builder.createQuery(MotivoMovimentoCaixa.class);

		Root<MotivoMovimentoCaixa> formPedido = criteria.from(MotivoMovimentoCaixa.class);

		List<Predicate> conditions = new ArrayList<>();
		conditions.add(builder.equal(formPedido.get("isDeleted"), deleted));
		if (filtro != null){
			if (!filtro.isEmpty() ) {
				conditions.add(builder.like(builder.upper(formPedido.<String>get(filtro)),"%"+pesquisa.toUpperCase()+"%")); // garantindo que tudo esteja em maiuscula para localizar o termo
			}
			
		}
		if (pegaIdEmpresa == null){
			conditions.add(builder.isNull(formPedido.get("idEmpresa")));
		}else{
//			if (pegaIdFilial == null){
				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
//				conditions.add(builder.isNull(formPedido.get("idFilial")));
//			}else{
//				conditions.add(builder.equal(formPedido.get("idEmpresa"),pegaIdEmpresa));
//				conditions.add(builder.equal(formPedido.get("idFilial"),pegaIdFilial));
//			}
		}
		
		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		cq.select(builder.count(cq.from(MotivoMovimentoCaixa.class)));
		this.getEntityManager().createQuery(cq);
		cq.where(conditions.toArray(new Predicate[0]));

		final Long totalRows = this.getEntityManager().createQuery(cq).getSingleResult();
		
		criteria.select(formPedido.alias("cf"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		
		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.orderBy(builder.asc(formPedido.get(pageRequest.getSortField())));

		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.orderBy(builder.desc(formPedido.get(pageRequest.getSortField())));

		}
		
		criteria.distinct(true);
		TypedQuery<MotivoMovimentoCaixa> typedQuery = this.getEntityManager().createQuery(criteria);
		
		// paginamos
				typedQuery.setFirstResult(pageRequest.getFirstResult());
				typedQuery.setMaxResults(pageRequest.getPageSize());
		return new Page<>(typedQuery.getResultList(),totalRows);
		
	}

}
