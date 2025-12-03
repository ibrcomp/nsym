package br.com.nsym.domain.model.repository.fiscal;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.fiscal.CFOP;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class CFOPRepository extends GenericRepositoryEmpDS<CFOP, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<CFOP> listCFOPAtivo(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}

	public List<CFOP> listCFOPAtivoEmpresa(Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("isDeleted", false),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}

	public boolean jaExiste(String nome , Long idEmpresa){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(Restrictions.ilike("cfop", nome),
				Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		if (criteria.list().isEmpty() == true){
			return false;
		}else {
			return true;
		}
	}
	/**
	 * Pesquisa Cfop por Filial/Empresa
	 * para pesquisar o Cfop de todas as filiais e matriz informe NULL para idFilial
	 * 
	 * @param dep Texto a ser pesquisado
	 * @param idEmpresa id da empresa
	 * @param idFilial id da filial
	 * @return lista de Cfop
	 */
	public List<CFOP> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();

		if (idFilial == null){
			criteria.add(Restrictions.and(
					Restrictions.ilike("cfop", "%"+dep+"%"),
					Restrictions.eq("isDeleted", false),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		}else{
			criteria.add(Restrictions.and(
					Restrictions.ilike("cfop", "%"+dep+"%"),
					Restrictions.eq("isDeleted", false),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
					Restrictions.eqOrIsNull("idFilial", idFilial)));

		}
		return criteria.list();
	}

	public CFOP achaCfop(String cfop, Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();

		if (idFilial == null){
			criteria.add(Restrictions.and(
					Restrictions.eq("cfop", cfop),
					Restrictions.eq("isDeleted", false),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa)));
		}else{
			criteria.add(Restrictions.and(
					Restrictions.eq("cfop", cfop),
					Restrictions.eq("isDeleted", false),
					Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
					Restrictions.eqOrIsNull("idFilial", idFilial)));

		}
		if ( criteria.list() == null){
			return null;
		}else{
			return (CFOP) criteria.uniqueResult();
		}
	}

}
