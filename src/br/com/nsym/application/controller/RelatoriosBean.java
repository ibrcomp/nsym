package br.com.nsym.application.controller;

import java.time.LocalDate;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.nsym.domain.model.entity.cadastro.Produto;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class RelatoriosBean extends AbstractBeanEmpDS<Produto>{
	
	@Override
	public Produto setIdEmpresa(Long idEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Produto setIdFilial(Long idFilial) {
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
	
	
	@Getter
	@Setter
	private LocalDate dataInicial = LocalDate.now();
	
	@Getter
	@Setter
	private LocalDate dataFinal = LocalDate.now();
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1992456028591421722L;

	public void initializeRelCadastro() {
		this.viewState = ViewState.LISTING;
		this.dataInicial = LocalDate.now();
		this.dataFinal = LocalDate.now();
	}



}
