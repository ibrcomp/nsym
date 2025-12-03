package br.com.nsym.domain.model.repository.cadastro;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.com.nsym.domain.model.entity.cadastro.Cliente;
import br.com.nsym.domain.model.entity.cadastro.Colaborador;
import br.com.nsym.domain.model.entity.cadastro.Contato;
import br.com.nsym.domain.model.entity.cadastro.Email;
import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Filial;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.cadastro.Transportadora;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class EmailRepository extends GenericRepositoryEmpDS<Email, Long>{
	
	
	/**
	 *
	 */
	private static final long serialVersionUID = 6239825682370801466L;

	public List<Email> listaEmailPorContato(Contato contato){
		final Criteria criteria = createCriteria();
		criteria.add(Restrictions.and(
				Restrictions.eq("contato", contato),
				Restrictions.eq("isDeleted", false)));
		
		return criteria.list();
	}
	
	public List<Email> listaDeTelefoneDosContatosPorEmpresa(List<Contato> contatos) {
		  Criteria criteria = createCriteriaEmail(Email.class,"f");
		  Conjunction e = Restrictions.conjunction();
		  for (Contato c : contatos) {
		    e.add(Subqueries.exists(
		      DetachedCriteria.forClass(Empresa.class, "emp")
		        .setProjection(Projections.id())
		        .add(Restrictions.eqProperty("f.contato", "emp.contato"))
		        .add(Restrictions.eq("emp.contato",c))));
		  }
		  criteria.add(e);
		  return criteria.list();
		}
	
	public Email update (Email emp,String usuario,Date dataAtualiza){
		emp.setEditedBy(usuario);
		emp.setLastEdition(dataAtualiza);
		return this.save(emp);
	}
	
	
	public Email pegaEmailNfe(Cliente cliente,Fornecedor fornecedor,Empresa emp,Filial filial,Transportadora tranp,Long empresa){
		final Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.and(
				Restrictions.eqOrIsNull("cliente",cliente),
				Restrictions.eqOrIsNull("fornecedor", fornecedor),
				Restrictions.eqOrIsNull("empresa", emp),
				Restrictions.eqOrIsNull("filial", filial),
				Restrictions.eqOrIsNull("transportadora", tranp),
				Restrictions.eqOrIsNull("idEmpresa", empresa)));
		
		
		return (Email) criteria.uniqueResult();
		
	}
	
	public Email pegaEmail(Colaborador colaborador,Long empresa){
		final Criteria criteria = this.createCriteria();
		if (empresa != null){
		criteria.add(Restrictions.and(
				Restrictions.eq("colaborador",colaborador),
				Restrictions.eq("idEmpresa", empresa)));
		}else {
			criteria.add(Restrictions.and(
					Restrictions.eq("colaborador",colaborador),
					Restrictions.isNull("idEmpresa")));
		}
		return (Email) criteria.uniqueResult();
		
	}
}
