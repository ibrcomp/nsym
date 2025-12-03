package br.com.nsym.domain.model.entity.fabrica;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.cadastro.Fornecedor;
import br.com.nsym.domain.model.entity.fabrica.util.EtapaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.LinhaProducao;
import br.com.nsym.domain.model.entity.fabrica.util.StatusAndamento;
import br.com.nsym.domain.model.entity.fiscal.tools.StatusNfe;
import br.com.nsym.domain.model.entity.tools.FiscalStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
public class OrdemServico extends PersistentEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 348923677377863368L;
	
	@Transient
	private MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="OP_ID")
	private Producao op;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Etapa_ID")
	private EtapaProducao etapa;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private StatusAndamento andamento;

	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name="Sequencia_ID")
	private LinhaProducao sequenciaProducao ;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal ValorCobrado = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Prestador_ID")
	private Fornecedor prestador;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Servico_ID")
	private Servico servico;
	
	@Getter
	@Setter
	private LocalDate previsaoRetorno;
	
	@Getter
	@Setter
	private LocalDate dataRetorno;
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantidadeEnviada = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Column(precision = 20 , scale = 5)
	private BigDecimal quantidadeRetorno = new BigDecimal("0",mc);
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private FiscalStatus fiscalStatus;
	
	@Getter
	@Setter
	private boolean financeiroCriado = false;


}
