package br.com.nsym.domain.model.repository.fiscal;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.Ncm;
import br.com.nsym.domain.model.entity.fiscal.TabFcpEstado;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class TabFcpEstadoRepository extends GenericRepositoryEmpDS<TabFcpEstado, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<TabFcpEstado> listaFcpPorNcm(Ncm idNcm,Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("ncm", idNcm),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		return criteria.list();	
	}

	public TabFcpEstado pegarFcpPorNcmEstado(Ncm idNcm, Uf uf,Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("ncm", idNcm),
				Restrictions.eq("uf", uf),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.uniqueResult() == null){  // caso ocorra erro com resultado vazio implementar esta saída
			return new TabFcpEstado();
		}else{
		
			return (TabFcpEstado) criteria.uniqueResult();
		}
	}

	public boolean jaExiste(Ncm idNcm,Uf uf, Long idEmpresa){
		final Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eq("ncm", idNcm),
				Restrictions.eq("uf", uf),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.list().isEmpty()){
			System.out.println("retornou false");
			return  false;
		}else{
			System.out.println("retornou true");
			return true;
		}
	}
}


