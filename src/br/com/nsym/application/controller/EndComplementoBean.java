package br.com.nsym.application.controller;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.nsym.domain.model.entity.cadastro.EndComplemento;
import br.com.nsym.domain.model.repository.cadastro.EndComplementoRepository;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class EndComplementoBean extends AbstractBeanEmpDS<EndComplemento> {

	@Getter
	@Setter
	private EndComplemento endComplemento;
	
	@Inject
	private EndComplementoRepository endComplementoDao;
	
	
	@Override
	public EndComplemento setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EndComplemento setIdFilial(Long idFilial) {
		// TODO Auto-generated method stub
		return null;
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
