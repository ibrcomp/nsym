package br.com.nsym.domain.model.repository.fabrica;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fabrica.Servico;
import br.com.nsym.domain.model.entity.tools.Finalidade;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

public class ServicoRepository extends GenericRepositoryEmpDS<Servico,Long>
{

	/**
	 *
	 */
	private static final long serialVersionUID = -1880217156928633558L;
	
	/**
	 * Busca o serviço atraves da ID 
	 * @param ref
	 * @param idEmpresa
	 * @param idFilial
	 * @return
	 */
	public Servico pegaServicoPorID(Long ref, Long idEmpresa, Long idFilial){
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Servico> criteria = builder.createQuery(Servico.class);

		Root<Servico> fromService = criteria.from(Servico.class);



		List<Predicate> conditions = new ArrayList<>();
		
		Predicate referencia = builder.equal(fromService.get("id"),ref);

		conditions.add(referencia);
		conditions.add(builder.equal(fromService.get("isDeleted"),false));
		if (idEmpresa == null){
			conditions.add(builder.isNull(fromService.get("idEmpresa")));
		}else{
			if (idFilial == null){
				conditions.add(builder.equal(fromService.get("idEmpresa"),idEmpresa));
				conditions.add(builder.isNull(fromService.get("idFilial")));
			}else{
				conditions.add(builder.equal(fromService.get("idEmpresa"),idEmpresa));
				conditions.add(builder.equal(fromService.get("idFilial"),idFilial));
			}
		}

		criteria.select(fromService.alias("p"));
		criteria.where(conditions.toArray(new Predicate[]{}));
		criteria.distinct(true);
		TypedQuery<Servico> typedQuery = this.getEntityManager().createQuery(criteria); //.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		Servico serv = new Servico();
		try {
			serv = (Servico) typedQuery.getSingleResult();
			
		}catch (NoResultException n) {
			serv = null;
		}
		// montamos o resultado paginado
		return  serv;
		
	}

}
