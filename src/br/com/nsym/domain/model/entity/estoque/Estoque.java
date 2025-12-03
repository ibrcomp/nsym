package br.com.nsym.domain.model.entity.estoque;

import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import br.com.nsym.domain.model.entity.fiscal.NcmEstoque;
import lombok.Getter;
import lombok.Setter;

public class Estoque {
	
	@Getter
	@Setter
	private NcmEstoque ncmEstoque;
	
	@Getter
	@Setter
	private BarrasEstoque barrasEstoque;

}
