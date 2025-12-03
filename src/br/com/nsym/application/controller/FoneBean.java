package br.com.nsym.application.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import br.com.nsym.domain.model.entity.cadastro.Empresa;
import br.com.nsym.domain.model.entity.cadastro.Fone;
import br.com.nsym.domain.model.entity.tools.Operadora;
import br.com.nsym.domain.model.repository.cadastro.EmpresaRepository;
import br.com.nsym.domain.model.repository.cadastro.FoneRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FoneBean extends AbstractBeanEmpDS<Fone> {

	@Getter
	@Setter
	private Fone fone = new Fone();

	@Inject
	private EmpresaRepository empresaDAO;

	@Inject
	private FoneRepository foneDao;

	@Getter
	@Setter
	private List<Fone> listaFone;




	@Override
	public Fone setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fone setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
	}


	public Operadora[] getOperadoraType(){
		return Operadora.values();
	}

	public void toSalva(){
		try{
			if (fone.getCliente().getId() != null | fone.getContato().getId() != null | fone.getEmpresa().getId() != null){
				fone.setEmpresa(empresaDAO.findById(pegaEmpresaNaSessao().getId(), false));
				fone.setDeleted(false);
				foneDao.save(fone);
			}
		}catch(IllegalArgumentException e){
			System.out.println("gravação cancelada " + e);
		}
	}

	public Empresa pegaEmpresaNaSessao(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		return (Empresa) session.getAttribute("EMPRESA");
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
