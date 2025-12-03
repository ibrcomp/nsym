package br.com.nsym.domain.model.repository.financeiro;

import javax.enterprise.context.Dependent;

import br.com.nsym.domain.model.entity.financeiro.tools.WsBanco;
import br.com.nsym.domain.model.repository.GenericRepositoryEmpDS;

@Dependent
public class WsBancoRepository extends GenericRepositoryEmpDS<WsBanco, Long>{

	/**
	 *
	 */
	private static final long serialVersionUID = -4979641919631632096L;

}
