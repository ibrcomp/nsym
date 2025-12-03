package br.com.nsym.domain.model.entity.financeiro.tools;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.financeiro.AgTitulo;
import br.com.nsym.domain.model.entity.financeiro.CartaoInf;
import br.com.nsym.domain.model.entity.financeiro.ChequeInf;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="parcelasNfe")
public class ParcelasNfe extends Parcelas{

	/**
	 *
	 */
	private static final long serialVersionUID = -5470156695716399859L;
	
	@Getter
	@Setter
	@OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name="id_Cheque",referencedColumnName = "id")
	private ChequeInf cheque;
	
	@Getter
	@Setter
	@OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name="id_Cartao",referencedColumnName = "id")
	private CartaoInf cartao;

	@Getter
	@Setter
	private String obs;
	
	@Getter
	@Setter
	@ManyToMany(mappedBy = "listaTitulosAgrupados"  ,fetch = FetchType.LAZY)
	private List<AgTitulo> listaAgTitulo = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean dpEspecial = false;
	
}
