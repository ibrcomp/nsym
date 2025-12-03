package br.com.nsym.domain.model.repository.financeiro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.financeiro.Caixa;
import br.com.nsym.domain.model.entity.financeiro.SaldoCaixa;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

public class SaldoCaixaRepository extends GenericRepositoryEmpDS<SaldoCaixa, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -883598870306369225L;
	
	
	/**
	 * Retorna uma lista com o saldo disponivel por TipoDePagamentosSimples
	 * @param caixa
	 * @param idEmpresa
	 * @param idFilial
	 * @return Lista<SaldoCaixa>
	 */
	public List<SaldoCaixa> listaSaldoDisponivel(Caixa caixa,Long idEmpresa,Long idFilial) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<SaldoCaixa> criteria = builder.createQuery(SaldoCaixa.class);

		Root<SaldoCaixa> formSaldoCaixa = criteria.from(SaldoCaixa.class);
		
		
		List<Predicate> conditions = new ArrayList<>();
		
		conditions.add(builder.equal(formSaldoCaixa.get("caixa"),caixa));
		
		if (idEmpresa == null){
			conditions.add(builder.isNull(formSaldoCaixa.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(formSaldoCaixa.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(formSaldoCaixa.get("idFilial")));
			}else{
				conditions.add(builder.equal(formSaldoCaixa.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(formSaldoCaixa.get("idFilial"),idFilial));
			}
		}
		
		criteria.select(formSaldoCaixa.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<SaldoCaixa> typedQuery = this.getEntityManager().createQuery(criteria);
		
		return typedQuery.getResultList();
	}

}
