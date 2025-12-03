package br.com.nsym.domain.model.repository.cadastro;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class EmpresaRepository extends GenericRepositoryEmpDS<Empresa, Long> {


	/**
	 *
	 */
	private static final long serialVersionUID = 328007573282922688L;

	public List<Filial> listFiliaisPorEmpresa(Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("idEmpresa", idEmpresa));

		return criteria.list();
	}
	/*
	 * Lista todas as Empresas Ativas
	 * @return List<Empresas>
	 */
	public List<Empresa> listEmpresaAtiva(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return criteria.list();
		}
	}
	
	public Empresa update (Empresa emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}
	
	public List<Empresa> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
						Restrictions.ilike("razaoSocial", "%"+dep+"%"),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
						Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();
	}
	
	
	
}
