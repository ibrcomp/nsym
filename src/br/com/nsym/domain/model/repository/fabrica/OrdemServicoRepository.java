package br.com.nsym.domain.model.repository.fabrica;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.nsym.domain.model.entity.fabrica.OrdemServico;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class OrdemServicoRepository  extends GenericRepositoryEmpDS<OrdemServico,Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = -5960544405260811881L;
	
	/**
	 *  Lista com as Ordens de Serviço por Ordem de Produção
	 * @param op
	 * @param idEmpresa
	 * @param idFilial
	 * @param global
	 * @param deleted
	 * @return
	 */
	  public List<OrdemServico> listaOSporOP (Long op,Long idEmpresa , Long idFilial,boolean global, boolean deleted){
	    	CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
			CriteriaQuery<OrdemServico> criteria =  builder.createQuery(getPersistentClass());
			
			Root<OrdemServico> formOS = criteria.from(getPersistentClass());
			List<Predicate> conditions = new ArrayList<>();
			
			
			conditions.add(builder.equal(formOS.get("op"), op));
			conditions.add(builder.equal(formOS.get("isDeleted"), deleted));
			
			if (idEmpresa == null){
				conditions.add(builder.isNull(formOS.get("idEmpresa")));
			}else{
				if (idFilial == null){
					conditions.add(builder.equal(formOS.get("idEmpresa"),idEmpresa));
					if (global == false) {
						conditions.add(builder.isNull(formOS.get("idFilial")));
					}
				}else{
					if (global == false) {
						conditions.add(builder.equal(formOS.get("idEmpresa"),idEmpresa));
						conditions.add(builder.equal(formOS.get("idFilial"),idFilial));
					}else {
						conditions.add(builder.equal(formOS.get("idEmpresa"),idEmpresa));
					}
				}
			}
			criteria.select(formOS.alias("p"));
			criteria.where(conditions.toArray(new Predicate[]{}));
			criteria.distinct(true);
			TypedQuery<OrdemServico> typedQuery = this.getEntityManager().createQuery(criteria);
			
			return typedQuery.getResultList();
	    }

}
