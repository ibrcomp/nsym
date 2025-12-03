package br.com.nsym.domain.model.repository.financeiro;

import java.time.LocalDate;
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
import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.MovimentoCaixa;
import br.com.nsym.domain.model.entity.financeiro.tools.MotivoMovimentoCaixa;
import br.com.nsym.domain.model.entity.financeiro.tools.MovimentoEnum;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class MovimentoCaixaRepository extends GenericRepositoryEmpDS<MovimentoCaixa, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6995195689189682970L;
	
	public Page<MovimentoCaixa> extratoMovimentoCaixaPorData(LocalDate dataInicial, LocalDate dataFinal,Long pegaIdEmpresa, Long pegaIdFilial,PageRequest pageRequest,String filtro , String pesquisa) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<MovimentoCaixa> criteria = builder.createQuery(MovimentoCaixa.class);

		Root<MovimentoCaixa> formPedido = criteria.from(MovimentoCaixa.class);

		List<Predicate> conditions = new ArrayList<>();
		
		
		conditions.add(builder.between(formPedido.get("dataEmissao"),dataInicial,dataFinal));

		if (filtro != null){
			if (!filtro.isEmpty() ) {
				conditions.add(builder.like(builder.upper(formPedido.<String>get(filtro)),"%"+pesquisa.toUpperCase()+"%")); // garantindo que tudo estja em maiuscula para localizar o termo
			}
			
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
		TypedQuery<MovimentoCaixa> typedQuery = this.getEntityManager().createQuery(criteria);
		
		// paginamos
				typedQuery.setFirstResult(pageRequest.getFirstResult());
				typedQuery.setMaxResults(pageRequest.getPageSize());
		return new Page<>(typedQuery.getResultList(),totalRows);
		
	}
	
	public List<MovimentoCaixa> listaMovimentacaoSangriaPorCaixa(Caixa caixa,Long idEmpresa,Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<MovimentoCaixa> criteria = builder.createQuery(MovimentoCaixa.class);

		Root<MovimentoCaixa> formMovimento = criteria.from(MovimentoCaixa.class);
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formMovimento.get("caixa"),caixa));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formMovimento.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formMovimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formMovimento.get("idFilial")));
			}else{
				conditions.add(builder.equal(formMovimento.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formMovimento.get("idFilial"),idFilial));
			}
		}
		
		criteria.select(formMovimento.alias("r"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<MovimentoCaixa> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
	}

}
