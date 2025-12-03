package br.com.nsym.domain.model.repository.venda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.tools.ControlePedido;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class ControlePedidoRepository extends GenericRepositoryEmpDS<ControlePedido, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5571747631787459387L;
	/**
	 * Método que retorna uma lista de numerações disponiveis com data = dia de hoje
	 * 
	 * @param idEmpresa
	 * @param idFilial
	 * @return controle (tipo Long)
	 */
	
	public List<ControlePedido> pegaListaControlePedido(Long idEmpresa, Long idFilial) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ControlePedido> criteria = builder.createQuery(ControlePedido.class);

		Root<ControlePedido> fromControle = criteria.from(ControlePedido.class);

		List<Predicate> conditions = new ArrayList<>();


//		conditions.add(builder.equal(fromControle.get("disponivel"), disponivel));
		conditions.add(builder.equal(fromControle.get("hoje"), LocalDate.now()));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromControle.get("idEmpresa")));
		}else{
			conditions.add(builder.equal(fromControle.get("idEmpresa"),idEmpresa));
			if (idFilial == null) {
				conditions.add(builder.isNull(fromControle.get("idFilial")));
			}else {
				conditions.add(builder.equal(fromControle.get("idFilial"),idFilial));
			}
		}

		criteria.select(fromControle.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<ControlePedido> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		

		return typedQuery.getResultList();
	}
}

