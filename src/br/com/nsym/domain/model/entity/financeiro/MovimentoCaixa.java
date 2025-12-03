package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.financeiro.tools.MotivoMovimentoCaixa;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="MovimentoCaixa",uniqueConstraints = {@UniqueConstraint(columnNames = {"id","id_empresa","id_filial"})})
public class MovimentoCaixa extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Caixa_ID")
	private Caixa caixa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="Motivo_ID")
	private MotivoMovimentoCaixa motivo;
	
	@Getter
	@Setter
	@OneToOne(mappedBy = "movimento",cascade = CascadeType.ALL)
	private RecebimentoParcial recebimento;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="MeioPag_ID")
	private FormaDePagamento pagamento;
	
	@Getter
	@Setter
	@Column(precision = 19 , scale = 5)
	private BigDecimal valor;

	@Getter
	@Setter
	private LocalDate data = LocalDate.now();

	@Getter
	@Setter
	private LocalTime hora = LocalTime.now();
	
	@Getter
	@Setter
	private String descMovimento;
	

}
