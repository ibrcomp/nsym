package br.com.nsym.domain.model.entity.financeiro;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.nsym.domain.model.entity.PersistentEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="formaPagamento",uniqueConstraints = {@UniqueConstraint(columnNames={"codigo","id_empresa","id_filial"})})
public class FormaDePagamento  extends PersistentEntity{

	
	/**
	 *
	 */
	private static final long serialVersionUID = -4818912220214982150L;

	@Getter
	@Setter
	private int codigo;
	
	@Getter
	@Setter
	private String descricao;
	
	@Getter
	@Setter
	private String descOutros;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TipoPagamento tipoPagamento;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private OperadoraCartao operadoraCartao = OperadoraCartao.CET;
	
	@Getter
	@Setter
	private int parcelas;
	
	@Getter
	@Setter
	private int carencia;

	@Getter
	@Setter
	private int intervalo;
	
	@Getter
	@Setter
	private boolean entrada = false;
	
	@Getter
	@Setter
	private BigDecimal valorEntrada = new BigDecimal("0");
	
	@Getter
	@Setter
	private boolean integraFinanceiro = false;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ContaCorrente_id")
	private ContaCorrente contaCorrente;

	
}
