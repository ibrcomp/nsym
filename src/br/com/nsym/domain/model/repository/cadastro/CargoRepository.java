package br.com.nsym.domain.model.repository.cadastro;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Cargo;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CargoRepository extends GenericRepositoryEmpDS<Cargo, Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Cargo> listCargoAtiva(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return criteria.list();
		}
	}
	public Cargo update (Cargo emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}

}
