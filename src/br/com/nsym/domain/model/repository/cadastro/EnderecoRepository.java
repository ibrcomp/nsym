package br.com.nsym.domain.model.repository.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.nsym.application.component.table.Page;
import br.com.nsym.application.component.table.PageRequest;
import br.com.nsym.domain.misc.AddressFinder;
import br.com.nsym.domain.misc.AddressFinder.Address;
import br.com.nsym.domain.misc.AddressFinder.AddressMania;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;
import br.com.nsym.infraestrutura.configuration.ApplicationUtils;

@Dependent
public class EnderecoRepository extends GenericRepositoryEmpDS<Endereco, Long> {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Page<Endereco> listCepPorEndereco( Uf uf, String localidade, String logradouro , PageRequest pageRequest) {


		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("uf", uf ),
				Restrictions.ilike("localidade",localidade,MatchMode.ANYWHERE),
				Restrictions.ilike("logradouro", logradouro, MatchMode.ANYWHERE)
				));

		criteria.add(Restrictions.eq("isDeleted", false));

		// projetamos para pegar o total de paginas possiveis
		criteria.setProjection(Projections.count("id"));

		final Long totalRows = (Long) criteria.uniqueResult();

		// limpamos a projection para que a criteria seja reusada
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// paginamos
		criteria.setFirstResult(pageRequest.getFirstResult());
		criteria.setMaxResults(pageRequest.getPageSize());

		if (pageRequest.getSortDirection() == PageRequest.SortDirection.ASC) {
			criteria.addOrder(Order.asc(pageRequest.getSortField()));
		} else if (pageRequest.getSortDirection() == PageRequest.SortDirection.DESC) {
			criteria.addOrder(Order.desc(pageRequest.getSortField()));
		}
		System.out.println("estou no EnderecoRepository fazendo no listCepPorEndereco");
		// montamos o resultado paginado
		return new Page<>((List<Endereco>) criteria.list(), totalRows);
	}
	/**
	 * Procura um cep na Base local caso não encontre procura o cep na WEB
	 * @param cep
	 * @return Endereco ou null quando não encontrado
	 */
	public Endereco listCep(String cep) {

		final Criteria criteria = this.createCriteria();
	
		criteria.add(Restrictions.and(
				Restrictions.eq("cep", cep ),
				Restrictions.eq("isDeleted", false)
				));

//		criteria.setMaxResults(1);

		System.out.println("teste de listaCEP em Endereco REpositorio");
		// montamos o resultado paginado
		Endereco resultado = (Endereco) criteria.uniqueResult();
//		System.out.println(resultado.getCep());
		if (resultado == null || resultado.getIbge() == null){
			System.out.println("resultado = null");
			AddressFinder address = new AddressFinder();
			if (ApplicationUtils.getConfiguration("ws.uso").contentEquals("0")) {
				AddressMania ende = address.findAddressByZipcode(cep);
				if (ende != null) {
					if (ende.getEndereco() != null) {
						if (resultado == null) {
							resultado = new Endereco();
						}
						resultado.setBairro(ende.getBairro());
						resultado.setLogra(ende.getEndereco());
						resultado.setCep(ende.getCep().substring(0,5)+"-"+ende.getCep().substring(5));
						resultado.setUf(Uf.valueOf(ende.getUf()));
						resultado.setIbge(ende.getIbge());
						resultado.setLocalidade(ende.getCidade());
						System.out.println(resultado.getLogra());
						System.out.println(resultado.getIbge());
						this.save(resultado);
						return resultado;
					}else {
						return resultado ;
					}
				}else {
					return null;
				}

			}else {
				Address ende = address.findAddressByZipcodeVia(cep);	
				if (ende != null) {
					if (ende.getLogradouro() != null) {
						if (resultado == null) {
							resultado = new Endereco();
						}
						resultado.setBairro(ende.getBairro());
						resultado.setLogra(ende.getLogradouro());
						resultado.setCep(ende.getCep());
						resultado.setUf(Uf.valueOf(ende.getUf()));
						resultado.setIbge(ende.getIbge());
						resultado.setLocalidade(ende.getLocalidade());
						System.out.println(resultado.getLogra());
						System.out.println(resultado.getIbge());
						this.save(resultado);
						return resultado;
					}else {
						return resultado ;
					}
				}else {
					return null;
				}

			}
			
		}else{
			System.out.println("pesquisei no banco!");
			return resultado;
		}
	}
	/**
	 * Localiza o cep na WEB atravez do estado,municipio e logradouro
	 * @param estado
	 * @param municipio
	 * @param logradouro
	 * @return Lista de Endereco
	 */
	public List<Endereco> procuraCepWeb(String estado, String municipio, String logradouro){
//		try{
			AddressFinder address = new AddressFinder();
			List<Address> tempAddress = new ArrayList<Address>();
			List<Endereco> tempEnd = new ArrayList<Endereco>();
			tempAddress = address.findAddressByLogradouro(estado, municipio, logradouro);
			
			for (Address add : tempAddress) {
				Endereco end = new Endereco();
				end.setBairro(add.getBairro());
				end.setLogra(add.getLogradouro());
				end.setCep(add.getCep());
				end.setUf(Uf.valueOf(add.getUf()));
				end.setIbge(add.getIbge());
				end.setLocalidade(add.getLocalidade());
				tempEnd.add(end);
			}
			
			
			return tempEnd;
//		}catch (Exception e) {
			// TODO: handle exception
//			return null;
//		}
	}
	
	/**
	 * Localiza o cep na base local
	 * @param cep
	 * @return Endereco caso nao encontrado retorna null
	*/		
	public Endereco procuraCepBase(String cep){
		final Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.and(
				Restrictions.eq("cep", cep )
				));

		criteria.add(Restrictions.eq("isDeleted", false));
		criteria.setMaxResults(1);

		System.out.println("teste de lisCEP em Endereco REpositorio");
		// montamos o resultado paginado
		return (Endereco) criteria.uniqueResult();
	}
	
}
