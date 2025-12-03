package br.com.nsym.domain.model.entity.financeiro.produto;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.cadastro.BarrasEstoque;
import lombok.Getter;
import lombok.Setter;

@Entity
public class BarrasCusto extends Custo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	@MapsId("id")
	@OneToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "PK_Barra")
	private BarrasEstoque barra;

}
