package br.com.nsym.domain.model.repository.fiscal.nfe;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.fiscal.nfe.NaturezaOperacao;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

public class NaturezaOperacaoRepository extends GenericRepositoryEmpDS<NaturezaOperacao, Long>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<NaturezaOperacao> listNaturezaOperacaoAtivo(){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isDeleted", false));
		if (criteria.list() == null){
			return null;
		}else {
			return  criteria.list();
		}
	}
	
	public List<NaturezaOperacao> pesquisaTexto(String dep,Long idEmpresa, Long idFilial){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
						Restrictions.ilike("descricao", "%"+dep+"%"),
						Restrictions.eq("isDeleted", false),
						Restrictions.eqOrIsNull("idEmpresa", idEmpresa),
						Restrictions.eqOrIsNull("idFilial", idFilial)));
		return criteria.list();
	}

}
