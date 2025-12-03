package br.com.nsym.application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.primefaces.event.SelectEvent;

import br.com.nsym.domain.misc.AddressFinder;
import br.com.nsym.domain.misc.AddressFinder.Address;
import br.com.nsym.domain.model.entity.cadastro.Endereco;
import br.com.nsym.domain.model.entity.tools.Uf;
import br.com.nsym.domain.model.entity.tools.UfSigla;
import br.com.nsym.domain.model.repository.cadastro.EnderecoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class EnderecoBean extends AbstractBeanEmpDS<Endereco> {


	@Getter
	@Setter
	private Endereco endereco;

	@Inject
	private EnderecoRepository enderecoDAO;

	@Getter
	@Setter
	private String cep;

	@Getter
	@Setter
	private String logradouro;

	@Getter
	@Setter
	private String municipio;

	@Getter
	@Setter
	private String estado;

	@Getter
	@Setter
	private UfSigla uf;

	@Getter
	@Setter
	private List<Endereco> listaEncontrada = new ArrayList<Endereco>();

	@Getter
	@Setter
	private Boolean consultaPorCepVisivel = true;

	public void telaPesquisaEndereco(){
		this.updateAndOpenDialog("pesquisaEnderecoDialog", "dialogEndereco");
	}

	public void procuraEnd(){
		limpaForm();
		this.openDialog("dialogEndereco");
	}

	public void enderecoLocalizado(SelectEvent event){
		endereco = (Endereco) event.getObject();
	}

	@Transactional
	public void procuraCep(){

		try {
			if (cep.isEmpty() != true){
				System.out.println("estou indo para o DAO fazer a pesquisa de CEP no nosso Banco!");
				this.endereco = enderecoDAO.listCep(cep);
				System.out.println("a pesquisa retornou "+ endereco.getLogra());
				insereEnderecoNaSessao(this.endereco);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public List<Endereco> procuraCepWeb(){
		try{
			endereco = new Endereco();
			AddressFinder address = new AddressFinder();
			List<Address> listTemp = new ArrayList<Address>();
			listTemp= address.findAddressByLogradouro(estado, municipio, logradouro);
			for (Address add : listTemp) {
				Endereco end = new Endereco();
				end.setBairro(add.getBairro());
				end.setLogra(add.getLogradouro());
				end.setCep(add.getCep());
				end.setUf(Uf.valueOf(add.getUf()));
				end.setIbge(add.getIbge());
				end.setLocalidade(add.getLocalidade());
				listaEncontrada.add(end);
			}
			return listaEncontrada;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@Override
	public Endereco setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Endereco setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}

	public void fecha(){
		this.closeDialog("dialogEndereco");
	}
	/**
	 * @return a lista de tipos validos para Enquadramento
	 */
	public UfSigla[] getUfSiglaType() {
		return UfSigla.values();
	}

	public void limpaForm(){
		this.endereco = new Endereco();
		this.cep = "";
		this.logradouro = "";
		this.municipio = "";
		this.estado = "";
	}
	@Transactional
	public void onRowSelect(SelectEvent event)throws IOException{
		this.endereco = ((Endereco) event.getObject());
		cep = this.endereco.getCep();
		enderecoDAO.save(endereco);
		insereEnderecoNaSessao(this.endereco);
	}
	public void insereEnderecoNaSessao(Endereco endereco){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.setAttribute("ENDERECO", endereco);

	}

	@Override
	public void initializeListing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeForm(Long id) {
		// TODO Auto-generated method stub
		
	}
	
}