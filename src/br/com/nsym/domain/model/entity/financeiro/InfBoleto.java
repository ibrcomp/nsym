package br.com.nsym.domain.model.entity.financeiro;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.Enum.CarteiraEnvio;
import br.com.nsym.domain.model.entity.financeiro.Enum.CodigoMora;
import br.com.nsym.domain.model.entity.financeiro.Enum.CodigoNegativacao;
import br.com.nsym.domain.model.entity.financeiro.Enum.Protesto;
import br.com.nsym.domain.model.entity.financeiro.Enum.Sacado;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoDesconto;
import br.com.nsym.domain.model.entity.financeiro.Enum.TipoImpressao;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="infboleto")
public class InfBoleto extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -158135705330622110L;
	
	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "conta_id")
	private ContaCorrente conta;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Sacado sacado;
	
	@Getter
	@Setter
	private boolean aceite = true;
	
	@Getter
	@Setter
	private int tipoOcorrencia;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private Protesto protesto;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoImpressao tipoImpressao;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoDesconto tipoDesconto;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CodigoMora codigoMora;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CarteiraEnvio carteiraEnvio;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private CodigoNegativacao codigoNegativacao;
	
	

}
