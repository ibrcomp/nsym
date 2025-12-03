package br.com.nsym.domain.model.entity.financeiro;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.tools.ParcelasNfe;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ChequeInf  extends PersistentEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = -6467948609230070412L;
	
	@Getter
	@Setter
	private String numCheque;
	
	@Getter
	@Setter
	private String numBanco;
	
	@Getter
	@Setter
	private String numAgencia;
	
	@Getter
	@Setter
	@OneToOne(mappedBy="cheque",cascade=CascadeType.ALL)
	private ParcelasNfe titulo;
	

}
